package taskMan.test.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;




import org.junit.Before;
import org.junit.Test;

import taskMan.Task;
import taskMan.resource.ResourceManager;
import taskMan.state.AvailableTask;
import taskMan.state.ExecutingTask;
import taskMan.state.TaskStatus;
import taskMan.state.UnavailableTask;
import taskMan.util.TimeSpan;
import taskMan.view.ResourceView;

public class TaskTest {

	private Task defaultTest;
	private Task TaskAsPrerequisite;
	//These Task are dependent of the previous Task
	private Task TaskDep1;
	private Task TaskDep2;
	
	private ResourceManager resMan = new ResourceManager();
	
	@Before
	public final void initialize(){
		defaultTest = new Task(1,"test",30,5,resMan, new ArrayList<Task>(),new HashMap<ResourceView,Integer>(),null);
		TaskAsPrerequisite = new Task(1,"PreTask",30,5,resMan,new ArrayList<Task>(),new HashMap<ResourceView,Integer>(),null);
		ArrayList<Task> pre = new ArrayList<>();
		pre.add(TaskAsPrerequisite);
		TaskDep1 = new Task(2,"Dep1",30,5,resMan,pre,new HashMap<ResourceView,Integer>(),null);
		TaskDep2 = new Task(3,"Dep2",30,5,resMan,pre,new HashMap<ResourceView,Integer>(),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailID(){
		new Task(-1,"test",30,5,resMan,new ArrayList<Task>(),new HashMap<ResourceView,Integer>(),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailDescription(){
		new Task(1,null,30,5,resMan,new ArrayList<Task>(),new HashMap<ResourceView,Integer>(),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailDurationNegative(){
		new Task(1,"test",-5,5,resMan,new ArrayList<Task>(),new HashMap<ResourceView,Integer>(),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailDurationZero(){
		new Task(1,"test",0,5,resMan,new ArrayList<Task>(),new HashMap<ResourceView,Integer>(),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailDeviation(){
		new Task(1,"test",30,-5,resMan,new ArrayList<Task>(),new HashMap<ResourceView,Integer>(),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailPrerequisitesNull(){
		new Task(1,"test",30,5,resMan,null,new HashMap<ResourceView,Integer>(),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailPrerequisitesNullInList(){
		ArrayList<Task> temp = new ArrayList<>();
		temp.add(null);
		new Task(1,"test",30,5,resMan,temp,new HashMap<ResourceView,Integer>(),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTAskConstr1FailResMan(){
		new Task(1,"test",30,5,null,new ArrayList<Task>(),new HashMap<ResourceView,Integer>(),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailPrerequisitesContainsAlt(){
		defaultTest.setBeginTime(LocalDateTime.of(2015, 2, 12, 14, 0));
		defaultTest.fail(LocalDateTime.of(2015, 2, 12, 16, 0));
		ArrayList<Task> temp = new ArrayList<>();
		temp.add(defaultTest);
		new Task(2, "aboutToFail", 30, 5, resMan,temp,new HashMap<ResourceView,Integer>(), defaultTest);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailAlternativeNotFailed(){
		Task temp = new Task(1,"test",30,5,resMan,new ArrayList<Task>(),new HashMap<ResourceView,Integer>(),null);
		new Task(1,"test",30,5,resMan,new ArrayList<Task>(),new HashMap<ResourceView,Integer>(),temp);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr2FailBadStatus(){
		new Task(1,"test",30,5,resMan,new ArrayList<Task>(),new HashMap<ResourceView,Integer>(),null,"fail",
				LocalDateTime.of(2015, 2, 11, 16, 0),LocalDateTime.of(2015, 2, 12, 16, 0),null,null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr2FailBadTime(){
		new Task(1,"test",30,5,resMan,new ArrayList<Task>(),new HashMap<ResourceView,Integer>(),null,"failed",
				LocalDateTime.of(2015, 2, 12, 16, 0),LocalDateTime.of(2015, 2, 11, 16, 0),null,null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr2FailBadTime2(){
		new Task(1,"test",30,5,resMan,new ArrayList<Task>(),new HashMap<ResourceView,Integer>(),null,"finished",
				LocalDateTime.of(2015, 2, 12, 16, 0),LocalDateTime.of(2015, 2, 11, 16, 0),null,null);
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
		assertEquals(defaultTest.getStatus().toLowerCase(),"unavailable");
	}
	
	@Test
	public void getSetAltTest(){
		defaultTest.plan(LocalDateTime.of(2015, 2, 11, 16, 0));
		defaultTest.execute(LocalDateTime.of(2015, 2, 11, 16, 0));
		defaultTest.fail(LocalDateTime.of(2015, 2, 12, 16, 0));
		Task temp = new Task(2,"test",30,0,resMan,new ArrayList<Task>(),new HashMap<ResourceView,Integer>(),defaultTest);
		assertEquals(temp.getAlternativeFor(), defaultTest);
	}
	
	@Test
	public void setFinishedTest(){
		defaultTest.plan(LocalDateTime.of(2015, 2, 12, 15, 0));
		defaultTest.execute(LocalDateTime.of(2015, 2, 12, 15, 0));
		assertFalse(defaultTest.isFinished());
		defaultTest.finish(LocalDateTime.of(2015, 2, 12, 16, 0));
		assertTrue(defaultTest.isFinished());
	}
	
	@Test
	public void setFailedTest(){
		defaultTest.plan(LocalDateTime.of(2015, 2, 12, 15, 0));
		defaultTest.execute(LocalDateTime.of(2015, 2, 12, 15, 0));
		assertFalse(defaultTest.isFailed());
		defaultTest.fail(LocalDateTime.of(2015, 2, 12, 16, 0));
		assertTrue(defaultTest.isFailed());
	}
	
	@Test
	public void testHasEndedFinished(){
		defaultTest.plan(LocalDateTime.of(2015, 2, 12, 15, 0));
		defaultTest.execute(LocalDateTime.of(2015, 2, 12, 15, 0));
		assertFalse(defaultTest.hasEnded());
		defaultTest.finish(LocalDateTime.of(2015, 2, 12, 16, 0));
		assertTrue(defaultTest.hasEnded());
	}
	
	@Test
	public void testHasEndedFailed(){
		defaultTest.plan(LocalDateTime.of(2015, 2, 12, 15, 0));
		defaultTest.execute(LocalDateTime.of(2015, 2, 12, 15, 0));
		assertFalse(defaultTest.hasEnded());
		defaultTest.fail(LocalDateTime.of(2015, 2, 12, 16, 0));
		assertTrue(defaultTest.hasEnded());
	}
	
	@Test
	public void finishedEndpointTestSelf(){
		defaultTest.plan(LocalDateTime.of(2015, 2, 12, 15, 0));
		defaultTest.execute(LocalDateTime.of(2015, 2, 12, 15, 0));
		assertFalse(defaultTest.hasFinishedEndpoint());
		defaultTest.finish(LocalDateTime.of(2015, 2, 12, 16, 0));
		assertTrue(defaultTest.hasFinishedEndpoint());
	}
	
	@Test
	public void finishedEndpointTestOther(){
		defaultTest.plan(LocalDateTime.of(2015, 2, 12, 15, 0));
		defaultTest.execute(LocalDateTime.of(2015, 2, 12, 15, 0));
		assertFalse(defaultTest.hasFinishedEndpoint());
		defaultTest.fail(LocalDateTime.of(2015, 2, 12, 16, 0));
		Task alt = new Task(2, "alternative", 80, 5, resMan, defaultTest.getPrerequisites(),new HashMap<ResourceView,Integer>(), defaultTest);
		alt.plan(LocalDateTime.of(2015, 2, 13, 16, 0));
		alt.execute(LocalDateTime.of(2015, 2, 13, 16, 0));
		assertFalse(defaultTest.hasFinishedEndpoint());
		alt.finish(LocalDateTime.of(2015, 2, 14, 16, 0));
		assertTrue(defaultTest.hasFinishedEndpoint());
	}
	
	@Test
	public void finishedEndpointTestNoReplacement(){
		defaultTest.setBeginTime(LocalDateTime.of(2015, 2, 11, 16, 0));
		assertFalse(defaultTest.hasFinishedEndpoint());
		defaultTest.fail(LocalDateTime.of(2015, 2, 12, 16, 0));
		assertFalse(defaultTest.hasFinishedEndpoint());
	}
	
	@Test
	public void replaceWithTestNotFailed(){
		Task temp = new Task(2,"temp",20,3,new ResourceManager(),new ArrayList<>(),new HashMap<ResourceView,Integer>(),null);
		assertFalse(defaultTest.isFailed());
		assertFalse(defaultTest.replaceWith(temp));
	}
	
	@Test
	public void replaceWithTestNull(){
		defaultTest.plan(LocalDateTime.of(2015, 2, 11, 16, 0));
		defaultTest.execute(LocalDateTime.of(2015, 2, 11, 16, 0));
		defaultTest.fail(LocalDateTime.of(2015, 2, 12, 16, 0));
		Task temp = new Task(2,"temp",20,3,new ResourceManager(),new ArrayList<>(),new HashMap<ResourceView,Integer>(),null);
		Task temp2 = new Task(3,"temp",20,3,new ResourceManager(),new ArrayList<>(),new HashMap<ResourceView,Integer>(),null);
		assertTrue(defaultTest.replaceWith(temp));
		assertFalse(defaultTest.replaceWith(temp2));
	}
	
	@Test
	public void setBeginTimeTestNull(){
		assertFalse(defaultTest.setBeginTime(null));
	}
	
	@Test
	public void setBeginTimeTestAlreadySet(){
		defaultTest.plan(LocalDateTime.of(2015, 2, 11, 16, 0));
		assertTrue(defaultTest.setBeginTime(LocalDateTime.of(2015, 2, 11, 16, 0)));
		assertFalse(defaultTest.setBeginTime(LocalDateTime.of(2015, 2, 14, 16, 0)));
	}
	
	@Test
	public void setEndTimeTestNull(){
		assertFalse(defaultTest.setEndTime(null));
	}
	
	@Test
	public void setEndTimeTestAlreadySet(){
		defaultTest.plan(LocalDateTime.of(2015, 2, 12, 15, 0));
		defaultTest.execute(LocalDateTime.of(2015, 2, 12, 15, 0));
		assertTrue(defaultTest.fail(LocalDateTime.of(2015, 2, 12, 16, 0)));
		assertFalse(defaultTest.setEndTime(LocalDateTime.of(2015, 2, 14, 16, 0)));
	}
	
	@Test
	public void registerTestSelf(){
		assertFalse(defaultTest.register(defaultTest));
	}
	
	@Test
	public void registerTestNull(){
		assertFalse(defaultTest.register(null));
	}
	
	@Test
	public void registerTestValid(){
		Task temp = new Task(2,"temp",20,3,new ResourceManager(),new ArrayList<>(),
				new HashMap<ResourceView,Integer>(),null);
		assertTrue(defaultTest.register(temp));
	}
	
	@Test
	public void createTaskWithPrerequisites(){
		ArrayList<Task> pre = new ArrayList<>();
		pre.add(defaultTest);
		Task temp = new Task(2,"temp",20,3,new ResourceManager(),pre,
				new HashMap<ResourceView,Integer>(),null);
		assertEquals(1, temp.getPrerequisites().size());
		assertTrue(temp.getPrerequisites().contains(defaultTest));
	}
	
	@Test
	public void removeAlternativesDepTest(){
		TaskAsPrerequisite.plan(LocalDateTime.of(2015, 2, 11, 16, 0));
		TaskAsPrerequisite.execute(LocalDateTime.of(2015, 2, 11, 16, 0));
		TaskAsPrerequisite.fail(LocalDateTime.of(2015, 2, 12, 16, 0));
		Task temp = new Task(4, "temp", 50, 3, resMan,TaskAsPrerequisite.getPrerequisites(), 
				new HashMap<ResourceView,Integer>(),TaskAsPrerequisite);
		assertEquals(temp,TaskAsPrerequisite.getReplacement());
		assertTrue(temp.getDependants().isEmpty());
		temp.addDependant(TaskDep1);
		assertEquals(1,temp.getDependants().size());
		temp.removeAlternativesDependencies();
		assertTrue(temp.getDependants().isEmpty());
	}
	
	@Test
	public void getTimeSpentTestTaskNotStarted(){
		LocalDateTime currentTime = LocalDateTime.of(2015, 2, 11, 16, 0);
		assertEquals(new TimeSpan(0),defaultTest.getTimeSpent(currentTime));
	}
	
	@Test
	public void getTimeSpentTestNoCurrentTime(){
		LocalDateTime beginTime = LocalDateTime.of(2015, 2, 11, 15, 0);
		defaultTest.setBeginTime(beginTime);
		assertEquals(new TimeSpan(0),defaultTest.getTimeSpent(null));
	}
	
	@Test
	public void getTimeSpentTestStartTimeAfterCurrentTime(){
		LocalDateTime beginTime = LocalDateTime.of(2015, 2, 11, 15, 0);
		LocalDateTime currentTime = LocalDateTime.of(2015, 2, 11, 14, 0);
		defaultTest.setBeginTime(beginTime);
		assertEquals(new TimeSpan(0),defaultTest.getTimeSpent(currentTime));
	}
	
	@Test
	public void getTimeSpentTestTaskHasEnded(){
		LocalDateTime beginTime = LocalDateTime.of(2015, 2, 11, 14, 0);
		LocalDateTime endTime = LocalDateTime.of(2015, 2, 11, 15, 30);
		LocalDateTime currentTime = LocalDateTime.of(2015, 2, 12, 14, 0);
		defaultTest.plan(beginTime);
		defaultTest.execute(beginTime);
		defaultTest.fail(endTime);
		assertEquals(new TimeSpan(90),defaultTest.getTimeSpent(currentTime));
	}
	
	@Test
	public void getTimeSpentTestTaskExecuting(){
		LocalDateTime beginTime = LocalDateTime.of(2015, 2, 11, 14, 0);
		LocalDateTime currentTime = LocalDateTime.of(2015, 2, 11, 15, 0);
		defaultTest.plan(beginTime);
		defaultTest.execute(beginTime);
		assertEquals(new TimeSpan(60),defaultTest.getTimeSpent(currentTime));
	}
	
	@Test
	public void getTimeSpentTestTaskIsAlternativeAndFinsihed(){
		LocalDateTime beginTimeDef = LocalDateTime.of(2015, 2, 11, 14, 0);
		LocalDateTime endTimeDef = LocalDateTime.of(2015, 2, 11, 15, 30);
		LocalDateTime currentTime = LocalDateTime.of(2015, 2, 12, 16, 0);
		defaultTest.plan(beginTimeDef);
		defaultTest.execute(beginTimeDef);
		defaultTest.fail(endTimeDef);
		assertEquals(new TimeSpan(90),defaultTest.getTimeSpent(currentTime));
		
		Task alt = new Task(5,"alt",60,3,resMan,new ArrayList<Task>(),
				new HashMap<ResourceView,Integer>(),defaultTest);
		assertEquals(defaultTest, alt.getAlternativeFor());
		LocalDateTime beginTimeAlt = LocalDateTime.of(2015, 2, 12, 14, 0);
		LocalDateTime endTimeAlt = LocalDateTime.of(2015, 2, 12, 15, 0);
		alt.plan(beginTimeAlt);
		alt.execute(beginTimeAlt);
		alt.finish(endTimeAlt);
		assertEquals(defaultTest, alt.getAlternativeFor());
		assertEquals(new TimeSpan(150),alt.getTimeSpent(currentTime));
	}
	
	@Test
	public void getTimeSpentTestTaskIsAlternativeAndExecuting(){
		LocalDateTime beginTimeDef = LocalDateTime.of(2015, 2, 11, 14, 0);
		LocalDateTime endTimeDef = LocalDateTime.of(2015, 2, 11, 15, 30);
		LocalDateTime currentTime = LocalDateTime.of(2015, 2, 12, 14, 30);
		defaultTest.plan(beginTimeDef);
		defaultTest.execute(beginTimeDef);
		defaultTest.fail(endTimeDef);
		assertEquals(new TimeSpan(90),defaultTest.getTimeSpent(currentTime));
		
		Task alt = new Task(5,"alt",60,3,resMan,new ArrayList<Task>(),
				new HashMap<ResourceView,Integer>(),defaultTest);
		assertEquals(defaultTest, alt.getAlternativeFor());
		LocalDateTime beginTimeAlt = LocalDateTime.of(2015, 2, 12, 14, 0);
		alt.plan(beginTimeAlt);
		alt.execute(beginTimeAlt);
		assertEquals(defaultTest, alt.getAlternativeFor());
		assertEquals(new TimeSpan(120),alt.getTimeSpent(currentTime));
	}
	
	@Test
	public void possibleStartTimesTest(){
		//TODO
		defaultTest.getPossibleTaskStartingTimes(3);
	}
	
	@Test
	public void requiredResourcesTest(){
		//TODO
		defaultTest.getRequiredResources();
	}
	
	@Test
	public void getResourceInstancesTest(){
		//TODO
		defaultTest.getPossibleResourceInstances(null);
	}
	
}
