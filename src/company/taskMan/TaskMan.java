package company.taskMan;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import company.BranchView;
import company.taskMan.project.DelegationProject;
import company.taskMan.project.Project;
import company.taskMan.project.TaskView;
import company.taskMan.resource.Reservation;
import company.taskMan.resource.ResourceManager;
import company.taskMan.resource.ResourcePrototype;
import company.taskMan.resource.ResourceView;
import company.taskMan.resource.user.User;

import exceptions.ResourceUnavailableException;
import exceptions.UnexpectedViewContentException;

//TODO Still not done
//TODO remove outcommented methods and other stuff 
/**
 * The Main System that keeps track of the list of projects and the current
 * Time. The TaskMan is also responsible of maintaining a resource manager
 * (which in turn is responsible for managing the resources of the system).
 * 
 * @author Tim Van den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 * 
 */
public class TaskMan {

	private ArrayList<Project> projectList;
	private Project delegationProject;
	private ResourceManager resMan;
	private Delegator delegator;
	private final String geographicLocation;

	/**
	 * Creates a TaskMan system instance with a given time.
	 * 
	 * @param time
	 *            The current TaskMan time.
	 */
	public TaskMan(String location, List<ResourcePrototype> prototypes) 
			throws IllegalArgumentException {
		if(location == null)
			throw new IllegalArgumentException("The given location is null");
		projectList = new ArrayList<>();
		delegationProject = new DelegationProject();
		resMan = new ResourceManager(prototypes);
		delegator = new Delegator();
		geographicLocation = location;
	}
	
	/**
	 * Returns the geographic location of the TaskMan
	 * 
	 * @return	The location of the TaskMan
	 */
	public String getGeographicLocation(){
		return this.geographicLocation;
	}

	/**
	 * Unwraps the ProjectView object and returns the Project that it contained
	 * IF the unwrapped project belongs to this taskman:
	 * projectList.contains(project)
	 * 
	 * @param view
	 *            | the ProjectView to unwrap
	 * @return | the unwrapped Project if it belonged to this TaskMan | NULL
	 *         otherwise
	 * @throws IllegalArgumentException, UnexpectedViewContentException
	 */
	private Project unwrapProjectView(ProjectView view) 
			throws IllegalArgumentException, UnexpectedViewContentException {
		if(view == null) {
			throw new IllegalArgumentException("There was no project to unwrap!");
		}
		Project project = view.unwrap();
		if(project != delegationProject && !projectList.contains(project)) {
			throw new UnexpectedViewContentException("Project does not belong to this TaskManager!");
		}
		return project;
	}

//	/**
//	 * Advances the current time to the given time.
//	 * 
//	 * @param time
//	 *            The time to which the system should advance
//	 * @return True if the advance time was successful. False if the time
//	 *         parameter is earlier than the current time.
//	 */
//	public boolean advanceTimeTo(LocalDateTime time) {
//		if(!currentUserHasPermission(UserPermission.ADVANCE_TIME));
//		if (time == null)
//			return false;
//		if (time.isAfter(currentTime)) {
//			currentTime = time;
//			return true;
//		} else
//			return false;
//
//	}

//	/**
//	 * Gets the current time
//	 * 
//	 * @return the current time
//	 */
//	public LocalDateTime getCurrentTime() {
//		return currentTime;
//	}

	/**
	 * Creates a new Project with a creation time
	 * 
	 * @param name
	 *            The name of the project
	 * @param description
	 *            The description of the project
	 * @param creationTime
	 *            The creation time of the project
	 * @param dueTime
	 *            The due time of the project
	 * @throws IllegalArgumentException
	 */
	public void createProject(String name, String description,
			LocalDateTime creationTime, LocalDateTime dueTime) 
				throws IllegalArgumentException {
		Project project = new Project(name, description, creationTime, dueTime);
		projectList.add(project);
	}

//	/**
//	 * Creates a new Project with the current time as the creation time.
//	 * 
//	 * @param name
//	 *            The name of the project
//	 * @param description
//	 *            The description of the project
//	 * @param dueTime
//	 *            The due time of the project
//	 * @return true if the project creation was successful false if the creation
//	 *         was unsuccessful
//	 */
//	public boolean createProject(String name, String description,
//			LocalDateTime dueTime) {
////		if(!currentUserHasPermission(UserPermission.CREATE_PROJECT)) {
////			return false;
////		}
//		return createProject(name, description, getCurrentTime(), dueTime);
//	}

