package filesync;

/* Author: Anela
Date: 2 May 2014
Name: PushThread.java
Description: This thread is primarily concerned w/ passing 
Instruction msgs marshalled as JSON objects to the destination.
It also must process acknwoledgements sent by PullThread.
*/

import java.net.*;
import java.io.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class PushThread extends Thread{
	DataInputStream is;
	DataOutputStream os;
	Socket ourSocket;
	SynchronisedFile fromFile;

	PushThread(Socket aSocket,DataInputStream in, DataOutputStream out, SynchronisedFile ff){
		fromFile = ff;
		ourSocket = aSocket;
		is = in;
		os = out;
		this.start(); 
	}
	public static void processMsg(String jsonString) throws BlockUnavailableException{
		JSONObject obj = null;
		JSONParser parser = new JSONParser();
		try {
			obj = (JSONObject) parser.parse(jsonString);
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		if(obj.get("ack").equals("Need new block!")){
			throw new BlockUnavailableException();
		} 
	}

	public void run(){
		Instruction inst;
		try{
			while((inst=fromFile.NextInstruction())!=null){
				try{
					// send instruction to Destination
					String instString = inst.ToJSON();
					os.writeUTF(instString);
					System.out.println("Sent: " + instString);
					
					// get and process ack from Destination
					String ack = is.readUTF();
					System.out.println("Received: "+ ack);
					processMsg(ack); // may throw BlockUnavailableException!
				} catch (BlockUnavailableException e){
					Instruction upgraded = new NewBlockInstruction((CopyBlockInstruction)inst); 
					String instStringNew = upgraded.ToJSON(); 
					os.writeUTF(instStringNew); // resend the CopyBlock upgraded to New
					System.out.println("Sent: " + instStringNew);
					String ackNew = is.readUTF();
					System.out.println("Received: " + ackNew);
					try{
						processMsg(ackNew);
					} catch (BlockUnavailableException e1){
						assert(false); // can never throw this should only be NewBlocks
					}
					
				} 
			}
		} catch(EOFException e){
			System.out.println("EOF: " + e.getMessage());
		} catch(IOException e2){
			System.out.println("Readline: " + e2.getMessage());
		}
		finally{
			try{
				ourSocket.close();
				System.out.println("Closed connection.");
			} catch(IOException e3) { 
				System.out.println("Close: " + e3.getMessage());
			} // close failed
		}
	}
}