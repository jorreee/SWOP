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

public class UseCase1ShowProjectsTest {

	private IFacade taskManager;
	private final LocalDateTime startDate = LocalDateTime.of(2015, 2, 9, 8, 0),
			workdate1 = LocalDateTime.of(2015, 2, 11, 16, 0),
			workdate2 = LocalDateTime.of(2015, 2, 12, 16, 0),
			workdate3 = LocalDateTime.of(2015, 2, 13, 16, 0),
			workdate4 = LocalDateTime.of(2015, 2, 15, 16, 0),
			workdate5 = LocalDateTime.of(2015, 2, 16, 10, 0),
			project0DueDate = LocalDateTime.of(2015, 2, 13, 23, 59),
			project1DueDate = LocalDateTime.of(2015, 2, 21, 23, 59),
			project2DueDate = LocalDateTime.of(2015, 2, 16, 23, 59),
			project3DueDate = LocalDateTime.of(2015, 2, 21, 23, 59),
			task00Start = startDate,
			task00End = LocalDateTime.of(2015, 2, 9, 10, 0),
			task10Start = LocalDateTime.of(2015, 2, 12, 8, 0),
			task10End = LocalDateTime.of(2015, 2, 13, 10, 0),
			task11Start = LocalDateTime.of(2015, 2, 13, 10, 15),
			task11End = LocalDateTime.of(2015, 2, 13, 14, 0),
			task30Start = LocalDateTime.of(2015, 2, 16, 8, 0),
			task30End = LocalDateTime.of(2015, 2, 16, 9, 45);
	private final int task00EstDur = 120,
			task10EstDur = 8*60,
			task11EstDur = 3*8*60,
			task12EstDur = 2*8*60,
			task13EstDur = 2*8*60,
			task20EstDur = 35*60,
			task30EstDur = 60,
			task31EstDur = 3*8*60;
	private final int task00Dev = 5,
			task10Dev = 30,
			task11Dev = 0,
			task12Dev = 15,
			task13Dev = 20,
			task20Dev = 50,	// Moet nog steeds delayed project geven!
			task30Dev = 0,
			task31Dev = 15;
	private final ArrayList<Integer> task00Dependencies = new ArrayList<Integer>(),
			task10Dependencies = new ArrayList<Integer>(),
			task11Dependencies = new ArrayList<Integer>(),
			task12Dependencies = new ArrayList<Integer>(),
			task13Dependencies = new ArrayList<Integer>(),
			task20Dependencies = new ArrayList<Integer>(),
			task30Dependencies = new ArrayList<Integer>(),
			task31Dependencies = new ArrayList<Integer>();
	
