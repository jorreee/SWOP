package userInterface;

import userInterface.requests.*;

public class InputParser {
	
	public Request parse(String readLine) {
		String[] input = readLine.split(" ");
		
		// Switch on first character
		switch(input[0].toLowerCase()) {
		case "h"		: return new HelpRequest();
		case "exit"		: return new ExitRequest();
		}
		return null;
	}
	
}
