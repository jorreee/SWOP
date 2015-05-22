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
import company.taskMan.task.DelegatingTask;
import company.taskMan.task.DelegatingTaskProxy;
import company.taskMan.task.OriginalTaskProxy;
import company.taskMan.task.Task;

import exceptions.ResourceUnavailableException;
import exceptions.UnexpectedViewContentException;

/**
 * The Main System that keeps track of the list of projects and the current
 * Time. The Branch is also responsible of maintaining a resource manager (which
 * in turn is responsible for managing the resources of the system). A Branch is
 * located in a specific geographical location. For inter-branch communication,
 * a specialized branch representative is appointed.
 * 
 * @author Tim Van den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 * 
 */
public class Branch {

	private ArrayList<Project> projectList;
	private Project delegationProject;
	private ResourceManager resMan;
	private BranchRepresentative branchRep;
	private final String geographicLocation;

	/**
	 * Creates a Branch system instance with a given time. and the agreed upon
	 * system resource types
	 * 
	 * @param location
	 *            | the geographical location of the branch
	 * @param prototypes
	 *            | The system-wide resource types
	 */
	public Branch(String location, List<ResourcePrototype> prototypes) 
			throws IllegalArgumentException {
		if(location == null)
			throw new IllegalArgumentException("The given location is null");
		projectList = new ArrayList<>();
		delegationProject = new DelegationProject();
		resMan = new ResourceManager(prototypes);
		branchRep = new BranchRepresentative();
		geographicLocation = location;
	}

	/**
	 * Clean out the Branch by resetting its projects, resource manager and
	 * branch representative
	 * 
	 * @param prototypes
	 *            | The system-wide resource types
	 * @throws IllegalArgumentException
	 *             | in the case of invalid prototypes
	 */
	public void flush(List<ResourcePrototype> prototypes) throws IllegalArgumentException {
		projectList = new ArrayList<>();
		delegationProject = new DelegationProject();
		resMan = new ResourceManager(prototypes);
		branchRep = new BranchRepresentative();
	}
	
	/**
	 * Returns the geographic location of the 
	 * 
	 * @return	The location of the Branch
	 */
	public String getGeographicLocation(){
		return this.geographicLocation;
	}

