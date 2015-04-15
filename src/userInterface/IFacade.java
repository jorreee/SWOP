package userInterface;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import taskMan.view.ProjectView;
import taskMan.view.ResourceView;
import taskMan.view.TaskView;

public interface IFacade {

	public boolean advanceTimeTo(LocalDateTime time);

	public LocalDateTime getCurrentTime();
	
	public String getCurrentUsername();
	public List<ResourceView> getPossibleUsernames();
	public boolean changeToUser(String username);

	
	public boolean createProject(String name, String description,
			LocalDateTime creationTime, LocalDateTime dueTime);

	public boolean createProject(String name, String description,
			LocalDateTime dueTime);

	public boolean createTask(ProjectView project, String description,
			int estimatedDuration, int acceptableDeviation,
			List<TaskView> prerequisiteTasks, Map<ResourceView, Integer> requiredResources, TaskView alternativeFor);
	
	public boolean createTask(ProjectView project, String description,
			int estimatedDuration, int acceptableDeviation,
			List<TaskView> prerequisiteTasks, TaskView alternativeFor, Map<ResourceView, Integer> requiredResources, 
			String taskStatus, LocalDateTime startTime, LocalDateTime endTime,
			LocalDateTime plannedStartTime, List<ResourceView> plannedDevelopers);

	
	public boolean setTaskFinished(ProjectView projectID, TaskView taskID,
			LocalDateTime endTime);

	public boolean setTaskFailed(ProjectView projectID, TaskView taskID,
			 LocalDateTime endTime);

	public List<ProjectView> getProjects();

	public boolean storeInMemento();

	public boolean revertFromMemento();

	public boolean discardMemento();

	public boolean createResourcePrototype(String name,
			Optional<LocalTime> availabilityStart,
			Optional<LocalTime> availabilityEnd);

	public boolean declareConcreteResource(String name, ResourceView resourceView);

	public boolean createDeveloper(String name);

	public boolean reserveResource(ResourceView resource, ProjectView project, TaskView task);
	public boolean reserveResource(ResourceView resource, ProjectView project, TaskView task, LocalDateTime startTime, LocalDateTime endTime);

	public List<LocalDateTime> getPossibleTaskStartingTimes(ProjectView project, TaskView task, int amount);

	public List<ResourceView> getDeveloperList();

	public Map<ProjectView, List<TaskView>> findConflictingDeveloperPlannings(
			ProjectView projectID, TaskView taskID, List<ResourceView> developerNames,
			LocalDateTime planningStartTime);

	public List<ResourceView> getResourcePrototypes();
	
	public boolean addRequirementsToResource(List<ResourceView> resourcesToAdd, ResourceView prototype);
	public boolean addConflictsToResource(List<ResourceView> resourcesToAdd, ResourceView prototype);

	public List<ResourceView> getConcreteResourcesForPrototype(
			ResourceView resourcePrototype);

	public boolean planTask(ProjectView project, TaskView task,
			LocalDateTime planningStartTime);

	public Map<ProjectView, List<TaskView>> reservationConflict(ResourceView requiredResource,
			ProjectView project, TaskView task, LocalDateTime planningStartTime);

	public boolean flushFutureReservations(ProjectView project,
			TaskView conflictingTask);
}
