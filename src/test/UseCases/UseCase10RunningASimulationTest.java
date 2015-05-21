package test.UseCases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;


import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import company.BranchManager;
import company.taskMan.ProjectView;
import company.taskMan.project.TaskView;
import company.taskMan.resource.ResourceView;
import userInterface.IFacade;

public class UseCase10RunningASimulationTest {

	private IFacade branchManager;
	private final LocalDateTime startDate = LocalDateTime.of(2015, 2, 9, 8, 0),
			workDate1 = LocalDateTime.of(2015, 2, 9, 12, 0),
			workDate2 = LocalDateTime.of(2015, 2, 10, 16, 0),
			workDate3 = LocalDateTime.of(2015, 	2, 11, 15, 0),
			project0DueDate = LocalDateTime.of(2015, 2, 13, 23, 59),
			project1DueDate = LocalDateTime.of(2015, 2, 15, 10, 0),
			project2DueDate = LocalDateTime.of(2015, 2, 12, 10, 0),
			task00Start = startDate,
			task00End = LocalDateTime.of(2015, 2, 9, 10, 0),
			task01Start = LocalDateTime.of(2015, 2, 10, 8, 0),
			task01End = LocalDateTime.of(2015, 2, 10, 15, 0),
			task02Start = LocalDateTime.of(2015, 2, 12, 8, 0),
			task10Start = LocalDateTime.of(2015, 2, 10, 10, 0),
			task20Start = LocalDateTime.of(2015, 2, 9, 8, 0),
			task20End = LocalDateTime.of(2015, 2, 9, 11, 0);
	private final int task00EstDur = 120,
			task01EstDur = 8*60,
			task02EstDur = 180,
			task10EstDur = 180,
			task20EstDur = 180;
	private final int task00Dev = 5,
			task01Dev = 30,
			task02Dev = 10,
			task10Dev = 10,
			task20Dev = 5;
	private ArrayList<TaskView> task00Dependencies,
			task01Dependencies,
			task02Dependencies,
			task10Dependencies,
			task20Dependencies;
	private Map<ResourceView, Integer> reqResTask00,
			reqResTask01,
			reqResTask02,
			reqResTask10,
			reqResTask20;
	private ArrayList<ResourceView> task00ConcRes,
			task01ConcRes,
			task02ConcRes,
			task10ConcRes,
			task20ConcRes;
	private ResourceView dev1,
			dev2,
			dev3;
	private ArrayList<ResourceView> task00Devs,
			task01Devs,
			task02Devs,
			task10Devs,
			task20Devs;
	private final Optional<LocalTime> emptyAvailabilityStart = Optional.empty(),
			emptyAvailabilityEnd = Optional.empty();

