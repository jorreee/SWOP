package userInterface.requests;

import java.io.BufferedReader;

import userInterface.IFacade;

public class InvalidRequest extends Request {

	public InvalidRequest(IFacade facade, BufferedReader inputReader) {
		super(facade, inputReader);
	}

	@Override
	public String execute() {
		return "Invalid command!";
	}

}
