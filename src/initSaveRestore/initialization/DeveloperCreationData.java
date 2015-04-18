package initSaveRestore.initialization;

/**
 * Instances of this class can be used to supply the data needed for the system
 * to initialize a developer
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class DeveloperCreationData {

	private final String name;

	public DeveloperCreationData(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
