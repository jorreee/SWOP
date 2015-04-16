package initSaveRestore.caretaker;

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

public class TaskManCaretaker {

	private final Stack<TaskManMemento> mementos;
	private final Facade facade;

	public TaskManCaretaker(Facade facade) {
		this.mementos = new Stack<>();
		this.facade = facade;
	}

	public boolean storeInMemento() {
		String taskman = buildMemento();
		mementos.push(new TaskManMemento(taskman));
		return true;
	}

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
		// TODO
		
		// tasks
		tman.append("\ntasksk:");
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
				// requiredResources // TODO
				// planning
				// status
				// startTime
				// endTime
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

	public boolean discardMemento() {
		try {
			mementos.pop();
			return true;
		} catch(EmptyStackException e) {
			return false;
		}
	}
}
