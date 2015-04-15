package taskMan;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import taskMan.resource.ResourceManager;
import taskMan.resource.user.User;
import taskMan.resource.user.UserCredential;
import taskMan.view.ProjectView;
import taskMan.view.ResourceView;
import taskMan.view.TaskView;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

//TODO Still not done

/**
 * The Main System that keeps track of the list of projects and the current Time.
 * @author Tim Van den Broecke, Joran Van de Woestijne, Vincent Van Gestel, Eli Vangrieken
 *
 */
public class TaskMan {
	
	private ArrayList<Project> projectList;
	private int nextProjectID;
	private LocalDateTime currentTime;
	private User currentUser;
	private ResourceManager resMan;
	
	
	/**
	 * Creates a TaskMan system instance with a given time.
	 * 
	 * @param 	time
	 * 			The current TaskMan time. 
	 * 
	 */
	public TaskMan(LocalDateTime time){
		projectList = new ArrayList<>();
		nextProjectID = 0;
		currentTime = time;
		resMan = new ResourceManager();
		currentUser = resMan.getDefaultUser();
	}
	
	/**
	 * Unwraps the ProjectView object and returns the Project that it contained
	 * 		IF the unwrapped project belongs to this taskman:
	 * 				projectList.contains(project)
	 * 
	 * @param 	p
	 * 			| the ProjectView to unwrap
	 * @return 
	 * 			| the unwrapped Project if it belonged to this TaskMan
	 * 			| NULL otherwise
	 */
	private Project unwrapProjectView(ProjectView p) {
		for(Project project : projectList) {
			if (p.hasAsProject(project)) {
				return project;
			}
		}
		return null;
	}
	
	/**
	 * Advances the current time to the given time.
	 * 
	 * @param 	time
	 * 			The time to which the system should advance
	 * @return	True if the advance time was successful.
	 * 			False if the time parameter is earlier than the current time.
	 */
	public boolean advanceTimeTo(LocalDateTime time) {
		if(time == null)
			return false;
		if (time.isAfter(currentTime)) {
			currentTime = time;
			return true;
		}
		else return false;
		
	}
	
//	/**
//	 * Gets the project with the given project ID
//	 * 
//	 * @param 	projectID
//	 * 			The ID of the project to be retrieved
//	 * @return	The project with the project ID
//	 * 			null if the ID isn't a valid one
//	 */
//	private Project getProject(int projectID) {
//		if(!isValidProjectID(projectID)) {
//			return null;
//		}
//		return projectList.get(projectID);
//	}
		
	/**
	 * Gets the current time
	 * 
	 * @return the current time
	 */
	public LocalDateTime getCurrentTime() { 
		return currentTime; 
		}
	
	/**
	 * Creates a new Project with a creation time
	 * 
	 * @param 	name
	 * 			The name of the project
	 * @param 	description
	 * 			The description of the project
	 * @param 	creationTime
	 * 			The creation time of the project
	 * @param 	dueTime
	 * 			The due time of the project
	 * @return 	true if the project creation was successful
	 * 			false if the creation was unsuccessful
	 */
	public boolean createProject(String name, String description, LocalDateTime creationTime, LocalDateTime dueTime) {
		Project project = null;
		try{
			 project = new Project(nextProjectID, name, description, creationTime, dueTime);
		} catch(IllegalArgumentException e) {
			return false;
		}
		nextProjectID++;
		return projectList.add(project);
	}
	
	/**
	 * Creates a new Project with the current time as the creation time.
	 * 
	 * @param 	name
	 * 			The name of the project
	 * @param 	description
	 * 			The description of the project
	 * @param 	dueTime
	 * 			The due time of the project
	 * @return 	true if the project creation was successful
	 * 			false if the creation was unsuccessful
	 */
	public boolean createProject(String name, String description, LocalDateTime dueTime) {
		if(!currentUser.hasAsCredential(UserCredential.PROJECTMANAGER)) {
			return false;
		} else {
			return createProject(name, description, getCurrentTime(), dueTime);
		}
	}
	
