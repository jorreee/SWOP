package test.UseCases;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import userInterface.IFacade;
import company.BranchManager;
import company.taskMan.ProjectView;
import company.taskMan.project.TaskView;
import company.taskMan.resource.ResourceView;
import company.taskMan.task.Task;
import userInterface.TaskManException;

public class UseCase7DelegateTaskTest {
	private IFacade branchManager;
	private final LocalDateTime startDate = LocalDateTime.of(2015, 2, 9, 8, 0),
			workdate1 = LocalDateTime.of(2015, 2, 11, 16, 0),
			workdate2 = LocalDateTime.of(2015, 2, 12, 16, 0),
			workdate3 = LocalDateTime.of(2015, 2, 13, 16, 0),
			workdate4 = LocalDateTime.of(2015, 2, 15, 16, 0),
			workdate5 = LocalDateTime.of(2015, 2, 18, 8, 0),
			workdate6 = LocalDateTime.of(2015, 2, 20, 8, 0),
			project0DueDate = LocalDateTime.of(2015, 2, 13, 23, 59),
			project1DueDate = LocalDateTime.of(2015, 2, 22, 23, 59),
			project2DueDate = LocalDateTime.of(2015, 2, 16, 23, 59),
			project3DueDate = LocalDateTime.of(2015, 2, 21, 23, 59),
			task00Start = startDate,
			task00End = LocalDateTime.of(2015, 2, 9, 10, 0),
			task10Start = LocalDateTime.of(2015, 2, 12, 8, 0),
			task10End = LocalDateTime.of(2015, 2, 13, 10, 0),
			task11Start = LocalDateTime.of(2015, 2, 13, 10, 15),
			task11End = LocalDateTime.of(2015, 2, 13, 14, 0),
			task13Start = LocalDateTime.of(2015, 2, 18, 8, 0),
			task20start = LocalDateTime.of(2015, 2, 16, 10, 0),
			task30Start = LocalDateTime.of(2015, 2, 16, 8, 0),
			task30End = LocalDateTime.of(2015, 2, 16, 9, 45);
	private final int task00EstDur = 120,
			task10EstDur = 8*60,
			task11EstDur = 3*8*60,
			task12EstDur = 1*8*60,
			task13EstDur = 110,
			task20EstDur = 35*60,
			task30EstDur = 60,
			task31EstDur = 3*8*60;
	private final int task00Dev = 5,
			task10Dev = 30,
			task11Dev = 0,
			task12Dev = 15,
			task13Dev = 20,
			task20Dev = 50,	// Moet nog steeds delayed project geven!
			task30Dev = 0,
			task31Dev = 15;
	private final ArrayList<TaskView> task00Dependencies = new ArrayList<TaskView>(),
			task10Dependencies = new ArrayList<TaskView>(),
			task11Dependencies = new ArrayList<TaskView>(),
			task12Dependencies = new ArrayList<TaskView>(),
			task13Dependencies = new ArrayList<TaskView>(),
			task20Dependencies = new ArrayList<TaskView>(),
			task30Dependencies = new ArrayList<TaskView>(),
			task31Dependencies = new ArrayList<TaskView>();
	private final ArrayList<ResourceView> task00ConcreteResources = new ArrayList<ResourceView>(),
			task10ConcreteResources = new ArrayList<ResourceView>(), 
			task11ConcreteResources = new ArrayList<ResourceView>(),
			task20ConcreteResources = new ArrayList<ResourceView>(),
			task30ConcreteResources = new ArrayList<ResourceView>(),
			task31ConcreteResources = new ArrayList<ResourceView>();
	private final List<ResourceView> devList1 = new ArrayList<ResourceView>(),
			devList2 = new ArrayList<ResourceView>(),
			devList3 = new ArrayList<>();
	private ResourceView weer, blunderbus, deNick;
	private final Optional<LocalTime> emptyAvailabilityStart = Optional.empty(),
			emptyAvailabilityEnd = Optional.empty();
	
	/**
	 * - project 0 START 9 feb 8u DUE 31 feb midnight
	 * 		task 0 FINISHED
	 * 
	 * - project 1 START 11 feb 16u DUE 
	 * 		task 0 FINISHED
	 * 		task 1 FAILED
	 * 		task 2 <- 0, 1
	 * 		task 3 ALT 1
	 * 
	 * - project 2 START 13 feb 16u DUE 
	 * 		task 0 AVAILABLE
	 * 
	 * - project 3 START 15 feb 16u DUE 
	 * 		task 0 FAILED
	 * 		task 1
	 * 		task 2 UNAVAILABLE 
	 * 
	 */
	
