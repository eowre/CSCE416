//Jhada Kahan-Thomas
//I/O stuff
import java.io.*;
//socket classes
import java.net.*;
//list usage
import java.util.*;

public class chat_server implements Runnable 
{
	//each thread has an instance of a socket
	private Socket cSock;
	private static String chatU ="";
	private static String chatU2 = "";
	//dynamic list of clients: name, state & sockey
	private static HashMap<String,PrintWriter> clientList;
	private static HashMap<String, String> status; //{"name", "status"};
	//hashmap for connections containing name of both clients
	private static HashMap<String,String> connectTable;
	//counter for clients
	private static int maxClients = 100;
	
	//constructor
	public chat_server(Socket sock)
	{
		cSock = sock;
		//chatU = "";
		//chatU2 = "";
		
	}
	
	//atomic add a client to client list
	public static synchronized boolean addClient(String name, PrintWriter toClientWriter)
	{
		clientList.put(name, toClientWriter);
		return(clientList.containsKey(name));
	}
	//was trying to keep track of name and state as well
	public static synchronized boolean addClient(String name, String state)
	{
		status.put(name, state);
		return(status.containsKey(name));
	}

	//remove the client
	public static synchronized boolean removeClient(String name, PrintWriter toClientWriter)
	{
		status.remove(name);
		return(clientList.remove(name, toClientWriter));
	}
	
	
	//what this to rely message to a specific client not the whole client list
	public static synchronized void relayMessage(String mesg, String peer)
	{
		// searches for particular client by name
		for(Map.Entry<String, PrintWriter> temp : clientList.entrySet())
		{	
			//found client 
			if(temp.getKey() == peer)
				temp.getValue().println(mesg);
		
		}
           
	}
	//relay message of updated clients active to all clients
	public static synchronized void relayStatus(String msg)
	{
			// Iterate through the list and send message to each client
		for(Map.Entry<String, PrintWriter> temp : clientList.entrySet())
		{
			//current print to is the iteratored current printwriter 
			PrintWriter clientWriter = temp.getValue();
		    //print to each user
			clientWriter.println(msg);
		}
				
	}
	//sends message to desire chat client from other client
	public static synchronized void request(String name)
	{
		for(Map.Entry<String, PrintWriter> temp : clientList.entrySet()){
			if(temp.getKey().equalsIgnoreCase(name))
			{
				PrintWriter clientWriter = temp.getValue();
				clientWriter.println("You got a request from " + chatU2
						+ "\n Connect? y or n");
				//conn = fromClientReader.readLine();
			}
		}

	}
	public static void main(String args[])
	{
		// server port
		if (args.length != 1) {
			System.out.println("usage: java chat server <port>");
			System.exit(1);
		}

		// responsible for accepting new clients
		try {
			// Create a server socket with the given port
			ServerSocket serverSock = 
					new ServerSocket(Integer.parseInt(args[0]));
			System.out.println("Waiting for clients ...");
			
			// Keep track of active clients
			clientList = new HashMap<String,PrintWriter>();
			
			// Keep accepting/serving new clients
			while (true) {
				// Wait for another client
				Socket clientSock = serverSock.accept();
				//accept up to 100 connections
				for(int i = 0; i < maxClients; i++)
				{
				// Spawn a thread to read/relay messages from this client
				Thread child = new Thread(new chat_server(clientSock));
				child.start();
				//break;
				}
			}
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}
	
	//child thread
		public void run()
		{
			// Read from the client and relay to other clients
			//current client
			String clientName = null;
			try {
				// Prepare to read from socket
				BufferedReader fromClientReader = new BufferedReader(
						new InputStreamReader(cSock.getInputStream()));
				
				// Get the client name from user input
				clientName = fromClientReader.readLine();
				System.out.println("Client Accepted");
			}
			catch (Exception e) {
				System.out.println(e);
			}
			try {
				// SOCKET: Prepare to write to socket (client screen) with auto flush on
				BufferedReader fromClientReader = new BufferedReader(
						new InputStreamReader(cSock.getInputStream()));
				PrintWriter toClientWriter =
						new PrintWriter(cSock.getOutputStream(), true);
				
				String state = "free";
				//String[] info = new String[]{clientName,state};
				// Add this client to the active client list & list to keep track of name & state
				addClient(clientName,toClientWriter);
				//addClient(clientName, state);
				
				System.out.println("List of clients and states");
				String msg = "List of clients and states";
				for( Map.Entry<String, PrintWriter> temp : clientList.entrySet() )
				{
						System.out.println(temp.getKey() + "			"+ state);
						msg = temp.getKey() + "			"+ state;
						relayStatus(msg);
					
				}
				//relayStatus(msg);
				
				toClientWriter.println("Connect with which client?");
				String userChoice = fromClientReader.readLine();
				//WANT a user
				if(userChoice != null)
				{
					chatU.equals(userChoice);
					//chatU.equals(holdName(userChoice));
					chatU2.equals(clientName);
				}
				String sender = "";
				String conn = "n";
				
				//toClientWriter.println("Connect to which client?");
				//String userChoice = fromClientReader.readLine();
				//fiind socket
				/*for(Map.Entry<String, PrintWriter> temp : clientList.entrySet()){
					if(temp.getKey().equalsIgnoreCase(userChoice))
					{
						requestedSock= temp.getValue();
						
						//their name goes 
					}
				}*/
				//check for waiting requests
				//boolean request(re)
				//PrintWriter toClientWriter =
				//new PrintWriter(cSock.getOutputStream(), true);
				/*if(chatU == clientName)
				{
						toClientWriter.println("You got a request from " + clientName
								+ "\n Connect? y or n");
						conn = fromClientReader.readLine();
				}*/
				//send message to client desired
				request(chatU);
				//answering request method
				conn = fromClientReader.readLine();
				//start a connection
				if(conn.equals("y"))
				{
					//running
					while (true) 
					{
							
					state = "busy";
					//Read a line from the client
					String line = fromClientReader.readLine();
	
					//client left
					if (line == null)
							break;
	
						//Send the line to chat client
						relayMessage(clientName + ": " + line + "\n", chatU);
				}
				}
				
				
				/*request(toClientWriter,)
				//received a request
				toClientWriter.println("Received")	
				do you want to
				y or no 
				
				
				return of request
				relay to client 
				
				
				//request fullfilled 
				toClientWriter.println("You are connected to "
						relay to client
				
				//connecting to clients 
				//toClientWriter.println("Connect to which client?");
				//String userChoice = fromClientReader.readLine();
				for(Map.Entry<PrintWriter, String[]> temp : clientList.entrySet()){
					if(temp.getValue()[0].equalsIgnoreCase(userChoice))
					{
						PrintWriter peerSoc = new PrintWriter(temp.getKey());
						acceptCon(toClientWriter, fromClientReader, peerSoc);
						toClientWriter.println("You are connected to " + userChoice);//connectClients(clientName, userChoice);
				
				//till client sends EOF
					PrintWriter peerSoc = new PrintWriter(connect());
					while (true) {
						// Read a line from the client
						String line = fromClientReader.readLine();

						// If we get null, it means client sent EOF
						if (line == null)
							break;

						// Send the line to all active clients
						relayMessage(clientName + ": " + line + "\n", peerSoc);
					}
					//}
					else
					{
						toClientWriter.println("no client found");
					}*/
				

				// client no longer active
				removeClient(clientName,toClientWriter);
				
				// close everything
				toClientWriter.close();
				System.out.println(clientName + " left the conference");
				}
			
			catch (Exception e) {
				System.out.println(e);
			}
		}
	
}


/* I apologize I just really do not understand. I tried to take extra time hoping it would help but it
 * still really does not make sense to me. I kept running into the issue that if I requested a connection 
 * in the client class then it would not read the the clients before projecting. I do not understand although the
 * server takes care of multiple clients when working within one thread how it is supposed to make a 
 * request to another and the other receive it to continue and begin a chat. Then the server would have to wait one client
 * to receive from another but I thought their actions are managed independently so I do not understand how you are supposed 
 * to intertwine those holds. It seems like a three way handshake except between clients I do not get how to relay that. I left 
 * alot of code that I tried in just to show. I was not sure if maybe I was supposed to implement another socket for the
 * whole class but that seems redunant. I also got a little stuck with maintaining socket, name and state. Initially I used a nested 
 * Hashmap but this then caused issues because the key was the socket, so you would have to pull the name from a list embedded 
 * to verify.
