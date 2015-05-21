package userInterface.requests;

import java.io.BufferedReader;

import userInterface.IFacade;

public class InvalidRequest extends Request {

	private String message;
	
	public InvalidRequest(IFacade facade, BufferedReader inputReader, String message) {
		super(facade, inputReader);
		this.message = message;
	}

	@Override
	public String execute() {
		return "Invalid command! (" + message + ")";
	}

	@Override
	public boolean isSimulationSupported() {
		return true;
	}

}
