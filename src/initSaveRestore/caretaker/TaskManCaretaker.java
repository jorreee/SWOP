package initSaveRestore.caretaker;

import initSaveRestore.initialization.PlanningCreationData;
import initSaveRestore.initialization.TaskManInitFileChecker;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import taskMan.Facade;
import taskMan.resource.AvailabilityPeriod;
import taskMan.resource.Reservation;
import taskMan.view.ProjectView;
import taskMan.view.ResourceView;
import taskMan.view.TaskView;
import userInterface.Main;

/**
 * This class represents the system caretaker. It is responsible for creating
 * system snapshots in order to save and restore a system image.
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class TaskManCaretaker {

	private final Stack<TaskManMemento> mementos;
	private final Facade facade;

	/**
	 * Constructs a caretaker linked to a specific facade
	 * 
	 * @param facade
	 *            | the facade with which the caretaker will talk (its taskman
	 *            can be saved and restored)
	 */
	public TaskManCaretaker(Facade facade) {
		this.mementos = new Stack<>();
		this.facade = facade;
	}

	/**
	 * Store in memento will push a memento of the current system on a stack for
	 * safekeeping. Every system state should be storeable.
	 */
	public void storeInMemento() {
		String taskman = buildMemento();
		mementos.push(new TaskManMemento(taskman));
	}
	
	/**
	 * The Build Memento method will ask the system for specific details about
	 * its current state. This happens through the facade. The method will
	 * return a string in the format of a TMAN file. These TMAN files can be
	 * considered snapshots of a system and are also used for initialization at
	 * startup.
	 * 
	 * @return The TMAN string based upon the current state of the system
	 */
	private String buildMemento() {
		StringBuilder tman = new StringBuilder();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
		
		List<ProjectView> existingProjects = facade.getProjects();
		List<ResourceView> existingPrototypes = facade.getResourcePrototypes();
		List<ResourceView> existingDevelopers = facade.getDeveloperList();
		
		List<TaskView> existingTasks = new ArrayList<>();
		for(ProjectView project : existingProjects) {
			existingTasks.addAll(project.getTasks());
		}
		
		List<PlanningCreationData> existingPlannings = new ArrayList<>();
		for(TaskView task : existingTasks) {
			if(task.isPlanned()) {
				List<Integer> plannedDevelopers = new ArrayList<>();
				for(ResourceView dev : task.getPlannedDevelopers()) {
					plannedDevelopers.add(existingDevelopers.indexOf(dev));
				}
				existingPlannings.add(new PlanningCreationData(task.getPlannedBeginTime(), plannedDevelopers));
			}
		}
		
		List<Reservation> existingReservations = facade.getAllReservations();
		
		// SystemTime
		tman.append("systemTime: \"" + facade.getCurrentTime().format(dateTimeFormatter) +"\"");
		
		// DailyAvailability
		// Construct AvailabilityPeriod List
		Set<AvailabilityPeriod> availabilityPeriodSet = new HashSet<>();
		for(ResourceView prototype : existingPrototypes) {
			if(prototype.isDailyAvailable()) {
				availabilityPeriodSet.add(new AvailabilityPeriod(prototype.getDailyAvailabilityStartTime(), prototype.getDailyAvailabilityEndTime()));
			}
		}
		List<AvailabilityPeriod> existingAvailabilityPeriods = new ArrayList<>(availabilityPeriodSet);
		
		tman.append("\ndailyAvailability :");
		if(!existingAvailabilityPeriods.isEmpty()) {
			for(AvailabilityPeriod dailyAvailability : existingAvailabilityPeriods) {
				tman.append("\n  - startTime : \"" + dailyAvailability.getStartTime().format(timeFormatter) + "\"\n"
						+ "    endTime   : \"" + dailyAvailability.getEndTime().format(timeFormatter) + "\"");
			}
		}
		
		// resourceType
		tman.append("\nresourceTypes:");
		for(ResourceView resprot : existingPrototypes) {
			tman.append("\n  - name              : \"" + resprot.getName() + "\""); // name
			tman.append("\n    requires          : [");
			Iterator<ResourceView> requiredProts = resprot.getRequiredResources().listIterator();
			while(requiredProts.hasNext()) {
				tman.append(existingPrototypes.indexOf(requiredProts.next())); // requires
				if(requiredProts.hasNext()) { tman.append(","); }
			}
			tman.append("]");
			tman.append("\n    conflictsWith     : [");
			Iterator<ResourceView> conflictingProts = resprot.getConflictingResources().listIterator();
			while(conflictingProts.hasNext()) {
				tman.append(existingPrototypes.indexOf(conflictingProts.next())); // conflicts
				if(conflictingProts.hasNext()) { tman.append(","); }
			}
			tman.append("]");
			tman.append("\n    dailyAvailability :");
			if(resprot.isDailyAvailable()) {
				tman.append(" " + existingAvailabilityPeriods.indexOf(
						new AvailabilityPeriod(resprot.getDailyAvailabilityStartTime(), resprot.getDailyAvailabilityEndTime())));
			}
		}
		
		// resources
		Map<ResourceView, Integer> existingConcreteResources = new HashMap<>();
		for(int i = 0 ; i < existingPrototypes.size() ; i++) {
			ResourceView prototype = existingPrototypes.get(i);
			
			List<ResourceView> concreteResources = facade.getConcreteResourcesForPrototype(prototype);
			for(ResourceView concreteResource : concreteResources) {
				existingConcreteResources.put(concreteResource, i);
			}
		}
		
		tman.append("\nresources:");
		List<ResourceView> concreteResources = new ArrayList<>(existingConcreteResources.keySet());
		for(ResourceView concreteResource : concreteResources) {
			tman.append("\n  - name: \"" + concreteResource.getName() + "\""); // resourceName
			tman.append("\n    type: " + existingConcreteResources.get(concreteResource)); // resourceType
		}

		// developers
		tman.append("\ndevelopers:");
		for(ResourceView dev : existingDevelopers) {
			tman.append("\n  - name : \"" + dev.getName() + "\""); // Name
		}
		
		// currentUser
		tman.append("\ncurrentUser:");
		tman.append("\n  - name: \"" + facade.getCurrentUsername() + "\""); // Current logged in person (admin or dev)
		
		// projects
		tman.append("\nprojects:");
		for(ProjectView project : existingProjects) {
			tman.append("\n  - name         : \"" + project.getName() + "\"" // name
					+ "\n    description  : \"" + project.getDescription() + "\"" // description
					+ "\n    creationTime : \"" + project.getCreationTime().format(dateTimeFormatter) + "\"" // creationTime
					+ "\n    dueTime      : \"" + project.getDueTime().format(dateTimeFormatter) + "\"");// dueTime
		}
		
		// plannings
		tman.append("\nplannings:");
		for(PlanningCreationData pcd : existingPlannings) {
			String plannedDevelopers = new String();
			Iterator<Integer> devs = pcd.getDevelopers().iterator();
			while(devs.hasNext()) {
				plannedDevelopers += devs.next();
				if(devs.hasNext()) { plannedDevelopers += ","; };
			}
			tman.append("\n  - plannedStartTime : " + pcd.getPlannedStartTime() // Planning start time
					+ "\n    developers       : [" + plannedDevelopers + "]"); // Planned developers
		}
		
		// tasks
		tman.append("\ntasks:");
		for(ProjectView project : existingProjects) {
			List<TaskView> tasks = project.getTasks();
			for(TaskView task : tasks) {
				tman.append("\n  - project            : " + existingProjects.indexOf(project) // project
						+ "\n    description        : \"" + task.getDescription() // description
						+ "\n    estimatedDuration  : " + task.getEstimatedDuration() // estimatedDuration
						+ "\n    acceptableDeviation: " + task.getAcceptableDeviation()); // acceptableDeviation
				TaskView alternative = task.getAlternativeTo();
				String altIndex = new String();
				if(alternative != null) {
					altIndex = String.valueOf(tasks.indexOf(alternative));
				}
				tman.append("\n    alternativeFor     : " + altIndex); // alternativeFor
				Iterator<TaskView> prereqs = task.getPrerequisites().iterator();
				String prereqsIndices = "[";
				while(prereqs.hasNext()) {
					TaskView prereq = prereqs.next();
					prereqsIndices += existingTasks.indexOf(prereq);
					if(prereqs.hasNext()) { prereqsIndices += ","; };
				}
				tman.append("\n    prerequisiteTasks  : " + prereqsIndices + "]"); // prerequisiteTasks
				Map<ResourceView, Integer> requiredResources = task.getRequiredResources();
				StringBuilder requiredResourcesAsStringBuilder = new StringBuilder();
				Iterator<ResourceView> requiredResourcesIterator = requiredResources.keySet().iterator();
				while(requiredResourcesIterator.hasNext()) {
					ResourceView requiredResource = requiredResourcesIterator.next();
					requiredResourcesAsStringBuilder.append("{type: " + existingPrototypes.indexOf(requiredResource)
							+ ",  quantity: " + requiredResources.get(requiredResource) + "}");
					if(requiredResourcesIterator.hasNext())	{
						requiredResourcesAsStringBuilder.append(", ");
					}
				}
				String requiredResourcesAsString = requiredResourcesAsStringBuilder.toString();
				tman.append("\n    requiredResources  : [" + requiredResourcesAsString + "]"); // requiredResources
				String planningIndex = new String();
				if(task.isPlanned()) {
					List<Integer> plannedDevelopers = new ArrayList<>();
					for(ResourceView dev : task.getPlannedDevelopers()) {
						plannedDevelopers.add(existingDevelopers.indexOf(dev));
					}
					planningIndex = String.valueOf(existingPlannings.indexOf(new PlanningCreationData(task.getPlannedBeginTime(), plannedDevelopers)));
				}
				tman.append("\n    planning           : " + planningIndex); // planning
				String taskStatus = task.getStatusAsString().toLowerCase();
				if(taskStatus.equalsIgnoreCase("unavailable") || taskStatus.equalsIgnoreCase("available")) {
					taskStatus = new String();
				}
				tman.append("\n    status             : " + taskStatus); // status
				if(!taskStatus.isEmpty()) {
					tman.append("\n    startTime          : \"" + task.getStartTime().format(dateTimeFormatter) + "\""); // startTime
				}
				if(!taskStatus.isEmpty() || !taskStatus.equalsIgnoreCase("executing")) {
					tman.append("\n    endTime            : \"" + task.getEndTime().format(dateTimeFormatter) + "\""); // endTime
				}
			}
		}
		
		// reservations
		tman.append("\nreservations:");
		for(Reservation reservation : existingReservations) {
			tman.append("\n  - resource:   " + concreteResources.indexOf(reservation.getReservedResource())
					+ "\n    task:       " + existingTasks.indexOf(new TaskView(reservation.getReservingTask()))
					+ "\n    startTime:  " + reservation.getStartTime().format(dateTimeFormatter)
					+ "\n    endTime:    " + reservation.getEndTime().format(dateTimeFormatter));
		}
			
		return tman.toString();
	}

	/**
	 * This method will ask the facade to recreate its current TaskMan. This
	 * TaskMan will be initialized with values stored in the memento on the top
	 * of the stack. In the case of a simulation, the last simulation started
	 * will be reverted first.
	 * 
	 * @return True if the system was initialized without errors, false
	 *         otherwise
	 */
	public boolean revertFromMemento() {
		TaskManInitFileChecker fileChecker = new TaskManInitFileChecker(
				new StringReader(mementos.pop().getMementoAsString()));
		fileChecker.checkFile();

		LocalDateTime systemTime = fileChecker.getSystemTime();
		
		// Initialize system through a facade
		// Set system time
		facade.initializeFromMemento(systemTime);
		
		boolean success = Main.initialize(facade, fileChecker);
		// End initialization
		return success;		
	}

	/**
	 * The top memento of the memento stack will be removed
	 * 
	 * @return True if the memento was removed without a problem, false
	 *         otherwise
	 */
	public boolean discardMemento() {
		try {
			mementos.pop();
			return true;
		} catch(EmptyStackException e) {
			return false;
		}
	}
}
