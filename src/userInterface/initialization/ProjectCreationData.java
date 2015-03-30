package userInterface.initialization;

import java.time.LocalDateTime;

public class ProjectCreationData {
	
	private final String name;
	private final String description;
	private final LocalDateTime creationTime;
	private final LocalDateTime dueTime;
	
	public ProjectCreationData(String name, String description,
			LocalDateTime creationTime, LocalDateTime dueTime) {
		this.name = name;
		this.description = description;
		this.creationTime = creationTime;
		this.dueTime = dueTime;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public LocalDateTime getCreationTime() {
		return creationTime;
	}

	public LocalDateTime getDueTime() {
		return dueTime;
	}

}
