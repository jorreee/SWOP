package taskMan.test.UseCases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import taskMan.Facade;
import taskMan.view.ProjectView;
import taskMan.view.TaskView;
import userInterface.IFacade;

public class UseCase1ShowProjectsTest {

	private IFacade taskManager;
	private final LocalDateTime startDate = LocalDateTime.of(2015, 2, 9, 8, 0),
			workdate1 = LocalDateTime.of(2015, 2, 11, 16, 0),
			workdate2 = LocalDateTime.of(2015, 2, 12, 16, 0),
			workdate3 = LocalDateTime.of(2015, 2, 13, 16, 0),
			workdate4 = LocalDateTime.of(2015, 2, 15, 16, 0),
			workdate5 = LocalDateTime.of(2015, 2, 16, 10, 0),
			project0DueDate = LocalDateTime.of(2015, 2, 13, 23, 59),
			project1DueDate = LocalDateTime.of(2015, 2, 21, 23, 59),
			project2DueDate = LocalDateTime.of(2015, 2, 16, 23, 59),
			project3DueDate = LocalDateTime.of(2015, 2, 21, 23, 59),
			task00Start = startDate,
			task00End = LocalDateTime.of(2015, 2, 9, 10, 0),
			task10Start = LocalDateTime.of(2015, 2, 12, 8, 0),
			task10End = LocalDateTime.of(2015, 2, 13, 10, 0),
			task11Start = LocalDateTime.of(2015, 2, 13, 10, 15),
			task11End = LocalDateTime.of(2015, 2, 13, 14, 0),
			task30Start = LocalDateTime.of(2015, 2, 16, 8, 0),
			task30End = LocalDateTime.of(2015, 2, 16, 9, 45);
	private final int task00EstDur = 120,
			task10EstDur = 8*60,
			task11EstDur = 3*8*60,
			task12EstDur = 2*8*60,
			task13EstDur = 2*8*60,
			task20EstDur = 35*60,
			task30EstDur = 60,
			task31EstDur = 3*8*60;
	private final int task00Dev = 5,
			task10Dev = 30,
			task11Dev = 0,
			task12Dev = 15,
			task13Dev = 20,
			task20Dev = 50,	// Moet nog steeds delayed project geven!
			task30Dev = 0,
			task31Dev = 15;
	private final ArrayList<TaskView> task00Dependencies = new ArrayList<TaskView>(),
			task10Dependencies = new ArrayList<TaskView>(),
			task11Dependencies = new ArrayList<TaskView>(),
			task12Dependencies = new ArrayList<TaskView>(),
			task13Dependencies = new ArrayList<TaskView>(),
			task20Dependencies = new ArrayList<TaskView>(),
			task30Dependencies = new ArrayList<TaskView>(),
			task31Dependencies = new ArrayList<TaskView>();
	
