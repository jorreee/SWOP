package userInterface;

import java.io.BufferedReader;

import userInterface.requests.AdvanceTimeRequest;
import userInterface.requests.CreateProjectRequest;
import userInterface.requests.CreateTaskRequest;
import userInterface.requests.ExitRequest;
import userInterface.requests.HelpRequest;
import userInterface.requests.Request;
import userInterface.requests.UpdateTaskStatusRequest;

public class InputParser {
	
	private IFacade facade;
	private BufferedReader inputReader;
	
	public InputParser(IFacade facade, BufferedReader inputReader) {
		this.facade = facade;
		this.inputReader = inputReader;
	}
	
	public Request parse(String readLine) {
		String[] input = readLine.split(" ");
		
		// Switch on first word
		switch(input[0].toLowerCase()) {
		case "h"		: return new HelpRequest(facade);
		case "exit"		: return new ExitRequest(facade);
		case "advance"	: return new AdvanceTimeRequest(facade, inputReader);
		case "update"	: if(input[1].toLowerCase().equals("task"))
							return new UpdateTaskStatusRequest(facade, inputReader);
		case "create"	: if(input[1].toLowerCase().equals("task"))
							return new CreateTaskRequest(facade, inputReader);
						  if(input[1].toLowerCase().equals("project"))
							return new CreateProjectRequest(facade, inputReader);
		}
		return null;
	}
	
}
