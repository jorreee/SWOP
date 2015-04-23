package taskMan.test.UseCases;

import static org.junit.Assert.assertEquals;
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
import taskMan.Project;
import taskMan.Task;
import taskMan.resource.ResourceManager;
import taskMan.view.ProjectView;
import taskMan.view.ResourceView;
import taskMan.view.TaskView;
import userInterface.IFacade;

public class UseCase3CreateTaskTest {

	private IFacade taskManager;
	private final LocalDateTime startDate = LocalDateTime.of(2015, 2, 9, 8, 0),
			project0DueDate = LocalDateTime.of(2015, 2, 13, 23, 59),
			task00StartDateGood = startDate,
			task00EndDateGood = LocalDateTime.of(2015,2,9,10,0),
			workDate = LocalDateTime.of(2015, 2, 9, 16, 0),
			altTaskEndDate = LocalDateTime.of(2015, 2, 9, 10, 0),
			task01StartDateGood = task00EndDateGood,
			task01EndDateGood = LocalDateTime.of(2015, 2, 9, 16, 0);
	private final int task00EstDur = 8*60,
			task01EstDur = 16*60,
			newTaskDur = 5*60;
	private final int task00Dev = 0,
			task01Dev = 50,
			newTaskDev = 10;
	private final ArrayList<TaskView> task00Dependencies = new ArrayList<TaskView>(),
			 						 task01Dependencies = new ArrayList<TaskView>(),
									 newTaskDependencies = new ArrayList<TaskView>();
	private final Map<ResourceView, Integer> reqResTask00 = new HashMap<>(),
			noReq = new HashMap<>();
	private final ArrayList<ResourceView> task00ConcreteResources = new ArrayList<ResourceView>(),
			task01ConcreteResources = new ArrayList<ResourceView>();
//			newTaskConcreteResources = new ArrayList<ResourceView>();
	private final List<ResourceView> devList1 = new ArrayList<ResourceView>(),
			devList2 = new ArrayList<ResourceView>();
	private ResourceView weer, blunderbus;
	private final Optional<LocalTime> emptyAvailabilityPeriodStart = Optional.empty(),
			emptyAvailabilityPeriodEnd = Optional.empty();

	/**
	 * - project 1 START 9 feb 8u DUE 13 feb midnight
	 * 		GEEN tasks aanwezig
	 */
	@Before
	public final void initialize() {
		taskManager = new Facade(startDate);

		assertTrue(taskManager.createProject("Test1", "testing 1", project0DueDate));
		taskManager.createResourcePrototype("car", emptyAvailabilityPeriodStart, emptyAvailabilityPeriodEnd);
		for(int i = 0;i<=5;i++){
			taskManager.declareConcreteResource("car" + i, taskManager.getResourcePrototypes().get(0));
		}
		taskManager.createResourcePrototype("whiteboard", emptyAvailabilityPeriodStart, emptyAvailabilityPeriodEnd);
		for(int i = 0;i<=5;i++){
			taskManager.declareConcreteResource("whiteboard" + i, taskManager.getResourcePrototypes().get(1));
		}
		reqResTask00.put(taskManager.getResourcePrototypes().get(0), 2);
		reqResTask00.put(taskManager.getResourcePrototypes().get(1), 1);
		taskManager.createDeveloper("Weer");
		taskManager.createDeveloper("Blunderbus");
		weer = taskManager.getDeveloperList().get(0);
		blunderbus = taskManager.getDeveloperList().get(1);
		devList1.add(weer);
		devList2.add(blunderbus);
		assertTrue(taskManager.advanceTimeTo(workDate));

	}

