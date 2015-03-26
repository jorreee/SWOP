package taskMan.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import taskMan.Facade;
import taskMan.Project;
import taskMan.Task;
import taskMan.util.TimeSpan;
import taskMan.view.ProjectView;
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
			task01EndDateGood = LocalDateTime.of(2015, 2, 10, 8, 0);
	private final int task00EstDur = 8*60,
			task01EstDur = 16*60,
			newTaskDur = 5*60;
	private final int task00Dev = 0,
			task01Dev = 50,
			newTaskDev = 10;
	private final ArrayList<TaskView> task00Dependencies = new ArrayList<TaskView>(),
			 						 task01Dependencies = new ArrayList<TaskView>(),
									 newTaskDependencies = new ArrayList<TaskView>();

	/**
	 * - project 1 START 9 feb 8u DUE 13 feb midnight
	 * 		GEEN tasks aanwezig
	 */
	@Before
	public final void initialize() {
		taskManager = new Facade(startDate);

		assertTrue(taskManager.createProject("Test1", "testing 1", project0DueDate));

		assertTrue(taskManager.advanceTimeTo(workDate));

	}

	@Test
	public void SuccesCaseTest() {
		// Step 1 and 2 are implicit
		// Step 3
		List<ProjectView> projects = taskManager.getProjects();
		assertTrue(projects.size() == 1);
		ProjectView project0 = projects.get(0);
		assertTrue(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies, null));
		// Step 4
		List<TaskView> p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 1);
		TaskView t = p0tasks.get(0);
		
		assertTrue(t.getTaskDescription().equals("A new TASK"));
		assertEquals(t.getEstimatedTaskDuration(), new TimeSpan(newTaskDur));
		assertEquals(t.getAcceptableTaskDeviation(),newTaskDev);
		assertFalse(t.isTaskAlternative());
		assertFalse(t.hasTaskPrerequisites());
		assertEquals(project0.getAvailableTasks().size(),1);
	}

	@Test
	public void SuccesCaseALTTest() {

		// Er is al een FAILED task aanwezig in het project
		List<ProjectView> projects = taskManager.getProjects();
		assertTrue(projects.size() == 1);
		ProjectView project0 = projects.get(0);
		assertTrue(taskManager.createTask(project0, "Design system", task00EstDur, task00Dev, task00Dependencies, null));
		
		List<TaskView> p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 1);
		TaskView t00 = p0tasks.get(0);
		assertTrue(taskManager.setTaskFailed(project0, t00, startDate, altTaskEndDate));
		
		
		// Step 1 and 2 are implicit
		// Step 3
		assertTrue(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies, t00));
		// Step 4
		p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 2);
		TaskView t01 = p0tasks.get(1);
		
		assertTrue(t01.getTaskDescription().equals("A new TASK"));
		assertEquals(t01.getEstimatedTaskDuration(), new TimeSpan(newTaskDur));
		assertEquals(t01.getAcceptableTaskDeviation(),newTaskDev);
		assertTrue(t01.isTaskAlternative());
		assertEquals(t01.getTaskAlternativeTo(),t00);					//
		assertFalse(t01.hasTaskPrerequisites());
		assertTrue(t01.getTaskStatusAsString().equals("Available"));
		assertEquals(project0.getAvailableTasks().size(),1);
		
	}

	@Test
	public void SuccesCaseDepTest() {

		// Er is al een task aanwezig in het project
		List<ProjectView> projects = taskManager.getProjects();
		assertTrue(projects.size() == 1);
		ProjectView project0 = projects.get(0);
		assertTrue(taskManager.createTask(project0, "Design system", task00EstDur, task00Dev, task00Dependencies, null));
		
		List<TaskView> p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 1);
		TaskView t00 = p0tasks.get(0);
		assertTrue(t00.getTaskStatusAsString().equals("Available"));

		// Step 1 and 2 are implicit
		// Step 3
		newTaskDependencies.add(t00);
		assertTrue(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies, null));
		// Step 4
		p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 2);
		TaskView t01 = p0tasks.get(1);
		
		assertTrue(t01.getTaskDescription().equals("A new TASK"));
		assertEquals(t01.getEstimatedTaskDuration(), new TimeSpan(newTaskDur));
		assertEquals(t01.getAcceptableTaskDeviation(),newTaskDev);
		assertFalse(t01.isTaskAlternative());
		assertTrue(t01.hasTaskPrerequisites());
		assertFalse(t01.isTaskAlternative());
		assertTrue(t01.hasTaskPrerequisites());
		assertTrue(t01.getTaskPrerequisites().contains(t00));				//
		assertTrue(t01.getTaskStatusAsString().equals("Unavailable"));
		assertEquals(project0.getAvailableTasks().size(),1);
		
	}
	
	@Test
	public void SuccesCaseFailedDepNoAltTest() {

		// Er is al een FAILED task aanwezig in het project
		List<ProjectView> projects = taskManager.getProjects();
		assertTrue(projects.size() == 1);
		ProjectView project0 = projects.get(0);
		assertTrue(taskManager.createTask(project0, "Design system", task00EstDur, task00Dev, task00Dependencies, null));

		List<TaskView> p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 1);
		TaskView t00 = p0tasks.get(0);
		assertEquals(t00.getTaskStatusAsString(),"Available");
		assertTrue(taskManager.setTaskFailed(project0, t00, startDate, altTaskEndDate));
		assertEquals(t00.getTaskStatusAsString(),"Failed");

		// Step 1 and 2 are implicit
		// Step 3
		newTaskDependencies.add(t00);
		assertTrue(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies, null));
		// Step 4
		p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 2);
		TaskView t01 = p0tasks.get(1);
		
		assertTrue(t01.getTaskDescription().equals("A new TASK"));
		assertEquals(t01.getEstimatedTaskDuration(), new TimeSpan(newTaskDur));
		assertEquals(t01.getAcceptableTaskDeviation(), newTaskDev);
		assertFalse(t01.isTaskAlternative());
		assertTrue(t01.hasTaskPrerequisites());
		assertTrue(t01.getTaskStatusAsString().equals("Unavailable"));
		assertEquals(project0.getAvailableTasks().size(),0);
	}
	
	@Test
	public void SuccesCaseFailedDepWithAltTest() {

		// Er is al een FAILED task MET ALT aanwezig in het project
		List<ProjectView> projects = taskManager.getProjects();
		assertTrue(projects.size() == 1);
		ProjectView project0 = projects.get(0);
		
		assertTrue(taskManager.createTask(project0, "Design system", task00EstDur, task00Dev, task00Dependencies, null));
		List<TaskView> p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 1);
		TaskView t00 = p0tasks.get(0);
		
		assertEquals(t00.getTaskStatusAsString(),"Available");
		assertTrue(taskManager.setTaskFailed(project0, t00, startDate, task00EndDateGood));
		assertEquals(t00.getTaskStatusAsString(),"Failed");

		assertTrue(taskManager.createTask(project0, "Implement native", task01EstDur, task01Dev, task01Dependencies, t00));
		p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 2);
		TaskView t01 = p0tasks.get(1);
		assertEquals(t01.getTaskStatusAsString(),"Available");

		// Step 1 and 2 are implicit
		// Step 3
		newTaskDependencies.add(t00);
		assertTrue(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies, null));
		// Step 4
		p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 3);
		TaskView nt = p0tasks.get(2);
		
		assertTrue(nt.hasTaskPrerequisites());
		assertTrue(nt.getTaskPrerequisites().contains(t00));
		assertFalse(nt.getTaskPrerequisites().contains(t01));				// NIET 0 !!!!!
		assertEquals(nt.getTaskStatusAsString(),"Unavailable");
		assertTrue(taskManager.setTaskFinished(project0, t01, task01StartDateGood, task01EndDateGood));
		
		assertEquals(nt.getTaskStatusAsString(),"Available");
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
		
		// Kan zichzelf niet als ALT nemen
		
		// Step 1 and 2 are implicit
		// Step 3
