package company.taskMan.task;

import java.time.LocalDateTime;


/**
 * This class represents the delegated state of a task
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class DelegatedState implements TaskStatus {

	@Override
	public void makeAvailable(Task task) { }

	@Override
	public void delegate(Task task, Task newTask)
			throws IllegalArgumentException, IllegalStateException {
		if(newTask == null) {
			throw new IllegalArgumentException("newTask must not be null");
		}
		if(newTask.equals(task)) {
//			Task oldDelegator = task.getDelegatingTask();
//			task.setDelegatingTask(null);

			newTask.unregister(task); //TODO hier kan nullpointer komen als de delegating task eerst wordt verwijderd
			task.setTaskStatus(new UnavailableState());
			
		} else {
//			Task oldDelegator = task.getDelegatingTask();
//			oldDelegator.unregister(task); //TODO hier kan nullpointer komen als de delegating task eerst wordt verwijderd
//			task.setDelegatingTask(newTask);
//			try {
//				newTask.setOriginalDelegatedTask(task);
//			} catch(IllegalStateException e) {
//				task.setDelegatingTask(oldDelegator);
//				throw e;
//			}
			//TODO unregisteren van oude delegatingTask?
			newTask.register(task); 
			
		}
	}

	@Override
	public void execute(Task task, LocalDateTime beginTime) 
			throws IllegalArgumentException, IllegalStateException { 
		throw new IllegalStateException("This task is delegated and you can't access it anymore");
	}

	@Override
	public void finish(Task task, LocalDateTime endTime) 
			throws IllegalArgumentException, IllegalStateException { 
		throw new IllegalStateException("This task is delegated and you don't have control anymore");
	}
	
	@Override
	public void fail(Task task, LocalDateTime endTime) 
			throws IllegalArgumentException, IllegalStateException { 
		throw new IllegalStateException("This task is delegated and you don't have control anymore");
	}

	@Override
	public void register(Task task, Dependant d) {
		task.addDependant(task);
	}

	@Override
	public boolean isAvailable() {
		return false;
	}

	@Override
	public boolean isUnavailable() {
		return false;
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	@Override
	public boolean isFailed() {
		return false;
	}

	@Override
	public boolean isExecuting() {
		return false;
	}
	
	@Override
	public boolean isDelegated(){
		return true;
	}
	
	@Override
	public String toString() {
		return "Delegated";
	}

}
