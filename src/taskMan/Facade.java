package taskMan;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

import userInterface.IFacade;

public class Facade implements IFacade {
	private TaskMan taskMan = new TaskMan();
	
	
	public LocalDateTime getCurrentTime() { return taskMan.getCurrentTime(); }
	
	@Override
	public boolean createProject(String name, String description, LocalDateTime creationTime, LocalDateTime dueTime) {
		return taskMan.createProject(name, description, creationTime, dueTime);
	}
	
	@Override
	public boolean createProject(String name, String description,
			LocalDateTime dueTime) {
		return taskMan.createProject(name, description, dueTime);
	}
	
	
	public boolean createTask(int projectID, String description, LocalTime estimatedDuration, int acceptableDeviation, String taskStatus, Integer alternativeFor, List<Integer> prerequisiteTasks, LocalDateTime startTime, LocalDateTime endTime) {
		return taskMan.createTask(projectID, description, estimatedDuration, acceptableDeviation, taskStatus, alternativeFor, prerequisiteTasks, startTime, endTime);	}
	
	public boolean updateTaskDetails(int projectID, int taskID, LocalDateTime startTime, LocalDateTime endTime, String taskStatus) {
		return taskMan.updateTaskDetails(projectID, taskID, startTime, endTime, taskStatus);
	}
	
	public String getProjectName(int projectID) {
		return taskMan.getProjectName(projectID);
	}
	
	public String getProjectDescription(int projectID) {
		return taskMan.getProjectDescription(projectID);
	}
	
	public LocalDateTime getProjectCreationTime(int projectID) {
		return taskMan.getProjectCreationTime(projectID);
	}
	
	public LocalDateTime getProjectDueTime(int projectID) {
		return taskMan.getProjectDueTime(projectID);
	}
	
	public LocalDateTime getProjectEndTime(int projectID) {
		return taskMan.getProjectEndTime(projectID);
	}
	
	public String getProjectStatus(int projectID) {
		return taskMan.getProjectStatus(projectID);
	}

	@Override
	public boolean advanceTimeTo(LocalDateTime time) {
		return taskMan.advanceTimeTo(time);
		
	}

	@Override
	public HashMap<Integer, List<Integer>> getAvailableTasks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> getAvailableTasks(int projectID) {
		// TODO Auto-generated method stub
		return null;
	}

	

	@Override
	public boolean createTask(int projectID, String description,
			int estimatedDuration, int acceptableDeviation, String taskStatus,
			Integer alternativeFor, List<Integer> prerequisiteTasks,
			LocalDateTime startTime, LocalDateTime endTime) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean createTask(int projectID, String description,
			int estimatedDuration, int acceptableDeviation,
			Integer alternativeFor, List<Integer> prerequisiteTasks) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getProjectAmount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isOnTime(int projectID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getDelay(int projectID) {
		// TODO Auto-generated method stub
		return 0;
	}

}