	/**
	 * Creates a new Task without a set status.
	 * 
	 * @param project
	 *            | The project this task belongs to, this project must be
	 *            notified whenever the state of this task changes
	 * @param description
	 *            | The description of the given Task.
	 * @param estimatedDuration
	 *            | The estimated duration of the Task.
	 * @param acceptableDeviation
	 *            | The acceptable deviation of the Task.
	 * @param alternativeFor
	 *            | The alternative Task.
	 * @param prerequisiteTasks
	 *            | The prerequisites Tasks for this Task.
	 * @param requiredResources
	 *            | The resource prototypes and their respective quantities that
	 *            are required by for this task
	 * @throws IllegalArgumentException, IllegalStateException, ResourceUnavailableException 
	 */
	public void createTask(ProjectView project, String description,
			int estimatedDuration, int acceptableDeviation,
			List<TaskView> prerequisiteTasks,
			Map<ResourceView, Integer> requiredResources,
			TaskView alternativeFor) 
				throws IllegalArgumentException, IllegalStateException, ResourceUnavailableException {
		
		createTask(project, description, estimatedDuration,
				acceptableDeviation, prerequisiteTasks, alternativeFor,
				requiredResources, null, null, null, null, null);

	}

	/**
	 * Creates a Planned Task as issued by the input file.
	 * 
	 * @param projectView
	 *            | The project where this task belongs to
	 * @param description
	 *            | The description of the Task.
	 * @param estimatedDuration
	 *            | The estimated duration of the Task.
	 * @param acceptableDeviation
	 *            | The acceptable deviation of the Task.
	 * @param prerequisiteTasks
	 *            | The prerequisites of the Task.
	 * @param alternativeFor
	 *            | The alternative for the Task.
	 * @param requiredResources
	 *            | The resources (types) and their respective quantity required
	 *            for this task
	 * @param taskStatus
	 *            | The status of the Task.
	 * @param startTime
	 *            | The startTime of the Task.
	 * @param endTime
	 *            | The endTime of the Task.
	 * @param plannedStartTime
	 *            | The due time of the planning of the Task.
	 * @param plannedDevelopers
	 *            | The planned developers of the Task.
	 * @throws IllegalArgumentException, IllegalStateException, ResourceUnavailableException 
	 */
	public void createTask(ProjectView projectView, String description,
			int estimatedDuration, int acceptableDeviation,
			List<TaskView> prerequisiteTasks, TaskView alternativeFor,
			Map<ResourceView, Integer> requiredResources, String taskStatus,
			LocalDateTime startTime, LocalDateTime endTime,
			LocalDateTime plannedStartTime, List<ResourceView> plannedDevelopers) 
				throws IllegalArgumentException, IllegalStateException, ResourceUnavailableException {
//		if(!currentUserHasPermission(UserPermission.CREATE_TASK)) {
//			return false;
//		}
//		Project project = unwrapProjectView(projectView);
//		if (project == null) {
//			return false;
//		}
		unwrapProjectView(projectView).createTask(description, estimatedDuration,
				acceptableDeviation, resMan, prerequisiteTasks,
				requiredResources, alternativeFor, taskStatus, startTime,
				endTime, plannedStartTime, plannedDevelopers);
	}

	/**
	 * Sets the task with the given task id belonging to the project with the
	 * given project id to finished
	 * 
	 * @param project
	 *            the id of the given project
	 * @param taskID
	 *            the id of the given task
	 * @param endTime
	 *            the end time of the given task
	 * @throws IllegalArgumentException, IllegalStateException
	 */
	public void setTaskFinished(ProjectView project, TaskView taskID,
			LocalDateTime endTime) 
					throws IllegalArgumentException, IllegalStateException {
//		if(!currentUserHasPermission(UserPermission.UPDATE_TASK)) {
//			return false;
//		}
//		if (endTime == null) { // || startTime == null) {
//			return false;
//		}
//		if (endTime.isAfter(currentTime)) {
//			return false;
//		}
//		Project p = unwrapProjectView(project);
//		if (p == null) {
//			return false;
//		}
		unwrapProjectView(project).setTaskFinished(taskID, endTime);
	}

