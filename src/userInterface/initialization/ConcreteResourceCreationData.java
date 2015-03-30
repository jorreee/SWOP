package userInterface.initialization;

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
