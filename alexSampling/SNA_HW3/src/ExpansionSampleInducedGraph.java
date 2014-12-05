import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class ExpansionSampleInducedGraph {
	
	public static String graphFileName = "ExpansionSampleInducedGraph.txt";
	public static String queryOutputDirName = "/ExpansionSampleInducedGraph";
	public static String outputAnsDirName = "./ExpansionSampleInducedGraphOutputAns";
	public static int queryNum = 110;
	
	public static void main(String[] args)
	{
		if (args.length != 4)
		{
			System.out.println("Wrong number of args");
			System.out.println("Usage: java -cp SNA_HW3.jar ExpansionSampleInducedGraph <graph outputfile name> <query outputDirectory name> <query times> <output ans dir>");
			System.out.println("Using default args: java -cp SNA_HW3.jar ExpansionSampleInducedGraph "+graphFileName+" "+queryOutputDirName+" "+queryNum+" "+outputAnsDirName);
		}
		else
		{
			graphFileName = args[0];
			queryOutputDirName = args[1];
			queryNum = Integer.parseInt(args[2]);
			outputAnsDirName = args[3];
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
			
			
			ExpansionGraphManipulation gm = new ExpansionGraphManipulation();
			//ArrayList<Node> sampleNode = gm.uniformSampleFromSeeds(Query.getSeeds(), 1);
			ArrayList<Node> sampleNode = gm.selectBiggestDegreeNodeFromSeeds(Query.getSeeds());
			ExpansionNode currentNode = (ExpansionNode)sampleNode.get(0);
			ArrayList<Edge> neighbors = new ArrayList<Edge>();
			ArrayList<Edge> neighborsEdge = new ArrayList<Edge>();
			for (int i = 1; i < queryNum; i++)
			{
				if (i % 10 == 0)
					System.out.println(i);
				neighborsEdge = gm.getNeighborReturnParse(currentNode.nodeID, Query.getNeighbor(currentNode.nodeID));
				currentNode.neighborsEdge = neighborsEdge;
				currentNode.neighbors = new HashSet<ExpansionNode>();
				for (Edge e:neighborsEdge)
				{
					if (!gm.containsVertex(e.node2))
					{
						neighbors.add(e);
						gm.addVertex(e.node2);
					}
					gm.addEdge(e);
					currentNode.neighbors.add((ExpansionNode)e.node2);
					if (((ExpansionNode)e.node2).neighborsEdge == null)
					{
						if (((ExpansionNode)e.node2).neighbors == null)
							((ExpansionNode)e.node2).neighbors = new HashSet<ExpansionNode>();
						((ExpansionNode)e.node2).neighbors.add((ExpansionNode)e.node1);
					}
				}
				
				Edge biggest = biggestExpansionNeighbor(gm, currentNode, neighbors);
				
				currentNode = (ExpansionNode)biggest.node2;
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
	
	public static Edge biggestExpansionNeighbor(GraphManipulation gm, Node currentNode, ArrayList<Edge> neighbors)
	{
		Edge biggest = neighbors.get(0);		//pick one as the biggest
		int alreadySeenNeighbors;
		int alreadySeenNeighborsOfBiggest = 0;
		ArrayList<Edge> removeList = new ArrayList<Edge>();
		
		/*for (Node node:gm.getVertices())
			if (((ExpansionNode)node).neighbors != null && ((ExpansionNode)node).neighbors.contains(biggest.node2));
				alreadySeenNeighborsOfBiggest++;*/
		alreadySeenNeighborsOfBiggest = ((ExpansionNode)biggest.node2).neighbors.size();
		
		for (Edge n:neighbors)
		{
			if (((ExpansionNode)n.node2).neighborsEdge != null)			//already query
				removeList.add(n);
			else
			{
				alreadySeenNeighbors = 0;
				/*for (Node node:gm.getVertices())
					if (((ExpansionNode)node).neighbors != null && ((ExpansionNode)node).neighbors.contains(n.node2))
						alreadySeenNeighbors++;*/
				alreadySeenNeighbors = ((ExpansionNode)n.node2).neighbors.size();
				if (n.node2.degree-alreadySeenNeighbors > biggest.node2.degree-alreadySeenNeighborsOfBiggest)		//not traversed yet, and bigger
				{
					biggest = n;
					alreadySeenNeighborsOfBiggest = alreadySeenNeighbors;
				}
			}
		}
		
		neighbors.removeAll(removeList);
		return biggest;
	}
	
	//inner classes definition
	static class ExpansionNode extends Node
	{

		public ArrayList<Edge> neighborsEdge;
		public HashSet<ExpansionNode> neighbors;
		
		public ExpansionNode(String nodeStr) {
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
	
	static class ExpansionGraphManipulation extends GraphManipulation
	{
		public void addAllInducedEdge(ExpansionNode node)
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
				return new ExpansionNode(nodeStr);
			}
		}
	}
	
}
