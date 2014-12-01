import java.io.File;
import java.util.ArrayList;

public class SampleEdgeCount {
	
	public static String graphFileName = "SampleEdgeCountSampledGraph.txt";
	public static String queryOutputDirName = "./SampleEdgeCount";
	
	public static void main(String[] args)
	{
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
			
			int queryNum = 100;
			GraphManipulation gm = new GraphManipulation();
			ArrayList<Node> sampleNode = gm.uniformSampleFromSeeds(Query.getSeeds(), 1);
			Node currentNode = sampleNode.get(0);
			ArrayList<Edge> neighbors = new ArrayList<Edge>();
			for (int i = 1; i < queryNum; i++)
			{
				if (i % 10 == 0)
					System.out.println(i);
				neighbors.addAll(gm.getNeighborReturnParse(currentNode.nodeID, Query.getNeighbor(currentNode.nodeID)));
				Edge biggest = biggestEdgeCountNeighbor(gm, currentNode, neighbors);
				
				if (gm.containsVertex(biggest.node2))
				{
					System.out.println("No new neighbor in this node");
					currentNode = biggest.node2;
				}
				else
				{
					gm.addVertex(biggest.node2);
					gm.addEdge(biggest);
					currentNode = biggest.node2;
				}
			}
			
			gm.outputGraph(graphFileName);
		}
		else
		{
			GraphManipulation gm = new GraphManipulation();
			gm.readGraph(graphFileName);
			gm.visualizeGraph();
			//gm.degreeDistri();
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
	
}
