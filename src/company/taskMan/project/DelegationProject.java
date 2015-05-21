package company.taskMan.project;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import company.taskMan.resource.ResourceManager;
import company.taskMan.resource.ResourceView;
import company.taskMan.task.DelegatingTask;
import company.taskMan.task.Task;

public class DelegationProject extends Project {
	
	public DelegationProject() {
		super("Delegation Project", "A hidden project to collect delegated tasks", LocalDateTime.now(), LocalDateTime.now().plusMinutes(1));
	}
	
	/**
	 * When a task is delegated again, the reference will be removed from this
	 * project
	 */
	@Override
	public void removeTask(TaskView task) {
		taskList.remove(task.unwrap());
	}
	
	/**
	 * Creates a new Task, delegated from another branch.
	 * 
	 * @param description
	 *            The description of the given Task.
	 * @param estimatedDuration
	 *            The estimated duration of the Task.
	 * @param acceptableDeviation
	 *            The acceptable deviation of the Task.
	 * @param resMan
	 *            | The resource manager
	 * @param prerequisiteTasks
	 *            The prerequisites Tasks for this Task.
	 * @param requiredResources
	 *            | The required resources for the Task.
	 * @param alternativeFor
	 *            The Task this new task will replace.
	 * @param taskStatus
	 *            The Status of the Task, should be null
	 * @param startTime
	 *            The start time of the Task, should be null
	 * @param endTime
	 *            The end time of the Task, should be null
	 * @param plannedStartTime
	 *            | The planned start time of the Task, should be null
	 * @param plannedDevelopers
	 *            | The assigned developers of the Task, should be null
	 * @throws IllegalArgumentException, IllegalStateException, ResourceUnavailableException 
	 */
	@Override
	public void createTask(String description, 
						int estimatedDuration, 
						int acceptableDeviation, 
						ResourceManager resMan, 
						List<TaskView> prerequisiteTasks, 
						Map<ResourceView, Integer> requiredResources, 
						TaskView alternativeFor, 
						String taskStatus,
						LocalDateTime startTime, 
						LocalDateTime endTime,
						LocalDateTime plannedStartTime,
						List<ResourceView> plannedDevelopers) {
		
		Task newTask = new DelegatingTask(description, 
					estimatedDuration, 
					acceptableDeviation, 
					resMan, 
					requiredResources,
					null);
		taskList.add(newTask);
		setProjectStatus(new OngoingState());
		newTask.register(this);
	}
	
}
