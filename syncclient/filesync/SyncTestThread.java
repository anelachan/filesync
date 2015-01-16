package filesync;

import java.io.IOException;

/*
 * This test thread provides comments to explain how the Client/Server 
 * architecture could be implemented that uses the file synchronisation protocol.
 */

public class SyncTestThread implements Runnable {

	SynchronisedFile fromFile; // this would be on the Client
	SynchronisedFile toFile; // this would be on the Server
	
	SyncTestThread(SynchronisedFile ff,SynchronisedFile tf){
		fromFile=ff;
		toFile=tf;
	}
	
	@Override
	public void run() {
		Instruction inst;
		InstructionFactory instFact=new InstructionFactory();
		// The Client reads instructions to send to the Server
		while((inst=fromFile.NextInstruction())!=null){
			String msg=inst.ToJSON(); 
			System.out.println("Sending: "+msg);
			/*
			 * Pretend the Client sends the msg to the Server.
			 */
			
			// network delay
			
			/*
			 * The Server receives the instruction here.
			 */
			Instruction receivedInst = instFact.FromJSON(msg);
			
			try {
				// The Server processes the instruction
				toFile.ProcessInstruction(receivedInst);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1); // just die at the first sign of trouble
			} catch (BlockUnavailableException e) {
				// The server does not have the bytes referred to by the block hash.
				try {
					/*
					 * At this point the Server needs to send a request back to the Client
					 * to obtain the actual bytes of the block.
					 */
					
					// network delay
					
					/*
					 * Client upgrades the CopyBlock to a NewBlock instruction and sends it.
					 */
					Instruction upgraded=new NewBlockInstruction((CopyBlockInstruction)inst);
					String msg2 = upgraded.ToJSON();
					System.out.println("Sending: "+msg2);
					
					
					// network delay
					
					/*
					 * Server receives the NewBlock instruction.
					 */
					Instruction receivedInst2 = instFact.FromJSON(msg2);
					toFile.ProcessInstruction(receivedInst2);
				} catch (IOException e1) {
					e1.printStackTrace();
					System.exit(-1);
				} catch (BlockUnavailableException e1) {
					assert(false); // a NewBlockInstruction can never throw this exception
				}
			}
			/*
			 * If using a synchronous RequestReply protocol, the server can now acknowledge 
			 * that the block was correctly received, and the next instruction can be sent.
			 */
			
			// network delay
			
			/*
			 * Client receives acknowledgement and moves on to process next instruction.
			 */
		} // get next instruction loop forever
	}
}
