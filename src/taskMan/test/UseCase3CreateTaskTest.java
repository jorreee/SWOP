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

public class UseCase3CreateTaskTest {

	private IFacade taskManager;
	private final LocalDateTime startDate = LocalDateTime.of(2015, 2, 9, 8, 0),
			project0DueDate = LocalDateTime.of(2015, 2, 13, 23, 59),
			task00StartDateGood = startDate,
			task00EndDateGood = LocalDateTime.of(2015,2,9,10,0),
			workDate = LocalDateTime.of(2015, 2, 9, 16, 0),
			altTaskEndDate = LocalDateTime.of(2015, 2, 9, 10, 0);
	private final int task00EstDur = 8*60,
			task01EstDur = 16*60,
			newTaskDur = 5*60;
	private final int task00Dev = 0,
			task01Dev = 50,
			newTaskDev = 10;
	private final ArrayList<Integer> task00Dependencies = new ArrayList<Integer>(),
			 						 task01Dependencies = new ArrayList<Integer>(),
									 newTaskDependencies = new ArrayList<Integer>();

	/**
	 * - project 1 START 9 feb 8u DUE 13 feb midnight
	 * 		GEEN tasks aanwezig
	 */
	@Before
	public final void initialize() {
		taskManager = new Facade(startDate);

		assertTrue(taskManager.createProject("Test1", "testing 1", project0DueDate));

		assertTrue(taskManager.advanceTimeTo(workDate));

	}

	@Test
	public void SuccesCaseTest() {
		// Step 1 and 2 are implicit
		// Step 3
		assertTrue(taskManager.createTask(0, "A new TASK", newTaskDur, newTaskDev, -1, newTaskDependencies));
		// Step 4
		assertTrue(taskManager.getTaskDescription(0,0).equals("A new TASK"));
		assertEquals(taskManager.getEstimatedTaskDuration(0,0),newTaskDur);
		assertEquals(taskManager.getAcceptableTaskDeviation(0,0),newTaskDev);
		assertFalse(taskManager.hasTaskAlternative(0, 0));
		assertFalse(taskManager.hasTaskPrerequisites(0, 0));
		assertEquals(taskManager.getAvailableTasks(0).size(),1);
	}

	@Test
	public void SuccesCaseALTTest() {

		// Er is al een FAILED task aanwezig in het project
		assertTrue(taskManager.createTask(0, "Design system", task00EstDur, task00Dev, -1, task00Dependencies));
		assertTrue(taskManager.setTaskFailed(0, 0, startDate, altTaskEndDate));
		
		
		// Step 1 and 2 are implicit
		// Step 3
		assertTrue(taskManager.createTask(0, "A new TASK", newTaskDur, newTaskDev, 0, newTaskDependencies));
		// Step 4
		assertTrue(taskManager.getTaskDescription(0,1).equals("A new TASK"));
		assertEquals(taskManager.getEstimatedTaskDuration(0,1),newTaskDur);
		assertEquals(taskManager.getAcceptableTaskDeviation(0,1),newTaskDev);
		assertTrue(taskManager.hasTaskAlternative(0, 0));
		assertEquals(taskManager.getTaskAlternativeTo(0, 0),1);					//
		assertFalse(taskManager.hasTaskPrerequisites(0, 0));
		assertFalse(taskManager.hasTaskAlternative(0, 1));
		assertFalse(taskManager.hasTaskPrerequisites(0, 1));
		assertTrue(taskManager.getTaskStatus(0, 1).equals("available"));
		assertEquals(taskManager.getAvailableTasks(0).size(),1);
		
	}

	@Test
	public void SuccesCaseDepTest() {

		// Er is al een task aanwezig in het project
		assertTrue(taskManager.createTask(0, "Design system", task00EstDur, task00Dev, -1, task00Dependencies));

		// Step 1 and 2 are implicit
		// Step 3
		newTaskDependencies.add(Integer.valueOf(0));
		assertTrue(taskManager.createTask(0, "A new TASK", newTaskDur, newTaskDev, -1, newTaskDependencies));
		// Step 4
		assertTrue(taskManager.getTaskDescription(0,1).equals("A new TASK"));
		assertEquals(taskManager.getEstimatedTaskDuration(0,1),newTaskDur);
		assertEquals(taskManager.getAcceptableTaskDeviation(0,1),newTaskDev);
		assertFalse(taskManager.hasTaskAlternative(0, 0));
		assertFalse(taskManager.hasTaskPrerequisites(0, 0));
		assertFalse(taskManager.hasTaskAlternative(0, 1));
		assertTrue(taskManager.hasTaskPrerequisites(0, 1));
		assertTrue(taskManager.getTaskPrerequisitesFor(0, 1).contains(0));				//
		assertTrue(taskManager.getTaskStatus(0, 1).equals("unavailable"));
		assertEquals(taskManager.getAvailableTasks(0).size(),1);
		
	}
	
