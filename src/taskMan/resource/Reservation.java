package taskMan.resource;

import java.time.LocalDateTime;

import taskMan.Task;

public class Reservation {
	
	// Reservation consists of:
	// An abstract resource (type or concrete)
	private final Resource reservedResource;
	// A task
	private final Task reservingTask;
	// Start and end time
	private final LocalDateTime startTime, endTime;
	
	public Reservation(Resource reservedResource, Task reservingTask,
			LocalDateTime startTime, LocalDateTime endTime) {
		super();
		this.reservedResource = reservedResource;
		this.reservingTask = reservingTask;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
}
