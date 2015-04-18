package initSaveRestore.caretaker;

/**
 * This class represents a system memento. These mementos contain data
 * representing the system at a specific moment in time. The TaskManCaretaker is
 * capable of using these mementos in order to save and restore the system
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class TaskManMemento {
	
	private final String taskman;

	/**
	 * Construct a memento with a specified string (a TMAN)
	 * 
	 * @param taskmanToSave
	 *            | A string in the format of a TMAN file, the files used for
	 *            initialization at system startup
	 */
	public TaskManMemento(String taskmanToSave) {
		this.taskman = taskmanToSave;
	}
	
	/**
	 * Retrieve the TMAN file present in this memento
	 * 
	 * @return The string stored in the memento
	 */
	public String getMementoAsString() {
		return taskman;
	}

}