	@Test
	public void SuccesCaseFailedDepNoAltTest() {

		// Er is al een FAILED task aanwezig in het project
		assertTrue(taskManager.createTask(0, "Design system", task00EstDur, task00Dev, -1, task00Dependencies));
		assertEquals(taskManager.getTaskStatus(0, 0),"available");
		assertTrue(taskManager.setTaskFailed(0, 0, startDate, altTaskEndDate));
		assertEquals(taskManager.getTaskStatus(0, 0),"failed");
		
		// Task kan FAILED task ZONDER ALT als dep nemen !!!!!!!!!!!!!!

		// Step 1 and 2 are implicit
		// Step 3
		newTaskDependencies.add(Integer.valueOf(0));
		assertTrue(taskManager.createTask(0, "A new TASK", newTaskDur, newTaskDev, -1, newTaskDependencies));
		// Step 4
		assertTrue(taskManager.hasTaskPrerequisites(0, 1));
		assertTrue(taskManager.getTaskPrerequisitesFor(0, 1).contains(0));
		assertEquals(taskManager.getTaskAmount(0),2);
	}
	
	@Test
	public void SuccesCaseFailedDepWithAltTest() {

		// Er is al een FAILED task MET ALT aanwezig in het project
		assertTrue(taskManager.createTask(0, "Design system", task00EstDur, task00Dev, -1, task00Dependencies));
		assertEquals(taskManager.getTaskStatus(0, 0),"available");
		assertTrue(taskManager.setTaskFailed(0, 0, startDate, altTaskEndDate));
		assertEquals(taskManager.getTaskStatus(0, 0),"failed");
		assertTrue(taskManager.createTask(0, "Implement Native", task01EstDur, task01Dev, 0, task01Dependencies));
		assertEquals(taskManager.getTaskStatus(0, 1),"available");

		// Step 1 and 2 are implicit
		// Step 3
		newTaskDependencies.add(Integer.valueOf(0));
		assertTrue(taskManager.createTask(0, "A new TASK", newTaskDur, newTaskDev, -1, newTaskDependencies));
		// Step 4
		assertTrue(taskManager.hasTaskPrerequisites(0, 2));
		assertTrue(taskManager.getTaskPrerequisitesFor(0, 2).contains(1));				// NIET 0 !!!!!
		assertEquals(taskManager.getTaskAmount(0),3);
	}

	@Test
	public void flow3aTest() {
		// De UI zal geen request doorsturen als de user geen volledig formulier invult.
		assertTrue(taskManager.getTaskAmount(0) == 0);
	}

	@Test
	public void flow4aUnknownAltTest() {
		
		// Kan zichzelf niet als ALT nemen
		
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskManager.createTask(0, "A new TASK", newTaskDur, newTaskDev, 0, newTaskDependencies));
		// Step 4
		assertEquals(taskManager.getTaskAmount(0),0);
		
