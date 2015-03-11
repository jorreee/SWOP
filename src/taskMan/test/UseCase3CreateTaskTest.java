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
			task00StartDateGood = startDate,
			task00EndDateGood = LocalDateTime.of(2015,2,9,10,0),
			task01StartDateGood = LocalDateTime.of(2015, 2, 9, 10, 10),
			task01EndDateGood = LocalDateTime.of(2015, 2, 9, 12, 0),
			task02StartDateGood = LocalDateTime.of(2015, 2, 9, 12, 10),
			task02EndDateGood = LocalDateTime.of(2015, 2, 9, 14, 0),
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
	 * DEFAULT TASKMAN TESTER
	 * - project 1 START 9 feb DUE 13 feb (midnight)
	 * 		GEEN tasks aanwezig
	 */
	@Before
	public final void initialize() {
		taskMan = new TaskMan(startDate);

		assertTrue(taskMan.createProject("Test1", "testing 1", project0StartDate, project0DueDate));

		assertTrue(taskMan.advanceTimeTo(workDate));
//
//		taskMan.createTask(1, "Design system", task11EstDur, task11Dev, null, task11Dependencies);		// TASK 1
//		task12Dependencies.add(Integer.valueOf(0));
//		taskMan.createTask(1, "Implement Native", task12EstDur, task12Dev, null, task12Dependencies);	// TASK 2
//		task13Dependencies.add(Integer.valueOf(1));
//		taskMan.createTask(1, "Test code", task13EstDur, task13Dev, null, task13Dependencies);			// TASK 3
//		task14Dependencies.add(Integer.valueOf(1));
//		taskMan.createTask(1, "Document code", task14EstDur, task14Dev, null, task14Dependencies);		// TASK 4

	}

	@Test
	public void SuccesCaseTest() {
		// Step 1 and 2 are implicit
		// Step 3
		assertTrue(taskMan.createTask(0, "A new TASK", newTaskDur, newTaskDev, -1, newTaskDependencies));
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

		// Er is al een FAILED task aanwezig in het project
		assertTrue(taskMan.createTask(0, "Design system", task00EstDur, task00Dev, -1, task00Dependencies));
		assertTrue(taskMan.setTaskFailed(0, 0, startDate, altTaskEndDate));
		
		
		// Step 1 and 2 are implicit
		// Step 3
		assertTrue(taskMan.createTask(0, "A new TASK", newTaskDur, newTaskDev, 0, newTaskDependencies));
		// Step 4
		assertTrue(taskMan.getTaskDescription(0,1).equals("A new TASK"));
		assertEquals(taskMan.getEstimatedTaskDuration(0,1),newTaskDur);
		assertEquals(taskMan.getAcceptableTaskDeviation(0,1),newTaskDev);
		assertTrue(taskMan.hasTaskAlternative(0, 0));
		assertEquals(taskMan.getTaskAlternativeTo(0, 0),1);					//
		assertFalse(taskMan.hasTaskPrerequisites(0, 0));
		assertFalse(taskMan.hasTaskAlternative(0, 1));
		assertFalse(taskMan.hasTaskPrerequisites(0, 1));
		assertTrue(taskMan.getTaskStatus(0, 1).equals("available"));
		assertEquals(taskMan.getAvailableTasks(0).size(),2);
		
	}

	@Test
	public void SuccesCaseDepTest() {

		// Er is al een task aanwezig in het project
		assertTrue(taskMan.createTask(0, "Design system", task00EstDur, task00Dev, -1, task00Dependencies));

		// Step 1 and 2 are implicit
		// Step 3
		newTaskDependencies.add(Integer.valueOf(0));
		assertTrue(taskMan.createTask(0, "A new TASK", newTaskDur, newTaskDev, -1, newTaskDependencies));
		// Step 4
		assertTrue(taskMan.getTaskDescription(0,1).equals("A new TASK"));
		assertEquals(taskMan.getEstimatedTaskDuration(0,1),newTaskDur);
		assertEquals(taskMan.getAcceptableTaskDeviation(0,1),newTaskDev);
		assertTrue(taskMan.hasTaskAlternative(0, 0));
		assertFalse(taskMan.hasTaskPrerequisites(0, 0));
		assertFalse(taskMan.hasTaskAlternative(0, 1));
		assertFalse(taskMan.hasTaskPrerequisites(0, 1));
		assertEquals(taskMan.getTaskPrerequisitesFor(0, 1),0);				//
		assertTrue(taskMan.getTaskStatus(0, 1).equals("available"));
		assertEquals(taskMan.getAvailableTasks(0).size(),2);
		
	}
	
	@Test
	public void SuccesCaseFailedDepNoAltTest() {

		// Er is al een FAILED task aanwezig in het project
		assertTrue(taskMan.createTask(0, "Design system", task00EstDur, task00Dev, -1, task00Dependencies));
		assertEquals(taskMan.getTaskStatus(0, 0),"available");
		assertTrue(taskMan.setTaskFailed(0, 0, startDate, altTaskEndDate));
		assertEquals(taskMan.getTaskStatus(0, 0),"failed");
		
		// Task kan FAILED task ZONDER ALT als dep nemen !!!!!!!!!!!!!!

		// Step 1 and 2 are implicit
		// Step 3
		newTaskDependencies.add(Integer.valueOf(0));
		assertTrue(taskMan.createTask(0, "A new TASK", newTaskDur, newTaskDev, -1, newTaskDependencies));
		// Step 4
		assertTrue(taskMan.hasTaskPrerequisites(0, 1));
		assertEquals(taskMan.getTaskPrerequisitesFor(0, 1),0);
		assertEquals(taskMan.getTaskAmount(0),2);
	}
	
	@Test
	public void SuccesCaseFailedDepWithAltTest() {

		// Er is al een FAILED task MET ALT aanwezig in het project
		assertTrue(taskMan.createTask(0, "Design system", task00EstDur, task00Dev, -1, task00Dependencies));
		assertEquals(taskMan.getTaskStatus(0, 0),"available");
		assertTrue(taskMan.setTaskFailed(0, 0, startDate, altTaskEndDate));
		assertEquals(taskMan.getTaskStatus(0, 0),"failed");
		assertTrue(taskMan.createTask(0, "Implement Native", task01EstDur, task01Dev, 0, task01Dependencies));
		assertEquals(taskMan.getTaskStatus(0, 1),"available");

		// Step 1 and 2 are implicit
		// Step 3
		newTaskDependencies.add(Integer.valueOf(0));
		assertTrue(taskMan.createTask(0, "A new TASK", newTaskDur, newTaskDev, -1, newTaskDependencies));
		// Step 4
		assertTrue(taskMan.hasTaskPrerequisites(0, 2));
		assertEquals(taskMan.getTaskPrerequisitesFor(0, 2),1);				// NIET 0 !!!!!
		assertEquals(taskMan.getTaskAmount(0),3);
	}

	@Test
	public void flow3aTest() {
		// De UI zal geen request doorsturen als de user geen volledig formulier invult.
		assertTrue(taskMan.getTaskAmount(0) == 0);
	}

	@Test
	public void flow4aUnknownAltTest() {
		
		// Kan zichzelf niet als ALT nemen
		
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskMan.createTask(0, "A new TASK", newTaskDur, newTaskDev, 0, newTaskDependencies));
		// Step 4
		assertEquals(taskMan.getTaskAmount(0),0);
		
		//--------------------------------------------------------------------------------------
		// Onbestaande task kan geen ALT nemen
		
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskMan.createTask(0, "A new TASK", newTaskDur, newTaskDev, 5, newTaskDependencies));
		// Step 4
		assertEquals(taskMan.getTaskAmount(0),0);
		
	}

	@Test
	public void flow4aBadAltTest() {
		
		// Er is al een AVAILABLE en UNAVAILABLE task aanwezig in het project
		assertTrue(taskMan.createTask(0, "Design system", task00EstDur, task00Dev, -1, task00Dependencies));
		assertEquals(taskMan.getTaskStatus(0, 0),"available");
		task01Dependencies.add(Integer.valueOf(0));
		assertTrue(taskMan.createTask(0, "Implement Native", task01EstDur, task01Dev, -1, task01Dependencies));
		assertEquals(taskMan.getTaskStatus(0, 1),"unavailable");
		
		// AVAILABLE task kan geen ALT nemen
		
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskMan.createTask(0, "A new TASK", newTaskDur, newTaskDev, 0, newTaskDependencies));
		// Step 4
		assertFalse(taskMan.hasTaskAlternative(0, 0));
		assertFalse(taskMan.hasTaskPrerequisites(0, 0));
		assertEquals(taskMan.getTaskAmount(0),2);
		
		//----------------------------------------------------------------------------------------
		// UNAVAILABLE task kan geen ALT nemen
		
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskMan.createTask(0, "A new TASK", newTaskDur, newTaskDev, 1, newTaskDependencies));
		// Step 4
		assertFalse(taskMan.hasTaskAlternative(0, 1));
		assertFalse(taskMan.hasTaskPrerequisites(0, 1));
		assertEquals(taskMan.getTaskAmount(0),2);
		
		//----------------------------------------------------------------------------------------
		// FINISHED task kan geen ALT nemen
		
		assertTrue(taskMan.setTaskFinished(0, 0, startDate, altTaskEndDate));
		
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskMan.createTask(0, "A new TASK", newTaskDur, newTaskDev, 0, newTaskDependencies));
		// Step 4
		assertFalse(taskMan.hasTaskAlternative(0, 0));
		assertFalse(taskMan.hasTaskPrerequisites(0, 0));
		assertEquals(taskMan.getTaskAmount(0),2);

	}
	
	@Test
	public void flow4aDoubleAltTest() {

		assertTrue(taskMan.createTask(0, "Design system", task00EstDur, task00Dev, -1, task00Dependencies));
		assertTrue(taskMan.setTaskFailed(0, 0, task00StartDateGood, task00EndDateGood));
		assertTrue(taskMan.createTask(0, "Implement Native", task01EstDur, task01Dev, 0, task01Dependencies));
		assertEquals(taskMan.getTaskStatus(0, 1),"available");
		
		// Geen twee ALT tasks voor een failed task mogelijk
		
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskMan.createTask(0, "A new TASK", newTaskDur, newTaskDev, 0, newTaskDependencies));
		// Step 4
		assertEquals(taskMan.getTaskAmount(0),2);
	}

	@Test
	public void flow4aUnknownDepTest() {
		
		// Kan zichzelf niet als DEP nemen
		
		newTaskDependencies.add(Integer.valueOf(0));
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskMan.createTask(0, "A new TASK", newTaskDur, newTaskDev, -1, newTaskDependencies));
		// Step 4
		assertEquals(taskMan.getTaskAmount(0),0);
		
		//--------------------------------------------------------------------------------------
		// Onbestaande task kan geen ALT nemen

		newTaskDependencies.remove(0);
		newTaskDependencies.add(Integer.valueOf(5));
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskMan.createTask(0, "A new TASK", newTaskDur, newTaskDev, -1, newTaskDependencies));
		// Step 4
		assertEquals(taskMan.getTaskAmount(0),0);
		
	}
	
	@Test
	public void flow4aBadDepTest() {

		// Er is al een FAILED task aanwezig in het project
		assertTrue(taskMan.createTask(0, "Design system", task00EstDur, task00Dev, -1, task00Dependencies));
		assertEquals(taskMan.getTaskStatus(0, 0),"available");
		assertTrue(taskMan.setTaskFailed(0, 0, startDate, altTaskEndDate));
		assertEquals(taskMan.getTaskStatus(0, 0),"failed");
		
		// Task kan geen PreReq zijn voor zijn ALT

		// Step 1 and 2 are implicit
		// Step 3
		newTaskDependencies.add(Integer.valueOf(0));
		assertFalse(taskMan.createTask(0, "A new TASK", newTaskDur, newTaskDev, 0, newTaskDependencies));
		// Step 4
		assertFalse(taskMan.hasTaskAlternative(0, 0));
		assertFalse(taskMan.hasTaskPrerequisites(0, 0));
		assertEquals(taskMan.getTaskAmount(0),1);
	}
	
	@Test
	public void flow4aUnknownProjectTest() {

		assertTrue(taskMan.createTask(0, "Design system", task00EstDur, task00Dev, -1, task00Dependencies));
		assertEquals(taskMan.getTaskStatus(0, 0),"available");

		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskMan.createTask(5, "A new TASK", newTaskDur, newTaskDev, -1, newTaskDependencies));
		// Step 4
		assertEquals(taskMan.getProjectAmount(),1);		
		
		//-------------------------------------------------------------------------------------------------

		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskMan.createTask(5, "A new TASK", newTaskDur, newTaskDev, 0, newTaskDependencies));
		// Step 4
		assertEquals(taskMan.getProjectAmount(),1);		
		
		//-------------------------------------------------------------------------------------------------

		// Step 1 and 2 are implicit
		// Step 3
		newTaskDependencies.add(Integer.valueOf(0));
		assertFalse(taskMan.createTask(5, "A new TASK", newTaskDur, newTaskDev, -1, newTaskDependencies));
		// Step 4
		assertEquals(taskMan.getProjectAmount(),1);	
		assertEquals(taskMan.getTaskAmount(0),1);
		
	}

	@Test
	public void flow4aBadProjectTest() {
		
		assertTrue(taskMan.createTask(0, "Design system", task00EstDur, task00Dev, -1, task00Dependencies));
		assertEquals(taskMan.getTaskStatus(0, 0),"available");
		assertTrue(taskMan.setTaskFinished(0, 0, task00StartDateGood, task00EndDateGood));
		
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskMan.createTask(5, "A new TASK", newTaskDur, newTaskDev, -1, newTaskDependencies));
		// Step 4
		assertEquals(taskMan.getProjectAmount(),1);
		assertEquals(taskMan.getTaskAmount(0),1);

	}

}
