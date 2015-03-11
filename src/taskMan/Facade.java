package taskMan;

import java.time.LocalDateTime;
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
	
	@Override
	public boolean createTask(int projectID, String description, int estimatedDuration, int acceptableDeviation, String taskStatus, Integer alternativeFor, List<Integer> prerequisiteTasks, LocalDateTime startTime, LocalDateTime endTime) {
		return taskMan.createTask(projectID, description, estimatedDuration, acceptableDeviation, taskStatus, alternativeFor, prerequisiteTasks, startTime, endTime);
	}
	
	@Override
	public boolean createTask(int projectID, String description, int estimatedDuration, int acceptableDeviation, Integer alternativeFor, List<Integer> prerequisiteTasks) {
		return taskMan.createTask(projectID, description, estimatedDuration, acceptableDeviation, alternativeFor, prerequisiteTasks);
	}
	
	
	@Override
	public String getProjectName(int projectID) {
		return taskMan.getProjectName(projectID);
	}
	
	@Override
	public String getProjectDescription(int projectID) {
		return taskMan.getProjectDescription(projectID);
	}
	
	@Override
	public LocalDateTime getProjectCreationTime(int projectID) {
		return taskMan.getProjectCreationTime(projectID);
	}
	
	@Override
	public LocalDateTime getProjectDueTime(int projectID) {
		return taskMan.getProjectDueTime(projectID);
	}
	
	@Override
	public LocalDateTime getProjectEndTime(int projectID) {
		return taskMan.getProjectEndTime(projectID);
	}
	
	@Override
	public String getProjectStatus(int projectID) {
		return taskMan.getProjectStatus(projectID);
	}

	@Override
	public boolean advanceTimeTo(LocalDateTime time) {
		return taskMan.advanceTimeTo(time);
		
	}

	@Override
	public HashMap<Integer, List<Integer>> getAvailableTasks() {
		return taskMan.getAvailableTasks();
	}

	@Override
	public List<Integer> getAvailableTasks(int projectID) {
		return taskMan.getAvailableTasks(projectID);
	}


	@Override
	public int getProjectAmount() {
		return taskMan.getProjectAmount();
	}

	@Override
	public boolean isOnTime(int projectID) {
		return taskMan.isOnTime(projectID);
	}

	@Override
	public int[] getProjectDelay(int projectID) {
		return taskMan.getProjectDelay(projectID);
	}

	@Override
	public int getTaskAmount(int projectID) {
		return taskMan.getTaskAmount(projectID);
	}

	@Override
	public String getTaskDescription(int projectID, int taskID) {
		return taskMan.getTaskDescription(projectID, taskID);
	}

	@Override
	public boolean hasTaskStarted(int projectID, int taskID) {
		return taskMan.hasTaskStarted(projectID, taskID);
	}

	@Override
	public LocalDateTime getTaskStartTime(int projectID, int taskID) {
		return taskMan.getTaskStartTime(projectID, taskID);
	}

	@Override
	public int getEstimatedTaskDuration(int projectID, int taskID) {
		return taskMan.getEstimatedTaskDuration(projectID, taskID);
	}

	@Override
	public int getAcceptableTaskDeviation(int projectID, int taskID) {
		return taskMan.getAcceptableTaskDeviation(projectID, taskID);
	}

	@Override
	public boolean hasTaskEnded(int projectID, int taskID) {
		return taskMan.hasTaskEnded(projectID, taskID);
	}

	@Override
	public LocalDateTime getTaskEndTime(int projectID, int taskID) {
		return taskMan.getTaskEndTime(projectID, taskID);
	}

	@Override
	public String getTaskStatus(int projectID, int taskID) {
		return taskMan.getTaskStatus(projectID, taskID);
	}

	@Override
	public boolean hasTaskPrerequisites(int projectID, int taskID) {
		return taskMan.hasTaskPrerequisites(projectID, taskID);
	}

	@Override
	public List<Integer> getTaskPrerequisitesFor(int projectID, int taskID) {
		return taskMan.getTaskPrerequisitesFor(projectID, taskID);
	}

	@Override
	public boolean hasTaskAlternative(int projectID, int taskID) {
		return taskMan.hasTaskAlternative(projectID, taskID);
	}

	@Override
	public int getTaskAlternativeTo(int projectID, int taskID) {
		return taskMan.getTaskAlternativeTo(projectID, taskID);
	}

	@Override
	public boolean setTaskFinished(int projectID, int taskID,
			LocalDateTime startTime, LocalDateTime endTime) {
		return taskMan.setTaskFinished(projectID, taskID, startTime, endTime);
	}

	@Override
	public boolean setTaskFailed(int projectID, int taskID,
			LocalDateTime startTime, LocalDateTime endTime) {
		return taskMan.setTaskFailed(projectID, taskID,startTime,endTime);
	}

}
