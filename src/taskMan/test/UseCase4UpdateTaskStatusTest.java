package taskMan.test;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import taskMan.TaskMan;

public class UseCase4UpdateTaskStatusTest {

	private TaskMan taskMan;
	private final LocalDateTime startDate = LocalDateTime.of(2015, 2, 9, 8, 0),
			project0StartDate = startDate,
			project0DueDate = LocalDateTime.of(2015, 2, 13, 23, 59),
			workDate = LocalDateTime.of(2015, 2, 9, 16, 0),
			task00StartDateGood = startDate,
			task00EndDateGood = LocalDateTime.of(2015,2,9,15,0),
			task00StartDateVeryBad1 = LocalDateTime.of(2015,2,1,8,0),
			task00EndDateVeryBad1 = task00EndDateGood,
			task00StartDateVeryBad2 = task00StartDateGood,
			task00EndDateVeryBad2 = LocalDateTime.of(2015,2,9,17,0);
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

		taskMan.createProject("Test1", "testing 1", project0StartDate, project0DueDate);

		
		taskMan.createTask(0, "Design system", task00EstDur, task00Dev, -1, task00Dependencies);		// TASK 1
		task01Dependencies.add(Integer.valueOf(1));
		taskMan.createTask(0, "Implement Native", task01EstDur, task01Dev, -1, task01Dependencies);	// TASK 2
		task02Dependencies.add(Integer.valueOf(2));
		taskMan.createTask(0, "Test code", task02EstDur, task02Dev, -1, task02Dependencies);			// TASK 3
		task03Dependencies.add(Integer.valueOf(2));
		taskMan.createTask(0, "Document code", task03EstDur, task03Dev, -1, task03Dependencies);		// TASK 4
		
		taskMan.advanceTimeTo(workDate); // Omdat task updates enkel in het verleden kunnen bestaan
	}
	
	//TODO Testen op aanapssing wanneer ALT wordt vervuld

	@Test
	public void succesCaseFINISHEDTest() {
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertTrue(taskMan.updateTaskDetails(0, 0, task00StartDateGood, task00EndDateGood, "finished"));
		// Step 6
		assertTrue(taskMan.getTaskStatus(0,0).equals("finished"));
		assertTrue(taskMan.getTaskStatus(0,1).equals("available"));
		assertTrue(taskMan.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(0,3).equals("unavailable"));
		
	}

	@Test
	public void succesCaseFAILEDTest() {
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertTrue(taskMan.updateTaskDetails(0, 0, task00StartDateGood, task00EndDateGood, "failed"));
		// Step 6
		assertTrue(taskMan.getTaskStatus(0,0).equals("failed"));
		assertTrue(taskMan.getTaskStatus(0,1).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(0,3).equals("unavailable"));
		
	}

	@Test
	public void flow3to5aTest() {
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertFalse(taskMan.updateTaskDetails(0, 0, null, null, null));
		// Step 6
		assertTrue(taskMan.getTaskStatus(0,0).equals("available"));
		assertTrue(taskMan.getTaskStatus(0,1).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(0,3).equals("unavailable"));
		
	}

	@Test
	public void flow6aBadStartDateTest() {
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		taskMan.updateTaskDetails(0, 0, task00StartDateVeryBad1, task00EndDateVeryBad1, "finished");		//Start date van task is VOOR project start date
		// Step 6
		assertTrue(taskMan.getTaskStatus(0,0).equals("available"));
		assertTrue(taskMan.getTaskStatus(0,1).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(0,3).equals("unavailable"));
		
	}

	@Test
	public void flow6aBadEndDateTest() {
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		taskMan.updateTaskDetails(0, 0, task00StartDateVeryBad2, task00EndDateVeryBad2, "finished");		//End date van task is NA current time
		// Step 6
		assertTrue(taskMan.getTaskStatus(0,0).equals("available"));
		assertTrue(taskMan.getTaskStatus(0,1).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(0,3).equals("unavailable"));
		
	}

	@Test
	public void flow6aBadStatusAVAILABLEtoAVAILABLETest() {
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		taskMan.updateTaskDetails(0, 0, task00StartDateGood, task00EndDateGood, "available");
		// Step 6
		assertTrue(taskMan.getTaskStatus(0,0).equals("available"));
		assertTrue(taskMan.getTaskStatus(0,1).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(0,3).equals("unavailable"));
		
	}

	@Test
	public void flow6aBadStatusUNAVAILABLEtoAVAILABLETest() {
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		taskMan.updateTaskDetails(1, 2, task00StartDateGood, task00EndDateGood, "available");
		// Step 6
		assertTrue(taskMan.getTaskStatus(1,1).equals("available"));
		assertTrue(taskMan.getTaskStatus(1,2).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,3).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,4).equals("unavailable"));
		
	}

	@Test
	public void flow6aBadStatusAVAILABLEtoUNAVAILABLETest() {
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		taskMan.updateTaskDetails(1, 1, task00StartDateGood, task00EndDateGood, "unavailable");
		// Step 6
		assertTrue(taskMan.getTaskStatus(1,1).equals("available"));
		assertTrue(taskMan.getTaskStatus(1,2).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,3).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,4).equals("unavailable"));
		
	}

	@Test
	public void flow6aBadStatusUNAVAILABLEtoFINISHEDFAILEDTest() {
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		taskMan.updateTaskDetails(1, 2, task00StartDateGood, task00EndDateGood, "finished");
		// Step 6
		assertTrue(taskMan.getTaskStatus(1,1).equals("available"));
		assertTrue(taskMan.getTaskStatus(1,2).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,3).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,4).equals("unavailable"));
		
		//----------------------------------------------------------------------------------
		
		taskMan.updateTaskDetails(1, 3, task00StartDateGood, task00EndDateGood, "failed");
		// Step 6
		assertTrue(taskMan.getTaskStatus(1,1).equals("available"));
		assertTrue(taskMan.getTaskStatus(1,2).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,3).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,4).equals("unavailable"));
		
	}

}
