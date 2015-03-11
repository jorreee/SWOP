package taskMan.test;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import taskMan.TaskMan;

public class UseCase2CreateProjectTest {

	private TaskMan taskMan;
	private final LocalDateTime startDate = LocalDateTime.of(2015, 2, 9, 8, 0),
			newDateNoChanges = LocalDateTime.of(2015, 2, 9, 10, 0),
			newDateWithChanges = LocalDateTime.of(2015, 3, 9, 10, 0),
			newDateVeryBad1 = null,
			newDateVeryBad2 = LocalDateTime.of(2015, 2, 8, 0, 0),
			newDateVeryBad3 = startDate,
			project0StartDate = startDate,
			project0DueDate = LocalDateTime.of(2015, 2, 13, 23, 59);
	private final int task00EstDur = 8*60,
			task01EstDur = 16*60,
			task02EstDur = 8*60,
			task03EstDur = 8*60;
	private final int task00Dev = 0,
			task01Dev = 50,
			task02Dev = 0,
			task03Dev = 0;
	private final ArrayList<Integer> task00Dependencies = new ArrayList(),
			task01Dependencies = new ArrayList(),
			task02Dependencies = new ArrayList(),
			task03Dependencies = new ArrayList();

	/**
	 * DEFAULT TASKMAN TESTER
	 * - project 1 START 9 feb DUE 13 feb (midnight)
	 * 		task 1			
	 * 		task 2 <- 1
	 * 		task 3 <- 2
	 * 		task 4 <- 2
	 */
	@Before
	public final void initialize() {
		taskMan = new TaskMan(startDate);

		taskMan.createProject("Test1", "testing 1", project0StartDate, project0DueDate);


		taskMan.createTask(0, "Design system", task00EstDur, task00Dev, -1, task00Dependencies);		// TASK 1
		task01Dependencies.add(Integer.valueOf(1));
		taskMan.createTask(0, "Implement Native", task01EstDur, task01Dev, -1, task01Dependencies);	// TASK 2
		task02Dependencies.add(Integer.valueOf(2));
		taskMan.createTask(0, "Test code", task02EstDur, task02Dev, -1, task02Dependencies);			// TASK 3
		task03Dependencies.add(Integer.valueOf(2));
		taskMan.createTask(0, "Document code", task03EstDur, task03Dev, -1, task03Dependencies);		// TASK 4

	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
