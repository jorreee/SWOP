package initSaveRestore.initialization;

/**
 * Instances of this class can be used to supply the data needed for the system
 * to initialize a concrete resource
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class ConcreteResourceCreationData {

	private final String name;
	private final int typeIndex;

	public ConcreteResourceCreationData(String name, int typeIndex) {
		this.name = name;
		this.typeIndex = typeIndex;
	}

	public String getName() {
		return name;
	}

	public int getTypeIndex() {
		return typeIndex;
	}

}
