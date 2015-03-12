package taskMan.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import taskMan.Facade;
import userInterface.IFacade;

public class UseCase4UpdateTaskStatusTest {

	private IFacade taskManager;
	private final LocalDateTime startDate = LocalDateTime.of(2015, 2, 9, 8, 0),
			project0DueDate = LocalDateTime.of(2015, 2, 13, 23, 59),
			workDate = LocalDateTime.of(2015, 2, 9, 16, 0),
			task00StartDateGood = startDate,
			task00EndDateGood = LocalDateTime.of(2015,2,9,10,0),
			task01StartDateGood = LocalDateTime.of(2015, 2, 9, 10, 10),
			task01EndDateGood = LocalDateTime.of(2015, 2, 9, 12, 0),
			task02StartDateGood = LocalDateTime.of(2015, 2, 9, 12, 10),
			task02EndDateGood = LocalDateTime.of(2015, 2, 9, 14, 0),
			task00StartDateVeryBad1 = LocalDateTime.of(2015,2,1,8,0),
			task00EndDateVeryBad1 = task00EndDateGood,
			task00StartDateVeryBad2 = task00StartDateGood,
			task00EndDateVeryBad2 = LocalDateTime.of(2015,2,9,17,0),
			newTaskEndDateGood = LocalDateTime.of(2015, 2, 9, 11, 0);
	private final int task00EstDur = 60,
			task01EstDur = 60,
			task02EstDur = 60,
			newTaskDur = 60;
	private final int task00Dev = 0,
			task01Dev = 50,
			task02Dev = 0,
			newTaskDev = 10;
	private final ArrayList<Integer> task00Dependencies = new ArrayList<Integer>(),
									 task01Dependencies = new ArrayList<Integer>(),
									 task02Dependencies = new ArrayList<Integer>(),
									 newTaskDependencies = new ArrayList<Integer>(),
									 newTask2Dependencies = new ArrayList<Integer>();

	/**
	 * - project 0 START 9 feb 8u DUE 13 feb midnight
	 * 		task 0			
	 * 		task 1 <- 0
	 * 		task 2 <- 1
	 */
	@Before
	public final void initialize() {
		taskManager = new Facade(startDate);

		assertTrue(taskManager.createProject("Test1", "testing 1", project0DueDate));

		
		assertTrue(taskManager.createTask(0, "Design system", task00EstDur, task00Dev, -1, task00Dependencies));		// TASK 1
		task01Dependencies.add(Integer.valueOf(0));
		assertTrue(taskManager.createTask(0, "Implement Native", task01EstDur, task01Dev, -1, task01Dependencies));	// TASK 2
		task02Dependencies.add(Integer.valueOf(1));

		assertTrue(taskManager.createTask(0, "Test code", task02EstDur, task02Dev, -1, task02Dependencies));			// TASK 3
		
		assertTrue(taskManager.advanceTimeTo(workDate)); // Omdat task updates enkel in het verleden kunnen gezet worden
	}
	
	@Test
	public void succesCaseALTFINISHEDTest() {
		assertTrue(taskManager.setTaskFailed(0, 0, task00StartDateGood, task00EndDateGood));
		assertTrue(taskManager.createTask(0, "A new TASK", newTaskDur, newTaskDev, 0, newTaskDependencies));

		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertTrue(taskManager.setTaskFinished(0, 3, startDate, newTaskEndDateGood));
		// Step 6
		assertTrue(taskManager.getTaskStatus(0,0).equals("failed"));
		assertTrue(taskManager.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskManager.getProjectStatus(0).equals("ongoing"));
		
		assertTrue(taskManager.getTaskStatus(0,3).equals("finished"));
		assertTrue(taskManager.getTaskStatus(0,1).equals("available"));			// 
		
	}

