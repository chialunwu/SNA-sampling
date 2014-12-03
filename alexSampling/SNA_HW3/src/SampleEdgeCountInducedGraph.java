import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class SampleEdgeCountInducedGraph {
	
	public static String graphFileName = "SampleEdgeCountInducedGraph.txt";
	public static String queryOutputDirName = "./SampleEdgeCountInducedGraph";
	public static String outputAnsDirName = "./SampleEdgeCountInducedGraphOutputAns";
	public static int queryNum = 10;
	
	public static void main(String[] args)
	{
		if (args.length != 4)
		{
			System.out.println("Wrong number of args");
			System.out.println("Usage: java -cp SNA_HW3.jar SampleEdgeCountInducedGraph <graph outputfile name> <query outputDirectory name> <query times> <output ans dir>");
			System.out.println("Using default args: java -cp SNA_HW3.jar SampleEdgeCountInducedGraph "+graphFileName+" "+queryOutputDirName+" "+queryNum);
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
			
			GraphManipulation gWalker = new GraphManipulation();
			ArrayList<Node> sampleNode = gWalker.uniformSampleFromSeeds(Query.getSeeds(), 1);
			Node currentNode = sampleNode.get(0);
			ArrayList<Edge> neighbors = new ArrayList<Edge>();
			ArrayList<Node> addedOrder = new ArrayList<Node>();
			addedOrder.add(currentNode);
			for (int i = 1; i < queryNum; i++)
			{
				if (i % 10 == 0)
					System.out.println(i);
				neighbors.addAll(gWalker.getNeighborReturnParse(currentNode.nodeID, Query.getNeighbor(currentNode.nodeID)));
				Edge biggest = biggestEdgeCountNeighbor(gWalker, currentNode, neighbors);
				
				if (gWalker.containsVertex(biggest.node2))
				{
					System.out.println("No new neighbor in this node");
					addedOrder.add(biggest.node2);
					currentNode = biggest.node2;
				}
				else
				{
					gWalker.addVertex(biggest.node2);
					gWalker.addEdge(biggest);
					addedOrder.add(biggest.node2);
					currentNode = biggest.node2;
				}
			}
			
			GraphManipulation inducedG = inducedGraphFromQueryOutputFile(queryNum, addedOrder);
			try {
				inducedG.outputAns(outputAnsDirName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			inducedG.outputGraph(graphFileName);
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
		File queryOutputDir = new File(queryOutputDirName);
		Query.queryOutputNeighborFileName = queryOutputDirName+"/neighbor";
		Query.queryOutputSeedFileName = queryOutputDirName+"/seed";
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
			if (gm.containsVertex(n.node2))
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
	
}