	@Test
	public void SuccesCaseTest() {
		// Step 1 and 2 are implicit
		// Step 3
		List<ProjectView> projects = taskManager.getProjects();
		assertTrue(projects.size() == 1);
		ProjectView project0 = projects.get(0);
		assertTrue(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies,reqResTask00, null));
		// Step 4
		List<TaskView> p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 1);
		TaskView t = p0tasks.get(0);
		
		assertTrue(t.getDescription().equals("A new TASK"));
		assertEquals(t.getEstimatedDuration(), newTaskDur);
		assertEquals(t.getAcceptableDeviation(),newTaskDev);
		assertFalse(t.isAlternative());
		assertFalse(t.hasPrerequisites());
	}

	@Test
	public void SuccesCaseALTTest() {

		// Er is al een FAILED task aanwezig in het project
		List<ProjectView> projects = taskManager.getProjects();
		assertTrue(projects.size() == 1);
		ProjectView project0 = projects.get(0);
		assertTrue(taskManager.createTask(project0, "Design system", task00EstDur, task00Dev, task00Dependencies,noReq, null));
		
		List<TaskView> p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 1);
		TaskView t00 = p0tasks.get(0);
		assertTrue(taskManager.planTask(project0, t00, startDate,task00ConcreteResources,devList1));
		assertTrue(taskManager.setTaskExecuting(project0, t00, startDate));
		assertTrue(taskManager.setTaskFailed(project0, t00, altTaskEndDate));
		
		
		// Step 1 and 2 are implicit
		// Step 3
		assertTrue(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies,reqResTask00, t00));
		// Step 4
		p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 2);
		TaskView t01 = p0tasks.get(1);
		
		assertTrue(t01.getDescription().equals("A new TASK"));
		assertEquals(t01.getEstimatedDuration(), newTaskDur);
		assertEquals(t01.getAcceptableDeviation(),newTaskDev);
		assertTrue(t01.isAlternative());
		assertTrue(t01.getAlternativeTo().equals(t00));					//
		assertFalse(t01.hasPrerequisites());
//		assertTrue(t01.getStatusAsString().equals("Available"));
//		assertEquals(project0.getAvailableTasks().size(),1);
		
	}

	@Test
	public void SuccesCaseDepTest() {

		// Er is al een task aanwezig in het project
		List<ProjectView> projects = taskManager.getProjects();
		assertTrue(projects.size() == 1);
		ProjectView project0 = projects.get(0);
		assertTrue(taskManager.createTask(project0, "Design system", task00EstDur, task00Dev, task00Dependencies, reqResTask00, null));
		
		List<TaskView> p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 1);
		TaskView t00 = p0tasks.get(0);
//		assertTrue(t00.getStatusAsString().equals("Available"));

		// Step 1 and 2 are implicit
		// Step 3
		newTaskDependencies.add(t00);
		assertTrue(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies,reqResTask00, null));
		// Step 4
		p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 2);
		TaskView t01 = p0tasks.get(1);
		
		assertTrue(t01.getDescription().equals("A new TASK"));
		assertEquals(t01.getEstimatedDuration(), newTaskDur);
		assertEquals(t01.getAcceptableDeviation(),newTaskDev);
		assertFalse(t01.isAlternative());
		assertTrue(t01.hasPrerequisites());
		assertFalse(t01.isAlternative());
		assertTrue(t01.hasPrerequisites());
		assertTrue(taskViewListContains(t01.getPrerequisites(),t00));				//
		assertTrue(t01.getStatusAsString().equals("Unavailable"));
