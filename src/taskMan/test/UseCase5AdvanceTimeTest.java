package taskMan.test;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.Before;
import org.junit.Test;

import taskMan.TaskMan;

public class UseCase5AdvanceTimeTest {
	
	private TaskMan taskMan;
	private final int hoursIncGood = 240, minutesIncGood = 40;
	private final int hoursIncBad1 = 0, minutesIncBad1 = 0;
	private final int hoursIncBad2 = -10, minutesIncBad2 = 50;
	private final int hoursIncBad3 = 50, minutesIncBad3 = -10;
	private final int hoursIncBad4 = 240, minutesIncBad4 = 40; //TODO: meer invalid stamps?
	private final int hoursIncBad5 = 240, minutesIncBad5 = 40;
	private final int hoursIncBad6 = 240, minutesIncBad6 = 40;
	private final LocalDateTime startDate = LocalDateTime.of(2015, 2, 9, 0, 0),
								project1StartDate = startDate,
								project1DueDate = LocalDateTime.of(2015, 2, 15, 23, 59),
								project2StartDate = LocalDateTime.of(2015, 2, 11, 0, 0),
								project2DueDate = LocalDateTime.of(2015, 2, 11, 0, 0);
	private final LocalTime task11EstDur = LocalTime.of(4, 30),
							task12EstDur = LocalTime.of(15,0),
							task13EstDur = LocalTime.of(1, 45),
							task21EstDur = LocalTime.of(6, 0),
							task22EstDur = LocalTime.of(120, 30),
							task23EstDur = LocalTime.of(2, 0);
	private final LocalDateTime newDate = LocalDateTime.of(2015, 2, 9, hoursIncGood, minutesIncGood);
	
	/**
	 * DEFAULT TASKMAN TESTER
	 * - project 1 START 10 feb DUE 15 feb (midnight)
	 * 		task 1
	 */
	@Before
	public final void initialize() {
		taskMan = new TaskMan(startDate);
		taskMan.createProject("Test1", "testing 1", project1StartDate, project1DueDate);
		taskMan.createProject("Test2", "testing 2", project2StartDate, project2DueDate);
		taskMan.createTask(1, "P1 Task 1", task11EstDur, acceptableDeviation, taskStatus, alternativeFor, prerequisiteTasks, startTime, endTime)
		
	}
	
	@Test
	public void SuccesCaseTest() {
		// Step 1 and 2 are implicit
		// Step 3 assumption: the user inputs CORRECT data
		LocalTime inc = LocalTime.of(hoursIncGood, minutesIncGood);
		taskMan.advanceTime(inc);
		assertEquals(taskMan.getCurrentTime(),supposedToBeNewDate);	
		// Step 4
		// TODO: test step 4
	}
	
	@Test
	public void flow3aTest() {
		// Step 1 and 2 are implicit
		// Step 3 assumption: the user inputs NO data
		LocalTime inc = null;
		taskMan.advanceTime(inc);
		assertEquals(taskMan.getCurrentTime(),startDate);
	}
	
	@Test
	public void flow4aTest() {
		// Step 1 and 2 are implicit
		// Step 3 assumption: the user inputs INVALID data
		LocalTime inc1 = LocalTime.of(hoursIncBad1, minutesIncBad1);
		taskMan.advanceTime(inc1);
		assertEquals(taskMan.getCurrentTime(),startDate);

		LocalTime inc2 = LocalTime.of(hoursIncBad2, minutesIncBad2);
		taskMan.advanceTime(inc2);
		assertEquals(taskMan.getCurrentTime(),startDate);

		LocalTime inc3 = LocalTime.of(hoursIncBad3, minutesIncBad3);
		taskMan.advanceTime(inc3);
		assertEquals(taskMan.getCurrentTime(),startDate);

		LocalTime inc4 = LocalTime.of(hoursIncBad4, minutesIncBad4);
		taskMan.advanceTime(inc4);
		assertEquals(taskMan.getCurrentTime(),startDate);

		LocalTime inc5 = LocalTime.of(hoursIncBad5, minutesIncBad5);
		taskMan.advanceTime(inc5);
		assertEquals(taskMan.getCurrentTime(),startDate);

		LocalTime inc6 = LocalTime.of(hoursIncBad6, minutesIncBad6);
		taskMan.advanceTime(inc6);
		assertEquals(taskMan.getCurrentTime(),startDate);
	}

}
