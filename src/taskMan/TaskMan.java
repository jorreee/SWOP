package taskMan;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import com.google.common.collect.ImmutableList;

public class TaskMan {
	
	private ArrayList<Project> projectList;
	private LocalDateTime currentTime;
	//private Project currentProject;
	
	public void advanceTime(LocalTime time) {
		//TODO
	}
	
	public ImmutableList<ProjectDetails> getProjectDetails() {
		ArrayList<ProjectDetails> projectDetails = new ArrayList<ProjectDetails>();
		for(Project project : projectList)
			projectDetails.add(project.getProjectDetails());
		return ImmutableList.copyOf(projectDetails);
	}
	
	public TaskDetails getTaskDetails(int projectID, int taskID) {
		return getProject(projectID).getTaskDetails(taskID); 
	}
	
	private Project getProject(int projectID) {
		//TODO
		return null;
	}
	
	public LocalDateTime getCurrentTime() { return currentTime; }
	
	public boolean createProject(/*ARGS*/) {
		//TODO
		return true;
	}
	
	public boolean updateProject(/*ARGS*/) {
		//TODO
		return true;
	}
	
	public boolean createTask(/*ARGS*/) {
		//TOOO
		return true;
	}
	
	public boolean updateTaskDetails(/*ARGS*/) {
		//TODO
		return true;
	}
	
}