	/**
	 * - project 0 START 9 feb 8u DUE 31 feb midnight
	 * 		task 0 FINISHED
	 * 
	 * - project 1 START 11 feb 16u DUE 
	 * 		task 0 FINISHED
	 * 		task 1 FAILED
	 * 		task 2 <- 0, 1
	 * 		task 3 ALT 1
	 * 
	 * - project 2 START 13 feb 16u DUE 
	 * 		task 0 
	 * 
	 * - project 3 START 15 feb 16u DUE 
	 * 		task 0 FAILED
	 * 		task 1 
	 * 
	 */
	@Before
	public final void initialize() {
		// INIT systeem en maak het eerste project aan, samen met zijn TASK
		taskManager = new Facade(startDate);
		assertTrue(taskManager.createProject("Project 0", "Describing proj 0", project0DueDate));
		ProjectView project0 = taskManager.getProjects().get(0);
		// 00 AVAILABLE
		assertTrue(taskManager.createTask(project0, "TASK 00", task00EstDur, task00Dev, task00Dependencies, null));		
				
		// Stap verder:
		// maak het tweede project aan en maak zijn TASK lijst
		assertTrue(taskManager.advanceTimeTo(workdate1));
		assertTrue(taskManager.createProject("Project 1", "Describing proj 1", project1DueDate));
		ProjectView project1 = taskManager.getProjects().get(1);
		
		// 10 AVAILABLE
		assertTrue(taskManager.createTask(project1, "TASK 10", task10EstDur, task10Dev, task10Dependencies, null));			
		// 11 AVAILABLE
		assertTrue(taskManager.createTask(project1, "TASK 11", task11EstDur, task11Dev, task11Dependencies, null));			
				
		TaskView task00 = project0.getTasks().get(0);
		TaskView task10 = project1.getTasks().get(0);
		TaskView task11 = project1.getTasks().get(1);
				
		assertTrue(task12Dependencies.add(task10)); 
		assertTrue(task12Dependencies.add(task11));
		// 12 UNAVAILABLE
		assertTrue(taskManager.createTask(project1, "TASK 12", task12EstDur, task12Dev, task12Dependencies, null));			
		
		// Stap verder:
		// maak het derde project aan, samen met zijn TASK
		assertTrue(taskManager.advanceTimeTo(workdate2));
		assertTrue(taskManager.createProject("Project 2", "Describing proj 2", project2DueDate));
		ProjectView project2 = taskManager.getProjects().get(2);
		// 20 AVAILABLE
		assertTrue(taskManager.createTask(project2, "TASK 20", task20EstDur, task20Dev, task20Dependencies, null));		
					
		// Stap verder:
		// maak TASK 0,0 af -> project 0 is finished
		// update project 1
		assertTrue(taskManager.advanceTimeTo(workdate3));
		// 00 FINISHED
		assertTrue(taskManager.setTaskFinished(project0, task00, task00Start, task00End));										
		//----------------------------------------------------
		// 10 FINISHED
		assertTrue(taskManager.setTaskFinished(project1, task10, task10Start, task10End));
		// 11 FAILED
		assertTrue(taskManager.setTaskFailed(project1, task11, task11Start, task11End));
		// 13 AVAILABLE
		assertTrue(taskManager.createTask(project1, "TASK 13", task13EstDur, task13Dev, task13Dependencies, task11));			
			
		// Stap verder:
		// maak het vierde project aan en maak zijn TASK lijst
		assertTrue(taskManager.advanceTimeTo(workdate4));
		assertTrue(taskManager.createProject("Project 3", "Describing proj 3", project3DueDate));
		ProjectView project3 = taskManager.getProjects().get(3);
		// 30 AVAILABLE
		assertTrue(taskManager.createTask(project3, "TASK 30", task30EstDur, task30Dev, task30Dependencies, null));	
		// 31 AVAILABLE
		assertTrue(taskManager.createTask(project3, "TASK 31", task31EstDur, task31Dev, task31Dependencies, null));		
				
		// Stap verder:
		// maak task 3,0 FAILED
		assertTrue(taskManager.advanceTimeTo(workdate5));
		TaskView task30 = project3.getTasks().get(0);
		// 30 FAILED DELAYED
		assertTrue(taskManager.setTaskFailed(project3, task30, task30Start, task30End));										

	}
	
