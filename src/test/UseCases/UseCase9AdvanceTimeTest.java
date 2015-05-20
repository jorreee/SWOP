package test.UseCases;

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

import company.BranchManager;
import company.taskMan.ProjectView;
import company.taskMan.project.TaskView;
import company.taskMan.resource.ResourceView;
import userInterface.IFacade;
import userInterface.TaskManException;

public class UseCase9AdvanceTimeTest {

	private IFacade branchManager;
	private final LocalDateTime startDate = LocalDateTime.of(2015, 2, 9, 8, 0),
			newDateNoChanges = LocalDateTime.of(2015, 2, 9, 10, 0),
			newDateWithChanges = LocalDateTime.of(2015, 3, 9, 10, 0),
			newDateVeryBad1 = null,
			newDateVeryBad2 = LocalDateTime.of(2015, 2, 8, 0, 0),
			newDateVeryBad3 = startDate,
			project0DueDate = LocalDateTime.of(2015, 2, 13, 23, 59);
	private final int task00EstDur = 8*60,
			task01EstDur = 16*60,
			task02EstDur = 8*60,
			task03EstDur = 8*60;
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
	private final Optional<LocalTime> emptyAvailabilityPeriodStart = Optional.empty(),
			emptyAvailabilityPeriodEnd = Optional.empty();

	/**
	 * - project 0 START 9 feb 8u DUE 13 feb midnight
	 * 		task 0			
	 * 		task 1 <- 0
	 * 		task 2 <- 1
	 * 		task 3 <- 1
	 */
	@Before
	public final void initialize() {
		branchManager = new BranchManager(startDate);
		
		branchManager.createResourcePrototype("car", emptyAvailabilityPeriodStart, emptyAvailabilityPeriodEnd);
		branchManager.createResourcePrototype("whiteboard", emptyAvailabilityPeriodStart, emptyAvailabilityPeriodEnd);
		branchManager.initializeBranch("Leuven");
		
		//create resources
		
		for(int i = 0;i<=6;i++){
			branchManager.declareConcreteResource("car" + i, branchManager.getResourcePrototypes().get(0));
		}
		
		for(int i = 0;i<=6;i++){
			branchManager.declareConcreteResource("whiteboard" + i, branchManager.getResourcePrototypes().get(1));
		}
		
		//assign resources to Hashsets for later use
		task00Res.put(branchManager.getResourcePrototypes().get(0), 1);
		task00Res.put(branchManager.getResourcePrototypes().get(1), 1);
		task01Res.put(branchManager.getResourcePrototypes().get(0), 2);
		task01Res.put(branchManager.getResourcePrototypes().get(1), 1);
		task02Res.put(branchManager.getResourcePrototypes().get(0), 1);
		task02Res.put(branchManager.getResourcePrototypes().get(1), 1);
		task03Res.put(branchManager.getResourcePrototypes().get(0), 2);
		task03Res.put(branchManager.getResourcePrototypes().get(1), 2);
		
		//Get a list of concrete resources to plan task00
		List<ResourceView> task00concRes = new ArrayList<>();
		task00concRes.add(branchManager.getConcreteResourcesForPrototype(
				branchManager.getResourcePrototypes().get(0)
				).get(0));
		task00concRes.add(branchManager.getConcreteResourcesForPrototype(
				branchManager.getResourcePrototypes().get(1)
				).get(0));
		List<ResourceView> devList1 = new ArrayList<>();
		branchManager.createDeveloper("Patrick Star");
		devList1.add(branchManager.getDeveloperList().get(0));

		branchManager.createProject("Test1", "testing 1", project0DueDate);
		ProjectView project0 = branchManager.getProjects().get(0);

		branchManager.createTask(project0, "Design system", task00EstDur, task00Dev, task00Dependencies, task00Res, null);		// TASK 1
		TaskView task00 = project0.getTasks().get(0);
		branchManager.planTask(project0, task00, branchManager.getCurrentTime(), task00concRes, devList1);
		task01Dependencies.add(task00);
		branchManager.createTask(project0, "Implement Native", task01EstDur, task01Dev, task01Dependencies, task01Res, null);	// TASK 2
		task02Dependencies.add(project0.getTasks().get(1));
		branchManager.createTask(project0, "Test code", task02EstDur, task02Dev, task02Dependencies, task02Res, null);			// TASK 3
		task03Dependencies.add(project0.getTasks().get(1));
		branchManager.createTask(project0, "Document code", task03EstDur, task03Dev, task03Dependencies, task03Res, null);		// TASK 4

	}

	@Test
	public void SuccesCaseNoChangesTest() {
		ProjectView project0 = branchManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);
		TaskView task03 = project0.getTasks().get(3);
		// Step 1 and 2 are implicit0
		// Step 3 assumption: the user inputs CORRECT data
		branchManager.advanceTimeTo(newDateNoChanges);
		assertEquals(branchManager.getCurrentTime(),newDateNoChanges);
		// Step 4
		assertTrue(task00.getStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task03.getStatusAsString().equalsIgnoreCase("unavailable"));
	}

	@Test
	public void SuccesCaseWithChangesTest() {
		ProjectView project0 = branchManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);
		TaskView task03 = project0.getTasks().get(3);
		// Step 1 and 2 are implicit
		// Step 3 assumption: the user inputs CORRECT data
		branchManager.advanceTimeTo(newDateWithChanges);
		assertEquals(branchManager.getCurrentTime(),newDateWithChanges);
		// Step 4
		assertTrue(task00.getStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task03.getStatusAsString().equalsIgnoreCase("unavailable"));
	}

	@Test(expected = TaskManException.class )
	public void flow3aTest() {
		ProjectView project0 = branchManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);
		TaskView task03 = project0.getTasks().get(3);
		// Step 1 and 2 are implicit
		// Step 3 assumption: the user inputs NO data
		branchManager.advanceTimeTo(newDateVeryBad1);
		assertEquals(branchManager.getCurrentTime(),startDate);
		// Step 4
		assertTrue(task00.getStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task03.getStatusAsString().equalsIgnoreCase("unavailable"));
	}

	@Test(expected = TaskManException.class )
	public void flow4aTest() {
		ProjectView project0 = branchManager.getProjects().get(0);
		TaskView task00 = project0.getTasks().get(0);
		TaskView task01 = project0.getTasks().get(1);
		TaskView task02 = project0.getTasks().get(2);
		TaskView task03 = project0.getTasks().get(3);
		// Step 1 and 2 are implicit
		// Step 3 assumption: the user inputs INVALID data
		branchManager.advanceTimeTo(newDateVeryBad2);
		assertEquals(branchManager.getCurrentTime(),startDate);
		// Step 4
		assertTrue(task00.getStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task03.getStatusAsString().equalsIgnoreCase("unavailable"));

		//-----------------------------------------------------------

		branchManager.advanceTimeTo(newDateVeryBad3);
		assertEquals(branchManager.getCurrentTime(),startDate);
		// Step 4
		assertTrue(task00.getStatusAsString().equalsIgnoreCase("available"));
		assertTrue(task01.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task02.getStatusAsString().equalsIgnoreCase("unavailable"));
		assertTrue(task03.getStatusAsString().equalsIgnoreCase("unavailable"));
	}

}
