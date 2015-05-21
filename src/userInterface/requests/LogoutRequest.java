package userInterface.requests;

import java.io.BufferedReader;

import userInterface.IFacade;

public class LogoutRequest extends Request {

	public LogoutRequest(IFacade facade, BufferedReader inputReader) {
		super(facade, inputReader);
	}

	@Override
	public String execute() {
		facade.logout();
		return "Logged out of the current branch office";
	}

	@Override
	public boolean isSimulationSupported() {
		return false;
	}

}
