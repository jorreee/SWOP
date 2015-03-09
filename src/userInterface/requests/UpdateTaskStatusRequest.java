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
		HashMap<Integer,List<Integer>> availableTasks = facade.getAvailableTasks();

		Set<Integer> projectSet = availableTasks.keySet();

		for(Integer projectID : projectSet) {
			System.out.println("- Project " + projectID + ":");
			for(Integer taskID : availableTasks.get(projectID)) {
				System.out.println("  * Task " + taskID + " is available");
			}
		}

		// SELECT PROJECT AND TASK
		boolean validLink = false;
		int projectID = 0;
		int taskID = 0;
		while(!validLink) {
			try {
				// Ask for user input
				System.out.println("Select a project and the task you wish to modify (type quit to exit");
				String input = inputReader.readLine();

				// User quits
				if(input.toLowerCase().equals("quit"))
					return quit();

				// Select task
				projectID = Integer.parseInt(input.split(" ")[0]);
				taskID = Integer.parseInt(input.split(" ")[1]);
				if(!availableTasks.containsKey(projectID) || !availableTasks.get(projectID).contains(taskID)) {
					System.out.println("Invalid Project and Task pair");
				} else {
					validLink = true;
				}
			} catch(Exception e) {
				System.out.println("Invalid Project and Task pair");
			}
		}
		
		// UPDATE TASK
		while(true) {
			try {
				boolean success = false;
				while(!success) {
					// Show update form and ask user for input
					System.out.println("Please enter the new status, start time and end time. Everything should go on a seperate line. If you wish not to suppply a certain element, simply leave said line blank (type quit at any time to exit)");
					System.out.println("Task Status? (Finished or Failed)");
					String status = inputReader.readLine();

					// User quits
					if(status.toLowerCase().equals("quit"))
						return quit();	

					System.out.println("Start Time?");
					String startTime = inputReader.readLine();

					// User quits
					if(startTime.toLowerCase().equals("quit"))
						return quit();

					System.out.println("End Time?");
					String endTime = inputReader.readLine();

					// User quits
					if(endTime.toLowerCase().equals("quit"))
						return quit();

					// System updates details
					success = facade.updateTaskDetails(projectID, taskID, LocalDateTime.parse(startTime), LocalDateTime.parse(endTime), status);

					// Invalid details
					if(!success)
						System.out.println("Invalid input");
				}
				System.out.println("Task updated!");
			} catch(Exception e) {
				System.out.println("Invalid input");
			}
		}
	}

	private String quit() {
		return "Tasks remain unaltered";
	}

}
