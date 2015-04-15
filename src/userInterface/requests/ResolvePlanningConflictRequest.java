package userInterface.requests;

import java.io.BufferedReader;
import java.util.List;
import java.util.Map;

import taskMan.view.ProjectView;
import taskMan.view.ResourceView;
import taskMan.view.TaskView;
import userInterface.IFacade;

public class ResolvePlanningConflictRequest extends Request {

	private Map<ProjectView, List<TaskView>> conflictingTasks;
	private boolean movePlanningTask = false;

	public ResolvePlanningConflictRequest(IFacade facade,
			BufferedReader inputReader, Map<ProjectView, List<TaskView>> conflictingTasks) {
		super(facade, inputReader);
		this.conflictingTasks = conflictingTasks;
	}

	@Override
	public String execute() {
		System.out.println("A conflict with other tasks has been detected!");

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
				facade.flushFutureReservations(project, conflictingTask);
				
				// Register newly planned reservations and assign newly planned planning to the conflicting task
				boolean success = facade.planTask(project, conflictingTask, newPlanning.getPlanningStartTime());

				if(!success) { System.out.println("Failed to plan task, try again"); continue; }
				else {
					for(ResourceView resourceToReserve : newPlanning.getResourcesToReserve()) {
						facade.reserveResource(resourceToReserve, project, conflictingTask);
					}
				}
			}
		}
		return "Conflict resolved";
	}

	public boolean shouldMovePlanningTask() {
		return movePlanningTask;
	}

}
