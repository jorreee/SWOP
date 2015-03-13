package taskMan.test;

import org.junit.Test;
import static org.junit.Assert.*;

import taskMan.Task;
import taskMan.util.TimeSpan;

public class TaskTest {
	
	private final TimeSpan extraTimeGood = new TimeSpan(50);

	@Test()
	public void contructorSuccesTest() {
		Task goodTask = new Task(1, "Descr", 5*60, 10, extraTimeGood);
		assertEquals(1,goodTask.getTaskID());
		assertEquals("Descr",goodTask.getDescription());
		assertEquals(new TimeSpan(5*60),goodTask.getEstimatedDuration());
		assertEquals(10,goodTask.getAcceptableDeviation());
	}

}
