package taskMan;

import initSaveRestore.caretaker.TaskManCaretaker;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

import taskMan.resource.AvailabilityPeriod;
import taskMan.resource.Resource;
import taskMan.util.IntPair;
import taskMan.view.ProjectView;
import taskMan.view.ResourceView;
import taskMan.view.TaskView;
import userInterface.IFacade;

import com.google.common.collect.ImmutableList;

public class Facade implements IFacade {
	private TaskMan taskMan;
	private final TaskManCaretaker caretaker;
	
	public Facade(LocalDateTime time) {
		this.taskMan = new TaskMan(time);
		caretaker = new TaskManCaretaker(this);
	}
	
	public void initializeFromMemento(LocalDateTime time) {
		this.taskMan = new TaskMan(time);
	}
	
	@Override
	public LocalDateTime getCurrentTime() { 
		return taskMan.getCurrentTime(); 
	}
	
	@Override
	public boolean createProject(String name, String description, LocalDateTime creationTime, LocalDateTime dueTime) {
		return taskMan.createProject(name, description, creationTime, dueTime);
	}
	
	@Override
	public boolean createProject(String name, String description,
			LocalDateTime dueTime) {
		return taskMan.createProject(name, description, dueTime);
	}

	@Override
	public boolean createTask(ProjectView project, String description,
			int estimatedDuration, int acceptableDeviation,
			List<TaskView> prerequisiteTasks, List<Resource> requiredResources,
			TaskView alternativeFor) {
		return taskMan.createTask(project, 
				description, 
				estimatedDuration, 
				acceptableDeviation, 
				prerequisiteTasks, 
				requiredResources,
				alternativeFor);
	}

	@Override
	public boolean createRawTask(int project, 
			String description,
			int estimatedDuration, 
			int acceptableDeviation,
			List<Integer> prerequisiteTasks, 
			int alternativeFor,
			List<IntPair> requiredResources, 
//			List<Resource> requiredResources, 
			String taskStatus, 
			LocalDateTime startTime, 
			LocalDateTime endTime) {
		
		return taskMan.createRawTask(project, 
				description, 
				estimatedDuration, 
				acceptableDeviation, 
				prerequisiteTasks,  
				alternativeFor, 
				requiredResources,
				taskStatus, 
				startTime, 
				endTime);
	}

	@Override
	public boolean advanceTimeTo(LocalDateTime time) {
		return taskMan.advanceTimeTo(time);
		
	}

	@Override
	public boolean setTaskFinished(ProjectView projectID, TaskView taskID,
			LocalDateTime endTime) {
		return taskMan.setTaskFinished(projectID, taskID, endTime);
	}

	@Override
	public boolean setTaskFailed(ProjectView projectID, TaskView taskID,
			LocalDateTime endTime) {
		return taskMan.setTaskFailed(projectID, taskID, endTime);
	}

	@Override
	public ImmutableList<ProjectView> getProjects() {
		return taskMan.getProjects();
	}

	@Override
	public boolean storeInMemento() {
		return caretaker.storeInMemento();
	}

	@Override
	public boolean revertFromMemento() {
		return caretaker.revertFromMemento();
	}

	@Override
	public boolean discardMemento() {
		return caretaker.discardMemento();		
	}

	@Override
	public String getCurrentUsername() {
		return taskMan.getCurrentUserName();
	}

	@Override
	public ImmutableList<ResourceView> getPossibleUsernames() {
		return taskMan.getPossibleUsernames();
	}

	@Override
	public boolean changeToUser(String name) {
		return taskMan.changeToUser(name);
	}

	@Override
	public boolean createRawPlannedTask(int project, String description,
			int estimatedDuration, int acceptableDeviation,
			List<Integer> prerequisiteTasks, int alternativeFor,
			List<IntPair> requiredResources,
			String statusString, LocalDateTime startTime,
			LocalDateTime endTime, LocalDateTime planningDueTime,
			List<Integer> plannedDevelopers, List<IntPair> plannedResources) {
		return taskMan.createRawPlannedTask(project, description, estimatedDuration, acceptableDeviation,
				prerequisiteTasks, alternativeFor, statusString, startTime, endTime, planningDueTime, plannedDevelopers, plannedResources);
	}
//
//	@Override
//	public boolean declareAvailabilityPeriod(LocalTime startTime,LocalTime endTime) {
//		return taskMan.declareAvailabilityPeriod(startTime,endTime);
//	}

	@Override
	public boolean createResourcePrototype(String name,
			List<Integer> requirements, 
			List<Integer> conflicts,
			LocalTime availabilityStart,
			LocalTime availabilityEnd) {
		return taskMan.createResourcePrototype(name,requirements,conflicts,availabilityStart,availabilityEnd);
	}

	@Override
	public boolean declareConcreteResource(String name, int typeIndex) {
		return taskMan.declareConcreteResource(name,typeIndex);
	}

	@Override
	public boolean createDeveloper(String name) {
		return taskMan.createDeveloper(name);
	}

	@Override
	public boolean createRawReservation(int resource, int project, int task,
			LocalDateTime startTime, LocalDateTime endTime) {
		return taskMan.createRawReservation(resource,project,task,startTime,endTime);
	}

	@Override
	public ImmutableList<LocalDateTime> getPossibleTaskStartingTimes(ProjectView project, TaskView task,
			int amount) {
		return taskMan.getPossibleTaskStartingTimes(project,task,amount);
	}

	@Override
	public ImmutableList<ResourceView> getDeveloperList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<ProjectView, List<TaskView>> findConflictingDeveloperPlannings(
			ProjectView projectID, TaskView taskID,
			List<ResourceView> developerNames, LocalDateTime planningStartTime) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<AvailabilityPeriod> getPossibleDailyAvailabilities() {
		return taskMan.getPossibleDailyAvailabilities();		
	}

	@Override
	public ImmutableList<ResourceView> getResourcePrototypes() {
		// TODO Auto-generated method stub
		return null;
	}
//
//	@Override
//	public ImmutableList<ResourceView> getResourceRequirements(
//			ResourceView resource) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	
//
//	@Override
//	public ImmutableList<ResourceView> getResourceConflicts(ResourceView resprot) {
//		return null;
//	}
//
//	@Override
//	public boolean isResourceDailyAvailable(ResourceView resprot) {
//		return false;
//	}

	public AvailabilityPeriod getDailyAvailability(ResourceView resprot) {
		// TODO Auto-generated method stub
		return null;
	}

	public ImmutableList<ResourceView> getAllConcreteResources() {
		// TODO Auto-generated method stub
		return null;
	}

	public ResourceView getPrototypeOf(ResourceView conres) {
		// TODO Auto-generated method stub
		return null;
	}

}
