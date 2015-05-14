package company.caretaker;

import initialization.TaskManInitFileChecker;

import java.io.StringReader;
import java.time.LocalDateTime;

import userInterface.Main;
import company.BranchManager;
import company.taskMan.resource.user.User;

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
	
	private final User currentUser;
	
	private final LocalDateTime currentTime;

	/**
	 * Construct a memento with a specified string (a TMAN)
	 * 
	 * @param taskmanToSave
	 *            | A string in the format of a TMAN file, the files used for
	 *            initialization at system startup
	 */
	public TaskManMemento(String taskmanToSave, User currentUserLoggedIn, LocalDateTime currentSystemTime) {
		this.taskman = taskmanToSave;
		this.currentUser = currentUserLoggedIn;
		this.currentTime = currentSystemTime;
	}
	
	public void revert(BranchManager branch) {

		TaskManInitFileChecker fileChecker = new TaskManInitFileChecker(
				new StringReader(taskman));
		fileChecker.checkFile();

		LocalDateTime systemTime = fileChecker.getSystemTime();
		
		// Initialize system through a facade
		// Set system time
		branch.initializeFromMemento(currentTime);
		
		Main.initializeBranch(branch, fileChecker);
		
		// End initialization
	}

}
