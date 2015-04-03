package initSaveRestore.initialization;

import java.time.LocalDateTime;
import java.util.List;

import taskMan.util.IntPair;

public class TaskCreationData {

	private final int project;
	private final String description;
	private final int estimatedDuration;
	private final int acceptableDeviation;
	private final int alternativeFor;
	private final List<Integer> prerequisiteTasks;
	private final List<IntPair> requiredResources;
	private final TaskStatus status;
	private final LocalDateTime startTime;
	private final LocalDateTime endTime;
	private PlanningCreationData planningData;

	public TaskCreationData(int project, String description,
			int estimatedDuration, int acceptableDeviation, int alternativeFor,
			List<Integer> prerequisiteTasks, List<IntPair> requiredResources,
			TaskStatus status, LocalDateTime startTime, LocalDateTime endTime,
			PlanningCreationData planningData) {
		this.project = project;
		this.description = description;
		this.estimatedDuration = estimatedDuration;
		this.acceptableDeviation = acceptableDeviation;
		this.alternativeFor = alternativeFor;
		this.prerequisiteTasks = prerequisiteTasks;
		this.requiredResources = requiredResources;
		this.status = status;
		this.startTime = startTime;
		this.endTime = endTime;
		this.planningData = planningData;
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

	public int getAlternativeFor() {
		return alternativeFor;
	}

	public List<Integer> getPrerequisiteTasks() {
		return prerequisiteTasks;
	}
	
	public List<IntPair> getRequiredResources() {
		return requiredResources;
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
	
	public PlanningCreationData getPlanningData() {
		return planningData;
	}

}
