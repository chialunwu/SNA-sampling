import java.io.File;
import java.util.ArrayList;

public class SampleEdgeCount {
	
	public static void main(String[] args)
	{
		File outputFile = new File("SampleEdgeCountSampledGraph.txt");
		
		if (!outputFile.exists())
		{
			int sampleNum = 1000;
			GraphManipulation gm = new GraphManipulation();
			ArrayList<Node> sampleNode = gm.uniformSampleFromSeeds(Query.getSeeds(), 1);
			Node currentNode = sampleNode.get(0);
			ArrayList<Edge> neighbors = new ArrayList<Edge>();
			for (int i = 1; i < sampleNum; i++)
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
			
			gm.outputGraph("SampleEdgeCountSampledGraph.txt");
		}
		else
		{
			GraphManipulation gm = new GraphManipulation();
			gm.readGraph("SampleEdgeCountSampledGraph.txt");
			gm.visualizeGraph();
			//gm.degreeDistri();
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
