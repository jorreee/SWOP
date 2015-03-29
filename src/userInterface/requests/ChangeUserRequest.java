package userInterface.requests;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import userInterface.IFacade;

public class ChangeUserRequest extends Request{

	public ChangeUserRequest(IFacade facade, BufferedReader inputReader) {
		super(facade, inputReader);
	}

	@Override
	public String execute() {
		while(true) {
			// Display current user
			System.out.println("Currently logged in as " + facade.getCurrentUsername());
			// Display different options
			List<String> possibleUsers = facade.getPossibleUsernames();

			for(String username : possibleUsers)
				System.out.println("Possible user: " + username);
			
			// Ask user for username to log in as
			System.out.println("Select a user (type quit to exit)");
			try{
				// Read User Input
				String userInput = inputReader.readLine();
				// Escape
				if(userInput.equalsIgnoreCase("quit"))
					return "User unaltered";
				
				if(facade.changeToUser(userInput)) // Valid User
					return "Now logged in as " + facade.getCurrentUsername();
				else // Invalid User
					System.out.println("Invalid username, try again");
			} catch(IOException e) {
				System.out.println("Invalid username, try again");
			}
		}
	}

}