	/**
	 * Sets the task with the given task id belonging to the project with the
	 * given project id to failed
	 * 
	 * @param project
	 *            the id of the given project
	 * @param taskID
	 *            the id of the given task
	 * @param endTime
	 *            the end time of the given task
	 * @throws IllegalArgumentException, IllegalStateException
	 */
	public void setTaskFailed(ProjectView project, TaskView taskID,
			LocalDateTime endTime) 
				throws IllegalArgumentException, IllegalStateException{
//		if(!currentUserHasPermission(UserPermission.UPDATE_TASK)) {
//			return false;
//		}
//		if (endTime == null) { // || startTime == null) {
//			return false;
//		}
//		if (endTime.isAfter(currentTime)) {
//			return false;
//		}
		unwrapProjectView(project).setTaskFailed(taskID, endTime);
	}

	/**
	 * Set the task belonging to a specific project to executing.
	 * 
	 * @param project
	 *            | The project the task belongs to
	 * @param task
	 *            | The task that should be executing
	 * @param startTime
	 *            | The actual begin time of the task
	 * @throws IllegalArgumentException, IllegalStateException
	 */
	public void setTaskExecuting(ProjectView project, TaskView task,
			LocalDateTime startTime) 
				throws IllegalArgumentException, IllegalStateException{
//		if(!currentUserHasPermission(UserPermission.UPDATE_TASK)) {
//			return false;
//		}
//		if (startTime == null) {
//			return false;
//		}
//		if (startTime.isAfter(currentTime)) {
//			return false;
//		}
//		Project p = unwrapProjectView(project);
//		if (p == null) {
//			return false;
//		}
		unwrapProjectView(project).setTaskExecuting(task, startTime);
	}

	/**
	 * Returns a list of ProjectView objects that each contain one of this
	 * taskman's projects
	 * 
	 * @return 
	 * 			| a list of ProjectViews
	 */
	public List<ProjectView> getProjects() {
		Builder<ProjectView> views = ImmutableList.builder();
		for (Project project : projectList)
			views.add(new ProjectView(project));
		return views.build();
	}
	
	/**
	 * Returns a list of ProjectView objects that each contain one of this
	 * taskman's projects and the delegation project
	 * 
	 * @return 
	 * 			| a list of ProjectViews
	 */
	public List<ProjectView> getAllProjects() {
		Builder<ProjectView> views = ImmutableList.builder();
		views.add(new ProjectView(delegationProject));
		for (Project project : projectList)
			views.add(new ProjectView(project));
		return views.build();
	}

//	/**
//	 * Return a view on the current user in the system
//	 * @return The current (logged in) user
//	 */
//	public ResourceView getCurrentUserName() {
//		return new ResourceView(currentUser);
//	}

//	/**
//	 * Change the current logged in user to another one
//	 * 
//	 * @param newUser
//	 *            | The new user to log in
//	 * @return True if the new user was successfully logged in
//	 */
//	public boolean changeToUser(ResourceView newUser) {
//		User user = resMan.getUser(newUser);
//		if (user == null) {
//			return false;
//		} else {
//			currentUser = user;
//			return true;
//		}
//	}

	/**
	 * Retrieve all possible users. This will be a list of every user in the
	 * system.
	 * 
	 * @return an immutable list containing every user in the system
	 */
	public List<ResourceView> getPossibleUsernames() {
		return resMan.getPossibleUsernames();
	}

	/**
	 * Define a new resource type.
	 * 
	 * @param resourceName
	 *            | the name of the new abstract resource
	 * @param availabilityStart
	 *            | the optional start time of the availability period
	 * @param availabilityEnd
	 *            | the optional end time of the availability period
	 * @return True when the prototype has been successfully initiated, false
	 *         otherwise
	 */
	//TODO exception
	public void createResourcePrototype(String resourceName,
			Optional<LocalTime> availabilityStart,
			Optional<LocalTime> availabilityEnd) {
		resMan.createResourcePrototype(resourceName, availabilityStart,
				availabilityEnd);
	}

	/**
	 * Construct a new concrete resource based on a resource prototype
	 * 
	 * @param name
	 *            | The name for the new concrete resource
	 * @param fromPrototype
	 *            | The prototype for which a new resource should be made
	 * @return True if and only if the new concrete resource was made and added
	 *         to its correct pool
	 */
	//TODO exception
	public void declareConcreteResource(String name,
			ResourceView fromPrototype) {
		resMan.declareConcreteResource(name, fromPrototype);
	}