	/**
	 * Creates a new Task without a set status.
	 * 
	 * @param 	description
	 * 			The description of the given Task.
	 * @param 	estimatedDuration
	 * 			The estimated duration of the Task.
	 * @param 	acceptableDeviation
	 * 			The acceptable deviation of the Task.
	 * @param 	alternativeFor
	 * 			The alternative Task.
	 * @param 	prerequisiteTasks
	 * 			The prerequisites Tasks for this Task.
	 * @return	True if the creation of a new Task was successful.
	 * 			False if the projectID is a valid one.
	 * 			False if the creation was unsuccessful
	 */
	public boolean createTask(ProjectView project, 
			String description, 
			int estimatedDuration, 
			int acceptableDeviation,
			List<TaskView> prerequisiteTasks, 
			Map<ResourceView, Integer> requiredResources, 
			TaskView alternativeFor) {
		if(!currentUser.hasAsCredential(UserCredential.PROJECTMANAGER)) {
			return false;
		} else {
			return createTask(project,
				description, 
				estimatedDuration, 
				acceptableDeviation,  
				prerequisiteTasks, 
				alternativeFor, 
				requiredResources,
				null,
				null,
				null,
				null,
				null);
		}
	}
	
	/**
	 * Creates a Planned Task as issued by the input file.
	 * 
	 * @param 	description
	 * 			The description of the Task.
	 * @param 	estimatedDuration
	 * 			The estimated duration of the Task.
	 * @param 	acceptableDeviation
	 * 			The acceptable deviation of the Task.
	 * @param 	prerequisiteTasks
	 * 			The prerequisites of the Task.
	 * @param 	alternativeFor
	 * 			The alternative for the Task.
	 * @param 	statusString
	 * 			The status of the Task.
	 * @param 	startTime
	 * 			The startTime of the Task.
	 * @param 	endTime
	 * 			The endTime of the Task.
	 * @param 	planningDueTime
	 * 			The due time of the planning of the Task.
	 * @param 	plannedDevelopers
	 * 			The planned developers of the Task.
	 * @param 	plannedResources
	 * 			The planned resources of the Task.
	 * @return	True if and only if the creation of the Raw Planned Task was succesful.
	 */
	public boolean createTask(ProjectView projectView, String description,
			int estimatedDuration, int acceptableDeviation,
			List<TaskView> prerequisiteTasks, TaskView alternativeFor,
			Map<ResourceView, Integer> requiredResources, String taskStatus,
			LocalDateTime startTime, LocalDateTime endTime,
			LocalDateTime plannedStartTime, List<ResourceView> plannedDevelopers) {
		Project project = unwrapProjectView(projectView);
		if(project == null) {
			return false;
		}
		return project.createTask(
				description,
				estimatedDuration,
				acceptableDeviation,
				resMan,
				prerequisiteTasks,
				requiredResources,
				alternativeFor,
				taskStatus,
				startTime,
				endTime,
				plannedStartTime,
				plannedDevelopers);
	}
	
	/**
	 * Sets the task with the given task id belonging to the project 
	 * with the given project id to finished
	 * 
	 * @param 	projectID
	 * 			the id of the given project
	 * @param 	taskID
	 * 			the id of the given task
	 * @param 	startTime
	 * 			the start time of the given task
	 * @param 	endTime
	 * 			the end time of the given task
	 * @return	True if setting the task to finished was successful,
	 * 			False if it was unsuccessful
	 * 			false if the project ID isn't a valid one
	 */
	public boolean setTaskFinished(ProjectView project, TaskView taskID, LocalDateTime endTime) {
		if(!currentUser.hasAsCredential(UserCredential.DEVELOPER)) {
			return false;
		}
		if(endTime == null) { // || startTime == null) {
			return false;
		}
		if(endTime.isAfter(currentTime)) {
			return false;
		}
		Project p = unwrapProjectView(project);
		if(p == null) {
			return false;
		}
		return p.setTaskFinished(taskID,endTime);
	}
	
