package userInterface.requests;

import java.io.BufferedReader;
import java.time.LocalDateTime;

import userInterface.IFacade;

public class CreateProjectRequest extends Request {
	
	BufferedReader inputReader;
	
	public CreateProjectRequest(IFacade facade, BufferedReader inputReader) {
		super(facade);
		this.inputReader = inputReader;
	}

	@Override
	public String execute() {
		while(true) {
			try {
				String[] creationForm = {"Name", "Description", "Due Time"};
				String[] input = new String[3];
				for(int i=0 ; i < 3 ; i++) {
					// Show project creation form
					System.out.println(creationForm[i] + "? (type quit to exit");

					// Take user input
					input[i] = inputReader.readLine();

					// User quits
					if(input[i].equals("quit"))
						return quit();
				}
				// System updates details
				boolean success = facade.createProject(input[0], input[1], LocalDateTime.parse(input[2]));

				// Invalid details
				if(success) {
					return "Project Created";
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
		return "No project created";
	}

}
