package taskMan.test;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import taskMan.TaskMan;

public class UseCase1ShowProjectsTest {

	private TaskMan taskMan;
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
	
	@Before
	public final void initialize() {
		// INIT systeem en maak het eerste project aan, samen met zijn TASK
		taskMan = new TaskMan(startDate);
			assertTrue(taskMan.createProject("Project 0", "Describing proj 0", project0DueDate));
				assertTrue(taskMan.createTask(0, "Task 00", task00EstDur, task00Dev, -1, task00Dependencies));		// 00 AVAILABLE
				
		// Stap verder:
		// maak het tweede project aan en maak zijn TASK lijst
		assertTrue(taskMan.advanceTimeTo(workdate1));
			assertTrue(taskMan.createProject("Project 1", "Describing proj 1", project1DueDate));
				assertTrue(taskMan.createTask(1, "TASK 10", task10EstDur, task10Dev, -1, task10Dependencies));			// 10 AVAILABLE
				assertTrue(taskMan.createTask(1, "TASK 11", task11EstDur, task11Dev, -1, task11Dependencies));			// 11 AVAILABLE
				assertTrue(task12Dependencies.add(Integer.valueOf(0))); assertTrue(task12Dependencies.add(Integer.valueOf(1)));
				assertTrue(taskMan.createTask(1, "TASK 12", task12EstDur, task12Dev, -1, task12Dependencies));			// 12 UNAVAILABLE
		
		// Stap verder:
		// maak het derde project aan, samen met zijn TASK
		taskMan.advanceTimeTo(workdate2);
			assertTrue(taskMan.createProject("Project 2", "Describing project 2", project2DueDate));
				assertTrue(taskMan.createTask(2, "TASK 20", task20EstDur, task20Dev, -1, task20Dependencies));		// 20 AVAILABLE
					
		// Stap verder:
		// maak TASK 0,0 af -> project 0 is finished
		// update project 1
		assertTrue(taskMan.advanceTimeTo(workdate3));
			assertTrue(taskMan.setTaskFinished(0, 0, task00Start, task00End));										// 00 FINISHED
			//----------------------------------------------------
			assertTrue(taskMan.setTaskFinished(1, 0, task10Start, task10End));										// 10 FINISHED
			assertTrue(taskMan.setTaskFailed(1, 1, task11Start, task11End));										// 11 FAILED
			assertTrue(taskMan.createTask(1, "TASK 13", task13EstDur, task13Dev, 1, task13Dependencies));			// 13 AVAILABLE
			
		// Stap verder:
		// maak het vierde project aan en maak zijn TASK lijst
		assertTrue(taskMan.advanceTimeTo(workdate4));
			assertTrue(taskMan.createProject("Project 3", "Describing project 3", project3DueDate));
				assertTrue(taskMan.createTask(3, "Task 30", task30EstDur, task30Dev, -1, task30Dependencies));		// 30 AVAILABLE
				assertTrue(taskMan.createTask(3, "Task 31", task31EstDur, task31Dev, -1, task31Dependencies));		// 31 AVAILABLE
				
		// Stap verder:
		// maak task 3,0 FAILED
		assertTrue(taskMan.advanceTimeTo(workdate5));
			assertTrue(taskMan.setTaskFailed(3, 0, task30Start, task30End));										// 30 FAILED DELAYED

	}
	
