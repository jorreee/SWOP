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

public class UseCase4PlanTaskTest {
	private IFacade taskManager;
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
		taskManager = new BranchManager(startDate);

		taskManager.createProject("Test1", "testing 1", project0DueDate);
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
		taskManager.createResourcePrototype("balpointpen", emptyAvailabilityPeriodStart, emptyAvailabilityPeriodEnd);
		for(int i = 0;i<=5;i++){
			taskManager.declareConcreteResource("balpointpen" + i, taskManager.getResourcePrototypes().get(2));
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

		taskManager.createTask(project0, "Design system", task00EstDur, task00Dev, task00Dependencies,task00Res, null);		// TASK 1
		TaskView task00 = project0.getTasks().get(0);
		task01Dependencies.add(task00);
		taskManager.createTask(project0, "Implement Native", task01EstDur, task01Dev, task01Dependencies,task01Res, null);	// TASK 2
		TaskView task01 = project0.getTasks().get(1);
		task02Dependencies.add(task01);

		taskManager.createTask(project0, "Test code", task02EstDur, task02Dev, task02Dependencies,task02Res, null);			// TASK 3

		taskManager.advanceTimeTo(workDate); // Omdat task updates enkel in het verleden kunnen gezet worden
	}
	
	@Test
	public void succesCaseTestPrototypes(){
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		ArrayList<ResourceView> concRes = new ArrayList<>();
		concRes.add(taskManager.getResourcePrototypes().get(0));
		concRes.add(taskManager.getResourcePrototypes().get(1));
		taskManager.planTask(project0, task00, task00StartDateGood, concRes, devList1);
	}
	
	@Test
	public void succesCaseTestConcrete(){
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		ArrayList<ResourceView> concRes = new ArrayList<>();
		concRes.add(taskManager.getConcreteResourcesForPrototype(taskManager.getResourcePrototypes().get(0)).get(0));
		concRes.add(taskManager.getConcreteResourcesForPrototype(taskManager.getResourcePrototypes().get(1)).get(0));
		taskManager.planTask(project0, task00, task00StartDateGood, concRes, devList1);
	}
	
	@Test
	public void succesCaseTestFinishedAltOfPreReq(){
		//Set up
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		ArrayList<ResourceView> concRes1 = new ArrayList<>();
		concRes1.add(taskManager.getConcreteResourcesForPrototype(taskManager.getResourcePrototypes().get(0)).get(0));
		concRes1.add(taskManager.getConcreteResourcesForPrototype(taskManager.getResourcePrototypes().get(1)).get(0));
		ArrayList<ResourceView> concRes2 = new ArrayList<>();
		concRes2.add(taskManager.getConcreteResourcesForPrototype(taskManager.getResourcePrototypes().get(0)).get(1));
		concRes2.add(taskManager.getConcreteResourcesForPrototype(taskManager.getResourcePrototypes().get(1)).get(1));
		
		//Plan task00 and let it fail
		taskManager.planTask(project0, task00, task00StartDateGood, concRes1, devList1);
		taskManager.setTaskExecuting(project0, task00, task00StartDateGood);
		taskManager.setTaskFailed(project0, task00, task00EndDateGood);

		task01ConcreteResources.add(taskManager.getConcreteResourcesForPrototype(taskManager.getResourcePrototypes().get(0)).get(3));
		task01ConcreteResources.add(taskManager.getConcreteResourcesForPrototype(taskManager.getResourcePrototypes().get(0)).get(4));
		task01ConcreteResources.add(taskManager.getConcreteResourcesForPrototype(taskManager.getResourcePrototypes().get(1)).get(2));
		taskManager.planTask(project0, task01, task01StartDateGood, task01ConcreteResources, devList1);
		
		//Create an alt for task00 and let it succeed
		taskManager.createTask(project0, "Alt", task00EstDur, task00Dev, task00Dependencies, noReq, task00);
		TaskView taskAlt = project0.getTasks().get(3);
		taskManager.planTask(project0, taskAlt, LocalDateTime.of(2015,2,9,10,2), new ArrayList<ResourceView>(), devList1);
		taskManager.setTaskExecuting(project0, taskAlt, LocalDateTime.of(2015,2,9,10,2));
		taskManager.setTaskFinished(project0, taskAlt, LocalDateTime.of(2015,2,9,14,0));
		
		//Plan task01 dependent on task00
		taskManager.planTask(project0, task01, LocalDateTime.of(2015,2,9,14,15), task01ConcreteResources, devList1);
	}
	
