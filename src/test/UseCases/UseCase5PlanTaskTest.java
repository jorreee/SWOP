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
import company.taskMan.resource.AvailabilityPeriod;
import company.taskMan.resource.ResourcePrototype;
import company.taskMan.resource.ResourceView;
import company.taskMan.resource.user.UserPrototype;
import userInterface.IFacade;
import userInterface.TaskManException;

public class UseCase5PlanTaskTest {
	private IFacade branchManager;
	private final LocalDateTime startDate = LocalDateTime.of(2015, 2, 9, 8, 0),
			project0DueDate = LocalDateTime.of(2015, 2, 13, 23, 59),
			workDate = LocalDateTime.of(2015, 2, 9, 16, 0),
			task00StartDateGood = startDate,
			task00EndDateGood = LocalDateTime.of(2015,2,9,10,0),
			task01StartDateGood = LocalDateTime.of(2015, 2, 9, 10, 10);
	private final int task00EstDur = 120,
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
			task02Res = new HashMap<ResourceView,Integer>(),
			newTaskRes = new HashMap<ResourceView,Integer>(),
			noReq = new HashMap<ResourceView, Integer>();
	private final ArrayList<ResourceView> task00ConcreteResources = new ArrayList<ResourceView>(),
			task01ConcreteResources = new ArrayList<ResourceView>();
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
	public void initialize(){
		branchManager = new BranchManager(startDate);
		
		branchManager.createResourcePrototype("car", emptyAvailabilityPeriodStart, emptyAvailabilityPeriodEnd);
		branchManager.createResourcePrototype("whiteboard", emptyAvailabilityPeriodStart, emptyAvailabilityPeriodEnd);
		branchManager.createResourcePrototype("balpointpen", emptyAvailabilityPeriodStart, emptyAvailabilityPeriodEnd);
		branchManager.createResourcePrototype("Beamer", Optional.of(LocalTime.of(8, 0)), Optional.of(LocalTime.of(12, 0)));
		
		branchManager.initializeBranch("Leuven");

		branchManager.createProject("Test1", "testing 1", project0DueDate);
		
		
		ProjectView project0 = branchManager.getProjects().get(0);

		//create resources

		for(int i = 0;i<=5;i++){
			branchManager.declareConcreteResource("car" + i, branchManager.getResourcePrototypes().get(0));
		}

		for(int i = 0;i<=5;i++){
			branchManager.declareConcreteResource("whiteboard" + i, branchManager.getResourcePrototypes().get(1));
		}
		
		for(int i = 0;i<=5;i++){
			branchManager.declareConcreteResource("balpointpen" + i, branchManager.getResourcePrototypes().get(2));
		}
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
		newTaskRes.put(branchManager.getResourcePrototypes().get(1), 1);

		branchManager.createTask(project0, "Design system", task00EstDur, task00Dev, task00Dependencies,task00Res, null);		// TASK 1
		TaskView task00 = project0.getTasks().get(0);
		task01Dependencies.add(task00);
		branchManager.createTask(project0, "Implement Native", task01EstDur, task01Dev, task01Dependencies,task01Res, null);	// TASK 2
		TaskView task01 = project0.getTasks().get(1);
		task02Dependencies.add(task01);

		branchManager.createTask(project0, "Test code", task02EstDur, task02Dev, task02Dependencies,task02Res, null);			// TASK 3

		branchManager.advanceTimeTo(workDate); // Omdat task updates enkel in het verleden kunnen gezet worden
	}
	
	@Test
	public void succesCaseTestPrototypes(){
		ProjectView project0 = branchManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		ArrayList<ResourceView> concRes = new ArrayList<>();
		concRes.add(branchManager.getResourcePrototypes().get(0));
		concRes.add(branchManager.getResourcePrototypes().get(1));
		branchManager.planTask(project0, task00, task00StartDateGood, concRes, devList1);
	}
	
	@Test
	public void succesCaseTestConcrete(){
		ProjectView project0 = branchManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		ArrayList<ResourceView> concRes = new ArrayList<>();
		concRes.add(branchManager.getConcreteResourcesForPrototype(branchManager.getResourcePrototypes().get(0)).get(0));
		concRes.add(branchManager.getConcreteResourcesForPrototype(branchManager.getResourcePrototypes().get(1)).get(0));
		branchManager.planTask(project0, task00, task00StartDateGood, concRes, devList1);
	}
	