	@Before
	public final void initialize() {
		// INIT system 
		branchManager = new BranchManager(startDate);
		branchManager.createResourcePrototype("car", emptyAvailabilityStart, emptyAvailabilityEnd);
		branchManager.createResourcePrototype("whiteboard", emptyAvailabilityStart, emptyAvailabilityEnd);
		branchManager.initializeBranch("Leuven");

		//INIT resources
		for(int i = 0;i<=5;i++){
			branchManager.declareConcreteResource("car" + i, branchManager.getResourcePrototypes().get(0));
		}
		for(int i = 0;i<=5;i++){
			branchManager.declareConcreteResource("whiteboard" + i, branchManager.getResourcePrototypes().get(1));
		}
		branchManager.createDeveloper("Weer");
		branchManager.createDeveloper("Blunderbus");
		branchManager.createDeveloper("De Nick");
		weer = branchManager.getDeveloperList().get(0);
		blunderbus = branchManager.getDeveloperList().get(1);
		deNick = branchManager.getDeveloperList().get(2);
		devList1.add(weer);
		devList2.add(blunderbus);
		devList3.add(deNick);
		
		//Create first project
		branchManager.createProject("Project 0", "Describing proj 0", project0DueDate);
		ProjectView project0 = branchManager.getProjects().get(0);
		// 00 AVAILABLE
		Map<ResourceView, Integer> reqResTask00 = new HashMap<>();
		reqResTask00.put(branchManager.getResourcePrototypes().get(0), 2);
		reqResTask00.put(branchManager.getResourcePrototypes().get(1), 1);
		branchManager.createTask(project0, "TASK 00", task00EstDur, task00Dev, task00Dependencies,reqResTask00,null);	
		TaskView task00 = project0.getTasks().get(0);
		task00ConcreteResources.add(branchManager.getResourcePrototypes().get(0));
		task00ConcreteResources.add(branchManager.getResourcePrototypes().get(0));
		task00ConcreteResources.add(branchManager.getResourcePrototypes().get(1));
		branchManager.planTask(project0, task00, task00Start,task00ConcreteResources,devList1);
		branchManager.setTaskExecuting(project0, task00, task00Start);
				
		// Stap verder:
		// maak het tweede project aan en maak zijn TASK lijst
		branchManager.advanceTimeTo(workdate1);
		branchManager.createProject("Project 1", "Describing proj 1", project1DueDate);
		ProjectView project1 = branchManager.getProjects().get(1);
		
		// 10 AVAILABLE
		Map<ResourceView, Integer> reqResTask10 = new HashMap<>();
		reqResTask10.put(branchManager.getResourcePrototypes().get(0), 1);
		reqResTask10.put(branchManager.getResourcePrototypes().get(1), 1);
		branchManager.createTask(project1, "TASK 10", task10EstDur, task10Dev, task10Dependencies,reqResTask10, null);	
		task10ConcreteResources.add(branchManager.getResourcePrototypes().get(0));
		task10ConcreteResources.add(branchManager.getResourcePrototypes().get(1));
		// 11 AVAILABLE
		Map<ResourceView, Integer> reqResTask11 = new HashMap<>();
		reqResTask11.put(branchManager.getResourcePrototypes().get(1), 3);
		branchManager.createTask(project1, "TASK 11", task11EstDur, task11Dev, task11Dependencies,reqResTask11, null);			
		task11ConcreteResources.add(branchManager.getResourcePrototypes().get(1));
		task11ConcreteResources.add(branchManager.getResourcePrototypes().get(1));
		task11ConcreteResources.add(branchManager.getResourcePrototypes().get(1));
		
		TaskView task10 = project1.getTasks().get(0);
		TaskView task11 = project1.getTasks().get(1);
				
		assertTrue(task12Dependencies.add(task10)); 
		assertTrue(task12Dependencies.add(task11));
		// 12 UNAVAILABLE
		Map<ResourceView, Integer> reqResTask12 = new HashMap<>();
		reqResTask12.put(branchManager.getResourcePrototypes().get(0), 2);
		branchManager.createTask(project1, "TASK 12", task12EstDur, task12Dev, task12Dependencies,reqResTask12, null);			
		
		// Stap verder:
		// maak het derde project aan, samen met zijn TASK
		branchManager.advanceTimeTo(workdate2);
		branchManager.createProject("Project 2", "Describing proj 2", project2DueDate);
		ProjectView project2 = branchManager.getProjects().get(2);
		// 20 AVAILABLE
		Map<ResourceView, Integer> reqResTask20 = new HashMap<>();
		reqResTask20.put(branchManager.getResourcePrototypes().get(0), 1);
		reqResTask20.put(branchManager.getResourcePrototypes().get(1), 3);
		branchManager.createTask(project2, "TASK 20", task20EstDur, task20Dev, task20Dependencies,reqResTask20, null);
		TaskView task20 = project2.getTasks().get(0);
		task20ConcreteResources.add(branchManager.getResourcePrototypes().get(0));
		task20ConcreteResources.add(branchManager.getResourcePrototypes().get(1));
		task20ConcreteResources.add(branchManager.getResourcePrototypes().get(1));
		task20ConcreteResources.add(branchManager.getResourcePrototypes().get(1));
		branchManager.planTask(project2, task20, task20start, task20ConcreteResources,devList2);
					
		// Stap verder:
		// maak TASK 0,0 af -> project 0 is finished
		// update project 1
		branchManager.advanceTimeTo(workdate3);
		// 00 FINISHED
		branchManager.setTaskFinished(project0, task00, task00End);										
		//----------------------------------------------------
		// 10 FINISHED
		branchManager.planTask(project1, task10, task10Start, task10ConcreteResources,devList1);
		branchManager.setTaskExecuting(project1, task10, task10Start);
		branchManager.setTaskFinished(project1, task10, task10End);
		// 11 FAILED
		branchManager.planTask(project1, task11, task11Start, task11ConcreteResources,devList1);
		branchManager.setTaskExecuting(project1, task11, task11Start);
		branchManager.setTaskFailed(project1, task11, task11End);
		// 13 AVAILABLE
		branchManager.createTask(project1, "TASK 13", task13EstDur, task13Dev, task13Dependencies,task11.getRequiredResources() ,task11);
		TaskView task13 = project1.getTasks().get(3);
		branchManager.planTask(project1, task13, task13Start, task11ConcreteResources,devList1);
			
		// Stap verder:
		// maak het vierde project aan en maak zijn TASK lijst
		branchManager.advanceTimeTo(workdate4);
		branchManager.createProject("Project 3", "Describing proj 3", project3DueDate);
		ProjectView project3 = branchManager.getProjects().get(3);
		// 30 AVAILABLE
		Map<ResourceView, Integer> reqResTask30 = new HashMap<>();
		reqResTask30.put(branchManager.getResourcePrototypes().get(0), 2);
		reqResTask30.put(branchManager.getResourcePrototypes().get(1), 3);
		branchManager.createTask(project3, "TASK 30", task30EstDur, task30Dev, task30Dependencies,reqResTask30, null);
		task30ConcreteResources.add(branchManager.getResourcePrototypes().get(0));
		task30ConcreteResources.add(branchManager.getResourcePrototypes().get(0));
		task30ConcreteResources.add(branchManager.getResourcePrototypes().get(1));
		task30ConcreteResources.add(branchManager.getResourcePrototypes().get(1));
		task30ConcreteResources.add(branchManager.getResourcePrototypes().get(1));
		// 31 AVAILABLE
		Map<ResourceView, Integer> reqResTask31 = new HashMap<>();
		reqResTask31.put(branchManager.getResourcePrototypes().get(0), 1);
		branchManager.createTask(project3, "TASK 31", task31EstDur, task31Dev, task31Dependencies,reqResTask31, null);
		TaskView task31 = project3.getTasks().get(1);
		task31ConcreteResources.add(branchManager.getResourcePrototypes().get(0));
		branchManager.planTask(project3, task31, task30Start, task31ConcreteResources,devList3);
		
		//32 UNAVAILABLE
		branchManager.createTask(project3, "TASK 32", task31EstDur, task31Dev, task31Dependencies,reqResTask31, null);
				
		// Stap verder:
		// maak task 3,0 FAILED
		branchManager.advanceTimeTo(workdate5);
		TaskView task30 = project3.getTasks().get(0);
		// 30 FAILED DELAYED
		branchManager.planTask(project3, task30, task30Start,task30ConcreteResources,devList2);
		branchManager.setTaskExecuting(project3, task30, task30Start);
		branchManager.setTaskFailed(project3, task30, task30End);	
		
		//Nieuw branch office zonder projecten
		branchManager.initializeBranch("Aarschot");
		
		//INIT resources
				for(int i = 0;i<=5;i++){
					branchManager.declareConcreteResource("car" + i, branchManager.getResourcePrototypes().get(0));
				}
				for(int i = 0;i<=5;i++){
					branchManager.declareConcreteResource("whiteboard" + i, branchManager.getResourcePrototypes().get(1));
				}
				
				
		branchManager.initializeBranch("Maaskantje");
				
		//INIT resources
				for(int i = 0;i<=5;i++){
					branchManager.declareConcreteResource("car" + i, branchManager.getResourcePrototypes().get(0));
				}
				for(int i = 0;i<=5;i++){
					branchManager.declareConcreteResource("whiteboard" + i, branchManager.getResourcePrototypes().get(1));
				}
		
	}
	
