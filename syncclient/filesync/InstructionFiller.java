package filesync;
/* Author: Anela
Date: 2 May 2014
Name: PullThread.java
Description: This thread fills the Instruction queue at the source,
checking every 5 seconds for changes to the source file.
*/
import java.net.*;
import java.io.*;

public class InstructionFiller extends Thread{

	SynchronisedFile fromFile;

	InstructionFiller(SynchronisedFile ff){
		fromFile = ff;
		this.start();
	}

	public void run(){
		while(true){
			try {
				fromFile.CheckFileState();
				Thread.sleep(5000);
				System.out.println("Source: calling fromFile.CheckFileState()");
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