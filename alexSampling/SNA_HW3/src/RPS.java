import java.io.File;
import java.util.ArrayList;


public class RPS {
	
	public static String graphFileName = "SampleEdgeCountSampledGraph.txt";
	public static String queryOutputDirName = "./SampleEdgeCount";
	
	public static void main(String[] args)
	{
		File outputFile = new File(graphFileName);
		
		if (!outputFile.exists())
		{
			initQueryOutputFile();
			
			int queryNum = 100;
			double m = 0.9;
			GraphManipulation queryG = new GraphManipulation();
			GraphManipulation gm = new GraphManipulation();
			
			
			
			String seedsStr = Query.getSeeds();
			queryG.createSeedsSubgraph(seedsStr);
			ArrayList<Node> sampleNode = gm.uniformSampleFromSeeds(seedsStr, 1);
			Node currentNode = sampleNode.get(0);
			Node currentNodeInQueryG = queryG.getVertex(currentNode.nodeID);
			int queryCount = 1;
			
			
			
			
			if (queryG.getDegree(currentNodeInQueryG) < currentNodeInQueryG.degree*m)		//too few neighbor of this node in the queryG
			{
				//query
				
			}
			else
			{
				
			}
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
}

class RPStatistic
{
	int [][]RPCount;
	int typeCount[];
	int typeDegree[];
	
	public RPStatistic(int dimension)
	{
		RPCount = new int[dimension][];
		for (int i = 0; i < dimension; i++)
			RPCount[i] = new int[dimension];
		typeCount = new int [dimension];
		typeDegree = new int [dimension];
	}
	
	private double deltaRP(int addedNodeID, GraphManipulation queryG)
	{
		Node addedNode = queryG.getVertex(addedNodeID);
		
	}
	
	private 
}