	@Test
	public void succesCaseTest() {
		branchManager.selectBranch(branchManager.getBranches().get(0));
		assertEquals(branchManager.getProjects().get(3).getTasks().get(2).getStatusAsString(),"Unavailable");
		
		branchManager.delegateTask(branchManager.getProjects().get(3), branchManager.getProjects().get(3).getTasks().get(2), branchManager.getBranches().get(1));
		assertEquals(branchManager.getProjects().get(3).getTasks().get(2).getStatusAsString(),"Delegated");
		branchManager.selectBranch(branchManager.getBranches().get(1));
		TaskView delegationTask = branchManager.getAllProjects().get(0).getTasks().get(0);
		assertEquals(delegationTask.getStatusAsString(),"Unavailable");
		assertEquals(delegationTask.getDescription(),"TASK 32");
		
	}
	
	
	@Test(expected = TaskManException.class)
	public void availableTaskFail() {
		branchManager.selectBranch(branchManager.getBranches().get(0));
		assertEquals(branchManager.getProjects().get(2).getTasks().get(0).getStatusAsString(),"Available");
		branchManager.delegateTask(branchManager.getProjects().get(2), branchManager.getProjects().get(2).getTasks().get(0), branchManager.getBranches().get(1));
	}
	
	@Test(expected = TaskManException.class)
	public void finishedTaskFail() {
		branchManager.selectBranch(branchManager.getBranches().get(0));
		assertEquals(branchManager.getProjects().get(0).getTasks().get(0).getStatusAsString(),"Finished");
		branchManager.delegateTask(branchManager.getProjects().get(0), branchManager.getProjects().get(0).getTasks().get(0), branchManager.getBranches().get(1));
	}
	