	@Test
	public void SuccesCasetest() {
		// Stap 1 is impliciet
		// Stap 2
		int numOfProj = taskManager.getProjects().size();
		assertEquals(numOfProj,4);
		// Stap 3
		
		//Check project0 details
		ProjectView project0 = taskManager.getProjects().get(0);
		assertTrue(project0.getDescription().equals("Describing proj 0"));
		assertEquals(project0.getDueTime(),project0DueDate);
		assertTrue(project0.getName().equals("Project 0"));
		assertTrue(project0.isFinished());
		assertEquals(project0.getEndTime(),task00End);
		assertEquals(project0.getTasks().size(),1);
		assertEquals(project0.getAvailableTasks().size(),0);
		assertEquals(project0.getCreationTime(),startDate);
		assertTrue(project0.isEstimatedOnTime(taskManager.getCurrentTime()));

		//Check project1 details
		ProjectView project1 = taskManager.getProjects().get(1);
		assertTrue(project1.getDescription().equals("Describing proj 1"));
		assertEquals(project1.getDueTime(),project1DueDate);
		assertTrue(project1.getName().equals("Project 1"));
		assertFalse(project1.isFinished());
		assertEquals(project1.getEndTime(),null);
		assertEquals(project1.getTasks().size(),4);
		assertEquals(project1.getAvailableTasks().size(),1);
		assertEquals(project1.getCreationTime(),workdate1);
		assertFalse(project1.isEstimatedOnTime(taskManager.getCurrentTime()));

		//Check project2 details
		ProjectView project2 = taskManager.getProjects().get(2);
		assertTrue(project2.getDescription().equals("Describing proj 2"));
		assertEquals(project2.getDueTime(),project2DueDate);
		assertTrue(project2.getName().equals("Project 2"));
		assertFalse(project2.isFinished());
		assertEquals(project2.getEndTime(),null);
		assertEquals(project2.getTasks().size(),1);
		assertEquals(project2.getAvailableTasks().size(),1);
		assertEquals(project2.getCreationTime(),workdate2);
		assertFalse(project2.isEstimatedOnTime(taskManager.getCurrentTime())); // DELAYED

		//Check project3 details
		ProjectView project3 = taskManager.getProjects().get(3);
		assertTrue(project3.getDescription().equals("Describing proj 3"));
		assertEquals(project3.getDueTime(),project3DueDate);
		assertTrue(project3.getName().equals("Project 3"));
		assertFalse(project3.isFinished());
		assertEquals(project3.getEndTime(),null);
		assertEquals(project3.getTasks().size(),2);
		assertEquals(project3.getAvailableTasks().size(),1);
		assertEquals(project3.getCreationTime(),workdate4);
		assertTrue(project3.isEstimatedOnTime(taskManager.getCurrentTime()));
	
		
		//--------------------------------------------------------------------------
		// Test Project 0 tasks

		TaskView task00 = project0.getTasks().get(0);
		assertTrue(task00.getDescription().equals("TASK 00"));
		assertTrue(task00.hasEnded());
		assertEquals("finished", task00.getStatusAsString().toLowerCase());
		assertEquals(task00.getStartTime(),task00Start);
		assertEquals(task00.getEndTime(),task00End);
		assertTrue(task00.isOnTime(taskManager.getCurrentTime()));
		assertFalse(task00.isUnacceptableOverdue(taskManager.getCurrentTime()));
		assertEquals(task00.getOvertimePercentage(taskManager.getCurrentTime()),0);
		
		//--------------------------------------------------------------------------
		// Test Project 1 tasks
		
		TaskView task10 = project1.getTasks().get(0);
		assertTrue(task10.getDescription().equals("TASK 10"));
		assertTrue(task10.hasEnded());
		assertEquals("finished", task10.getStatusAsString().toLowerCase());
		assertEquals(task10.getStartTime(),task10Start);
		assertEquals(task10.getEndTime(),task10End);
		assertFalse(task10.isUnacceptableOverdue(taskManager.getCurrentTime()));
		assertEquals(task10.getOvertimePercentage(taskManager.getCurrentTime()),0);
		
		TaskView task11 = project1.getTasks().get(1);
		assertTrue(task11.getDescription().equals("TASK 11"));
		assertTrue(task11.hasEnded());
		assertEquals("failed", task11.getStatusAsString().toLowerCase());
		assertEquals(task11.getStartTime(),task11Start);
		assertEquals(task11.getEndTime(),task11End);
		assertFalse(task11.isUnacceptableOverdue(taskManager.getCurrentTime()));		// !!!!!!!
		assertEquals(task11.getOvertimePercentage(taskManager.getCurrentTime()),0);		// !!!!!!!
		
		TaskView task12 = project1.getTasks().get(2);
		assertTrue(task12.getDescription().equals("TASK 12"));
		assertFalse(task12.hasEnded());
		assertEquals("unavailable", task12.getStatusAsString().toLowerCase());
		assertEquals(task12.getStartTime(),null);
		assertEquals(task12.getEndTime(),null);
		assertFalse(task12.isOnTime(taskManager.getCurrentTime()));
		assertTrue(task12.isUnacceptableOverdue(taskManager.getCurrentTime()));
		assertTrue(task12.getOvertimePercentage(taskManager.getCurrentTime()) > 0);

		TaskView task13 = project1.getTasks().get(3);
		assertTrue(task13.getDescription().equals("TASK 13"));
		assertFalse(task13.hasEnded());
		assertEquals("available", task13.getStatusAsString().toLowerCase());
		assertEquals(task13.getStartTime(),null);
		assertEquals(task13.getEndTime(),null);
		assertTrue(task13.isUnacceptableOverdue(taskManager.getCurrentTime()));
		assertTrue(task13.getOvertimePercentage(taskManager.getCurrentTime()) > 0);
		
		//--------------------------------------------------------------------------
		// Test Project 2 tasks

		TaskView task20 = project2.getTasks().get(0);
		assertTrue(task20.getDescription().equals("TASK 20"));
		assertFalse(task20.hasEnded());
		assertEquals(task20.getStartTime(),null);
		assertEquals(task20.getEndTime(),null);
//		assertTrue(task20.isTaskOnTime(taskManager.getCurrentTime()));
//		assertFalse(task20.isTaskUnacceptableOverdue(taskManager.getCurrentTime()));			// !!!!!
//		assertEquals(task20.getTaskOvertimePercentage(taskManager.getCurrentTime()),0);
		
		//--------------------------------------------------------------------------
		// Test Project 3 tasks

		TaskView task30 = project3.getTasks().get(0);
		assertTrue(task30.getDescription().equals("TASK 30"));
		assertTrue(task30.hasEnded());
		assertEquals(task30.getStartTime(),task30Start);
		assertEquals(task30.getEndTime(),task30End);
//		assertFalse(task30.isTaskOnTime(taskManager.getCurrentTime()));
//		assertTrue(task30.isTaskUnacceptableOverdue(taskManager.getCurrentTime()));
		
		TaskView task31 = project3.getTasks().get(1);
		assertTrue(task31.getDescription().equals("TASK 31"));
		assertFalse(task31.hasEnded());
		assertEquals(task31.getStartTime(),null);
		assertEquals(task31.getEndTime(),null);
//		assertTrue(task31.isTaskOnTime(taskManager.getCurrentTime()));
//		assertFalse(task31.isTaskUnacceptableOverdue(taskManager.getCurrentTime()));
//		assertEquals(task31.getTaskOvertimePercentage(taskManager.getCurrentTime()),0);
		
	}
	
