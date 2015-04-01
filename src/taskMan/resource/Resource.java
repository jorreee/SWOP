package taskMan.resource;

import java.time.LocalTime;
import java.util.List;

public abstract class Resource {
	
	protected String resName;
	protected List<ResourcePrototype> reqResourcesList;
	protected List<ResourcePrototype> conResourcesList;
	
	protected DailyAvailability dailyAvailable = null;
	
	public Resource(String resourceName, List<ResourcePrototype> requiredResourcesList, List<ResourcePrototype> conflictingResourcesList, DailyAvailability dailyAvailability) {
		this.resName = resourceName;
		this.reqResourcesList = requiredResourcesList;
		this.conResourcesList = conflictingResourcesList;
		this.dailyAvailable = dailyAvailability;
	}
	
	public String getName() { return resName; }
	public List<ResourcePrototype> getRequiredResources() { return reqResourcesList; }
	public List<ResourcePrototype> getConflictingResources() { return conResourcesList; }
	
	public boolean isDailyAvailable() { return dailyAvailable != null; }
	
	public LocalTime getDailyAvailabilityStartTime() {
		return dailyAvailable.getStartTime();
	}
	
	public LocalTime getDailyAvailabilityEndTime() {
		return dailyAvailable.getEndTime();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((conResourcesList == null) ? 0 : conResourcesList.hashCode());
		result = prime
				* result
				+ ((reqResourcesList == null) ? 0 : reqResourcesList.hashCode());
		result = prime * result + ((resName == null) ? 0 : resName.hashCode());
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
		Resource other = (Resource) obj;
		if (conResourcesList == null) {
			if (other.conResourcesList != null)
				return false;
		} else if (!conResourcesList.equals(other.conResourcesList))
			return false;
		if (reqResourcesList == null) {
			if (other.reqResourcesList != null)
				return false;
		} else if (!reqResourcesList.equals(other.reqResourcesList))
			return false;
		if (resName == null) {
			if (other.resName != null)
				return false;
		} else if (!resName.equals(other.resName))
			return false;
		return true;
	}
}
