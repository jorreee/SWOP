package userInterface.initialization;

import java.time.LocalDateTime;
import java.util.List;

import taskMan.util.IntPair;

public class PlanningCreationData {
	
	private final List<IntPair> resources;
	private final List<Integer> developers;
	private final LocalDateTime dueTime;

	public PlanningCreationData(LocalDateTime dueTime,
			List<Integer> developers, List<IntPair> resources) {
		this.dueTime = dueTime;
		this.developers = developers;
		this.resources = resources;
	}

	public List<IntPair> getResources() {
		return resources;
	}

	public List<Integer> getDevelopers() {
		return developers;
	}

	public LocalDateTime getDueTime() {
		return dueTime;
	}

}
