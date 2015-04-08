package taskMan.resource;

import java.time.LocalTime;

public class AvailabilityPeriod {
	private LocalTime startTime;
	private LocalTime endTime;

	public AvailabilityPeriod(LocalTime startTime, LocalTime endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public LocalTime getStartTime() { 
		return startTime;
	}

	public LocalTime getEndTime() { 
		return endTime;
	}

}
