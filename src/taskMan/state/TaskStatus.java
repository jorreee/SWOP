package taskMan.state;

import java.time.LocalDateTime;

import taskMan.util.Dependant;

/**
 * The status of a task is contained in classes implementing this interface.
 * This follows the state pattern. A task will ask its status to change to another
 * and the implementation will determine if it is possible to change.
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public interface TaskStatus {

	/**
	 * Change the status to an available status
	 * 
	 * @return True if the new status is available, False if it remained
	 *         unchanged
	 */
	public boolean makeAvailable();

	/**
	 * Change the status to an executing status
	 * 
	 * @param beginTime
	 * @return True if the new status is executing, False if it remained
	 *         unchanged
	 */
	public boolean execute(LocalDateTime beginTime);

	/**
	 * Change the status to a finished status
	 * 
	 * @param endTime
	 * @return True if the new status is finished, False if it remained
	 *         unchanged
	 */
	public boolean finish(LocalDateTime endTime);

	/**
	 * Change the status to a failed status
	 * 
	 * @param endTime
	 * @return True if the new status is failed, False if it remained unchanged
	 */
	public boolean fail(LocalDateTime endTime);

	/**
	 * Register a new dependant to the current task
	 * 
	 * @param d
	 *            | The new dependant
	 * @return true if the dependant was added
	 */
	public boolean register(Dependant d);

	/**
	 * Is this state an available state
	 * 
	 * @return true if the state is available
	 */
	public boolean isAvailable();

	/**
	 * Is this state an unavailable state
	 * 
	 * @return true if the state is unavailable
	 */
	public boolean isUnavailable();

	/**
	 * Is this state a finished state
	 * 
	 * @return true if the state is finished
	 */
	public boolean isFinished();

	/**
	 * Is this state a failed state
	 * 
	 * @return true if the state is failed
	 */
	public boolean isFailed();

	/**
	 * Is this state an executing state
	 * 
	 * @return true if the state is executing
	 */
	public boolean isExecuting();

}
