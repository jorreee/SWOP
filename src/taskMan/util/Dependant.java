package taskMan.util;

import taskMan.Task;

/**
 * OBSERVER
 * 
 * @author 
 *
 */
public interface Dependant {
	
	public boolean updateDependency(Task preTask); 
	// TODO kan ook "heyDependant,I'mFinishedSoGoAhead()" noemen

}