//		assertFalse(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies, )); // TODO hoe testen?
		// Step 4
		List<TaskView> p0tasks = project0.getTasks();
		assertEquals(p0tasks.size(),0);
		
		//--------------------------------------------------------------------------------------
		// Onbestaande task kan geen ALT nemen
		Task unexistent = new Task(10, "Very bad", 50, 10, new ArrayList<Task>(), null);
		
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies, new TaskView(unexistent)));
		// Step 4
		p0tasks = project0.getTasks();
		assertEquals(p0tasks.size(),0);
		
	}

	@Test
	public void flow4aBadAltTest() {
		List<ProjectView> projects = taskManager.getProjects();
		assertTrue(projects.size() == 1);
		ProjectView project0 = projects.get(0);
		
		// Er is al een AVAILABLE en UNAVAILABLE task aanwezig in het project
		assertTrue(taskManager.createTask(project0, "Design system", task00EstDur, task00Dev, task00Dependencies, null));
		List<TaskView> p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 1);
		TaskView t00 = p0tasks.get(0);
		assertEquals(t00.getTaskStatusAsString(),"Available");
		task01Dependencies.add(t00);
		assertTrue(taskManager.createTask(project0, "Implement Native", task01EstDur, task01Dev, task01Dependencies, null));
		p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 2);
		TaskView t01 = p0tasks.get(1);
		assertEquals(t01.getTaskStatusAsString(),"Unavailable");
		
		// AVAILABLE task kan geen ALT krijgen
		
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies, t00));
		// Step 4
		p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 2);
		
		//----------------------------------------------------------------------------------------
		// UNAVAILABLE task kan geen ALT krijgen
		
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies, t01));
		// Step 4
		p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 2);
		
		//----------------------------------------------------------------------------------------
		// FINISHED task kan geen ALT krijgen
		
		assertTrue(taskManager.setTaskFinished(project0, t00, startDate, altTaskEndDate));
		
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies, t00));
		// Step 4
		p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 2);

	}
	
	@Test
	public void flow4aDoubleAltTest() {
		List<ProjectView> projects = taskManager.getProjects();
		assertTrue(projects.size() == 1);
		ProjectView project0 = projects.get(0);

		assertTrue(taskManager.createTask(project0, "Design system", task00EstDur, task00Dev, task00Dependencies, null));
		List<TaskView> p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 1);
		TaskView t00 = p0tasks.get(0);
		assertTrue(taskManager.setTaskFailed(project0, t00, task00StartDateGood, task00EndDateGood));
		assertTrue(taskManager.createTask(project0, "Implement Native", task01EstDur, task01Dev, task01Dependencies, t00));
		p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 2);
		TaskView t01 = p0tasks.get(1);
		assertEquals(t01.getTaskStatusAsString(),"Available");
		
		// Geen twee ALT tasks voor een failed task mogelijk
		
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies, t00));
		p0tasks = project0.getTasks();
		// Step 4
		assertTrue(p0tasks.size() == 2);
	}

	@Test
	public void flow4aUnknownDepTest() {
		List<ProjectView> projects = taskManager.getProjects();
		assertTrue(projects.size() == 1);
		ProjectView project0 = projects.get(0);
		
		// Kan zichzelf niet als DEP nemen
		
//		newTaskDependencies.add(); // TODO hoe testen?
		// Step 1 and 2 are implicit
		// Step 3
//		assertFalse(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies, null));
		// Step 4
		List<TaskView> p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 0);
		
		//--------------------------------------------------------------------------------------
		// Onbestaande task kan geen ALT nemen
		Task unexistent = new Task(10, "Very bad", 50, 10, new ArrayList<Task>(), null);

