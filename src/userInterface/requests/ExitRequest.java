package userInterface.requests;

import java.io.BufferedReader;

import userInterface.IFacade;

public class ExitRequest extends Request {

	public ExitRequest(IFacade facade, BufferedReader inputReader) {
		super(facade, inputReader);
	}

	@Override
	public String execute() {
		System.exit(0);
		return null;
	}

	@Override
	public boolean isSimulationSupported() {
		return false;
	}

}
