package filesync;
/* Author: Anela
Date: 2 May 2014
Name: Server.java
Description: Server listens for connections, accepts requests,
starts push/pull thread based on client init message.
*/
import java.net.*;
import java.io.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Server{

	/* unlike client, any instance of server process is tied to a connection.
	because client specifies blocksize and mode, server cannot initialise 
	anything until a connection is made. connection is in main method,
	so the initialization and conneciton are unfortunately coupled for now. */

	public static void main(String args[]){

		SynchronisedFile serverFile = null;
		if (!validateArgs(args)) printUsageQuit();

		try{
			ServerSocket listenSocket = new ServerSocket(9090); 
			// CHANGE THIS!

			System.out.println("Server listening for connection..."); 
			Socket clientSocket = listenSocket.accept();
			System.out.println("Received connection."); 
			DataInputStream is = null;
			DataOutputStream os = null;
			
			// get direction and blocksize from client. defaults are push, 1024
			try{
				is = new DataInputStream(clientSocket.getInputStream());
				os = new DataOutputStream(clientSocket.getOutputStream());					
			} catch (IOException e){
				System.out.println("Connection: "+e.getMessage());
			}
			String initString = is.readUTF();
			String mode = getMode(initString);
			int blocksize = getBlockSize(initString);
			System.out.println("Client in mode: " + mode); 
			System.out.println("Block size: " + blocksize);

			try {
				serverFile=new SynchronisedFile(args[1],blocksize);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}

			// proceed according to client-specifed instruction
			if(mode.equals("push")){
				PullThread serverPullThread = new PullThread(clientSocket,is,os,serverFile);
			} else if (mode.equals("pull")){
				PushThread serverPushThread = new PushThread(clientSocket,is,os,serverFile);
				InstructionFiller filler = new InstructionFiller(serverFile);
			}

		}
		catch(IOException e1){
			System.out.println("Listen socket: " + e1.getMessage());
		}
	}

	public static boolean validateArgs(String args[]){
		if ((args.length == 2) && args[0].equals("-file"))
			return true;
		else return false;
	}

	public static void printUsageQuit(){
		System.out.println("Usage: java -jar syncserver.jar -file filename");
		System.exit(-1);
	}

	private static String getMode(String jsonString){
		JSONObject obj = null;
		JSONParser parser = new JSONParser();
		try {
			obj = (JSONObject) parser.parse(jsonString);
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return (String)obj.get("mode");
	}

	private static int getBlockSize(String jsonString){
		JSONObject obj = null;
		JSONParser parser = new JSONParser();
		try {
			obj = (JSONObject) parser.parse(jsonString);
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return Integer.parseInt((String)obj.get("blocksize"));
	}
	
}