	/**
	 * Project0:			Ongoing
	 * task00				Finished
	 * task01<-task00		Failed
	 * taskAlt for task01	Finished
	 * task02<-task01		Planned
	 * 
	 * Project1:			Ongoing
	 * task10				Planned
	 * 
	 * Project2:			Finished
	 * task20				Finished
	 * 
	 */
	@Before
	public void initialize(){
		//INIT system 
		branchManager = new BranchManager(startDate);
		//INIT resources
		
		branchManager.createResourcePrototype("car", emptyAvailabilityStart, emptyAvailabilityEnd);
		branchManager.createResourcePrototype("whiteboard", emptyAvailabilityStart, emptyAvailabilityEnd);
		
		branchManager.initializeBranch("Leuven");
		branchManager.initializeBranch("Aarschot");
		branchManager.selectBranch(branchManager.getBranches().get(0));
		
		for(int i = 0;i<=5;i++){
			branchManager.declareConcreteResource("car" + i, branchManager.getResourcePrototypes().get(0));
		}
		
		for(int i = 0;i<=5;i++){
			branchManager.declareConcreteResource("whiteboard" + i, branchManager.getResourcePrototypes().get(1));
		}
		//Set up container objects
		task00Dependencies = new ArrayList<TaskView>();
		reqResTask00 = new HashMap<>();
		task00ConcRes = new ArrayList<>();
		task00Devs = new ArrayList<>();
		task01Dependencies = new ArrayList<TaskView>();
		reqResTask01 = new HashMap<>();
		task01ConcRes = new ArrayList<>();
		task01Devs = new ArrayList<>();
		task02Dependencies = new ArrayList<TaskView>();
		reqResTask02 = new HashMap<>();
		task02ConcRes = new ArrayList<>();
		task02Devs = new ArrayList<>();
		task10Dependencies = new ArrayList<TaskView>();
		reqResTask10 = new HashMap<>();
		task10ConcRes = new ArrayList<>();
		task10Devs = new ArrayList<>();
		task20Dependencies = new ArrayList<TaskView>();
		reqResTask20 = new HashMap<>();
		task20ConcRes = new ArrayList<>();
		task20Devs = new ArrayList<>();
		//Create Developers
		branchManager.createDeveloper("Achilles");
		dev1 = branchManager.getDeveloperList().get(0);
		branchManager.createDeveloper("Ajax");
		dev2 = branchManager.getDeveloperList().get(1);
		branchManager.createDeveloper("Odysseus");
		dev3 = branchManager.getDeveloperList().get(2);
		//Create Developers lists
		task00Devs.add(dev1);
		task00Devs.add(dev2);
		task01Devs = task00Devs;
		task02Devs = task00Devs;
		task10Devs.add(dev3);
		task20Devs.add(dev3);
		//Create first project
		branchManager.createProject("Project 0", "Describing proj 0", project0DueDate);
		ProjectView project0 = branchManager.getProjects().get(0);
		// Create task00
		reqResTask00.put(branchManager.getResourcePrototypes().get(0), 2);
		reqResTask00.put(branchManager.getResourcePrototypes().get(1), 1);
		branchManager.createTask(project0, "TASK 00", task00EstDur, task00Dev, task00Dependencies,reqResTask00,null);	
		TaskView task00 = project0.getTasks().get(0);
		//Create task01
		task01Dependencies.add(task00);
		reqResTask01.put(branchManager.getResourcePrototypes().get(0), 1);
		reqResTask01.put(branchManager.getResourcePrototypes().get(1), 1);
		branchManager.createTask(project0, "TASK 01", task01EstDur, task01Dev, task01Dependencies, reqResTask01, null);
		//Create task02
		TaskView task01 = branchManager.getProjects().get(0).getTasks().get(1);
		task02Dependencies.add(task01);
		reqResTask02.put(branchManager.getResourcePrototypes().get(0), 1);
		branchManager.createTask(project0, "TASK 02", task02EstDur, task02Dev, task02Dependencies, reqResTask02, null);
		TaskView task02 = branchManager.getProjects().get(0).getTasks().get(2);
		//Create second project
		branchManager.createProject("Project1", "Project1", project1DueDate);
		ProjectView project1 = branchManager.getProjects().get(1);
		//Create task10
		reqResTask10.put(branchManager.getResourcePrototypes().get(1), 1);
		branchManager.createTask(project1, "TASK 10", task10EstDur, task10Dev, task10Dependencies, reqResTask10, null);
		TaskView task10 = branchManager.getProjects().get(1).getTasks().get(0);
		//Create third project
		branchManager.createProject("Project3", "Project3", project2DueDate);
		ProjectView project2 = branchManager.getProjects().get(2);
		//Create task20
		branchManager.createTask(project2, "TASK20", task20EstDur, task20Dev, task20Dependencies, reqResTask20, null);
		TaskView task20 = branchManager.getProjects().get(2).getTasks().get(0);
		//Plan task00
		task00ConcRes.add(branchManager.getConcreteResourcesForPrototype(branchManager.getResourcePrototypes().get(0)).get(0));
		task00ConcRes.add(branchManager.getConcreteResourcesForPrototype(branchManager.getResourcePrototypes().get(0)).get(1));
		task00ConcRes.add(branchManager.getConcreteResourcesForPrototype(branchManager.getResourcePrototypes().get(1)).get(0));
		branchManager.planTask(project0, task00, task00Start, task00ConcRes, task00Devs);
		//Plan task20
		branchManager.planTask(project2, task20, task20Start, task20ConcRes, task20Devs);
		//Execute and finish task00
		branchManager.advanceTimeTo(workDate1);
		branchManager.setTaskExecuting(project0, task00, task00Start);
		branchManager.setTaskFinished(project0, task00, task00End);
		//Execute and finish task20
		branchManager.setTaskExecuting(project2, task20, task20Start);
		branchManager.setTaskFinished(project2, task20, task20End);
		assertTrue(branchManager.getProjects().get(2).isFinished());
		//plan task01
		task01ConcRes.add((branchManager.getResourcePrototypes().get(0)));
		task01ConcRes.add((branchManager.getResourcePrototypes().get(1)));
		branchManager.planTask(project0, task01, task01Start, task01ConcRes, task01Devs);
		//Execute and fail task01
		branchManager.advanceTimeTo(workDate2);
		branchManager.setTaskExecuting(project0, task01, task01Start);
		branchManager.setTaskFailed(project0, task01, task01End);
		task02ConcRes.add(branchManager.getResourcePrototypes().get(0));	
//		branchManager.planTask(project0, task02, task02Start, task02ConcRes, task02Devs);
		//Plan task10
		task10ConcRes.add(branchManager.getResourcePrototypes().get(1));
		branchManager.planTask(project1, task10, task10Start, task10ConcRes, task10Devs);
		//create an alternative
		branchManager.createTask(project0, "Alt", 60, 5, task01Dependencies, reqResTask01, task01);
		TaskView taskAlt = branchManager.getProjects().get(0).getTasks().get(3);
		//Plan alternative
		branchManager.planTask(project0, taskAlt, LocalDateTime.of(2015, 2, 11, 8, 0), task01ConcRes, task01Devs);
		//Succeed Alternative
		branchManager.advanceTimeTo(workDate3);
		branchManager.setTaskExecuting(project0, taskAlt, LocalDateTime.of(2015, 2, 11, 8, 0));
		branchManager.setTaskFinished(project0, taskAlt, LocalDateTime.of(2015, 2, 11, 9, 0));
		//Plan task02
		branchManager.planTask(project0, task02, task02Start, task02ConcRes, task02Devs);
		// INIT current user
		branchManager.changeToUser(branchManager.getPossibleUsers().get(0));
	}
	
