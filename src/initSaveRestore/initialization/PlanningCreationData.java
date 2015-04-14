package initSaveRestore.initialization;

import java.time.LocalDateTime;
import java.util.List;

public class PlanningCreationData {
	
	private final List<Integer> developers;
	private final LocalDateTime dueTime;

	public PlanningCreationData(LocalDateTime dueTime,
			List<Integer> developers) {
		this.dueTime = dueTime;
		this.developers = developers;
	}

	public List<Integer> getDevelopers() {
		return developers;
	}

	public LocalDateTime getPlannedStartTime() {
		return dueTime;
	}

}