	@Test
	public void failCaseBadResources(){
		//Teveel concrete resources
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		ArrayList<ResourceView> concRes = new ArrayList<>();
		concRes.add(taskManager.getConcreteResourcesForPrototype(taskManager.getResourcePrototypes().get(0)).get(0));
		concRes.add(taskManager.getConcreteResourcesForPrototype(taskManager.getResourcePrototypes().get(0)).get(1));
		concRes.add(taskManager.getConcreteResourcesForPrototype(taskManager.getResourcePrototypes().get(1)).get(0));
		concRes.add(taskManager.getConcreteResourcesForPrototype(taskManager.getResourcePrototypes().get(1)).get(1));
		taskManager.planTask(project0, task00, task00StartDateGood, concRes, devList1);
		
		//Teweining concrete resources
		concRes = new ArrayList<>();
		concRes.add(taskManager.getConcreteResourcesForPrototype(taskManager.getResourcePrototypes().get(0)).get(0));
		taskManager.planTask(project0, task00, task00StartDateGood, concRes, devList1);
		
		//Verkeerde type bestaande resource
		concRes = new ArrayList<>();
		concRes.add(taskManager.getConcreteResourcesForPrototype(taskManager.getResourcePrototypes().get(2)).get(0));
		taskManager.planTask(project0, task00, task00StartDateGood, concRes, devList1);
		
		//onbestaande resource
		concRes = new ArrayList<>();
		concRes.add(new ResourceView(new ResourcePrototype("Lipton ice-tea", 
				new AvailabilityPeriod(LocalTime.of(12, 0), LocalTime.of(15,0)))));
		taskManager.planTask(project0, task00, task00StartDateGood, concRes, devList1);
		
		//null for resource
		concRes = new ArrayList<>();
		concRes.add(null);
		taskManager.planTask(project0, task00, task00StartDateGood, concRes, devList1);
		//all resources reserved
		HashMap<ResourceView,Integer >newTaskRes1 = new HashMap<>();
		newTaskRes1.put(taskManager.getResourcePrototypes().get(1), 6);
		taskManager.createTask(project0, "test", 60, 5, new ArrayList<TaskView>(), newTaskRes1, null);
		TaskView test = project0.getTasks().get(3);
		concRes = new ArrayList<>();
		concRes.add(taskManager.getResourcePrototypes().get(1));
		concRes.add(taskManager.getResourcePrototypes().get(1));
		concRes.add(taskManager.getResourcePrototypes().get(1));
		concRes.add(taskManager.getResourcePrototypes().get(1));
		concRes.add(taskManager.getResourcePrototypes().get(1));
		concRes.add(taskManager.getResourcePrototypes().get(1));
		taskManager.planTask(project0, test, task00StartDateGood, concRes, devList1);
		
		ArrayList<ResourceView> concRes1 = new ArrayList<>();
		concRes1.add(taskManager.getConcreteResourcesForPrototype(taskManager.getResourcePrototypes().get(0)).get(0));
		concRes1.add(taskManager.getConcreteResourcesForPrototype(taskManager.getResourcePrototypes().get(1)).get(0));
		taskManager.planTask(project0, task00, task00StartDateGood, concRes1, devList2);
	}
	
