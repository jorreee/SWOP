package userInterface;

import java.time.LocalDateTime;
import java.util.List;

public class TaskCreationData {

	private final int project;
	private final String description;
	private final int estimatedDuration;
	private final int acceptableDeviation;
	private final Integer alternativeFor;
	private final List<Integer> prerequisiteTasks;
	private final TaskStatus status;
	private final LocalDateTime startTime;
	private final LocalDateTime endTime;

	public TaskCreationData(int project, String description, int estimatedDuration,
			int acceptableDeviation, Integer alternativeFor,
			List<Integer> prerequisiteTasks, TaskStatus status, LocalDateTime startTime, LocalDateTime endTime) {
		this.project = project;
		this.description = description;
		this.estimatedDuration = estimatedDuration;
		this.acceptableDeviation = acceptableDeviation;
		this.alternativeFor = alternativeFor;
		this.prerequisiteTasks = prerequisiteTasks;
		this.status = status;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public int getProject() {
		return project;
	}

	public String getDescription() {
		return description;
	}

	public int getEstimatedDuration() {
		return estimatedDuration;
	}

	public int getAcceptableDeviation() {
		return acceptableDeviation;
	}

	public Integer getAlternativeFor() {
		return alternativeFor;
	}

	public List<Integer> getPrerequisiteTasks() {
		return prerequisiteTasks;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

}
