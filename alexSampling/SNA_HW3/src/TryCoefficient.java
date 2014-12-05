
public class TryCoefficient {
	
	public static String graphFileName = "./TryCoefficient/SECandRPSInducedGraph_";
	public static String queryOutputDirName = "/TryCoefficient/SECandRPSInducedGraph_";
	public static String outputAnsDirName = "./TryCoefficient/SECandRPSInducedGraphOutputAns_";
	public static int queryNumSEC = 6;
	public static int queryNumRPS = 200;
	
	public static void main(String[] args)
	{
		for (int queryNumSEC = 3; queryNumSEC <= 15; queryNumSEC++)
		{
				String arg[] = new String[]{graphFileName+queryNumSEC+"_"+queryNumRPS, 
						queryOutputDirName+queryNumSEC+"_"+queryNumRPS, 
						""+queryNumSEC, ""+queryNumRPS, 
						outputAnsDirName+queryNumSEC+"_"+queryNumRPS, "-1"};
				
				SECandRPSInducedGraph.main(arg);
		}
	}
}
