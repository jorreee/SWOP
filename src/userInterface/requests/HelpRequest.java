package userInterface.requests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HelpRequest extends Request {
	
	@Override
	public String execute() {
		try {
			byte[] encoded = Files.readAllBytes(Paths.get("src", "userInterface", "requests", "help.txt"));
			return new String(encoded);			
		} catch (IOException e) {
			System.out.println("Help file not found! ~Should not happen!");;
		}
		return "Should not happen";
	}

}
