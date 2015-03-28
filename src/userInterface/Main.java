package userInterface;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;

import taskMan.Facade;
import userInterface.initialization.ConcreteResourceCreationData;
import userInterface.initialization.DeveloperCreationData;
import userInterface.initialization.PlanningCreationData;
import userInterface.initialization.ProjectCreationData;
import userInterface.initialization.ReservationCreationData;
import userInterface.initialization.ResourcePrototypeCreationData;
import userInterface.initialization.TaskCreationData;
import userInterface.initialization.TaskManInitFileChecker;
import userInterface.initialization.TaskStatus;
import userInterface.requests.Request;
/**
 * Main class of the User Interface of the project TaskMan.
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and Eli Vangrieken
 */
public class Main {
		
	public static void main(String[] args) throws IOException {
		System.out.println("~~~~~~~~~~~~~~~ TASKMAN ~~~~~~~~~~~~~~~");
		// Initialize variables from file
		TaskManInitFileChecker fileChecker;
		
		if (args.length < 1)
			System.err.println("Error: First command line argument must be filename.");
		fileChecker = new TaskManInitFileChecker(new FileReader(args[0]));
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
		facade.declareDailyAvailability(fileChecker.getDailyAvailabilityTime()[0],
				fileChecker.getDailyAvailabilityTime()[1]);
			// Init resource prototypes
		for(ResourcePrototypeCreationData rprot : resourcePrototypes) {
			facade.createResourcePrototype(rprot.getName(), rprot.getRequirements(), rprot.getConflicts(), rprot.getAvailabilityIndex());
		}
			// Init concrete resources
		for(ConcreteResourceCreationData cres : concreteResources) {
			facade.createRawResource(cres.getName(), cres.getTypeIndex());
		}
			// Init developers
		for(DeveloperCreationData dev : developers) {
			facade.createDeveloper(dev.getName());
		}
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
			PlanningCreationData planning = tcd.getPlanningData();
			if(planning != null)
				facade.createRawPlannedTask(tcd.getProject(), tcd.getDescription(),
						tcd.getEstimatedDuration(),
						tcd.getAcceptableDeviation(), tcd.getPrerequisiteTasks(),
						tcd.getAlternativeFor(), statusString,
						tcd.getStartTime(), tcd.getEndTime(),
						planning.getDueTime(), planning.getDevelopers(),
						planning.getResources());
			else
				facade.createRawTask(tcd.getProject(), tcd.getDescription(),
					tcd.getEstimatedDuration(),
					tcd.getAcceptableDeviation(), tcd.getPrerequisiteTasks(),
					tcd.getAlternativeFor(), statusString,
					tcd.getStartTime(), tcd.getEndTime());
		}
			// Init reservations
		for(ReservationCreationData rcd : reservations) {
			facade.createRawReservation(rcd.getResource(), taskData.get(rcd.getTask()).getProject(), rcd.getTask(), rcd.getStartTime(), rcd.getEndTime());
		}
		// End initialization
		
		// Start accepting user input
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		InputParser inParser = new InputParser(facade, input);
		while(true) {
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

}
