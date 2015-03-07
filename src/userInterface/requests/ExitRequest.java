package userInterface.requests;

public class ExitRequest extends Request {

	@Override
	public String execute() {
		System.exit(0);
		return null;
	}

}
