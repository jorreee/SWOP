package taskMan.test;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import taskMan.TaskMan;

public class UseCase2CreateProjectTest {

	private TaskMan taskMan;
	private final LocalDateTime startDate = LocalDateTime.of(2015, 2, 9, 8, 0),
			newDateNoChanges = LocalDateTime.of(2015, 2, 9, 10, 0),
			newDateWithChanges = LocalDateTime.of(2015, 3, 9, 10, 0),
			newDateVeryBad1 = null,
			newDateVeryBad2 = LocalDateTime.of(2015, 2, 8, 0, 0),
			newDateVeryBad3 = startDate,
			project0StartDateGood = startDate,
			project0StartDateVeryBad = LocalDateTime.of(2015, 2, 5, 0, 0),
			project0DueDateGood = LocalDateTime.of(2015, 2, 13, 23, 59),
			project0DueDateVeryBad1 = LocalDateTime.of(2015, 2, 8, 0, 0),
			project0DueDateVeryBad2 = startDate;
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

//		assertTrue(taskMan.createProject("Test1", "testing 1", project0StartDateGood, project0DueDateGood));
//

//		assertTrue(taskMan.createTask(0, "Design system", task00EstDur, task00Dev, -1, task00Dependencies));		// TASK 1
//		task01Dependencies.add(Integer.valueOf(1));
//		assertTrue(taskMan.createTask(0, "Implement Native", task01EstDur, task01Dev, -1, task01Dependencies));		// TASK 2
//		task02Dependencies.add(Integer.valueOf(2));
//		assertTrue(taskMan.createTask(0, "Test code", task02EstDur, task02Dev, -1, task02Dependencies));			// TASK 3
//		task03Dependencies.add(Integer.valueOf(2));
//		assertTrue(taskMan.createTask(0, "Document code", task03EstDur, task03Dev, -1, task03Dependencies));		// TASK 4

	}
	
	@Test
	public void SuccesCaseTest() {
		// Stap 1 en 2 zijn impliciet
		// Stap 3
		assertTrue(taskMan.createProject("Test1", "testing 1", project0StartDateGood, project0DueDateGood));
		// Stap 4
		assertTrue(taskMan.getProjectName(0).equals("Test1"));
		assertTrue(taskMan.getProjectDescription(0).equals("testing 1"));
		assertEquals(taskMan.getAvailableTasks(0).size(),0);
		assertEquals(taskMan.getProjectDueTime(0),project0DueDateGood);
		assertTrue(taskMan.getProjectStatus(0).equals("ongoing"));
		assertTrue(taskMan.isProjectOnTime(0));
		assertEquals(taskMan.getProjectAmount(),1);
		
	}
	
	@Test
	public void flow3aTest() {
		// Nothing will be created
		assertEquals(taskMan.getProjectAmount(),0);
	}

//	@Test //TODO PROBLEM MET DE TMAN
//	public void flow4aTooEarlyTest() {
//		// Stap 1 en 2 zijn impliciet
//		// Stap 3
//		assertFalse(taskMan.createProject("Test1", "testing 1", project0StartDateVeryBad, project0DueDateGood));
//		// Stap 4
//		assertEquals(taskMan.getProjectAmount(),0);
//	}

	@Test
	public void flow4aBadDueTest() {
		// Stap 1 en 2 zijn impliciet
		// Stap 3
		assertFalse(taskMan.createProject("Test1", "testing 1", project0StartDateGood, project0DueDateVeryBad1));
		// Stap 4
		assertEquals(taskMan.getProjectAmount(),0);
	}

	@Test
	public void flow4aNoTimeTest() {
		// Stap 1 en 2 zijn impliciet
		// Stap 3
		assertFalse(taskMan.createProject("Test1", "testing 1", project0StartDateGood, project0DueDateVeryBad2));
		// Stap 4
		assertEquals(taskMan.getProjectAmount(),0);
	}
	
	

}