//		assertEquals(project0.getAvailableTasks().size(),1);
		
	}
	
	@Test
	public void SuccesCaseFailedDepNoAltTest() {

		// Er is al een FAILED task aanwezig in het project
		List<ProjectView> projects = taskManager.getProjects();
		assertTrue(projects.size() == 1);
		ProjectView project0 = projects.get(0);
		assertTrue(taskManager.createTask(project0, "Design system", task00EstDur, task00Dev, task00Dependencies,noReq, null));

		List<TaskView> p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 1);
		TaskView t00 = p0tasks.get(0);
		assertTrue(taskManager.planTask(project0, t00, startDate,task00ConcreteResources,devList1));
		assertTrue(taskManager.setTaskExecuting(project0, t00, startDate));
		assertTrue(taskManager.setTaskFailed(project0, t00, altTaskEndDate));
		assertEquals(t00.getStatusAsString(),"Failed");

		// Step 1 and 2 are implicit
		// Step 3
		newTaskDependencies.add(t00);
		Map<ResourceView, Integer> reqResTask = new HashMap<ResourceView, Integer>();
		reqResTask.put(taskManager.getResourcePrototypes().get(0), 2);
		reqResTask.put(taskManager.getResourcePrototypes().get(1), 1);
		assertTrue(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies,reqResTask, null));
		// Step 4
		p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 2);
		TaskView t01 = p0tasks.get(1);
		
		assertTrue(t01.getDescription().equals("A new TASK"));
		assertEquals(t01.getEstimatedDuration(), newTaskDur);
		assertEquals(t01.getAcceptableDeviation(), newTaskDev);
		assertFalse(t01.isAlternative());
		assertTrue(t01.hasPrerequisites());
		assertTrue(t01.getStatusAsString().equals("Unavailable"));
		assertEquals(project0.getAvailableTasks().size(),0);
	}
	
	@Test
	public void SuccesCaseFailedDepWithAltTest() {

		// Er is al een FAILED task MET ALT aanwezig in het project
		List<ProjectView> projects = taskManager.getProjects();
		assertTrue(projects.size() == 1);
		ProjectView project0 = projects.get(0);
		
		assertTrue(taskManager.createTask(project0, "Design system", task00EstDur, task00Dev, task00Dependencies, noReq, null));
		List<TaskView> p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 1);
		TaskView t00 = p0tasks.get(0);
		
		assertTrue(taskManager.planTask(project0, t00, startDate,task00ConcreteResources,devList1));
		assertTrue(taskManager.setTaskExecuting(project0, t00, startDate));
		assertTrue(taskManager.setTaskFailed(project0, t00, task00EndDateGood));
		assertEquals(t00.getStatusAsString(),"Failed");

		assertTrue(taskManager.createTask(project0, "Implement native", task01EstDur, task01Dev, task01Dependencies, noReq, t00));
		p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 2);
		TaskView t01 = p0tasks.get(1);
		taskManager.planTask(project0, t01, task01StartDateGood, task01ConcreteResources,devList1);
		assertEquals(t01.getStatusAsString(),"Available");

		// Step 1 and 2 are implicit
		// Step 3
		newTaskDependencies.add(t00);
		Map<ResourceView,Integer>reqRes = new HashMap<>();
		reqRes.put(taskManager.getResourcePrototypes().get(0), 1);
		assertTrue(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies, reqRes,null));
		// Step 4
		p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 3);
		TaskView nt = p0tasks.get(2);
		
		assertTrue(nt.hasPrerequisites());
		assertTrue(taskViewListContains(nt.getPrerequisites(),t00));
		assertFalse(nt.getPrerequisites().contains(t01));				// NIET 0 !!!!!
		assertEquals(nt.getStatusAsString(),"Unavailable");
		assertTrue(taskManager.setTaskExecuting(project0, t01, task01StartDateGood));
		assertTrue(taskManager.setTaskFinished(project0, t01, task01EndDateGood));
		
