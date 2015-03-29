package userInterface.requests;

import java.io.BufferedReader;
import java.time.LocalDateTime;
import java.util.List;

import taskMan.view.ProjectView;
import taskMan.view.TaskView;
import userInterface.IFacade;

public class PlanTaskRequest extends Request {

	public PlanTaskRequest(IFacade facade, BufferedReader inputReader) {
		super(facade, inputReader);
	}

	@Override
	public String execute() {

		// Show a list of projects and their unplanned tasks
		List<ProjectView> projects = facade.getProjects();

		for(ProjectView project : projects) {
			System.out.println("- Project " + project.getID() + ":");
			List<TaskView> availableTasks = project.getUnplannedTasks();
			for(TaskView task : availableTasks) {
				System.out.println("  * Task " + task.getID() + " is unplanned");
			}
		}

		// SELECT PROJECT AND TASK TO PLAN
		int projectID = -1;
		int taskID = -1;
		while(true) {
			try {
				// Ask for user input
				System.out.println("Select a project and the task you wish to plan (Format: ProjectID TaskID, type quit to exit)");
				String input = inputReader.readLine();

				// User quits
				if(input.toLowerCase().equals("quit"))
					return quit();

				// Select task
				projectID = Integer.parseInt(input.split(" ")[0]);
				taskID = Integer.parseInt(input.split(" ")[1]);

				ProjectView project = projects.get(projectID);
				TaskView task;

				if(isValidUnplannedTask(project, taskID)) {
					task = project.getTasks().get(taskID);
				} else {
					throw new IllegalArgumentException();
				}
				
				// PLAN TASK
				PlanningScheme planning = planTask(task);
				if(planning == null) { return quit(); }
				
				// Show list of developers
				
				// User quits
				if(input.toLowerCase().equals("quit")) { return quit(); }
				// User selects developers
					// If selected dev conflicts with another task planning init resolve conflict request
				
				// Assign developers to task, assign planning to task, reserve selected resource(types)
				
			} catch(Exception e) {
				System.out.println("Invalid input");
			}
		}
	}

	private String quit() {
		return "Tasks remain unplanned";
	}

	private boolean isValidUnplannedTask(ProjectView project, int taskID) {
		List<TaskView> unplannedTasks = project.getUnplannedTasks();

		for(int i = 0 ; i < unplannedTasks.size() ; i++) {
			if(unplannedTasks.get(i).getID() == taskID)
				return true;
		}
		return false;
	}
	
	// TODO dit afwerken
	// TODO Return type? Een soort planning container met voorstellen voor het plan en de reservaties?
	public PlanningScheme planTask(TaskView task) {
		
		// show list of possible starting times
		List<LocalDateTime> possibleStartingTimes = facade.getPossibleTaskStartingTimes(task, 3);
		// (3 first possible (enough res and devs available))
		for(int i = 0 ; i < possibleStartingTimes.size() ; i++) {
			System.out.println("(" + i + ") Possible task starting time: " + possibleStartingTimes.get(i).toString());
		}
		// Ask user for time slot
		while(true) {
			try {
				System.out.println("Select a time slot (type quit to exit)");
				String input = inputReader.readLine();		
				
				// User quits
				if(input.toLowerCase().equals("quit"))	{ return null; }
				
				// Confirm time slot
				LocalDateTime timeSlotStart = possibleStartingTimes.get(Integer.parseInt(input));
				System.out.println("Time slot selected from " + timeSlotStart.toString() + " unti " + timeSlotStart.plusMinutes(task.getEstimatedTaskDuration()).toString()); // TODO TaskView.getEstimatedTaskDuration should be int
				
				// Show each required resource
				
				// Ask user fot input
				input = inputReader.readLine();
				
				// User quits
				if(input.toLowerCase().equals("quit")) { return null; }
				
					// User allows system to choose the resource (Abstraction)
				
					// User wants a specific resource
						// Propose every concrete res from the abstract res type to reserve
						
						// User quits
						if(input.toLowerCase().equals("quit")) { return null; }
					// if resource is already reserved initiate resolve conflict request
				
			} catch(Exception e) {
				System.out.println("Invalid input");
			}
		}
	}

}

class PlanningScheme {
	
	LocalDateTime timeSlotStartTime;
	
	public PlanningScheme(LocalDateTime timeSlotStartTime) {
		
	}
}





