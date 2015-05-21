package userInterface.requests;

import java.io.BufferedReader;
import java.util.List;

import userInterface.IFacade;
import userInterface.TaskManException;

import company.BranchView;

public class ChangeBranchRequest extends Request{

	public ChangeBranchRequest(IFacade facade, BufferedReader inputReader) {
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
			} catch(TaskManException e) {
				System.out.println(e.getMessage());
			} catch(Exception e) {
				e.printStackTrace();
				System.out.println("Invalid branch, try again");
			}

			// SELECT USER
			ChangeUserRequest chr = new ChangeUserRequest(facade, inputReader);
			return chr.execute();		
		}
	}

	private String quit() {
		return "User unaltered";
	}

	@Override
	public boolean isSimulationSupported() {
		return false;
	}

}