	/**
	 * - project 0 START 9 feb 8u DUE 31 feb midnight
	 * 		task 0 FINISHED
	 * 
	 * - project 1 START 11 feb 16u DUE 
	 * 		task 0 FINISHED
	 * 		task 1 FAILED
	 * 		task 2 <- 0, 1
	 * 		task 3 ALT 0
	 * 
	 * - project 2 START 13 feb 16u DUE 
	 * 		task 0 
	 * 
	 * - project 3 START 15 feb 16u DUE 
	 * 		task 0 FAILED
	 * 		task 1 
	 * 
	 */
	@Before
	public final void initialize() {
		// INIT systeem en maak het eerste project aan, samen met zijn TASK
		taskManager = new Facade(startDate);
			assertTrue(taskManager.createProject("Project 0", "Describing proj 0", project0DueDate));
				assertTrue(taskManager.createTask(0, "TASK 00", task00EstDur, task00Dev, -1, task00Dependencies));		// 00 AVAILABLE
				
		// Stap verder:
		// maak het tweede project aan en maak zijn TASK lijst
		assertTrue(taskManager.advanceTimeTo(workdate1));
			assertTrue(taskManager.createProject("Project 1", "Describing proj 1", project1DueDate));
				assertTrue(taskManager.createTask(1, "TASK 10", task10EstDur, task10Dev, -1, task10Dependencies));			// 10 AVAILABLE
				assertTrue(taskManager.createTask(1, "TASK 11", task11EstDur, task11Dev, -1, task11Dependencies));			// 11 AVAILABLE
				assertTrue(task12Dependencies.add(Integer.valueOf(0))); assertTrue(task12Dependencies.add(Integer.valueOf(1)));
				assertTrue(taskManager.createTask(1, "TASK 12", task12EstDur, task12Dev, -1, task12Dependencies));			// 12 UNAVAILABLE
		
		// Stap verder:
		// maak het derde project aan, samen met zijn TASK
		assertTrue(taskManager.advanceTimeTo(workdate2));
			assertTrue(taskManager.createProject("Project 2", "Describing proj 2", project2DueDate));
				assertTrue(taskManager.createTask(2, "TASK 20", task20EstDur, task20Dev, -1, task20Dependencies));		// 20 AVAILABLE
					
		// Stap verder:
		// maak TASK 0,0 af -> project 0 is finished
		// update project 1
		assertTrue(taskManager.advanceTimeTo(workdate3));
			assertTrue(taskManager.setTaskFinished(0, 0, task00Start, task00End));										// 00 FINISHED
			//----------------------------------------------------
			assertTrue(taskManager.setTaskFinished(1, 0, task10Start, task10End));										// 10 FINISHED
			assertTrue(taskManager.setTaskFailed(1, 1, task11Start, task11End));										// 11 FAILED
			assertTrue(taskManager.createTask(1, "TASK 13", task13EstDur, task13Dev, 1, task13Dependencies));			// 13 AVAILABLE
			
		// Stap verder:
		// maak het vierde project aan en maak zijn TASK lijst
		assertTrue(taskManager.advanceTimeTo(workdate4));
			assertTrue(taskManager.createProject("Project 3", "Describing proj 3", project3DueDate));
				assertTrue(taskManager.createTask(3, "TASK 30", task30EstDur, task30Dev, -1, task30Dependencies));		// 30 AVAILABLE
				assertTrue(taskManager.createTask(3, "TASK 31", task31EstDur, task31Dev, -1, task31Dependencies));		// 31 AVAILABLE
				
		// Stap verder:
		// maak task 3,0 FAILED
		assertTrue(taskManager.advanceTimeTo(workdate5));
			assertTrue(taskManager.setTaskFailed(3, 0, task30Start, task30End));										// 30 FAILED DELAYED

	}
	