	@Test
	public void failCaseBadDevelopers(){
		//null Dev
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		ArrayList<ResourceView> concRes = new ArrayList<>();
		concRes.add(taskManager.getConcreteResourcesForPrototype(taskManager.getResourcePrototypes().get(0)).get(0));
		concRes.add(taskManager.getConcreteResourcesForPrototype(taskManager.getResourcePrototypes().get(1)).get(0));
		ArrayList<ResourceView> devs = new ArrayList<>();
		devs.add(null);
		taskManager.planTask(project0, task00, task00StartDateGood, concRes, devs);
		
		//Onbestaande Dev
		devs = new ArrayList<>();
		UserPrototype watProt = new UserPrototype();
		devs.add(new ResourceView(watProt.instantiateDeveloper("Spartacus")));
		taskManager.planTask(project0, task00, task00StartDateGood, concRes, devs);
		//Dev already reserved
		taskManager.createTask(project0, "test", 60, 10, new ArrayList<TaskView>(), newTaskRes, null);
		TaskView test = project0.getTasks().get(3);
		devs = new ArrayList<>();
		devs.add(blunderbus);
		task00ConcreteResources.add(taskManager.getConcreteResourcesForPrototype(taskManager.getResourcePrototypes().get(0)).get(0));
		task00ConcreteResources.add(taskManager.getConcreteResourcesForPrototype(taskManager.getResourcePrototypes().get(1)).get(0));
		taskManager.planTask(project0, task00, task00StartDateGood, task00ConcreteResources, devs);
		
		concRes = new ArrayList<ResourceView>();
		concRes.add(taskManager.getResourcePrototypes().get(1));
		taskManager.planTask(project0, test, task00StartDateGood, concRes, devs);
	}
	
	@Test
	public void failCaseBadStartDate(){
		//Set up
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		ArrayList<ResourceView> concRes1 = new ArrayList<>();
		concRes1.add(taskManager.getResourcePrototypes().get(0));
		concRes1.add(taskManager.getResourcePrototypes().get(1));
		ArrayList<ResourceView> concRes2 = new ArrayList<>();
		concRes2.add(taskManager.getResourcePrototypes().get(0));
		concRes2.add(taskManager.getResourcePrototypes().get(0));
		concRes2.add(taskManager.getResourcePrototypes().get(1));
		
		//null date
		taskManager.planTask(project0, task00, null, concRes1, devList1);
	
		//To early for planned enddates of preReq's
		taskManager.planTask(project0, task00, task00StartDateGood, concRes1, devList1);
		taskManager.setTaskExecuting(project0, task00, task00StartDateGood);
		taskManager.planTask(project0, task01, LocalDateTime.of(2015,2,9,9,0), concRes2, devList1);
	}
	
	@Test
	public void failCaseBadStateAllButFinished(){
		//Set Up
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		ArrayList<ResourceView> concRes = new ArrayList<>();
		concRes.add(taskManager.getConcreteResourcesForPrototype(taskManager.getResourcePrototypes().get(0)).get(0));
		concRes.add(taskManager.getConcreteResourcesForPrototype(taskManager.getResourcePrototypes().get(1)).get(0));
		taskManager.planTask(project0, task00, task00StartDateGood, concRes, devList1);
		
		//Already executing
		taskManager.setTaskExecuting(project0, task00, task00StartDateGood);
		taskManager.planTask(project0, task00, task00StartDateGood, concRes, devList1);
		
		//Already failed
		taskManager.setTaskFailed(project0, task00, task00EndDateGood);
		taskManager.planTask(project0, task00, task00StartDateGood, concRes, devList1);
	}
	
