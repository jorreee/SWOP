package taskMan.test.UseCases;

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

public class UseCase5ResolveConflictTest {

	private IFacade taskManager;
	private final LocalDateTime startDate = LocalDateTime.of(2015, 2, 9, 8, 0),
			project0DueDate = LocalDateTime.of(2015, 2, 13, 23, 59),
			workDate = LocalDateTime.of(2015, 2, 9, 16, 0),
			task00StartDateGood = startDate,
			task00EndDateGood = LocalDateTime.of(2015,2,9,10,0),
			task01StartDateGood = LocalDateTime.of(2015, 2, 9, 10, 10),
			task01EndDateGood = LocalDateTime.of(2015, 2, 9, 12, 0),
			task02StartDateGood = LocalDateTime.of(2015, 2, 9, 12, 10),
			task02EndDateGood = LocalDateTime.of(2015, 2, 9, 14, 0);
	private final int task00EstDur = 60,
			task01EstDur = 60,
			task02EstDur = 60;
	private final int task00Dev = 0,
			task01Dev = 50,
			task02Dev = 0;
	private final ArrayList<TaskView> task00Dependencies = new ArrayList<TaskView>(),
			task01Dependencies = new ArrayList<TaskView>(),
			task02Dependencies = new ArrayList<TaskView>();
	private final Map<ResourceView,Integer> task00Res = new HashMap<ResourceView,Integer>(),
			task01Res = new HashMap<ResourceView,Integer>(),
			task02Res = new HashMap<ResourceView,Integer>();
	private final ArrayList<ResourceView> task00ConcreteResources = new ArrayList<ResourceView>(),
			task01ConcreteResources = new ArrayList<ResourceView>(),
			task02ConcreteResources = new ArrayList<ResourceView>();
	private final List<ResourceView> devList1 = new ArrayList<ResourceView>(),
			devList2 = new ArrayList<ResourceView>();
	private ResourceView weer, blunderbus;
	private final Optional<LocalTime> emptyAvailabilityPeriodStart = Optional.empty(),
			emptyAvailabilityPeriodEnd = Optional.empty();
	
	/**
	 * - project 0 START 9 feb 8u DUE 13 feb midnight
	 * 		task 0			
	 * 		task 1 
	 * 		task 2 
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

		assertTrue(taskManager.createTask(project0, "Design system", task00EstDur, task00Dev, task00Dependencies,task00Res, null));		// TASK 1
		TaskView task00 = project0.getTasks().get(0);
		task01Dependencies.add(task00);
		assertTrue(taskManager.createTask(project0, "Implement Native", task01EstDur, task01Dev, task01Dependencies,task01Res, null));	// TASK 2
		TaskView task01 = project0.getTasks().get(1);
		task02Dependencies.add(task01);

		assertTrue(taskManager.createTask(project0, "Test code", task02EstDur, task02Dev, task02Dependencies,task02Res, null));			// TASK 3

		//assertTrue(taskManager.advanceTimeTo(workDate)); // Omdat task updates enkel in het verleden kunnen gezet worden
	}
	
	@Test
	public void successCaseTest(){
		ProjectView project00 = taskManager.getProjects().get(0);
		TaskView task00 = project00.getTasks().get(0);
		TaskView task01 = project00.getTasks().get(1);
		TaskView task02 = project00.getTasks().get(2);
	}
}