	@Test
	public void succesCaseTestFinishedAltOfPreReq(){
		//Set up
		ProjectView project0 = branchManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		ArrayList<ResourceView> concRes1 = new ArrayList<>();
		concRes1.add(branchManager.getConcreteResourcesForPrototype(branchManager.getResourcePrototypes().get(0)).get(0));
		concRes1.add(branchManager.getConcreteResourcesForPrototype(branchManager.getResourcePrototypes().get(1)).get(0));
		ArrayList<ResourceView> concRes2 = new ArrayList<>();
		concRes2.add(branchManager.getConcreteResourcesForPrototype(branchManager.getResourcePrototypes().get(0)).get(1));
		concRes2.add(branchManager.getConcreteResourcesForPrototype(branchManager.getResourcePrototypes().get(1)).get(1));
		
		//Plan task00 and let it fail
		branchManager.planTask(project0, task00, task00StartDateGood, concRes1, devList1);
		branchManager.setTaskExecuting(project0, task00, task00StartDateGood);
		branchManager.setTaskFailed(project0, task00, task00EndDateGood);

		task01ConcreteResources.add(branchManager.getConcreteResourcesForPrototype(branchManager.getResourcePrototypes().get(0)).get(3));
		task01ConcreteResources.add(branchManager.getConcreteResourcesForPrototype(branchManager.getResourcePrototypes().get(0)).get(4));
		task01ConcreteResources.add(branchManager.getConcreteResourcesForPrototype(branchManager.getResourcePrototypes().get(1)).get(2));
		boolean exception = false;
		try {
		branchManager.planTask(project0, task01, task01StartDateGood, task01ConcreteResources, devList1);
		} catch (TaskManException e) {
			exception = true;
		}
		assertTrue(exception);
		
		
		//Create an alt for task00 and let it succeed
		branchManager.createTask(project0, "Alt", task00EstDur, task00Dev, task00Dependencies, noReq, task00);
		TaskView taskAlt = project0.getTasks().get(3);
		branchManager.planTask(project0, taskAlt, LocalDateTime.of(2015,2,9,10,2), new ArrayList<ResourceView>(), devList1);
		branchManager.setTaskExecuting(project0, taskAlt, LocalDateTime.of(2015,2,9,10,2));
		branchManager.setTaskFinished(project0, taskAlt, LocalDateTime.of(2015,2,9,14,0));
		
		//Plan task01 dependent on task00
		branchManager.planTask(project0, task01, LocalDateTime.of(2015,2,9,14,15), task01ConcreteResources, devList1);
	}
	
	@Test(expected = TaskManException.class)
	public void failCaseBadResources(){
		//Teveel concrete resources
		ProjectView project0 = branchManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		ArrayList<ResourceView> concRes = new ArrayList<>();
		concRes.add(branchManager.getConcreteResourcesForPrototype(branchManager.getResourcePrototypes().get(0)).get(0));
		concRes.add(branchManager.getConcreteResourcesForPrototype(branchManager.getResourcePrototypes().get(0)).get(1));
		concRes.add(branchManager.getConcreteResourcesForPrototype(branchManager.getResourcePrototypes().get(1)).get(0));
		concRes.add(branchManager.getConcreteResourcesForPrototype(branchManager.getResourcePrototypes().get(1)).get(1));
		branchManager.planTask(project0, task00, task00StartDateGood, concRes, devList1);
		
		//Teweining concrete resources
		concRes = new ArrayList<>();
		concRes.add(branchManager.getConcreteResourcesForPrototype(branchManager.getResourcePrototypes().get(0)).get(0));
		branchManager.planTask(project0, task00, task00StartDateGood, concRes, devList1);
		
		//Verkeerde type bestaande resource
		concRes = new ArrayList<>();
		concRes.add(branchManager.getConcreteResourcesForPrototype(branchManager.getResourcePrototypes().get(2)).get(0));
		branchManager.planTask(project0, task00, task00StartDateGood, concRes, devList1);
		
		//onbestaande resource
		concRes = new ArrayList<>();
		concRes.add(new ResourceView(new ResourcePrototype("Lipton ice-tea", 
				new AvailabilityPeriod(LocalTime.of(12, 0), LocalTime.of(15,0)))));
		branchManager.planTask(project0, task00, task00StartDateGood, concRes, devList1);
		
		//null for resource
		concRes = new ArrayList<>();
		concRes.add(null);
		branchManager.planTask(project0, task00, task00StartDateGood, concRes, devList1);
		//all resources reserved
		HashMap<ResourceView,Integer >newTaskRes1 = new HashMap<>();
		newTaskRes1.put(branchManager.getResourcePrototypes().get(1), 6);
		branchManager.createTask(project0, "test", 60, 5, new ArrayList<TaskView>(), newTaskRes1, null);
		TaskView test = project0.getTasks().get(3);
		concRes = new ArrayList<>();
		concRes.add(branchManager.getResourcePrototypes().get(1));
		concRes.add(branchManager.getResourcePrototypes().get(1));
		concRes.add(branchManager.getResourcePrototypes().get(1));
		concRes.add(branchManager.getResourcePrototypes().get(1));
		concRes.add(branchManager.getResourcePrototypes().get(1));
		concRes.add(branchManager.getResourcePrototypes().get(1));
		branchManager.planTask(project0, test, task00StartDateGood, concRes, devList1);
		
		ArrayList<ResourceView> concRes1 = new ArrayList<>();
		concRes1.add(branchManager.getConcreteResourcesForPrototype(branchManager.getResourcePrototypes().get(0)).get(0));
		concRes1.add(branchManager.getConcreteResourcesForPrototype(branchManager.getResourcePrototypes().get(1)).get(0));
		branchManager.planTask(project0, task00, task00StartDateGood, concRes1, devList2);
	}
	
