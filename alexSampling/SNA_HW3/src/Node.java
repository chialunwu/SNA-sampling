
public class Node {
	
	public int nodeID;
	public int degree;
	public int[] attributes;
	
	public Node(String nodeStr)
	{
		String[] token = nodeStr.split(" ");
		this.nodeID = Integer.parseInt(token[0]);
		this.degree = Integer.parseInt(token[1]);
		this.attributes = new int [token.length-2];
		for (int i = 2; i < token.length; i++)
		{
			this.attributes[i-2] = Integer.parseInt(token[i]);
		}
	}
	
	@Override
	public String toString()
	{
		String s = ""+nodeID+" "+degree;
		for (int a:attributes)
			s += " "+a;
		return s;
	}
	
	@Override
	public boolean equals(Object n)
	{
		return this.nodeID == ((Node)n).nodeID;
	}
	
	@Override
	public int hashCode()
	{
		return nodeID;
	}
	
}
