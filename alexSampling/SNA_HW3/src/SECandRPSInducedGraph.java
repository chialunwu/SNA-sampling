import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SECandRPSInducedGraph {
	
	public static String graphFileName = "SECandRPSInducedGraph.txt";
	public static String queryOutputDirName = "/SECandRPSInducedGraph";
	public static String outputAnsDirName = "./SECandRPSInducedGraphOutputAns";
	public static int queryNumSEC = 6;
	public static int queryNumRPS = 200;
	
	public static int attributeIndex = -1;
	
	private static final int[] histogram = new int[]{2,3,4,7,11,16,22,29,37,46,56,71,101,201};
	
	private static final int[] dimension = new int[]{2, 2, 10, 10, 140};
	
	public static void main(String[] args)
	{
		if (args.length != 6)
		{
			System.out.println("Wrong number of args");
			System.out.println("Usage: java -cp SNA_HW3.jar SECandRPSInducedGraph <graph outputfile name> <query outputDirectory name> <SEC query times> <RPS query times> <output ans dir> <attri index>");
			System.out.println("Using default args: java -cp SNA_HW3.jar SECandRPSInducedGraph "+graphFileName+" "+queryOutputDirName+" "+queryNumSEC+" "+queryNumRPS+" "+outputAnsDirName+" "+attributeIndex);
		}
		else
		{
			graphFileName = args[0];
			queryOutputDirName = args[1];
			queryNumSEC = Integer.parseInt(args[2]);
			queryNumRPS = Integer.parseInt(args[3]);
			outputAnsDirName = args[4];
			attributeIndex = Integer.parseInt(args[5]);
		}
		
		File outputFile = new File(graphFileName);
		
		if (!outputFile.exists())
		{
			try {
				initQueryOutputFile();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			
			SECandRPSGraphManipulation gm = new SECandRPSGraphManipulation();
			//ArrayList<Node> sampleNode = gWalker.uniformSampleFromSeeds(Query.getSeeds(), 1);
			ArrayList<Node> sampleNode = gm.selectBiggestDegreeNodeFromSeeds(Query.getSeeds());
			
			RPStatistic theRPStatistic = null;
			if (attributeIndex == -1)
				theRPStatistic = new RPStatistic(-1, histogram);			//attribute -1: degree, range: histogram
			else
				theRPStatistic = new RPStatistic(attributeIndex, dimension[attributeIndex]);			//attributes
			SECandRPSNode currentNode = (SECandRPSNode)sampleNode.get(0);
			ArrayList<Edge> neighbors = new ArrayList<Edge>();
			HashSet<SECandRPSNode> neighborsNode = new HashSet<SECandRPSNode>();
			ArrayList<Edge> neighborsEdge = new ArrayList<Edge>();
			for (int i = 1; i < queryNumSEC; i++)
			{
				if (i % 10 == 0)
					System.out.println(i);
				neighborsEdge = gm.getNeighborReturnParse(currentNode.nodeID, Query.getNeighbor(currentNode.nodeID));
				currentNode.neighborsEdge = neighborsEdge;
				currentNode.neighbors = new HashSet<SECandRPSNode>();
				theRPStatistic.update(currentNode, gm);
				for (Edge e:neighborsEdge)
				{
					if (!gm.containsVertex(e.node2))
					{
						neighbors.add(e);
						neighborsNode.add((SECandRPSNode)e.node2);
						gm.addVertex(e.node2);
					}
					gm.addEdge(e);
					currentNode.neighbors.add((SECandRPSNode)e.node2);
					if (((SECandRPSNode)e.node2).neighborsEdge == null)
					{
						if (((SECandRPSNode)e.node2).neighbors == null)
							((SECandRPSNode)e.node2).neighbors = new HashSet<SECandRPSNode>();
						((SECandRPSNode)e.node2).neighbors.add((SECandRPSNode)e.node1);
					}
				}
				neighborsNode.remove(currentNode);
				
				Edge biggest = biggestEdgeCountNeighbor(gm, currentNode, neighbors);
				
				currentNode = (SECandRPSNode)biggest.node2;
			}
			
			for (int i = 1; i < queryNumRPS; i++)
			{
				if (i % 10 == 0)
					System.out.println(i);
				
				currentNode = biggestDeltaRPNeighbor(theRPStatistic, gm, neighborsNode);
				
				neighborsEdge = gm.getNeighborReturnParse(currentNode.nodeID, Query.getNeighbor(currentNode.nodeID));
				currentNode.neighborsEdge = neighborsEdge;
				currentNode.neighbors = new HashSet<SECandRPSNode>();
				theRPStatistic.update(currentNode, gm);
				for (Edge e:neighborsEdge)
				{
					if (!gm.containsVertex(e.node2))
					{
						neighborsNode.add((SECandRPSNode)e.node2);
						gm.addVertex(e.node2);
					}
					gm.addEdge(e);
					currentNode.neighbors.add((SECandRPSNode)e.node2);
					
				}
				neighborsNode.remove(currentNode);
			}
			
			try {
				gm.outputAns(outputAnsDirName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			gm.evaluateSample(outputAnsDirName);
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
			gm.evaluateSample(outputAnsDirName);
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
	
	public static Edge biggestEdgeCountNeighbor(GraphManipulation gm, Node currentNode, ArrayList<Edge> neighbors)
	{
		Edge biggest = neighbors.get(0);
		ArrayList<Edge> removeList = new ArrayList<Edge>();
		for (Edge n:neighbors)
		{
			if (((SECandRPSNode)n.node2).neighborsEdge != null)		//already query
				removeList.add(n);
			else if (n.node2.degree > biggest.node2.degree)		//not traversed yet, and bigger
				biggest = n;
		}
		neighbors.removeAll(removeList);
		return biggest;
	}
	
	public static SECandRPSNode biggestDeltaRPNeighbor(RPStatistic theRPStatistic, SECandRPSGraphManipulation gm, HashSet<SECandRPSNode> neighbors)
	{
		double maxExpectedDeltaRP = 0;
		SECandRPSNode nodeWithMaxEDRP = null;
		double expectedDeltaRP = 0;
		boolean firstIter = true;
		for (SECandRPSNode n:neighbors)
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
		
		//System.out.println(maxExpectedDeltaRP);
		return nodeWithMaxEDRP;
	}
	
	//inner classes definition
	static class SECandRPSNode extends Node
	{

		public ArrayList<Edge> neighborsEdge;
		public HashSet<SECandRPSNode> neighbors;
		
		public SECandRPSNode(String nodeStr) {
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
	
	static class SECandRPSGraphManipulation extends GraphManipulation
	{
		public void addAllInducedEdge(SECandRPSNode node)
		{
			for (Edge e:node.neighborsEdge)
				if (g.containsVertex(e.node2))
					this.addEdge(e);
		}
		
		protected Node getVertex(String nodeStr, HashMap<Integer, Node> nodes)
		{
			int nodeID = Integer.parseInt(nodeStr.split(" ")[0]);
			
			if (nodes.containsKey(nodeID))
				return nodes.get(nodeID);
			else
			{
				return new SECandRPSNode(nodeStr);
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
		
		public int getTypeIndex(SECandRPSNode addedNode)
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
		public double deltaRP(SECandRPSNode addedNode, SECandRPSGraphManipulation gm)
		{
			int deltaMatrix[][] = new int[typeDegree.length][];
			for (int i = 0; i < typeDegree.length; i++)
				deltaMatrix[i] = new int[typeDegree.length];
			
			for (Node n:gm.getNeighbor(addedNode))
			{
				deltaMatrix[getTypeIndex(addedNode)][getTypeIndex((SECandRPSNode)n)]++;
				deltaMatrix[getTypeIndex((SECandRPSNode)n)][getTypeIndex(addedNode)]++;
			}
			
			/*System.out.println("Delta:");
			for (int i = 0; i < RPCount.length; i++)
			{
				for (int j = 0; j < RPCount.length; j++)
					System.out.print(deltaMatrix[i][j]+"\t");
				System.out.println();
			}*/
			
			double delta = 0;
			for (int i = 0; i < typeDegree.length; i++)
				for (int j = 0; j < typeDegree.length; j++)
				{
					if (typeDegree[i] != 0)
						delta += ((double)deltaMatrix[i][j] / typeDegree[i]) * ((double)deltaMatrix[i][j] / typeDegree[i]);
				}
			delta /=  typeDegree.length*typeDegree.length*gm.getDegree(addedNode);//*gm.getDegree(addedNode);
			delta = Math.sqrt(delta);
			
			return delta;
		}
		
		public void update(SECandRPSNode addedNode, GraphManipulation gm)
		{
			for (Edge e:addedNode.neighborsEdge)
			{
				if (!gm.containsEdge(e))
				{
					RPCount[getTypeIndex(addedNode)][getTypeIndex((SECandRPSNode)e.node2)]++;
					RPCount[getTypeIndex((SECandRPSNode)e.node2)][getTypeIndex(addedNode)]++;
					typeDegree[getTypeIndex((SECandRPSNode)e.node2)]++;
					typeDegree[getTypeIndex(addedNode)]++;
				}
			}
			/*
			System.out.println("RPCount:");
			for (int i = 0; i < RPCount.length; i++)
			{
				for (int j = 0; j < RPCount.length; j++)
					System.out.print(RPCount[i][j]+"\t");
				System.out.println();
			}
			System.out.println("typeDrgree:");
			for (int i = 0; i < typeDegree.length; i++)
				System.out.println(typeDegree[i]);
				*/
		}
		
	}
	
}
