package taskMan.resource;

import java.time.LocalDateTime;

import taskMan.Task;

public class Reservation {
	
	// Reservation consists of:
	// An abstract resource (type or concrete)
	private final ConcreteResource reservedResource; //TODO steeds een concrete
	// A task
	private final Task reservingTask;
	// Start and end time
	private final LocalDateTime startTime, endTime;
	
	public Reservation(ConcreteResource reservedResource, Task reservingTask,
			LocalDateTime startTime, LocalDateTime endTime) {
		this.reservedResource = reservedResource;
		this.reservingTask = reservingTask;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public ConcreteResource getReservedResource() {
		return reservedResource;
	}
	
	public Task getReservingTask() {
		return reservingTask;
	}
	
	public LocalDateTime getStartTime() {
		return startTime;
	}
	
	public LocalDateTime getEndTime() {
		return endTime;
	}
	
	public boolean overlaps(LocalDateTime start, LocalDateTime end) {
		//             ###############
		//     nnnnnnn
		if(!end.isAfter(startTime)) {
			return false;
		}
		// ##########
		//			  nnnnnnnnnn
		if(!start.isBefore(endTime)) {
			return false;
		}
		return true;		
	}
	
}
