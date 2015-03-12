package userInterface;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public interface IFacade {

	public boolean advanceTimeTo(LocalDateTime time);
	
	public LocalDateTime getCurrentTime();
	
	public boolean createProject(String name, String description, LocalDateTime creationTime, LocalDateTime dueTime);
	public boolean createProject(String name, String description, LocalDateTime dueTime);
	
	public boolean createTask(int projectID, String description, int estimatedDuration, int acceptableDeviation, String taskStatus, Integer alternativeFor, List<Integer> prerequisiteTasks, LocalDateTime startTime, LocalDateTime endTime);
	public boolean createTask(int projectID, String description, int estimatedDuration, int acceptableDeviation, Integer alternativeFor, List<Integer> prerequisiteTasks);
	
	
	public boolean setTaskFinished(int projectID, int taskID, LocalDateTime startTime, LocalDateTime endTime);
	public boolean setTaskFailed(int projectID, int taskID, LocalDateTime startTime, LocalDateTime endTime);
	
	public int getProjectAmount();
	public int getTaskAmount(int projectID);	
	
	public String getProjectName(int projectID);
	
	public boolean isOnTime(int projectID);
	public boolean isTaskUnacceptableOverdue(int projectID, int i);
	public boolean isTaskOnTime(int projectID, int i);
	public String getTaskOverTimePercentage(int projectID, int i);
	
	public int[] getProjectDelay(int projectID);
	
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

	public HashMap<Integer, List<Integer>> getAvailableTasks();
	public List<Integer> getAvailableTasks(int projectID);

	public boolean hasTaskPrerequisites(int projectID, int taskID);
	public List<Integer> getTaskPrerequisitesFor(int projectID, int taskID);

	public boolean hasTaskAlternative(int projectID, int taskID);
	public int getTaskAlternativeTo(int projectID, int taskID);
	
}
