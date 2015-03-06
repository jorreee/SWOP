package taskMan;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

public class Project {
	
	private ArrayList<Task> taskList;
	private final String projectName;
	private final String description;
	private final LocalDateTime creationTime;
	private final LocalDateTime dueTime;
	private LocalDateTime endTime;
	private HashMap<Task,Task[]> taskAlternatives;
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

	public boolean createTask(String description, LocalTime estimatedDuration, float acceptableDeviation) {
		Task newTask = new Task(taskList.size(), description, estimatedDuration, acceptableDeviation);
		return taskList.add(newTask);
	}
	
	public boolean createFinishedTask(String description, LocalTime estimatedDuration, float acceptableDeviation, TaskStatus taskStatus, LocalDateTime startTime, LocalDateTime endTime) {
		//TODO
		return true;
	}
	
	public boolean createAlternativeTask(/*ARGS*/) {
		//TODO
		return true;
	}
	
	private boolean hasAlternatives(Project project) {
		return taskAlternatives.containsKey(project);
	}
	
	public int getProjectID() {
		return projectID;
	}
	
	public ProjectDetails getProjectDetails() {
		//TODO
		return null;
	}
	
	public TaskDetails getTaskDetails(int taskID) {
		//TODO
		return getTask(taskID).getTaskDetails();
	}
	
	private Task getTask(int taskID) {
		//TODO
		return null;
	}
	
	public boolean updateTaskDetails(int taskID, LocalDateTime startTime, LocalDateTime endTime, TaskStatus taskStatus) {
		return getTask(taskID).updateTaskDetails(startTime, endTime, taskStatus);
	}
	
	
}
