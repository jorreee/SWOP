package userInterface;

import initialization.ConcreteResourceCreationData;
import initialization.DeveloperCreationData;
import initialization.IntPair;
import initialization.PlanningCreationData;
import initialization.ProjectCreationData;
import initialization.ReservationCreationData;
import initialization.ResourcePrototypeCreationData;
import initialization.TaskCreationData;
import initialization.TaskManInitFileChecker;
import initialization.TaskStatus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import userInterface.requests.ChangeUserRequest;
import userInterface.requests.Request;

import company.BranchManager;
import company.BranchView;
import company.taskMan.ProjectView;
import company.taskMan.project.TaskView;
import company.taskMan.resource.ResourceView;
/**
 * Main class of the User Interface of the project TaskMan.
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and Eli Vangrieken
 */
public class Main {

	private static boolean initSuccess = true;
	private static List<Delegation> delegations = new ArrayList<>();


	public static void main(String[] args) throws IOException {
		System.out.println("~~~~~~~~~~~~~~~ TASKMAN ~~~~~~~~~~~~~~~");		

		IFacade facade;
		if(args.length < 1) {
			facade = new BranchManager(LocalDateTime.now());
		} else {
			facade = initializeFromFile(new File(args[0]));
		}
		
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

		// Start accepting user input
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		InputParser inParser = new InputParser(facade, input);
		Request request;
		while(true) {
			// Display System status
			if(facade.isLoggedIn()) {
				System.out.println("Current System time: " + facade.getCurrentTime().format(dateTimeFormatter) +
						", logged in as: " + facade.getCurrentUser().getName());
				// Ask user for input
				System.out.println("TaskMan instruction? (h for help)");
				// Parse user input
				request = inParser.parse(input.readLine());
			} else {
				request = new ChangeUserRequest(facade,input);
			}
			// Execute request
			String response = request.execute();

			// Display the response of the previous request
			System.out.println(response);

		} // Repeat
	}

	private static IFacade initializeFromFile(File file) throws FileNotFoundException {

		TaskManInitFileChecker fileChecker;
		// Initialize system from file
		fileChecker = new TaskManInitFileChecker(new FileReader(file));
		fileChecker.checkSystemFile();
		
		LocalDateTime systemTime = fileChecker.getSystemTime();
		List<ResourcePrototypeCreationData> resourcePrototypes = fileChecker.getResourcePrototypeDataList();

		// Get facade
		IFacade facade = new BranchManager(systemTime);
		
		// Init resource prototypes
		List<ResourceView> resourceProts = new ArrayList<>();
		boolean success = true;
		for(ResourcePrototypeCreationData rprot : resourcePrototypes) {
			try{
				facade.createResourcePrototype(
					rprot.getName(),
					fileChecker.getDailyAvailabilityStartByIndex(rprot.getAvailabilityIndex()),
					fileChecker.getDailyAvailabilityEndByIndex(rprot.getAvailabilityIndex()));
			} catch(Exception e) { e.printStackTrace(); failInit("creating a resource prototype!"); }
			List<ResourceView> currentExistingProts = facade.getResourcePrototypes();
			ResourceView currentProt = currentExistingProts.get(facade.getResourcePrototypes().size() -1); 
			resourceProts.add(currentProt);
			List<ResourceView> requirements = new ArrayList<>();
			for(Integer index : rprot.getRequirements()) {
				requirements.add(currentExistingProts.get(index));
			}
			List<ResourceView> conflicts = new ArrayList<>();
			for(Integer index : rprot.getConflicts()) {
				conflicts.add(currentExistingProts.get(index));
			}
			try {
				facade.addRequirementsToResource(requirements, currentProt);
			} catch(Exception e) { e.printStackTrace(); failInit("adding requirements to a prototype!"); }
			try {
				facade.addConflictsToResource(conflicts, currentProt);
			} catch(Exception e) { e.printStackTrace(); failInit("adding conflicts to a prototype!"); }
		}
		
		if(!success) {
			System.out.println("Initialization from tman failed, check your file!");
			return new BranchManager(LocalDateTime.now());
		}
		
		// Get branch init files
		Queue<String> branches = fileChecker.getBranches();
		
		int branchID = 0;
		String branch = branches.poll();
		while(branch != null) {
			FileReader branchReader = new FileReader(file.getParent() + File.separator + branch);
			
			// Initialize branches from file
			fileChecker = new TaskManInitFileChecker(branchReader);
			fileChecker.checkFile();
	
			success = initializeBranch(facade, fileChecker, branchID);
			branch = branches.poll();
			if(!success) {
				System.out.println("Initialization from tman failed, check your file!");
				return new BranchManager(LocalDateTime.now());
			}
			
			branchID++;
		}
		
		facade.logout();
		
		// Delegate tasks to their responsible branches
		List<BranchView> branchViews = facade.getBranches();
		for(Delegation delegation : delegations) {
			facade.delegateTask(delegation.project, delegation.task, branchViews.get(delegation.oldBranch), branchViews.get(delegation.newBranch));
		}
		
		return facade;
	}

