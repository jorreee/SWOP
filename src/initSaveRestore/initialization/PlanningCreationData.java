package initSaveRestore.initialization;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Instances of this class can be used to supply the data needed for the system
 * to initialize a planning
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((developers == null) ? 0 : developers.hashCode());
		result = prime * result + ((dueTime == null) ? 0 : dueTime.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlanningCreationData other = (PlanningCreationData) obj;
		if (developers == null) {
			if (other.developers != null)
				return false;
		} else if (!developers.equals(other.developers))
			return false;
		if (dueTime == null) {
			if (other.dueTime != null)
				return false;
		} else if (!dueTime.equals(other.dueTime))
			return false;
		return true;
	}

}
