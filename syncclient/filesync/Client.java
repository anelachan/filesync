package filesync;
/* Author: Anela
Date: 2 May 2014
Name: Client.java
Description: Client makes a connection with the Server and
starts push/pull thread based on command-line arguments.
*/
import java.net.*;
import java.io.*;
import org.json.simple.JSONObject;

public class Client{

	// each instance vars will be specified on the commandline.
	private SynchronisedFile clientFile = null;
	private String hostName = null;
	private String mode = null;
	private int blocksize = 0;

	Client(String args[]){
		if(!validateArgs(args)) printUsageQuit();

		String hostName = args[3];
		try{
			if(args[4].equals("-direction"))
				mode = args[5];
			else if(args[4].equals("-blocksize")){ // if no mode given but blocksize given
				mode = "push";
			}		
		} catch (ArrayIndexOutOfBoundsException e){
				mode = "push"; // if no direction specified, will push
		}

		try{
			if(args[4].equals("-blocksize"))
				blocksize = Integer.parseInt(args[5]);
			else if(args[6].equals("-blocksize"))
				blocksize = Integer.parseInt(args[7]);
		} catch (ArrayIndexOutOfBoundsException e1){
				blocksize = 1024; // if no blocksize specified, use 1024
		}

		// will use 2-arg constructor, so first need blocksize.
		try{
			clientFile = new SynchronisedFile(args[1],blocksize); 
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}	
	}

	public static void main(String args[]){

		/* INITIALIZING */
		Client myClient = new Client(args);
		String mode = myClient.getMode(); 
		int blocksize = myClient.getBlockSize();
		SynchronisedFile clientFile = myClient.getFile();

		/* CONNECT TO SERVER, RUN THREAD(S) */
		try{
			// connect to server
			Socket s = new Socket(myClient.getHost(), 9090);
			DataInputStream is = null;
			DataOutputStream os = null;
			try{
				is = new DataInputStream(s.getInputStream());
				os = new DataOutputStream(s.getOutputStream());				
			} catch(IOException e1){
				System.out.println("Connection: "+e1.getMessage());
			}
			System.out.println("Connection established."); 
			System.out.println("Working in direction: " + mode); 
			System.out.println("Blocksize: " + Integer.toString(blocksize));

			// send initializing info
			String initString = initJSON(mode,blocksize);
			os.writeUTF(initString);			

			// run thread or threads
			if(mode.equals("push")){ 
				PushThread clientPushThread = new PushThread(s,is,os,clientFile);
				InstructionFiller filler = new InstructionFiller(clientFile);
			}
			else if(mode.equals("pull")){
				PullThread clientPullThread = new PullThread(s,is,os,clientFile);
			} else{
				assert(false);
			}
		} catch(UnknownHostException e){
			System.out.println("Socket: " + e.getMessage());
		} catch(IOException e2){
			System.out.println("Readline: " + e2.getMessage());
		}
	}

	private static boolean validateArgs(String args[]){
		if(args.length == 4){
			if(args[0].equals("-file") && args[2].equals("-host"))
				return true;
		}
		else if(args.length == 6){
			if(args[0].equals("-file") && args[2].equals("-host") 
				&& args[4].equals("-direction"))
				return true;
			else if(args[0].equals("-file") && args[2].equals("-host") 
				&& args[4].equals("-blocksize"))
				return true;
		} else if (args.length == 8){
			if(args[0].equals("-file") && args[2].equals("-host") 
				&& args[4].equals("-direction") && args[6].equals("-blocksize"))
				return true; // mode must always be specified first
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private static String initJSON(String mode,int blocksize){
		JSONObject obj = new JSONObject();
		obj.put("mode",mode); 
		obj.put("blocksize",Integer.toString(blocksize));
		return obj.toJSONString();
	}

	private static void printUsageQuit(){
		System.out.println("Usage: java -jar syncclient.jar -file filename -host hostname [-direction push/pull] [-blocksize blocksize]");
		System.exit(-1);
	}

	public String getHost(){
		return hostName;
	}

	public String getMode(){
		return mode;
	}

	public int getBlockSize(){
		return blocksize;
	}

	public SynchronisedFile getFile(){
		return clientFile;
	}

}