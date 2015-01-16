package filesync;

/* Author: Anela
Date: 2 May 2014
Name: PullThread.java
Description: This thread is primarily concerned w/ unmarshalling  
JSON objects to instructions and processing those Instructions.
Will send negative acknowledgements to the pusher in the case
of a BlockUnavailableException, and positive otherwise. 
*/

import java.net.*;
import java.io.*;
import org.json.simple.JSONObject;

public class PullThread extends Thread{
	// PullThread runs over some TCP connection
	DataInputStream is;
	DataOutputStream os;
	Socket ourSocket;
	SynchronisedFile toFile;

	PullThread(Socket aSocket, DataInputStream in, DataOutputStream out, SynchronisedFile tf){
		toFile = tf;
		ourSocket = aSocket;
		is = in;
		os = out;
		this.start();
	}

	@SuppressWarnings("unchecked")
	private static String posAck(){
		JSONObject obj = new JSONObject();
		obj.put("ack","Received and processed"); 
		return obj.toJSONString();
	}
	@SuppressWarnings("unchecked")
	private static String negAck(){
		JSONObject obj = new JSONObject();
		obj.put("ack","Need new block!"); 
		return obj.toJSONString();
	}
	
	public void run(){
		InstructionFactory instFact = new InstructionFactory();
		try{
			while(true){ 

				String instString=is.readUTF();
				System.out.println("Received: " + instString);
				Instruction receivedInst = instFact.FromJSON(instString);
				try{
					toFile.ProcessInstruction(receivedInst);
					String ack = posAck();
					os.writeUTF(ack);
					System.out.println("Sent: " + ack);
				} catch(BlockUnavailableException e){
					String ack2 = negAck();
					os.writeUTF(ack2); 
					System.out.println("Sent: " + ack2);
					String instStringNew = is.readUTF(); // read incoming NewBlockInstruction
					System.out.println("Received: " + instStringNew);
					try{
						Instruction receivedInstNew = instFact.FromJSON(instStringNew); // must be NewBlock
						toFile.ProcessInstruction(receivedInstNew);
						String ack3 = posAck();
						os.writeUTF(ack3);
						System.out.println("Sent: " + ack3);			
					} catch (IOException e1) {
						e1.printStackTrace();
						System.exit(-1);
					} catch (BlockUnavailableException e1) {
						assert(false); // a NewBlockInstruction can never throw this exception
					}
				} 
			}
		} catch(EOFException e){
			System.out.println("EOF: " + e.getMessage());
		} catch(IOException e){
			System.out.println("Readline: " + e.getMessage());
		}
		finally{
			try{
				ourSocket.close();
				System.out.println("Closed connection.");
			} catch(IOException e) { } // close failed
		}
	}
}