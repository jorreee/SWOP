package userInterface.requests;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import userInterface.IFacade;

import company.BranchView;
import company.taskMan.ProjectView;
import company.taskMan.project.TaskView;
import company.taskMan.resource.ResourceView;

public class ShowProjectsRequest extends Request {

	public ShowProjectsRequest(IFacade facade, BufferedReader inputReader) {
		super(facade, inputReader);
	}

	@Override
	public String execute() {
		List<BranchView> branches = facade.getBranches();

		while(true) {
			try {
				// For each branch, show List of projects with their status
				for(int i = 0 ; i < branches.size() ; i++) {
					System.out.println("<" + i +  "> " + branches.get(i).getGeographicLocation() + " Branch:");
					List<ProjectView> projects = branches.get(i).getProjects();
					int projectAmount = projects.size();
					if(projectAmount == 0) {
						System.out.println("  - No projects are present in this branch yet. ");
					}
					for(int j = 0 ; j < projectAmount ; j++) {
						System.out.println("  - Project " + j + " "
								+ projects.get(j).getName() + ": "
								+ projects.get(j).getStatusAsString()); // PRINT PROJECT i HEADER
					}
				}
				// Ask user for project selection
				System.out.println("Select a branch and project to view more details (Format: branch location in list and project location in list, seperated by spaces (type quit to exit)");
				String input = inputReader.readLine();

				// User quits
				if(input.equals("quit"))
					return quit();

				// Parse input
				String[] splitInput = input.split(" ");
				int branchID = Integer.parseInt(splitInput[0]);
				int projectID = Integer.parseInt(splitInput[1]);
				
				BranchView branch = branches.get(branchID);
				ProjectView project = branch.getProjects().get(projectID);

				// Show overview of project details including list of tasks and their status
				StringBuilder projectHeader = new StringBuilder(); // build project details
				projectHeader.append("- Project " + projectID + " "
						+ project.getName() + ": "
						+ project.getStatusAsString() + ", ");

				int[] delay = null;
				//				if(!project.isFinished()) { 
				if(project.isEstimatedOnTime(facade.getCurrentTime())) {
					projectHeader.append("on time");
				} else {
					projectHeader.append("over time");
				}
				//				}
				delay = project.getDelay(facade.getCurrentTime());

				projectHeader.append(" (Due "
						+ project.getDueTime().toLocalDate().format(dateFormatter));

				if(!Arrays.equals(delay, new int[]{ 0,0,0,0,0 })) {
					projectHeader.append(", ");
					if(delay[0] != 0) projectHeader.append(delay[0] + " working years "); // years
					if(delay[1] != 0) projectHeader.append(delay[1] + " working months "); // months
					if(delay[2] != 0) projectHeader.append(delay[2] + " working days "); // days
					if(delay[3] != 0) projectHeader.append(delay[3] + " working hours "); // hours
					if(delay[4] != 0) projectHeader.append(delay[4] + " working minutes "); // minutes
					projectHeader.append("short");
				}
				
				if(!project.isFinished()) {
					projectHeader.append(", estimated to end on "
							+ project.getEstimatedEndTime(facade.getCurrentTime()).format(dateTimeFormatter));
				}
				
				projectHeader.append(")");

				System.out.println(projectHeader.toString());
				System.out.println("   \"" + project.getDescription() + "\""); // PRINT SELECTED PROJECT HEADER

				List<TaskView> tasks = project.getTasks();

				int taskAmount = tasks.size();
				if(taskAmount > 0) {
					for(int i = 0 ; i < taskAmount ; i++) {
						StringBuilder taskiHead = new StringBuilder();
						taskiHead.append("  {" + i + "}");
						if(tasks.get(i).isUnacceptableOverdue(facade.getCurrentTime())) {
							taskiHead.append("!");
						}
						taskiHead.append(" Task \"" + tasks.get(i).getDescription() + "\": " + tasks.get(i).getStatusAsString());
						if(tasks.get(i).isOnTime(facade.getCurrentTime())) {
							taskiHead.append(", on time");
						} else {
							taskiHead.append(", over time by " + tasks.get(i).getOverTimePercentage(facade.getCurrentTime()) + "%");
						}
						if(tasks.get(i).isDelegated()) {
							taskiHead.append(", responsible branch " + facade.getResponsibleBranch(project, tasks.get(i), branch).get().getGeographicLocation());
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

					if(taskID < 0 || taskID > taskAmount) {
						throw new IllegalArgumentException();
					}

					TaskView task = tasks.get(taskID);

					// Show overview of task details
					StringBuilder taskHeader = new StringBuilder(); // Build task details
					taskHeader.append("  *");
					if(task.isUnacceptableOverdue(facade.getCurrentTime())) {
						taskHeader.append("!");
					}
					taskHeader.append(" Task " + taskID + " "
							+ task.getStatusAsString() + ": "
							+ task.getDescription() + ", "
							+ task.getEstimatedDuration() + " minutes, "
							+ task.getAcceptableDeviation() + "% margin");
					if(task.isPlanned()) {
						taskHeader.append(", planned to start " + task.getPlannedBeginTime());
						Iterator<ResourceView> devs = task.getPlannedDevelopers().iterator();
						if(devs.hasNext()) {
							taskHeader.append("assigned developers: ");
						}
						while(devs.hasNext()) {
							ResourceView dev = devs.next();
							taskHeader.append(dev.getName());
							if(devs.hasNext()) {
								taskHeader.append(", ");
							}
						}
						Iterator<ResourceView> reservedResources = task.getReservedResources().iterator();
						if(reservedResources.hasNext()) {
							taskHeader.append("reserved resources: ");
						}
						while(reservedResources.hasNext()) {
							ResourceView reservedResource = reservedResources.next();
							taskHeader.append(reservedResource.getName());
							if(reservedResources.hasNext()) {
								taskHeader.append(", ");
							}
						}
					}
					if(task.isOnTime(facade.getCurrentTime())) {
						taskHeader.append(", on time");
					}
					else {
						taskHeader.append(", over time by " + task.getOverTimePercentage(facade.getCurrentTime()) + "%");
					}
					if(task.isDelegated()) {
						taskHeader.append(", responsible branch " + facade.getResponsibleBranch(project, task, branch).get().getGeographicLocation());
					}
					if(task.hasPrerequisites()) {
						List<TaskView> prereqs = task.getPrerequisites();
						taskHeader.append(", depends on");
						for(int i = 0 ; i < prereqs.size() ; i++) {
							if(i == 0)
								taskHeader.append(" task \"" + prereqs.get(i).getDescription() + "\"");
							else
								taskHeader.append("\"" + prereqs.get(i).getDescription() + "\"");
							if(i < prereqs.size() - 1)
								taskHeader.append(" and ");
						}
					}
					if(task.isAlternative()) {
						taskHeader.append(", alternative to task \"" + task.getAlternativeTo().getDescription() + "\"");
					}
					if(task.hasEnded()){
						taskHeader.append(", started " + task.getStartTime().format(dateTimeFormatter) + " , finished " + task.getEndTime().format(dateTimeFormatter));
					}
					System.out.println(taskHeader.toString()); // PRINT SELECTED TASK HEADER

				} else {
					System.out.println("This project doesn't have any tasks yet. \n");
				}

			} catch(Exception e) {
				e.printStackTrace();
				System.out.println("Invalid input");
			}
		}
	}

	private String quit() {
		return "Left overview";
	}

}
