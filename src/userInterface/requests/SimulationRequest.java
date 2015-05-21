package userInterface.requests;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;

import userInterface.IFacade;
import userInterface.InputParser;

public class SimulationRequest extends Request {

	public SimulationRequest(IFacade facade, BufferedReader inputReader) {
		super(facade, inputReader);
	}

	@Override
	public String execute() {
		// Store current system state
		if(!facade.storeInMemento()) {
			return "Error";
		}
		LocalDateTime simulationStart = facade.getCurrentTime();

		// Create a user-input-parser to generate requests
		InputParser inParser = new InputParser(facade, inputReader);

		// Ask user for requests and execute them
		while(true) {
			try {
				System.out.println("~~~ TASKMAN SIMULATION ~~~");
				System.out.println("Simulation started at: " + simulationStart.format(dateTimeFormatter));
				// Display possible requests
				System.out.println("TaskMan iteration 3 simulation commands:");
				System.out.println("h			Display all commands with a short description");
				System.out.println("change user		Log in as a different system user");
				System.out.println("show			Overview with additional details");
				System.out.println("advance			Advance time to a later date");
				System.out.println("create project		Create a new project");
				System.out.println("create task		Create a new task assigned to an existing project");
				System.out.println("plan task		Plan a specific task and reserve required resources");
				System.out.println("delegate task		Delegate a task to a different branch");
				System.out.println("commit			to commit changes made in the simulation");
				System.out.println("revert			to revert changes made in the simulation");
				
				// Parse user input for escape commands
				String input = inputReader.readLine();
				switch(input) {
				case "commit" :
					facade.discardMemento();
					return "Simulation committed";
				case "revert" :
					facade.revertFromMemento();
					return "Simulation reverted";
				}
				
				// Parse user input
				Request request = inParser.parse(input);
				
				String response;
				
				if(!request.isSimulationSupported()) {
					response = "This operation is not supported during the simulation, type h for help on supported commands during a simulation";
				} else if(request instanceof HelpRequest) {
					response = "";
				} else {
				// Execute Request
					response = request.execute();
				}
				
				// Display the response of the previous request
				System.out.println(response);
			} catch(IOException e) {
				System.out.println("Invalid input!");
			}
		} // REPEAT
	}

	@Override
	public boolean isSimulationSupported() {
		return false;
	}

}
