package taskMan.test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import taskMan.TaskMan;
import taskMan.TaskStatus;

public class UseCase4UpdateTaskStatusTest {

	private TaskMan taskMan;
	private final LocalDateTime startDate = LocalDateTime.of(2015, 2, 9, 8, 0),
			project1StartDate = startDate,
			project1DueDate = LocalDateTime.of(2015, 2, 13, 23, 59),
			workDate = LocalDateTime.of(2015, 2, 9, 16, 0),
			task11StartDateGood = startDate,
			task11EndDateGood = LocalDateTime.of(2015,2,9,15,0),
			task11StartDateVeryBad1 = LocalDateTime.of(2015,2,1,8,0),
			task11EndDateVeryBad1 = task11EndDateGood,
			task11StartDateVeryBad2 = task11StartDateGood,
			task11EndDateVeryBad2 = LocalDateTime.of(2015,2,9,17,0);
	private final int task11EstDur = 8*60,
			task12EstDur = 16*60,
			task13EstDur = 8*60,
			task14EstDur = 8*60;
	private final int task11Dev = 0,
			task12Dev = 50,
			task13Dev = 0,
			task14Dev = 0;
	private final ArrayList<Integer> task11Dependencies = new ArrayList(),
									 task12Dependencies = new ArrayList(),
									 task13Dependencies = new ArrayList(),
									 task14Dependencies = new ArrayList();

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

		taskMan.createProject("Test1", "testing 1", project1StartDate, project1DueDate);

		
		taskMan.createTask(1, "Design system", task11EstDur, task11Dev, null, task11Dependencies);		// TASK 1
		task12Dependencies.add(Integer.valueOf(1));
		taskMan.createTask(1, "Implement Native", task12EstDur, task12Dev, null, task12Dependencies);	// TASK 2
		task13Dependencies.add(Integer.valueOf(2));
		taskMan.createTask(1, "Test code", task13EstDur, task13Dev, null, task13Dependencies);			// TASK 3
		task14Dependencies.add(Integer.valueOf(2));
		taskMan.createTask(1, "Document code", task14EstDur, task14Dev, null, task14Dependencies);		// TASK 4
		
		taskMan.advanceTimeTo(workDate); // Omdat task updates enkel in het verleden kunnen bestaan
	}
	
	//TODO Testen op aanapssing wanneer ALT wordt vervuld

	@Test
	public void succesCaseFINISHEDTest() {
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		taskMan.updateTaskDetails(1, 1, task11StartDateGood, task11EndDateGood, "finished");
		// Step 6
		assertTrue(taskMan.getTaskStatus(1,1).equals("finished"));
		assertTrue(taskMan.getTaskStatus(1,2).equals("available"));
		assertTrue(taskMan.getTaskStatus(1,3).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,4).equals("unavailable"));
		
	}

	@Test
	public void succesCaseFAILEDTest() {
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		taskMan.updateTaskDetails(1, 1, task11StartDateGood, task11EndDateGood, "failed");
		// Step 6
		assertTrue(taskMan.getTaskStatus(1,1).equals("failed"));
		assertTrue(taskMan.getTaskStatus(1,2).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,3).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,4).equals("unavailable"));
		
	}

	@Test
	public void flow3to5aTest() {
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		taskMan.updateTaskDetails(1, 1, null, null, null);
		// Step 6
		assertTrue(taskMan.getTaskStatus(1,1).equals("available"));
		assertTrue(taskMan.getTaskStatus(1,2).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,3).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,4).equals("unavailable"));
		
	}

	@Test
	public void flow6aBadStartDateTest() {
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		taskMan.updateTaskDetails(1, 1, task11StartDateVeryBad1, task11EndDateVeryBad1, "finished");		//Start date van task is VOOR project start date
		// Step 6
		assertTrue(taskMan.getTaskStatus(1,1).equals("available"));
		assertTrue(taskMan.getTaskStatus(1,2).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,3).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,4).equals("unavailable"));
		
	}

	@Test
	public void flow6aBadEndDateTest() {
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		taskMan.updateTaskDetails(1, 1, task11StartDateVeryBad2, task11EndDateVeryBad2, "finished");		//End date van task is NA current time
		// Step 6
		assertTrue(taskMan.getTaskStatus(1,1).equals("available"));
		assertTrue(taskMan.getTaskStatus(1,2).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,3).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,4).equals("unavailable"));
		
	}

	@Test
	public void flow6aBadStatusAVAILABLEtoAVAILABLETest() {
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		taskMan.updateTaskDetails(1, 1, task11StartDateGood, task11EndDateGood, "available");
		// Step 6
		assertTrue(taskMan.getTaskStatus(1,1).equals("available"));
		assertTrue(taskMan.getTaskStatus(1,2).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,3).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,4).equals("unavailable"));
		
	}

	@Test
	public void flow6aBadStatusUNAVAILABLEtoAVAILABLETest() {
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		taskMan.updateTaskDetails(1, 2, task11StartDateGood, task11EndDateGood, "available");
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
		taskMan.updateTaskDetails(1, 1, task11StartDateGood, task11EndDateGood, "unavailable");
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
		taskMan.updateTaskDetails(1, 2, task11StartDateGood, task11EndDateGood, "finished");
		// Step 6
		assertTrue(taskMan.getTaskStatus(1,1).equals("available"));
		assertTrue(taskMan.getTaskStatus(1,2).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,3).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,4).equals("unavailable"));
		
		//----------------------------------------------------------------------------------
		
		taskMan.updateTaskDetails(1, 3, task11StartDateGood, task11EndDateGood, "failed");
		// Step 6
		assertTrue(taskMan.getTaskStatus(1,1).equals("available"));
		assertTrue(taskMan.getTaskStatus(1,2).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,3).equals("unavailable"));
		assertTrue(taskMan.getTaskStatus(1,4).equals("unavailable"));
		
	}

}
