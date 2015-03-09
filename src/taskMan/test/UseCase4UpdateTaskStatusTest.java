package taskMan.test;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.Before;
import org.junit.Test;

import taskMan.TaskMan;
import taskMan.TaskStatus;

public class UseCase4UpdateTaskStatusTest {
	
	private TaskMan taskMan = new TaskMan();
	private final LocalDateTime goodStartDate = LocalDateTime.of(2015, 2, 10, 8, 0),
								goodEndDate = LocalDateTime.of(2015, 2, 10, 12, 0);
	
	@Before
	public final void initialize() {
		taskMan.createProject("Tests", "Serious testing", 
							LocalDateTime.of(2015, 2, 10, 8, 0), LocalDateTime.of(2015, 3, 10, 16, 0));
		taskMan.createTask(1, "testing", LocalTime.of(4, 0), 100, 
							TaskStatus.AVAILABLE, null, null, 
							null, null);
	}

	@Test
	public void succesCaseTest() {
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		taskMan.updateTaskDetails(1, 1, goodStartDate, goodEndDate, TaskStatus.FINISHED);
		// Step 6
		assertEquals(taskMan.getTaskStatus(),TaskStatus.FINISHED);
		
	}

	@Test
	public void flow3to5aTest() {
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		taskMan.updateTaskDetails(1, 1, null, null, TaskStatus.FINISHED);
		// Step 6
		assertEquals(taskMan.getTaskStatus(),TaskStatus.AVAILABLE);
		
	}

	@Test
	public void flow6aBadStartDateTest() {
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		taskMan.updateTaskDetails(1, 1, badStartDate, goodEndDate, TaskStatus.FINISHED);
		// Step 6
		assertEquals(taskMan.getTaskStatus(),TaskStatus.FINISHED);
		
	}

	@Test
	public void flow6aBadEndDateTest() {
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		taskMan.updateTaskDetails(1, 1, badStartDate, goodEndDate, TaskStatus.FINISHED);
		// Step 6
		assertEquals(taskMan.getTaskStatus(),TaskStatus.FINISHED);
		
	}

	@Test
	public void flow6aBadStatusAVAILABLETest() {
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		taskMan.updateTaskDetails(1, 1, badStartDate, goodEndDate, TaskStatus.FINISHED);
		// Step 6
		assertEquals(taskMan.getTaskStatus(),TaskStatus.FINISHED);
		
	}

	@Test
	public void flow6aBadStatusUNAVAILABLETest() {
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		taskMan.updateTaskDetails(1, 1, badStartDate, goodEndDate, TaskStatus.FINISHED);
		// Step 6
		assertEquals(taskMan.getTaskStatus(),TaskStatus.FINISHED);
		
	}

	@Test
	public void flow6aBadStatusFAILEDTest() {
		// Step 1 is implicit
		// Step 2 and 3 are handled in UI
		// Step 4 and 5
		taskMan.updateTaskDetails(1, 1, badStartDate, goodEndDate, TaskStatus.FINISHED);
		// Step 6
		assertEquals(taskMan.getTaskStatus(),TaskStatus.FINISHED);
		
	}

}
