
public class Edge {
	
	public int edgeID;
	public Node node1;
	public Node node2;
	public int[] attributes;
	//private static int edgeCount = 0;
	
	public Edge(String edgeStr, Node node1, Node node2)
	{
		this.edgeID = -1;
		this.node1 = node1;
		this.node2 = node2;
		
		String[] token = edgeStr.split(" ");
		if (token.length <= 2)
			attributes = null;
		else
		{
			attributes = new int [token.length-2];
			for (int i = 2; i < token.length+2; i++)
				attributes[i-2] = Integer.parseInt(token[i]);
		}
	}
	
	@Override
	public String toString()
	{
		String s = ""+node1.nodeID+" "+node2.nodeID;
		if (attributes != null)
		{
			for (int a:attributes)
				s += " " + a;
		}
		return s;
	}
	
	public String attributesStr()
	{
		if (attributes != null)
		{
			String s = null;
			for (int a:attributes)
				s += " " + a;
			return s;
		}
		return "";
	}
	
}
