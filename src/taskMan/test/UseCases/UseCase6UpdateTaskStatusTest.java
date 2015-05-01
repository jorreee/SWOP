package taskMan.test.UseCases;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import taskMan.Facade;
import taskMan.view.ProjectView;
import taskMan.view.ResourceView;
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
	private final Map<ResourceView,Integer> task00Res = new HashMap<ResourceView,Integer>(),
			task01Res = new HashMap<ResourceView,Integer>(),
			task02Res = new HashMap<ResourceView,Integer>(),
			newTaskRes = new HashMap<ResourceView,Integer>();
	private final ArrayList<ResourceView> task00ConcreteResources = new ArrayList<ResourceView>(),
			task01ConcreteResources = new ArrayList<ResourceView>(),
			task02ConcreteResources = new ArrayList<ResourceView>(),
			task03ConcreteResources = new ArrayList<ResourceView>();
	private final List<ResourceView> devList1 = new ArrayList<ResourceView>(),
			devList2 = new ArrayList<ResourceView>();
	private ResourceView weer, blunderbus;
	private final Optional<LocalTime> emptyAvailabilityPeriodStart = Optional.empty(),
			emptyAvailabilityPeriodEnd = Optional.empty();
			

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

		//create resources
		taskManager.createResourcePrototype("car", emptyAvailabilityPeriodStart, emptyAvailabilityPeriodEnd);
		for(int i = 0;i<=5;i++){
			taskManager.declareConcreteResource("car" + i, taskManager.getResourcePrototypes().get(0));
		}
		taskManager.createResourcePrototype("whiteboard", emptyAvailabilityPeriodStart, emptyAvailabilityPeriodEnd);
		for(int i = 0;i<=5;i++){
			taskManager.declareConcreteResource("whiteboard" + i, taskManager.getResourcePrototypes().get(1));
		}
		taskManager.createDeveloper("Weer");
		taskManager.createDeveloper("Blunderbus");
		weer = taskManager.getDeveloperList().get(0);
		blunderbus = taskManager.getDeveloperList().get(1);
		devList1.add(weer);
		devList2.add(blunderbus);

		//assign resources to Hashsets for later use
		task00Res.put(taskManager.getResourcePrototypes().get(0), 1);
		task00Res.put(taskManager.getResourcePrototypes().get(1), 1);
		task01Res.put(taskManager.getResourcePrototypes().get(0), 2);
		task01Res.put(taskManager.getResourcePrototypes().get(1), 1);
		task02Res.put(taskManager.getResourcePrototypes().get(0), 1);
		task02Res.put(taskManager.getResourcePrototypes().get(1), 1);
		newTaskRes.put(taskManager.getResourcePrototypes().get(1), 1);

		assertTrue(taskManager.createTask(project0, "Design system", task00EstDur, task00Dev, task00Dependencies,task00Res, null));		// TASK 1
		TaskView task00 = project0.getTasks().get(0);
		task01Dependencies.add(task00);
		assertTrue(taskManager.createTask(project0, "Implement Native", task01EstDur, task01Dev, task01Dependencies,task01Res, null));	// TASK 2
		TaskView task01 = project0.getTasks().get(1);
		task02Dependencies.add(task01);

		assertTrue(taskManager.createTask(project0, "Test code", task02EstDur, task02Dev, task02Dependencies,task02Res, null));			// TASK 3

		assertTrue(taskManager.advanceTimeTo(workDate)); // Omdat task updates enkel in het verleden kunnen gezet worden
	}

	@Test
	public void succesCaseALTFINISHEDTest() {
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);

		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(1));		
		assertTrue(taskManager.planTask(project0, task00, task00StartDateGood, task00ConcreteResources, devList1));
		assertTrue(taskManager.setTaskExecuting(project0, task00, task00StartDateGood));
		assertTrue(taskManager.setTaskFailed(project0, task00, task00EndDateGood));
		assertTrue(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies,task00Res, task00));

		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);
		TaskView task03 = project0.getTasks().get(3);
		assertTrue(taskManager.planTask(project0, task03, startDate, task00ConcreteResources, devList1));
		assertTrue(taskManager.setTaskExecuting(project0, task03, startDate));
		assertTrue(taskManager.setTaskFinished(project0, task03, newTaskEndDateGood));
		// Step 6
		assertTrue(task00.getStatusAsString().equalsIgnoreCase("failed"));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isFinished());
		assertTrue(task03.getStatusAsString().equalsIgnoreCase("finished"));
		task01ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task01ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task01ConcreteResources.add(taskManager.getResourcePrototypes().get(1));
		assertTrue(taskManager.planTask(project0, task01, task01StartDateGood, task01ConcreteResources, devList1));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("available"));			// 

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
		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(1));
		assertTrue(taskManager.planTask(project0, task00, task00StartDateGood,task00ConcreteResources, devList1));
		assertTrue(taskManager.setTaskExecuting(project0, task00, task00StartDateGood));
		assertTrue(taskManager.setTaskFinished(project0, task00, task00EndDateGood));
		// Step 6
		task01ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task01ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task01ConcreteResources.add(taskManager.getResourcePrototypes().get(1));
		assertTrue(taskManager.planTask(project0, task01, task01StartDateGood, task01ConcreteResources, devList1));
		assertTrue(task00.getStatusAsString().equalsIgnoreCase("finished"));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isFinished());

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
		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(1));
		assertTrue(taskManager.planTask(project0, task00, task00StartDateGood,task00ConcreteResources, devList1));
		assertTrue(taskManager.setTaskExecuting(project0, task00, task00StartDateGood));
		assertTrue(taskManager.setTaskFailed(project0, task00, task00EndDateGood));
		// Step 6
		assertTrue(task00.getStatusAsString().equalsIgnoreCase("failed"));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isFinished());

	}

	@Test
	public void succesCaseMultiplePrereqTest() {
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);

		newTaskDependencies.add(task00);
		newTaskDependencies.add(task01);
		assertTrue(taskManager.createTask(project0, "Test1", newTaskDur, newTaskDev, newTaskDependencies, newTaskRes, null));
		TaskView task03 = project0.getTasks().get(3);
		assertTrue(task03.getPrerequisites().contains(task00));
		assertTrue(task03.getPrerequisites().contains(task01));

		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(1));
		assertTrue(taskManager.planTask(project0, task00, task00StartDateGood,task00ConcreteResources, devList1));
		assertTrue(taskManager.setTaskExecuting(project0, task00, task00StartDateGood));
		assertTrue(taskManager.setTaskFinished(project0, task00, task00EndDateGood));
		// Step 6
		assertTrue(task00.getStatusAsString().equalsIgnoreCase("finished"));
		task01ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task01ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task01ConcreteResources.add(taskManager.getResourcePrototypes().get(1));
		assertTrue(taskManager.planTask(project0, task01, task01StartDateGood, task01ConcreteResources, devList1));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isFinished());
		assertTrue(task03.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(taskManager.setTaskExecuting(project0, task01, task01StartDateGood));
		assertTrue(taskManager.setTaskFinished(project0, task01, task01EndDateGood));
		assertTrue(task00.getStatusAsString().equalsIgnoreCase("finished"));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("finished"));
		task02ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task02ConcreteResources.add(taskManager.getResourcePrototypes().get(1));
		assertTrue(taskManager.planTask(project0, task02, task02StartDateGood, task02ConcreteResources, devList1));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("available"));
		assertFalse(project0.isFinished());
		task03ConcreteResources.add(taskManager.getResourcePrototypes().get(1));
		assertTrue(taskManager.planTask(project0, task03, task02StartDateGood, task03ConcreteResources, devList2));
		assertTrue(task03.getStatusAsString().equalsIgnoreCase("available"));

	}

	@Test
	public void flow6aMultiplePrereqFAILEDTest() {
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);

		newTaskDependencies.add(task00);
		newTaskDependencies.add(task01);
		assertTrue(taskManager.createTask(project0, "Test1", newTaskDur, newTaskDev, newTaskDependencies, newTaskRes, null));
		TaskView task03 = project0.getTasks().get(3);
		assertTrue(task03.getPrerequisites().contains(task00));
		assertTrue(task03.getPrerequisites().contains(task01));

		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(1));
		assertTrue(taskManager.planTask(project0, task00, task00StartDateGood, task00ConcreteResources, devList1));
		assertTrue(taskManager.setTaskExecuting(project0, task00, task00StartDateGood));
		assertTrue(taskManager.setTaskFinished(project0, task00, task00EndDateGood));
		// Step 6
		assertTrue(task00.getStatusAsString().equalsIgnoreCase("finished"));
		task01ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task01ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task01ConcreteResources.add(taskManager.getResourcePrototypes().get(1));
		assertTrue(taskManager.planTask(project0, task01, task01StartDateGood, task01ConcreteResources, devList1));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isFinished());
		assertTrue(task03.getStatusAsString().equalsIgnoreCase("unavailable"));

		assertTrue(taskManager.setTaskExecuting(project0, task01, task01StartDateGood));
		assertTrue(taskManager.setTaskFailed(project0, task01, task01EndDateGood));
		assertTrue(task00.getStatusAsString().equalsIgnoreCase("finished"));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("failed"));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isFinished());
		assertTrue(task03.getStatusAsString().equalsIgnoreCase("unavailable"));

	}

	@Test
	public void succesCaseMultiplePrereqFAILEDALTTest() {
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);

		newTaskDependencies.add(task00);
		newTaskDependencies.add(task01);
		assertTrue(taskManager.createTask(project0, "Test1", newTaskDur, newTaskDev, newTaskDependencies, newTaskRes, null));
		TaskView task03 = project0.getTasks().get(3);
		assertTrue(task03.getPrerequisites().contains(task00));
		assertTrue(task03.getPrerequisites().contains(task01));

		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(1));
		assertTrue(taskManager.planTask(project0, task00, task00StartDateGood, task00ConcreteResources, devList1));
		assertTrue(taskManager.setTaskExecuting(project0, task00, task00StartDateGood));
		assertTrue(taskManager.setTaskFinished(project0, task00, task00EndDateGood));
		// Step 6
		assertTrue(task00.getStatusAsString().equalsIgnoreCase("finished"));
		task01ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task01ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task01ConcreteResources.add(taskManager.getResourcePrototypes().get(1));
		assertTrue(taskManager.planTask(project0, task01, task01StartDateGood, task01ConcreteResources, devList1));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isFinished());
		assertTrue(task03.getStatusAsString().equalsIgnoreCase("unavailable"));

		assertTrue(taskManager.setTaskExecuting(project0, task01, task01StartDateGood));
		assertTrue(taskManager.setTaskFailed(project0, task01, task01EndDateGood));
		assertTrue(task00.getStatusAsString().equalsIgnoreCase("finished"));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("failed"));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isFinished());
		assertTrue(task03.getStatusAsString().equalsIgnoreCase("unavailable"));

		assertTrue(taskManager.createTask(project0, "Test2", newTaskDur, newTaskDev, newTask2Dependencies, task01Res, task01));
		TaskView task04 = project0.getTasks().get(4); 
		task02ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task02ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task02ConcreteResources.add(taskManager.getResourcePrototypes().get(1));
		assertTrue(taskManager.planTask(project0, task04, task02StartDateGood, task02ConcreteResources, devList1));
		assertTrue(taskManager.setTaskExecuting(project0, task04, task02StartDateGood));
		assertTrue(taskManager.setTaskFinished(project0, task04, task02EndDateGood));
		assertTrue(task00.getStatusAsString().equalsIgnoreCase("finished"));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("failed"));
		task02ConcreteResources.remove(0);
		assertTrue(taskManager.planTask(project0, task02, task02StartDateGood, task02ConcreteResources, devList1));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task04.getStatusAsString().equalsIgnoreCase("finished"));
		assertFalse(project0.isFinished());

		task02ConcreteResources.remove(0);
		assertTrue(taskManager.planTask(project0, task03, task02StartDateGood, task02ConcreteResources, devList2));
		assertTrue(task03.getStatusAsString().equalsIgnoreCase("available"));

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
		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(1));
		assertTrue(taskManager.planTask(project0, task00, task00StartDateGood, task00ConcreteResources, devList1));
		assertFalse(taskManager.setTaskExecuting(project0, task00, null));
		assertFalse(taskManager.setTaskFailed(project0, task00, null));
		// Step 6
		assertTrue(task00.getStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isFinished());

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
		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(1));
		assertTrue(taskManager.planTask(project0, task00, task00StartDateGood, task00ConcreteResources, devList1));
		assertFalse(taskManager.setTaskExecuting(project0, task00, task00StartDateVeryBad1));
		assertFalse(taskManager.setTaskFinished(project0, task00, task00EndDateVeryBad1));		//Start date van task is VOOR project start date
		// Step 6
		assertTrue(task00.getStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isFinished());

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
		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(1));
		assertTrue(taskManager.planTask(project0, task00, task00StartDateGood, task00ConcreteResources, devList1));
		assertTrue(taskManager.setTaskExecuting(project0, task00, task00StartDateGood));
		assertFalse(taskManager.setTaskFinished(project0, task00, task00EndDateVeryBad2));		//End date van task is NA current time
		// Step 6
		assertTrue(task00.getStatusAsString().equalsIgnoreCase("executing"));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isFinished());

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
		assertFalse(taskManager.planTask(project0, task01, task01StartDateGood, task01ConcreteResources, devList1));
		assertFalse(taskManager.setTaskExecuting(project0, task01, task01StartDateGood));
		assertFalse(taskManager.setTaskFinished(project0, task01, task01EndDateGood));
		// Step 6
		assertTrue(task00.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isFinished());

		//----------------------------------------------------------------------------------

		assertFalse(taskManager.planTask(project0, task01, task01StartDateGood, task01ConcreteResources, devList1));
		assertFalse(taskManager.setTaskExecuting(project0, task01, task01StartDateGood));
		assertFalse(taskManager.setTaskFailed(project0, task01, task01EndDateGood));
		// Step 6
		assertTrue(task00.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isFinished());

	}

	@Test 
	public void flow6aBadStatusFAILEDtoANYTHINGTest() {
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);

		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(1));
		assertTrue(taskManager.planTask(project0, task00, task00StartDateGood, task00ConcreteResources, devList1));
		assertTrue(taskManager.setTaskExecuting(project0, task00, task00StartDateGood));
		assertTrue(taskManager.setTaskFailed(project0, task00, task00EndDateGood));

		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertFalse(taskManager.setTaskFinished(project0, task00, task00EndDateGood));
		// Step 6
		assertTrue(task00.getStatusAsString().equalsIgnoreCase("failed"));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isFinished());

		//---------------------------------------------------------------------------------

		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertFalse(taskManager.setTaskFailed(project0, task00, task00EndDateGood));
		// Step 6
		assertTrue(task00.getStatusAsString().equalsIgnoreCase("failed"));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isFinished());
	}

	@Test 
	public void flow6aBadStatusFINISHEDtoANYTHINGTest() {
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);

		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(1));
		assertTrue(taskManager.planTask(project0, task00, task00StartDateGood, task00ConcreteResources, devList1));
		assertTrue(taskManager.setTaskExecuting(project0, task00, task00StartDateGood));
		assertTrue(taskManager.setTaskFinished(project0, task00, task00EndDateGood));

		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		assertFalse(taskManager.setTaskFinished(project0, task00, task00EndDateGood));
		// Step 6

		task01ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task01ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task01ConcreteResources.add(taskManager.getResourcePrototypes().get(1));
		assertTrue(taskManager.planTask(project0, task01, task01StartDateGood, task01ConcreteResources, devList1));
		
		assertTrue(task00.getStatusAsString().equalsIgnoreCase("finished"));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isFinished());

		//----------------------------------------------------------------------------------

		assertFalse(taskManager.setTaskFailed(project0, task00, task00EndDateGood));
		// Step 6
		assertTrue(task00.getStatusAsString().equalsIgnoreCase("finished"));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertFalse(project0.isFinished());

	}

	@Test
	public void SuccesCaseProjectFINISHEDTest() {

		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);

		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(1));
		assertTrue(taskManager.planTask(project0, task00, task00StartDateGood, task00ConcreteResources, devList1));
		assertTrue(taskManager.setTaskExecuting(project0, task00, task00StartDateGood));
		assertTrue(taskManager.setTaskFinished(project0, task00, task00EndDateGood));
		task01ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task01ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task01ConcreteResources.add(taskManager.getResourcePrototypes().get(1));		assertTrue(taskManager.planTask(project0, task01, task01StartDateGood, task01ConcreteResources, devList1));
		assertTrue(taskManager.setTaskExecuting(project0, task01, task01StartDateGood));
		assertTrue(taskManager.setTaskFinished(project0, task01, task01EndDateGood));

		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		task02ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task02ConcreteResources.add(taskManager.getResourcePrototypes().get(1));
		assertTrue(taskManager.planTask(project0, task02, task02StartDateGood, task02ConcreteResources, devList1));
		assertTrue(taskManager.setTaskExecuting(project0, task02, task02StartDateGood));
		assertTrue(taskManager.setTaskFinished(project0, task02, task02EndDateGood));
		// Step 6

		assertTrue(task00.getStatusAsString().equalsIgnoreCase("finished"));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("finished"));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("finished"));
		assertTrue(project0.isFinished());

	}



}
