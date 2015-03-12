package taskMan.test;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import taskMan.TaskMan;

public class UseCase1ShowProjectsTest {

	private TaskMan taskMan;
	private final LocalDateTime startDate = LocalDateTime.of(2015, 2, 9, 8, 0),
			workdate1 = LocalDateTime.of(2015, 2, 11, 16, 0),
			workdate2 = LocalDateTime.of(2015, 2, 12, 16, 0),
			workdate3 = LocalDateTime.of(2015, 2, 13, 16, 0),
			workdate4 = LocalDateTime.of(2015, 2, 15, 16, 0),
			project0DueDate = LocalDateTime.of(2015, 2, 13, 23, 59),
			project1DueDate = LocalDateTime.of(2015, 2, 21, 23, 59),
			project2DueDate = LocalDateTime.of(2015, 2, 15, 23, 59),
			project3DueDate = LocalDateTime.of(2015, 2, 21, 23, 59),
			task00Start = startDate,
			task00End = LocalDateTime.of(2015, 2, 9, 10, 0);
	private final int task00EstDur = 120,
			task20EstDur = 35*60;
	private final int task00Dev = 5,
			task20Dev = 50; // Moet nog steeds delayed project geven!
	private final ArrayList<Integer> task00Dependencies = new ArrayList<Integer>(),
			task20Dependencies = new ArrayList<Integer>();
	
	@Before
	public final void initialize() {
		// INIT systeem en maak het eerste project aan, samen met zijn TASK
		taskMan = new TaskMan(startDate);
			taskMan.createProject("Project 0", "Describing proj 0", project0DueDate);
				taskMan.createTask(0, "Task 00", task00EstDur, task00Dev, -1, task00Dependencies);
				
		// Stap verder:
		// maak het tweede project aan en maak zijn TASK lijst
		taskMan.advanceTimeTo(workdate1);
			taskMan.createProject("Project 1", "Describing proj 1", project1DueDate);
				//TODO maak project 1 takensysteem
		
		// Stap verder:
		// maak het derde project aan, samen met zijn TASK
		taskMan.advanceTimeTo(workdate2);
			taskMan.createProject("Project 2", "Describing project 2", project2DueDate);
				taskMan.createTask(2, "Task 20", task20EstDur, task20Dev, -1, task20Dependencies);
				
		// Stap verder:
		// maak TASK 0,0 af -> project 1 is finished
		taskMan.advanceTimeTo(workdate3);
			taskMan.setTaskFinished(0, 0, task00Start, task00End);
			
		// Stap verder:
		// maak het vierde project aan en maak zijn TASK lijst
		taskMan.advanceTimeTo(workdate3);
			taskMan.createProject("Project 3", "Describing project 3", project3DueDate);
				//TODO maak project 3 takensyteem

	}

	//TODO verwacht: PROJ0 FINISHED, PROJ2 DELAYED, PROJ1,3 ONGOING
	
	@Test
	public void SuccesCasetest() {
		//TODO
		
	}

}
