package userInterface.requests;

import java.io.BufferedReader;

import userInterface.IFacade;

public class ShowProjectsRequest extends Request {

	public ShowProjectsRequest(IFacade facade, BufferedReader inputReader) {
		super(facade, inputReader);
	}

	@Override
	public String execute() {
		try {
			// Show List of projects with their status
			System.out.println("Current time: " + facade.getCurrentTime().toString());
			int projectAmount = facade.getProjectAmount();
			String onTime = new String();
			int delay;
			for(int i = 0 ; i < projectAmount ; i++) {
				delay = 0;
				if(facade.isOnTime(i)) {
					onTime = "on time";
				} else {
					onTime = "over time";
					delay = facade.getProjectDelay(i);
				}
				StringBuilder projectHeader = new StringBuilder();
				System.out.println("- Project " + i + " "
						+ facade.getProjectName(i) + ": "
						+ facade.getProjectStatus(i) + ", " + onTime + " (Due "
						+ facade.getProjectDueTime(i).toLocalDate().toString()
						+ ")");
				
			}
			
			// Ask user for project selection
			String input = inputReader.readLine();
			
			// User quits
			if(input.equals("quit"))
				return quit();
			
			// Show overview of project details including list of tasks and their status
			
			
			// Ask user for task selection
			
			
			// User quits
			if(input.equals("quit"))
				return quit();
			
			// Show overview of task details
			
		} catch(Exception e) {

		}
		return null;
	}

	private String quit() {
		return "Left overview";
	}

}
