package userInterface.requests;

import java.io.BufferedReader;
import java.util.List;

import userInterface.IFacade;
import company.BranchView;
import company.taskMan.ProjectView;
import company.taskMan.project.TaskView;

public class DelegateTaskRequest extends Request {

	public DelegateTaskRequest(IFacade facade, BufferedReader inputReader) {
		super(facade, inputReader);
	}

	@Override
	public String execute() {
		// SELECT PROJECT AND TASK TO DELEGATE
		int projectID = -1;
		int taskID = -1;
		while(true) {
			try {
				// Show a list of projects and their unplanned tasks
				List<ProjectView> projects = facade.getAllProjects();
								
				for(int i = 0 ; i < projects.size() ; i++) {
					System.out.println("(" + i + ") Project " + projects.get(i).getName() + ":");
					List<TaskView> unplannedTasks = projects.get(i).getUnplannedTasks();
					for(int j = 0 ; j < unplannedTasks.size() ; j++) {
						System.out.println("  {" + j + "} " + unplannedTasks.get(j).getDescription());
					}
				}				

				// Ask for user input
				System.out.println("Select a project and the task you wish to delegate (Format: (Project location in list) (Task location in list), type quit to exit)");
				String input = inputReader.readLine();

				// User quits
				if(input.toLowerCase().equals("quit"))
				{
					return quit();
				}

				// Select task
				projectID = Integer.parseInt(input.split(" ")[0]);
				taskID = Integer.parseInt(input.split(" ")[1]);

				ProjectView project = projects.get(projectID);
				TaskView task = project.getUnplannedTasks().get(taskID);

				// SELECT NEW BRANCH
				List<BranchView> branches = facade.getBranches();

				System.out.println("Please select your branch (type quit to exit)");
				for(int i = 0 ; i < branches.size() ; i++) {
					System.out.println("<" + i + "> The " + branches.get(i).getGeographicLocation() + " Branch");
				}

				input = inputReader.readLine();
				// Escape
				if(input.equalsIgnoreCase("quit"))
				{
					return quit();
				}

				BranchView newBranch = branches.get(Integer.parseInt(input));

				facade.delegateTask(project, task, newBranch);
				return "Task " + task.getDescription() + " was delegated to the " + newBranch.getGeographicLocation() + " Branch!";
			} catch(Exception e) {
				e.printStackTrace();
				System.out.println("Invalid selection! Try again");
			}
		}
	}

	private String quit() {
		return "No tasks were delegated";
	}
}
