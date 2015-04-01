package userInterface.requests;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.List;

import taskMan.view.ProjectView;
import taskMan.view.TaskView;
import userInterface.IFacade;

public class ResolvePlanningConflictRequest extends Request {

	private HashMap<ProjectView, List<TaskView>> conflictingTasks;
	private boolean movePlanningTask = false;

	public ResolvePlanningConflictRequest(IFacade facade,
			BufferedReader inputReader, HashMap<ProjectView, List<TaskView>> conflictingTasks) {
		super(facade, inputReader);
		this.conflictingTasks = conflictingTasks;
	}

	@Override
	public String execute() {

		// Show conflicting tasks
		for(ProjectView project : conflictingTasks.keySet()) {
			for(TaskView conflictingTask : conflictingTasks.get(project)) {
				System.out.println("Planning conflicts with project: " + project.getID() + ", task: " + conflictingTask.getID());
			}
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
		for(ProjectView project : conflictingTasks.keySet()) {
			for(TaskView conflictingTask : conflictingTasks.get(project)) {
				// Plan specific conflicting task
				PlanningScheme newPlanning = planTaskRequest.planTask(project, conflictingTask);
				// Remove planned reservations for conflicting task
				
				// Register newly planned reservations and assign newly planned planning to the conflicting task
			}
		}
		return "Conflict resolved";
	}

	public boolean shouldMovePlanningTask() {
		return movePlanningTask;
	}

}
