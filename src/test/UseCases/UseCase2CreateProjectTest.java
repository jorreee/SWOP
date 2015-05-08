package test.UseCases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

import company.BranchManager;
import company.taskMan.ProjectView;
import userInterface.IFacade;

public class UseCase2CreateProjectTest {

	private IFacade taskManager;
	private final LocalDateTime startDate = LocalDateTime.of(2015, 2, 9, 8, 0),
			project0StartDateGood = startDate,
			project0DueDateGood = LocalDateTime.of(2015, 2, 13, 23, 59),
			project0DueDateVeryBad1 = LocalDateTime.of(2015, 2, 8, 0, 0),
			project0DueDateVeryBad2 = startDate,
			project0DueDateVeryBad3 = null;

	@Before
	public final void initialize() {
		taskManager = new BranchManager(startDate);
	}
	
	@Test
	public void SuccesCaseTest() {
		// Stap 1 en 2 zijn impliciet
		// Stap 3
		assertTrue(taskManager.createProject("Test1", "testing 1", project0StartDateGood, project0DueDateGood));
		ProjectView project0 = taskManager.getProjects().get(0);

		// Stap 4
		assertTrue(project0.getName().equals("Test1"));
		assertTrue(project0.getDescription().equals("testing 1"));
		assertEquals(project0.getAvailableTasks().size(),0);
		assertEquals(project0.getDueTime(),project0DueDateGood);
		assertFalse(project0.isFinished());
		assertEquals(taskManager.getProjects().size(),1);
		
	}
	
	@Test
	public void flow3aTest() {
		// Nothing will be created
		assertEquals(taskManager.getProjects().size(),0);
	}

	@Test
	public void flow4aBadDueTest() {
		// Stap 1 en 2 zijn impliciet
		// Stap 3
		assertFalse(taskManager.createProject("Test1", "testing 1", project0StartDateGood, project0DueDateVeryBad1));
		// Stap 4
		assertEquals(taskManager.getProjects().size(),0);
	}

	@Test
	public void flow4aNoTimeTest() {
		// Stap 1 en 2 zijn impliciet
		// Stap 3
		assertFalse(taskManager.createProject("Test1", "testing 1", project0StartDateGood, project0DueDateVeryBad2));
		// Stap 4
		assertEquals(taskManager.getProjects().size(),0);
	}

	@Test
	public void flow4aNullDueTest() {
		// Stap 1 en 2 zijn impliciet
		// Stap 3
		assertFalse(taskManager.createProject("Test1", "testing 1", project0StartDateGood, project0DueDateVeryBad3));
		// Stap 4
		assertEquals(taskManager.getProjects().size(),0);
	}
	
	

}
