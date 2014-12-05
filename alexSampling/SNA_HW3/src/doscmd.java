import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;



public class doscmd 
{ 
    public static void cmdCall(String command) 
    { 
        try 
        {
            Process p=Runtime.getRuntime().exec("cmd /c "+command); 
            p.waitFor(); 
            BufferedReader reader=new BufferedReader(
                new InputStreamReader(p.getInputStream())
            ); 
            String line; 
            while((line = reader.readLine()) != null) 
            { 
                System.out.println(line);
            } 
            reader=new BufferedReader(
                new InputStreamReader(p.getErrorStream())
            ); 
            while((line = reader.readLine()) != null) 
            { 
                System.out.println(line);
            } 

        }
        catch(IOException e1) {e1.printStackTrace();} 
        catch(InterruptedException e2) {e2.printStackTrace();} 
    } 
}