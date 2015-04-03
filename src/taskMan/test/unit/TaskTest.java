package taskMan.test.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import taskMan.Task;
import taskMan.resource.ResourceManager;
import taskMan.state.TaskStatus;
import taskMan.state.UnavailableTask;
import taskMan.util.TimeSpan;

public class TaskTest {

	private Task defaultTest;
	
	private ResourceManager resMan = new ResourceManager();
	
	@Before
	public final void initialize(){
		defaultTest = new Task(1,"test",30,5,resMan,new ArrayList<Task>(),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailID(){
		new Task(-1,"test",30,5,resMan,new ArrayList<Task>(),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailDescription(){
		new Task(1,null,30,5,resMan,new ArrayList<Task>(),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailDurationNegative(){
		new Task(1,"test",-5,5,resMan,new ArrayList<Task>(),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailDurationZero(){
		new Task(1,"test",0,5,resMan,new ArrayList<Task>(),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailDeviation(){
		new Task(1,"test",30,-5,resMan,new ArrayList<Task>(),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailPrerequisitesNull(){
		new Task(1,"test",30,5,resMan,null,null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailPrerequisitesNullInList(){
		ArrayList<Task> temp = new ArrayList<>();
		temp.add(null);
		new Task(1,"test",30,5,resMan,temp,null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTAskConstr1FailResMan(){
		new Task(1,"test",30,5,null,new ArrayList<Task>(),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailPrerequisitesContainsAlt(){
		defaultTest.setFailed(LocalDateTime.of(2015, 2, 11, 16, 0), 
				LocalDateTime.of(2015, 2, 12, 16, 0));
		ArrayList<Task> temp = new ArrayList<>();
		temp.add(defaultTest);
		new Task(2, "aboutToFail", 30, 5, resMan,temp, defaultTest);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailAlternativeNotFailed(){
		Task temp = new Task(1,"test",30,5,resMan,new ArrayList<Task>(),null);
		new Task(1,"test",30,5,resMan,new ArrayList<Task>(),temp);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr2FailBadStatus(){
		new Task(1,"test",30,5,resMan,new ArrayList<Task>(),null,"fail",
				LocalDateTime.of(2015, 2, 11, 16, 0),LocalDateTime.of(2015, 2, 12, 16, 0));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr2FailBadTime(){
		new Task(1,"test",30,5,resMan,new ArrayList<Task>(),null,"failed",
				LocalDateTime.of(2015, 2, 12, 16, 0),LocalDateTime.of(2015, 2, 11, 16, 0));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr2FailBadTime2(){
		new Task(1,"test",30,5,resMan,new ArrayList<Task>(),null,"finished",
				LocalDateTime.of(2015, 2, 12, 16, 0),LocalDateTime.of(2015, 2, 11, 16, 0));
	}
	
	@Test
	public void getIDTest(){
		assertEquals(defaultTest.getID(),1);
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
		defaultTest.setFailed(LocalDateTime.of(2015, 2, 11, 16, 0),
				LocalDateTime.of(2015, 2, 12, 16, 0));
		Task temp = new Task(2,"test",30,0,resMan,new ArrayList<Task>(),defaultTest);
		assertEquals(temp.getAlternativeFor(), defaultTest);
	}
	
	@Test
	public void setFinishedTest(){
		assertFalse(defaultTest.isFinished());
		defaultTest.setFinished(LocalDateTime.of(2015, 2, 11, 16, 0),
				LocalDateTime.of(2015, 2, 12, 16, 0));
		assertTrue(defaultTest.isFinished());
	}
	
	@Test
	public void setFailedTest(){
		assertFalse(defaultTest.isFailed());
		defaultTest.setFinished(LocalDateTime.of(2015, 2, 12, 16, 0),
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
		defaultTest.setFinished(LocalDateTime.of(2015, 2, 11, 16, 0),
				LocalDateTime.of(2015, 2, 12, 16, 0));
		assertTrue(defaultTest.hasEnded());
	}
	
	@Test
	public void testHasEndedFailed(){
		assertFalse(defaultTest.hasEnded());
		defaultTest.setFailed(LocalDateTime.of(2015, 2, 11, 16, 0),
				LocalDateTime.of(2015, 2, 12, 16, 0));
		assertTrue(defaultTest.hasEnded());
	}
	
	@Test
	public void finishedEndpointSelf(){
		assertFalse(defaultTest.hasFinishedEndpoint());
		defaultTest.setFinished(LocalDateTime.of(2015, 2, 11, 16, 0), 
				LocalDateTime.of(2015, 2, 12, 16, 0));
		assertTrue(defaultTest.hasFinishedEndpoint());
	}
	
	@Test
	public void finishedEndpointOther(){
		assertFalse(defaultTest.hasFinishedEndpoint());
		defaultTest.setFailed(LocalDateTime.of(2015, 2, 11, 16, 0), 
				LocalDateTime.of(2015, 2, 12, 16, 0));
		Task alt = new Task(2, "alternative", 80, 5, resMan, defaultTest.getPrerequisites(), defaultTest);
		alt.setFinished(LocalDateTime.of(2015, 2, 13, 16, 0), 
				LocalDateTime.of(2015, 2, 14, 16, 0));
		System.out.println(defaultTest.isFinished());
		assertTrue(defaultTest.hasFinishedEndpoint());
	}
}
