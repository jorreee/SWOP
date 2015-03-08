package userInterface.requests;

import java.io.BufferedReader;
import java.time.LocalDateTime;

import userInterface.IFacade;

public class AdvanceTimeRequest extends Request {

	private BufferedReader inputReader;

	public AdvanceTimeRequest(IFacade facade, BufferedReader inputReader) {
		super(facade);
		this.inputReader = inputReader;
	}

	@Override
	public String execute() {
		LocalDateTime currentTime = facade.getCurrentTime();
		//Allow user to choose new timestamp
		System.out.println("The current time is " + currentTime.toString() + ", advance time to? (type quit to exit)");
		while(true) {
			try {
				String input = inputReader.readLine();

				// User requests not to alter timestamp
				if(input.toLowerCase().equals("quit"))
					return "Time unaltered";

				// Parse timestamp
				LocalDateTime time = LocalDateTime.parse(input);

				// Invalid timestamp
				if(currentTime.isAfter(time)) {
					System.out.println("Invalid time, you can only specify a later date. Please try again");
				} else {// Correct timestamp
					facade.advanceTime(time);
					return "Time advanced to " + time.toString();
				}
				// Use Case ends
			} catch(Exception e) {
				System.out.println("Invalid input, try again");
			}
		}
	}
}