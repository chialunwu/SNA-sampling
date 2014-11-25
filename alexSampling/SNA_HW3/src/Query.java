import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;


public class Query {

	public static String getNeighbor(int nodeID)
	{
		String urlToRead = "http://140.112.31.186/SNA2014/hw3/query.php?team=4MdY9pZz6b&node="+nodeID;
		URL url;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		String result = "";
		
		try {
		   url = new URL(urlToRead);
		   conn = (HttpURLConnection) url.openConnection();
		   conn.setRequestMethod("GET");
		   rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		   while ((line = rd.readLine()) != null)
		   {
		      result += line+"\n";
		   }
		   rd.close();
		} catch (IOException e) {
		   e.printStackTrace();
		} catch (Exception e) {
		   e.printStackTrace();
		}
		
		return result;
	}
	
	public static String getSeeds()
	{
		String urlToRead = "http://140.112.31.186/SNA2014/hw3/query.php?team=4MdY9pZz6b";
		URL url;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		String result = "";
		
		try {
		   url = new URL(urlToRead);
		   conn = (HttpURLConnection) url.openConnection();
		   conn.setRequestMethod("GET");
		   rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		   while ((line = rd.readLine()) != null)
		   {
		      result += line+"\n";
		   }
		   rd.close();
		} catch (IOException e) {
		   e.printStackTrace();
		} catch (Exception e) {
		   e.printStackTrace();
		}
		
		return result;
	}
	
}

class seedsReturn
{
	public ArrayList<Node> nodes;
	public ArrayList<Edge> edges;
}