	@Test
	public void succesCaseFINISHEDTest() {
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertTrue(taskManager.setTaskFinished(0, 0, task00StartDateGood, task00EndDateGood));
		// Step 6
		assertTrue(taskManager.getTaskStatus(0,0).equals("finished"));
		assertTrue(taskManager.getTaskStatus(0,1).equals("available"));
		assertTrue(taskManager.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskManager.getProjectStatus(0).equals("ongoing"));
		
	}

	@Test
	public void succesCaseFAILEDTest() {
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertTrue(taskManager.setTaskFailed(0, 0, task00StartDateGood, task00EndDateGood));
		// Step 6
		assertTrue(taskManager.getTaskStatus(0,0).equals("failed"));
		assertTrue(taskManager.getTaskStatus(0,1).equals("unavailable"));
		assertTrue(taskManager.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskManager.getProjectStatus(0).equals("ongoing"));
		
	}
	
	@Test
	public void succesCaseMultiplePrereqTest() {

		newTaskDependencies.add(Integer.valueOf(0));
		newTaskDependencies.add(Integer.valueOf(1));
		assertTrue(taskManager.createTask(0, "Test1", newTaskDur, newTaskDev, -1, newTaskDependencies));
		assertTrue(taskManager.getTaskPrerequisitesFor(0, 3).contains(0));
		assertTrue(taskManager.getTaskPrerequisitesFor(0, 3).contains(1));
		
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertTrue(taskManager.setTaskFinished(0, 0, task00StartDateGood, task00EndDateGood));
		// Step 6
		assertTrue(taskManager.getTaskStatus(0,0).equals("finished"));
		assertTrue(taskManager.getTaskStatus(0,1).equals("available"));
		assertTrue(taskManager.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskManager.getProjectStatus(0).equals("ongoing"));
		assertTrue(taskManager.getTaskStatus(0, 3).equals("unavailable"));
		
		assertTrue(taskManager.setTaskFinished(0, 1, task01StartDateGood, task01EndDateGood));
		assertTrue(taskManager.getTaskStatus(0,0).equals("finished"));
		assertTrue(taskManager.getTaskStatus(0,1).equals("finished"));
		assertTrue(taskManager.getTaskStatus(0,2).equals("available"));
		assertTrue(taskManager.getProjectStatus(0).equals("ongoing"));
		assertTrue(taskManager.getTaskStatus(0, 3).equals("available"));
		
	}
	
	@Test
	public void flow6aMultiplePrereqFAILEDTest() {

		newTaskDependencies.add(Integer.valueOf(0));
		newTaskDependencies.add(Integer.valueOf(1));
		assertTrue(taskManager.createTask(0, "Test1", newTaskDur, newTaskDev, -1, newTaskDependencies));
		assertTrue(taskManager.getTaskPrerequisitesFor(0, 3).contains(0));
		assertTrue(taskManager.getTaskPrerequisitesFor(0, 3).contains(1));
		
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertTrue(taskManager.setTaskFinished(0, 0, task00StartDateGood, task00EndDateGood));
		// Step 6
		assertTrue(taskManager.getTaskStatus(0,0).equals("finished"));
		assertTrue(taskManager.getTaskStatus(0,1).equals("available"));
		assertTrue(taskManager.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskManager.getProjectStatus(0).equals("ongoing"));
		assertTrue(taskManager.getTaskStatus(0, 3).equals("unavailable"));
		
		assertTrue(taskManager.setTaskFailed(0, 1, task01StartDateGood, task01EndDateGood));
		assertTrue(taskManager.getTaskStatus(0,0).equals("finished"));
		assertTrue(taskManager.getTaskStatus(0,1).equals("failed"));
		assertTrue(taskManager.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskManager.getProjectStatus(0).equals("ongoing"));
		assertTrue(taskManager.getTaskStatus(0, 3).equals("unavailable"));
		
	}
	
