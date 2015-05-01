package taskMan.test.UseCases;

import static org.junit.Assert.*;

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
			task00StartDateGood = startDate,
			task02StartDateGood = LocalDateTime.of(2015, 2, 9, 12, 10);
	private final int task00EstDur = 60,
			task01EstDur = 60,
			task02EstDur = 60,
			task03EstDur = 60;
	private final int task00Dev = 0,
			task01Dev = 50,
			task02Dev = 0,
			task03Dev = 0;
	private final ArrayList<TaskView> task00Dependencies = new ArrayList<TaskView>(),
			task01Dependencies = new ArrayList<TaskView>(),
			task02Dependencies = new ArrayList<TaskView>(),
			task03Dependencies = new ArrayList<TaskView>();
	private final Map<ResourceView,Integer> task00Res = new HashMap<ResourceView,Integer>(),
			task01Res = new HashMap<ResourceView,Integer>(),
			task02Res = new HashMap<ResourceView,Integer>(),
			task03Res = new HashMap<ResourceView,Integer>();
	private final ArrayList<ResourceView> task00ConcreteResources = new ArrayList<ResourceView>(),
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
	 * 		task 1 <-0
	 * 		task 2 
	 */
	@Before
	public final void initialize() {
		taskManager = new Facade(startDate);

		assertTrue(taskManager.createProject("project1", "testing 1", project0DueDate));
		ProjectView project0 = taskManager.getProjects().get(0);

		//create resources
		assertTrue(taskManager.createResourcePrototype("car", emptyAvailabilityPeriodStart, emptyAvailabilityPeriodEnd));
		for(int i = 0;i<=5;i++){
			assertTrue(taskManager.declareConcreteResource("car" + i, taskManager.getResourcePrototypes().get(0)));
		}
		assertTrue(taskManager.createResourcePrototype("whiteboard", emptyAvailabilityPeriodStart, emptyAvailabilityPeriodEnd));
		for(int i = 0;i<=5;i++){
			assertTrue(taskManager.declareConcreteResource("whiteboard" + i, taskManager.getResourcePrototypes().get(1)));
		}
		assertTrue(taskManager.createResourcePrototype("beamer", emptyAvailabilityPeriodStart, emptyAvailabilityPeriodEnd));
		assertTrue(taskManager.declareConcreteResource("TheOnlyBeamer", taskManager.getResourcePrototypes().get(2)));
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
		task03Res.put(taskManager.getResourcePrototypes().get(2), 1);

		assertTrue(taskManager.createTask(project0, "task00", task00EstDur, task00Dev, task00Dependencies,task00Res, null));		// TASK 0
		TaskView task00 = project0.getTasks().get(0);
		task01Dependencies.add(task00);
		assertTrue(taskManager.createTask(project0, "task01", task01EstDur, task01Dev, task01Dependencies,task01Res, null));	// TASK 1

		assertTrue(taskManager.createTask(project0, "task02", task02EstDur, task02Dev, task02Dependencies,task02Res, null));			// TASK 2
		
		assertTrue(taskManager.createTask(project0, "task03", task03EstDur, task03Dev, task03Dependencies, task03Res, null));

		//assertTrue(taskManager.advanceTimeTo(workDate)); // Omdat task updates enkel in het verleden kunnen gezet worden
	}
	
	@Test
	public void successCaseTestConflictingDeveloper(){
		//A test with a conflicting planned developer 
		ProjectView project00 = taskManager.getProjects().get(0);
		TaskView task00 = project00.getTasks().get(0);
		TaskView task02 = project00.getTasks().get(2);
		
		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(1));
		
		task02ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task02ConcreteResources.add(taskManager.getResourcePrototypes().get(1));
		
		assertTrue(taskManager.planTask(project00, task00, task00StartDateGood, task00ConcreteResources, devList1));
		assertTrue(taskManager.planRawTask(project00, task02, task00StartDateGood, task02ConcreteResources, devList1));
		
		Map<ProjectView, List<TaskView>> conflicts = taskManager.findConflictingPlannings(task02);
		assertEquals(1,conflicts.keySet().size());
		project00 = conflicts.keySet().iterator().next();
		assertEquals(1,conflicts.get(project00).size());
		assertEquals(task00, conflicts.get(project00).get(0));
		assertTrue(taskManager.planTask(project00, task00, task02StartDateGood, task00ConcreteResources, devList1));
		
		conflicts = taskManager.findConflictingPlannings(task02);
		assertTrue(conflicts.isEmpty());
	}
	
	@Test
	public void successCaseTestConflictingResource(){
		//A test with conflicting resource
		ProjectView project00 = taskManager.getProjects().get(0);
		TaskView task03 = project00.getTasks().get(3);
		
		task03ConcreteResources.add(taskManager.getResourcePrototypes().get(2));
		assertTrue(taskManager.planTask(project00, task03, task00StartDateGood, task03ConcreteResources, devList1));
		
		HashMap<ResourceView, Integer >reqRes = new HashMap<ResourceView,Integer>();
		reqRes.put(taskManager.getResourcePrototypes().get(2), 1);
		assertTrue(taskManager.createTask(project00, "test", 60, 5, new ArrayList<TaskView>(), reqRes, null));
		TaskView test = project00.getTasks().get(4);
		ArrayList<ResourceView> concRes = new ArrayList<ResourceView>();
		concRes.add(taskManager.getResourcePrototypes().get(2));
		assertTrue(taskManager.planRawTask(project00, test, task00StartDateGood, concRes, devList2));
		Map<ProjectView, List<TaskView>> conflicts = taskManager.findConflictingPlannings(test);
		assertEquals(1,conflicts.keySet().size());
		project00 = conflicts.keySet().iterator().next();
		assertEquals(1,conflicts.get(project00).size());
		assertEquals(task03, conflicts.get(project00).get(0));
		assertTrue(taskManager.planTask(project00, task03, task02StartDateGood, task03ConcreteResources, devList1));
		
		conflicts = taskManager.findConflictingPlannings(test);
		assertTrue(conflicts.isEmpty());
	}
	
	@Test
	public void successCaseTestMultipleTasksConflict(){
		//A test with multiple conflicting tasks
		ProjectView project00 = taskManager.getProjects().get(0);
		TaskView task00 = project00.getTasks().get(0);
		TaskView task03 = project00.getTasks().get(3);
		
		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(1));
		
		task03ConcreteResources.add(taskManager.getResourcePrototypes().get(2));

		assertTrue(taskManager.planTask(project00, task00, task00StartDateGood, task00ConcreteResources, devList1));
		assertTrue(taskManager.planTask(project00, task03, task00StartDateGood, task03ConcreteResources, devList2));

		HashMap<ResourceView, Integer >reqRes = new HashMap<ResourceView,Integer>();
		reqRes.put(taskManager.getResourcePrototypes().get(2), 1);
		assertTrue(taskManager.createTask(project00, "test", 60, 5, new ArrayList<TaskView>(), reqRes, null));
		TaskView test = project00.getTasks().get(4);
		ArrayList<ResourceView> concRes = new ArrayList<ResourceView>();
		concRes.add(taskManager.getResourcePrototypes().get(2));
		assertTrue(taskManager.planRawTask(project00, test, task00StartDateGood, concRes, devList1));
		
		Map<ProjectView, List<TaskView>> conflicts = taskManager.findConflictingPlannings(test);
		assertEquals(1,conflicts.keySet().size());
		project00 = conflicts.keySet().iterator().next();
		assertEquals(2,conflicts.get(project00).size());
		assertTrue(conflicts.get(project00).contains(task00));
		assertTrue(conflicts.get(project00).contains(task03));
		assertTrue(taskManager.planTask(project00, task03, task02StartDateGood, task03ConcreteResources, devList2));
		assertTrue(taskManager.planTask(project00, task00, task02StartDateGood, task00ConcreteResources, devList1));
		
		conflicts = taskManager.findConflictingPlannings(test);
		assertTrue(conflicts.isEmpty());
	}
	
	@Test
	public void successCaseTestMultipleProjectsConflict(){
		//A test with conflicts across multiple projects
		ProjectView project00 = taskManager.getProjects().get(0);
		TaskView task00 = project00.getTasks().get(0);
		
		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(0));
		task00ConcreteResources.add(taskManager.getResourcePrototypes().get(1));
		
		assertTrue(taskManager.planTask(project00, task00, task00StartDateGood, task00ConcreteResources, devList1));
		
		assertTrue(taskManager.createProject("project2", "test", project0DueDate));
		ProjectView project01 = taskManager.getProjects().get(1);
		assertTrue(taskManager.createTask(project01, "task10", 60, 10, new ArrayList<TaskView>(), task03Res, null));
		TaskView task10 = project01.getTasks().get(0);
		
		task03ConcreteResources.add(taskManager.getResourcePrototypes().get(2));
		
		assertTrue(taskManager.planTask(project01, task10, task00StartDateGood, task03ConcreteResources, devList2));
		
		HashMap<ResourceView, Integer >reqRes = new HashMap<ResourceView,Integer>();
		reqRes.put(taskManager.getResourcePrototypes().get(2), 1);
		assertTrue(taskManager.createTask(project00, "test", 60, 5, new ArrayList<TaskView>(), reqRes, null));
		TaskView test = project00.getTasks().get(4);
		ArrayList<ResourceView> concRes = new ArrayList<ResourceView>();
		concRes.add(taskManager.getResourcePrototypes().get(2));
		assertTrue(taskManager.planRawTask(project00, test, task00StartDateGood, concRes, devList1));	
		
		Map<ProjectView, List<TaskView>> conflicts = taskManager.findConflictingPlannings(test);
		assertEquals(2,conflicts.keySet().size());
		
		ProjectView[] con = new ProjectView[2];
		con = conflicts.keySet().toArray(con);
		if(con[0].getName().equals("project1")){
			project00 = con[0];
			project01 = con[1];
		}else{
			project00 = con[1];
			project01 = con[0];
		}
		assertEquals(1,conflicts.get(project00).size());
		assertEquals(1,conflicts.get(project01).size());
		assertTrue(conflicts.get(project00).contains(task00));
		assertTrue(conflicts.get(project01).contains(task10));
		
		assertTrue(taskManager.planTask(project00, task00, task02StartDateGood, task00ConcreteResources, devList1));
		assertTrue(taskManager.planTask(project01, task10, task02StartDateGood, task03ConcreteResources, devList2));
		
		conflicts = taskManager.findConflictingPlannings(test);
		assertTrue(conflicts.isEmpty());
	}
}
