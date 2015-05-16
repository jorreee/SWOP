package company.taskMan.project;

import java.time.LocalDateTime;

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
}
