package userInterface.initialization;

import java.util.List;

public class ResourcePrototypeCreationData {

	private final String name;
	private final List<Integer> requirements;
	private final List<Integer> conflicts;
	private final Integer availabilityIndex;

	public ResourcePrototypeCreationData(String name,
			List<Integer> requirements, List<Integer> conflicts,
			Integer availabilityIndex) {
		this.name = name;
		this.requirements = requirements;
		this.conflicts = conflicts;
		this.availabilityIndex = availabilityIndex;
	}

	public String getName() {
		return name;
	}

	public List<Integer> getRequirements() {
		return requirements;
	}

	public List<Integer> getConflicts() {
		return conflicts;
	}

	public Integer getAvailabilityIndex() {
		return availabilityIndex;
	}

}
