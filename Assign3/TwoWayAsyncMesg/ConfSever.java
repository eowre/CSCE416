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
		// Iterate through the client list and
		// relay message to each client (but not the sender)
	}

	// The child thread starts here
	public void run()
	{
		// Read from the client and relay to other clients
		try {
			// Prepare to read from socket

			// Get the client name

			// Prepare to write to socket with auto flush on

			// Add this client to the active client list

			// Keep doing till client sends EOF
			while (true) {
				// Read a line from the client

				// If we get null, it means client quit, break out of loop

				// Else, relay the line to all active clients
			}

			// Done with the client, remove it from client list
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

			// Keep accepting/serving new clients
			while (true) {
				// Wait to accept another client

				// Spawn a thread to read/relay messages from this client
			}
		}
		catch(Exception e) {
			System.out.println(e);
			System.exit(1);
		}
	}
}
