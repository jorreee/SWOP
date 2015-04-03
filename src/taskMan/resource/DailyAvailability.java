package taskMan.resource;

import java.time.LocalTime;

public class DailyAvailability {
	private LocalTime startTime;
	private LocalTime endTime;

	public DailyAvailability(LocalTime startTime, LocalTime endTime) {
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
