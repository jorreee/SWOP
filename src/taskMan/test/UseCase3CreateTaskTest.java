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
			project0StartDate = startDate,
			project0DueDate = LocalDateTime.of(2015, 2, 13, 23, 59),
			workDate = LocalDateTime.of(2015, 2, 9, 16, 0),
			altTaskEndDate = LocalDateTime.of(2015, 2, 9, 10, 0);
	private final int task00EstDur = 8*60,
			newTaskDur = 5*60;
	private final int task00Dev = 0,
			newTaskDev = 10;
	private final ArrayList<Integer> task00Dependencies = new ArrayList<Integer>(),
									 newTaskDependencies = new ArrayList<Integer>();

	/**
	 * DEFAULT TASKMAN TESTER
	 * - project 1 START 9 feb DUE 13 feb (midnight)
	 * 		GEEN tasks aanwezig
	 */
	@Before
	public final void initialize() {
		taskMan = new TaskMan(startDate);

		taskMan.createProject("Test1", "testing 1", project0StartDate, project0DueDate);

		taskMan.advanceTimeTo(workDate);
//
//		taskMan.createTask(1, "Design system", task11EstDur, task11Dev, null, task11Dependencies);		// TASK 1
//		task12Dependencies.add(Integer.valueOf(1));
//		taskMan.createTask(1, "Implement Native", task12EstDur, task12Dev, null, task12Dependencies);	// TASK 2
//		task13Dependencies.add(Integer.valueOf(2));
//		taskMan.createTask(1, "Test code", task13EstDur, task13Dev, null, task13Dependencies);			// TASK 3
//		task14Dependencies.add(Integer.valueOf(2));
//		taskMan.createTask(1, "Document code", task14EstDur, task14Dev, null, task14Dependencies);		// TASK 4

	}

	@Test
	public void SuccesCaseTest() {
		// Step 1 and 2 are implicit
		// Step 3
		taskMan.createTask(0, "A new TASK", newTaskDur, newTaskDev, -1, newTaskDependencies);
		// Step 4
		assertTrue(taskMan.getTaskDescription(0,0).equals("A new TASK"));
		assertEquals(taskMan.getEstimatedTaskDuration(0,0),newTaskDur);
		assertEquals(taskMan.getAcceptableTaskDeviation(0,0),newTaskDev);
		assertFalse(taskMan.hasTaskAlternative(0, 0));
		assertFalse(taskMan.hasTaskPrerequisites(0, 0));
		assertEquals(taskMan.getAvailableTasks(0).size(),1);
	}

	@Test
	public void SuccesCaseALTTest() {

		// Er is al een task aanwezig in het project
		taskMan.createTask(0, "Design system", task00EstDur, task00Dev, -1, task00Dependencies);
		taskMan.updateTaskDetails(0, 0, startDate, altTaskEndDate, "failed");

		// Step 1 and 2 are implicit
		// Step 3
		newTaskDependencies.add(Integer.valueOf(0));
		taskMan.createTask(0, "A new TASK", newTaskDur, newTaskDev, Integer.valueOf(0), newTaskDependencies);
		// Step 4
		assertTrue(taskMan.getTaskDescription(0,1).equals("A new TASK"));
		assertEquals(taskMan.getTaskAlternativeTo(0, 0),0);//TODO dependencies
		String status = taskMan.getTaskStatus(0,1);
		assertTrue(status.equals("available"));
	}

	@Test
	public void SuccesCasDepTest() {

		// Er is al een task aanwezig in het project
		taskMan.createTask(0, "Design system", task00EstDur, task00Dev, -1, task00Dependencies);

		// Step 1 and 2 are implicit
		// Step 3
		newTaskDependencies.add(Integer.valueOf(1));
		taskMan.createTask(0, "A new TASK", newTaskDur, newTaskDev, -1, newTaskDependencies);
		// Step 4
		assertTrue(taskMan.getTaskDescription(0,0).equals("A new TASK"));
		List<Integer> alts = taskMan.getTaskAlternatives(0,0);
		assertTrue(alts.size() == 0);
		List<Integer> deps = taskMan.getTaskDependencies(0,0);
		assertTrue(deps.size() == 1 && deps.contains(Integer.valueOf(0)));
		String status = taskMan.getTaskStatus(0,0);
		assertTrue(status.equals("unavailable"));
	}

	@Test
	public void flow3aTest() {
		// De UI zal geen request doorsturen als de user geen volledig formulier invult.
		assertTrue(taskMan.getTaskAmount(0) == 0);
	}

	//TODO Onbestaande Alt testen + not finished Alt testen + double Alt
	@Test
	public void flow4aUnknownAltTest() {
		// Step 1 and 2 are implicit
		// Step 3
		newTaskDependencies.add(Integer.valueOf(0));
		taskMan.createTask(0, "A new TASK", newTaskDur, newTaskDev, -1, newTaskDependencies);
		// Step 4
		assertEquals(taskMan.getTaskAmount(0),0);
		
		//--------------------------------------------------------------------------------------
		
		// Step 1 and 2 are implicit
		// Step 3
		newTaskDependencies.remove(0);
		newTaskDependencies.add(Integer.valueOf(5));
		taskMan.createTask(0, "A new TASK", newTaskDur, newTaskDev, -1, newTaskDependencies);
		// Step 4
		assertEquals(taskMan.getTaskAmount(0),0);
		
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
