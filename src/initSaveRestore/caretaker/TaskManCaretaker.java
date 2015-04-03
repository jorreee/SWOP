package initSaveRestore.caretaker;

import initSaveRestore.initialization.ConcreteResourceCreationData;
import initSaveRestore.initialization.DeveloperCreationData;
import initSaveRestore.initialization.PlanningCreationData;
import initSaveRestore.initialization.ProjectCreationData;
import initSaveRestore.initialization.ReservationCreationData;
import initSaveRestore.initialization.ResourcePrototypeCreationData;
import initSaveRestore.initialization.TaskCreationData;
import initSaveRestore.initialization.TaskManInitFileChecker;
import initSaveRestore.initialization.TaskStatus;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import taskMan.Facade;
import taskMan.view.ResourceView;

import com.google.common.collect.ImmutableList;

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
		StringBuilder taskman = new StringBuilder();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

		// SystemTime
		taskman.append("systemTime: \"" + facade.getCurrentTime().format(dateTimeFormatter) +"\"");
		
		// DailyAvailability
		taskman.append("\ndailyAvailability :");
		ImmutableList<DailyAvailability> dailyAvailabilities = facade.getPossibleDailyAvailabilities();
		if(!dailyAvailability.isEmpty()) {
			for(DailyAvailability dailyAvailability : dailyAvailabilities) {
				taskman.append("\n  - startTime : \"" + dailyAvailability.getStartTime().format(timeFormatter) + "\"\n"
						+ "    endTime   : \"" + dailyAvailability.getEndTime().format(timeFormatter) + "\"");
			}
		}
		
		// resourceType
		taskman.append("\nresourceTypes:");
		ImmutableList<ResourceView> resprots = facade.getResourcePrototypes();
		for(ResourceView resprot : resprots) {
			taskman.append("\n  - name              : \"" + resprot.getName() + "\"");
			taskman.append("\n    requires          : [");
			Iterator<ResourceView> requiredProts = facade.getResourceRequirements(resprot).listIterator();
			while(requiredProts.hasNext()) {
				taskman.append(resprots.indexOf(requiredProts.next()));
				if(requiredProts.hasNext()) { taskman.append(","); }
			}
			taskman.append("]");
			taskman.append("\n    conflictsWith     : [");
			Iterator<ResourceView> conflictingProts = facade.getResourceConflicts(resprot).listIterator();
			while(conflictingProts.hasNext()) {
				taskman.append(resprots.indexOf(conflictingProts.next()));
				if(conflictingProts.hasNext()) { taskman.append(","); }
			}
			taskman.append("]");
		}

		return taskman.toString();
	}

	public boolean revertFromMemento() {
		TaskManInitFileChecker fileChecker = new TaskManInitFileChecker(
				new StringReader(mementos.pop().getMementoAsString()));
		fileChecker.checkFile();

		LocalDateTime systemTime = fileChecker.getSystemTime();
		List<ProjectCreationData> projectData = fileChecker.getProjectDataList();
		List<TaskCreationData> taskData = fileChecker.getTaskDataList();
		List<ResourcePrototypeCreationData> resourcePrototypes = fileChecker.getResourcePrototypeDataList();
		List<ConcreteResourceCreationData> concreteResources = fileChecker.getConcreteResourceDataList();
		List<DeveloperCreationData> developers = fileChecker.getDeveloperDataList();
		List<ReservationCreationData> reservations = fileChecker.getReservationDataList();

		// Initialize system through a facade
		// Set system time
		facade.initializeFromMemento(systemTime);
		// Init daily availability
		facade.declareDailyAvailability(fileChecker.getDailyAvailabilityTime()[0],
				fileChecker.getDailyAvailabilityTime()[1]);
		// Init resource prototypes
		for(ResourcePrototypeCreationData rprot : resourcePrototypes) {
			facade.createResourcePrototype(rprot.getName(), rprot.getRequirements(), rprot.getConflicts(), rprot.getAvailabilityIndex());
		}
		// Init concrete resources
		for(ConcreteResourceCreationData cres : concreteResources) {
			facade.createRawResource(cres.getName(), cres.getTypeIndex());
		}
		// Init developers
		for(DeveloperCreationData dev : developers) {
			facade.createDeveloper(dev.getName());
		}
		
		// Init current user
		facade.changeToUser(fileChecker.getCurrentUser());
		
		// Init projects
		for(ProjectCreationData pcd : projectData) {
			facade.createProject(pcd.getName(), pcd.getDescription(), pcd.getCreationTime(), pcd.getDueTime());
		}
		// Init tasks (planned and unplanned)
		for(TaskCreationData tcd : taskData) {
			TaskStatus status = tcd.getStatus();
			String statusString = null;
			if(status != null)
				statusString = status.name();
			PlanningCreationData planning = tcd.getPlanningData();
			if(planning != null)
				facade.createRawPlannedTask(tcd.getProject(), tcd.getDescription(),
						tcd.getEstimatedDuration(),
						tcd.getAcceptableDeviation(), tcd.getPrerequisiteTasks(),
						tcd.getAlternativeFor(), tcd.getRequiredResources(),
						statusString, tcd.getStartTime(), tcd.getEndTime(),
						planning.getDueTime(), planning.getDevelopers(),
						planning.getResources());
			else
				facade.createRawTask(tcd.getProject(), tcd.getDescription(),
						tcd.getEstimatedDuration(),
						tcd.getAcceptableDeviation(), tcd.getPrerequisiteTasks(),
						tcd.getAlternativeFor(), tcd.getRequiredResources(),
						statusString, tcd.getStartTime(), tcd.getEndTime());
		}
		// Init reservations
		for(ReservationCreationData rcd : reservations) {
			facade.createRawReservation(rcd.getResource(), taskData.get(rcd.getTask()).getProject(), rcd.getTask(), rcd.getStartTime(), rcd.getEndTime());
		}
		// End initialization
		return true;		
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
