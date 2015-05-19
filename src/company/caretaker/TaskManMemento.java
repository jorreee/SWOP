package company.caretaker;

import initialization.TaskManInitFileChecker;

import java.io.StringReader;
import java.time.LocalDateTime;

import company.BranchManager;
import company.taskMan.resource.ResourceView;

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
	
	private final String currentUsername;
	
	private final LocalDateTime currentTime;

	// TODO add delegation TO and FROM links for rebuilding
	
	/**
	 * Construct a memento with a specified string (a TMAN)
	 * 
	 * @param taskmanToSave
	 *            | A string in the format of a TMAN file, the files used for
	 *            initialization at system startup
	 */
	public TaskManMemento(String taskmanToSave, ResourceView currentUserLoggedIn, LocalDateTime currentSystemTime) {
		this.taskman = taskmanToSave;
		this.currentUsername = currentUserLoggedIn.getName();
		this.currentTime = currentSystemTime;
	}
	
	public void revert(BranchManager branch) {

		TaskManInitFileChecker fileChecker = new TaskManInitFileChecker(
				new StringReader(taskman));
		fileChecker.checkFile();

		// Initialize system through a facade
		// reset system time
		branch.initializeFromMemento(currentTime, fileChecker);
		
		// Look for new User representing previous "Current User"
		ResourceView currentUser = null;
		for(ResourceView user : branch.getPossibleUsers()) {
			if(user.getName().equals(currentUsername)) {
				currentUser = user;
				break;
			}
		}
		
		// Reset Current User
		branch.changeToUser(currentUser);
		
		// Reset Delegation links (TO and FROM list)
		// TODO
		
		// End initialization
	}
}