//		assertEquals(nt.getStatusAsString(),"Available");
	}

	@Test
	public void flow3aTest() {
		// De UI zal geen request doorsturen als de user geen volledig formulier invult.
		List<ProjectView> projects = taskManager.getProjects();
		assertEquals(projects.size(),1);
		ProjectView project0 = projects.get(0);
		List<TaskView> p0tasks = project0.getTasks();
		assertEquals(p0tasks.size(),0);
	}

	@Test
	public void flow4aUnknownAltTest() {
		List<ProjectView> projects = taskManager.getProjects();
		assertTrue(projects.size() == 1);
		ProjectView project0 = projects.get(0);
		
		// Onbestaande task kan geen ALT nemen
		Task unexistent = new Task("Very bad", 50, 10, new ResourceManager(), new ArrayList<Task>(),new HashMap<ResourceView,Integer>(), null);

		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies,new HashMap<ResourceView,Integer>(), new TaskView(unexistent)));
		// Step 4
		List<TaskView> p0tasks = project0.getTasks();
		assertEquals(p0tasks.size(),0);

	}

	@Test
	public void flow4aBadAltTest() {
		List<ProjectView> projects = taskManager.getProjects();
		assertTrue(projects.size() == 1);
		ProjectView project0 = projects.get(0);
		
		// Er is al een AVAILABLE en UNAVAILABLE task aanwezig in het project
		assertTrue(taskManager.createTask(project0, "Design system", task00EstDur, task00Dev, task00Dependencies,noReq, null));
		List<TaskView> p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 1);
		TaskView t00 = p0tasks.get(0);
		taskManager.planTask(project0, t00, task00StartDateGood, task00ConcreteResources,devList1);
		assertEquals(t00.getStatusAsString(),"Available");
		task01Dependencies.add(t00);
		assertTrue(taskManager.createTask(project0, "Implement Native", task01EstDur, task01Dev, task01Dependencies,reqResTask00, null));
		p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 2);
		TaskView t01 = p0tasks.get(1);
		assertEquals(t01.getStatusAsString(),"Unavailable");
		
		// AVAILABLE task kan geen ALT krijgen
		
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies,new HashMap<ResourceView,Integer>(), t00));
		// Step 4
		p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 2);
		
		//----------------------------------------------------------------------------------------
		// UNAVAILABLE task kan geen ALT krijgen
		
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies,new HashMap<ResourceView,Integer>(), t01));
		// Step 4
		p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 2);
		
		//----------------------------------------------------------------------------------------
		// FINISHED task kan geen ALT krijgen
		
		assertTrue(taskManager.setTaskExecuting(project0, t00, task00StartDateGood));
		assertTrue(taskManager.setTaskFinished(project0, t00,altTaskEndDate));
		
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies, new HashMap<ResourceView,Integer>(), t00));
		// Step 4
		p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 2);

	}
	
	@Test
	public void flow4aDoubleAltTest() {
		List<ProjectView> projects = taskManager.getProjects();
		assertTrue(projects.size() == 1);
		ProjectView project0 = projects.get(0);

		assertTrue(taskManager.createTask(project0, "Design system", task00EstDur, task00Dev, task00Dependencies, noReq, null));
		List<TaskView> p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 1);
		TaskView t00 = p0tasks.get(0);
		assertTrue(taskManager.planTask(project0, t00, task00StartDateGood, task00ConcreteResources,devList1));
		assertTrue(taskManager.setTaskExecuting(project0, t00, task00StartDateGood));
		assertTrue(taskManager.setTaskFailed(project0, t00, task00EndDateGood));
		assertTrue(taskManager.createTask(project0, "Implement Native", task01EstDur, task01Dev, task01Dependencies, noReq, t00));
		p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 2);
		TaskView t01 = p0tasks.get(1);
		taskManager.planTask(project0, t01, task01StartDateGood, task01ConcreteResources,devList1);
		assertEquals(t01.getStatusAsString(),"Available");
		
		// Geen twee ALT tasks voor een failed task mogelijk
		
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies, reqResTask00, t00));
		p0tasks = project0.getTasks();
		// Step 4
		assertTrue(p0tasks.size() == 2);
	}

	@Test
	public void flow4aUnknownDepTest() {
		List<ProjectView> projects = taskManager.getProjects();
		assertTrue(projects.size() == 1);
		ProjectView project0 = projects.get(0);
		
		List<TaskView> p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 0);
		
		// Onbestaande task kan geen ALT nemen
		Task unexistent = new Task("Very bad", 50, 10, new ResourceManager(), new ArrayList<Task>(), noReq, null);

		newTaskDependencies.add(new TaskView(unexistent));
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies, reqResTask00, null));
		// Step 4
		p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 0);
		
	}
	
	@Test
	public void flow4aBadDepTest() {
		List<ProjectView> projects = taskManager.getProjects();
		assertTrue(projects.size() == 1);
		ProjectView project0 = projects.get(0);

		// Er is al een FAILED task aanwezig in het project
		assertTrue(taskManager.createTask(project0, "Design system", task00EstDur, task00Dev, task00Dependencies, noReq, null));
		List<TaskView> p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 1);
		TaskView t00 = p0tasks.get(0);
		assertTrue(taskManager.planTask(project0, t00, startDate,task00ConcreteResources,devList1));
		assertEquals(t00.getStatusAsString(),"Available");
		assertTrue(taskManager.setTaskExecuting(project0, t00, startDate));
		assertTrue(taskManager.setTaskFailed(project0, t00, altTaskEndDate));
		assertEquals(t00.getStatusAsString(),"Failed");
		
		// Task kan geen PreReq zijn voor zijn ALT

		// Step 1 and 2 are implicit
		// Step 3
		newTaskDependencies.add(t00);
		assertFalse(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies,reqResTask00, t00));
		// Step 4
		assertTrue(p0tasks.size() == 1);
	}
	
	@Test
	public void flow4aUnknownProjectTest() {
		Project unexistent = new Project("Very bad", "Very bad project", startDate, project0DueDate);
		List<ProjectView> projects = taskManager.getProjects();
		assertTrue(projects.size() == 1);
		ProjectView project0 = projects.get(0);

		assertTrue(taskManager.createTask(project0, "Design system", task00EstDur, task00Dev, task00Dependencies, noReq, null));
		List<TaskView> p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 1);
		TaskView t00 = p0tasks.get(0);
		assertTrue(taskManager.planTask(project0, t00, startDate,task00ConcreteResources,devList1));
		assertEquals(t00.getStatusAsString(),"Available");

		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskManager.createTask(new ProjectView(unexistent), "A new TASK", newTaskDur, newTaskDev, newTaskDependencies,reqResTask00, null));
		// Step 4
		assertEquals(taskManager.getProjects().size(),1);
		p0tasks = project0.getTasks();
		assertEquals(p0tasks.size(),1);
		
		//-------------------------------------------------------------------------------------------------

		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskManager.createTask(new ProjectView(unexistent), "A new TASK", newTaskDur, newTaskDev, newTaskDependencies, reqResTask00, t00));
		// Step 4
		assertEquals(taskManager.getProjects().size(),1);
		p0tasks = project0.getTasks();
		assertEquals(p0tasks.size(),1);
		
		//-------------------------------------------------------------------------------------------------

		// Step 1 and 2 are implicit
		// Step 3
		newTaskDependencies.add(t00);
		assertFalse(taskManager.createTask(new ProjectView(unexistent), "A new TASK", newTaskDur, newTaskDev, newTaskDependencies, reqResTask00, null));
		// Step 4
		assertEquals(taskManager.getProjects().size(),1);
		p0tasks = project0.getTasks();
		assertEquals(p0tasks.size(),1);
		
	}

	@Test
	public void SuccessCaseFinishedProjectTest() {
		List<ProjectView> projects = taskManager.getProjects();
		assertTrue(projects.size() == 1);
		ProjectView project0 = projects.get(0);
		
		assertTrue(taskManager.createTask(project0, "Design system", task00EstDur, task00Dev, task00Dependencies, noReq, null));
		List<TaskView> p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 1);
		TaskView t00 = p0tasks.get(0);
		assertTrue(taskManager.planTask(project0, t00, task00StartDateGood, task00ConcreteResources,devList1));
		assertEquals(t00.getStatusAsString(),"Available");
		assertTrue(taskManager.setTaskExecuting(project0, t00, task00StartDateGood));
		assertTrue(taskManager.setTaskFinished(project0, t00, task00EndDateGood));
		assertTrue(project0.isFinished());
		
		// Step 1 and 2 are implicit
		// Step 3
		assertTrue(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies, reqResTask00, null));
		// Step 4
		p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 2);
		assertFalse(project0.isFinished());
		
	}
	
	public boolean taskViewListContains(List<TaskView> list, TaskView task){
		for (TaskView t : list){
			if (t.equals(task)){
				return true;
			}
		}
		return false;
	}

}
