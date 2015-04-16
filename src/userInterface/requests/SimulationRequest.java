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
		facade.storeInMemento();
		LocalDateTime simulationStart = facade.getCurrentTime();

		// Create a user-input-parser to generate requests
		InputParser inParser = new InputParser(facade, inputReader);

		// Ask user for requests and execute them
		while(true) {
			try {
				System.out.println("~~~ TASKMAN SIMULATION ~~~");
				System.out.println("Simulation started at: " + simulationStart.format(dateTimeFormatter));
				// Display possible requests
				System.out.println(new HelpRequest(facade, inputReader).execute());
				System.out.println("commit				to commit changes made in the simulation");
				System.out.println("revert				to revert changes made in the simulation");
				
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
				
				// Execute Request
				String response = request.execute();
				
				// Display the response of the previous request
				System.out.println(response);
			} catch(IOException e) {
				System.out.println("Invalid input!");
			}
		} // REPEAT
	}

}
