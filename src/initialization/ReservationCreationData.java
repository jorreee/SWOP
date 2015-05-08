package initialization;

import java.time.LocalDateTime;

/**
 * Instances of this class can be used to supply the data needed for the system
 * to initialize a reservation
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class ReservationCreationData {

	private final int resource;
	private final int task;
	private final LocalDateTime startTime;
	private final LocalDateTime endTime;

	public ReservationCreationData(int resource, int task,
			LocalDateTime startTime, LocalDateTime endTime) {
		this.resource = resource;
		this.task = task;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public int getResource() {
		return resource;
	}

	public int getTask() {
		return task;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}
}