	@Test
	public void succesCaseSimpleRevertMem(){
		//Set up memento
		branchManager.storeInMemento();
		//Some simulation
		ProjectView project0 = branchManager.getProjects().get(0);
		HashMap<ResourceView, Integer> reqResTest = new HashMap<>();
		branchManager.createTask(project0, "test", 50, 5, new ArrayList<TaskView>(), reqResTest, null);
		ProjectView proj1 = branchManager.getProjects().get(1);
		TaskView task10 = proj1.getTasks().get(0);
		branchManager.changeToUser(branchManager.getPossibleUsers().get(1));
		branchManager.setTaskExecuting(proj1, task10, task10Start);
		branchManager.setTaskFinished(proj1, task10, LocalDateTime.of(2015, 2, 10, 14, 0));
		assertTrue(proj1.isFinished());
		assertEquals(3,branchManager.getProjects().size());
		assertEquals(5,branchManager.getProjects().get(0).getTasks().size());
		//Revert the memento
		branchManager.revertFromMemento();
		assertEquals(3,branchManager.getProjects().size());
		project0 = branchManager.getProjects().get(0);
		//Check the first project
		assertEquals(4,branchManager.getProjects().get(0).getTasks().size());
		assertEquals("finished",project0.getTasks().get(0).getStatusAsString().toLowerCase());
		assertEquals("failed",project0.getTasks().get(1).getStatusAsString().toLowerCase());
		assertTrue(project0.getTasks().get(2).isPlanned());
		assertEquals("finished",project0.getTasks().get(3).getStatusAsString().toLowerCase());
		//Check the second project
		ProjectView project1 = branchManager.getProjects().get(1);
		assertEquals(1, project1.getTasks().size());
		assertTrue(project1.getTasks().get(0).isPlanned());
		//Check the third project
		ProjectView project2 = branchManager.getProjects().get(2);
		assertEquals(1, project2.getTasks().size());
		assertEquals("finished",project2.getTasks().get(0).getStatusAsString().toLowerCase());
		assertTrue(project2.isFinished());
	}
	
	@Test
	public void succesCaseSimpleDiscardMem(){
		//Memento set up
		branchManager.storeInMemento();
		//Some simulations
		ProjectView project0 = branchManager.getProjects().get(0);
		HashMap<ResourceView, Integer> reqResTest = new HashMap<>();
		branchManager.createTask(project0, "test", 50, 5, new ArrayList<TaskView>(), reqResTest, null);
		ProjectView proj1 = branchManager.getProjects().get(1);
		TaskView task10 = proj1.getTasks().get(0);
		branchManager.changeToUser(branchManager.getPossibleUsers().get(1));
		branchManager.setTaskExecuting(proj1, task10, task10Start);
		branchManager.setTaskFinished(proj1, task10, LocalDateTime.of(2015, 2, 10, 14, 0));
		assertTrue(proj1.isFinished());
		assertEquals(3,branchManager.getProjects().size());
		assertEquals(5,branchManager.getProjects().get(0).getTasks().size());
		//Discard the memento
		branchManager.discardMemento();
		assertEquals(3,branchManager.getProjects().size());
		//Check the first project
		assertEquals(5,branchManager.getProjects().get(0).getTasks().size());
		project0 = branchManager.getProjects().get(0);
		assertEquals("finished",project0.getTasks().get(0).getStatusAsString().toLowerCase());
		assertEquals("failed",project0.getTasks().get(1).getStatusAsString().toLowerCase());
		assertTrue(project0.getTasks().get(2).isPlanned());
		assertEquals("finished",project0.getTasks().get(3).getStatusAsString().toLowerCase());
		assertTrue(project0.getTasks().get(4).isUnavailable());
		//Check the second project
		assertEquals(1, branchManager.getProjects().get(1).getTasks().size());
		assertTrue(branchManager.getProjects().get(1).isFinished());
		//Check the third project
		ProjectView project2 = branchManager.getProjects().get(2);
		assertEquals(1, project2.getTasks().size());
		assertEquals("finished",project2.getTasks().get(0).getStatusAsString().toLowerCase());
		assertTrue(project2.isFinished());
	}
	
	@Test
	public void succesCaseDelegateRevertMem(){
		ProjectView project0 = branchManager.getProjects().get(0);
		//create an task to delegate
		branchManager.createTask(project0, "delegation", 60, 5, new ArrayList<TaskView>(), reqResTask00, null);
		TaskView newTask = project0.getTasks().get( project0.getTasks().size()-1);
		assertFalse(newTask.isDelegated());
		
		//Memento set up
		branchManager.storeInMemento();
		
		//delegate the task to the other branch
		branchManager.delegateTask(project0, newTask, branchManager.getBranches().get(1));
		Optional<TaskView> delTask = branchManager.getDelegatingTask(project0, newTask);
		assertTrue(delTask.isPresent());
		
		//revert the memento
		branchManager.revertFromMemento();
		assertFalse(newTask.isDelegated());
	}
}
