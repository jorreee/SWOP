package userInterface.requests;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import taskMan.view.ProjectView;
import taskMan.view.ResourceView;
import taskMan.view.TaskView;
import userInterface.IFacade;

public class CreateTaskRequest extends Request {

	public CreateTaskRequest(IFacade facade, BufferedReader inputReader) {
		super(facade, inputReader);
	}

	@Override
	public String execute() {
		List<ProjectView> projects = facade.getProjects();
		for(ProjectView project : projects) {
			System.out.println(project.getID() + ": Project " + project.getName());
		}
		
		List<ResourceView> resourceTypes = facade.getResourcePrototypes();
		for(int i = 0 ; i < resourceTypes.size() ; i++) {
			System.out.println(i + ": Resouce: " + resourceTypes.get(i).getName()
					+ ", total quantity " + facade.getConcreteResourcesForPrototype(resourceTypes.get(i)).size());
		}
		
		while(true) {
			try {
				String[] creationForm = { "Project ID", "Description",
						"Estimated Duration (in minutes)",
						"Acceptable Deviation (a precentage)",
						"Alternative For (-1 for no alternative)",
						"Prerequisite Tasks (Seperated by spaces, nothing for no prerequisites)",
						"Desired resources (Resource type and quantity separated by spaces, nothing for no resources)"};
				String[] input = new String[creationForm.length];
				for(int i=0 ; i < creationForm.length ; i++) {
					// Show task creation form
					System.out.println(creationForm[i] + "? (type quit to exit)");

					// Take user input
					input[i] = inputReader.readLine();

					// User quits
					if(input[i].equals("quit"))
						return quit();
				}

				ProjectView project = projects.get(Integer.parseInt(input[0]));
				List<TaskView> tasks = project.getTasks();
				
				// System updates details
				ArrayList<TaskView> prereqList = new ArrayList<>();
				for(String prereq : input[5].split(" ")) {
					if(!prereq.equals("")) {
						prereqList.add(tasks.get(Integer.parseInt(prereq)));
					}
				}
				
				HashMap<ResourceView, Integer> reqRes = new HashMap<>();
				Iterator<String> resourceInput = Arrays.asList(input[6].split(" ")).iterator();
				while(resourceInput.hasNext()) {
					Integer type = Integer.parseInt(resourceInput.next());
					Integer quantity = Integer.parseInt(resourceInput.next());
					reqRes.put(resourceTypes.get(type), quantity);
				}
				
				//-1 mag geen error geven
				TaskView altFor = null;
				int altForID = Integer.parseInt(input[4]);
				if(altForID >= 0) {
					altFor = tasks.get(altForID);
				}
				
				// createTask(ProjectView project, String description,
				// int estimatedDuration, int acceptableDeviation,
				// List<TaskView> prerequisiteTasks,
				// Map<ResourceView, Integer> requiredResources
				// TaskView alternativeFor);
				boolean success = facade.createTask(
						projects.get(Integer.parseInt(input[0])), input[1],
						Integer.parseInt(input[2]), Integer.parseInt(input[3]),
						prereqList, reqRes, altFor);

				// Invalid details
				if(success) {
					return "Task Created";
				} else {
					System.out.println("Invalid input");
				}

			} catch(Exception e) {
				e.printStackTrace();
				System.out.println("Invalid input\n");
			}
			return null;
		}
	}

	private String quit() {
		return "No task created";
	}

}
