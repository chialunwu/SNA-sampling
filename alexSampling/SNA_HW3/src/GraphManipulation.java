import java.util.ArrayList;
import java.util.HashMap;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;


public class GraphManipulation {
	
	public static Graph<Node, Edge> g;
	public static HashMap<Integer, Node> nodes;
	public static int nodeAttriNum;
	public static int edgeAttriNum;
	
	public static void createSeedsSubgraph(String getSeedsReturnStr)
	{
		String[] token;
		String[] lines = getSeedsReturnStr.split("\n");
		token = lines[2].split(" ");
		nodeAttriNum = Integer.parseInt(token[0]);
		edgeAttriNum = Integer.parseInt(token[1]);
		
		int seedNum = Integer.parseInt(lines[3]);
		Node node;
		g = new UndirectedSparseMultigraph<Node, Edge>();
		for (int i = 4; i < seedNum+4; i++)
		{
			token = lines[i].split(" ");
			node = getVertex(lines[i], nodes);
			addVertex(node, g, nodes);
		}
		
		Node node1, node2;
		for (int i = seedNum+4; i < lines.length; i++)
		{
			node1 = getVertex(Integer.parseInt(lines[i].split(" ")[0]), nodes);
			node2 = getVertex(Integer.parseInt(lines[i].split(" ")[1]), nodes);
			g.addEdge(new Edge(lines[i]), node1, node2);
		}
	}
	
	public static ArrayList<Node> getNeighborReturnParse(int nodeID, String getNeighborReturnStr)
	{
		String[] token;
		String[] lines = getNeighborReturnStr.split("\n");
		for (int i = 2; i < lines.length; i++)
		{
			token = lines[i].split(" ");
			
		}
	}
	
	private static Node getVertex(String nodeStr, HashMap<Integer, Node> nodes)
	{
		int nodeID = Integer.parseInt(nodeStr.split(" ")[0]);
		
		if (nodes.containsKey(nodeID))
			return nodes.get(nodeID);
		else
		{
			return new Node(nodeStr);
		}
	}
	
	private static Node getVertex(int nodeID, HashMap<Integer, Node> nodes)
	{
		if (nodes.containsKey(nodeID))
			return nodes.get(nodeID);
		else
		{
			return null;
		}
	}
	
	private static void addVertex(Node n, Graph<Node, Edge> g, HashMap<Integer, Node> nodes)
	{
		if (!nodes.containsKey(n.nodeID))
		{
			g.addVertex(n);
			nodes.put(n.nodeID, n);
		}
	}
	
}
