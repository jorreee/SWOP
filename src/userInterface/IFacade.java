package userInterface;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

public interface IFacade {

	public boolean advanceTime(LocalDateTime time);
	
	public LocalDateTime getCurrentTime();
	
	public boolean createProject(String name, String description, LocalDateTime dueTime);
	
	public boolean createTask(int projectID, String description, int estimatedDuration, int acceptableDeviation, String taskStatus, Integer alternativeFor, List<Integer> prerequisiteTasks, LocalDateTime startTime, LocalDateTime endTime);
	public boolean createTask(int projectID, String description, int estimatedDuration, int acceptableDeviation, Integer alternativeFor, List<Integer> prerequisiteTasks);
	
	public boolean updateTaskDetails(int projectID, int taskID, LocalDateTime startTime, LocalDateTime endTime, String taskStatus);
	
	public String getProjectName(int projectID);
	
	public String getProjectDescription(int projectID);
	
	public LocalDateTime getProjectCreationTime(int projectID);
	
	public LocalDateTime getProjectDueTime(int projectID);
	
	public LocalDateTime getProjectEndTime(int projectID);
	
	public String getProjectStatus(int projectID);

	public HashMap<Integer, List<Integer>> getAvailableTasks();
	public List<Integer> getAvailableTasks(int projectID);


	
}
