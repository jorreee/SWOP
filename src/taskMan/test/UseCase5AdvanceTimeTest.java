package taskMan.test;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import taskMan.TaskMan;

public class UseCase5AdvanceTimeTest {

	private TaskMan taskMan;
	private final LocalDateTime startDate = LocalDateTime.of(2015, 2, 9, 0, 0),
			newDateGood = LocalDateTime.of(2015, 2, 15, 16, 0),
			newDateVeryBad1 = LocalDateTime.of(2015, 2, 8, 0, 0),
			newDateVeryBad2 = null,
			project1StartDate = startDate,
			project1DueDate = LocalDateTime.of(2015, 2, 15, 23, 59),
			project2StartDate = LocalDateTime.of(2015, 2, 11, 0, 0),
			project2DueDate = LocalDateTime.of(2015, 2, 11, 0, 0);
	private final int task11EstDur = 4*60 + 30,
			task12EstDur = 15*60,
			task13EstDur = 105,
			task21EstDur = 360,
			task22EstDur = 120*60 + 30,
			task23EstDur = 120;
	private final int task11Dev = 10,
			task12Dev = 50,
			task13Dev = 0,
			task21Dev = 0,
			task22Dev = 1,
			task23Dev = 100;
	private final ArrayList<Integer> task13Dependencies = new ArrayList();
	private final ArrayList<Integer> task23Dependencies = new ArrayList();

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

		task13Dependencies.add(Integer.valueOf(1));
		taskMan.createTask(1, "P1 Task 1", task11EstDur, task11Dev, null, null);
		taskMan.createTask(1, "P1 Task 2", task12EstDur, task12Dev, null, null);
		taskMan.createTask(1, "P1 Task 3", task13EstDur, task13Dev, null, task13Dependencies);
		//
		task13Dependencies.add(Integer.valueOf(1),Integer.valueOf(2));
		taskMan.createTask(1, "P2 Task 1", task21EstDur, task21Dev, null, null);
		taskMan.createTask(1, "P2 Task 2", task22EstDur, task22Dev, null, null);
		taskMan.createTask(1, "P2 Task 3", task23EstDur, task23Dev, null, task23Dependencies);

	}

	@Test
	public void SuccesCaseTest() {
		// Step 1 and 2 are implicit
		// Step 3 assumption: the user inputs CORRECT data
		taskMan.advanceTime(newDateGood);
		assertEquals(taskMan.getCurrentTime(),newDateGood);	
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
		taskMan.advanceTime(newDateVeryBad1);
		assertEquals(taskMan.getCurrentTime(),startDate);

		taskMan.advanceTime(newDateVeryBad2);
		assertEquals(taskMan.getCurrentTime(),startDate);
	}

}
