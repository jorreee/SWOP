package userInterface.requests;

import java.io.BufferedReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import company.taskMan.ProjectView;
import company.taskMan.project.TaskView;
import company.taskMan.resource.ResourceView;
import userInterface.IFacade;

public class PlanTaskRequest extends Request {

	public PlanTaskRequest(IFacade facade, BufferedReader inputReader) {
		super(facade, inputReader);
	}

	@Override
	public String execute() {

		// SELECT PROJECT AND TASK TO PLAN
		int projectID = -1;
		int taskID = -1;
		while(true) {
			try {
				// Show a list of projects and their unplanned tasks
				List<ProjectView> projects = facade.getProjects();

				for(int i = 0 ; i < projects.size() ; i++) {
					System.out.println("(" + i + ") Project " + projects.get(i).getName() + ":");
					List<TaskView> unplannedTasks = projects.get(i).getUnplannedTasks();
					for(int j = 0 ; j < unplannedTasks.size() ; j++) {
						System.out.println("  {" + j + "} " + unplannedTasks.get(j).getDescription());
					}
				}				

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
				boolean shouldMove = false;
				boolean success = false;
				do {
					PlanningScheme planning = planTask(project, task);
					if(planning == null) { 
						return quit();
					}

					// Assign developers to task, assign planning to task, reserve selected resource(types)
					// This can be done safely or not (raw), be sure to check for conflicts when raw
					if(planning.isSafePlanning()) {
						success = facade.planTask(project, task, 
								planning.getPlanningStartTime(), 
								planning.getResourcesToReserve(),
								planning.getDevelopers());
					} else {
						success = facade.planRawTask(project, task, 
								planning.getPlanningStartTime(), 
								planning.getResourcesToReserve(),
								planning.getDevelopers());
					}
					// If selected planning conflicts with another task planning init resolve conflict request
					Map<ProjectView, List<TaskView>> conflictingTasks = facade.findConflictingPlannings(task);


					if(!conflictingTasks.isEmpty()) {
						ResolvePlanningConflictRequest resolveConflictRequest = new ResolvePlanningConflictRequest(facade, inputReader, conflictingTasks);
						System.out.println(resolveConflictRequest.execute());
						// Conflict dictates that this planning should start over
						if(resolveConflictRequest.shouldMovePlanningTask()) {
							shouldMove = true;
						}
					} else {
						shouldMove = false;
					}
				}
				// If task planning needs to be moved, restart process.
				while (shouldMove == true);

				if(!success) { System.out.println("Failed to plan task, try again"); continue; }

			} catch(Exception e) {
				e.printStackTrace();
				System.out.println("Invalid input");
			}
		}
	}

	private String quit() {
		return "Tasks remain unplanned";
	}

	public PlanningScheme planTask(ProjectView project, TaskView task) {

		PlanningScheme planning = new PlanningScheme();
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
								amountLeft = 0;
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
								ResourceView resourceReserved = concreteResources.get(Integer.parseInt(input));
								if(planning.getResourcesToReserve().contains(resourceReserved)) {
									System.out.println("Resource already chosen!");
								} else {
									planning.addToReservationList(resourceReserved);
									amountLeft--;
								}
							}
						}
					}
				}

				// Show list of developers
				List<ResourceView> devs = facade.getDeveloperList();
				System.out.println("Possible developers to assign:");
				for(int i = 0; i < devs.size() ; i++) {
					System.out.println("(" + i + ") " + devs.get(i).getName());
				}
				System.out.println("Choose the developers you wish to assign (their numbers on one line, seperated by spaces) (type quit to exit)");
				input = inputReader.readLine();
				// User quits
				if(input.toLowerCase().equals("quit")) { return null; }
				// User selects developers
				String[] devIDs = input.split(" ");
				Set<String> devSet = new HashSet<>(Arrays.asList(devIDs));
				List<ResourceView> devNames = new ArrayList<>();
				for(String devID : devSet) {
					devNames.add(devs.get(Integer.parseInt(devID)));
				}
				for(ResourceView dev : devNames) {
					planning.addDeveloper(dev);
				}


				// show list of possible starting times
				List<ResourceView> toReserve = new ArrayList<ResourceView>(planning.getResourcesToReserve());
				toReserve.addAll(planning.getDevelopers());
				List<LocalDateTime> possibleStartingTimes = task.getPossibleStartingTimes(toReserve,facade.getCurrentTime(),3);
				// (3 first possible (enough res and devs available))
				for(int i = 0 ; i < possibleStartingTimes.size() ; i++) {
					System.out.println("Possible task starting time " + i + ": " + possibleStartingTimes.get(i).toString());
				}

				System.out.println("Do you want to choose your own time slot? (Yes: Y, No: N) WARNING: Not conflict free!");
				input = inputReader.readLine();
				LocalDateTime timeSlotStart = null;
				switch(input.toUpperCase()){
				case "Y" : 
					System.out.println("Enter a starting time. Format: Y M D H M");

					if(input.toLowerCase().equals("quit"))	{ return null; }

					String startTime = inputReader.readLine();
					String[] startBits = startTime.split(" ");
					timeSlotStart = LocalDateTime.of(Integer.parseInt(startBits[0]), Integer.parseInt(startBits[1]), Integer.parseInt(startBits[2]), Integer.parseInt(startBits[3]), Integer.parseInt(startBits[4]));
					planning.setSafePlanning(false);
					break;
				case "N" :
					System.out.println("Select a time slot (type quit to exit)");
					input = inputReader.readLine();		

					// User quits
					if(input.toLowerCase().equals("quit"))	{ return null; }

					// Confirm time slot
					timeSlotStart = possibleStartingTimes.get(Integer.parseInt(input));
					break;

				}


				System.out.println("Time slot selected from " + timeSlotStart.format(dateTimeFormatter) + " until " + timeSlotStart.plusMinutes(task.getEstimatedDuration()).format(dateTimeFormatter));
				planning.setPlanBeginTime(timeSlotStart);


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
	private List<ResourceView> developers;
	private boolean safe;

	public PlanningScheme() {
		resourcesToReserve = new ArrayList<>();
		developers = new ArrayList<>();
		safe = true;
	}

	public void setPlanBeginTime(LocalDateTime timeSlotStartTime) {
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

	public void addDeveloper(ResourceView d) {
		developers.add(d);
	}

	public List<ResourceView> getDevelopers() {
		return developers;
	}

	public void setSafePlanning(boolean safe) {
		this.safe = safe;
	}

	public boolean isSafePlanning() {
		return safe;
	}
}
