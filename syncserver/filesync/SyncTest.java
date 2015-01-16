package filesync;

/**
 * @author aaron
 * @date 7th April 2013
 */

import java.io.File;
import java.io.IOException;

/*
 * SyncTest is a test class. It is not a client/server program.
 * Both the sender and receiver are maintained locally.
 * Two file names should be given as arguments: fromFile
 * and toFile.
 * SyncTest will call the synchroniser every
 * 5 seconds to copy changes from fromFile to toFile.
 */

public class SyncTest {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SynchronisedFile fromFile=null,toFile=null;
		
		/*
		 * Initialise the SynchronisedFiles.
		 */
		try {
			fromFile=new SynchronisedFile(args[0]);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		try {
			toFile=new SynchronisedFile(args[1]);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		/*
		 * Start a thread to service the Instruction queue.
		 */
		Thread stt = new Thread(new SyncTestThread(fromFile,toFile));
		stt.start();
		
		/*
		 * Continue forever, checking the fromFile every 5 seconds.
		 */
		while(true){
			try {
				// TODO: skip if the file is not modified
				System.out.println("SynchTest: calling fromFile.CheckFileState()");
				fromFile.CheckFileState();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(-1);
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
		
		
	}

}
