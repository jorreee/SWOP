package userInterface.requests;

import java.io.BufferedReader;
import java.time.LocalDateTime;

import userInterface.IFacade;

public class AdvanceTimeRequest extends Request {

	public AdvanceTimeRequest(IFacade facade, BufferedReader inputReader) {
		super(facade, inputReader);
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
				
				// Advance time
				boolean success = facade.advanceTimeTo(time);
				
				// Invalid timestamp
				if(!success) {
					System.out.println("Invalid time");
				} else {// Correct timestamp
					return "Time advanced to " + time.toString();
				}
				// Use Case ends
			} catch(Exception e) {
				System.out.println("Invalid input, try again");
			}
		}
	}
}