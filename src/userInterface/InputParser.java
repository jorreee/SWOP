package userInterface;

import java.io.BufferedReader;

import userInterface.requests.AdvanceTimeRequest;
import userInterface.requests.ChangeUserRequest;
import userInterface.requests.CreateProjectRequest;
import userInterface.requests.CreateTaskRequest;
import userInterface.requests.ExitRequest;
import userInterface.requests.HelpRequest;
import userInterface.requests.InvalidRequest;
import userInterface.requests.Request;
import userInterface.requests.ShowProjectsRequest;
import userInterface.requests.SimulationRequest;
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
		try {
			switch(input[0].toLowerCase()) {
			case "h"		: 
				return new HelpRequest(facade, inputReader);
			case "exit"		: 
				return new ExitRequest(facade, inputReader);
			case "advance"	: 
				return new AdvanceTimeRequest(facade, inputReader);
			case "update"	:
				if(input[1].toLowerCase().equals("task")) {
					return new UpdateTaskStatusRequest(facade, inputReader);
				} else {
					throw new IllegalArgumentException();
				}
			case "create"	: 
				if(input[1].toLowerCase().equals("task")) {
					return new CreateTaskRequest(facade, inputReader);
				}
				if(input[1].toLowerCase().equals("project")) {
					return new CreateProjectRequest(facade, inputReader);
				}
				else {
					throw new IllegalArgumentException();
				}
			case "show"		: 
				return new ShowProjectsRequest(facade, inputReader);
			case "simulate" :
				return new SimulationRequest(facade, inputReader);
			case "change"	:
				if(input[1].toLowerCase().equals("user")) {
					return new ChangeUserRequest(facade, inputReader);
				} else {
					throw new IllegalArgumentException();
				}
			default	: 
				return new InvalidRequest(facade, inputReader);
			}
		}catch(Exception e) {
			return new InvalidRequest(facade, inputReader);
		}
	}
}

