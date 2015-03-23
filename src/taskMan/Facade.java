package taskMan;

import java.time.LocalDateTime;
import java.util.List;

import taskMan.view.ProjectView;
import taskMan.view.TaskView;
import userInterface.IFacade;

public class Facade implements IFacade {
	private final TaskMan taskMan;
	
	public Facade() {
		this.taskMan = new TaskMan();
	}
	
	public Facade(LocalDateTime time) {
		this.taskMan = new TaskMan(time);
	}
	
	@Override
	public LocalDateTime getCurrentTime() { 
		return taskMan.getCurrentTime(); 
	}
	
	@Override
	public boolean createProject(String name, String description, LocalDateTime creationTime, LocalDateTime dueTime) {
		return taskMan.createProject(name, description, creationTime, dueTime);
	}
	
	@Override
	public boolean createProject(String name, String description,
			LocalDateTime dueTime) {
		return taskMan.createProject(name, description, dueTime);
	}
	
	@Override //TODO projectID -> ProjectView
	public boolean createRawTask(int projectID, String description, int estimatedDuration, int acceptableDeviation, String taskStatus, Integer alternativeFor, List<Integer> prerequisiteTasks, LocalDateTime startTime, LocalDateTime endTime) {
		return taskMan.createTask(projectID, description, estimatedDuration, acceptableDeviation, taskStatus, alternativeFor, prerequisiteTasks, startTime, endTime);
	}
	
	@Override //TODO projectID -> ProjectView
	public boolean createTask(ProjectView projectID, String description, int estimatedDuration, int acceptableDeviation, Integer alternativeFor, List<Integer> prerequisiteTasks) {
		return taskMan.createTask(projectID, description, estimatedDuration, acceptableDeviation, alternativeFor, prerequisiteTasks);
	}
	/*
	@Override //TODO kill
	public String getProjectName(int projectID) {
		return taskMan.getProjectName(projectID);
	}
	
	@Override //TODO kill
	public String getProjectDescription(int projectID) {
		return taskMan.getProjectDescription(projectID);
	}
	
	@Override //TODO kill
	public LocalDateTime getProjectCreationTime(int projectID) {
		return taskMan.getProjectCreationTime(projectID);
	}
	
	@Override //TODO kill
	public LocalDateTime getProjectDueTime(int projectID) {
		return taskMan.getProjectDueTime(projectID);
	}
	
	@Override //TODO kill
	public LocalDateTime getProjectEndTime(int projectID) {
		return taskMan.getProjectEndTime(projectID);
	}
	
	@Override //TODO kill
	public String getProjectStatus(int projectID) {
		return taskMan.getProjectStatus(projectID);
	}

	@Override
	public boolean advanceTimeTo(LocalDateTime time) {
		return taskMan.advanceTimeTo(time);
		
	}
	
	@Override// TODO ProjectView,List<TaskView>
	public HashMap<Integer,List<Integer>> getAvailableTasks() {
		return taskMan.getAvailableTasks();
	}

	@Override //TODO kill
	public List<Integer> getAvailableTasks(int projectID) {
		return taskMan.getAvailableTasks(projectID);
	}

	@Override //TODO kill
	public int getProjectAmount() {
		return taskMan.getProjectAmount();
	}

	@Override //TODO review voor CURRENTTIME coupling
	public int[] getProjectDelay(int projectID) {
		return taskMan.getProjectDelay(projectID);
	}

	@Override //TODO kill
	public int getTaskAmount(int projectID) {
		return taskMan.getTaskAmount(projectID);
	}

	@Override //TODO kill
	public String getTaskDescription(int projectID, int taskID) {
		return taskMan.getTaskDescription(projectID, taskID);
	}

//	@Override
//	public boolean hasTaskStarted(int projectID, int taskID) {
//		return taskMan.hasTaskStarted(projectID, taskID);
//	}

	@Override //TODO kill
	public LocalDateTime getTaskStartTime(int projectID, int taskID) {
		return taskMan.getTaskStartTime(projectID, taskID);
	}

	@Override //TODO kill
	public int getEstimatedTaskDuration(int projectID, int taskID) {
		return taskMan.getEstimatedTaskDuration(projectID, taskID);
	}

	@Override //TODO kill
	public int getAcceptableTaskDeviation(int projectID, int taskID) {
		return taskMan.getAcceptableTaskDeviation(projectID, taskID);
	}

	@Override //TODO kill
	public boolean hasTaskEnded(int projectID, int taskID) {
		return taskMan.hasTaskEnded(projectID, taskID);
	}

	@Override //TODO kill
	public LocalDateTime getTaskEndTime(int projectID, int taskID) {
		return taskMan.getTaskEndTime(projectID, taskID);
	}

	@Override //TODO kill
	public String getTaskStatus(int projectID, int taskID) {
		return taskMan.getTaskStatus(projectID, taskID);
	}

	@Override //TODO REFACTOR AS OBESERVER + kill
	public boolean hasTaskPrerequisites(int projectID, int taskID) {
		return taskMan.hasTaskPrerequisites(projectID, taskID);
	}

	@Override  //TODO REFACTOR AS OBSERVER + kill
	public List<Integer> getTaskPrerequisitesFor(int projectID, int taskID) {
		return taskMan.getTaskPrerequisitesFor(projectID, taskID);
	}

	@Override  //TODO REFACTOR AS OBSERVER + kill
	public boolean hasTaskAlternative(int projectID, int taskID) {
		return taskMan.hasTaskAlternative(projectID, taskID);
	}

	@Override  //TODO REFACTOR AS OBSERVER + kill
	public int getTaskAlternativeTo(int projectID, int taskID) {
		return taskMan.getTaskAlternativeTo(projectID, taskID);
	}

	@Override //TODO IDs -> Views
	public boolean setTaskFinished(int projectID, int taskID,
			LocalDateTime startTime, LocalDateTime endTime) {
		return taskMan.setTaskFinished(projectID, taskID, startTime, endTime);
	}

	@Override //TODO IDs -> Views
	public boolean setTaskFailed(int projectID, int taskID,
			LocalDateTime startTime, LocalDateTime endTime) {
		return taskMan.setTaskFailed(projectID, taskID,startTime,endTime);
	}

	@Override //TODO kill
	public boolean isTaskUnacceptableOverdue(int projectID, int taskID) {
		return taskMan.isTaskUnacceptableOverdue(projectID, taskID);
	}

	@Override //TODO kill
	public boolean isTaskOnTime(int projectID, int taskID) {
		return taskMan.isTaskOnTime(projectID, taskID);
	}

	@Override //TODO kill
	public int getTaskOverTimePercentage(int projectID, int taskID) {
		return taskMan.getTaskOverTimePercentage(projectID, taskID);
	}

	@Override //TODO kill
	public boolean isProjectFinished(int projectID) {
		return taskMan.isProjectFinished(projectID);
	}

	@Override //TODO kill
	public boolean isProjectEstimatedOnTime(int projectID) {
		return taskMan.isProjectEstimatedOnTime(projectID);
	}

	@Override //TODO kill
	public int[] getEstimatedProjectDelay(int projectID) {
		return taskMan.getEstimatedProjectDelay(projectID);
	}
	*/

	@Override
	public boolean advanceTimeTo(LocalDateTime time) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setTaskFinished(ProjectView projectID, TaskView taskID,
			LocalDateTime startTime, LocalDateTime endTime) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setTaskFailed(ProjectView projectID, TaskView taskID,
			LocalDateTime startTime, LocalDateTime endTime) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<ProjectView> getProjects() {
		return taskMan.getProjects();
	}

}
