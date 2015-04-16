package userInterface.requests;

import java.io.BufferedReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import taskMan.view.ProjectView;
import taskMan.view.ResourceView;
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

		for(int i = 0 ; i < projects.size() ; i++) {
			System.out.println("(" + i + ") Project " + projects.get(i).getName() + ":");
			List<TaskView> unplannedTasks = projects.get(i).getUnplannedTasks();
			for(int j = 0 ; j < unplannedTasks.size() ; j++) {
				System.out.println("  {" + j + "} " + unplannedTasks.get(j).getDescription());
			}
		}

		// SELECT PROJECT AND TASK TO PLAN
		int projectID = -1;
		int taskID = -1;
		while(true) {
			try {
				// Ask for user input
				System.out.println("Select a project and the task you wish to plan (Format: (Project location in list) (Task location in list), type quit to exit)");
				String input = inputReader.readLine();

				// User quits
				if(input.toLowerCase().equals("quit"))
					return quit();

				// Select task
				projectID = Integer.parseInt(input.split(" ")[0]);
				taskID = Integer.parseInt(input.split(" ")[1]);

				ProjectView project = projects.get(projectID);
				TaskView task = project.getUnplannedTasks().get(taskID);

				// PLAN TASK
				PlanningScheme planning = planTask(project, task);
				if(planning == null) { 
					return quit();
				}

				// Assign developers to task, assign planning to task, reserve selected resource(types)
				boolean success = facade.planTask(project, task, planning.getPlanningStartTime());

				if(!success) { System.out.println("Failed to plan task, try again"); continue; }
				else {
					for(ResourceView resourceToReserve : planning.getResourcesToReserve()) {
						facade.reserveResource(resourceToReserve, project, task);
					}
				}

			} catch(Exception e) {
				System.out.println("Invalid input");
			}
		}
	}

	private String quit() {
		return "Tasks remain unplanned";
	}

	public PlanningScheme planTask(ProjectView project, TaskView task) {

		PlanningScheme planning = null;
		String input;
		
		// Ask user for time slot
		while(true) {
			try {

				// Show each required resource
				Map<ResourceView, Integer> requiredResources = task.getRequiredResources();
				
				if(!requiredResources.isEmpty()) {
					System.out.println("The following resources are required: ");
					for(ResourceView requiredResource : requiredResources.keySet()) {
						int amountLeft = requiredResources.get(requiredResource);
						System.out.println(requiredResource.getName() + ", amount: " + amountLeft);
						while(amountLeft > 0) {
							System.out.println(amountLeft + " left to reserve, reserve specific resource or will any suffice? (type quit to exit)");
							List<ResourceView> concreteResources = facade.getConcreteResourcesForPrototype(requiredResource);
							for(int i = 0 ; i < concreteResources.size() ; i++) {
								System.out.println("(" + i + ") " + concreteResources.get(i).getName());
							}
							System.out.println("(any)");

							// Ask user for input
							input = inputReader.readLine();

							// User quits
							if(input.toLowerCase().equals("quit")) { return null; }
							if(input.toLowerCase().equals("any")) {
								planning.addSeveralToReservationList(requiredResource, amountLeft);
								break;
							} else {
//								// if resource is already reserved initiate resolve conflict request
//								Map<ProjectView, List<TaskView>> conflictingTasks = facade.reservationConflict(concreteResources.get(Integer.parseInt(input)), project, task, planning.getPlanningStartTime());
//								if(!conflictingTasks.isEmpty()) {
//									ResolvePlanningConflictRequest resolveConflictRequest = new ResolvePlanningConflictRequest(facade, inputReader, conflictingTasks);
//									System.out.println(resolveConflictRequest.execute());
//									// Conflict dictates that this planning should start over
//									if(resolveConflictRequest.shouldMovePlanningTask()) {
//										return planTask(project, task);
//									}
//								}
								planning.addToReservationList(concreteResources.get(Integer.parseInt(input)));
								amountLeft--;
							}
						}
					}
				}

				// show list of possible starting times
				List<LocalDateTime> possibleStartingTimes = task.getPossibleStartingTimes(planning.getResourcesToReserve(),3);
				//TODO wat als er GEEN zijn? NOOIT.
				// (3 first possible (enough res and devs available))
				for(int i = 0 ; i < possibleStartingTimes.size() ; i++) {
					System.out.println("Possible task starting time " + i + ": " + possibleStartingTimes.get(i).toString());
				}

				System.out.println("Select a time slot (type quit to exit)");
				input = inputReader.readLine();		

				// User quits
				if(input.toLowerCase().equals("quit"))	{ return null; }

				// Confirm time slot
				LocalDateTime timeSlotStart = possibleStartingTimes.get(Integer.parseInt(input));
				System.out.println("Time slot selected from " + timeSlotStart.toString() + " until " + timeSlotStart.plusMinutes(task.getEstimatedDuration()).toString());
				planning = new PlanningScheme(timeSlotStart);
				
				// Show list of developers
				List<ResourceView> devs = facade.getDeveloperList();
				System.out.println("Possible developers to assign:");
				for(int i = 0; i < devs.size() ; i++) {
					System.out.println("(" + i + ") " + devs.get(i));
				}
				System.out.println("Choose the developers you wish to assign (their numbers on one line, seperated by spaces) (type quit to exit)");
				input = inputReader.readLine();
				// User quits
				if(input.toLowerCase().equals("quit")) { return null; }
				// User selects developers
				String[] devIDs = input.split(" ");
				List<ResourceView> devNames = new ArrayList<>();
				for(String devID : devIDs) {
					devNames.add(devs.get(Integer.parseInt(devID)));
				}

				// If selected dev conflicts with another task planning init resolve conflict request
				Map<ProjectView, List<TaskView>> conflictingTasks = facade.findConflictingDeveloperPlannings(project, task, devNames, timeSlotStart);

				if(!conflictingTasks.isEmpty()) {
					ResolvePlanningConflictRequest resolveConflictRequest = new ResolvePlanningConflictRequest(facade, inputReader, conflictingTasks);
					System.out.println(resolveConflictRequest.execute());
					// Conflict dictates that this planning should start over
					if(resolveConflictRequest.shouldMovePlanningTask()) {
						return planTask(project, task);
					}
				}
				return planning;
			} catch(Exception e) {
				System.out.println("Invalid input");
			}
		}
	}

}

class PlanningScheme {

	private LocalDateTime timeSlotStartTime;
	private List<ResourceView> resourcesToReserve;

	public PlanningScheme(LocalDateTime timeSlotStartTime) {
		this.timeSlotStartTime = timeSlotStartTime;
	}


	public LocalDateTime getPlanningStartTime() {
		return timeSlotStartTime;
	}

	public boolean addToReservationList(ResourceView resource) {
		return resourcesToReserve.add(resource);
	}

	public void addSeveralToReservationList(ResourceView requiredResource, int amount) {
		for(int i = 0 ; i < amount ; i++) {
			resourcesToReserve.add(requiredResource);
		}
	}

	public List<ResourceView> getResourcesToReserve() {
		return resourcesToReserve;
	}
}





