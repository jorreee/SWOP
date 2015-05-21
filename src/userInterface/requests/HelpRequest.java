package userInterface.requests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import userInterface.IFacade;

public class HelpRequest extends Request {
	
	public HelpRequest(IFacade facade, BufferedReader inputReader) {
		super(facade, inputReader);
	}

	@Override
	public String execute() {
		try {
			InputStream helpFileStream = getClass().getResourceAsStream("help.txt");
			BufferedReader bufRead = new BufferedReader(new InputStreamReader(helpFileStream));
			StringBuilder helpFileBody = new StringBuilder();
			String fileLine;
			while((fileLine = bufRead.readLine()) != null) {
				helpFileBody.append(fileLine + "\n");
			}
			return helpFileBody.toString();
		} catch (IOException e) {
			System.out.println("Help file not found! ~Should not happen!");;
		}
		return "Should not happen";
	}

	@Override
	public boolean isSimulationSupported() {
		return true;
	}

}
