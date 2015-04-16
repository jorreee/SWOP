package taskMan.test.UseCases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import taskMan.Facade;
import taskMan.view.ProjectView;
import taskMan.view.TaskView;
import userInterface.IFacade;

public class UseCase7AdvanceTimeTest {

	private IFacade taskManager;
	private final LocalDateTime startDate = LocalDateTime.of(2015, 2, 9, 8, 0),
			newDateNoChanges = LocalDateTime.of(2015, 2, 9, 10, 0),
			newDateWithChanges = LocalDateTime.of(2015, 3, 9, 10, 0),
			newDateVeryBad1 = null,
			newDateVeryBad2 = LocalDateTime.of(2015, 2, 8, 0, 0),
			newDateVeryBad3 = startDate,
			project0DueDate = LocalDateTime.of(2015, 2, 13, 23, 59);
	private final int task00EstDur = 8*60,
			task01EstDur = 16*60,
			task02EstDur = 8*60,
			task03EstDur = 8*60;
	private final int task00Dev = 0,
			task01Dev = 50,
			task02Dev = 0,
			task03Dev = 0;
	private final ArrayList<TaskView> task00Dependencies = new ArrayList<TaskView>(),
			task01Dependencies = new ArrayList<TaskView>(),
			task02Dependencies = new ArrayList<TaskView>(),
			task03Dependencies = new ArrayList<TaskView>();

	/**
	 * - project 0 START 9 feb 8u DUE 13 feb midnight
	 * 		task 0			
	 * 		task 1 <- 0
	 * 		task 2 <- 1
	 * 		task 3 <- 1
	 */
	@Before
	public final void initialize() {
		taskManager = new Facade(startDate);

		assertTrue(taskManager.createProject("Test1", "testing 1", project0DueDate));
		ProjectView project0 = taskManager.getProjects().get(0);

		assertTrue(taskManager.createTask(project0, "Design system", task00EstDur, task00Dev, task00Dependencies, null));		// TASK 1
		task01Dependencies.add(project0.getTasks().get(0));
		assertTrue(taskManager.createTask(project0, "Implement Native", task01EstDur, task01Dev, task01Dependencies, null));	// TASK 2
		task02Dependencies.add(project0.getTasks().get(1));
		assertTrue(taskManager.createTask(project0, "Test code", task02EstDur, task02Dev, task02Dependencies, null));			// TASK 3
		task03Dependencies.add(project0.getTasks().get(1));
		assertTrue(taskManager.createTask(project0, "Document code", task03EstDur, task03Dev, task03Dependencies, null));		// TASK 4

	}

	@Test
	public void SuccesCaseNoChangesTest() {
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);
		TaskView task03 = project0.getTasks().get(3);
		// Step 1 and 2 are implicit0
		// Step 3 assumption: the user inputs CORRECT data
		assertTrue(taskManager.advanceTimeTo(newDateNoChanges));
		assertEquals(taskManager.getCurrentTime(),newDateNoChanges);
		// Step 4
		assertTrue(task00.getStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task03.getStatusAsString().equalsIgnoreCase("unavailable"));
	}

	@Test
	public void SuccesCaseWithChangesTest() {
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);
		TaskView task03 = project0.getTasks().get(3);
		// Step 1 and 2 are implicit
		// Step 3 assumption: the user inputs CORRECT data
		assertTrue(taskManager.advanceTimeTo(newDateWithChanges));
		assertEquals(taskManager.getCurrentTime(),newDateWithChanges);
		// Step 4
		assertTrue(task00.getStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task03.getStatusAsString().equalsIgnoreCase("unavailable"));
	}

	@Test
	public void flow3aTest() {
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);
		TaskView task03 = project0.getTasks().get(3);
		// Step 1 and 2 are implicit
		// Step 3 assumption: the user inputs NO data
		assertFalse(taskManager.advanceTimeTo(newDateVeryBad1));
		assertEquals(taskManager.getCurrentTime(),startDate);
		// Step 4
		assertTrue(task00.getStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task03.getStatusAsString().equalsIgnoreCase("unavailable"));
	}

	@Test
	public void flow4aTest() {
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);
		TaskView task03 = project0.getTasks().get(3);
		// Step 1 and 2 are implicit
		// Step 3 assumption: the user inputs INVALID data
		assertFalse(taskManager.advanceTimeTo(newDateVeryBad2));
		assertEquals(taskManager.getCurrentTime(),startDate);
		// Step 4
		assertTrue(task00.getStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task03.getStatusAsString().equalsIgnoreCase("unavailable"));

		//-----------------------------------------------------------

		assertFalse(taskManager.advanceTimeTo(newDateVeryBad3));
		assertEquals(taskManager.getCurrentTime(),startDate);
		// Step 4
		assertTrue(task00.getStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task03.getStatusAsString().equalsIgnoreCase("unavailable"));
	}

}
