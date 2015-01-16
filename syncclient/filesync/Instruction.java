package filesync;

/**
 * @author aaron
 * @date 7th April 2013
 */

import org.json.simple.parser.JSONParser;

/*
 * All instructions have a Type which is a String name.
 * All instructions can produce a JSON String for network communication.
 * Use the InstructionFactory class to convert a JSON String back to an Instruction class.
 */

abstract class Instruction {

	protected static final JSONParser parser = new JSONParser();
	
	abstract String Type();
	
	abstract String ToJSON();

	abstract void FromJSON(String jst);
}