	@Test
	public void succesCaseMultiplePrereqFAILEDALTTest() {

		newTaskDependencies.add(Integer.valueOf(0));
		newTaskDependencies.add(Integer.valueOf(1));
		assertTrue(taskManager.createTask(0, "Test1", newTaskDur, newTaskDev, -1, newTaskDependencies));
		assertTrue(taskManager.getTaskPrerequisitesFor(0, 3).contains(0));
		assertTrue(taskManager.getTaskPrerequisitesFor(0, 3).contains(1));
		
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertTrue(taskManager.setTaskFinished(0, 0, task00StartDateGood, task00EndDateGood));
		// Step 6
		assertTrue(taskManager.getTaskStatus(0,0).equals("finished"));
		assertTrue(taskManager.getTaskStatus(0,1).equals("available"));
		assertTrue(taskManager.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskManager.getProjectStatus(0).equals("ongoing"));
		assertTrue(taskManager.getTaskStatus(0, 3).equals("unavailable"));
		
		assertTrue(taskManager.setTaskFailed(0, 1, task01StartDateGood, task01EndDateGood));
		assertTrue(taskManager.getTaskStatus(0,0).equals("finished"));
		assertTrue(taskManager.getTaskStatus(0,1).equals("failed"));
		assertTrue(taskManager.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskManager.getProjectStatus(0).equals("ongoing"));
//		assertFalse(taskManager.isProjectEstimatedOnTime(0));									// Geen available tasks -> kan nooit eindigen
		assertTrue(taskManager.getTaskStatus(0, 3).equals("unavailable"));
		
		assertTrue(taskManager.createTask(0, "Test2", newTaskDur, newTaskDev, 1, newTask2Dependencies));
		assertTrue(taskManager.setTaskFinished(0, 4, task02StartDateGood, task02EndDateGood));
		assertTrue(taskManager.getTaskStatus(0,0).equals("finished"));
		assertTrue(taskManager.getTaskStatus(0,1).equals("failed"));
		assertTrue(taskManager.getTaskStatus(0,2).equals("available"));
		assertTrue(taskManager.getTaskStatus(0,4).equals("finished"));
		assertTrue(taskManager.getProjectStatus(0).equals("ongoing"));
		
		assertTrue(taskManager.getTaskStatus(0, 3).equals("available"));
		
	}

	@Test
	public void flow3to5aTest() {
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertFalse(taskManager.setTaskFailed(0, 0, null, null));
		// Step 6
		assertTrue(taskManager.getTaskStatus(0,0).equals("available"));
		assertTrue(taskManager.getTaskStatus(0,1).equals("unavailable"));
		assertTrue(taskManager.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskManager.getProjectStatus(0).equals("ongoing"));
		
	}

	@Test
	public void flow6aBadStartDateTest() {
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertFalse(taskManager.setTaskFinished(0, 0, task00StartDateVeryBad1, task00EndDateVeryBad1));		//Start date van task is VOOR project start date
		// Step 6
		assertTrue(taskManager.getTaskStatus(0,0).equals("available"));
		assertTrue(taskManager.getTaskStatus(0,1).equals("unavailable"));
		assertTrue(taskManager.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskManager.getProjectStatus(0).equals("ongoing"));
		
	}

	@Test
	public void flow6aBadEndDateTest() {
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertFalse(taskManager.setTaskFinished(0, 0, task00StartDateVeryBad2, task00EndDateVeryBad2));		//End date van task is NA current time
		// Step 6
		assertTrue(taskManager.getTaskStatus(0,0).equals("available"));
		assertTrue(taskManager.getTaskStatus(0,1).equals("unavailable"));
		assertTrue(taskManager.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskManager.getProjectStatus(0).equals("ongoing"));
		
	}

