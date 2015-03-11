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
			project3DueDate = LocalDateTime.of(2015, 2, 21, 23, 59);
	private final int task00EstDur = 60,
			task20EstDur = 35;
	private final int task00Dev = 0,
			task20Dev = 50; // Moet nog steeds delayed project geven!
	private final ArrayList<Integer> task00Dependencies = new ArrayList(),
			task02Dependencies = new ArrayList();

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

		taskMan.createProject("Test1", "testing 1", project0DueDate);


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
