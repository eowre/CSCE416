import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

public class HttpClient {

  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println("Syntax: <URL>");
      return;
    }
    /*this ^^ was originally args.length <2 to check that an output file was passed in but that
     * functionality was removed
     */

    String url = args[0];
    String filepath = "HttpClientOutput";
    //String filepath = args[1]; //took this line out, but can be added for more functionality 
    
    try {
      URL urlObj = new URL(url);
      HttpURLConnection http_conn = (HttpURLConnection) urlObj.openConnection();
      
      int responseCode = http_conn.getResponseCode();
      if (responseCode != HttpURLConnection.HTTP_OK) {
        if (responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
	    responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
	    responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
           String new_url = http_conn.getHeaderField("Location");
	   /*System.out.println("redirecting" + new_url + url); testing if redirect works properly
	    */
	   http_conn = (HttpURLConnection) new URL(new_url).openConnection(); //updating http connection
        }
      }

      InputStream in = http_conn.getInputStream();//inputstream

      http_conn = (HttpURLConnection) urlObj.openConnection();
      /*Keep getting the same IllegalStateException, reading the HttpURLConnection documentation I understand 
      that this is because each instance can only make a single request, but I do not know how to fix this
      */

      Map headers = http_conn.getHeaderFields();//creating map of headers
      PrintStream print_stream = new PrintStream(filepath);//using a print stream to write to a file
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));//intiating the reader
      
      int count = 0;//iterating through headers based using .size() of a map
      while (count <= headers.size()) {
        print_stream.print(headers.get(count));// printing headers to file
	count++;
        //print_stream.print(reader.readLine());
      }
      while (reader.readLine() != null) {
        print_stream.print(reader.readLine());
      }
      print_stream.close();//close reader and printing stream
      reader.close();
    } catch (MalformedURLException e) {
        System.out.println(e.getMessage());
    } catch (IOException e) {
        System.out.println(e.getMessage());
    }
  }
}
