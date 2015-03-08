package userInterface;

import java.io.BufferedReader;

import userInterface.requests.AdvanceTimeRequest;
import userInterface.requests.ExitRequest;
import userInterface.requests.HelpRequest;
import userInterface.requests.Request;

public class InputParser {
	
	private IFacade facade;
	private BufferedReader inputReader;
	
	public InputParser(IFacade facade, BufferedReader inputReader) {
		this.facade = facade;
		this.inputReader = inputReader;
	}
	
	public Request parse(String readLine) {
		String[] input = readLine.split(" ");
		
		// Switch on first character
		switch(input[0].toLowerCase()) {
		case "h"		: return new HelpRequest(facade);
		case "exit"		: return new ExitRequest(facade);
		case "advance"	: return new AdvanceTimeRequest(facade, inputReader);
		}
		return null;
	}
	
}
