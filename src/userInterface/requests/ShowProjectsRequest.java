package userInterface.requests;

import java.io.BufferedReader;
import java.util.List;

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
				System.out.println("Select a project to view more details (type quit to exit)");
				String input = inputReader.readLine();

				// User quits
				if(input.equals("quit"))
					return quit();

				// Parse input
				int projectID = Integer.parseInt(input);

				// Show overview of project details including list of tasks and their status
				String onTime = new String(); // build project details
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
				
				System.out.println(projectHeader.toString());
				System.out.println("\"" + facade.getProjectDescription(projectID) + "\""); // PRINT SELECTED PROJECT HEADER

				int taskAmount = facade.getTaskAmount(projectID);
				for(int i = 0 ; i < taskAmount ; i++) {
					System.out.println("  * Task " + i + ": "
							+ facade.getTaskStatus(projectID, i)); // PRINT TASK i FROM SELECTED PROJECT HEADER
				}

				// Ask user for task selection
				System.out.println("Select a task to view more details (type quit to exit)");
				input = inputReader.readLine();

				// User quits
				if(input.equals("quit"))
					return quit();

				// Parse input
				int taskID = Integer.parseInt(input);

				// Show overview of task details
				StringBuilder taskHeader = new StringBuilder(); // Build task details
				taskHeader.append("  * Task " + taskID + " "
						+ facade.getTaskStatus(projectID, taskID) + ": "
						+ facade.getTaskDescription(projectID, taskID) + ", "
						+ facade.getEstimatedTaskDuration(projectID, taskID) + " minutes, "
						+ facade.getAcceptableTaskDeviation(projectID, taskID) + "% margin");
				
				if(facade.hasTaskPrerequisites(projectID, taskID)) {
					List<Integer> prereqs = facade.getTaskPrerequisitesFor(projectID, taskID);
					taskHeader.append(", depends on");
					for(int i = 0 ; i < prereqs.size() ; i++) {
						if(i == 0)
							taskHeader.append(" task " + prereqs.get(i));
						if(i < prereqs.size() - 1)
							taskHeader.append(" and ");
					}
				}
				if(facade.hasTaskAlternative(projectID, taskID))
					taskHeader.append(", alternative to task " + facade.getTaskAlternativeTo(projectID, taskID));
				if(facade.hasTaskStarted(projectID, taskID))
					taskHeader.append(", started " + facade.getTaskStartTime(projectID, taskID).toString());
				if(facade.hasTaskEnded(projectID, taskID))
					taskHeader.append(", finished " + facade.getTaskEndTime(projectID, taskID).toString());
				System.out.println(taskHeader.toString()); // PRINT SELECTED TASK HEADER
				
			} catch(Exception e) {
				System.out.println("Invalid input");
			}
		}
	}

	private String quit() {
		return "Left overview";
	}

}
