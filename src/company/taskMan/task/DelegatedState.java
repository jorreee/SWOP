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
	public boolean makeAvailable(Task task) {
		return false;
	}

	@Override
	public boolean execute(Task task, LocalDateTime beginTime) {
		return false;
	}

	@Override
	public boolean finish(Task task, LocalDateTime endTime) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean fail(Task task, LocalDateTime endTime) {
		// TODO Auto-generated method stub
		return false;
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
