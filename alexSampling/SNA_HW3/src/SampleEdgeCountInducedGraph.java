import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SampleEdgeCountInducedGraph {
	
	public static String graphFileName = "SampleEdgeCountInducedGraph.txt";
	public static String queryOutputDirName = "/SampleEdgeCountInducedGraph";
	public static String outputAnsDirName = "./SampleEdgeCountInducedGraphOutputAns";
	public static int queryNum = 10;
	
	public static void main(String[] args)
	{
		if (args.length != 4)
		{
			System.out.println("Wrong number of args");
			System.out.println("Usage: java -cp SNA_HW3.jar SampleEdgeCountInducedGraph <graph outputfile name> <query outputDirectory name> <query times> <output ans dir>");
			System.out.println("Using default args: java -cp SNA_HW3.jar SampleEdgeCountInducedGraph "+graphFileName+" "+queryOutputDirName+" "+queryNum+" "+outputAnsDirName);
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
			
			SECGraphManipulation gm = new SECGraphManipulation();
			//ArrayList<Node> sampleNode = gWalker.uniformSampleFromSeeds(Query.getSeeds(), 1);
			ArrayList<Node> sampleNode = gm.selectBiggestDegreeNodeFromSeeds(Query.getSeeds());
			SECNode currentNode = (SECNode)sampleNode.get(0);
			ArrayList<Edge> neighbors = new ArrayList<Edge>();
			ArrayList<Edge> neighborsEdge = new ArrayList<Edge>();
			for (int i = 1; i < queryNum; i++)
			{
				if (i % 10 == 0)
					System.out.println(i);
				neighborsEdge = gm.getNeighborReturnParse(currentNode.nodeID, Query.getNeighbor(currentNode.nodeID));
				currentNode.neighborsEdge = neighborsEdge;
				currentNode.neighbors = new HashSet<SECNode>();
				for (Edge e:neighborsEdge)
				{
					if (!gm.containsVertex(e.node2))
					{
						neighbors.add(e);
						gm.addVertex(e.node2);
					}
					gm.addEdge(e);
					currentNode.neighbors.add((SECNode)e.node2);
					if (((SECNode)e.node2).neighborsEdge == null)
					{
						if (((SECNode)e.node2).neighbors == null)
							((SECNode)e.node2).neighbors = new HashSet<SECNode>();
						((SECNode)e.node2).neighbors.add((SECNode)e.node1);
					}
				}
				
				Edge biggest = biggestEdgeCountNeighbor(gm, currentNode, neighbors);
				
				currentNode = (SECNode)biggest.node2;
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
			if (((SECNode)n.node2).neighborsEdge != null)		//already query
				removeList.add(n);
			else if (n.node2.degree > biggest.node2.degree)		//not traversed yet, and bigger
				biggest = n;
		}
		neighbors.removeAll(removeList);
		return biggest;
	}
	
	public static GraphManipulation inducedGraphFromQueryOutputFile(int queryNum, ArrayList<Node> addedOrder)
	{
		GraphManipulation inducedG = new GraphManipulation();
		try {
			BufferedReader input = new BufferedReader(new FileReader(Query.queryOutputSeedFileName+"0.txt"));
			String line, result = "";
			line = input.readLine();
			while (line != null)
			{
				result += line + "\n";
				line = input.readLine();
			}
			input.close();
			inducedG.createSeedsSubgraph(result);
			
			ArrayList<Edge> neighbors = new ArrayList<Edge>();
			for (int i = 0; i < queryNum-1; i++)
			{
				input = new BufferedReader(new FileReader(Query.queryOutputNeighborFileName+i+".txt"));
				result = "";
				input.readLine();
				line = input.readLine();
				while (line != null)
				{
					result += line + "\n";
					line = input.readLine();
				}
				input.close();
				neighbors.clear();
				neighbors.addAll(inducedG.getNeighborReturnParse(addedOrder.get(i).nodeID, result));
				
				for (Edge e:neighbors)
				{
					inducedG.addVertex(e.node1);
					inducedG.addVertex(e.node2);
					inducedG.addEdge(e);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return inducedG;
	}
	
	//inner classes definition
	static class SECNode extends Node
	{

		public ArrayList<Edge> neighborsEdge;
		public HashSet<SECNode> neighbors;
		
		public SECNode(String nodeStr) {
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
	
	static class SECGraphManipulation extends GraphManipulation
	{
		public void addAllInducedEdge(SECNode node)
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
				return new SECNode(nodeStr);
			}
		}
	}
	
}