	/**
	 * Define a new developer with a given name in the system.
	 * 
	 * @param name
	 *            | The name of the new developer
	 * @return true if the new developer was added to the system
	 */
	//TODO exception
	public boolean createDeveloper(String name) {
		return resMan.createDeveloper(name);
	}

	/**
	 * Reserve a resource for a task from a given start time to a given end time
	 * 
	 * @param resource
	 *            | The resource to reserve
	 * @param project
	 *            | The project the task belongs to
	 * @param task
	 *            | The reserving task
	 * @param startTime
	 *            | The start time of the new reservation
	 * @param endTime
	 *            | The end time of the new reservation
	 * @return True if the resource was reserved by the given task, false
	 *         otherwise
	 * @throws IllegalStateException, IllegalArgumentException, ResourceUnavailableException 
	 */
	//TODO exception
	public void reserveResource(ResourceView resource, ProjectView project,
			TaskView task, LocalDateTime startTime, LocalDateTime endTime) 
					throws ResourceUnavailableException, 
					IllegalArgumentException, 
					IllegalStateException {
		unwrapProjectView(project).reserve(resource, task, startTime, endTime);
	}

	/**
	 * This method will return an immutable list of every user managed by the
	 * resource manager that has the DEVELOPER credential
	 * 
	 * @return a list of the developers in the system
	 */
	public List<ResourceView> getDeveloperList() {
		return resMan.getDeveloperList();
	}

	/**
	 * Plan a task in the system from a given start time. Reservations will be
	 * made for all the required resources and the developers will be assigned.
	 * 
	 * @param project
	 *            | The project the task belongs to
	 * @param task
	 *            | The task to plan
	 * @param plannedStartTime
	 *            | The planned begin time
	 * @param concRes
	 *            | The resources to plan
	 * @param devs
	 *            | The developers to assign
	 * @throws IllegalArgumentException, IllegalStateException, ResourceUnavailableException 
	 */
	public void planTask(ProjectView project, TaskView task,
			LocalDateTime plannedStartTime, List<ResourceView> concRes, List<ResourceView> devs) 
				throws IllegalArgumentException, IllegalStateException, ResourceUnavailableException{
//		if(!currentUserHasPermission(UserPermission.PLAN_TASK)) {
//			return false;
//		}
//		Project p = unwrapProjectView(project);
//		if(p == null) {
//			return false;
//		}
		unwrapProjectView(project).planTask(task, plannedStartTime, concRes, devs);
	}
	
	/**
	 * Plan a task in the system from a given start time. Reservations will be
	 * made for all the required resources and the developers will be assigned.
	 * It is however possible that this planning will conflict with other
	 * plannings in the system. It is the responsibility of the user when
	 * applying this method to check for conflicts using the
	 * findConflictingPlannings and handle these conflicts appropriately. If you
	 * want to be sure to not get into an inconsistent state, use planTask
	 * instead.
	 * 
	 * @param project
	 *            | The project the task belongs to
	 * @param task
	 *            | The task to plan
	 * @param plannedStartTime
	 *            | The planned begin time
	 * @param concRes
	 *            | The resources to plan
	 * @param devs
	 *            | The developers to assign
	 * @throws IllegalArgumentException, IllegalStateException, ResourceUnavailableException 
	 */
	public void planRawTask(ProjectView project, TaskView task,
			LocalDateTime plannedStartTime, List<ResourceView> concRes, List<ResourceView> devs) 
				throws IllegalArgumentException, IllegalStateException, ResourceUnavailableException {
//		if(!currentUserHasPermission(UserPermission.PLAN_TASK)) {
//			return false;
//		}
//		Project p = unwrapProjectView(project);
//		if(p == null) {
//			return false;
//		}
		unwrapProjectView(project).planRawTask(task, plannedStartTime, concRes, devs);
	}

	/**
	 * This method will locate the resource pool responsible for the given
	 * prototype and return an immutable list of all its concrete resource
	 * instances (wrapped in a resourceView object)
	 * 
	 * @param resourcePrototype
	 *            | The prototype for which the concrete resources are wanted
	 * @return an immutable list of resourceView linked with the concrete
	 *         resources based on the given resource prototype, null if the
	 *         prototype is not associated with any pool
	 */
	public List<ResourceView> getConcreteResourcesForPrototype(
			ResourceView resourcePrototype) {
		return resMan.getConcreteResourcesForPrototype(resourcePrototype);
	}

