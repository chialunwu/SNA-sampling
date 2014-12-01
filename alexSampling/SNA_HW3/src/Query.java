import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class Query {

	public static String queryOutputSeedFileName;
	public static String queryOutputNeighborFileName;
	public static int querySeedTimes;
	public static int queryNeighborTimes;
	
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
		   
		   PrintStream outputNeighbor = new PrintStream(new File(queryOutputNeighborFileName+(queryNeighborTimes++)+".txt"));
		   outputNeighbor.println(nodeID);
		   outputNeighbor.print(result);
		   outputNeighbor.close();
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
		   
		   PrintStream outputSeed = new PrintStream(new File(queryOutputSeedFileName+(querySeedTimes++)+".txt"));
		   outputSeed.print(result);
		   outputSeed.close();
		} catch (IOException e) {
		   e.printStackTrace();
		} catch (Exception e) {
		   e.printStackTrace();
		}
		
		return result;
	}
	
}