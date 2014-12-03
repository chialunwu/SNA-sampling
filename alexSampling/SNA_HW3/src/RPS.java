import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;


public class RPS {
	
	public static String graphFileName = "RPSGraph.txt";
	public static String queryOutputDirName = "/RPS";
	public static String outputAnsDirName = "./RPSOutputAns";
	public static int queryNum = 110;
	
	public static int attributeIndex = -1;
	
	private static final int[] histogram = new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,40,50,60,70,80,90,100,200};
	
	private static final int[] dimension = new int[]{2, 2, 10, 10, 140};
	
	public static void main(String[] args)
	{
		if (args.length != 5)
		{
			System.out.println("Wrong number of args");
			System.out.println("Usage: java -cp SNA_HW3.jar RPS <graph outputfile name> <query outputDirectory name> <query times> <output ans dir> <attri index>");
			System.out.println("Using default args: java -cp SNA_HW3.jar RPS "+graphFileName+" "+queryOutputDirName+" "+queryNum+" "+outputAnsDirName+" "+attributeIndex);
		}
		else
		{
			graphFileName = args[0];
			queryOutputDirName = args[1];
			queryNum = Integer.parseInt(args[2]);
			outputAnsDirName = args[3];
			attributeIndex = Integer.parseInt(args[4]);
		}
		
		File outputFile = new File(graphFileName);
		
		if (!outputFile.exists())
		{
			try {
				initQueryOutputFile();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			}
			
			
			RPSGraphManipulation gm = new RPSGraphManipulation();
			String seedsStr = Query.getSeeds();
			ArrayList<Node> sampleNode = gm.uniformSampleFromSeeds(seedsStr, 1);
			
			RPStatistic theRPStatistic = null;
			if (attributeIndex == -1)
				theRPStatistic = new RPStatistic(-1, histogram);			//attribute -1: degree, range: histogram
			else
				theRPStatistic = new RPStatistic(attributeIndex, dimension[attributeIndex]);			//attributes
			RPSNode addedNode = (RPSNode)sampleNode.get(0);
			HashSet<RPSNode> neighbors = new HashSet<RPSNode>();
			ArrayList<Edge> neighborsEdge = new ArrayList<Edge>();
			theRPStatistic.update(addedNode, gm);
			
			neighborsEdge = gm.getNeighborReturnParse(addedNode.nodeID, Query.getNeighbor(addedNode.nodeID));
			addedNode.neighborsEdge = neighborsEdge;
			addedNode.neighbors = new HashSet<RPSNode>();
			for (Edge e:neighborsEdge)
			{
				neighbors.add((RPSNode)e.node2);
				addedNode.neighbors.add((RPSNode)e.node2);
			}
			
			for (int i = 1; i < queryNum; i++)
			{
				if (i % 10 == 0)
					System.out.println(i);
				
				addedNode = biggestDeltaRPNeighbor(theRPStatistic, gm, neighbors);
				gm.addVertex(addedNode);
				
				neighborsEdge = gm.getNeighborReturnParse(addedNode.nodeID, Query.getNeighbor(addedNode.nodeID));
				addedNode.neighborsEdge = neighborsEdge;
				addedNode.neighbors = new HashSet<RPSNode>();
				for (Edge e:neighborsEdge)
				{
					if (!gm.containsVertex(e.node2))
						neighbors.add((RPSNode)e.node2);
					addedNode.neighbors.add((RPSNode)e.node2);
				}
				neighbors.remove(addedNode);
				
				gm.addAllInducedEdge(addedNode);
				theRPStatistic.update(addedNode, gm);
			}
			
			try {
				gm.outputAns(outputAnsDirName);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			gm.outputGraph(graphFileName);
		}
		else
		{
			GraphManipulation gm = new GraphManipulation();
			gm.readGraph(graphFileName);
			gm.visualizeGraph();
			try {
				gm.outputAns(outputAnsDirName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void initQueryOutputFile() throws Exception
	{
		File queryOutputDir = new File("./Query"+queryOutputDirName);
		Query.queryOutputNeighborFileName = "./Query"+queryOutputDirName+"/neighbor";
		Query.queryOutputSeedFileName = "./Query"+queryOutputDirName+"/seed";
		Query.queryNeighborTimes = 0;
		Query.querySeedTimes = 0;
		if (queryOutputDir.exists())
		{
			System.out.println("Query output directory existed!");
			throw new Exception();
		}
		else
		{
			queryOutputDir.mkdir();
		}
	}
	
	public static RPSNode biggestDeltaRPNeighbor(RPStatistic theRPStatistic, RPSGraphManipulation gm, HashSet<RPSNode> neighbors)
	{
		double maxExpectedDeltaRP = 0;
		RPSNode nodeWithMaxEDRP = null;
		double expectedDeltaRP = 0;
		boolean firstIter = true;
		for (RPSNode n:neighbors)
		{
			expectedDeltaRP = theRPStatistic.deltaRP(n, gm);
			if (firstIter)
			{
				nodeWithMaxEDRP = n;
				maxExpectedDeltaRP = expectedDeltaRP;
				firstIter = false;
			}
			else if (maxExpectedDeltaRP < expectedDeltaRP)
			{
				nodeWithMaxEDRP = n;
				maxExpectedDeltaRP = expectedDeltaRP;
			}
		}
		
		System.out.println(maxExpectedDeltaRP);
		return nodeWithMaxEDRP;
	}
	
	//inner classes definition
	static class RPSNode extends Node
	{

		public ArrayList<Edge> neighborsEdge;
		public HashSet<RPSNode> neighbors;
		
		public RPSNode(String nodeStr) {
			super(nodeStr);
			// TODO Auto-generated constructor stub
		}
		
		public int getType(int typeAttributeIndex)
		{
			if (typeAttributeIndex == -1)
				return degree;
			else
				return attributes[typeAttributeIndex];
		}
		
	}

	static class RPSGraphManipulation extends GraphManipulation
	{
		public void addAllInducedEdge(RPSNode node)
		{
			for (Edge e:node.neighborsEdge)
				if (g.containsVertex(e.node2))
					this.addEdge(e);
		}
		
		/*public ArrayList<Node> uniformSampleFromSeeds(String getSeedsReturnStr, int sampleNum)
		{
			String[] token;
			String[] lines = getSeedsReturnStr.split("\n");
			token = lines[2].split(" ");
			nodeAttriNum = Integer.parseInt(token[0]);
			edgeAttriNum = Integer.parseInt(token[1]);
			
			int seedNum = Integer.parseInt(lines[3]);
			RPSNode node;
			int[] sampleIndex = uniformRandomArray(4, seedNum, sampleNum);
			ArrayList<Node> sampleNode = new ArrayList<Node>(sampleNum);
			
			for (int i = 0; i < sampleNum; i++)
			{
				token = lines[sampleIndex[i]].split(" ");
				node = (RPSNode) getVertex(lines[sampleIndex[i]], nodes);
				addVertex(node, g, nodes);
				sampleNode.add(node);
			}
			return sampleNode;
		}*/
		
		protected Node getVertex(String nodeStr, HashMap<Integer, Node> nodes)
		{
			int nodeID = Integer.parseInt(nodeStr.split(" ")[0]);
			
			if (nodes.containsKey(nodeID))
				return nodes.get(nodeID);
			else
			{
				return new RPSNode(nodeStr);
			}
		}
	}

	static class RPStatistic
	{
		int [][]RPCount;
		int typeDegree[];			//RP(i,j) = RPCount(i,j) / typeDegree(i)
		int typeAttributeIndex;
		int[] histogram;
		
		public RPStatistic(int typeAttributeIndex, int[] histogram)
		{
			this.histogram = histogram;
			RPCount = new int[histogram.length+1][];
			for (int i = 0; i < histogram.length+1; i++)
				RPCount[i] = new int[histogram.length+1];
			typeDegree = new int [histogram.length+1];
			this.typeAttributeIndex =  typeAttributeIndex;
		}
		
		public RPStatistic(int typeAttributeIndex, int dimension)
		{
			this.histogram = null;
			RPCount = new int[dimension][];
			for (int i = 0; i < dimension; i++)
				RPCount[i] = new int[dimension];
			typeDegree = new int [dimension];
			this.typeAttributeIndex =  typeAttributeIndex;
		}
		
		public int getTypeIndex(RPSNode addedNode)
		{
			int type = addedNode.getType(typeAttributeIndex);
			if (histogram == null)
			{
				return type;
			}
			if (type < histogram[0])
				return 0;
			for (int i = 1; i < histogram.length; i++)
			{
				if (type < histogram[i])
					return i;
			}
			return histogram.length;
		}
		public double deltaRP(RPSNode addedNode, RPSGraphManipulation gm)
		{
			
			int deltaMatrix[][] = new int[typeDegree.length][];
			for (int i = 0; i < typeDegree.length; i++)
				deltaMatrix[i] = new int[typeDegree.length];
			int deltaTypeDegree[] = new int[typeDegree.length];
			
			for (Node n:gm.getVertices())
			{
				if (((RPSNode)n).neighbors.contains(addedNode))
				{
					deltaMatrix[getTypeIndex(addedNode)][getTypeIndex((RPSNode)n)]++;
					deltaMatrix[getTypeIndex((RPSNode)n)][getTypeIndex(addedNode)]++;
					deltaTypeDegree[getTypeIndex(addedNode)]++;
					deltaTypeDegree[getTypeIndex((RPSNode)n)]++;
				}
			}
			
			double delta = 0;
			for (int i = 0; i < typeDegree.length; i++)
				for (int j = 0; j < typeDegree.length; j++)
				{
					if (deltaTypeDegree[i]+typeDegree[i] != 0)
						delta += ((double)deltaMatrix[i][j] / (deltaTypeDegree[i]+typeDegree[i])) * ((double)deltaMatrix[i][j] / (deltaTypeDegree[i]+typeDegree[i]));
				}
			delta /=  typeDegree.length*typeDegree.length;
			delta = Math.sqrt(delta);
			
			return delta;
		}
		
		public void update(RPSNode addedNode, GraphManipulation gm)
		{
			Collection<Node> neighbors = gm.getNeighbor(addedNode);
			typeDegree[getTypeIndex(addedNode)] += neighbors.size();
			for (Node n:neighbors)
			{
				RPCount[getTypeIndex(addedNode)][getTypeIndex((RPSNode)n)]++;
				RPCount[getTypeIndex((RPSNode)n)][getTypeIndex(addedNode)]++;
				typeDegree[getTypeIndex((RPSNode)n)]++;
			}
		}
		
	}
}


