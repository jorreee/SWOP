package userInterface;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.security.auth.login.CredentialException;

import company.BranchView;
import company.taskMan.ProjectView;
import company.taskMan.project.TaskView;
import company.taskMan.resource.ResourceView;
import company.taskMan.resource.user.UserPermission;

public interface IFacade {

	public List<BranchView> getBranches();
	public void selectBranch(BranchView branch);
	public void initializeBranch(String geographicLocation);
	
	public void advanceTimeTo(LocalDateTime time) throws IllegalArgumentException, CredentialException;

	public LocalDateTime getCurrentTime();
	public boolean isLoggedIn();
	public void logout();
	
	public ResourceView getCurrentUser();
	public List<ResourceView> getPossibleUsers();
	public void changeToUser(ResourceView user);

	
	public void createProject(String name, String description,
			LocalDateTime creationTime, LocalDateTime dueTime) throws CredentialException;

	public void createProject(String name, String description,
			LocalDateTime dueTime) throws CredentialException;

	public void createTask(ProjectView project, String description,
			int estimatedDuration, int acceptableDeviation,
			List<TaskView> prerequisiteTasks, Map<ResourceView, Integer> requiredResources, TaskView alternativeFor) throws CredentialException;
	
	public void createTask(ProjectView project, String description,
			int estimatedDuration, int acceptableDeviation,
			List<TaskView> prerequisiteTasks, TaskView alternativeFor, Map<ResourceView, Integer> requiredResources, 
			String taskStatus, LocalDateTime startTime, LocalDateTime endTime,
			LocalDateTime plannedStartTime, List<ResourceView> plannedDevelopers) throws CredentialException;

	
	public void setTaskFinished(ProjectView project, TaskView task,
			LocalDateTime endTime) throws CredentialException, IllegalArgumentException;

	public void setTaskFailed(ProjectView project, TaskView task,
			 LocalDateTime endTime) throws CredentialException, IllegalArgumentException;
	
	public void setTaskExecuting(ProjectView project, TaskView task, LocalDateTime startTime) throws CredentialException, IllegalArgumentException;

	public List<ProjectView> getProjects();

	public boolean storeInMemento();

	public boolean revertFromMemento();

	public boolean discardMemento();

	public void createResourcePrototype(String name,
			Optional<LocalTime> availabilityStart,
			Optional<LocalTime> availabilityEnd);

	public void declareConcreteResource(String name, ResourceView resourceView);

	public void createDeveloper(String name);

//	public boolean reserveResource(ResourceView resource, ProjectView project, TaskView task);
	public void reserveResource(ResourceView resource, ProjectView project, TaskView task, LocalDateTime startTime, LocalDateTime endTime);

//	public List<LocalDateTime> getPossibleTaskStartingTimes(ProjectView project, TaskView task, int amount);

	public List<ResourceView> getDeveloperList();

	public Map<ProjectView, List<TaskView>> findConflictingPlannings(
			TaskView taskID);

	public List<ResourceView> getResourcePrototypes();
	
	public void addRequirementsToResource(List<ResourceView> resourcesToAdd, ResourceView prototype);
	public void addConflictsToResource(List<ResourceView> resourcesToAdd, ResourceView prototype);

	public List<ResourceView> getConcreteResourcesForPrototype(
			ResourceView resourcePrototype);

	public void planTask(ProjectView project, TaskView task,
			LocalDateTime planningStartTime, List<ResourceView> concRes, List<ResourceView> devs) throws CredentialException;
	public void planRawTask(ProjectView project, TaskView task,
			LocalDateTime planningStartTime, List<ResourceView> concRes, List<ResourceView> devs) throws CredentialException;

//	public Map<ProjectView, List<TaskView>> reservationConflict(ResourceView requiredResource,
//			ProjectView project, TaskView task, LocalDateTime planningStartTime);

//	public boolean flushFutureReservations(ProjectView project,
//			TaskView conflictingTask);

	public boolean currentUserHasPermission(UserPermission permission);

	public void delegateTask(ProjectView project, TaskView task, BranchView newBranch);
	public BranchView getResponsibleBranch(TaskView task);

}
