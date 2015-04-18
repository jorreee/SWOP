package userInterface.requests;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import taskMan.view.ResourceView;
import userInterface.IFacade;

public class ChangeUserRequest extends Request{

	public ChangeUserRequest(IFacade facade, BufferedReader inputReader) {
		super(facade, inputReader);
	}

	@Override
	public String execute() {
		while(true) {
			// Display current user
			System.out.println("Currently logged in as " + facade.getCurrentUser().getName());
			// Display different options
			List<ResourceView> possibleUsers = facade.getPossibleUsers();

			int i = 0;
			for(ResourceView user : possibleUsers) {
				System.out.println("(" + i + ") Possible user: " + user.getName());
				i++;
			}
			
			// Ask user for username to log in as
			System.out.println("Select a user (type quit to exit)");
			try{
				// Read User Input
				String userInput = inputReader.readLine();
				// Escape
				if(userInput.equalsIgnoreCase("quit"))
					return quit();
				
				if(facade.changeToUser(possibleUsers.get(Integer.valueOf(userInput)))) // Valid User
					return "Now logged in as " + facade.getCurrentUser().getName();
				else // Invalid User
					System.out.println("Invalid username, try again");
			} catch(Exception e) {
				System.out.println("Invalid username, try again");
			}
		}
	}

	private String quit() {
		return "User unaltered";
	}
	
}