	@Test
	public void failCaseBadStateFinished(){
		//Set Up
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		ArrayList<ResourceView> concRes = new ArrayList<>();
		concRes.add(taskManager.getConcreteResourcesForPrototype(taskManager.getResourcePrototypes().get(0)).get(0));
		concRes.add(taskManager.getConcreteResourcesForPrototype(taskManager.getResourcePrototypes().get(1)).get(0));
		taskManager.planTask(project0, task00, task00StartDateGood, concRes, devList1);
		
		taskManager.setTaskExecuting(project0, task00, task00StartDateGood);
		taskManager.planTask(project0, task00, task00StartDateGood, concRes, devList1);
		taskManager.setTaskFinished(project0, task00, task00EndDateGood);
		taskManager.planTask(project0, task00, task00StartDateGood, concRes, devList1);
	}
	
	@Test
	public void failCaseBadPreReq(){
		//Set up
		ProjectView project0 = taskManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		ArrayList<ResourceView> concRes = new ArrayList<>();
		concRes.add(taskManager.getResourcePrototypes().get(0));
		concRes.add(taskManager.getResourcePrototypes().get(1));
		
		//Unplanned preReq
		taskManager.planTask(project0, task01, task01StartDateGood, concRes, devList1);
		//Failed preReq
		taskManager.planTask(project0, task00, task00StartDateGood, concRes, devList1);
		taskManager.setTaskExecuting(project0, task00, task00EndDateGood);
		taskManager.setTaskFailed(project0, task00, task00EndDateGood);
		taskManager.planTask(project0, task01, task01StartDateGood, concRes, devList1);
	}
	

	@Test
	public void planTaskWithResourceAvailabilitySuccess(){
		//Set up
		taskManager.createResourcePrototype("Beamer", Optional.of(LocalTime.of(8, 0)), Optional.of(LocalTime.of(12, 0)));
		taskManager.declareConcreteResource("TheOnlyBeamer", taskManager.getResourcePrototypes().get(3));
		assertEquals(4,taskManager.getResourcePrototypes().size());
		ProjectView project0 = taskManager.getProjects().get(0);
		HashMap<ResourceView, Integer> reqResNewTask = new HashMap<>();
		reqResNewTask.put(taskManager.getResourcePrototypes().get(3),1);
		taskManager.createTask(project0, "newTask", 60, 0, new ArrayList<TaskView>(), reqResNewTask, null);
		//Plan the task
		TaskView newTask = taskManager.getProjects().get(0).getTasks().get(3);
		ArrayList<ResourceView> concRes = new ArrayList<>();
		concRes.add(taskManager.getResourcePrototypes().get(3));
		taskManager.planTask(project0, newTask, task00StartDateGood, concRes, devList1);
	}
	
//	@Test // todo deze test heeft nog weinig zin zolang reservations in 1 grote blok worden gedaan
//	public void planTaskWithResourceAvailabilityFailTestStartTimeAfterResourceEndTime(){
//		//Set up
//		assertTrue(taskManager.createResourcePrototype("Beamer", Optional.of(LocalTime.of(8, 0)), Optional.of(LocalTime.of(12, 0))));
//		assertTrue(taskManager.declareConcreteResource("TheOnlyBeamer", taskManager.getResourcePrototypes().get(3)));
//		assertEquals(4,taskManager.getResourcePrototypes().size());
//		ProjectView project0 = taskManager.getProjects().get(0);
//		HashMap<ResourceView, Integer> reqResNewTask = new HashMap<>();
//		reqResNewTask.put(taskManager.getResourcePrototypes().get(3),1);
//		assertTrue(taskManager.createTask(project0, "newTask", 60, 0, new ArrayList<TaskView>(), reqResNewTask, null));
//		//Plan the task
//		TaskView newTask = taskManager.getProjects().get(0).getTasks().get(3);
//		ArrayList<ResourceView> concRes = new ArrayList<>();
//		concRes.add(taskManager.getResourcePrototypes().get(3));
//		//todo Error:  Het is nog mogelijk om taak te plannen als de beschikbaarheid van de resource al verlopen is
//		assertFalse(taskManager.planTask(project0, newTask, LocalDateTime.of(2015, 2, 9, 13, 0), concRes, devList1));
//	}
}
