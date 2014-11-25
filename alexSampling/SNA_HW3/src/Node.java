
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
		for (int i = 2; i < token.length-2; i++)
		{
			this.attributes[i-2] = Integer.parseInt(token[i]);
		}
	}
}
