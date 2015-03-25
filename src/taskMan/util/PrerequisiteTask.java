package taskMan.util;

/**
 * SUBJECT
 * 
 * @author 
 *
 */
public interface PrerequisiteTask {

	public boolean register(DependentTask t);

	public boolean unregister(DependentTask t);

	public boolean notifyDependants();

}
