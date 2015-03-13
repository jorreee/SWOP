package taskMan.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import taskMan.Facade;
import userInterface.IFacade;

public class UseCase5AdvanceTimeTest {

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
	private final ArrayList<Integer> task00Dependencies = new ArrayList<Integer>(),
			task01Dependencies = new ArrayList<Integer>(),
			task02Dependencies = new ArrayList<Integer>(),
			task03Dependencies = new ArrayList<Integer>();

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


		assertTrue(taskManager.createTask(0, "Design system", task00EstDur, task00Dev, -1, task00Dependencies));		// TASK 1
		task01Dependencies.add(Integer.valueOf(0));
		assertTrue(taskManager.createTask(0, "Implement Native", task01EstDur, task01Dev, -1, task01Dependencies));		// TASK 2
		task02Dependencies.add(Integer.valueOf(1));
		assertTrue(taskManager.createTask(0, "Test code", task02EstDur, task02Dev, -1, task02Dependencies));			// TASK 3
		task03Dependencies.add(Integer.valueOf(1));
		assertTrue(taskManager.createTask(0, "Document code", task03EstDur, task03Dev, -1, task03Dependencies));		// TASK 4

	}

	@Test
	public void SuccesCaseNoChangesTest() {
		// Step 1 and 2 are implicit
		// Step 3 assumption: the user inputs CORRECT data
		assertTrue(taskManager.advanceTimeTo(newDateNoChanges));
		assertEquals(taskManager.getCurrentTime(),newDateNoChanges);
		// Step 4
		assertTrue(taskManager.getTaskStatus(0,0).equals("available"));
		assertTrue(taskManager.getTaskStatus(0,1).equals("unavailable"));
		assertTrue(taskManager.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskManager.getTaskStatus(0,3).equals("unavailable"));
	}

	@Test
	public void SuccesCaseWithChangesTest() {
		// Step 1 and 2 are implicit
		// Step 3 assumption: the user inputs CORRECT data
		assertTrue(taskManager.advanceTimeTo(newDateWithChanges));
		assertEquals(taskManager.getCurrentTime(),newDateWithChanges);
		// Step 4
		assertTrue(taskManager.getTaskStatus(0,0).equals("available"));
		assertTrue(taskManager.getTaskStatus(0,1).equals("unavailable"));
		assertTrue(taskManager.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskManager.getTaskStatus(0,3).equals("unavailable"));
	}

	@Test
	public void flow3aTest() {
		// Step 1 and 2 are implicit
		// Step 3 assumption: the user inputs NO data
		assertFalse(taskManager.advanceTimeTo(newDateVeryBad1));
		assertEquals(taskManager.getCurrentTime(),startDate);
		// Step 4
		assertTrue(taskManager.getTaskStatus(0,0).equals("available"));
		assertTrue(taskManager.getTaskStatus(0,1).equals("unavailable"));
		assertTrue(taskManager.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskManager.getTaskStatus(0,3).equals("unavailable"));
	}

	@Test
	public void flow4aTest() {
		// Step 1 and 2 are implicit
		// Step 3 assumption: the user inputs INVALID data
		assertFalse(taskManager.advanceTimeTo(newDateVeryBad2));
		assertEquals(taskManager.getCurrentTime(),startDate);
		// Step 4
		assertTrue(taskManager.getTaskStatus(0,0).equals("available"));
		assertTrue(taskManager.getTaskStatus(0,1).equals("unavailable"));
		assertTrue(taskManager.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskManager.getTaskStatus(0,3).equals("unavailable"));

		//-----------------------------------------------------------

		assertFalse(taskManager.advanceTimeTo(newDateVeryBad3));
		assertEquals(taskManager.getCurrentTime(),startDate);
		// Step 4
		assertTrue(taskManager.getTaskStatus(0,0).equals("available"));
		assertTrue(taskManager.getTaskStatus(0,1).equals("unavailable"));
		assertTrue(taskManager.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskManager.getTaskStatus(0,3).equals("unavailable"));
	}

}
