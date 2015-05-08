package initialization;

import java.util.List;

/**
 * Instances of this class can be used to supply the data needed for the system
 * to initialize a resource prototype
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
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
