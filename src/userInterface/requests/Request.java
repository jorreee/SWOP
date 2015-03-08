package userInterface.requests;

import userInterface.IFacade;

public abstract class Request {
	
	protected IFacade facade;
	
	public Request(IFacade facade) {
		this.facade = facade;
	}
	
	public abstract String execute();
	
}
