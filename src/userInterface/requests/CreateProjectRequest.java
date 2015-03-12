package userInterface.requests;

import java.io.BufferedReader;
import java.time.LocalDateTime;

import userInterface.IFacade;

public class CreateProjectRequest extends Request {
	
	public CreateProjectRequest(IFacade facade, BufferedReader inputReader) {
		super(facade, inputReader);
	}

	@Override
	public String execute() {
		while(true) {
			try {
				String[] creationForm = {"Name", "Description", "Due Time (Format Y M D H M)"};
				String[] input = new String[3];
				for(int i=0 ; i < 3 ; i++) {
					// Show project creation form
					System.out.println(creationForm[i] + "? (type quit to exit)");

					// Take user input
					input[i] = inputReader.readLine();

					// User quits
					if(input[i].equals("quit"))
						return quit();
				}
				// System updates details
				String[] dueBits = input[2].split(" ");
				LocalDateTime due = LocalDateTime.of(Integer.parseInt(dueBits[0]), Integer.parseInt(dueBits[1]), Integer.parseInt(dueBits[2]), Integer.parseInt(dueBits[3]), Integer.parseInt(dueBits[4]));
				boolean success = facade.createProject(input[0], input[1], due);

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
