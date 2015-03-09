package taskMan.test;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import taskMan.TaskMan;

public class UseCase3CreateTaskTest {

	private TaskMan taskMan;
	private final LocalDateTime startDate = LocalDateTime.of(2015, 2, 9, 8, 0),
			project1StartDate = startDate,
			project1DueDate = LocalDateTime.of(2015, 2, 13, 23, 59);
	private final int task11EstDur = 8*60,
					  newTaskDur = 5*60;
	private final int task11Dev = 0,
					  newTaskDev = 10;
	private final ArrayList<Integer> newTaskDependencies = new ArrayList();

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
//		task12Dependencies.add(Integer.valueOf(1));
//		taskMan.createTask(1, "Implement Native", task12EstDur, task12Dev, null, task12Dependencies);	// TASK 2
//		task13Dependencies.add(Integer.valueOf(2));
//		taskMan.createTask(1, "Test code", task13EstDur, task13Dev, null, task13Dependencies);			// TASK 3
//		task14Dependencies.add(Integer.valueOf(2));
//		taskMan.createTask(1, "Document code", task14EstDur, task14Dev, null, task14Dependencies);		// TASK 4

	}
	
	//TODO alternative task + dependencies
	//TODO als ��N alt task voor task X slaagt, vervallen de anderen dan?

	@Test
	public void SuccesCaseTest() {
		// Step 1 and 2 are implicit
		// Step 3
		taskMan.createTask(1, "A new TASK", newTaskDur, newTaskDev, null, newTaskDependencies);
		// Step 4
		assertTrue(taskMan.getTaskDescription(1,1).equals("A new TASK"));
		assertEquals(taskMan.getTaskEstimatedDuration(1,1),newTaskDur);
		assertEquals(taskMan.getTaskAcceptedDeviation(1,1),newTaskDev);
		List<Integer> alts = taskMan.getTaskAlternatives(1,1);
		assertTrue(alts.size() == 0);
		List<Integer> deps = taskMan.getTaskDependencies(1,1);				//TODO moet dit kunnen worden opgevraagd?
		assertTrue(deps.size() == 1 && deps.contains(Integer.valueOf(1)));
		String status = taskMan.getTaskStatus(1,1);
		assertTrue(status.equals("available"));
	}
	
	//TODO Alt testen
	@Test
	public void SuccesCaseALTTest() {
		// Step 1 and 2 are implicit
		// Step 3
		taskMan.createTask(1, "A new TASK", newTaskDur, newTaskDev, null, newTaskDependencies);
		// Step 4
		assertTrue(taskMan.getTaskDescription(1,1).equals("A new TASK"));
		assertEquals(taskMan.getTaskEstimatedDuration(1,1),newTaskDur);
		assertEquals(taskMan.getTaskAcceptedDeviation(1,1),newTaskDev);
		List<Integer> alts = taskMan.getTaskAlternatives(1,1);
		assertTrue(alts.size() == 0);
		List<Integer> deps = taskMan.getTaskDependencies(1,1);
		assertTrue(deps.size() == 1 && deps.contains(Integer.valueOf(1)));
		String status = taskMan.getTaskStatus(1,1);
		assertTrue(status.equals("available"));
	}
	
	//TODO Dep testen
	@Test
	public void SuccesCasDepTest() {
		// Step 1 and 2 are implicit
		// Step 3
		taskMan.createTask(1, "A new TASK", newTaskDur, newTaskDev, null, newTaskDependencies);
		// Step 4
		assertTrue(taskMan.getTaskDescription(1,1).equals("A new TASK"));
		assertEquals(taskMan.getTaskEstimatedDuration(1,1),newTaskDur);
		assertEquals(taskMan.getTaskAcceptedDeviation(1,1),newTaskDev);
		List<Integer> alts = taskMan.getTaskAlternatives(1,1);
		assertTrue(alts.size() == 0);
		List<Integer> deps = taskMan.getTaskDependencies(1,1);
		assertTrue(deps.size() == 1 && deps.contains(Integer.valueOf(1)));
		String status = taskMan.getTaskStatus(1,1);
		assertTrue(status.equals("available"));
	}
	
	@Test
	public void flow3aTest() {
		// De UI zal geen request doorsturen.
		assertTrue(taskMan.getProjectNumberOfTasks(1) == 0); //TODO Moet dit kunnen worden opgevraagd?
	}
	
	//TODO Onbestaande Alt testen + not finished Alt testen
	@Test
	public void flow4aUnknownAltTest() {
		
	}
	
	@Test
	public void flow4aBadAltTest() {
		
	}
	
	@Test
	public void flow4aUnknownDepTest() {
		
	}
	
	//TODO FINISHED project of bad reference resten
	@Test
	public void flow4aUnknownProjectTest() {
		
	}
	
	@Test
	public void flow4aBadProjectTest() {
		
	}

}
