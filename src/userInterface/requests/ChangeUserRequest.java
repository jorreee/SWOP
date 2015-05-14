package userInterface.requests;

import java.io.BufferedReader;
import java.util.List;

import userInterface.IFacade;

import company.BranchView;
import company.taskMan.resource.ResourceView;

public class ChangeUserRequest extends Request{

	public ChangeUserRequest(IFacade facade, BufferedReader inputReader) {
		super(facade, inputReader);
	}

	@Override
	public String execute() {
		while(true) {
			// Display current user
			if(facade.isLoggedIn()) {
				System.out.println("Currently logged in as " + facade.getCurrentUser().getName());
			} else {
				System.out.println("Currently not logged in");
			}

			// SELECT BRANCH
			List<BranchView> branches = facade.getBranches();

			System.out.println("Please select your branch (type quit to exit)");
			for(int i = 0 ; i < branches.size() ; i++) {
				System.out.println("<" + i + "> The " + branches.get(i).getGeographicLocation() + " Branch");
			}

			try {
				String userInput = inputReader.readLine();
				// Escape
				if(userInput.equalsIgnoreCase("quit"))
				{
					return quit();
				}

				facade.selectBranch(branches.get(Integer.parseInt(userInput)));
			} catch(Exception e) {
				e.printStackTrace();
				System.out.println("Invalid branch, try again");
			}

			// SELECT USER
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

				facade.changeToUser(possibleUsers.get(Integer.valueOf(userInput))); // Valid User
				return "Now logged in as " + facade.getCurrentUser().getName();
			} catch(Exception e) {
				e.printStackTrace();
				System.out.println("Invalid username, try again");
			}
		}
	}

	private String quit() {
		return "User unaltered";
	}

}