//		newTaskDependencies.remove(0);
		newTaskDependencies.add(new TaskView(unexistent));
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies, null));
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
		assertTrue(taskManager.createTask(project0, "Design system", task00EstDur, task00Dev, task00Dependencies, null));
		List<TaskView> p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 1);
		TaskView t00 = p0tasks.get(0);
		assertEquals(t00.getTaskStatusAsString(),"Available");
		assertTrue(taskManager.setTaskFailed(project0, t00, startDate, altTaskEndDate));
		assertEquals(t00.getTaskStatusAsString(),"Failed");
		
		// Task kan geen PreReq zijn voor zijn ALT

		// Step 1 and 2 are implicit
		// Step 3
		newTaskDependencies.add(t00);
		assertFalse(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies, t00));
		// Step 4
		assertTrue(p0tasks.size() == 1);
	}
	
	@Test
	public void flow4aUnknownProjectTest() {
		Project unexistent = new Project(1, "Very bad", "Very bad project", startDate, project0DueDate);
		List<ProjectView> projects = taskManager.getProjects();
		assertTrue(projects.size() == 1);
		ProjectView project0 = projects.get(0);

		assertTrue(taskManager.createTask(project0, "Design system", task00EstDur, task00Dev, task00Dependencies, null));
		List<TaskView> p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 1);
		TaskView t00 = p0tasks.get(0);
		assertEquals(t00.getTaskStatusAsString(),"Available");

		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskManager.createTask(new ProjectView(unexistent), "A new TASK", newTaskDur, newTaskDev, newTaskDependencies, null));
		// Step 4
		assertEquals(taskManager.getProjects().size(),1);
		p0tasks = project0.getTasks();
		assertEquals(p0tasks.size(),1);
		
		//-------------------------------------------------------------------------------------------------

		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskManager.createTask(new ProjectView(unexistent), "A new TASK", newTaskDur, newTaskDev, newTaskDependencies, t00));
		// Step 4
		assertEquals(taskManager.getProjects().size(),1);
		p0tasks = project0.getTasks();
		assertEquals(p0tasks.size(),1);
		
		//-------------------------------------------------------------------------------------------------

		// Step 1 and 2 are implicit
		// Step 3
		newTaskDependencies.add(t00);
		assertFalse(taskManager.createTask(new ProjectView(unexistent), "A new TASK", newTaskDur, newTaskDev, newTaskDependencies, null));
		// Step 4
		assertEquals(taskManager.getProjects().size(),1);
		p0tasks = project0.getTasks();
		assertEquals(p0tasks.size(),1);
		
	}

	@Test
	public void flow4aBadProjectTest() {
		List<ProjectView> projects = taskManager.getProjects();
		assertTrue(projects.size() == 1);
		ProjectView project0 = projects.get(0);
		
		assertTrue(taskManager.createTask(project0, "Design system", task00EstDur, task00Dev, task00Dependencies, null));
		List<TaskView> p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 1);
		TaskView t00 = p0tasks.get(0);
		assertEquals(t00.getTaskStatusAsString(),"Available");
		assertTrue(taskManager.setTaskFinished(project0, t00, task00StartDateGood, task00EndDateGood));
		assertTrue(project0.isProjectFinished());
		
		// Step 1 and 2 are implicit
		// Step 3
		assertFalse(taskManager.createTask(project0, "A new TASK", newTaskDur, newTaskDev, newTaskDependencies, null));
		// Step 4
		p0tasks = project0.getTasks();
		assertTrue(p0tasks.size() == 1);
		
	}

}
