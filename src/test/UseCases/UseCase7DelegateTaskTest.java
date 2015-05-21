package test.UseCases;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;

import userInterface.IFacade;
import company.BranchManager;
import company.taskMan.ProjectView;
import company.taskMan.project.TaskView;
import company.taskMan.resource.ResourceView;

public class UseCase7DelegateTaskTest {
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

}
