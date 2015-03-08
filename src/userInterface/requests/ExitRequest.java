package userInterface.requests;

import userInterface.IFacade;

public class ExitRequest extends Request {

	public ExitRequest(IFacade facade) {
		super(facade);
	}

	@Override
	public String execute() {
		System.exit(0);
		return null;
	}

}
