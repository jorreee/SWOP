package userInterface.requests;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import userInterface.IFacade;
import view.ProjectView;

public class CreateTaskRequest extends Request {

	public CreateTaskRequest(IFacade facade, BufferedReader inputReader) {
		super(facade, inputReader);
	}

	@Override
	public String execute() {
		List<ProjectView> projects = facade.getProjects();
		for(ProjectView project : projects) {
			System.out.println("Project " + project.getProjectName() + " ID: " + project.getID());
		}
		
		while(true) {
			try {
				String[] creationForm = { "Project ID", "Description",
						"Estimated Duration (in minutes)",
						"Acceptable Deviation (a precentage)",
						"Alternative For (-1 for no alternative)",
						"Prerequisite Tasks (Seperated by spaces, nothing for no prerequisite)" };
				String[] input = new String[6];
				for(int i=0 ; i < 6 ; i++) {
					// Show task creation form
					System.out.println(creationForm[i] + "? (type quit to exit)");

					// Take user input
					input[i] = inputReader.readLine();

					// User quits
					if(input[i].equals("quit"))
						return quit();
				}
				// System updates details
				ArrayList<Integer> prereqList = new ArrayList<>();
				for(String prereq : input[5].split(" ")) {
					if(!prereq.equals("")) {
						prereqList.add(Integer.parseInt(prereq));
					}
				}
				
				boolean success = facade.createTask(
						projects.get(Integer.parseInt(input[0])), input[1],
						Integer.parseInt(input[2]), Integer.parseInt(input[3]),
						Integer.parseInt(input[4]), prereqList);

				// Invalid details
				if(success) {
					return "Task Created";
				} else {
					System.out.println("Invalid input");
				}

			} catch(Exception e) {
				System.out.println("Invalid input");
			}
			return null;
		}
	}

	private String quit() {
		return "No task created";
	}

}
