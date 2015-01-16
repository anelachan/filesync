package filesync;

/**
 * @author aaron
 * @date 7th April 2013
 */

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;

/*
 * A CopyBlock instruction does not contain the actual bytes of the block.
 * Rather it assumes the bytes are already at the server, from a previous
 * synchronisation step. The instruction merely sends a hash of the bytes.
 * If the file at the server has changed by other processes, causing the
 * bytes to be actually not available, then the CopyBlock needs to be 
 * upgraded to a NewBlock that contains the actual bytes and this needs 
 * to be sent.
 */

public class CopyBlockInstruction extends Instruction {

	Block blk;
	
	CopyBlockInstruction(){
		
	}
	
	CopyBlockInstruction(Block blk){
		this.blk=(Block)blk.clone();
	}

	CopyBlockInstruction(CopyBlockInstruction inst){
		this.blk = inst.blk.clone();
	}
	
	/*
	 * Getters and Setters
	 */
	public Block getBlock() {
		return blk;
	}

	public void setBlock(Block blk) {
		this.blk = blk;
	}

	@Override
	String Type() {
		return "CopyBlock";
	}

	@SuppressWarnings("unchecked")
	@Override
	String ToJSON() {
		JSONObject obj=new JSONObject();
		obj.put("Type", Type());
		try {
			obj.put("hash", new String(Base64.encodeBase64(blk.getHash().getBytes()),"US-ASCII"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		obj.put("length", blk.getLength());
		obj.put("offset", blk.getOffset());
		return obj.toJSONString();
	}

	@Override
	void FromJSON(String jst) {
		JSONObject obj=null;
		
		try {
			obj = (JSONObject) parser.parse(jst);
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		if(obj!=null){
			blk=new Block();
			blk.setHash(new String(Base64.decodeBase64((String) obj.get("hash"))));
			blk.setLength((Long) obj.get("length"));
			blk.setOffset((Long) obj.get("offset"));
		}
		
	}

	
	
}
