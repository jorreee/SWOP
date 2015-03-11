package taskMan.test;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import taskMan.TaskMan;

public class UseCase5AdvanceTimeTest {

	private TaskMan taskMan;
	private final LocalDateTime startDate = LocalDateTime.of(2015, 2, 9, 8, 0),
			newDateNoChanges = LocalDateTime.of(2015, 2, 9, 10, 0),
			newDateWithChanges = LocalDateTime.of(2015, 3, 9, 10, 0),
			newDateVeryBad1 = null,
			newDateVeryBad2 = LocalDateTime.of(2015, 2, 8, 0, 0),
			newDateVeryBad3 = startDate,
			project0StartDate = startDate,
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
	 * DEFAULT TASKMAN TESTER
	 * - project 1 START 9 feb DUE 13 feb (midnight)
	 * 		task 1			
	 * 		task 2 <- 1
	 * 		task 3 <- 2
	 * 		task 4 <- 2
	 */
	@Before
	public final void initialize() {
		taskMan = new TaskMan(startDate);

		assertTrue(taskMan.createProject("Test1", "testing 1", project0StartDate, project0DueDate));


		assertTrue(taskMan.createTask(0, "Design system", task00EstDur, task00Dev, -1, task00Dependencies));		// TASK 1
		task01Dependencies.add(Integer.valueOf(1));
		assertTrue(taskMan.createTask(0, "Implement Native", task01EstDur, task01Dev, -1, task01Dependencies));	// TASK 2
		task02Dependencies.add(Integer.valueOf(2));
		assertTrue(taskMan.createTask(0, "Test code", task02EstDur, task02Dev, -1, task02Dependencies));			// TASK 3
		task03Dependencies.add(Integer.valueOf(2));
		assertTrue(taskMan.createTask(0, "Document code", task03EstDur, task03Dev, -1, task03Dependencies));		// TASK 4

	}

	@Test
	public void SuccesCaseNoChangesTest() {
		// Step 1 and 2 are implicit
		// Step 3 assumption: the user inputs CORRECT data
		assertTrue(taskMan.advanceTimeTo(newDateNoChanges));
		assertEquals(taskMan.getCurrentTime(),newDateNoChanges);
		// Step 4
		assertTrue(taskMan.getTaskStatus(0,0).equals("available"));
		assertTrue(taskMan.getTaskStatus(0,1).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(0,3).equals("unavailable"));
		assertEquals(taskMan.getProjectDelay(0),0);
	}

	@Test
	public void SuccesCaseWithChangesTest() {
		// Step 1 and 2 are implicit
		// Step 3 assumption: the user inputs CORRECT data
		assertTrue(taskMan.advanceTimeTo(newDateWithChanges));
		assertEquals(taskMan.getCurrentTime(),newDateWithChanges);
		// Step 4
		assertTrue(taskMan.getTaskStatus(0,0).equals("available"));
		assertTrue(taskMan.getTaskStatus(0,1).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(0,3).equals("unavailable"));
		assertTrue(taskMan.getProjectDelay(0) > 0); 					//CHANGED
		// Step 4
	}

	@Test
	public void flow3aTest() {
		// Step 1 and 2 are implicit
		// Step 3 assumption: the user inputs NO data
		assertFalse(taskMan.advanceTimeTo(newDateVeryBad1));
		assertEquals(taskMan.getCurrentTime(),startDate);
		// Step 4
		assertTrue(taskMan.getTaskStatus(0,0).equals("available"));
		assertTrue(taskMan.getTaskStatus(0,1).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(0,3).equals("unavailable"));
		assertEquals(taskMan.getProjectDelay(0),0);
		// Step 4
	}

	@Test
	public void flow4aTest() {
		// Step 1 and 2 are implicit
		// Step 3 assumption: the user inputs INVALID data
		assertFalse(taskMan.advanceTimeTo(newDateVeryBad2));
		assertEquals(taskMan.getCurrentTime(),startDate);
		// Step 4
		assertTrue(taskMan.getTaskStatus(0,0).equals("available"));
		assertTrue(taskMan.getTaskStatus(0,1).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(0,3).equals("unavailable"));
		assertEquals(taskMan.getProjectDelay(0),0);

		//-----------------------------------------------------------

		assertFalse(taskMan.advanceTimeTo(newDateVeryBad3));
		assertEquals(taskMan.getCurrentTime(),startDate);
		// Step 4
		assertTrue(taskMan.getTaskStatus(0,0).equals("available"));
		assertTrue(taskMan.getTaskStatus(0,1).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(0,3).equals("unavailable"));
		assertEquals(taskMan.getProjectDelay(0),0);
	}

}
