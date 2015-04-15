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
			// Init resource prototypes
		List<ResourceView> resourceProts = new ArrayList<>();
		for(ResourcePrototypeCreationData rprot : resourcePrototypes) {
			boolean success = facade.createResourcePrototype(
					rprot.getName(),
					fileChecker.getDailyAvailabilityStartByIndex(rprot.getAvailabilityIndex()),
					fileChecker.getDailyAvailabilityEndByIndex(rprot.getAvailabilityIndex()));
			if(success) {
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
				facade.addRequirementsToResource(requirements, currentProt);
				facade.addConflictsToResource(conflicts, currentProt);
			}
		}
			// Init concrete resources
		List<ResourceView> allConcreteResources = new ArrayList<>();
		for(ConcreteResourceCreationData cres : concreteResources) {
			boolean success = facade.declareConcreteResource(cres.getName(), resourceProts.get(cres.getTypeIndex()));
			if(success) {
				List<ResourceView> specificResources = facade.getConcreteResourcesForPrototype(resourceProts.get(cres.getTypeIndex()));
				allConcreteResources.add(specificResources.get(specificResources.size() - 1));
			}
		}
		// --------------------------------
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
		}
			// Init reservations
		for(ReservationCreationData rcd : reservations) {
			
			ProjectView project = facade.getProjects().get(taskData.get(rcd.getTask()).getProject());
			TaskView task = creationList.get(rcd.getTask());
			
			facade.reserveResource(
					allConcreteResources.get(rcd.getResource()), 
					project,
					task,
					rcd.getStartTime(),
					rcd.getEndTime());
		}
		// End initialization
		
		return facade;
	}

}
