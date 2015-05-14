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
	private LocalDateTime currentTime;
	private ResourceView currentUser;
	private final TaskManCaretaker caretaker;

	
	public BranchManager(LocalDateTime time) {
		taskMen = new ArrayList<>();
		delegator = new Delegator();
		caretaker = new TaskManCaretaker(this);
	}
	
//	public void declareBranch(LocalDateTime branchTime, String geographicLocation) {
//		taskMen.add(new Branch(branchTime, geographicLocation));
//	}
//	
//	public void initializeFromMemento() {
//		this.taskMan = new TaskMan();
//	}
	
	public void declareTaskMan(){
		TaskMan newTaskMan = new TaskMan();
		taskMen.add(newTaskMan);
		currentTaskMan = newTaskMan;
	}
	
	@Override
	public LocalDateTime getCurrentTime() { 
		return this.currentTime; 
	}
	
	@Override
	public boolean createProject(String name, String description, LocalDateTime creationTime, LocalDateTime dueTime) {
		return currentTaskMan.createProject(name, description, creationTime, dueTime);
	}
	
	@Override
	public boolean createProject(String name, String description,
			LocalDateTime dueTime) {
		return currentTaskMan.createProject(name, description, dueTime);
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
		
		return currentTaskMan.createTask(project, 
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
		
		return currentTaskMan.createTask(project, 
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
		return currentTaskMan.advanceTimeTo(time);
		
	}

	@Override
	public boolean setTaskFinished(ProjectView project, TaskView task,
			LocalDateTime endTime) {
		return currentTaskMan.setTaskFinished(project, task, endTime);
	}

	@Override
	public boolean setTaskFailed(ProjectView project, TaskView task,
			LocalDateTime endTime) {
		return currentTaskMan.setTaskFailed(project, task, endTime);
	}
	
	@Override
	public boolean setTaskExecuting(ProjectView project, TaskView task, LocalDateTime startTime){
		return currentTaskMan.setTaskExecuting(project,task,startTime);
	}

	@Override
	public List<ProjectView> getProjects() {
		return currentTaskMan.getProjects();
	}

	@Override
	public boolean storeInMemento() {
		if (!currentTaskMan.currentUserHasPermission(UserPermission.SIMULATE)) {
			return false;
		}
		caretaker.storeInMemento();
		return true;
	}

	@Override
	public boolean revertFromMemento() {
		return  caretaker.revertFromMemento();
	}

	@Override
	public boolean discardMemento() {
		return caretaker.discardMemento();
	}

	@Override
	public ResourceView getCurrentUser() {
		return this.currentUser;
	}

	@Override
	public List<ResourceView> getPossibleUsers() {
		return currentTaskMan.getPossibleUsernames();
	}

	@Override
	public boolean changeToUser(ResourceView user) {
		return currentTaskMan.changeToUser(user);
	}

	@Override
	public boolean createResourcePrototype(String name,
			Optional<LocalTime> availabilityStart,
			Optional<LocalTime> availabilityEnd) {
		return currentTaskMan.createResourcePrototype(name,availabilityStart,availabilityEnd);
	}

	@Override
	public boolean declareConcreteResource(String name, ResourceView fromPrototype) {
		return currentTaskMan.declareConcreteResource(name,fromPrototype);
	}

	@Override
	public boolean createDeveloper(String name) {
		return currentTaskMan.createDeveloper(name);
	}
	
	@Override
	public boolean reserveResource(ResourceView resource, ProjectView project, TaskView task, LocalDateTime startTime, LocalDateTime endTime) {
		return currentTaskMan.reserveResource(resource, project, task, startTime, endTime);
	}

	@Override
	public List<ResourceView> getDeveloperList() {
		return currentTaskMan.getDeveloperList();
	}

	@Override
	public HashMap<ProjectView, List<TaskView>> findConflictingPlannings(TaskView task) {
		return currentTaskMan.findConflictingPlannings(task);
	}

	@Override
	public List<ResourceView> getResourcePrototypes() {
		return currentTaskMan.getResourcePrototypes();
	}

	public ResourceView getPrototypeOf(ResourceView resource) {
		return currentTaskMan.getPrototypeOf(resource);
	}

	@Override
	public List<ResourceView> getConcreteResourcesForPrototype(
			ResourceView resourcePrototype) {
		return currentTaskMan.getConcreteResourcesForPrototype(resourcePrototype);
	}

	@Override
	public boolean planTask(ProjectView project, TaskView task,
			LocalDateTime plannedStartTime, List<ResourceView> concRes, List<ResourceView> devs) {
		return currentTaskMan.planTask(project, task, plannedStartTime, concRes, devs);
	}
	
	@Override
	public boolean planRawTask(ProjectView project, TaskView task,
			LocalDateTime plannedStartTime, List<ResourceView> concRes, List<ResourceView> devs) {
		return currentTaskMan.planRawTask(project, task, plannedStartTime, concRes, devs);
	}

	@Override
	public boolean addRequirementsToResource(List<ResourceView> resourcesToAdd,
			ResourceView prototype) {
		return currentTaskMan.addRequirementsToResource(resourcesToAdd, prototype);
	}

	@Override
	public boolean addConflictsToResource(List<ResourceView> resourcesToAdd,
			ResourceView prototype) {
		return currentTaskMan.addConflictsToResource(resourcesToAdd, prototype);
	}

	@Override
	public boolean currentUserHasPermission(UserPermission permission) {
		return currentTaskMan.currentUserHasPermission(permission);
	}
	
	@Override
	public List<TaskView> getUpdatableTasksForUser(ProjectView project){
		return currentTaskMan.getUpdatableTasksForUser(project);
	}

	public List<Reservation> getAllReservations() {
		return currentTaskMan.getAllReservations();
	}

	@Override
	public boolean isLoggedIn() {
		// TODO Auto-generated method stub
		return false;
	}
}