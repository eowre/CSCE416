//Jhada kahan-Thomas
// Package for socket related stuff
import java.net.*;
import java.util.*;
// Package for I/O related stuff
import java.io.*;
public class chat_client implements Runnable 
{
		// reading messages keyboard
		private BufferedReader fromUserReader;
		private static Socket sock;
		// For writing messages to the socket
		private PrintWriter toSockWriter;
		private static ArrayList<String> names = new ArrayList<String>();
		// reader and writer for the child thread
		public chat_client(BufferedReader reader, PrintWriter writer)
		{
			fromUserReader = reader;
			toSockWriter = writer;
		}

		// The child thread starts here
		public void run()
		{
			// Read from the keyboard and write to socket
			try {
				System.out.println("Enter client name:");
				String name = fromUserReader.readLine();
				// give server username
				toSockWriter.println(name);
				names.add(name);
				System.out.println("Welcome to the 416 chat server");
				
				// Keep doing till user types EOF (Ctrl-D)
				while (true) {
					// Read a line from the user
					String line = fromUserReader.readLine();

					// If we get null, it means EOF, close socket
					if (line == null) {
						toSockWriter.close();
						break;
					}

					// Write the line to the socket
					toSockWriter.println(line);
				}
			}
			catch (Exception e) {
				System.out.println(e);
			}
		}
	
		 
		//sets up streams for reading & writing from keyboard and socket
		 
		public static void main(String args[])
		{	
			//Client needs server's contact information: host and port
			if (args.length != 2) {
				System.out.println("usage: java ConfClient <host> <port>");
				System.exit(1);
			}
			
			//Connect to the server at the given host and port
			sock = null;
			try {			
				sock = new Socket(args[0], Integer.parseInt(args[1]));
				
						
			}
			catch(Exception e) {
				System.out.println(e);
			}

			// Set up a thread to read from user and send to server
			try {
				// Prepare to write to socket with auto flush on
				PrintWriter toSockWriter =
						new PrintWriter(sock.getOutputStream(), true);
				

				// Prepare to read from keyboard
				BufferedReader fromUserReader = new BufferedReader(
						new InputStreamReader(System.in));

				//create thread start child
				Thread child = new Thread(
						new chat_client(fromUserReader, toSockWriter));
				child.start();
			}
			catch(Exception e) {
				System.out.println(e);
			}
			try {
				//read from socket
				BufferedReader fromSockReader = new BufferedReader(
						new InputStreamReader(sock.getInputStream()));

				
				//until server exits
				while (true) {
					// Read a line from the socket
					String line = fromSockReader.readLine();

					//EOF on socket
					if (line == null)
						break;

					// Write to the user
					System.out.println(line);
				}
			
			}
			catch(SocketException e) {
				
			}
			catch(Exception e) {
				System.out.println(e);
			}
			//stops the thread
			System.exit(0);
		}
}
