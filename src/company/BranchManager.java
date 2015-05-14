package company;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import userInterface.IFacade;
import company.caretaker.TaskManCaretaker;
import company.taskMan.ProjectView;
import company.taskMan.TaskMan;
import company.taskMan.project.TaskView;
import company.taskMan.resource.Reservation;
import company.taskMan.resource.ResourceView;
import company.taskMan.resource.user.UserPermission;

public class BranchManager implements IFacade {
	private List<TaskMan> taskMen;
	private TaskMan currentTaskMan;
	private Delegator delegator;
	private final TaskManCaretaker caretaker;

	
	public BranchManager(LocalDateTime time) {
		taskMen = new ArrayList<>();
		delegator = new Delegator();
		caretaker = new TaskManCaretaker(this);
	}
	
	public void declareBranch(LocalDateTime branchTime, String geographicLocation) {
		taskMen.add(new Branch(branchTime, geographicLocation));
	}
	
	public void initializeFromMemento(LocalDateTime time) {
		currentTaskMan.initializeFromMemento(time);
	}
	
	@Override
	public LocalDateTime getCurrentTime() { 
		return currentBranch.getCurrentTime(); 
	}
	
	@Override
	public boolean createProject(String name, String description, LocalDateTime creationTime, LocalDateTime dueTime) {
		return currentBranch.createProject(name, description, creationTime, dueTime);
	}
	
	@Override
	public boolean createProject(String name, String description,
			LocalDateTime dueTime) {
		return currentBranch.createProject(name, description, dueTime);
	}

	@Override
	public boolean createTask(
			ProjectView project, 
			String description,
			int estimatedDuration, 
			int acceptableDeviation,
			List<TaskView> prerequisiteTasks, 
			Map<ResourceView, Integer> requiredResources,
			TaskView alternativeFor) {
		
		return currentBranch.createTask(project, 
				description, 
				estimatedDuration, 
				acceptableDeviation, 
				prerequisiteTasks, 
				requiredResources,
				alternativeFor);
	}

	@Override
	public boolean createTask(
			ProjectView project,
			String description,
			int estimatedDuration, 
			int acceptableDeviation,
			List<TaskView> prerequisiteTasks, 
			TaskView alternativeFor,
			Map<ResourceView, Integer> requiredResources, 
			String taskStatus,
			LocalDateTime startTime, 
			LocalDateTime endTime,
			LocalDateTime plannedStartTime, 
			List<ResourceView> plannedDevelopers) {
		
		return currentBranch.createTask(project, 
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
		return currentBranch.advanceTimeTo(time);
		
	}

	@Override
	public boolean setTaskFinished(ProjectView project, TaskView task,
			LocalDateTime endTime) {
		return currentBranch.setTaskFinished(project, task, endTime);
	}

	@Override
	public boolean setTaskFailed(ProjectView project, TaskView task,
			LocalDateTime endTime) {
		return currentBranch.setTaskFailed(project, task, endTime);
	}
	
	@Override
	public boolean setTaskExecuting(ProjectView project, TaskView task, LocalDateTime startTime){
		return currentBranch.setTaskExecuting(project,task,startTime);
	}

	@Override
	public List<ProjectView> getProjects() {
		return currentBranch.getProjects();
	}

	@Override
	public boolean storeInMemento() {
		return currentBranch.storeInMemento();
	}

	@Override
	public boolean revertFromMemento() {
		return currentBranch.revertFromMemento();
	}

	@Override
	public boolean discardMemento() {
		return currentBranch.discardMemento();		
	}

	@Override
	public ResourceView getCurrentUser() {
		return currentBranch.getCurrentUser();
	}

	@Override
	public List<ResourceView> getPossibleUsers() {
		return currentBranch.getPossibleUsers();
	}

	@Override
	public boolean changeToUser(ResourceView user) {
		return currentBranch.changeToUser(user);
	}

	@Override
	public boolean createResourcePrototype(String name,
			Optional<LocalTime> availabilityStart,
			Optional<LocalTime> availabilityEnd) {
		return currentBranch.createResourcePrototype(name,availabilityStart,availabilityEnd);
	}

	@Override
	public boolean declareConcreteResource(String name, ResourceView fromPrototype) {
		return currentBranch.declareConcreteResource(name,fromPrototype);
	}

	@Override
	public boolean createDeveloper(String name) {
		return currentBranch.createDeveloper(name);
	}
	
	@Override
	public boolean reserveResource(ResourceView resource, ProjectView project, TaskView task, LocalDateTime startTime, LocalDateTime endTime) {
		return currentBranch.reserveResource(resource, project, task, startTime, endTime);
	}

	@Override
	public List<ResourceView> getDeveloperList() {
		return currentBranch.getDeveloperList();
	}

	@Override
	public HashMap<ProjectView, List<TaskView>> findConflictingPlannings(TaskView task) {
		return currentBranch.findConflictingPlannings(task);
	}

	@Override
	public List<ResourceView> getResourcePrototypes() {
		return currentBranch.getResourcePrototypes();
	}

	public ResourceView getPrototypeOf(ResourceView resource) {
		return currentBranch.getPrototypeOf(resource);
	}

	@Override
	public List<ResourceView> getConcreteResourcesForPrototype(
			ResourceView resourcePrototype) {
		return currentBranch.getConcreteResourcesForPrototype(resourcePrototype);
	}

	@Override
	public boolean planTask(ProjectView project, TaskView task,
			LocalDateTime plannedStartTime, List<ResourceView> concRes, List<ResourceView> devs) {
		return currentBranch.planTask(project, task, plannedStartTime, concRes, devs);
	}
	
	@Override
	public boolean planRawTask(ProjectView project, TaskView task,
			LocalDateTime plannedStartTime, List<ResourceView> concRes, List<ResourceView> devs) {
		return currentBranch.planRawTask(project, task, plannedStartTime, concRes, devs);
	}

	@Override
	public boolean addRequirementsToResource(List<ResourceView> resourcesToAdd,
			ResourceView prototype) {
		return currentBranch.addRequirementsToResource(resourcesToAdd, prototype);
	}

	@Override
	public boolean addConflictsToResource(List<ResourceView> resourcesToAdd,
			ResourceView prototype) {
		return currentBranch.addConflictsToResource(resourcesToAdd, prototype);
	}

	@Override
	public boolean currentUserHasPermission(UserPermission permission) {
		return currentBranch.currentUserHasPermission(permission);
	}
	
	@Override
	public List<TaskView> getUpdatableTasksForUser(ProjectView project){
		return currentBranch.getUpdatableTasksForUser(project);
	}

	public List<Reservation> getAllReservations() {
		return currentBranch.getAllReservations();
	}

	@Override
	public boolean isLoggedIn() {
		// TODO Auto-generated method stub
		return false;
	}
}