	/**
	 * Sets the task with the given task id belonging to the project 
	 * with the given project id to failed
	 * 
	 * @param 	projectID
	 * 			the id of the given project
	 * @param 	taskID
	 * 			the id of the given task
	 * @param 	startTime
	 * 			the start time of the given task
	 * @param 	endTime
	 * 			the end time of the given task
	 * @return	True if setting the task to failed was successful.
	 * 			False if it was unsuccessful.
	 * 			False if the project ID isn't a valid one.
	 * 			False if the end time is null or the end time comes after the current time.
	 */
	public boolean setTaskFailed(ProjectView project, TaskView taskID, LocalDateTime endTime) {
		if(!currentUser.hasAsCredential(UserCredential.DEVELOPER)) {
			return false;
		}
		if(endTime == null) { // || startTime == null) {
			return false;
		}
		if(endTime.isAfter(currentTime)) {
			return false;
		}
		Project p = unwrapProjectView(project);
		if(p == null) {
			return false;
		}
		return p.setTaskFailed(taskID, endTime);
	}

	/**
	 * Returns a list of ProjectView objects that each contain one of this taskman's projects
	 * 
	 * @return
	 * 			| a list of ProjectViews
	 */
	public List<ProjectView> getProjects() {
		Builder<ProjectView> views = ImmutableList.builder();
		for(Project project : projectList)
			views.add(new ProjectView(project));
		return views.build();
	}

	public String getCurrentUserName(){
		return currentUser.getName();
	}
	
	public boolean changeToUser(String name){
		User user = resMan.getUser(name);
		if (user == null){
			return false;
		}
		else {
			currentUser = user;
			return true;
		}
	}
	
	public List<ResourceView> getPossibleUsernames(){
		return resMan.getPossibleUsernames();
	}
	
	public boolean createResourcePrototype(String name,
			List<Integer> requirements, 
			List<Integer> conflicts,
			Optional<LocalTime> availabilityStart,
			Optional<LocalTime> availabilityEnd) {
		return resMan.createResourcePrototype(name,requirements,conflicts,availabilityStart,availabilityEnd);
	}
	
	public boolean declareConcreteResource(String name, ResourceView typeIndex) {
		return resMan.declareConcreteResource(name,typeIndex);
	}
	
	public boolean createDeveloper(String name) {
		return resMan.createDeveloper(name);
	}
	
	public boolean reserveResource(ResourceView resource, ProjectView project, TaskView task) {
		return false;
		//TODO dit kan via de rare "raw plan" data worden geinitialiseerd
	}
	
	public List<LocalDateTime> getPossibleTaskStartingTimes(ProjectView project, TaskView task,
			int amount) {
		return unwrapProjectView(project).getPossibleTaskStartingTimes(task,amount);
	}

//	public List<AvailabilityPeriod> getPossibleDailyAvailabilities() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	public List<ResourceView> getDeveloperList() {
		return resMan.getDeveloperList();
	}

	public boolean flushFutureReservations(ProjectView project, TaskView task) {
		// TODO Auto-generated method stub
		return false;
	}

	public Map<ProjectView, List<TaskView>> reservationConflict(
			ResourceView requiredResource, ProjectView project, TaskView task,
			LocalDateTime plannedStartTime) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean planTask(ProjectView project, TaskView task,
			LocalDateTime plannedStartTime) {
		if(!currentUser.hasAsCredential(UserCredential.PROJECTMANAGER)) {
			return false;
		}
		// TODO Auto-generated method stub
		return false;
	}

	public List<ResourceView> getConcreteResourcesForPrototype(
			ResourceView resourcePrototype) {
		return resMan.getConcreteResourcesForPrototype(resourcePrototype);
	}

	public ResourceView getPrototypeOf(ResourceView resource) {
		return resMan.getPrototypeOf(resource);
	}

	@Deprecated
	public List<ResourceView> getAllConcreteResources() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ResourceView> getResourcePrototypes() {
		return resMan.getResourcePrototypes();
	}

	public boolean addRequirementsToResource(List<ResourceView> resourcesToAdd,
			ResourceView prototype) {
		return resMan.addRequirementsToResource(resourcesToAdd, prototype);
	}

	public boolean addConflictsToResource(List<ResourceView> resourcesToAdd,
			ResourceView prototype) {
		return resMan.addConflictsToResource(resourcesToAdd, prototype);
	}
	
}