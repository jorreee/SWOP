package taskMan;

import java.time.LocalDateTime;
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
	
	public Project() {
		//TODO
	}
	
	public boolean createTask(/*ARGS*/) {
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
		return new ProjectDetails();
	}
	
	public TaskDetails getTaskDetails(int taskID) {
		//TODO
		return getTask(taskID).getTaskDetails();
	}
	
	private Task getTask(int taskID) {
		//TODO
		return null;
	}
	
	public boolean updateTaskDetails(/*ARGS*/) {
		//TODO
		return true;
	}
	
	
}
