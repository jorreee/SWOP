package userInterface;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import taskMan.Facade;
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
		
		ArrayList<ProjectCreationData> projectData = fileChecker.getProjectDataList();
		ArrayList<TaskCreationData> taskData = fileChecker.getTaskDataList();
		
		// Get facade
		Facade facade = new Facade();
		
		// Initialize system through a facade
		for(ProjectCreationData pcd : projectData) {
			facade.createProject(pcd.getName(), pcd.getDescription(), pcd.getCreationTime(), pcd.getDueTime());
		}
		for(TaskCreationData tcd : taskData) {
			TaskStatus status = tcd.getStatus();
			String statusString = null;
			if(status != null)
				statusString = status.name();
			facade.createTask(tcd.getProject(), tcd.getDescription(),
					tcd.getEstimatedDuration(),
					tcd.getAcceptableDeviation(), statusString,
					tcd.getAlternativeFor(), tcd.getPrerequisiteTasks(),
					tcd.getStartTime(), tcd.getEndTime());
		}
		
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
