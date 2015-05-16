package userInterface.requests;

import java.io.BufferedReader;
import java.util.List;
import java.util.Map;

import company.taskMan.ProjectView;
import company.taskMan.project.TaskView;
import userInterface.IFacade;
import userInterface.TaskManException;

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

		boolean hasExecuting = false;
		// Show conflicting tasks
		for(ProjectView project : conflictingTasks.keySet()) {
			for(TaskView conflictingTask : conflictingTasks.get(project)) {
				if (conflictingTask.isExecuting()){
					hasExecuting = true;
				}
				System.out.println("Planning conflicts with project: " + project.getName() + ", task: \"" + conflictingTask.getDescription() + "\"");
			}
		}
		if (hasExecuting){
			System.out.println("The task conflicts with an executing task. The task currently being scheduled must be moved.");
			movePlanningTask = true;
			return "Conflict resolved";

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
				boolean shouldMove = false;
				do {
					System.out.println("Now re-planning task: " + conflictingTask.getDescription() + ", from project: " + project.getName());
					// Plan specific conflicting task
					PlanningScheme newPlanning = planTaskRequest.planTask(project, conflictingTask);

					// Register newly planned reservations and assign newly planned planning to the conflicting task
					try {
						facade.planRawTask(project, conflictingTask, newPlanning.getPlanningStartTime(),newPlanning.getResourcesToReserve(),newPlanning.getDevelopers());

						// If selected planning conflicts with another task planning init resolve conflict request
						Map<ProjectView, List<TaskView>> newConflictingTasks = facade.findConflictingPlannings(conflictingTask);

						if(!newConflictingTasks.isEmpty()) {
							ResolvePlanningConflictRequest resolveConflictRequest = new ResolvePlanningConflictRequest(facade, inputReader, newConflictingTasks);
							System.out.println(resolveConflictRequest.execute());
							// Conflict dictates that this planning should start over
							if(resolveConflictRequest.shouldMovePlanningTask()) {
								shouldMove = true;
							}
						} else {
							shouldMove = false;
						}
					} catch(TaskManException e) {
						System.out.println(e.getMessage());
					} catch(Exception e) {
						e.printStackTrace();
						System.out.println("Failed to plan task, try again");
					}
				} while(shouldMove);
			}
		}
		return "Conflict resolved";
	}

	public boolean shouldMovePlanningTask() {
		return movePlanningTask;
	}

}
