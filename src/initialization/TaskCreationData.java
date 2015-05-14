package initialization;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Instances of this class can be used to supply the data needed for the system
 * to initialize a task
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class TaskCreationData {

	private final int project;
	private final String description;
	private final int estimatedDuration;
	private final int acceptableDeviation;
	private final int alternativeFor;
	private final List<Integer> prerequisiteTasks;
	private final List<IntPair> requiredResources;
	private final TaskStatus status;
	private final Integer responsibleBranch;
	private final LocalDateTime startTime;
	private final LocalDateTime endTime;
	private PlanningCreationData planningData;

	public TaskCreationData(int project, String description,
			int estimatedDuration, int acceptableDeviation, int alternativeFor,
			List<Integer> prerequisiteTasks, List<IntPair> requiredResources,
			TaskStatus status, Integer responsibleBranch, LocalDateTime startTime,
			LocalDateTime endTime, PlanningCreationData planningData) {
		this.project = project;
		this.description = description;
		this.estimatedDuration = estimatedDuration;
		this.acceptableDeviation = acceptableDeviation;
		this.alternativeFor = alternativeFor;
		this.prerequisiteTasks = prerequisiteTasks;
		this.requiredResources = requiredResources;
		this.status = status;
		this.responsibleBranch = responsibleBranch;
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

	public Integer getResponsibleBranch() {
		return responsibleBranch;
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