	/**
	 * This method will find the prototype corresponding to the given
	 * resourceView and return a resourceView of that prototype. If there is no
	 * prototype associated with the given resource, this method will return
	 * null.
	 * 
	 * @param resource
	 *            | The resource to find the prototype of
	 * @return a resourceView of the prototype associated with the given
	 *         resource or null if no corresponding prototype was found
	 */
	public ResourceView getPrototypeOf(ResourceView resource) {
		return resMan.getPrototypeOf(resource);
	}

	/**
	 * This method will return a list of all prototypes present in the resource
	 * pools in this resource manager.
	 * 
	 * @return an immutable list of resource prototypes. For every resource pool
	 *         present in the system, one resource prototype will be added to
	 *         this list
	 */
	public List<ResourceView> getResourcePrototypes() {
		return resMan.getResourcePrototypes();
	}

	/**
	 * Add resource requirements to a prototype
	 * 
	 * @param resourcesToAdd
	 *            | The new requirements to add
	 * @param prototype
	 *            | The prototype that the new requirements should be added to
	 * @throws IllegalArgumentException
	 */
	//TODO exceptions
	public void addRequirementsToResource(List<ResourceView> resourcesToAdd,
			ResourceView prototype) {
		resMan.addRequirementsToResource(resourcesToAdd, prototype);
	}

	/**
	 * Add resource conflicts to a prototype
	 * 
	 * @param resourcesToAdd
	 *            | The new conflicts to add
	 * @param prototype
	 *            | The prototype that the new conflicts should be added to
	 * @throws IllegalArgumentException
	 */
	//TODO exceptions
	public void addConflictsToResource(List<ResourceView> resourcesToAdd,
			ResourceView prototype) {
		resMan.addConflictsToResource(resourcesToAdd, prototype);
	}

//	/**
//	 * Check whether or not this user has a specific credential
//	 * 
//	 * @param permission
//	 *            | The permission to check
//	 * @return True if the user has the credential, false otherwise
//	 */
//	public boolean currentUserHasPermission(UserPermission permission) {
//		return currentUser.getPermissions().contains(permission);
//	}
//
//	/**
//	 * Retrieve a list of all tasks that can be updated by the current user for
//	 * a given project
//	 * 
//	 * @param project
//	 *            | project the current user wants to check
//	 * @return an immutable list of tasks that can be updated by a given user
//	 */
//	public List<TaskView> getUpdatableTasksForUser(ProjectView project, User user) {
//		Project p = unwrapProjectView(project);
//		if(p == null) {
//			Builder<TaskView> bob = ImmutableList.builder();
//			return bob.build();
//		}
//		return p.getUpdatableTasksForUser(
//				new ResourceView(user));
//	}

	/**
	 * Return an immutable list of all the reservations present in the resource
	 * manager
	 * 
	 * @return all reservations
	 */
	public List<Reservation> getAllReservations() {
		return resMan.getAllReservations();
	}
	
	/**
	 * Finds all the conflicting plannings for the given task.
	 * @param 	task
	 * 			The task to check whether there are other tasks with conflicting plannings.
	 * @return	A list of conflicting planned tasks.
	 */
	public HashMap<ProjectView, List<TaskView>> findConflictingPlannings(
			TaskView task) {
		HashMap<ProjectView, List<TaskView>> conflicts = new HashMap<>();
		for (ProjectView proj : getAllProjects()){
			Project project = proj.unwrap();
			List<TaskView> conflictingTasks = project.findConflictingPlannings(task);
			if (!conflictingTasks.isEmpty()){
			conflicts.put(proj, conflictingTasks);
			}
		}
		return conflicts;
	}
	
	/**
	 * Returns the super user of this branch
	 * 
	 * @return	The super user of this branch
	 */
	public User getSuperUser(){
		return resMan.getSuperUser();
	}
	
	/**
	 * Returns the user belonging to the view
	 * 
	 * @param 	user
	 * 			View to unpack
	 * @return	The user belonging to the view
	 */
	public User getUser(ResourceView user){
		return resMan.unWrapUserView(user); //TODO public unwrap
	}

	public Optional<BranchView> getResponsibleBranch(ProjectView project, TaskView task) {
		return project.unwrap().getResponsibleBranch(delegator, task);
	}
}