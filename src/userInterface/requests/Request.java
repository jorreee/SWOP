package userInterface.requests;

import java.io.BufferedReader;

import userInterface.IFacade;

public abstract class Request {
	
	protected IFacade facade;
	protected BufferedReader inputReader;
	
	public Request(IFacade facade, BufferedReader inputReader) {
		this.facade = facade;
		this.inputReader = inputReader;
	}
	
	public abstract String execute();
	
}