	@Test(expected = TaskManException.class)
	public void failCaseBadDevelopers(){
		//null Dev
		ProjectView project0 = branchManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		ArrayList<ResourceView> concRes = new ArrayList<>();
		concRes.add(branchManager.getConcreteResourcesForPrototype(branchManager.getResourcePrototypes().get(0)).get(0));
		concRes.add(branchManager.getConcreteResourcesForPrototype(branchManager.getResourcePrototypes().get(1)).get(0));
		ArrayList<ResourceView> devs = new ArrayList<>();
		devs.add(null);
		branchManager.planTask(project0, task00, task00StartDateGood, concRes, devs);
		
		//Onbestaande Dev
		devs = new ArrayList<>();
		UserPrototype watProt = new UserPrototype();
		devs.add(new ResourceView(watProt.instantiateDeveloper("Spartacus")));
		branchManager.planTask(project0, task00, task00StartDateGood, concRes, devs);
		//Dev already reserved
		branchManager.createTask(project0, "test", 60, 10, new ArrayList<TaskView>(), newTaskRes, null);
		TaskView test = project0.getTasks().get(3);
		devs = new ArrayList<>();
		devs.add(blunderbus);
		task00ConcreteResources.add(branchManager.getConcreteResourcesForPrototype(branchManager.getResourcePrototypes().get(0)).get(0));
		task00ConcreteResources.add(branchManager.getConcreteResourcesForPrototype(branchManager.getResourcePrototypes().get(1)).get(0));
		branchManager.planTask(project0, task00, task00StartDateGood, task00ConcreteResources, devs);
		
		concRes = new ArrayList<ResourceView>();
		concRes.add(branchManager.getResourcePrototypes().get(1));
		branchManager.planTask(project0, test, task00StartDateGood, concRes, devs);
	}
	
	@Test(expected = TaskManException.class)
	public void failCaseBadStartDate(){
		//Set up
		ProjectView project0 = branchManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		ArrayList<ResourceView> concRes1 = new ArrayList<>();
		concRes1.add(branchManager.getResourcePrototypes().get(0));
		concRes1.add(branchManager.getResourcePrototypes().get(1));
		ArrayList<ResourceView> concRes2 = new ArrayList<>();
		concRes2.add(branchManager.getResourcePrototypes().get(0));
		concRes2.add(branchManager.getResourcePrototypes().get(0));
		concRes2.add(branchManager.getResourcePrototypes().get(1));
		
		//null date
		branchManager.planTask(project0, task00, null, concRes1, devList1);
	
		//To early for planned enddates of preReq's
		branchManager.planTask(project0, task00, task00StartDateGood, concRes1, devList1);
		branchManager.setTaskExecuting(project0, task00, task00StartDateGood);
		branchManager.planTask(project0, task01, LocalDateTime.of(2015,2,9,9,0), concRes2, devList1);
	}
	
	@Test(expected = TaskManException.class)
	public void failCaseBadStateAllButFinished(){
		//Set Up
		ProjectView project0 = branchManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		ArrayList<ResourceView> concRes = new ArrayList<>();
		concRes.add(branchManager.getConcreteResourcesForPrototype(branchManager.getResourcePrototypes().get(0)).get(0));
		concRes.add(branchManager.getConcreteResourcesForPrototype(branchManager.getResourcePrototypes().get(1)).get(0));
		branchManager.planTask(project0, task00, task00StartDateGood, concRes, devList1);
		
		//Already executing
		branchManager.setTaskExecuting(project0, task00, task00StartDateGood);
		branchManager.planTask(project0, task00, task00StartDateGood, concRes, devList1);
		
		//Already failed
		branchManager.setTaskFailed(project0, task00, task00EndDateGood);
		branchManager.planTask(project0, task00, task00StartDateGood, concRes, devList1);
	}
	
