import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.JFrame;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.scoring.DistanceCentralityScorer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;


public class GraphManipulation {
	
	protected Graph<Node, Edge> g;
	protected HashMap<Integer, Node> nodes;
	protected int nodeAttriNum;
	protected int edgeAttriNum;
	private int edgeCount;
	
	/*public static void main(String[] args)
	{
		GraphManipulation gm = new GraphManipulation();
		int[] array = gm.uniformRandomArray(0, 10, 10);
		for (int i = 0; i < array.length; i++)
			System.out.println(array[i]);
	}*/
	
	public GraphManipulation()
	{
		g = new UndirectedSparseMultigraph<Node, Edge>();
		nodes = new HashMap<Integer, Node>();
		edgeCount = 0;
	}
	
	public void createSeedsSubgraph(String getSeedsReturnStr)
	{
		String[] token;
		String[] lines = getSeedsReturnStr.split("\n");
		token = lines[2].split(" ");
		nodeAttriNum = Integer.parseInt(token[0]);
		edgeAttriNum = Integer.parseInt(token[1]);
		
		int seedNum = Integer.parseInt(lines[3]);
		Node node;
		
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
			addEdge(new Edge(lines[i], node1, node2));
		}
	}
	
	public ArrayList<Node> uniformSampleFromSeeds(String getSeedsReturnStr, int sampleNum)
	{
		String[] token;
		String[] lines = getSeedsReturnStr.split("\n");
		token = lines[2].split(" ");
		nodeAttriNum = Integer.parseInt(token[0]);
		edgeAttriNum = Integer.parseInt(token[1]);
		
		int seedNum = Integer.parseInt(lines[3]);
		Node node;
		int[] sampleIndex = uniformRandomArray(4, seedNum, sampleNum);
		ArrayList<Node> sampleNode = new ArrayList<Node>(sampleNum);
		
		for (int i = 0; i < sampleNum; i++)
		{
			token = lines[sampleIndex[i]].split(" ");
			node = getVertex(lines[sampleIndex[i]], nodes);
			addVertex(node, g, nodes);
			sampleNode.add(node);
		}
		return sampleNode;
	}
	
	public ArrayList<Edge> getNeighborReturnParse(int nodeID, String getNeighborReturnStr)
	{
		String[] token;
		String[] lines = getNeighborReturnStr.split("\n");
		String nodeStr, edgeStr;
		Node node = getVertex(nodeID, nodes), neighbor;
		Edge edge;
		ArrayList<Edge> neighbors = new ArrayList<Edge>(lines.length-2);
		for (int i = 3; i < lines.length; i++)
		{
			token = lines[i].split(" ");
			nodeStr = token[0];
			for (int j = 1; j < nodeAttriNum+2; j++)
				nodeStr += " "+token[j];
			edgeStr = ""+nodeID+" "+token[0];
			for (int j = nodeAttriNum+2; j < nodeAttriNum+2+edgeAttriNum; j++)
				edgeStr += " "+token[j];
			neighbor = getVertex(nodeStr, nodes);
			edge = getEdge(edgeStr, node, neighbor);
			neighbors.add(edge);
		}
		return neighbors;
	}
	
	public void outputGraph(String outputFileName)
	{
		PrintStream output = null;
		File outputFile = null;
		try {
			outputFile = new File(outputFileName);
			if (outputFile.exists())
			{
				System.out.println("Already exists");
				return;
			}
			output = new PrintStream(outputFile);
			output.println(""+g.getVertexCount()+" "+g.getEdgeCount());
			output.println(""+nodeAttriNum+" "+edgeAttriNum);
			
			for (Node n:g.getVertices())
			{
				output.println(n);
			}
			
			for (Edge e:g.getEdges())
			{
				output.println(e);
			}
			
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void outputAns(String outputAnsDirName) throws Exception
	{
		File outputAnsDir = new File(outputAnsDirName);
		if (outputAnsDir.exists())
		{
			System.out.println("output ans directory existed!");
			throw new Exception();
		}
		else
		{
			outputAnsDir.mkdir();
			//output degree
			PrintStream outputDegree = new PrintStream(new File(outputAnsDirName+"/degree.txt"));
			int[] degree = new int [10000];
			int maxDegree = 0;
			for (Node n:g.getVertices())
			{
				if (maxDegree < g.getNeighborCount(n))
					maxDegree = g.getNeighborCount(n);
				degree[g.getNeighborCount(n)]++;
			}
			
			for (int i = 1; i < maxDegree+1; i++)
			{
				if (degree[i] != 0)
					outputDegree.println(i+"\t"+((double)degree[i]/g.getVertexCount()));
			}
			outputDegree.close();
			
			//output node_attr_k
			for (int k = 1; k < this.nodeAttriNum+1; k++)
			{
				PrintStream outputNodeAttr = new PrintStream(new File(outputAnsDirName+"/node_attr_"+k+".txt"));
				int[] numAttrValue = new int[100000];
				for(Node n:g.getVertices())
					numAttrValue[n.attributes[k-1]]++;
				for (int i = 0; i < numAttrValue.length; i++)
				{
					if (numAttrValue[i] != 0)
						outputNodeAttr.println(i+"\t"+((double)numAttrValue[i]/g.getVertexCount()));
				}
				outputNodeAttr.close();
			}
			
			//output closeness
			PrintStream outputCloseness = new PrintStream(new File(outputAnsDirName+"/closeness.txt"));
			DistanceCentralityScorer<Node, Edge> closenessScorer = new DistanceCentralityScorer<Node, Edge> (g, true);
			int[] nodeID = new int[g.getVertexCount()];
			double[] score = new double[g.getVertexCount()];
			int[] sortedIndex = new int[g.getVertexCount()];
			int topN = 100;
			int count = 0;
			try
			{
				for (Node n:g.getVertices())
				{
					nodeID[count] = n.nodeID;
					score[count] = closenessScorer.getVertexScore(n);
					sortedIndex[count] = count;
					count++;
				}
				getSortedIndex(sortedIndex, score, 0, score.length-1);
				for (int i = 0; i < topN; i++)
					outputCloseness.println(nodeID[sortedIndex[nodeID.length-1-i]]);
			}
			catch (java.lang.OutOfMemoryError e)
			{
				e.printStackTrace();
				e.printStackTrace(outputCloseness);
			}
			outputCloseness.close();
		}
		
	}
	
	private static int partition(int index[], double arr[], int left, int right)
	{
	      int i = left, j = right;
	      int tmp;
	      double pivot = arr[index[(left + right) / 2]];
	     
	      while (i <= j) {
	            while (arr[index[i]] < pivot)
	                  i++;
	            while (arr[index[j]] > pivot)
	                  j--;
	            if (i <= j) {
	                  tmp = index[i];
	                  index[i] = index[j];
	                  index[j] = tmp;
	                  i++;
	                  j--;
	            }
	      };
	     
	      return i;
	}
	 
	private static void getSortedIndex(int index[], double arr[], int left, int right) {
	      int indexI = partition(index, arr, left, right);
	      if (left < indexI - 1)
	    	  getSortedIndex(index, arr, left, indexI - 1);
	      if (indexI < right)
	    	  getSortedIndex(index, arr, indexI, right);
	}
	
	public void readGraph(String inputFileName)
	{
		BufferedReader input = null;
		String line;
		String[] token;
		int nodeNum, edgeNum;
		Node node;
		try 
		{
			input = new BufferedReader(new FileReader(inputFileName));
			line = input.readLine();
			token = line.split(" ");
			nodeNum = Integer.parseInt(token[0]);
			edgeNum = Integer.parseInt(token[1]);
			line = input.readLine();
			token = line.split(" ");
			nodeAttriNum = Integer.parseInt(token[0]);
			edgeAttriNum = Integer.parseInt(token[1]);
			
			for (int i = 0; i < nodeNum; i++)
			{
				line = input.readLine();
				node = getVertex(line, nodes);
				addVertex(node, g, nodes);
			}
			
			Node node1, node2;
			for (int i = 0; i < edgeNum; i++)
			{
				line = input.readLine();
				token = line.split(" ");
				node1 = getVertex(Integer.parseInt(token[0]), nodes);
				node2 = getVertex(Integer.parseInt(token[1]), nodes);
				addEdge(new Edge(line, node1, node2));
			}
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean containsVertex(Node n)
	{
		return nodes.containsKey(n.nodeID);
	}
	
	public void addVertex(Node n)
	{
		if (!containsVertex(n))
			addVertex(n, g, nodes);
	}
	
	public void addEdge(Edge e)
	{
		if (g.findEdge(e.node1, e.node2) == null)
		{
			e.edgeID = edgeCount++;
			g.addEdge(e, e.node1, e.node2);
		}
	}
	
	public boolean containsEdge(Edge e)
	{
		return g.findEdge(e.node1, e.node2) != null;
	}
	
	public Node getVertex(int nodeID)
	{
		return nodes.get(nodeID);
	}
	
	public int getDegree(Node n)
	{
		return g.getNeighborCount(n);
	}
	
	public Collection<Node> getNeighbor(Node n)
	{
		return g.getNeighbors(n);
	}
	
	public Collection<Node> getVertices()
	{
		return g.getVertices();
	}
	
	public void visualizeGraph()
	{
		// The Layout<V, E> is parameterized by the vertex and edge types
        //Layout<Node, Edge> layout = new FRLayout<Node,Edge>(g);
		//Layout<Node, Edge> layout = new FRLayout2<Node,Edge>(g);
		Layout<Node, Edge> layout = new ISOMLayout<Node,Edge>(g);
		//Layout<Node, Edge> layout = new KKLayout<Node,Edge>(g);
        layout.setSize(new Dimension(1400,700)); // sets the initial size of the layout space
        // The BasicVisualizationServer<V,E> is parameterized by the vertex and edge types
        BasicVisualizationServer<Node,Edge> vv = new BasicVisualizationServer<Node,Edge>(layout);
        vv.setPreferredSize(new Dimension(1450,750)); //Sets the viewing area size
        
        JFrame frame = new JFrame("Simple Graph View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv); 
        frame.pack();
        frame.setVisible(true);
	}
	
	public void degreeDistri()
	{
		int[] degree = new int [10000];
		int maxDegree = 0;
		for (Node n:g.getVertices())
		{
			if (maxDegree < g.getNeighborCount(n))
				maxDegree = g.getNeighborCount(n);
			degree[g.getNeighborCount(n)]++;
		}
		
		for (int i = 0; i < maxDegree+1; i++)
		{
			System.out.println(degree[i]);
		}
	}
	
	protected int[] uniformRandomArray(int start, int arrayLength, int randomNum)
	{
		int[] array = new int [arrayLength];
		for (int i = 0; i < arrayLength; i++)
			array[i] = start+i;
		int select;
		int temp;
		for (int i = 0; i < randomNum; i++)
		{
			select = (int)(Math.random()*(arrayLength-i));
			temp = array[i+select];
			array[i+select] = array[i];
			array[i] = temp;
		}
		return array;
	}
	
	protected Node getVertex(String nodeStr, HashMap<Integer, Node> nodes)
	{
		int nodeID = Integer.parseInt(nodeStr.split(" ")[0]);
		
		if (nodes.containsKey(nodeID))
			return nodes.get(nodeID);
		else
		{
			return new Node(nodeStr);
		}
	}
	
	protected Node getVertex(int nodeID, HashMap<Integer, Node> nodes)
	{
		if (nodes.containsKey(nodeID))
			return nodes.get(nodeID);
		else
		{
			return null;
		}
	}
	
	protected void addVertex(Node n, Graph<Node, Edge> g, HashMap<Integer, Node> nodes)
	{
		if (!nodes.containsKey(n.nodeID))
		{
			g.addVertex(n);
			if (nodes.containsKey(n.nodeID))
				System.out.println("Duplicate nodeID");
			nodes.put(n.nodeID, n);
		}
	}
	
	protected Edge getEdge(String edgeStr, Node node1, Node node2)
	{
		Edge edge = g.findEdge(node1, node2);
		if (edge == null)
			edge = new Edge(edgeStr, node1, node2);
		return edge;
	}
	
}
