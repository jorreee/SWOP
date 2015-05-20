package company.taskMan.task;

import company.taskMan.util.TimeSpan;

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
	 * Update dependency of this Dependant on preTask. 
	 * 
	 * @param preTask
	 *            | The prerequisite that has changed
	 * @throws IllegalStateException
	 * 				| if preTask isn't a Dependant
	 */
	public void updateDependencyFinished(Task preTask) throws IllegalStateException;

	/**
	 * Get the longest possible duration that a series of dependants (this one and
	 * its dependants) could require to finish (estimated)
	 * 
	 * @return the longest possible duration as a TimeSpan for this dependant and its
	 *         dependants to finish (estimated)
	 */
	public TimeSpan getMaxDelayChain();

}
