package taskMan.util;

import taskMan.Task;

/**
 * This interface determines all methods required in an observer pattern for the
 * OBSERVER part. Both projects and tasks implement this interface. Tasks can be
 * dependant of other tasks (prerequisite tasks), projects are dependant of
 * tasks (a project can only be finished when it has at least one task and all
 * tasks are finished or have a finished alternative).
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public interface Dependant {
	
	/**
	 * A prerequisite has changed, the state of the dependant might change.
	 * 
	 * @param preTask
	 *            | The prerequisite that has changed
	 * @return True if the dependencies were successfully updated, False
	 *         otherwise
	 */
	public boolean updateDependency(Task preTask);

	/**
	 * Get the longest possible duration that a series of dependants (this one and
	 * its dependants) could require to finish (estimated)
	 * 
	 * @return the longest possible duration as a TimeSpan for this dependant and its
	 *         dependants to finish (estimated)
	 */
	public TimeSpan getMaxDelayChain();

}
