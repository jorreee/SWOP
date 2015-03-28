package taskMan.util;


/**
 * SUBJECT
 * 
 * @author 
 *
 */
public interface Prerequisite {

	public boolean register(Dependant t);

	public boolean unregister(Dependant t);

	public boolean notifyDependants();

}
