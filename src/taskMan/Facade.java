package taskMan;

import initSaveRestore.caretaker.TaskManCaretaker;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
			List<TaskView> prerequisiteTasks, Map<ResourceView, Integer> requiredResources,
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
	public boolean createTask(ProjectView project, String description,
			int estimatedDuration, int acceptableDeviation,
			List<TaskView> prerequisiteTasks, TaskView alternativeFor,
			Map<ResourceView, Integer> requiredResources, String taskStatus,
			LocalDateTime startTime, LocalDateTime endTime) {
		return taskMan.createTask(project, 
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
	public boolean createPlannedTask(ProjectView project, String description,
			int estimatedDuration, int acceptableDeviation,
			List<TaskView> prerequisiteTasks, TaskView alternativeFor,
			Map<ResourceView, Integer> requiredResources, String taskStatus,
			LocalDateTime startTime, LocalDateTime endTime,
			LocalDateTime plannedStartTime, List<ResourceView> plannedDevelopers) {
		return taskMan.createPlannedTask(project, 
				description, 
				estimatedDuration, 
				acceptableDeviation, 
				prerequisiteTasks, 
				alternativeFor,
				requiredResources,
				taskStatus,
				startTime,
				endTime,
				plannedStartTime,
				plannedDevelopers);
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
	public boolean createResourcePrototype(String name,
			List<Integer> requirements, 
			List<Integer> conflicts,
			Optional<LocalTime> availabilityStart,
			Optional<LocalTime> availabilityEnd) {
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
	public boolean reserveResource(ResourceView resource, ProjectView project, TaskView task) {
		return taskMan.reserveResource(resource, project, task);
	}
	
	@Override
	public boolean reserveResource(ResourceView resource, ProjectView project, TaskView task, LocalDateTime startTime, LocalDateTime endTime) {
		return taskMan.reserveResource(resource, project, task);
	}

	@Override
	public ImmutableList<LocalDateTime> getPossibleTaskStartingTimes(ProjectView project, TaskView task,
			int amount) {
		return taskMan.getPossibleTaskStartingTimes(project,task,amount);
	}

	@Override
	public ImmutableList<ResourceView> getDeveloperList() {
		return taskMan.getDeveloperList();
	}

	@Override
	public HashMap<ProjectView, List<TaskView>> findConflictingDeveloperPlannings(
			ProjectView project, TaskView task,
			List<ResourceView> developers, LocalDateTime plannedStartTime) {
		return findConflictingDeveloperPlannings(
				project, task,
				developers, plannedStartTime);
	}

	@Override
	public ImmutableList<ResourceView> getResourcePrototypes() {
		return taskMan.getResourcePrototypes();
	}

	public ImmutableList<ResourceView> getAllConcreteResources() {
		return taskMan.getAllConcreteResources();
	}

	public ResourceView getPrototypeOf(ResourceView resource) {
		return taskMan.getPrototypeOf(resource);
	}

	@Override
	public ImmutableList<ResourceView> getConcreteResourcesForPrototype(
			ResourceView resourcePrototype) {
		return taskMan.getConcreteResourcesForPrototype(resourcePrototype);
	}

	@Override
	public boolean planTask(ProjectView project, TaskView task,
			LocalDateTime plannedStartTime) {
		return taskMan.planTask(project, task, plannedStartTime);
	}

	@Override
	public HashMap<ProjectView, List<TaskView>> reservationConflict(
			ResourceView requiredResource, ProjectView project, TaskView task,
			LocalDateTime plannedStartTime) {
		return taskMan.reservationConflict(requiredResource, project, task, plannedStartTime);
	}

	@Override
	public boolean flushFutureReservations(ProjectView project,
			TaskView task) {
		return taskMan.flushFutureReservations(project, task);
	}

}
