//written by Evan Owre April 15 2021
import java.io.*;

import java.net.*;

import java.util.*;

public class ConfServer implements Runnable
{
	// Each instance has a separate socket
	private Socket clientSock;

	// The class keeps track of active clients
	private static List<PrintWriter> clientList;

	// Constructor sets the socket for the child thread to process
	public ConfServer(Socket sock)
	{
		clientSock = sock;
	}

	// Add the given client to the active clients list
	// Since all threads share this, we use "synchronized" to make it atomic
	public static synchronized boolean addClient(PrintWriter toClientWriter)
	{
		return(clientList.add(toClientWriter));
	}

	// Remove the given client from the active clients list
	// Since all threads share this, we use "synchronized" to make it atomic
	public static synchronized boolean removeClient(PrintWriter toClientWriter)
	{
		return(clientList.remove(toClientWriter));
	}

	// Relay the given message to all the active clients
	// Since all threads share this, we use "synchronized" to make it atomic
	public static synchronized void relayMessage(
			PrintWriter fromClientWriter, String mesg)
	{
		for (int i = 0; i < clientList.size(); ++i) {
			if (fromClientWriter == clientList.get(i))
				continue;
			clientList.get(i).println(mesg);
		}
		// relay message to each client (but not the sender)
	}

	// The child thread starts here
	public void run()
	{
		// Read from the client and relay to other clients
		try {
			// Prepare to read from socket
			BufferedReader fromSocketReader = new BufferedReader(
					new InputStreamReader(clientSock.getInputStream()));
			// Get the client name
			String clientName = fromSocketReader.readLine();
			// Prepare to write to socket with auto flush on
			PrintWriter toSocketWriter = new PrintWriter(clientSock.getOutputStream(), true);
			// Add this client to the active client list
			addClient(toSocketWriter);
			// Keep doing till client sends EOF
			while (true) {
				// Read a line from the client
				String Line = fromSocketReader.readLine();
				// concating the name and the message to send
				String printout = clientName + ": " + Line;
				//If we get null, it means client quit break out of loop
				if(Line == null)
					break;
				// Else, relay the line to all active clients
				relayMessage(toSocketWriter, printout);
			}

			// Done with the client, remove it from client list
			removeClient(toSocketWriter);
		}
		catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}
	}

	/*
	 * The conf server program starts from here.
	 * This main thread accepts new clients and spawns a thread for each client
	 * Each child thread does the stuff under the run() method
	 */
	public static void main(String args[])
	{
		// Server needs a port to listen on
		if (args.length != 1) {
			System.out.println("usage: java ConfServer <server port>");
			System.exit(1);
		}

		// Be prepared to catch socket related exceptions
		try {
			// Create a server socket with the given port
			ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
			// Keep accepting/serving new clients
			clientList = new ArrayList<PrintWriter>();
			while (true) {
				// Wait to accept another client
				Socket clientSocket = serverSocket.accept();
				// Spawn a thread to read/relay messages from this client
				Thread child = new Thread(new ConfServer(clientSocket));
				child.start();
			}
		}
		catch(Exception e) {
			System.out.println(e);
			System.exit(1);
		}
	}
}
