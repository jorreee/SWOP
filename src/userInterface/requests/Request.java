package userInterface.requests;

import java.io.BufferedReader;
import java.time.format.DateTimeFormatter;

import userInterface.IFacade;

public abstract class Request {
	
	protected IFacade facade;
	protected BufferedReader inputReader;
	
	protected DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	protected DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	protected DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	
	public Request(IFacade facade, BufferedReader inputReader) {
		this.facade = facade;
		this.inputReader = inputReader;
	}
	
	public abstract String execute();

	public abstract boolean isSimulationSupported();
	
}
