package filesync;

/**
 * @author aaron
 * @date 7th April 2013
 */

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;

/*
 * A NewBlock instruction contains the actual bytes of the block. It should
 * be sent only when the Server does not actually have the bytes already.
 * Usually this is when the file is being synchronised for the first time, or
 * when changes occur that need to be sent to the server. A CopyBlock can be
 * upgraded to a NewBlock instruction using the constructor.
 * 
 * (In fact, both instructions contain the actual bytes, but the marshalling
 * to JSON does not include the bytes for the CopyBlock.)
 */

public class NewBlockInstruction extends Instruction {

	Block b;
	
	NewBlockInstruction(){
		
	}
	
	NewBlockInstruction(Block b){
		this.b = b.clone();
	}
	
	NewBlockInstruction(CopyBlockInstruction cb){
		this.b = cb.getBlock().clone();
	}

	NewBlockInstruction(NewBlockInstruction inst){
		this.b = inst.b.clone();
	}

	/*
	 * Getters and Setters
	 */
	
	public Block getBlock() {
		return b;
	}

	public void setBlock(Block b) {
		this.b = b;
	}

	@Override
	String Type() {
		return "NewBlock";
	}

	@SuppressWarnings("unchecked")
	@Override
	String ToJSON() {
		JSONObject obj=new JSONObject();
		try {
			obj.put("Type", Type());
			obj.put("hash", new String(Base64.encodeBase64(b.getHash().getBytes()),"US-ASCII"));
			obj.put("length", b.getLength());
			obj.put("offset", b.getOffset());
			obj.put("bytes", new String(Base64.encodeBase64(b.getBytes()),"US-ASCII"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.exit(-1);
		}
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
			b=new Block();
			b.setHash(new String(Base64.decodeBase64((String) obj.get("hash"))));
			b.setLength((Long) obj.get("length"));
			b.setOffset((Long) obj.get("offset"));
			b.setBytes(Base64.decodeBase64((String) obj.get("bytes")));
		}
	}
	
	
}