		//--------------------------------------------------------------------------------------
		// Onbestaande task kan geen ALT nemen
		
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskManager.createTask(0, "A new TASK", newTaskDur, newTaskDev, 5, newTaskDependencies));
		// Step 4
		assertEquals(taskManager.getTaskAmount(0),0);
		
	}

	@Test
	public void flow4aBadAltTest() {
		
		// Er is al een AVAILABLE en UNAVAILABLE task aanwezig in het project
		assertTrue(taskManager.createTask(0, "Design system", task00EstDur, task00Dev, -1, task00Dependencies));
		assertEquals(taskManager.getTaskStatus(0, 0),"available");
		task01Dependencies.add(Integer.valueOf(0));
		assertTrue(taskManager.createTask(0, "Implement Native", task01EstDur, task01Dev, -1, task01Dependencies));
		assertEquals(taskManager.getTaskStatus(0, 1),"unavailable");
		
		// AVAILABLE task kan geen ALT nemen
		
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskManager.createTask(0, "A new TASK", newTaskDur, newTaskDev, 0, newTaskDependencies));
		// Step 4
		assertFalse(taskManager.hasTaskAlternative(0, 0));
		assertFalse(taskManager.hasTaskPrerequisites(0, 0));
		assertEquals(taskManager.getTaskAmount(0),2);
		
		//----------------------------------------------------------------------------------------
		// UNAVAILABLE task kan geen ALT nemen
		
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskManager.createTask(0, "A new TASK", newTaskDur, newTaskDev, 1, newTaskDependencies));
		// Step 4
		assertFalse(taskManager.hasTaskAlternative(0, 1));
		assertTrue(taskManager.hasTaskPrerequisites(0, 1));
		assertEquals(taskManager.getTaskAmount(0),2);
		
		//----------------------------------------------------------------------------------------
		// FINISHED task kan geen ALT nemen
		
		assertTrue(taskManager.setTaskFinished(0, 0, startDate, altTaskEndDate));
		
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskManager.createTask(0, "A new TASK", newTaskDur, newTaskDev, 0, newTaskDependencies));
		// Step 4
		assertFalse(taskManager.hasTaskAlternative(0, 0));
		assertFalse(taskManager.hasTaskPrerequisites(0, 0));
		assertEquals(taskManager.getTaskAmount(0),2);

	}
	
	@Test
	public void flow4aDoubleAltTest() {

		assertTrue(taskManager.createTask(0, "Design system", task00EstDur, task00Dev, -1, task00Dependencies));
		assertTrue(taskManager.setTaskFailed(0, 0, task00StartDateGood, task00EndDateGood));
		assertTrue(taskManager.createTask(0, "Implement Native", task01EstDur, task01Dev, 0, task01Dependencies));
		assertEquals(taskManager.getTaskStatus(0, 1),"available");
		
		// Geen twee ALT tasks voor een failed task mogelijk
		
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskManager.createTask(0, "A new TASK", newTaskDur, newTaskDev, 0, newTaskDependencies));
		// Step 4
		assertEquals(taskManager.getTaskAmount(0),2);
	}

	@Test
	public void flow4aUnknownDepTest() {
		
		// Kan zichzelf niet als DEP nemen
		
		newTaskDependencies.add(Integer.valueOf(0));
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskManager.createTask(0, "A new TASK", newTaskDur, newTaskDev, -1, newTaskDependencies));
		// Step 4
		assertEquals(taskManager.getTaskAmount(0),0);
		
		//--------------------------------------------------------------------------------------
		// Onbestaande task kan geen ALT nemen

		newTaskDependencies.remove(0);
		newTaskDependencies.add(Integer.valueOf(5));
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskManager.createTask(0, "A new TASK", newTaskDur, newTaskDev, -1, newTaskDependencies));
		// Step 4
		assertEquals(taskManager.getTaskAmount(0),0);
		
	}
	
	@Test
	public void flow4aBadDepTest() {

		// Er is al een FAILED task aanwezig in het project
		assertTrue(taskManager.createTask(0, "Design system", task00EstDur, task00Dev, -1, task00Dependencies));
		assertEquals(taskManager.getTaskStatus(0, 0),"available");
		assertTrue(taskManager.setTaskFailed(0, 0, startDate, altTaskEndDate));
		assertEquals(taskManager.getTaskStatus(0, 0),"failed");
		
		// Task kan geen PreReq zijn voor zijn ALT

		// Step 1 and 2 are implicit
		// Step 3
		newTaskDependencies.add(Integer.valueOf(0));
		assertFalse(taskManager.createTask(0, "A new TASK", newTaskDur, newTaskDev, 0, newTaskDependencies));
		// Step 4
		assertFalse(taskManager.hasTaskAlternative(0, 0));
		assertFalse(taskManager.hasTaskPrerequisites(0, 0));
		assertEquals(taskManager.getTaskAmount(0),1);
	}
	
	@Test
	public void flow4aUnknownProjectTest() {

		assertTrue(taskManager.createTask(0, "Design system", task00EstDur, task00Dev, -1, task00Dependencies));
		assertEquals(taskManager.getTaskStatus(0, 0),"available");

		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskManager.createTask(5, "A new TASK", newTaskDur, newTaskDev, -1, newTaskDependencies));
		// Step 4
		assertEquals(taskManager.getProjectAmount(),1);		
		
		//-------------------------------------------------------------------------------------------------

		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskManager.createTask(5, "A new TASK", newTaskDur, newTaskDev, 0, newTaskDependencies));
		// Step 4
		assertEquals(taskManager.getProjectAmount(),1);		
		
		//-------------------------------------------------------------------------------------------------

		// Step 1 and 2 are implicit
		// Step 3
		newTaskDependencies.add(Integer.valueOf(0));
		assertFalse(taskManager.createTask(5, "A new TASK", newTaskDur, newTaskDev, -1, newTaskDependencies));
		// Step 4
		assertEquals(taskManager.getProjectAmount(),1);	
		assertEquals(taskManager.getTaskAmount(0),1);
		
	}

	@Test
	public void flow4aBadProjectTest() {
		
		assertTrue(taskManager.createTask(0, "Design system", task00EstDur, task00Dev, -1, task00Dependencies));
		assertEquals(taskManager.getTaskStatus(0, 0),"available");
		assertTrue(taskManager.setTaskFinished(0, 0, task00StartDateGood, task00EndDateGood));
		assertTrue(taskManager.getProjectStatus(0).equals("finished"));
		
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskManager.createTask(5, "A new TASK", newTaskDur, newTaskDev, -1, newTaskDependencies));
		// Step 4
		assertEquals(taskManager.getProjectAmount(),1);
		assertEquals(taskManager.getTaskAmount(0),1);
		
	}

}
