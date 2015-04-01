package userInterface;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

import taskMan.util.IntPair;
import taskMan.view.ProjectView;
import taskMan.view.TaskView;

public interface IFacade {

	public boolean advanceTimeTo(LocalDateTime time);

	public LocalDateTime getCurrentTime();
	
	public String getCurrentUsername();
	public List<String> getPossibleUsernames();
	public boolean changeToUser(String username);

	
	public boolean createProject(String name, String description,
			LocalDateTime creationTime, LocalDateTime dueTime);

	public boolean createProject(String name, String description,
			LocalDateTime dueTime);

	public boolean createRawTask(int project, String description,
			int estimatedDuration, int acceptableDeviation,
			List<Integer> prerequisiteTasks, int alternativeFor,
			String taskStatus, LocalDateTime startTime, LocalDateTime endTime);

	public boolean createRawPlannedTask(int project, String description,
			int estimatedDuration, int acceptableDeviation,
			List<Integer> prerequisiteTasks, int alternativeFor,
			String statusString, LocalDateTime startTime,
			LocalDateTime endTime, LocalDateTime planningDueTime,
			List<Integer> plannedDevelopers, List<IntPair> plannedResources);

	public boolean createTask(ProjectView project, String description,
			int estimatedDuration, int acceptableDeviation,
			List<TaskView> prerequisiteTasks, TaskView alternativeFor);

	public boolean setTaskFinished(ProjectView projectID, TaskView taskID,
			LocalDateTime startTime, LocalDateTime endTime);

	public boolean setTaskFailed(ProjectView projectID, TaskView taskID,
			LocalDateTime startTime, LocalDateTime endTime);

	public List<ProjectView> getProjects();

	public void storeInMemento();

	public void revertFromMemento();

	public void discardMemento();

	public boolean declareDailyAvailability(LocalTime startTime, LocalTime endTime);

	public boolean createResourcePrototype(String name,
			List<Integer> requirements, List<Integer> conflicts,
			Integer availabilityIndex);

	public boolean createRawResource(String name, int typeIndex);

	public boolean createDeveloper(String name);

	public boolean createRawReservation(int resource, int project, int task,
			LocalDateTime startTime, LocalDateTime endTime);

	public List<LocalDateTime> getPossibleTaskStartingTimes(ProjectView project, TaskView task, int amount);

	public List<String> getDeveloperList();

	public HashMap<ProjectView, List<TaskView>> findConflictingDeveloperPlannings(
			ProjectView projectID, TaskView taskID, List<String> developerNames,
			LocalDateTime planningStartTime);

	/*
	public boolean createTask(int projectID, String description, int estimatedDuration, int acceptableDeviation, String taskStatus, Integer alternativeFor, List<Integer> prerequisiteTasks, LocalDateTime startTime, LocalDateTime endTime);
	public boolean createTask(int projectID, String description, int estimatedDuration, int acceptableDeviation, Integer alternativeFor, List<Integer> prerequisiteTasks);
	

	public boolean setTaskFinished(int projectID, int taskID, LocalDateTime startTime, LocalDateTime endTime);
	public boolean setTaskFailed(int projectID, int taskID, LocalDateTime startTime, LocalDateTime endTime);
	
	public int getProjectAmount();
	public int getTaskAmount(int projectID);	
	
	public String getProjectName(int projectID);
	
	public boolean isProjectEstimatedOnTime(int projectID);
	public boolean isTaskUnacceptableOverdue(int projectID, int taskID);
	public boolean isTaskOnTime(int projectID, int taskID);
	public int getTaskOverTimePercentage(int projectID, int TaskID);
	
	public int[] getProjectDelay(int projectID);
	public boolean isProjectFinished(int projectID);
	public int[] getEstimatedProjectDelay(int projectID);
	
	public String getProjectDescription(int projectID);
	public String getTaskDescription(int projectID, int taskID);
	
	public LocalDateTime getProjectCreationTime(int projectID);
	public LocalDateTime getTaskStartTime(int projectID, int taskID);
	
	public LocalDateTime getProjectDueTime(int projectID);
	public int getEstimatedTaskDuration(int projectID, int taskID);	
	public int getAcceptableTaskDeviation(int projectID, int taskID);
	
	public LocalDateTime getProjectEndTime(int projectID);
	public boolean hasTaskEnded(int projectID, int taskID);
	public LocalDateTime getTaskEndTime(int projectID, int taskID);
	
	public String getProjectStatus(int projectID);
	public String getTaskStatus(int projectID, int taskID);

	public HashMap<Integer,List<Integer>> getAvailableTasks();
	public List<Integer> getAvailableTasks(int projectID);

	public boolean hasTaskPrerequisites(int projectID, int taskID);
	public List<Integer> getTaskPrerequisitesFor(int projectID, int taskID);

	public boolean hasTaskAlternative(int projectID, int taskID);
	public int getTaskAlternativeTo(int projectID, int taskID);
	*/
}
