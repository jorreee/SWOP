package company.caretaker;

import initialization.TaskManInitFileChecker;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import company.BranchManager;
import company.taskMan.project.TaskView;
import company.taskMan.resource.ResourceView;
import company.taskMan.task.DelegatingTaskProxy;
import company.taskMan.task.OriginalTaskProxy;
import company.taskMan.task.Task;

/**
 * This class represents a system memento. These mementos contain data
 * representing the system at a specific moment in time. The TaskManCaretaker
 * can choose to discard these mementos if no longer needed, or order them to
 * revert the system to the state stored in the specific memento.
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class TaskManMemento {
	
	private final String taskman;
	
	private final String currentUsername;
	
	private final LocalDateTime currentTime;

	private final Map<Integer, OriginalTaskProxy> originalTaskProxy;
	
	private final List<DelegatedTaskMemento> delegatedTasks;
	
	/**
	 * Construct a memento with a string representation of the system, a user
	 * (the current user), the time and maps and mementos containing data for
	 * re-linking proxies
	 * 
	 * @param taskmanToSave
	 *            | The string representation of the system, comparable to the
	 *            TMAN files used at initialization
	 * @param currentUserLoggedIn
	 *            | The user logged in the system when the memento was saved
	 * @param currentSystemTime
	 *            | The time when the memento was saved
	 * @param originalTaskProxy
	 *            | A map linking delegating tasks to their respective proxies
	 * @param delegatedTasks
	 *            | A data structure linking delegated tasks with their
	 *            respective proxies
	 */
	public TaskManMemento(String taskmanToSave, ResourceView currentUserLoggedIn, LocalDateTime currentSystemTime, Map<Integer, OriginalTaskProxy> originalTaskProxy, List<DelegatedTaskMemento> delegatedTasks) {
		this.taskman = taskmanToSave;
		this.currentUsername = currentUserLoggedIn.getName();
		this.currentTime = currentSystemTime;
		this.originalTaskProxy = originalTaskProxy;
		this.delegatedTasks = delegatedTasks;
	}
	
	/**
	 * Revert the branch to the last memento save.
	 * @param 	branchManager
	 * 			The BranchManager linked with the branch to revert.
	 */
	public void revert(BranchManager branchManager) {

		TaskManInitFileChecker fileChecker = new TaskManInitFileChecker(
				new StringReader(taskman));
		fileChecker.checkFile();

		// Initialize system through a facade
		// reset system time
		branchManager.initializeFromMemento(currentTime, fileChecker);
		
		// Look for new User representing previous "Current User"
		ResourceView currentUser = null;
		for(ResourceView user : branchManager.getPossibleUsers()) {
			if(user.getName().equals(currentUsername)) {
				currentUser = user;
				break;
			}
		}
		
		// Reset Current User
		branchManager.changeToUser(currentUser);
		
		// Reset Delegation links (Original and Delegating lists)
		// Part 1 : Fix originalTaskProxy data in THIS branch
		List<TaskView> delegatingTasks = branchManager.getAllProjects().get(0).getTasks();
		Map<Task, OriginalTaskProxy> newOriginalProxies = new HashMap<>();
		for(Integer i : originalTaskProxy.keySet()) {
			OriginalTaskProxy otp = originalTaskProxy.get(i);
			delegatingTasks.get(i).link(newOriginalProxies, otp);
		}
		
		branchManager.offerOriginalTaskProxies(newOriginalProxies);
		
		// Part 2 : Set delegated task on delegated and re-link the DelegatingTaskProxies in THIS branch
		Map<Task, DelegatingTaskProxy> newDelegatingProxies = new HashMap<>();
		for(DelegatedTaskMemento dtm : delegatedTasks) {
			// find task
			TaskView delegatedTask = branchManager.getAllProjects()
					.get(dtm.getProjectID()).getTasks().get(dtm.getTaskID());
			delegatedTask.link(newDelegatingProxies, dtm.getDelegatingProxy());
		}
		
		branchManager.offerDelegatingTaskProxies(newDelegatingProxies);
		// End initialization
	}
}