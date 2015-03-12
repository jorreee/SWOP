package taskMan.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

import taskMan.TaskMan;

public class UseCase2CreateProjectTest {

	private TaskMan taskMan;
	private final LocalDateTime startDate = LocalDateTime.of(2015, 2, 9, 8, 0),
			project0StartDateGood = startDate,
			project0DueDateGood = LocalDateTime.of(2015, 2, 13, 23, 59),
			project0DueDateVeryBad1 = LocalDateTime.of(2015, 2, 8, 0, 0),
			project0DueDateVeryBad2 = startDate;

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
