package taskMan.test.UseCases;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import userInterface.IFacade;
import taskMan.Facade;
import taskMan.view.ProjectView;
import taskMan.view.ResourceView;
import taskMan.view.TaskView;


public class UseCase8RunningASimulationTest {

	private IFacade taskManager;
	private final LocalDateTime startDate = LocalDateTime.of(2015, 2, 9, 8, 0),
			workDate1 = LocalDateTime.of(2015, 2, 9, 11, 0),
			project0DueDate = LocalDateTime.of(2015, 2, 13, 23, 59),
			task00Start = startDate,
			task00End = LocalDateTime.of(2015, 2, 9, 10, 0),
			task01Start = LocalDateTime.of(2015, 2, 12, 8, 0);
	private final int task00EstDur = 120,
			task01EstDur = 8*60;
	private final int task00Dev = 5,
			task01Dev = 30;
	private ArrayList<TaskView> task00Dependencies,
			task01Dependencies;
	private Map<ResourceView, Integer> reqResTask00,
			reqResTask01;
	private ArrayList<ResourceView> task00ConcRes,
			task01ConcRes;
	private final Optional<LocalTime> emptyAvailabilityStart = Optional.empty(),
			emptyAvailabilityEnd = Optional.empty();

	@Before
	public void initialize(){
		//INIT system 
		taskManager = new Facade(startDate);
		//INIT resources
		taskManager.createResourcePrototype("car", emptyAvailabilityStart, emptyAvailabilityEnd);
		for(int i = 0;i<=5;i++){
			taskManager.declareConcreteResource("car" + i, taskManager.getResourcePrototypes().get(0));
		}
		taskManager.createResourcePrototype("whiteboard", Optional.empty(), Optional.empty());
		for(int i = 0;i<=5;i++){
			taskManager.declareConcreteResource("whiteboard" + i, taskManager.getResourcePrototypes().get(1));
		}
		//Set up container objects
		task00Dependencies = new ArrayList<TaskView>();
		reqResTask00 = new HashMap<>();
		task00ConcRes = new ArrayList<>();
		task01Dependencies = new ArrayList<TaskView>();
		reqResTask01 = new HashMap<>();
		task01ConcRes = new ArrayList<>();
		//Create first project
		assertTrue(taskManager.createProject("Project 0", "Describing proj 0", project0DueDate));
		ProjectView project0 = taskManager.getProjects().get(0);
		// Create a first Task
		reqResTask00.put(taskManager.getResourcePrototypes().get(0), 2);
		reqResTask00.put(taskManager.getResourcePrototypes().get(1), 1);
		assertTrue(taskManager.createTask(project0, "TASK 00", task00EstDur, task00Dev, task00Dependencies,reqResTask00,null));	
		TaskView task00 = project0.getTasks().get(0);
		//Create a second Task
		task01Dependencies.add(task00);
		reqResTask01.put(taskManager.getResourcePrototypes().get(0), 1);
		reqResTask01.put(taskManager.getResourcePrototypes().get(1), 1);
		assertTrue(taskManager.createTask(project0, "TASK 01", task01EstDur, task01Dev, task01Dependencies, reqResTask01, null));
		//Plan first Task
		task00ConcRes.add(taskManager.getConcreteResourcesForPrototype(taskManager.getResourcePrototypes().get(0)).get(0));
		task00ConcRes.add(taskManager.getConcreteResourcesForPrototype(taskManager.getResourcePrototypes().get(0)).get(1));
		task00ConcRes.add(taskManager.getConcreteResourcesForPrototype(taskManager.getResourcePrototypes().get(1)).get(0));
		assertTrue(taskManager.planTask(project0, task00, task00Start, task00ConcRes));
		//Execute and finish first task
		assertTrue(taskManager.setTaskExecuting(project0, task00, task00Start));
		assertTrue(taskManager.advanceTimeTo(workDate1));
		assertTrue(taskManager.setTaskFinished(project0, task00, task00End));
		//plan second task
		TaskView task01 = project0.getTasks().get(0);
		task01ConcRes.add((taskManager.getResourcePrototypes().get(0)));
		task01ConcRes.add((taskManager.getResourcePrototypes().get(1)));
		assertTrue(taskManager.planTask(project0, task01, task01Start, task01ConcRes));
	}
	
	@Test
	public void succesCaseSimpleRevertMem(){
		taskManager.storeInMemento();
		ProjectView project0 = taskManager.getProjects().get(0);
		HashMap<ResourceView, Integer> reqResTest = new HashMap<>();
		taskManager.createTask(project0, "test", 50, 5, new ArrayList<TaskView>(), reqResTest, null);
		assertEquals(1,taskManager.getProjects().size());
		assertEquals(3,taskManager.getProjects().get(0).getTasks().size());
		assertTrue(taskManager.revertFromMemento());
		assertEquals(1,taskManager.getProjects().size());
		assertEquals(2,taskManager.getProjects().get(0).getTasks().size());
	}
	
	@Test
	public void succesCaseSimpleDiscardMem(){
		taskManager.storeInMemento();
		ProjectView project0 = taskManager.getProjects().get(0);
		HashMap<ResourceView, Integer> reqResTest = new HashMap<>();
		taskManager.createTask(project0, "test", 50, 5, new ArrayList<TaskView>(), reqResTest, null);
		assertEquals(1,taskManager.getProjects().size());
		assertEquals(3,taskManager.getProjects().get(0).getTasks().size());
		assertTrue(taskManager.discardMemento());
		assertEquals(1,taskManager.getProjects().size());
		assertEquals(3,taskManager.getProjects().get(0).getTasks().size());
	}
}
