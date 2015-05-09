package company;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import company.caretaker.TaskManCaretaker;
import company.taskMan.ProjectView;
import company.taskMan.TaskMan;
import company.taskMan.project.TaskView;
import company.taskMan.resource.Reservation;
import company.taskMan.resource.ResourceView;
import company.taskMan.resource.user.UserPermission;

public class Branch {
	private TaskMan taskMan;
	private final TaskManCaretaker caretaker;
	private final String geographicLocation;

	public Branch(LocalDateTime time, String geographicLocation) {
		this.taskMan = new TaskMan(time);
		caretaker = new TaskManCaretaker(this);
		this.geographicLocation = geographicLocation;
	}

	public void initializeFromMemento(LocalDateTime time) {
		this.taskMan = new TaskMan(time);
	}

	public LocalDateTime getCurrentTime() {
		return taskMan.getCurrentTime();
	}

	public boolean createProject(String name, String description,
			LocalDateTime creationTime, LocalDateTime dueTime) {
		return taskMan.createProject(name, description, creationTime, dueTime);
	}

	public boolean createProject(String name, String description,
			LocalDateTime dueTime) {
		return taskMan.createProject(name, description, dueTime);
	}

	public boolean createTask(ProjectView project, String description,
			int estimatedDuration, int acceptableDeviation,
			List<TaskView> prerequisiteTasks,
			Map<ResourceView, Integer> requiredResources,
			TaskView alternativeFor) {

		return taskMan.createTask(project, description, estimatedDuration,
				acceptableDeviation, prerequisiteTasks, requiredResources,
				alternativeFor);
	}

	public boolean createTask(ProjectView project, String description,
			int estimatedDuration, int acceptableDeviation,
			List<TaskView> prerequisiteTasks, TaskView alternativeFor,
			Map<ResourceView, Integer> requiredResources, String taskStatus,
			LocalDateTime startTime, LocalDateTime endTime,
			LocalDateTime plannedStartTime, List<ResourceView> plannedDevelopers) {

		return taskMan.createTask(project, description, estimatedDuration,
				acceptableDeviation, prerequisiteTasks, alternativeFor,
				requiredResources, taskStatus, startTime, endTime,
				plannedStartTime, plannedDevelopers);
	}

	public boolean advanceTimeTo(LocalDateTime time) {
		return taskMan.advanceTimeTo(time);

	}

	public boolean setTaskFinished(ProjectView project, TaskView task,
			LocalDateTime endTime) {
		return taskMan.setTaskFinished(project, task, endTime);
	}

	public boolean setTaskFailed(ProjectView project, TaskView task,
			LocalDateTime endTime) {
		return taskMan.setTaskFailed(project, task, endTime);
	}

	public boolean setTaskExecuting(ProjectView project, TaskView task,
			LocalDateTime startTime) {
		return taskMan.setTaskExecuting(project, task, startTime);
	}

	public List<ProjectView> getProjects() {
		return taskMan.getProjects();
	}

	public boolean storeInMemento() {
		if (!taskMan.currentUserHasPermission(UserPermission.SIMULATE)) {
			return false;
		}
		caretaker.storeInMemento();
		return true;
	}

	public boolean revertFromMemento() {
		return caretaker.revertFromMemento();
	}

	public boolean discardMemento() {
		return caretaker.discardMemento();
	}

	public ResourceView getCurrentUser() {
		return taskMan.getCurrentUserName();
	}

	public List<ResourceView> getPossibleUsers() {
		return taskMan.getPossibleUsernames();
	}

	public boolean changeToUser(ResourceView user) {
		return taskMan.changeToUser(user);
	}

	public boolean createResourcePrototype(String name,
			Optional<LocalTime> availabilityStart,
			Optional<LocalTime> availabilityEnd) {
		return taskMan.createResourcePrototype(name, availabilityStart,
				availabilityEnd);
	}

	public boolean declareConcreteResource(String name,
			ResourceView fromPrototype) {
		return taskMan.declareConcreteResource(name, fromPrototype);
	}

	public boolean createDeveloper(String name) {
		return taskMan.createDeveloper(name);
	}

	public boolean reserveResource(ResourceView resource, ProjectView project,
			TaskView task, LocalDateTime startTime, LocalDateTime endTime) {
		return taskMan.reserveResource(resource, project, task, startTime,
				endTime);
	}

	public List<ResourceView> getDeveloperList() {
		return taskMan.getDeveloperList();
	}

	public HashMap<ProjectView, List<TaskView>> findConflictingPlannings(
			TaskView task) {
		return taskMan.findConflictingPlannings(task);
	}

	public List<ResourceView> getResourcePrototypes() {
		return taskMan.getResourcePrototypes();
	}

	public ResourceView getPrototypeOf(ResourceView resource) {
		return taskMan.getPrototypeOf(resource);
	}

	public List<ResourceView> getConcreteResourcesForPrototype(
			ResourceView resourcePrototype) {
		return taskMan.getConcreteResourcesForPrototype(resourcePrototype);
	}

	public boolean planTask(ProjectView project, TaskView task,
			LocalDateTime plannedStartTime, List<ResourceView> concRes,
			List<ResourceView> devs) {
		return taskMan.planTask(project, task, plannedStartTime, concRes, devs);
	}

	public boolean planRawTask(ProjectView project, TaskView task,
			LocalDateTime plannedStartTime, List<ResourceView> concRes,
			List<ResourceView> devs) {
		return taskMan.planRawTask(project, task, plannedStartTime, concRes,
				devs);
	}

	public boolean addRequirementsToResource(List<ResourceView> resourcesToAdd,
			ResourceView prototype) {
		return taskMan.addRequirementsToResource(resourcesToAdd, prototype);
	}

	public boolean addConflictsToResource(List<ResourceView> resourcesToAdd,
			ResourceView prototype) {
		return taskMan.addConflictsToResource(resourcesToAdd, prototype);
	}

	public boolean currentUserHasPermission(UserPermission permission) {
		return taskMan.currentUserHasPermission(permission);
	}

	public List<TaskView> getUpdatableTasksForUser(ProjectView project) {
		return taskMan.getUpdatableTasksForUser(project);
	}

	public List<Reservation> getAllReservations() {
		return taskMan.getAllReservations();
	}
}
