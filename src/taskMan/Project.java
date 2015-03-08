package taskMan;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Project {
	
	private ArrayList<Task> taskList;
	private final String projectName;
	private final String description;
	private final LocalDateTime creationTime;
	private final LocalDateTime dueTime;
	private LocalDateTime endTime;
	private HashMap<Task,Task> taskAlternatives;
	private ProjectStatus projectStatus;
	private final int projectID;
	
	public Project(int projectID, String projectName, String description,
			LocalDateTime creationTime, LocalDateTime dueTime) {
		this.projectName = projectName;
		this.description = description;
		this.creationTime = creationTime;
		this.dueTime = dueTime;
		this.projectID = projectID;
	}

	public boolean createTask(String description, LocalTime estimatedDuration, float acceptableDeviation, String taskStatus, Integer alternativeFor, List<Integer> prerequisiteTasks, LocalDateTime startTime, LocalDateTime endTime) {
		// TODO Check for nulls and use correct constructor
		Task newTask = new Task(taskList.size(), description, estimatedDuration, acceptableDeviation, taskStatus, startTime, endTime);
		//TODO put alternative and prerequisite in lists
		return taskList.add(newTask);
	}
	
	private boolean hasFinishedAlternative(Task task) {
		if(task == null)
			return false;
		return taskAlternatives.get(task).isFinished() || hasFinishedAlternative(taskAlternatives.get(task));
	}
		
	public int getProjectID() {
		return projectID;
	}
	
	/**
	 * This method will adjust the status of the project, depending on its tasks.
	 * If the project has at least one task and all of those tasks are finished (or failed with a finished alternative),
	 * the project itself will be considered finished.
	 */
	private void recalcultateProjectStatus() {
		for(Task task : taskList) {
			TaskStatus status = task.getTaskStatus();
			if( status == TaskStatus.AVAILABLE || status == TaskStatus.UNAVAILABLE)
				return;
			if( status == TaskStatus.FAILED) {
				if(!hasFinishedAlternative(task))
					return;
			}
		}
		this.projectStatus = ProjectStatus.FINISHED;
	}
	
	/*
	public ProjectDetails getProjectDetails() {
		//TODO
		return null;
	}
	
	public TaskDetails getTaskDetails(int taskID) {
		//TODO
		return getTask(taskID).getTaskDetails();
	}*/
	
	private Task getTask(int taskID) {
		return taskList.get(taskID);
	}
	
	public boolean updateTaskDetails(int taskID, LocalDateTime startTime, LocalDateTime endTime, String taskStatus) {
		return getTask(taskID).updateTaskDetails(startTime, endTime, taskStatus);
	}

	public String getProjectName() { return projectName; }
	public String getProjectDescription() {	return description;	}
	public LocalDateTime getProjectCreationTime() { return creationTime; }
	public LocalDateTime getProjectDueTime() { return dueTime; }
	public LocalDateTime getProjectEndTime() { return endTime; }
	public String getProjectStatus() { return projectStatus.name();	}
	
}