	public static boolean initializeBranch(IFacade facade, TaskManInitFileChecker fileChecker, int branchID) {
		try {
			String geographicLocation = fileChecker.getGeographicLocation();
			facade.initializeBranch(geographicLocation);
			
			List<ProjectCreationData> projectData = fileChecker.getProjectDataList();
			List<TaskCreationData> taskData = fileChecker.getTaskDataList();
			List<ConcreteResourceCreationData> concreteResources = fileChecker.getConcreteResourceDataList();
			List<DeveloperCreationData> developers = fileChecker.getDeveloperDataList();
			List<ReservationCreationData> reservations = fileChecker.getReservationDataList();
			List<ResourceView> resourceProts = facade.getResourcePrototypes();
			
			// Initialize system through a facade

			// Init concrete resources
			List<ResourceView> allConcreteResources = new ArrayList<>();
			for(ConcreteResourceCreationData cres : concreteResources) {
				try {
					facade.declareConcreteResource(cres.getName(), resourceProts.get(cres.getTypeIndex()));
				} catch(Exception e) { e.printStackTrace(); failInit("creating a concrete resource!"); }
				List<ResourceView> specificResources = facade.getConcreteResourcesForPrototype(resourceProts.get(cres.getTypeIndex()));
				allConcreteResources.add(specificResources.get(specificResources.size() - 1));
			}
			// --------------------------------
			// Init developers
			for(DeveloperCreationData dev : developers) {
				try {
					facade.createDeveloper(dev.getName());
				} catch(Exception e) { e.printStackTrace(); failInit("creating developer" + dev.getName() + "!"); }
			}

			// --------------------------------

			// Init projects
			for(ProjectCreationData pcd : projectData) {
				try {
					facade.createProject(pcd.getName(), pcd.getDescription(), pcd.getCreationTime(), pcd.getDueTime());
				} catch(Exception e) { e.printStackTrace(); failInit("creating project" + pcd.getName() + "!"); }
			}
			// Init tasks (planned and unplanned)
			List<TaskView> creationList = new ArrayList<>();
			for(TaskCreationData tcd : taskData) {
				TaskStatus status = tcd.getStatus();
				String statusString = null;
				if(status != null)
					statusString = status.name();

				ProjectView project = facade.getProjects().get(tcd.getProject());
				List<TaskView> prerequisiteTasks = new ArrayList<>();
				for(Integer i : tcd.getPrerequisiteTasks()) {
					prerequisiteTasks.add(creationList.get(i));
				}
				TaskView taskAlternative = null;
				if(tcd.getAlternativeFor() != -1) {
					taskAlternative = creationList.get(tcd.getAlternativeFor());
				}
				Map<ResourceView, Integer> requiredResources = new HashMap<>();
				List<ResourceView> resources = facade.getResourcePrototypes();
				for(IntPair intPair : tcd.getRequiredResources()) {
					requiredResources.put(resources.get(intPair.first), intPair.second);
				}

				PlanningCreationData planning = tcd.getPlanningData();
				try {
				if(planning != null) {

					List<ResourceView> devs = facade.getDeveloperList();
					List<ResourceView> plannedDevelopers = new ArrayList<>();
					for(Integer integer : planning.getDevelopers()) {
						plannedDevelopers.add(devs.get(integer));
					}

					facade.createTask(
							project, 
							tcd.getDescription(),
							tcd.getEstimatedDuration(),
							tcd.getAcceptableDeviation(), 
							prerequisiteTasks,
							taskAlternative, 
							requiredResources,
							statusString, 
							tcd.getStartTime(), 
							tcd.getEndTime(),
							planning.getPlannedStartTime(),
							plannedDevelopers);
				} else {
					facade.createTask(
							project, 
							tcd.getDescription(),
							tcd.getEstimatedDuration(),
							tcd.getAcceptableDeviation(), 
							prerequisiteTasks,
							taskAlternative, 
							requiredResources,
							statusString, 
							tcd.getStartTime(), 
							tcd.getEndTime(),
							null,
							null);

				}
				
				List<TaskView> tasks = project.getTasks();
				creationList.add(tasks.get(tasks.size() - 1));
				
				if(tcd.getResponsibleBranch() != null) {
					delegations.add(new Delegation(project, tasks.get(tasks.size() - 1), branchID, tcd.getResponsibleBranch()));
				}
				} catch(Exception e) { e.printStackTrace(); failInit("creating task: " + tcd.getDescription() + ", in project " + tcd.getProject() + "!"); }


			}
			// Init reservations
			for(ReservationCreationData rcd : reservations) {

				ProjectView project = facade.getProjects().get(taskData.get(rcd.getTask()).getProject());
				TaskView task = creationList.get(rcd.getTask());

				try {
					facade.reserveResource(
						allConcreteResources.get(rcd.getResource()), 
						project,
						task,
						rcd.getStartTime(),
						rcd.getEndTime());
				} catch(Exception e) { e.printStackTrace(); failInit("trying to reserve resource " + rcd.getResource() + " for task " + rcd.getTask() + "!"); }
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			failInit("an exception was thrown!");
		}
		// End initialization
		return initSuccess;
	}

	private static void failInit(String issue) {
		System.out.println("Initialization failed when " + issue);
		initSuccess = false;
	}

}

class Delegation {
	protected final ProjectView project;
	protected final TaskView task;
	protected final Integer oldBranch;
	protected final Integer newBranch;
	
	protected Delegation(ProjectView project, TaskView task, Integer oldBranch, Integer newBranch) {
		this.project = project;
		this.task = task;
		this.oldBranch = oldBranch;
		this.newBranch = newBranch;
	}
}