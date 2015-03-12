package taskMan.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

import taskMan.Facade;
import userInterface.IFacade;

public class UseCase2CreateProjectTest {

	private IFacade taskManager;
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
		taskManager = new Facade(startDate);
	}
	
	@Test
	public void SuccesCaseTest() {
		// Stap 1 en 2 zijn impliciet
		// Stap 3
		assertTrue(taskManager.createProject("Test1", "testing 1", project0StartDateGood, project0DueDateGood));
		// Stap 4
		assertTrue(taskManager.getProjectName(0).equals("Test1"));
		assertTrue(taskManager.getProjectDescription(0).equals("testing 1"));
		assertEquals(taskManager.getAvailableTasks(0).size(),0);
		assertEquals(taskManager.getProjectDueTime(0),project0DueDateGood);
		assertTrue(taskManager.getProjectStatus(0).equals("ongoing"));
		assertTrue(taskManager.isProjectEstimatedOnTime(0));
		assertEquals(taskManager.getProjectAmount(),1);
		
	}
	
	@Test
	public void flow3aTest() {
		// Nothing will be created
		assertEquals(taskManager.getProjectAmount(),0);
	}

	@Test
	public void flow4aBadDueTest() {
		// Stap 1 en 2 zijn impliciet
		// Stap 3
		assertFalse(taskManager.createProject("Test1", "testing 1", project0StartDateGood, project0DueDateVeryBad1));
		// Stap 4
		assertEquals(taskManager.getProjectAmount(),0);
	}

	@Test
	public void flow4aNoTimeTest() {
		// Stap 1 en 2 zijn impliciet
		// Stap 3
		assertFalse(taskManager.createProject("Test1", "testing 1", project0StartDateGood, project0DueDateVeryBad2));
		// Stap 4
		assertEquals(taskManager.getProjectAmount(),0);
	}
	
	

}