	/**
	 * Unwraps the ProjectView object and returns the Project that it contained
	 * IF the unwrapped project belongs to this Branch:
	 * projectList.contains(project)
	 * 
	 * @param view
	 *            | the ProjectView to unwrap
	 * @return | the unwrapped Project if it belonged to this Branch | NULL
	 *         otherwise
	 * @throws IllegalArgumentException
	 * 			| If the supplied arguments are invalid
	 * @throws UnexpectedViewContentException
	 * 			| If the view didn't contain a valid Project
	 */
	private Project unwrapProjectView(ProjectView view) 
			throws IllegalArgumentException, UnexpectedViewContentException {
		if(view == null) {
			throw new IllegalArgumentException("There was no project to unwrap!");
		}
		Project project = view.unwrap();
		if(project != delegationProject && !projectList.contains(project)) {
			throw new UnexpectedViewContentException("Project does not belong to this Branch");
		}
		return project;
	}
	
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
	 * 			| If the supplied arguments are invalid
	 */
	public void createProject(String name, String description,
			LocalDateTime creationTime, LocalDateTime dueTime) 
				throws IllegalArgumentException {
		Project project = new Project(name, description, creationTime, dueTime);
		projectList.add(project);
	}

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
	 * @throws IllegalArgumentException
	 * 			| If the supplied arguments are invalid
	 * @throws IllegalStateException
	 * 			| If this method would result in an inconsistent system state
	 * @throws ResourceUnavailableException 
	 * 			| if the requested resource isn't available for reservation
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
	 * @throws IllegalArgumentException
	 * 			| If the supplied arguments are invalid
	 * @throws IllegalStateException
	 * 			| If this method would result in an inconsistent system state
	 * @throws ResourceUnavailableException 
	 * 			| if the requested resource isn't available for reservation
	 */
	public void createTask(ProjectView projectView, String description,
			int estimatedDuration, int acceptableDeviation,
			List<TaskView> prerequisiteTasks, TaskView alternativeFor,
			Map<ResourceView, Integer> requiredResources, String taskStatus,
			LocalDateTime startTime, LocalDateTime endTime,
			LocalDateTime plannedStartTime, List<ResourceView> plannedDevelopers) 
				throws IllegalArgumentException, IllegalStateException, ResourceUnavailableException {
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
	 * @throws IllegalArgumentException
	 * 			| If the supplied arguments are invalid
	 * @throws IllegalStateException
	 * 			| If this method would result in an inconsistent system state
	 */
	public void setTaskFinished(ProjectView project, TaskView taskID,
			LocalDateTime endTime) 
					throws IllegalArgumentException, IllegalStateException {
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
	 * @throws IllegalArgumentException
	 * 			| If the supplied arguments are invalid
	 * @throws IllegalStateException
	 * 			| If this method would result in an inconsistent system state
	 */
	public void setTaskFailed(ProjectView project, TaskView taskID,
			LocalDateTime endTime) 
				throws IllegalArgumentException, IllegalStateException{
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
	 * @throws IllegalArgumentException
	 * 			| If the supplied arguments are invalid
	 * @throws IllegalStateException
	 * 			| if this method would result in an inconsistent system state
	 */
	public void setTaskExecuting(ProjectView project, TaskView task,
			LocalDateTime startTime) 
				throws IllegalArgumentException, IllegalStateException {
		unwrapProjectView(project).setTaskExecuting(task, startTime);
	}

	/**
	 * Returns a list of ProjectView objects that each contain one of this
	 * Branch's projects
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
	 * Branch's projects and the delegation project
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
	 * @throws IllegalArgumentException
	 * 			| If the supplied arguments are invalid
	 */
	public void createResourcePrototype(String resourceName,
			Optional<LocalTime> availabilityStart,
			Optional<LocalTime> availabilityEnd) throws IllegalArgumentException {
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
	 */
	public void declareConcreteResource(String name,
			ResourceView fromPrototype) throws IllegalArgumentException, UnexpectedViewContentException{
		resMan.declareConcreteResource(name, fromPrototype);
	}

	/**
	 * Define a new developer with a given name in the system.
	 * 
	 * @param name
	 *            | The name of the new developer
	 * @return true if the creation succeeded, false otherwise
	 */
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
	 * @throws IllegalStateException
	 * 			| If the reservation would create an inconsistent system state
	 * @throws IllegalArgumentException
	 * 			| If the supplied arguments are invalid
	 * @throws ResourceUnavailableException 
	 * 			| if the requested resource isn't available for reservation
	 */
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
	 * @throws IllegalArgumentException
	 * 			| If the supplied arguments are invalid
	 * @throws IllegalStateException
	 * 			| if this method would create an inconsistent system state
	 * @throws ResourceUnavailableException 
	 * 			| If the requested resoruces aren't available for reservation
	 */
	public void planTask(ProjectView project, TaskView task,
			LocalDateTime plannedStartTime, List<ResourceView> concRes, List<ResourceView> devs) 
				throws IllegalArgumentException, IllegalStateException, ResourceUnavailableException{
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
	 * @throws IllegalArgumentException
	 * 			| If the supplied arguments are invalid
	 * @throws IllegalStateException
	 * 			| If the planning would create an inconsistent system state
	 * @throws ResourceUnavailableException
	 * 			| If a required resource is unavailable for reservation 
	 */
	public void planRawTask(ProjectView project, TaskView task,
			LocalDateTime plannedStartTime, List<ResourceView> concRes, List<ResourceView> devs) 
				throws IllegalArgumentException, IllegalStateException, ResourceUnavailableException {
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
	 * 			| If the supplied arguments are invalid
	 */
	public void addRequirementsToResource(List<ResourceView> resourcesToAdd,
			ResourceView prototype) throws IllegalArgumentException {
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
	 * 			| If the supplied arguments are invalid
	 */
	public void addConflictsToResource(List<ResourceView> resourcesToAdd,
			ResourceView prototype) throws IllegalArgumentException {
		resMan.addConflictsToResource(resourcesToAdd, prototype);
	}
	
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
	public User getUser(ResourceView user) throws IllegalArgumentException, UnexpectedViewContentException {
		return resMan.unWrapUserView(user);
	}

	/**
	 *  Get the responsible branch for the given task
	 * @param 	project
	 * 			The project containing the task
	 * @param 	task
	 * 			the task to get the responsible branch from.
	 * @return	The responsible branch for the task.
	 */
	public Optional<BranchView> getResponsibleBranch(ProjectView project, TaskView task) {
		return project.unwrap().getResponsibleBranch(branchRep, task);
	}
	
	public TaskView getDelegatingTask(ProjectView project, TaskView task) {
		return project.unwrap().getDelegatingTask(branchRep, task).orElse(task);
	}

	/**
	 *  Delegate the task in the given taskview to the given branch.
	 * @param 	project
	 * 			The project containing the task to delegate.
	 * @param 	task
	 * 			The task to delegate.
	 * @param 	newBranch
	 * 			The new branch to delegate the task to.
	 */
	public void delegateTask(ProjectView project, TaskView task,
			Branch newBranch) {
		Project p = unwrapProjectView(project);
		p.delegateTask(branchRep, task, this, newBranch);
		
	}
	
	/**
	 * Delegate the given task to the given branch.
	 * @param 	task
	 * 			The task to delegate 
	 * @param 	toBranch
	 * 			The branch to delegate the task to
	 */
	public void delegateTask(Task task, Branch toBranch) {
		branchRep.delegateTask(task, this, toBranch);
	}

	/**
	 * Accept a delegation throuw a DelegatingTaskProxy
	 * 
	 * @param delProxy the DelegatingTaskProxy in the remote (original) branch
	 * @throws IllegalArgumentException
	 * 			| If the supplied arguments are invalid
	 */
	public void delegateAccept(DelegatingTaskProxy delProxy) throws IllegalArgumentException {
		TaskView task = new TaskView(delProxy.getTask()); //Dit kan vermeden worden door de task Creation date hieronder als parameter mee te geven
		Map<ResourceView, Integer> wrappedResources = task.getRequiredResources();
		
		try {
			delegationProject.createTask(task.getDescription(), task.getEstimatedDuration(), task.getAcceptableDeviation(), resMan, new ArrayList<TaskView>(), wrappedResources, null, null, null, null, null, null);
		} catch (ResourceUnavailableException | IllegalArgumentException
				| IllegalStateException e) {
			throw new IllegalArgumentException("Task cannot be accepted for delegation, reason: " + e.getMessage());
		}
		DelegatingTask delTask = (DelegatingTask) delegationProject.getTasks().get(delegationProject.getTasks().size() - 1);
		branchRep.delegateAccept(delProxy, delTask, this);
	}

	public void removeDelegatedTask(Task task) {
		delegationProject.removeTask(new TaskView(task));		
	}

	/**
	 * Toggles buffer mode to on or off
	 * @param 	bool
	 * 			True means on, false means off
	 */
	public void setBufferMode(boolean bool) {
		branchRep.setBufferMode(bool);
	}

	/**
	 * @return the original proxies of this branch representative
	 */
	public Map<Task, OriginalTaskProxy> getOriginalProxies() {
		return branchRep.getOriginalProxies();
	}

	/**
	 * @return the delegating proxies of this branch representative
	 */
	public Map<Task, DelegatingTaskProxy> getDelegatingProxies() {
		return branchRep.getDelegatingProxies();
	}
	
	/**
	 * Offer new task and (original) proxy pairings to the representative
	 * 
	 * @param proxies
	 *            | The new task-proxy pairings present in the system
	 */
	public void offerOriginalTaskProxies(Map<Task, OriginalTaskProxy> proxies) {
		branchRep.offerOriginalTaskProxies(proxies);		
	}

	/**
	 * Offer new task and (delegating) proxy pairings to the representative
	 * 
	 * @param proxies
	 *            | The new task-proxy pairings present in the system
	 */
	public void offerDelegatingTaskProxies(
			Map<Task, DelegatingTaskProxy> proxies) {
		branchRep.offerDelegatingTaskProxies(proxies);
	}
}
