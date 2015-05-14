package company;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import userInterface.IFacade;
import company.caretaker.TaskManCaretaker;
import company.taskMan.ProjectView;
import company.taskMan.TaskMan;
import company.taskMan.project.Project;
import company.taskMan.project.TaskView;
import company.taskMan.resource.Reservation;
import company.taskMan.resource.ResourceView;
import company.taskMan.resource.user.User;
import company.taskMan.resource.user.UserPermission;

//TODO list of prototypes in branchMa
public class BranchManager implements IFacade {
	private List<TaskMan> taskMen;
	private TaskMan currentTaskMan;
	private Delegator delegator;
	private LocalDateTime currentTime;
	private User currentUser;
	private final TaskManCaretaker caretaker;

	
	public BranchManager(LocalDateTime time) {
		taskMen = new ArrayList<>();
		delegator = new Delegator();
		caretaker = new TaskManCaretaker(this);
		currentUser = null;
	}
	
//	public void declareBranch(LocalDateTime branchTime, String geographicLocation) {
//		taskMen.add(new Branch(branchTime, geographicLocation));
//	}
//	
//	public void initializeFromMemento() {
//		this.taskMan = new TaskMan();
//	}
	
	/**
	 * declares a TaskMan
	 * 
	 * @param	location
	 * 			The location of the branch.
	 */
	private void declareTaskMan(String location){
		TaskMan newTaskMan = new TaskMan(location);
		taskMen.add(newTaskMan);
		currentTaskMan = newTaskMan;
	}
	
	/**
	 * Gets the current time
	 * 
	 * @return the current time
	 */
	@Override
	public LocalDateTime getCurrentTime() {
		return currentTime;
	}
	
	@Override
	public boolean createProject(String name, String description, LocalDateTime creationTime, LocalDateTime dueTime) {
		if(!currentUserHasPermission(UserPermission.CREATE_PROJECT)) {
			return false;
		}
		return currentTaskMan.createProject(name, description, creationTime, dueTime);
	}
	
	@Override
	public boolean createProject(String name, String description,
			LocalDateTime dueTime) {
		if(!currentUserHasPermission(UserPermission.CREATE_PROJECT)) {
			return false;
		}
		return currentTaskMan.createProject(name, description, this.currentTime, dueTime);
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
		
		if(!currentUserHasPermission(UserPermission.CREATE_TASK)) {
			return false;
		}
		
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
		
		if(!currentUserHasPermission(UserPermission.CREATE_TASK)) {
			return false;
		}
		
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

	/**
	 * Advances the current time to the given time.
	 * 
	 * @param time
	 *            The time to which the system should advance
	 * @return True if the advance time was successful. False if the time
	 *         parameter is earlier than the current time.
	 */
	@Override
	public boolean advanceTimeTo(LocalDateTime time) {
		if(!currentUserHasPermission(UserPermission.ADVANCE_TIME));
		if (time == null)
			return false;
		if (time.isAfter(currentTime)) {
			currentTime = time;
			return true;
		} else
			return false;

	}

	@Override
	public boolean setTaskFinished(ProjectView project, TaskView task,
			LocalDateTime endTime) {
		if(!currentUserHasPermission(UserPermission.UPDATE_TASK)) {
			return false;
		}
		if (endTime == null) { // || startTime == null) {
			return false;
		}
		if (endTime.isAfter(currentTime)) {
			return false;
		}
		return currentTaskMan.setTaskFinished(project, task, endTime);
	}

	@Override
	public boolean setTaskFailed(ProjectView project, TaskView task,
			LocalDateTime endTime) {
		if(!currentUserHasPermission(UserPermission.UPDATE_TASK)) {
			return false;
		}
		if (endTime == null) { // || startTime == null) {
			return false;
		}
		if (endTime.isAfter(currentTime)) {
			return false;
		}
		return currentTaskMan.setTaskFailed(project, task, endTime);
	}
	
	@Override
	public boolean setTaskExecuting(ProjectView project, TaskView task, LocalDateTime startTime){
		if(!currentUserHasPermission(UserPermission.UPDATE_TASK)) {
			return false;
		}
		if (startTime == null) {
			return false;
		}
		if (startTime.isAfter(currentTime)) {
			return false;
		}
		return currentTaskMan.setTaskExecuting(project,task,startTime);
	}

	@Override
	public List<ProjectView> getProjects() {
		return currentTaskMan.getProjects();
	}

	@Override
	public boolean storeInMemento() {
		if (!currentUserHasPermission(UserPermission.SIMULATE)) {
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
		return new ResourceView(currentUser);
	}

	@Override
	public List<ResourceView> getPossibleUsers() {
		return currentTaskMan.getPossibleUsernames();
	}

	@Override
	public boolean changeToUser(ResourceView user) {
		User newUser = currentTaskMan.getUser(user);
		if (user == null) {
			return false;
		} else {
			currentUser = newUser;
			return true;
		}
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
		if(!currentUserHasPermission(UserPermission.PLAN_TASK)) {
			return false;
		}
		return currentTaskMan.planTask(project, task, plannedStartTime, concRes, devs);
	}
	
	@Override
	public boolean planRawTask(ProjectView project, TaskView task,
			LocalDateTime plannedStartTime, List<ResourceView> concRes, List<ResourceView> devs) {
		if(!currentUserHasPermission(UserPermission.PLAN_TASK)) {
			return false;
		}
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
	
	/**
	 * Check whether or not this user has a specific credential
	 * 
	 * @param permission
	 *            | The permission to check
	 * @return True if the user has the credential, false otherwise
	 */
	@Override
	public boolean currentUserHasPermission(UserPermission permission) {
		return currentUser.getPermissions().contains(permission);
	}

	public List<Reservation> getAllReservations() {
		return currentTaskMan.getAllReservations();
	}

	@Override
	public boolean isLoggedIn() {
		return currentUser == null && currentTaskMan == null;
	}

	@Override
	public List<BranchView> getBranches() {
		Builder<BranchView> views = ImmutableList.builder();
		for (TaskMan taskMan : taskMen)
			views.add(new BranchView(taskMan));
		return views.build();
	}

	@Override
	public void selectBranch(BranchView branch) {
		TaskMan taskMan = unwrapBranchView(branch);
		currentTaskMan = taskMan;
	}

	@Override
	public void initializeBranch(String geographicLocation) {
		this.declareTaskMan(geographicLocation);
	}

	@Override
	public void logout() {
		currentUser = null;
		currentTaskMan = null;
	}

	@Override
	public void delegateTask(ProjectView project, TaskView task,
			BranchView newBranch) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Unwraps the BranchView to a TaskMan object
	 * 
	 * @param 	view
	 * 			The view to unwrap
	 * @return	The TaskMan if if belongs to this BranchManager
	 * @throws 	NullPointerException
	 * 			View can't be null
	 * @throws 	IllegalArgumentException
	 * 			TaskMan must belong to this manager
	 */
	private TaskMan unwrapBranchView(BranchView view) 
			throws NullPointerException, IllegalArgumentException{
		if(view == null) {
			throw new NullPointerException("There was no branch to unwrap!");
		}
		TaskMan taskMan = view.unwrap();
		if(!taskMen.contains(taskMan))
			throw new IllegalArgumentException("Branch does not belong to this BranchManager!");
		return taskMan;

	}

	@Override
	public BranchView getResponsibleBranch(TaskView task) {
		// TODO Auto-generated method stub
		return null;
	}
	
}