package taskMan.test;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.LocalTime;
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
			project1StartDate = startDate,
			project1DueDate = LocalDateTime.of(2015, 2, 13, 23, 59);
	private final int task11EstDur = 8*60,
			task12EstDur = 16*60,
			task13EstDur = 8*60,
			task14EstDur = 8*60;
	private final int task11Dev = 0,
			task12Dev = 50,
			task13Dev = 0,
			task14Dev = 0;
	private final ArrayList<Integer> task11Dependencies = new ArrayList(),
									 task12Dependencies = new ArrayList(),
									 task13Dependencies = new ArrayList(),
									 task14Dependencies = new ArrayList();

	/**
	 * DEFAULT TASKMAN TESTER
	 * - project 1 START 10 feb DUE 15 feb (midnight)
	 * 		task 1			
	 * 		task 2 <- 1
	 * 		task 3 <- 2
	 * 		task 4 <- 2
	 */
	@Before
	public final void initialize() {
		taskMan = new TaskMan(startDate);

		taskMan.createProject("Test1", "testing 1", project1StartDate, project1DueDate);

		
		taskMan.createTask(1, "Design system", task11EstDur, task11Dev, null, task11Dependencies);		// TASK 1
		task12Dependencies.add(Integer.valueOf(1));
		taskMan.createTask(1, "Implement Native", task12EstDur, task12Dev, null, task12Dependencies);	// TASK 2
		task13Dependencies.add(Integer.valueOf(2));
		taskMan.createTask(1, "Test code", task13EstDur, task13Dev, null, task13Dependencies);			// TASK 3
		task14Dependencies.add(Integer.valueOf(2));
		taskMan.createTask(1, "Document code", task14EstDur, task14Dev, null, task14Dependencies);		// TASK 4

	}

	@Test
	public void SuccesCaseNoChangesTest() {
		// Step 1 and 2 are implicit
		// Step 3 assumption: the user inputs CORRECT data
		taskMan.advanceTimeTo(newDateNoChanges);
		assertEquals(taskMan.getCurrentTime(),newDateNoChanges);
		// Step 4
		assertTrue(taskMan.getTaskStatus(1,1).equals("available"));
		assertTrue(taskMan.getTaskStatus(1,2).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,3).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,4).equals("unavailable"));
		assertEquals(taskMan.getProjectDelay(1),0);
	}

	@Test
	public void SuccesCaseWithChangesTest() {
		// Step 1 and 2 are implicit
		// Step 3 assumption: the user inputs CORRECT data
		taskMan.advanceTimeTo(newDateWithChanges);
		assertEquals(taskMan.getCurrentTime(),newDateWithChanges);
		// Step 4
		assertTrue(taskMan.getTaskStatus(1,1).equals("available"));
		assertTrue(taskMan.getTaskStatus(1,2).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,3).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,4).equals("unavailable"));
		assertTrue(taskMan.getProjectDelay(1) > 0); 					//CHANGED
		// Step 4
	}

	@Test
	public void flow3aTest() {
		// Step 1 and 2 are implicit
		// Step 3 assumption: the user inputs NO data
		taskMan.advanceTimeTo(newDateVeryBad1);
		assertEquals(taskMan.getCurrentTime(),startDate);
		// Step 4
		assertTrue(taskMan.getTaskStatus(1,1).equals("available"));
		assertTrue(taskMan.getTaskStatus(1,2).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,3).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,4).equals("unavailable"));
		assertEquals(taskMan.getProjectDelay(1),0);
		// Step 4
	}

	@Test
	public void flow4aTest() {
		// Step 1 and 2 are implicit
		// Step 3 assumption: the user inputs INVALID data
		taskMan.advanceTimeTo(newDateVeryBad2);
		assertEquals(taskMan.getCurrentTime(),startDate);
		// Step 4
		assertTrue(taskMan.getTaskStatus(1,1).equals("available"));
		assertTrue(taskMan.getTaskStatus(1,2).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,3).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,4).equals("unavailable"));
		assertEquals(taskMan.getProjectDelay(1),0);
		
		 //-----------------------------------------------------------
		
		taskMan.advanceTimeTo(newDateVeryBad3);
		assertEquals(taskMan.getCurrentTime(),startDate);
		// Step 4
		assertTrue(taskMan.getTaskStatus(1,1).equals("available"));
		assertTrue(taskMan.getTaskStatus(1,2).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,3).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,4).equals("unavailable"));
		assertEquals(taskMan.getProjectDelay(1),0);
	}

}