	@Test(expected=IndexOutOfBoundsException.class)
	public void badInputCasetest() {
		List<ProjectView> projects = taskManager.getProjects();
		
		// Stap 1 is impliciet
		// Stap 2
		assertTrue(projects.size() == 4);
		// Stap 3
		assertEquals(null,projects.get(4));
//		assertEquals(taskManager.getProjectDescription(4),null); //TODO is eigenlijk null testen
//		assertEquals(taskManager.getProjectDueTime(4),null);
//		assertEquals(taskManager.getProjectName(4),null);
//		assertEquals(taskManager.getProjectStatus(4),null);
//		assertEquals(taskManager.getProjectEndTime(4),null);
//		assertEquals(taskManager.getTaskAmount(4),-1);
//		assertEquals(taskManager.getAvailableTasks(4),null);
//		assertEquals(taskManager.getProjectCreationTime(4),null);
//		assertFalse(taskManager.isProjectEstimatedOnTime(4));
//
//		assertEquals(taskManager.getTaskDescription(4, 0),null);
//		assertFalse(taskManager.hasTaskEnded(4, 0));
//		assertEquals(taskManager.getTaskStartTime(4, 0),null);
//		assertEquals(taskManager.getTaskEndTime(4, 0),null);
//		assertFalse(taskManager.isTaskOnTime(4, 0));
//		assertFalse(taskManager.isTaskUnacceptableOverdue(4, 0));
//		assertEquals(taskManager.getTaskOverTimePercentage(4, 0),-1);
//		
//		assertEquals(taskManager.getTaskDescription(3, 5),null);
//		assertFalse(taskManager.hasTaskEnded(3, 5));
//		assertEquals(taskManager.getTaskStartTime(3, 5),null);
//		assertEquals(taskManager.getTaskEndTime(3, 5),null);
//		assertFalse(taskManager.isTaskOnTime(3, 5));
//		assertFalse(taskManager.isTaskUnacceptableOverdue(3, 5));
//		assertEquals(taskManager.getTaskOverTimePercentage(3, 5),-1);
		
		
	}

}
