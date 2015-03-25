package taskMan.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import taskMan.Facade;
import taskMan.view.ProjectView;
import taskMan.view.TaskView;
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
		ProjectView project0 = taskManager.getProjects().get(0);
		
		assertTrue(taskManager.createTask(project0, "Design system", task00EstDur, task00Dev, task00Dependencies, -1));		// TASK 1
		task01Dependencies.add(Integer.valueOf(0));
		assertTrue(taskManager.createTask(project0, "Implement Native", task01EstDur, task01Dev, task01Dependencies, -1));	// TASK 2
		task02Dependencies.add(Integer.valueOf(1));

		assertTrue(taskManager.createTask(project0, "Test code", task02EstDur, task02Dev, task02Dependencies, -1));			// TASK 3
		
		assertTrue(taskManager.advanceTimeTo(workDate)); // Omdat task updates enkel in het verleden kunnen gezet worden
	}
	
	@Test
	public void succesCaseALTFINISHEDTest() {
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		
		assertTrue(taskManager.setTaskFailed(project0, task00, task00StartDateGood, task00EndDateGood));
		assertTrue(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies, 0));

		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);
		TaskView task03 = project0.getTasks().get(3);
		assertTrue(taskManager.setTaskFinished(project0, task03, startDate, newTaskEndDateGood));
		// Step 6
		assertTrue(task00.isFailed());
		assertTrue(task02.isUnavailable());
		assertFalse(project0.isProjectFinished());
		
		assertTrue(task03.isFinished());
		assertTrue(task01.isAvailable());			// 
		
	}

	@Test
	public void succesCaseFINISHEDTest() {
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertTrue(taskManager.setTaskFinished(project0, task00, task00StartDateGood, task00EndDateGood));
		// Step 6
		assertTrue(task00.isFinished());
		assertTrue(task01.isAvailable());
		assertTrue(task02.isUnavailable());
		assertFalse(project0.isProjectFinished());
		
	}

	@Test
	public void succesCaseFAILEDTest() {
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertTrue(taskManager.setTaskFailed(project0, task00, task00StartDateGood, task00EndDateGood));
		// Step 6
		assertTrue(task00.isFailed());
		assertTrue(task01.isUnavailable);
		assertTrue(task02.isUnavailable());
		assertFalse(project0.isProjectFinished());
		
	}
	
	@Test
	public void succesCaseMultiplePrereqTest() {
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);

		newTaskDependencies.add(Integer.valueOf(0));
		newTaskDependencies.add(Integer.valueOf(1));
		assertTrue(taskManager.createTask(project0, "Test1", newTaskDur, newTaskDev, newTaskDependencies, -1));
		TaskView task03 = project0.getTasks().get(3);
		assertTrue(task03.getTaskPrerequisites().contains(task00));
		assertTrue(task03.getTaskPrerequisites().contains(task01));
		
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertTrue(taskManager.setTaskFinished(project0, task00, task00StartDateGood, task00EndDateGood));
		// Step 6
		assertTrue(task00.isFinished());
		assertTrue(task01.isAvailable());
		assertTrue(task02.isUnavailable());
		assertFalse(project0.isProjectFinished());
		assertTrue(task03.isUnavailable());
		
		assertTrue(taskManager.setTaskFinished(project0, task01, task01StartDateGood, task01EndDateGood));
		assertTrue(task00.isFinished());
		assertTrue(task01.isFinished());
		assertTrue(task02.isAvailable());
		assertFalse(project0.isProjectFinished());
		assertTrue(task03.isAvailable());
		
	}
	
	@Test
	public void flow6aMultiplePrereqFAILEDTest() {
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);

		newTaskDependencies.add(Integer.valueOf(0));
		newTaskDependencies.add(Integer.valueOf(1));
		assertTrue(taskManager.createTask(project0, "Test1", newTaskDur, newTaskDev, newTaskDependencies, -1));
		TaskView task03 = project0.getTasks().get(3);
		assertTrue(task03.getTaskPrerequisites().contains(task00));
		assertTrue(task03.getTaskPrerequisites().contains(task01));
		
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertTrue(taskManager.setTaskFinished(project0, task00, task00StartDateGood, task00EndDateGood));
		// Step 6
		assertTrue(task00.isFinished());
		assertTrue(task01.isAvailable());
		assertTrue(task02.isUnavailable());
		assertFalse(project0.isProjectFinished());
		assertTrue(task03.isUnavailable());
		
		assertTrue(taskManager.setTaskFailed(project0, task01, task01StartDateGood, task01EndDateGood));
		assertTrue(task00.isFinished());
		assertTrue(task01.isFailed());
		assertTrue(task02.isUnavailable());
		assertFalse(project0.isProjectFinished());
		assertTrue(task03.isUnavailable());
		
	}
	
	@Test
	public void succesCaseMultiplePrereqFAILEDALTTest() {
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);

		newTaskDependencies.add(Integer.valueOf(0));
		newTaskDependencies.add(Integer.valueOf(1));
		assertTrue(taskManager.createTask(project0, "Test1", newTaskDur, newTaskDev, newTaskDependencies, -1));
		TaskView task03 = project0.getTasks().get(3);
		assertTrue(task03.getTaskPrerequisites().contains(task00));
		assertTrue(task03.getTaskPrerequisites().contains(task01));
		
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertTrue(taskManager.setTaskFinished(project0, task00, task00StartDateGood, task00EndDateGood));
		// Step 6
		assertTrue(task00.isFinished());
		assertTrue(task01.isAvailable());
		assertTrue(task02.isUnavailable());
		assertFalse(project0.isProjectFinished());
		assertTrue(task03.isUnavailable());
		
		assertTrue(taskManager.setTaskFailed(project0, task01, task01StartDateGood, task01EndDateGood));
		assertTrue(task00.isFinished());
		assertTrue(task01.isFailed());
		assertTrue(task02.isUnavailable());
		assertFalse(project0.isProjectFinished());
		assertTrue(task03.isUnavailable());
		
		assertTrue(taskManager.createTask(project0, "Test2", newTaskDur, newTaskDev, newTask2Dependencies, 1));
		TaskView task04 = project0.getTasks().get(4); 
		assertTrue(taskManager.setTaskFinished(project0, task04, task02StartDateGood, task02EndDateGood));
		assertTrue(task00.isFinished());
		assertTrue(task01.isFailed());
		assertTrue(task02.isAvailable());
		assertTrue(task04.isFinished());
		assertFalse(project0.isProjectFinished());
		
		assertTrue(task03.isAvailable());
		
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
		assertFalse(taskManager.isProjectFinished(0));
		
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
		assertFalse(taskManager.isProjectFinished(0));
		
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
		assertFalse(taskManager.isProjectFinished(0));
		
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
		assertFalse(taskManager.isProjectFinished(0));
		
		//----------------------------------------------------------------------------------
		
		assertFalse(taskManager.setTaskFailed(0, 1, task00StartDateGood, task00EndDateGood));
		// Step 6
		assertTrue(taskManager.getTaskStatus(0,0).equals("available"));
		assertTrue(taskManager.getTaskStatus(0,1).equals("unavailable"));
		assertTrue(taskManager.getTaskStatus(0,2).equals("unavailable"));
		assertFalse(taskManager.isProjectFinished(0));
		
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
		assertFalse(taskManager.isProjectFinished(0));
		
		//---------------------------------------------------------------------------------
		
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertFalse(taskManager.setTaskFailed(0, 0, task00StartDateGood, task00EndDateGood));
		// Step 6
		assertTrue(taskManager.getTaskStatus(0,0).equals("failed"));
		assertTrue(taskManager.getTaskStatus(0,1).equals("unavailable"));
		assertTrue(taskManager.getTaskStatus(0,2).equals("unavailable"));
		assertFalse(taskManager.isProjectFinished(0));
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
		assertFalse(taskManager.isProjectFinished(0));
		
		//----------------------------------------------------------------------------------
		
		assertFalse(taskManager.setTaskFailed(0, 0, task00StartDateGood, task00EndDateGood));
		// Step 6
		assertTrue(taskManager.getTaskStatus(0,0).equals("finished"));
		assertTrue(taskManager.getTaskStatus(0,1).equals("available"));
		assertTrue(taskManager.getTaskStatus(0,2).equals("unavailable"));
		assertFalse(taskManager.isProjectFinished(0));
		
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
		assertTrue(taskManager.isProjectFinished(0));
		
	}
	
	

}