	@Test
	public void SuccesCasetest() {
		// Stap 1 is impliciet
		// Stap 2
		int numOfProj = taskManager.getProjectAmount();
		assertEquals(numOfProj,4);
		// Stap 3
		assertTrue(taskManager.getProjectDescription(0).equals("Describing proj 0"));
		assertEquals(taskManager.getProjectDueTime(0),project0DueDate);
		assertTrue(taskManager.getProjectName(0).equals("Project 0"));
		assertTrue(taskManager.isProjectFinished(0));
		assertEquals(taskManager.getProjectEndTime(0),task00End);
		assertEquals(taskManager.getTaskAmount(0),1);
		assertEquals(taskManager.getAvailableTasks(0).size(),0);
		assertEquals(taskManager.getProjectCreationTime(0),startDate);
		assertTrue(taskManager.isProjectEstimatedOnTime(0));

		assertTrue(taskManager.getProjectDescription(1).equals("Describing proj 1"));
		assertEquals(taskManager.getProjectDueTime(1),project1DueDate);
		assertTrue(taskManager.getProjectName(1).equals("Project 1"));
		assertFalse(taskManager.isProjectFinished(1));
		assertEquals(taskManager.getProjectEndTime(1),null);
		assertEquals(taskManager.getTaskAmount(1),4);
		assertEquals(taskManager.getAvailableTasks(1).size(),1);
		assertEquals(taskManager.getProjectCreationTime(1),workdate1);
		assertFalse(taskManager.isProjectEstimatedOnTime(1));

		assertTrue(taskManager.getProjectDescription(2).equals("Describing proj 2"));
		assertEquals(taskManager.getProjectDueTime(2),project2DueDate);
		assertTrue(taskManager.getProjectName(2).equals("Project 2"));
		assertFalse(taskManager.isProjectFinished(2));
		assertEquals(taskManager.getProjectEndTime(2),null);
		assertEquals(taskManager.getTaskAmount(2),1);
		assertEquals(taskManager.getAvailableTasks(2).size(),1);
		assertEquals(taskManager.getProjectCreationTime(2),workdate2);
		assertFalse(taskManager.isProjectEstimatedOnTime(2));											// DELAYED

		assertTrue(taskManager.getProjectDescription(3).equals("Describing proj 3"));
		assertEquals(taskManager.getProjectDueTime(3),project3DueDate);
		assertTrue(taskManager.getProjectName(3).equals("Project 3"));
		assertFalse(taskManager.isProjectFinished(3));
		assertEquals(taskManager.getProjectEndTime(3),null);
		assertEquals(taskManager.getTaskAmount(3),2);
		assertEquals(taskManager.getAvailableTasks(3).size(),1);
		assertEquals(taskManager.getProjectCreationTime(3),workdate4);
		assertTrue(taskManager.isProjectEstimatedOnTime(0));
		
		//--------------------------------------------------------------------------
		// Test Project 0 tasks

		assertTrue(taskManager.getTaskDescription(0, 0).equals("TASK 00"));
		assertTrue(taskManager.hasTaskEnded(0, 0));
		assertEquals(taskManager.getTaskStartTime(0, 0),task00Start);
		assertEquals(taskManager.getTaskEndTime(0, 0),task00End);
		assertTrue(taskManager.isTaskOnTime(0, 0));
		assertFalse(taskManager.isTaskUnacceptableOverdue(0, 0));
		assertEquals(taskManager.getTaskOverTimePercentage(0, 0),0);
		
		//--------------------------------------------------------------------------
		// Test Project 1 tasks

		assertTrue(taskManager.getTaskDescription(1, 0).equals("TASK 10"));
		assertTrue(taskManager.hasTaskEnded(1, 0));
		assertEquals(taskManager.getTaskStartTime(1, 0),task10Start);
		assertEquals(taskManager.getTaskEndTime(1, 0),task10End);
		assertTrue(taskManager.isTaskOnTime(1, 0));
		assertFalse(taskManager.isTaskUnacceptableOverdue(1, 0));
		assertEquals(taskManager.getTaskOverTimePercentage(1, 0),0);

		assertTrue(taskManager.getTaskDescription(1, 1).equals("TASK 11"));
		assertTrue(taskManager.hasTaskEnded(1, 1));
		assertEquals(taskManager.getTaskStartTime(1, 1),task11Start);
		assertEquals(taskManager.getTaskEndTime(1, 1),task11End);
		assertFalse(taskManager.isTaskOnTime(1, 1));						// Want FAILED task is niet op tijd
		assertFalse(taskManager.isTaskUnacceptableOverdue(1, 1));			// !!!!!!
		assertEquals(taskManager.getTaskOverTimePercentage(1, 1),0);		// !!!!!!

		assertTrue(taskManager.getTaskDescription(1, 2).equals("TASK 12"));
		assertFalse(taskManager.hasTaskEnded(1, 2));
		assertEquals(taskManager.getTaskStartTime(1, 2),null);
		assertEquals(taskManager.getTaskEndTime(1, 2),null);
		assertTrue(taskManager.isTaskOnTime(1, 2));
		assertFalse(taskManager.isTaskUnacceptableOverdue(1, 2));
		assertEquals(taskManager.getTaskOverTimePercentage(1, 2),0);

		assertTrue(taskManager.getTaskDescription(1, 3).equals("TASK 13"));
		assertFalse(taskManager.hasTaskEnded(1, 3));
		assertEquals(taskManager.getTaskStartTime(1, 3),null);
		assertEquals(taskManager.getTaskEndTime(1, 3),null);
		assertTrue(taskManager.isTaskOnTime(1, 3));
		assertFalse(taskManager.isTaskUnacceptableOverdue(1, 3));
		assertEquals(taskManager.getTaskOverTimePercentage(1, 3),0);
		
		//--------------------------------------------------------------------------
		// Test Project 2 tasks

		assertTrue(taskManager.getTaskDescription(2, 0).equals("TASK 20"));
		assertFalse(taskManager.hasTaskEnded(2, 0));
		assertEquals(taskManager.getTaskStartTime(2, 0),null);
		assertEquals(taskManager.getTaskEndTime(2, 0),null);
		assertTrue(taskManager.isTaskOnTime(2, 0));
		assertFalse(taskManager.isTaskUnacceptableOverdue(2, 0));				// !!!!!
		assertEquals(taskManager.getTaskOverTimePercentage(2, 0),0);
		
		//--------------------------------------------------------------------------
		// Test Project 3 tasks

		assertTrue(taskManager.getTaskDescription(3, 0).equals("TASK 30"));
		assertTrue(taskManager.hasTaskEnded(3, 0));
		assertEquals(taskManager.getTaskStartTime(3, 0),task30Start);
		assertEquals(taskManager.getTaskEndTime(3, 0),task30End);
		assertFalse(taskManager.isTaskOnTime(3, 0));
		assertTrue(taskManager.isTaskUnacceptableOverdue(3, 0));
		assertTrue(taskManager.getTaskOverTimePercentage(3, 0) > 0);

		assertTrue(taskManager.getTaskDescription(3, 1).equals("TASK 31"));
		assertFalse(taskManager.hasTaskEnded(3, 1));
		assertEquals(taskManager.getTaskStartTime(3, 1),null);
		assertEquals(taskManager.getTaskEndTime(3, 1),null);
		assertTrue(taskManager.isTaskOnTime(3, 1));
		assertFalse(taskManager.isTaskUnacceptableOverdue(3, 1));
		assertEquals(taskManager.getTaskOverTimePercentage(3, 1),0);
		
	}
	
