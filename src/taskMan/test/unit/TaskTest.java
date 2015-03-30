package taskMan.test.unit;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.*;
import static org.junit.Assert.*; 

import taskMan.Task;
import taskMan.util.TimeSpan;
import taskMan.state.*;

public class TaskTest {

	private Task defaultTest;
	
	@Before
	public final void initialize(){
		defaultTest = new Task(1,"test",30,5,new ArrayList<Task>(),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailID(){
		new Task(-1,"test",30,5,new ArrayList<Task>(),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailDescription(){
		new Task(1,null,30,5,new ArrayList<Task>(),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailDurationNegative(){
		new Task(1,"test",-5,5,new ArrayList<Task>(),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailDurationZero(){
		new Task(1,"test",0,5,new ArrayList<Task>(),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailDeviation(){
		new Task(1,"test",30,-5,new ArrayList<Task>(),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailPrerequisitesNull(){
		new Task(1,"test",30,5,null,null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailPrerequisitesNullInList(){
		ArrayList<Task> temp = new ArrayList<>();
		temp.add(null);
		new Task(1,"test",30,5,temp,null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailPrerequisitesContainsAlt(){
		defaultTest.setTaskFailed(LocalDateTime.of(2015, 2, 11, 16, 0), 
				LocalDateTime.of(2015, 2, 12, 16, 0));
		ArrayList<Task> temp = new ArrayList<>();
		temp.add(defaultTest);
		new Task(2, "aboutToFail", 30, 5, temp, defaultTest);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailAlternativeNotFailed(){
		Task temp = new Task(1,"test",30,5,new ArrayList<Task>(),null);
		new Task(1,"test",30,5,new ArrayList<Task>(),temp);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr2FailBadStatus(){
		new Task(1,"test",30,5,new ArrayList<Task>(),null,"fail",
				LocalDateTime.of(2015, 2, 11, 16, 0),LocalDateTime.of(2015, 2, 12, 16, 0));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr2FailBadTime(){
		new Task(1,"test",30,5,new ArrayList<Task>(),null,"failed",
				LocalDateTime.of(2015, 2, 12, 16, 0),LocalDateTime.of(2015, 2, 11, 16, 0));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr2FailBadTime2(){
		new Task(1,"test",30,5,new ArrayList<Task>(),null,"finished",
				LocalDateTime.of(2015, 2, 12, 16, 0),LocalDateTime.of(2015, 2, 11, 16, 0));
	}
	
	@Test
	public void getIDTest(){
		assertEquals(defaultTest.getTaskID(),1);
	}
	
	@Test
	public void getDescriptionTest(){
		assertEquals(defaultTest.getDescription(),"test");
	}
	
	@Test
	public void getDurationTest(){
		assertEquals(defaultTest.getEstimatedDuration(), new TimeSpan(30));
	}
	
	@Test
	public void getDeviationTest(){
		assertEquals(defaultTest.getAcceptableDeviation(), 5);
	}
	
	@Test
	public void getStateString(){
		assertEquals(defaultTest.getStatus().toLowerCase(),"available");
	}
	
	@Test
	public void getSetAltTest(){
		defaultTest.setTaskFailed(LocalDateTime.of(2015, 2, 11, 16, 0),
				LocalDateTime.of(2015, 2, 12, 16, 0));
		Task temp = new Task(2,"test",30,0,new ArrayList<Task>(),defaultTest);
		assertEquals(temp.getAlternativeFor(), defaultTest);
	}
	
	@Test
	public void setFinishedTest(){
		assertFalse(defaultTest.isFinished());
		defaultTest.setTaskFinished(LocalDateTime.of(2015, 2, 11, 16, 0),
				LocalDateTime.of(2015, 2, 12, 16, 0));
		assertTrue(defaultTest.isFinished());
	}
	
	@Test
	public void setFailedTest(){
		assertFalse(defaultTest.isFailed());
		defaultTest.setTaskFinished(LocalDateTime.of(2015, 2, 12, 16, 0),
				LocalDateTime.of(2015, 2, 11, 16, 0));
		assertFalse(defaultTest.isFailed());
	}
	
	@Test
	public void setUnavailableTest(){
		assertTrue(defaultTest.isAvailable());
		TaskStatus newStatus = new UnavailableTask(defaultTest);
		defaultTest.setTaskStatus(newStatus);
		assertTrue(defaultTest.isUnavailable());
	}
	
	@Test
	public void testHasEndedFinished(){
		assertFalse(defaultTest.hasEnded());
		defaultTest.setTaskFinished(LocalDateTime.of(2015, 2, 11, 16, 0),
				LocalDateTime.of(2015, 2, 12, 16, 0));
		assertTrue(defaultTest.hasEnded());
	}
	
	@Test
	public void testHasEndedFailed(){
		assertFalse(defaultTest.hasEnded());
		defaultTest.setTaskFailed(LocalDateTime.of(2015, 2, 11, 16, 0),
				LocalDateTime.of(2015, 2, 12, 16, 0));
		assertTrue(defaultTest.hasEnded());
	}
}
