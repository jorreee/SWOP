package userInterface.requests;

import java.io.BufferedReader;

import userInterface.IFacade;

public class ShowProjectsRequest extends Request {

	public ShowProjectsRequest(IFacade facade, BufferedReader inputReader) {
		super(facade, inputReader);
	}

	@Override
	public String execute() {
		while(true) {
			try {
				// Show List of projects with their status
				System.out.println("Current time: " + facade.getCurrentTime().toString());
				int projectAmount = facade.getProjectAmount();
				for(int i = 0 ; i < projectAmount ; i++) {
					System.out.println("- Project " + i + " "
							+ facade.getProjectName(i) + ": "
							+ facade.getProjectStatus(i)); // PRINT PROJECT i HEADER
				}

				// Ask user for project selection
				String input = inputReader.readLine();

				// User quits
				if(input.equals("quit"))
					return quit();

				// Parse input
				int projectID = Integer.parseInt(input);

				// Show overview of project details including list of tasks and their status
				String onTime = new String();
				int delay;	

				delay = 0;
				if(facade.isOnTime(projectID)) {
					onTime = "on time";
				} else {
					onTime = "over time";
					delay = facade.getProjectDelay(projectID);
				}

				StringBuilder projectHeader = new StringBuilder();
				projectHeader.append("- Project " + projectID + " "
						+ facade.getProjectName(projectID) + ": "
						+ facade.getProjectStatus(projectID) + ", " + onTime + " (Due "
						+ facade.getProjectDueTime(projectID).toLocalDate().toString());

				if(delay > 0) {
					projectHeader.append("(" + delay + " working minutes short)");
				}
				projectHeader.append(")");

				System.out.println("\"" + facade.getProjectDescription(projectID) + "\""); // PRINT SELECTED PROJECT HEADER

				int taskAmount = facade.getTaskAmount(projectID);
				for(int i = 0 ; i < taskAmount ; i++) {
					System.out.println("  * Task " + i + ": "
							+ facade.getTaskStatus(projectID, i)); // PRINT TASK i FROM SELECTED PROJECT HEADER
				}

				// Ask user for task selection
				input = inputReader.readLine();

				// User quits
				if(input.equals("quit"))
					return quit();

				// Parse input
				int taskID = Integer.parseInt(input);

				// Show overview of task details
				String onTime = new String();
				int delay;	

				delay = 0;
				if(facade.isOnTime(projectID)) {
					onTime = "on time";
				} else {
					onTime = "over time";
					delay = facade.getProjectDelay(projectID);
				}

				StringBuilder projectHeader = new StringBuilder();
				projectHeader.append("- Project " + projectID + " "
						+ facade.getProjectName(projectID) + ": "
						+ facade.getProjectStatus(projectID) + ", " + onTime + " (Due "
						+ facade.getProjectDueTime(projectID).toLocalDate().toString());

				if(delay > 0) {
					projectHeader.append("(" + delay + " working minutes short)");
				}
				projectHeader.append(")");

				System.out.println("\"" + facade.getProjectDescription(projectID) + "\""); // PRINT SELECTED TASK HEADER
				
			} catch(Exception e) {
				System.out.println("Invalid input");
			}
		}
	}

	private String quit() {
		return "Left overview";
	}

}
