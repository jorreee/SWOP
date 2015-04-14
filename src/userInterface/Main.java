package userInterface;

import initSaveRestore.initialization.ConcreteResourceCreationData;
import initSaveRestore.initialization.DeveloperCreationData;
import initSaveRestore.initialization.IntPair;
import initSaveRestore.initialization.PlanningCreationData;
import initSaveRestore.initialization.ProjectCreationData;
import initSaveRestore.initialization.ReservationCreationData;
import initSaveRestore.initialization.ResourcePrototypeCreationData;
import initSaveRestore.initialization.TaskCreationData;
import initSaveRestore.initialization.TaskManInitFileChecker;
import initSaveRestore.initialization.TaskStatus;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import taskMan.Facade;
import taskMan.view.ProjectView;
import taskMan.view.ResourceView;
import taskMan.view.TaskView;
import userInterface.requests.Request;
/**
 * Main class of the User Interface of the project TaskMan.
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and Eli Vangrieken
 */
public class Main {
		
	public static void main(String[] args) throws IOException {
		System.out.println("~~~~~~~~~~~~~~~ TASKMAN ~~~~~~~~~~~~~~~");		
//		if (args.length < 1) {
//			System.err.println("Error: First command line argument must be filename.");
//			return;
//		}
		
		IFacade facade;
		if(args.length < 1) {
			facade = new Facade(LocalDateTime.now());
		} else {
			facade = initialize(args[0]);
		}
		
		// Start accepting user input
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		InputParser inParser = new InputParser(facade, input);
		while(true) {
			// Display System status
			System.out.println("Current System time: " + facade.getCurrentTime().toString() +
					", logged in as: " + facade.getCurrentUsername());
			// Ask user for input
			System.out.println("TaskMan instruction? (h for help)");
			// Parse user input
			Request request = inParser.parse(input.readLine());
			
			// Execute request
			String response = request.execute();
			
			// Display the response of the previous request
			System.out.println(response);

		} // Repeat
	}
	
	private static IFacade initialize(String fileLocation) throws FileNotFoundException {

		// Initialize variables from file
		TaskManInitFileChecker fileChecker;
		
		fileChecker = new TaskManInitFileChecker(new FileReader(fileLocation));
		fileChecker.checkFile();
		
		LocalDateTime systemTime = fileChecker.getSystemTime();
		List<ProjectCreationData> projectData = fileChecker.getProjectDataList();
		List<TaskCreationData> taskData = fileChecker.getTaskDataList();
		List<ResourcePrototypeCreationData> resourcePrototypes = fileChecker.getResourcePrototypeDataList();
		List<ConcreteResourceCreationData> concreteResources = fileChecker.getConcreteResourceDataList();
		List<DeveloperCreationData> developers = fileChecker.getDeveloperDataList();
		List<ReservationCreationData> reservations = fileChecker.getReservationDataList();
		
		// Get facade
		IFacade facade = new Facade(systemTime);
		
		// Initialize system through a facade
			// Init daily availability
//		facade.declareAvailabilityPeriod(fileChecker.getDailyAvailabilityTime()[0],
//				fileChecker.getDailyAvailabilityTime()[1]);
			// Init resource prototypes
		
		for(ResourcePrototypeCreationData rprot : resourcePrototypes) {
//			facade.createResourcePrototype(rprot.getName(), rprot.getRequirements(), rprot.getConflicts(), rprot.getAvailabilityIndex());
			facade.createResourcePrototype( // TODO less raw maybe
					rprot.getName(), 
					rprot.getRequirements(),
					rprot.getConflicts(),
					fileChecker.getDailyAvailabilityStartByIndex(rprot.getAvailabilityIndex()),
					fileChecker.getDailyAvailabilityEndByIndex(rprot.getAvailabilityIndex()));
			//, rprot.getRequirements(), rprot.getConflicts(), rprot.getAvailabilityIndex());
		}
			// Init concrete resources
		for(ConcreteResourceCreationData cres : concreteResources) {
			facade.declareConcreteResource(cres.getName(), cres.getTypeIndex());
		}
		// -------------------------- TODO
			// Init developers
		for(DeveloperCreationData dev : developers) {
			facade.createDeveloper(dev.getName());
		}
		
			// Init current user
		facade.changeToUser(fileChecker.getCurrentUser());
		// --------------------------------
		
			// Init projects
		for(ProjectCreationData pcd : projectData) {
			facade.createProject(pcd.getName(), pcd.getDescription(), pcd.getCreationTime(), pcd.getDueTime());
		}
			// Init tasks (planned and unplanned)
		for(TaskCreationData tcd : taskData) {
			TaskStatus status = tcd.getStatus();
			String statusString = null;
			if(status != null)
				statusString = status.name();
			
			ProjectView project = facade.getProjects().get(tcd.getProject());
			List<TaskView> tasks = project.getTasks();
			List<TaskView> prerequisiteTasks = new ArrayList<>();
			for(Integer i : tcd.getPrerequisiteTasks()) {
				prerequisiteTasks.add(tasks.get(i));
			}
			TaskView taskAlternative = null;
			if(tcd.getAlternativeFor() != -1) {
				taskAlternative = tasks.get(tcd.getAlternativeFor());
			}
			Map<ResourceView, Integer> requiredResources = new HashMap<>();
			List<ResourceView> resources = facade.getResourcePrototypes();
			for(IntPair intPair : tcd.getRequiredResources()) {
				requiredResources.put(resources.get(intPair.first), intPair.second);
			}
			
			PlanningCreationData planning = tcd.getPlanningData();
			if(planning != null) {
				
				List<ResourceView> devs = facade.getDeveloperList();
				List<ResourceView> plannedDevelopers = new ArrayList<>();
				for(Integer integer : planning.getDevelopers()) {
					plannedDevelopers.add(devs.get(integer));
				}
				
				facade.createPlannedTask(
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
						tcd.getEndTime());
			}
		}
			// Init reservations
		
		List<ResourceView> allConcreteResources = facade.getAllConcreteResources();
		allConcreteResources.sort(new Comparator<ResourceView>() {
			@Override
			public int compare(ResourceView resource1, ResourceView resource2) {
				if(resource1.getCreationIndex() < resource2.getCreationIndex())
					return -1;
				else if(resource1.getCreationIndex() > resource2.getCreationIndex())
					return 1;
				else return 0;
			}
		});
		
		for(ReservationCreationData rcd : reservations) {
			
			ProjectView project = facade.getProjects().get(taskData.get(rcd.getTask()).getProject());
			TaskView task = project.getTasks().get(rcd.getTask());
			
			facade.reserveResource(
					allConcreteResources.get(rcd.getResource()), 
					project,
					task);
		}
		// End initialization
		
		return facade;
	}

}
