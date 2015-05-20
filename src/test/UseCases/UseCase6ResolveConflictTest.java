package test.UseCases;

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

import company.BranchManager;
import company.taskMan.ProjectView;
import company.taskMan.project.TaskView;
import company.taskMan.resource.ResourceView;
import userInterface.IFacade;

public class UseCase6ResolveConflictTest {

	private IFacade branchManager;
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
		branchManager = new BranchManager(startDate);

		branchManager.createResourcePrototype("car", emptyAvailabilityPeriodStart, emptyAvailabilityPeriodEnd);
		branchManager.createResourcePrototype("whiteboard", emptyAvailabilityPeriodStart, emptyAvailabilityPeriodEnd);
		branchManager.createResourcePrototype("beamer", emptyAvailabilityPeriodStart, emptyAvailabilityPeriodEnd);
		
		branchManager.initializeBranch("Leuven");
		
		branchManager.createProject("project1", "testing 1", project0DueDate);
		ProjectView project0 = branchManager.getProjects().get(0);

		//create resources
		for(int i = 0;i<=5;i++){
			branchManager.declareConcreteResource("car" + i, branchManager.getResourcePrototypes().get(0));
		}

		for(int i = 0;i<=5;i++){
			branchManager.declareConcreteResource("whiteboard" + i, branchManager.getResourcePrototypes().get(1));
		}
		branchManager.declareConcreteResource("TheOnlyBeamer", branchManager.getResourcePrototypes().get(2));
		branchManager.createDeveloper("Weer");
		branchManager.createDeveloper("Blunderbus");
		weer = branchManager.getDeveloperList().get(0);
		blunderbus = branchManager.getDeveloperList().get(1);
		devList1.add(weer);
		devList2.add(blunderbus);

		//assign resources to Hashsets for later use
		task00Res.put(branchManager.getResourcePrototypes().get(0), 1);
		task00Res.put(branchManager.getResourcePrototypes().get(1), 1);
		task01Res.put(branchManager.getResourcePrototypes().get(0), 2);
		task01Res.put(branchManager.getResourcePrototypes().get(1), 1);
		task02Res.put(branchManager.getResourcePrototypes().get(0), 1);
		task02Res.put(branchManager.getResourcePrototypes().get(1), 1);
		task03Res.put(branchManager.getResourcePrototypes().get(2), 1);

		branchManager.createTask(project0, "task00", task00EstDur, task00Dev, task00Dependencies,task00Res, null);		// TASK 0
		TaskView task00 = project0.getTasks().get(0);
		task01Dependencies.add(task00);
		branchManager.createTask(project0, "task01", task01EstDur, task01Dev, task01Dependencies,task01Res, null);	// TASK 1

		branchManager.createTask(project0, "task02", task02EstDur, task02Dev, task02Dependencies,task02Res, null);			// TASK 2
		
		branchManager.createTask(project0, "task03", task03EstDur, task03Dev, task03Dependencies, task03Res, null);

