package taskMan.test.UseCases;

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

public class UseCase6UpdateTaskStatusTest {

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
	private final ArrayList<TaskView> task00Dependencies = new ArrayList<TaskView>(),
									 task01Dependencies = new ArrayList<TaskView>(),
									 task02Dependencies = new ArrayList<TaskView>(),
									 newTaskDependencies = new ArrayList<TaskView>(),
									 newTask2Dependencies = new ArrayList<TaskView>();

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
		
		assertTrue(taskManager.createTask(project0, "Design system", task00EstDur, task00Dev, task00Dependencies, null));		// TASK 1
		TaskView task00 = project0.getTasks().get(0);
		task01Dependencies.add(task00);
		assertTrue(taskManager.createTask(project0, "Implement Native", task01EstDur, task01Dev, task01Dependencies, null));	// TASK 2
		TaskView task01 = project0.getTasks().get(1);
		task02Dependencies.add(task01);

		assertTrue(taskManager.createTask(project0, "Test code", task02EstDur, task02Dev, task02Dependencies, null));			// TASK 3
		
		assertTrue(taskManager.advanceTimeTo(workDate)); // Omdat task updates enkel in het verleden kunnen gezet worden
	}
	
	@Test
	public void succesCaseALTFINISHEDTest() {
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		
		assertTrue(taskManager.setTaskFailed(project0, task00, task00StartDateGood, task00EndDateGood));
		assertTrue(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies, task00));

		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);
		TaskView task03 = project0.getTasks().get(3);
		assertTrue(taskManager.setTaskFinished(project0, task03, startDate, newTaskEndDateGood));
		// Step 6
		assertTrue(task00.getTaskStatusAsString().equalsIgnoreCase("failed"));
		assertTrue(task02.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isProjectFinished());
		
		assertTrue(task03.getTaskStatusAsString().equalsIgnoreCase("finished"));
		assertTrue(task01.getTaskStatusAsString().equalsIgnoreCase("available"));			// 
		
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
		assertTrue(task00.getTaskStatusAsString().equalsIgnoreCase("finished"));
		assertTrue(task01.getTaskStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task02.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
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
		assertTrue(task00.getTaskStatusAsString().equalsIgnoreCase("failed"));
		assertTrue(task01.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task02.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isProjectFinished());
		
	}
	
	@Test
	public void succesCaseMultiplePrereqTest() {
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);

		newTaskDependencies.add(task00);
		newTaskDependencies.add(task01);
		assertTrue(taskManager.createTask(project0, "Test1", newTaskDur, newTaskDev, newTaskDependencies, null));
		TaskView task03 = project0.getTasks().get(3);
		assertTrue(task03.getTaskPrerequisites().contains(task00));
		assertTrue(task03.getTaskPrerequisites().contains(task01));
		
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertTrue(taskManager.setTaskFinished(project0, task00, task00StartDateGood, task00EndDateGood));
		// Step 6
		assertTrue(task00.getTaskStatusAsString().equalsIgnoreCase("finished"));
		assertTrue(task01.getTaskStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task02.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isProjectFinished());
		assertTrue(task03.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
		
		assertTrue(taskManager.setTaskFinished(project0, task01, task01StartDateGood, task01EndDateGood));
		assertTrue(task00.getTaskStatusAsString().equalsIgnoreCase("finished"));
		assertTrue(task01.getTaskStatusAsString().equalsIgnoreCase("finished"));
		assertTrue(task02.getTaskStatusAsString().equalsIgnoreCase("available"));
		assertFalse(project0.isProjectFinished());
		assertTrue(task03.getTaskStatusAsString().equalsIgnoreCase("available"));
		
	}
	
	@Test
	public void flow6aMultiplePrereqFAILEDTest() {
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);

		newTaskDependencies.add(task00);
		newTaskDependencies.add(task01);
		assertTrue(taskManager.createTask(project0, "Test1", newTaskDur, newTaskDev, newTaskDependencies, null));
		TaskView task03 = project0.getTasks().get(3);
		assertTrue(task03.getTaskPrerequisites().contains(task00));
		assertTrue(task03.getTaskPrerequisites().contains(task01));
		
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertTrue(taskManager.setTaskFinished(project0, task00, task00StartDateGood, task00EndDateGood));
		// Step 6
		assertTrue(task00.getTaskStatusAsString().equalsIgnoreCase("finished"));
		assertTrue(task01.getTaskStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task02.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isProjectFinished());
		assertTrue(task03.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
		
		assertTrue(taskManager.setTaskFailed(project0, task01, task01StartDateGood, task01EndDateGood));
		assertTrue(task00.getTaskStatusAsString().equalsIgnoreCase("finished"));
		assertTrue(task01.getTaskStatusAsString().equalsIgnoreCase("failed"));
		assertTrue(task02.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isProjectFinished());
		assertTrue(task03.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
		
	}
	
	@Test
	public void succesCaseMultiplePrereqFAILEDALTTest() {
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);

		newTaskDependencies.add(task00);
		newTaskDependencies.add(task01);
		assertTrue(taskManager.createTask(project0, "Test1", newTaskDur, newTaskDev, newTaskDependencies, null));
		TaskView task03 = project0.getTasks().get(3);
		assertTrue(task03.getTaskPrerequisites().contains(task00));
		assertTrue(task03.getTaskPrerequisites().contains(task01));
		
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertTrue(taskManager.setTaskFinished(project0, task00, task00StartDateGood, task00EndDateGood));
		// Step 6
		assertTrue(task00.getTaskStatusAsString().equalsIgnoreCase("finished"));
		assertTrue(task01.getTaskStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task02.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isProjectFinished());
		assertTrue(task03.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
		
		assertTrue(taskManager.setTaskFailed(project0, task01, task01StartDateGood, task01EndDateGood));
		assertTrue(task00.getTaskStatusAsString().equalsIgnoreCase("finished"));
		assertTrue(task01.getTaskStatusAsString().equalsIgnoreCase("failed"));
		assertTrue(task02.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isProjectFinished());
		assertTrue(task03.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
		
		assertTrue(taskManager.createTask(project0, "Test2", newTaskDur, newTaskDev, newTask2Dependencies, task01));
		TaskView task04 = project0.getTasks().get(4); 
		assertTrue(taskManager.setTaskFinished(project0, task04, task02StartDateGood, task02EndDateGood));
		assertTrue(task00.getTaskStatusAsString().equalsIgnoreCase("finished"));
		assertTrue(task01.getTaskStatusAsString().equalsIgnoreCase("failed"));
		assertTrue(task02.getTaskStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task04.getTaskStatusAsString().equalsIgnoreCase("finished"));
		assertFalse(project0.isProjectFinished());
		
		assertTrue(task03.getTaskStatusAsString().equalsIgnoreCase("available"));
		
	}

	@Test
	public void flow3to5aTest() {
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);
		
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertFalse(taskManager.setTaskFailed(project0, task00, null, null));
		// Step 6
		assertTrue(task00.getTaskStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task01.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task02.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isProjectFinished());
		
	}

	@Test
	public void flow6aBadStartDateTest() {
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertFalse(taskManager.setTaskFinished(project0, task00, task00StartDateVeryBad1, task00EndDateVeryBad1));		//Start date van task is VOOR project start date
		// Step 6
		assertTrue(task00.getTaskStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task01.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task02.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isProjectFinished());
		
	}

	@Test
	public void flow6aBadEndDateTest() {
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertFalse(taskManager.setTaskFinished(project0, task00, task00StartDateVeryBad2, task00EndDateVeryBad2));		//End date van task is NA current time
		// Step 6
		assertTrue(task00.getTaskStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task01.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task02.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isProjectFinished());
		
	}

	@Test
	public void flow6aBadStatusUNAVAILABLEtoANYTHINGTest() {
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertFalse(taskManager.setTaskFinished(project0, task01, task00StartDateGood, task00EndDateGood));
		// Step 6
		assertTrue(task00.getTaskStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task01.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task02.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isProjectFinished());
		
		//----------------------------------------------------------------------------------
		
		assertFalse(taskManager.setTaskFailed(project0, task01, task00StartDateGood, task00EndDateGood));
		// Step 6
		assertTrue(task00.getTaskStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task01.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task02.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isProjectFinished());
		
	}
	
	@Test 
	public void flow6aBadStatusFAILEDtoANYTHINGTest() {
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);
		
		assertTrue(taskManager.setTaskFailed(project0, task00, task00StartDateGood, task00EndDateGood));
		
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertFalse(taskManager.setTaskFinished(project0, task00, task00StartDateGood, task00EndDateGood));
		// Step 6
		assertTrue(task00.getTaskStatusAsString().equalsIgnoreCase("failed"));
		assertTrue(task01.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task02.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isProjectFinished());
		
		//---------------------------------------------------------------------------------
		
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertFalse(taskManager.setTaskFailed(project0, task00, task00StartDateGood, task00EndDateGood));
		// Step 6
		assertTrue(task00.getTaskStatusAsString().equalsIgnoreCase("failed"));
		assertTrue(task01.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task02.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isProjectFinished());
	}
	
	@Test 
	public void flow6aBadStatusFINISHEDtoANYTHINGTest() {
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);
		
		assertTrue(taskManager.setTaskFinished(project0, task00, task00StartDateGood, task00EndDateGood));
		
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertFalse(taskManager.setTaskFinished(project0, task00, task00StartDateGood, task00EndDateGood));
		// Step 6
		
		assertTrue(task00.getTaskStatusAsString().equalsIgnoreCase("finished"));
		assertTrue(task01.getTaskStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task02.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isProjectFinished());
		
		//----------------------------------------------------------------------------------
		
		assertFalse(taskManager.setTaskFailed(project0, task00, task00StartDateGood, task00EndDateGood));
		// Step 6
		assertTrue(task00.getTaskStatusAsString().equalsIgnoreCase("finished"));
		assertTrue(task01.getTaskStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task02.getTaskStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isProjectFinished());
		
	}
	
	@Test
	public void SuccesCaseProjectFINISHEDTest() {

		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);
		
		assertTrue(taskManager.setTaskFinished(project0, task00, task00StartDateGood, task00EndDateGood));
		assertTrue(taskManager.setTaskFinished(project0, task01, task01StartDateGood, task01EndDateGood));
		
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertTrue(taskManager.setTaskFinished(project0, task02, task02StartDateGood, task02EndDateGood));
		// Step 6
		
		assertTrue(task00.getTaskStatusAsString().equalsIgnoreCase("finished"));
		assertTrue(task01.getTaskStatusAsString().equalsIgnoreCase("finished"));
		assertTrue(task02.getTaskStatusAsString().equalsIgnoreCase("finished"));
		assertTrue(project0.isProjectFinished());
		
	}
	
	

}
