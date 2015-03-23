package userInterface.requests;

import java.io.BufferedReader;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import userInterface.IFacade;

public class UpdateTaskStatusRequest extends Request {

	public UpdateTaskStatusRequest(IFacade facade, BufferedReader inputReader) {
		super(facade, inputReader);
	}

	@Override
	public String execute() {
		// Show list of available tasks and their project
		List<ProjectView> projects = facade.getProjects();

		for(ProjectView project : projects) {
			System.out.println("- Project " + project.getID() + ":");
			List<TaskView> availableTasks = project.getAvailableTasks();
			for(TaskView task : availableTasks) {
				System.out.println("  * Task " + task.getID() + " is available");
			}
		}

		// SELECT PROJECT AND TASK
		int projectID = -1;
		int taskID = -1;
		while(true) {
			try {
				// Ask for user input
				System.out.println("Select a project and the task you wish to modify (Format: ProjectID TaskID, type quit to exit)");
				String input = inputReader.readLine();

				// User quits
				if(input.toLowerCase().equals("quit"))
					return quit();

				// Select task
				projectID = Integer.parseInt(input.split(" ")[0]);
				taskID = Integer.parseInt(input.split(" ")[1]);

				ProjectView project = projects.get(projectID);
				if(isValidAvailableTask(project, taskID)) {
					TaskView task = project.getTasks().get(taskID);
				} else {
					throw new IllegalArgumentException();
				}
				
				// UPDATE TASK

				boolean success = false;
				while(!success) {
					// Show update form and ask user for input
					System.out.println("Please enter the new status, start time and end time. Everything should go on a seperate line (type quit at any time to exit)");
					System.out.println("Task Status? (Finished or Failed)");
					String status = inputReader.readLine();

					// User quits
					if(status.toLowerCase().equals("quit"))
						return quit();	

					System.out.println("Start Time? (Format: Y M D H M)");
					String startTime = inputReader.readLine();

					// User quits
					if(startTime.toLowerCase().equals("quit"))
						return quit();

					String[] startBits = startTime.split(" ");
					LocalDateTime start = LocalDateTime.of(Integer.parseInt(startBits[0]), Integer.parseInt(startBits[1]), Integer.parseInt(startBits[2]), Integer.parseInt(startBits[3]), Integer.parseInt(startBits[4]));


					System.out.println("End Time? (Format: Y M D H M)");
					String endTime = inputReader.readLine();

					// User quits
					if(endTime.toLowerCase().equals("quit"))
						return quit();

					String[] endBits = endTime.split(" ");
					LocalDateTime end = LocalDateTime.of(Integer.parseInt(endBits[0]), Integer.parseInt(endBits[1]), Integer.parseInt(endBits[2]), Integer.parseInt(endBits[3]), Integer.parseInt(endBits[4]));

					// System updates details
					if(status.toLowerCase().equals("finished"))
						success = facade.setTaskFinished(project, task, start, end);
					if(status.toLowerCase().equals("failed"))
						success = facade.setTaskFailed(project, task, start, end);
					// Invalid details
					if(!success)
						System.out.println("Invalid input");
				}
				return "Task updated!";
			} catch(Exception e) {
				System.out.println("Invalid input");
			}
		}
	}

	private String quit() {
		return "Tasks remain unaltered";
	}
	
	private boolean isValidAvailableTask(ProjectView project, int taskID) {
		List<TaskView> availableTasks = project.getAvailableTasks();
		
		for(int i = 0 ; i < availableTasks.size() ; i++) {
			if(availableTasks.get(i).getID() == taskID)
				return true;
		}
		return false;
	}

}