	@Test
	public void SuccesCasetest() {
		// Stap 1 is impliciet
		// Stap 2
		int numOfProj = taskMan.getProjectAmount();
		assertEquals(numOfProj,4);
		// Stap 3
		assertTrue(taskMan.getProjectDescription(0).equals("Describing project 0"));
		assertEquals(taskMan.getProjectDueTime(0),project0DueDate);
		assertTrue(taskMan.getProjectName(0).equals("Project 0"));
		assertTrue(taskMan.getProjectStatus(0).equals("finished"));
		assertEquals(taskMan.getProjectEndTime(0),task00End);
		assertEquals(taskMan.getTaskAmount(0),1);
		assertEquals(taskMan.getAvailableTasks(0).size(),0);
		assertEquals(taskMan.getProjectCreationTime(0),startDate);
		assertTrue(taskMan.isOnTime(0));

		assertTrue(taskMan.getProjectDescription(1).equals("Describing project 1"));
		assertEquals(taskMan.getProjectDueTime(1),project1DueDate);
		assertTrue(taskMan.getProjectName(1).equals("Project 1"));
		assertTrue(taskMan.getProjectStatus(1).equals("ongoing"));
		assertEquals(taskMan.getProjectEndTime(1),null);
		assertEquals(taskMan.getTaskAmount(1),4);
		assertEquals(taskMan.getAvailableTasks(1).size(),1);
		assertEquals(taskMan.getProjectCreationTime(1),workdate1);
		assertTrue(taskMan.isOnTime(1));

		assertTrue(taskMan.getProjectDescription(2).equals("Describing project 2"));
		assertEquals(taskMan.getProjectDueTime(2),project2DueDate);
		assertTrue(taskMan.getProjectName(2).equals("Project 2"));
		assertTrue(taskMan.getProjectStatus(2).equals("ongoing"));
		assertEquals(taskMan.getProjectEndTime(2),null);
		assertEquals(taskMan.getTaskAmount(2),1);
		assertEquals(taskMan.getAvailableTasks().size(),1);
		assertEquals(taskMan.getProjectCreationTime(2),workdate2);
		assertFalse(taskMan.isOnTime(2));											// DELAYED

		assertTrue(taskMan.getProjectDescription(3).equals("Describing project 3"));
		assertEquals(taskMan.getProjectDueTime(3),project3DueDate);
		assertTrue(taskMan.getProjectName(3).equals("Project 3"));
		assertTrue(taskMan.getProjectStatus(3).equals("ongoing"));
		assertEquals(taskMan.getProjectEndTime(3),null);
		assertEquals(taskMan.getTaskAmount(3),2);
		assertEquals(taskMan.getAvailableTasks().size(),1);
		assertEquals(taskMan.getProjectCreationTime(3),workdate4);
		assertTrue(taskMan.isOnTime(0));
		
		//--------------------------------------------------------------------------
		// Test Project 0 tasks

		assertTrue(taskMan.getTaskDescription(0, 0).equals("TASK 00"));
		assertTrue(taskMan.hasTaskEnded(0, 0));
		assertEquals(taskMan.getTaskStartTime(0, 0),task00Start);
		assertEquals(taskMan.getTaskEndTime(0, 0),task00End);
		assertTrue(taskMan.isTaskOnTime(0, 0));
		assertFalse(taskMan.isTaskUnacceptableOverdue(0, 0));
		assertEquals(taskMan.getTaskOvertimePercentage(0, 0),0);
		
		//--------------------------------------------------------------------------
		// Test Project 1 tasks

		assertTrue(taskMan.getTaskDescription(1, 0).equals("TASK 10"));
		assertTrue(taskMan.hasTaskEnded(1, 0));
		assertEquals(taskMan.getTaskStartTime(1, 0),task10Start);
		assertEquals(taskMan.getTaskEndTime(1, 0),task10End);
		assertTrue(taskMan.isTaskOnTime(1, 0));
		assertFalse(taskMan.isTaskUnacceptableOverdue(1, 0));
		assertEquals(taskMan.getTaskOvertimePercentage(1, 0),0);

		assertTrue(taskMan.getTaskDescription(1, 1).equals("TASK 11"));
		assertTrue(taskMan.hasTaskEnded(1, 1));
		assertEquals(taskMan.getTaskStartTime(1, 1),task10Start);
		assertEquals(taskMan.getTaskEndTime(1, 1),task10End);
		assertFalse(taskMan.isTaskOnTime(1, 1));						// Want FAILED task is niet op tijd
		assertFalse(taskMan.isTaskUnacceptableOverdue(1, 1));			// !!!!!!
		assertEquals(taskMan.getTaskOvertimePercentage(1, 1),0);		// !!!!!!

		assertTrue(taskMan.getTaskDescription(1, 2).equals("TASK 12"));
		assertFalse(taskMan.hasTaskEnded(1, 2));
		assertEquals(taskMan.getTaskStartTime(1, 2),null);
		assertEquals(taskMan.getTaskEndTime(1, 2),null);
		assertTrue(taskMan.isTaskOnTime(1, 2));
		assertFalse(taskMan.isTaskUnacceptableOverdue(1, 2));
		assertEquals(taskMan.getTaskOvertimePercentage(1, 2),0);

		assertTrue(taskMan.getTaskDescription(1, 3).equals("TASK 13"));
		assertTrue(taskMan.hasTaskEnded(1, 3));
		assertEquals(taskMan.getTaskStartTime(1, 3),null);
		assertEquals(taskMan.getTaskEndTime(1, 3),null);
		assertTrue(taskMan.isTaskOnTime(1, 3));
		assertFalse(taskMan.isTaskUnacceptableOverdue(1, 3));
		assertEquals(taskMan.getTaskOvertimePercentage(1, 3),0);
		
		//--------------------------------------------------------------------------
		// Test Project 2 tasks

		assertTrue(taskMan.getTaskDescription(2, 0).equals("TASK 20"));
		assertFalse(taskMan.hasTaskEnded(2, 0));
		assertEquals(taskMan.getTaskStartTime(2, 0),null);
		assertEquals(taskMan.getTaskEndTime(2, 0),null);
		assertTrue(taskMan.isTaskOnTime(2, 0));
		assertFalse(taskMan.isTaskUnacceptableOverdue(2, 0));				// !!!!!
		assertEquals(taskMan.getTaskOvertimePercentage(2, 0),0);
		
		//--------------------------------------------------------------------------
		// Test Project 3 tasks

		assertTrue(taskMan.getTaskDescription(3, 0).equals("TASK 30"));
		assertFalse(taskMan.hasTaskEnded(3, 0));
		assertEquals(taskMan.getTaskStartTime(3, 0),null);
		assertEquals(taskMan.getTaskEndTime(3, 0),null);
		assertFalse(taskMan.isTaskOnTime(3, 0));
		assertTrue(taskMan.isTaskUnacceptableOverdue(3, 0));
		assertTrue(taskMan.getTaskOvertimePercentage(3, 0) > 0);

		assertTrue(taskMan.getTaskDescription(3, 1).equals("TASK 30"));
		assertFalse(taskMan.hasTaskEnded(3, 1));
		assertEquals(taskMan.getTaskStartTime(3, 1),null);
		assertEquals(taskMan.getTaskEndTime(3, 1),null);
		assertTrue(taskMan.isTaskOnTime(3, 1));
		assertFalse(taskMan.isTaskUnacceptableOverdue(3, 1));
		assertEquals(taskMan.getTaskOvertimePercentage(3, 1),0);
		
	}
	
	//TODO verkeerde IDs testen
	
	@Test
	public void badInputCasetest() {
		
	}

}
