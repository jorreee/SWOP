package userInterface.requests;

import java.io.BufferedReader;
import java.util.List;

import taskMan.view.ProjectView;
import taskMan.view.TaskView;
import userInterface.IFacade;

public class ShowProjectsRequest extends Request {

	public ShowProjectsRequest(IFacade facade, BufferedReader inputReader) {
		super(facade, inputReader);
	}

	@Override
	public String execute() {
		List<ProjectView> projects = facade.getProjects();
		
		while(true) {
			try {
				// Show List of projects with their status
				System.out.println("Current time: " + facade.getCurrentTime().toString());
				int projectAmount = projects.size();
				for(int i = 0 ; i < projectAmount ; i++) {
					System.out.println("- Project " + i + " "
							+ projects.get(i).getProjectName() + ": "
							+ projects.get(i).getProjectStatusAsString()); // PRINT PROJECT i HEADER
				}

				// Ask user for project selection
				System.out.println("Select a project to view more details (type quit to exit)");
				String input = inputReader.readLine();

				// User quits
				if(input.equals("quit"))
					return quit();

				// Parse input
				int projectID = Integer.parseInt(input);
				
				if(projectID < 0 || projectID > projectAmount)
					throw new IllegalArgumentException();
				
				ProjectView project = projects.get(projectID);
				
				// Show overview of project details including list of tasks and their status
				StringBuilder projectHeader = new StringBuilder(); // build project details
				projectHeader.append("- Project " + projectID + " "
						+ project.getProjectName() + ": "
						+ project.getProjectStatusAsString() + ", ");
				
				int[] delay = null;
				if(!project.isProjectFinished()) {
					if(project.isProjectEstimatedOnTime(facade.getCurrentTime())) {
						projectHeader.append("is estimated on time");
					} else {
						projectHeader.append("is estimated over time");
						delay = project.getEstimatedProjectDelay(facade.getCurrentTime());
					}
				} else {
					delay = project.getCurrentProjectDelay(facade.getCurrentTime());
				}
				projectHeader.append(" (Due "
						+ project.getProjectDueTime().toLocalDate().toString());

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
				System.out.println("\"" + project.getProjectDescription() + "\""); // PRINT SELECTED PROJECT HEADER

				List<TaskView> tasks = project.getTasks();
				
				int taskAmount = tasks.size();
				for(int i = 0 ; i < taskAmount ; i++) {
					StringBuilder taskiHead = new StringBuilder();
					taskiHead.append("  *");
					if(tasks.get(i).isTaskUnacceptableOverdue())
						taskiHead.append("!");
					taskiHead.append(" Task " + i + ":" + tasks.get(i).getTaskStatusAsString());
					if(tasks.get(i).isTaskOnTime()) {
						taskiHead.append(", on time");
					} else {
						taskiHead.append(", over time by " + tasks.get(i).getTaskOvertimePercentage() + "%");
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
				
				if(taskID < 0 || taskID > taskAmount)
					throw new IllegalArgumentException();

				TaskView task = tasks.get(taskID);
				
				// Show overview of task details
				StringBuilder taskHeader = new StringBuilder(); // Build task details
				taskHeader.append("  *");
				if(task.isTaskUnacceptableOverdue());
					taskHeader.append("!");
				taskHeader.append(" Task " + taskID + " "
						+ task.getTaskStatusAsString() + ": "
						+ task.getTaskDescription() + ", "
						+ task.getEstimatedTaskDuration() + " minutes, "
						+ task.getAcceptableTaskDeviation() + "% margin");

				if(task.isTaskOnTime())
					taskHeader.append(", on time");
				else {
					taskHeader.append(", over time by " + task.getTaskOvertimePercentage() + "%");
				}

				if(task.hasTaskPrerequisites()) {
					List<TaskView> prereqs = task.getTaskPrerequisites();
					taskHeader.append(", depends on");
					for(int i = 0 ; i < prereqs.size() ; i++) {
						if(i == 0)
							taskHeader.append(" task " + prereqs.get(i).getID());
						else
							taskHeader.append(prereqs.get(i).getID());
						if(i < prereqs.size() - 1)
							taskHeader.append(" and ");
					}
				}
				if(task.isTaskAlternative())
					taskHeader.append(", alternative to task " + task.getTaskAlternativeTo().getID());
				if(task.hasEnded())
					taskHeader.append(", started " + task.getTaskStartTime().toString() + " , finished " + task.getTaskEndTime().toString());
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
