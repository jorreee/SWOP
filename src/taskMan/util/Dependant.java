package taskMan.util;

/**
 * OBSERVER
 * 
 * @author 
 *
 */
public interface Dependant {
	
	public boolean updateDependency(Prerequisite preTask); 
	// TODO kan ook "heyDependant,I'mFinishedSoGoAhead()" noemen

}
