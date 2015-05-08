package company.taskMan.resource;

import java.time.LocalDateTime;

import company.taskMan.task.Task;

/**
 * A reservation will link a resource (a concrete resource or a user) with a task (reserving the
 * resource). A reservation has a start and an end time. When this reservation
 * is active, the same resource should not be reserved again in a timeslot
 * overlapping with this reservation.
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class Reservation {
	
	// Reservation consists of:
	// A concrete resource or a user
	private final ConcreteResource reservedResource;
	// A task
	private final Task reservingTask;
	// Start and end time
	private final LocalDateTime startTime, endTime;
	
	/**
	 * Construct a new reservation for a resource by a task during a specific
	 * timeslot.
	 * 
	 * @param reservedResource
	 *            | The resource that should be reserved
	 * @param reservingTask
	 *            | The task making the reservation
	 * @param startTime
	 *            | The time indicating the start of the reservation (the
	 *            reserved resource is no longer available for use)
	 * @param endTime
	 *            | The time indicating the end of the reservation (the reserved
	 *            resource is again available for use)
	 */
	public Reservation(ConcreteResource reservedResource, Task reservingTask,
			LocalDateTime startTime, LocalDateTime endTime) {
		this.reservedResource = reservedResource;
		this.reservingTask = reservingTask;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	/**
	 * A getter for the reserved resource
	 * 
	 * @return the resource reserved by this reservation
	 */
	public ConcreteResource getReservedResource() {
		return reservedResource;
	}
	
	/**
	 * A getter for the reserving task
	 * 
	 * @return the task reserving the resource contained in this reservation
	 */
	public Task getReservingTask() {
		return reservingTask;
	}
	
	/**
	 * A getter for the start time
	 * 
	 * @return the time when the reservation will be in effect
	 */
	public LocalDateTime getStartTime() {
		return startTime;
	}

	/**
	 * A getter for the end time
	 * 
	 * @return the time when the reservation will no longer be in effect
	 */
	public LocalDateTime getEndTime() {
		return endTime;
	}
	

	/**
	 * Returns whether the reservation overlaps with a given start and end date.
	 * 
	 * @param 	start
	 * 			The start date
	 * @param 	end
	 * 			The end date
	 * @return true if it overlaps, false otherwise
	 */
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

	public boolean hasAsResource(ConcreteResource resource) {
		if(reservedResource == resource) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean equals(Object other) {
		if(other == null) {
			return false;
		}
		try{
			Reservation otherReservation = (Reservation) other;
			if(!this.hasAsResource(otherReservation.getReservedResource())) {
				return false;
			}
			if(!this.reservingTask.equals(otherReservation.getReservingTask())) {
				return false;
			}
			if(!this.startTime.equals(otherReservation.getStartTime()) &&!this.endTime.equals(otherReservation.getEndTime())) {
				return false;
			}
		} catch(ClassCastException e) {
			return false;
		}
		return true;
	}
	
}