		//assertTrue(branchManager.advanceTimeTo(workDate)); // Omdat task updates enkel in het verleden kunnen gezet worden
	}
	
	@Test
	public void successCaseTestConflictingDeveloper(){
		//A test with a conflicting planned developer 
		ProjectView project00 = branchManager.getProjects().get(0);
		TaskView task00 = project00.getTasks().get(0);
		TaskView task02 = project00.getTasks().get(2);
		
		task00ConcreteResources.add(branchManager.getResourcePrototypes().get(0));
		task00ConcreteResources.add(branchManager.getResourcePrototypes().get(1));
		
		task02ConcreteResources.add(branchManager.getResourcePrototypes().get(0));
		task02ConcreteResources.add(branchManager.getResourcePrototypes().get(1));
		
		branchManager.planTask(project00, task00, task00StartDateGood, task00ConcreteResources, devList1);
		branchManager.planRawTask(project00, task02, task00StartDateGood, task02ConcreteResources, devList1);
		
		Map<ProjectView, List<TaskView>> conflicts = branchManager.findConflictingPlannings(task02);
		assertEquals(1,conflicts.keySet().size());
		project00 = conflicts.keySet().iterator().next();
		assertEquals(1,conflicts.get(project00).size());
		assertEquals(task00, conflicts.get(project00).get(0));
		branchManager.planTask(project00, task00, task02StartDateGood, task00ConcreteResources, devList1);
		
		conflicts = branchManager.findConflictingPlannings(task02);
		assertTrue(conflicts.isEmpty());
	}
	
	@Test
	public void successCaseTestConflictingResource(){
		//A test with conflicting resource
		ProjectView project00 = branchManager.getProjects().get(0);
		TaskView task03 = project00.getTasks().get(3);
		
		task03ConcreteResources.add(branchManager.getResourcePrototypes().get(2));
		branchManager.planTask(project00, task03, task00StartDateGood, task03ConcreteResources, devList1);
		
		HashMap<ResourceView, Integer >reqRes = new HashMap<ResourceView,Integer>();
		reqRes.put(branchManager.getResourcePrototypes().get(2), 1);
		branchManager.createTask(project00, "test", 60, 5, new ArrayList<TaskView>(), reqRes, null);
		TaskView test = project00.getTasks().get(4);
		ArrayList<ResourceView> concRes = new ArrayList<ResourceView>();
		concRes.add(branchManager.getResourcePrototypes().get(2));
		branchManager.planRawTask(project00, test, task00StartDateGood, concRes, devList2);
		Map<ProjectView, List<TaskView>> conflicts = branchManager.findConflictingPlannings(test);
		assertEquals(1,conflicts.keySet().size());
		project00 = conflicts.keySet().iterator().next();
		assertEquals(1,conflicts.get(project00).size());
		assertEquals(task03, conflicts.get(project00).get(0));
		branchManager.planTask(project00, task03, task02StartDateGood, task03ConcreteResources, devList1);
		
		conflicts = branchManager.findConflictingPlannings(test);
		assertTrue(conflicts.isEmpty());
	}
	
	@Test
	public void successCaseTestMultipleTasksConflict(){
		//A test with multiple conflicting tasks
		ProjectView project00 = branchManager.getProjects().get(0);
		TaskView task00 = project00.getTasks().get(0);
		TaskView task03 = project00.getTasks().get(3);
		
		task00ConcreteResources.add(branchManager.getResourcePrototypes().get(0));
		task00ConcreteResources.add(branchManager.getResourcePrototypes().get(1));
		
		task03ConcreteResources.add(branchManager.getResourcePrototypes().get(2));

		branchManager.planTask(project00, task00, task00StartDateGood, task00ConcreteResources, devList1);
		branchManager.planTask(project00, task03, task00StartDateGood, task03ConcreteResources, devList2);

		HashMap<ResourceView, Integer >reqRes = new HashMap<ResourceView,Integer>();
		reqRes.put(branchManager.getResourcePrototypes().get(2), 1);
		branchManager.createTask(project00, "test", 60, 5, new ArrayList<TaskView>(), reqRes, null);
		TaskView test = project00.getTasks().get(4);
		ArrayList<ResourceView> concRes = new ArrayList<ResourceView>();
		concRes.add(branchManager.getResourcePrototypes().get(2));
		branchManager.planRawTask(project00, test, task00StartDateGood, concRes, devList1);
		
		Map<ProjectView, List<TaskView>> conflicts = branchManager.findConflictingPlannings(test);
		assertEquals(1,conflicts.keySet().size());
		project00 = conflicts.keySet().iterator().next();
		assertEquals(2,conflicts.get(project00).size());
		assertTrue(conflicts.get(project00).contains(task00));
		assertTrue(conflicts.get(project00).contains(task03));
		branchManager.planTask(project00, task03, task02StartDateGood, task03ConcreteResources, devList2);
		branchManager.planTask(project00, task00, task02StartDateGood, task00ConcreteResources, devList1);
		
		conflicts = branchManager.findConflictingPlannings(test);
		assertTrue(conflicts.isEmpty());
	}
	
	@Test
	public void successCaseTestMultipleProjectsConflict(){
		//A test with conflicts across multiple projects
		ProjectView project00 = branchManager.getProjects().get(0);
		TaskView task00 = project00.getTasks().get(0);
		
		task00ConcreteResources.add(branchManager.getResourcePrototypes().get(0));
		task00ConcreteResources.add(branchManager.getResourcePrototypes().get(1));
		
		branchManager.planTask(project00, task00, task00StartDateGood, task00ConcreteResources, devList1);
		
		branchManager.createProject("project2", "test", project0DueDate);
		ProjectView project01 = branchManager.getProjects().get(1);
		branchManager.createTask(project01, "task10", 60, 10, new ArrayList<TaskView>(), task03Res, null);
		TaskView task10 = project01.getTasks().get(0);
		
		task03ConcreteResources.add(branchManager.getResourcePrototypes().get(2));
		
		branchManager.planTask(project01, task10, task00StartDateGood, task03ConcreteResources, devList2);
		
		HashMap<ResourceView, Integer >reqRes = new HashMap<ResourceView,Integer>();
		reqRes.put(branchManager.getResourcePrototypes().get(2), 1);
		branchManager.createTask(project00, "test", 60, 5, new ArrayList<TaskView>(), reqRes, null);
		TaskView test = project00.getTasks().get(4);
		ArrayList<ResourceView> concRes = new ArrayList<ResourceView>();
		concRes.add(branchManager.getResourcePrototypes().get(2));
		branchManager.planRawTask(project00, test, task00StartDateGood, concRes, devList1);	
		
		Map<ProjectView, List<TaskView>> conflicts = branchManager.findConflictingPlannings(test);
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
		
		branchManager.planTask(project00, task00, task02StartDateGood, task00ConcreteResources, devList1);
		branchManager.planTask(project01, task10, task02StartDateGood, task03ConcreteResources, devList2);
		
		conflicts = branchManager.findConflictingPlannings(test);
		assertTrue(conflicts.isEmpty());
	}
}
