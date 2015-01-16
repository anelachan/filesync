package filesync;

/**
 * @author aaron
 * @date 7th April 2013
 */

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;

/*
 * The StartUpdate instruction prepares the receiver to synchronise
 * the file. It must be issued prior to receiving updates.
 */

public class StartUpdateInstruction extends Instruction {

	@Override
	String Type() {
		return "StartUpdate";
	}

	@SuppressWarnings("unchecked")
	@Override
	String ToJSON() {
		JSONObject obj=new JSONObject();
		obj.put("Type", Type());
		return obj.toJSONString();
	}

	@Override
	void FromJSON(String jst) {
		// TODO Auto-generated method stub
		
	}

}