	@Test(expected = TaskManException.class)
	public void failCaseBadStateFinished(){
		//Set Up
		ProjectView project0 = branchManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		ArrayList<ResourceView> concRes = new ArrayList<>();
		concRes.add(branchManager.getConcreteResourcesForPrototype(branchManager.getResourcePrototypes().get(0)).get(0));
		concRes.add(branchManager.getConcreteResourcesForPrototype(branchManager.getResourcePrototypes().get(1)).get(0));
		branchManager.planTask(project0, task00, task00StartDateGood, concRes, devList1);
		
		branchManager.setTaskExecuting(project0, task00, task00StartDateGood);
		branchManager.planTask(project0, task00, task00StartDateGood, concRes, devList1);
		branchManager.setTaskFinished(project0, task00, task00EndDateGood);
		branchManager.planTask(project0, task00, task00StartDateGood, concRes, devList1);
	}
	
	@Test(expected = TaskManException.class)
	public void failCaseBadPreReq(){
		//Set up
		ProjectView project0 = branchManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		ArrayList<ResourceView> concRes = new ArrayList<>();
		concRes.add(branchManager.getResourcePrototypes().get(0));
		concRes.add(branchManager.getResourcePrototypes().get(1));
		
		//Unplanned preReq
		branchManager.planTask(project0, task01, task01StartDateGood, concRes, devList1);
		//Failed preReq
		branchManager.planTask(project0, task00, task00StartDateGood, concRes, devList1);
		branchManager.setTaskExecuting(project0, task00, task00EndDateGood);
		branchManager.setTaskFailed(project0, task00, task00EndDateGood);
		branchManager.planTask(project0, task01, task01StartDateGood, concRes, devList1);
	}
	

	@Test
	public void planTaskWithResourceAvailabilitySuccess(){
		//Set up
		branchManager.declareConcreteResource("TheOnlyBeamer", branchManager.getResourcePrototypes().get(3));
		assertEquals(4,branchManager.getResourcePrototypes().size());
		ProjectView project0 = branchManager.getProjects().get(0);
		HashMap<ResourceView, Integer> reqResNewTask = new HashMap<>();
		reqResNewTask.put(branchManager.getResourcePrototypes().get(3),1);
		branchManager.createTask(project0, "newTask", 60, 0, new ArrayList<TaskView>(), reqResNewTask, null);
		//Plan the task
		TaskView newTask = branchManager.getProjects().get(0).getTasks().get(3);
		ArrayList<ResourceView> concRes = new ArrayList<>();
		concRes.add(branchManager.getResourcePrototypes().get(3));
		branchManager.planTask(project0, newTask, task00StartDateGood, concRes, devList1);
	}
	
//	@Test // todo deze test heeft nog weinig zin zolang reservations in 1 grote blok worden gedaan
//	public void planTaskWithResourceAvailabilityFailTestStartTimeAfterResourceEndTime(){
//		//Set up
//		assertTrue(branchManager.createResourcePrototype("Beamer", Optional.of(LocalTime.of(8, 0)), Optional.of(LocalTime.of(12, 0))));
//		assertTrue(branchManager.declareConcreteResource("TheOnlyBeamer", branchManager.getResourcePrototypes().get(3)));
//		assertEquals(4,branchManager.getResourcePrototypes().size());
//		ProjectView project0 = branchManager.getProjects().get(0);
//		HashMap<ResourceView, Integer> reqResNewTask = new HashMap<>();
//		reqResNewTask.put(branchManager.getResourcePrototypes().get(3),1);
//		assertTrue(branchManager.createTask(project0, "newTask", 60, 0, new ArrayList<TaskView>(), reqResNewTask, null));
//		//Plan the task
//		TaskView newTask = branchManager.getProjects().get(0).getTasks().get(3);
//		ArrayList<ResourceView> concRes = new ArrayList<>();
//		concRes.add(branchManager.getResourcePrototypes().get(3));
//		//todo Error:  Het is nog mogelijk om taak te plannen als de beschikbaarheid van de resource al verlopen is
//		assertFalse(branchManager.planTask(project0, newTask, LocalDateTime.of(2015, 2, 9, 13, 0), concRes, devList1));
//	}
}
