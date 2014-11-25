
public class Edge {
	
	public int edgeID;
	public int[] attributes;
	private static int edgeCount = 0;
	
	public Edge(String edgeStr)
	{
		edgeID = edgeCount++;
		
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
	
}
