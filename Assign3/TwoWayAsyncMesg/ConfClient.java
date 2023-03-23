//written by Evan Owre, April 15 2021
import java.io.*;

import java.net.*;

import java.util.*;

public class ConfClient implements Runnable {
	
	private BufferedReader fromUserReader;

	private PrintWriter toSocketWriter;

	public ConfClient(BufferedReader reader, PrintWriter writer) {
		fromUserReader = reader;
		toSocketWriter = writer;
	}
	
	public void run() {
		
		try{
			while (true) {
				//Read line from user
				String Line = fromUserReader.readLine();

				//If null break loop
				if (Line == null) 
					break;

				//Write the line to the socket
				toSocketWriter.println(Line);
			}
		}
		catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}
	}

	public static void main(String args[]){
		//ensure the command line prompt has a host port and clients name
		if (args.length != 3) {
			System.out.println("usage: java ConfClient <host> <port> <name>");
			System.exit(1);
		}
		//creating the socket
		Socket sock = null;
		try {
			sock = new Socket(args[0], Integer.parseInt(args[1]));
			System.out.println( "Connected to server at " +args[0] + ":" + args[1]);
		}
		catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}
		//building the writer and reader
		try {
			PrintWriter toSocketWriter = new PrintWriter(sock.getOutputStream(), true);
			//this line is the first message the server gets from the client, 
			//this ensures the name is passed first and stored with the server
			toSocketWriter.println(args[2]);

			BufferedReader fromUserReader = new BufferedReader( 
					new InputStreamReader(System.in));

			Thread child = new Thread( new ConfClient(fromUserReader, toSocketWriter));

			child.start();
		}
		catch(Exception e) {
			System.out.println(e);
			System.exit(1);
		}
		//building the reader to take output from the socket and pushing it to the terminal
		try {
			BufferedReader fromSocketReader = new BufferedReader( 
					new InputStreamReader( sock.getInputStream()));
			while(true) {
				String Line = fromSocketReader.readLine();

				if (Line == null) {
					System.out.println("Server quit");
					break;
				}

				System.out.println(Line);
			}
		}
		catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}

		System.exit(0);
	}

}
