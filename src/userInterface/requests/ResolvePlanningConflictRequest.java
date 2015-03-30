package userInterface.requests;

import java.io.BufferedReader;
import java.util.List;

import taskMan.view.TaskView;
import userInterface.IFacade;

public class ResolvePlanningConflictRequest extends Request {

	List<TaskView> conflictingTasks;
	boolean movePlanningTask = false;

	public ResolvePlanningConflictRequest(IFacade facade,
			BufferedReader inputReader, List<TaskView> conflictingTasks) {
		super(facade, inputReader);
		this.conflictingTasks = conflictingTasks;
	}

	@Override
	public String execute() {

		// Show conflicting tasks
		for(TaskView conflictingTask : conflictingTasks) {
			System.out.println("Planning conflicts with task: " + conflictingTask.getID());
		}
		System.out.println("Move task currently being scheduled? (Y/N)");

		// Ask user for input
		boolean done = false;
		while(!done) {
			try {
				String input = inputReader.readLine();

				switch(input.toUpperCase()) {
				case "Y" : // User chooses to move task currently being planned
					movePlanningTask = true;
					return "Conflict resolved";
				case "N" : // User chooses to move conflicting task
					done = true;
					break;
				default : throw new IllegalArgumentException();
				}

			} catch(Exception e) {
				System.out.println("Invalid input, try again");
			}
		}
		// Resolve conflict for each conflicting task
		PlanTaskRequest planTaskRequest = new PlanTaskRequest(facade, inputReader);
		for(TaskView conflictingTask : conflictingTasks) {
			// Plan specific conflicting task
			PlanningScheme newPlanning = planTaskRequest.planTask(conflictingTask);
			// Remove planned reservations for conflicting task
			
			// Register newly planned reservations and assign newly planned planning to the conflicting task
		}
		return "Conflict resolved";
	}

	public boolean shouldMovePlanningTask() {
		return movePlanningTask;
	}

}