	@Test
	public void badInputCasetest() {
		// Stap 1 is impliciet
		// Stap 2
		int numOfProj = taskManager.getProjectAmount();
		assertEquals(numOfProj,4);
		// Stap 3
		assertEquals(taskManager.getProjectDescription(4),null);
		assertEquals(taskManager.getProjectDueTime(4),null);
		assertEquals(taskManager.getProjectName(4),null);
		assertEquals(taskManager.getProjectStatus(4),null);
		assertEquals(taskManager.getProjectEndTime(4),null);
		assertEquals(taskManager.getTaskAmount(4),-1);
		assertEquals(taskManager.getAvailableTasks(4),null);
		assertEquals(taskManager.getProjectCreationTime(4),null);
		assertFalse(taskManager.isProjectEstimatedOnTime(4));

		assertEquals(taskManager.getTaskDescription(4, 0),null);
		assertFalse(taskManager.hasTaskEnded(4, 0));
		assertEquals(taskManager.getTaskStartTime(4, 0),null);
		assertEquals(taskManager.getTaskEndTime(4, 0),null);
		assertFalse(taskManager.isTaskOnTime(4, 0));
		assertFalse(taskManager.isTaskUnacceptableOverdue(4, 0));
		assertEquals(taskManager.getTaskOverTimePercentage(4, 0),-1);
		
		assertEquals(taskManager.getTaskDescription(3, 5),null);
		assertFalse(taskManager.hasTaskEnded(3, 5));
		assertEquals(taskManager.getTaskStartTime(3, 5),null);
		assertEquals(taskManager.getTaskEndTime(3, 5),null);
		assertFalse(taskManager.isTaskOnTime(3, 5));
		assertFalse(taskManager.isTaskUnacceptableOverdue(3, 5));
		assertEquals(taskManager.getTaskOverTimePercentage(3, 5),-1);
		
		
	}

}