	@Test
	public void succesDelegateBackTest() {
		branchManager.selectBranch(branchManager.getBranches().get(0));
		assertEquals(branchManager.getProjects().get(3).getTasks().get(2).getStatusAsString(),"Unavailable");
		
		branchManager.delegateTask(branchManager.getProjects().get(3), branchManager.getProjects().get(3).getTasks().get(2), branchManager.getBranches().get(1));
		assertEquals(branchManager.getProjects().get(3).getTasks().get(2).getStatusAsString(),"Delegated");
		branchManager.selectBranch(branchManager.getBranches().get(1));
		TaskView delegationTask = branchManager.getAllProjects().get(0).getTasks().get(0);
		assertEquals(delegationTask.getStatusAsString(),"Unavailable");
		assertEquals(delegationTask.getDescription(),"TASK 32");
		
		branchManager.delegateTask(branchManager.getAllProjects().get(0), branchManager.getAllProjects().get(0).getTasks().get(0), branchManager.getBranches().get(0));
		assertEquals(branchManager.getAllProjects().get(0).getTasks().size(),0);
		branchManager.selectBranch(branchManager.getBranches().get(0));
		assertEquals(branchManager.getProjects().get(3).getTasks().get(2).getStatusAsString(),"Unavailable");
	}
	
	@Test
	public void succesDelegateFurtherTest() {
		branchManager.selectBranch(branchManager.getBranches().get(0));
		assertEquals(branchManager.getProjects().get(3).getTasks().get(2).getStatusAsString(),"Unavailable");
		
		branchManager.delegateTask(branchManager.getProjects().get(3), branchManager.getProjects().get(3).getTasks().get(2), branchManager.getBranches().get(1));
		assertEquals(branchManager.getProjects().get(3).getTasks().get(2).getStatusAsString(),"Delegated");
		branchManager.selectBranch(branchManager.getBranches().get(1));
		TaskView delegationTask = branchManager.getAllProjects().get(0).getTasks().get(0);
		assertEquals(delegationTask.getStatusAsString(),"Unavailable");
		assertEquals(delegationTask.getDescription(),"TASK 32");
		
		branchManager.delegateTask(branchManager.getAllProjects().get(0), delegationTask, branchManager.getBranches().get(2));
		assertEquals(branchManager.getAllProjects().get(0).getTasks().size(),0);
		branchManager.selectBranch(branchManager.getBranches().get(2));
		TaskView furtherDelegationTask = branchManager.getAllProjects().get(0).getTasks().get(0);
		assertEquals(furtherDelegationTask.getStatusAsString(),"Unavailable");
		assertEquals(furtherDelegationTask.getDescription(),"TASK 32");
	}
	
	@Test
	public void delegateToSelf() {
		branchManager.selectBranch(branchManager.getBranches().get(0));
		assertEquals(branchManager.getProjects().get(3).getTasks().get(2).getStatusAsString(),"Unavailable");
		branchManager.delegateTask(branchManager.getProjects().get(3), branchManager.getProjects().get(3).getTasks().get(2), branchManager.getBranches().get(0));
		assertEquals(branchManager.getProjects().get(3).getTasks().get(2).getStatusAsString(),"Unavailable");
	}
	
	
}