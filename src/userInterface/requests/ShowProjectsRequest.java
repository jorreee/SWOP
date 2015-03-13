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
				
				if(projectID < 0 || projectID > facade.getProjectAmount())
					throw new IllegalArgumentException();
				
				// Show overview of project details including list of tasks and their status
				StringBuilder projectHeader = new StringBuilder(); // build project details
				projectHeader.append("- Project " + projectID + " "
						+ facade.getProjectName(projectID) + ": "
						+ facade.getProjectStatus(projectID) + ", ");
				
				int[] delay = null;
				if(!facade.isProjectFinished(projectID)) {
					if(facade.isProjectEstimatedOnTime(projectID)) {
						projectHeader.append("is estimated on time");
					} else {
						projectHeader.append("is estimated over time");
						delay = facade.getEstimatedProjectDelay(projectID);
					}
				} else {
					delay = facade.getProjectDelay(projectID);
				}
				projectHeader.append(" (Due "
						+ facade.getProjectDueTime(projectID).toLocalDate().toString());

				if(delay != null) {
					projectHeader.append("(");
					if(delay[0] != 0) projectHeader.append(delay[0] + " working years "); // years
					if(delay[1] != 0) projectHeader.append(delay[1] + " working months "); // months
					if(delay[2] != 0) projectHeader.append(delay[2] + " working days "); // days
					if(delay[3] != 0) projectHeader.append(delay[3] + " working hours "); // hours
					if(delay[4] != 0) projectHeader.append(delay[4] + " working minutes "); // minutes
					projectHeader.append("short)");
				}
				projectHeader.append(")");

				System.out.println(projectHeader.toString());
				System.out.println("\"" + facade.getProjectDescription(projectID) + "\""); // PRINT SELECTED PROJECT HEADER

				int taskAmount = facade.getTaskAmount(projectID);
				for(int i = 0 ; i < taskAmount ; i++) {
					StringBuilder taskiHead = new StringBuilder();
					taskiHead.append("  *");
					if(facade.isTaskUnacceptableOverdue(projectID, i))
						taskiHead.append("!");
					taskiHead.append(" Task " + i + ":" + facade.getTaskStatus(projectID, i));
					if(facade.isTaskOnTime(projectID, i))
						taskiHead.append(", on time");
					else {
						taskiHead.append(", over time by " + facade.getTaskOverTimePercentage(projectID, i) + "%");
					}
					System.out.println(taskiHead.toString()); // PRINT TASK i FROM SELECTED PROJECT HEADER
				}

				// Ask user for task selection
				System.out.println("Select a task to view more details (type quit to exit)");
				input = inputReader.readLine();

				// User quits
				if(input.equals("quit"))
					return quit();

				// Parse input
				int taskID = Integer.parseInt(input);
				
				if(taskID < 0 || taskID > facade.getTaskAmount(projectID))
					throw new IllegalArgumentException();

				// Show overview of task details
				StringBuilder taskHeader = new StringBuilder(); // Build task details
				taskHeader.append("  *");
				if(facade.isTaskUnacceptableOverdue(projectID, taskID))
					taskHeader.append("!");
				taskHeader.append(" Task " + taskID + " "
						+ facade.getTaskStatus(projectID, taskID) + ": "
						+ facade.getTaskDescription(projectID, taskID) + ", "
						+ facade.getEstimatedTaskDuration(projectID, taskID) + " minutes, "
						+ facade.getAcceptableTaskDeviation(projectID, taskID) + "% margin");

				if(facade.isTaskOnTime(projectID, taskID))
					taskHeader.append(", on time");
				else {
					taskHeader.append(", over time by " + facade.getTaskOverTimePercentage(projectID, taskID) + "%");
				}

				if(facade.hasTaskPrerequisites(projectID, taskID)) {
					List<Integer> prereqs = facade.getTaskPrerequisitesFor(projectID, taskID);
					taskHeader.append(", depends on");
					for(int i = 0 ; i < prereqs.size() ; i++) {
						if(i == 0)
							taskHeader.append(" task " + prereqs.get(i));
						else
							taskHeader.append(prereqs.get(i));
						if(i < prereqs.size() - 1)
							taskHeader.append(" and ");
					}
				}
				if(facade.hasTaskAlternative(projectID, taskID))
					taskHeader.append(", alternative to task " + facade.getTaskAlternativeTo(projectID, taskID));
				if(facade.hasTaskEnded(projectID, taskID))
					taskHeader.append(", started " + facade.getTaskStartTime(projectID, taskID).toString() + " , finished " + facade.getTaskEndTime(projectID, taskID).toString());
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