	@Test
	public void flow6aBadStatusUNAVAILABLEtoANYTHINGTest() {
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertFalse(taskManager.setTaskFinished(0, 1, task00StartDateGood, task00EndDateGood));
		// Step 6
		assertTrue(taskManager.getTaskStatus(0,0).equals("available"));
		assertTrue(taskManager.getTaskStatus(0,1).equals("unavailable"));
		assertTrue(taskManager.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskManager.getProjectStatus(0).equals("ongoing"));
		
		//----------------------------------------------------------------------------------
		
		assertFalse(taskManager.setTaskFailed(0, 1, task00StartDateGood, task00EndDateGood));
		// Step 6
		assertTrue(taskManager.getTaskStatus(0,0).equals("available"));
		assertTrue(taskManager.getTaskStatus(0,1).equals("unavailable"));
		assertTrue(taskManager.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskManager.getProjectStatus(0).equals("ongoing"));
		
	}
	
	@Test 
	public void flow6aBadStatusFAILEDtoANYTHINGTest() {

		assertTrue(taskManager.setTaskFailed(0, 0, task00StartDateGood, task00EndDateGood));
		
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertFalse(taskManager.setTaskFinished(0, 0, task00StartDateGood, task00EndDateGood));
		// Step 6
		assertTrue(taskManager.getTaskStatus(0,0).equals("failed"));
		assertTrue(taskManager.getTaskStatus(0,1).equals("unavailable"));
		assertTrue(taskManager.getTaskStatus(0,2).equals("unavailable"));
//		assertFalse(taskManager.isProjectEstimatedOnTime(0));									// Geen available tasks -> kan nooit eindigen
		assertTrue(taskManager.getProjectStatus(0).equals("ongoing"));
		
		//---------------------------------------------------------------------------------
		
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertFalse(taskManager.setTaskFailed(0, 0, task00StartDateGood, task00EndDateGood));
		// Step 6
		assertTrue(taskManager.getTaskStatus(0,0).equals("failed"));
		assertTrue(taskManager.getTaskStatus(0,1).equals("unavailable"));
		assertTrue(taskManager.getTaskStatus(0,2).equals("unavailable"));
//		assertFalse(taskManager.isProjectEstimatedOnTime(0));									// Geen available tasks -> kan nooit eindigen
		assertTrue(taskManager.getProjectStatus(0).equals("ongoing"));
	}
	
	@Test 
	public void flow6aBadStatusFINISHEDtoANYTHINGTest() {

		assertTrue(taskManager.setTaskFinished(0, 0, task00StartDateGood, task00EndDateGood));
		
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertFalse(taskManager.setTaskFinished(0, 0, task00StartDateGood, task00EndDateGood));
		// Step 6
		assertTrue(taskManager.getTaskStatus(0,0).equals("finished"));
		assertTrue(taskManager.getTaskStatus(0,1).equals("available"));
		assertTrue(taskManager.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskManager.getProjectStatus(0).equals("ongoing"));
		
		//----------------------------------------------------------------------------------
		
		assertFalse(taskManager.setTaskFailed(0, 0, task00StartDateGood, task00EndDateGood));
		// Step 6
		assertTrue(taskManager.getTaskStatus(0,0).equals("finished"));
		assertTrue(taskManager.getTaskStatus(0,1).equals("available"));
		assertTrue(taskManager.getTaskStatus(0,2).equals("unavailable"));
		assertTrue(taskManager.getProjectStatus(0).equals("ongoing"));
		
	}
	
	@Test
	public void SuccesCaseProjectFINISHEDTest() {

		assertTrue(taskManager.setTaskFinished(0, 0, task00StartDateGood, task00EndDateGood));
		assertTrue(taskManager.setTaskFinished(0, 1, task01StartDateGood, task01EndDateGood));
		
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertTrue(taskManager.setTaskFinished(0, 2, task02StartDateGood, task02EndDateGood));
		// Step 6
		assertTrue(taskManager.getTaskStatus(0,0).equals("finished"));
		assertTrue(taskManager.getTaskStatus(0,1).equals("finished"));
		assertTrue(taskManager.getTaskStatus(0,2).equals("finished"));
		assertTrue(taskManager.getProjectStatus(0).equals("finished"));
		
	}
	
	

}
