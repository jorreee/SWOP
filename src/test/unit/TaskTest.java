package test.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import company.taskMan.resource.ResourceManager;
import company.taskMan.resource.ResourcePrototype;
import company.taskMan.resource.ResourceView;
import company.taskMan.task.Task;
import company.taskMan.util.TimeSpan;
import exceptions.ResourceUnavailableException;

public class TaskTest {

	private Task defaultTest;
	private Task TaskAsPrerequisite;
	
	private ResourceManager resMan;
	private List<ResourceView> concreteResDef, devList;
	private Map<ResourceView,Integer> resDef;
	private ResourceView carDef;	//car0
	private ResourceView boardDef;	//board0
	private ResourceView weer, blunderbus;
	
	private final Optional<LocalTime> emptyAvailabilityPeriodStart = Optional.empty(),
			emptyAvailabilityPeriodEnd = Optional.empty();
	
	@Before
	public final void initialize(){
		//Prepare the resources and developers
		resMan = new ResourceManager(new ArrayList<ResourcePrototype>());
		resMan.createResourcePrototype("car", emptyAvailabilityPeriodStart, emptyAvailabilityPeriodEnd);
		resMan.createResourcePrototype("whiteboard",  emptyAvailabilityPeriodStart, emptyAvailabilityPeriodEnd);
		for(int i = 0;i<5;i++){
			resMan.declareConcreteResource("car" + i, resMan.getResourcePrototypes().get(0));
		}
		for(int i = 0;i<5;i++){
			resMan.declareConcreteResource("whiteboard" + i, resMan.getResourcePrototypes().get(1));
		}
		resMan.createDeveloper("Weer");
		resMan.createDeveloper("Blunderbus");
		weer = resMan.getDeveloperList().get(0);
		blunderbus = resMan.getDeveloperList().get(1);
		
		//Set up the default
		resDef = new HashMap<ResourceView,Integer>();
		resDef.put(resMan.getResourcePrototypes().get(0), 1);
		resDef.put(resMan.getResourcePrototypes().get(1), 1);
		carDef = resMan.getConcreteResourcesForPrototype(resMan.getResourcePrototypes().get(0)).get(0);
		boardDef = resMan.getConcreteResourcesForPrototype(resMan.getResourcePrototypes().get(1)).get(0);
		concreteResDef = new ArrayList<>();
		concreteResDef.add(carDef);
		concreteResDef.add(boardDef);
		devList = new ArrayList<ResourceView>();
		devList.add(weer);
		devList.add(blunderbus);
		defaultTest = new Task("test",30,5,resMan, new ArrayList<Task>(),resDef,null);
		TaskAsPrerequisite = new Task("PreTask",30,5,resMan,new ArrayList<Task>(),new HashMap<ResourceView,Integer>(),null);
		ArrayList<Task> pre = new ArrayList<>();
		pre.add(TaskAsPrerequisite);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailDescription(){
		new Task(null,30,5,resMan,new ArrayList<Task>(),new HashMap<ResourceView,Integer>(),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailDurationNegative(){
		new Task("test",-5,5,resMan,new ArrayList<Task>(),new HashMap<ResourceView,Integer>(),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailDurationZero(){
		new Task("test",0,5,resMan,new ArrayList<Task>(),new HashMap<ResourceView,Integer>(),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailDeviation(){
		new Task("test",30,-5,resMan,new ArrayList<Task>(),new HashMap<ResourceView,Integer>(),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailPrerequisitesNull(){
		new Task("test",30,5,resMan,null,new HashMap<ResourceView,Integer>(),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailPrerequisitesNullInList(){
		ArrayList<Task> temp = new ArrayList<>();
		temp.add(null);
		new Task("test",30,5,resMan,temp,new HashMap<ResourceView,Integer>(),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTAskConstr1FailResMan(){
		new Task("test",30,5,null,new ArrayList<Task>(),new HashMap<ResourceView,Integer>(),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr1FailAlternativeNotFailed(){
		Task temp = new Task("test",30,5,resMan,new ArrayList<Task>(),new HashMap<ResourceView,Integer>(),null);
		new Task("test",30,5,resMan,new ArrayList<Task>(),new HashMap<ResourceView,Integer>(),temp);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr2FailBadStatus() throws ResourceUnavailableException, IllegalArgumentException{
		new Task("test",30,5,resMan,new ArrayList<Task>(),new HashMap<ResourceView,Integer>(),null,"fail",
				LocalDateTime.of(2015, 2, 11, 16, 0),LocalDateTime.of(2015, 2, 12, 16, 0),LocalDateTime.of(2015, 2, 12, 16, 0),null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr2FailBadTime() throws ResourceUnavailableException, IllegalArgumentException{
		new Task("test",30,5,resMan,new ArrayList<Task>(),new HashMap<ResourceView,Integer>(),null,"failed",
				LocalDateTime.of(2015, 2, 12, 16, 0),LocalDateTime.of(2015, 2, 11, 16, 0),LocalDateTime.of(2015, 2, 12, 16, 0),new ArrayList<ResourceView>());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createTaskConstr2FailBadTime2() throws ResourceUnavailableException, IllegalArgumentException{
		new Task("test",30,5,resMan,new ArrayList<Task>(),new HashMap<ResourceView,Integer>(),null,"finished",
				LocalDateTime.of(2015, 2, 12, 16, 0),LocalDateTime.of(2015, 2, 11, 16, 0),LocalDateTime.of(2015, 2, 12, 16, 0),new ArrayList<ResourceView>());
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
	public void getSetAltTest() throws ResourceUnavailableException, IllegalArgumentException, IllegalStateException{
		defaultTest.plan(LocalDateTime.of(2015, 2, 11, 16, 0),concreteResDef,devList);
		defaultTest.execute(LocalDateTime.of(2015, 2, 11, 16, 0));
		defaultTest.fail(LocalDateTime.of(2015, 2, 12, 16, 0));
		Task temp = new Task("test",30,0,resMan,new ArrayList<Task>(),new HashMap<ResourceView,Integer>(),defaultTest);
		assertEquals(temp.getAlternativeFor(), defaultTest);
	}
	
	@Test
	public void setFinishedTest() throws ResourceUnavailableException, IllegalArgumentException, IllegalStateException{
		defaultTest.plan(LocalDateTime.of(2015, 2, 12, 15, 0),concreteResDef,devList);
		defaultTest.execute(LocalDateTime.of(2015, 2, 12, 15, 0));
		assertFalse(defaultTest.isFinished());
		defaultTest.finish(LocalDateTime.of(2015, 2, 12, 16, 0));
		assertTrue(defaultTest.isFinished());
	}
	
	@Test
	public void setFailedTest() throws ResourceUnavailableException, IllegalArgumentException, IllegalStateException{
		defaultTest.plan(LocalDateTime.of(2015, 2, 12, 15, 0),concreteResDef,devList);
		defaultTest.execute(LocalDateTime.of(2015, 2, 12, 15, 0));
		assertFalse(defaultTest.isFailed());
		defaultTest.fail(LocalDateTime.of(2015, 2, 12, 16, 0));
		assertTrue(defaultTest.isFailed());
	}
	
	@Test
	public void testHasEndedFinished() throws ResourceUnavailableException, IllegalArgumentException, IllegalStateException{
		defaultTest.plan(LocalDateTime.of(2015, 2, 12, 15, 0),concreteResDef,devList);
		defaultTest.execute(LocalDateTime.of(2015, 2, 12, 15, 0));
		assertFalse(defaultTest.hasEnded());
		defaultTest.finish(LocalDateTime.of(2015, 2, 12, 16, 0));
		assertTrue(defaultTest.hasEnded());
	}
	
	@Test
	public void testHasEndedFailed() throws ResourceUnavailableException, IllegalArgumentException, IllegalStateException{
		defaultTest.plan(LocalDateTime.of(2015, 2, 12, 15, 0),concreteResDef,devList);
		defaultTest.execute(LocalDateTime.of(2015, 2, 12, 15, 0));
		assertFalse(defaultTest.hasEnded());
		defaultTest.fail(LocalDateTime.of(2015, 2, 12, 16, 0));
		assertTrue(defaultTest.hasEnded());
	}
	
	@Test
	public void finishedEndpointTestSelf() throws ResourceUnavailableException, IllegalArgumentException, IllegalStateException{
		defaultTest.plan(LocalDateTime.of(2015, 2, 12, 15, 0),concreteResDef,devList);
		defaultTest.execute(LocalDateTime.of(2015, 2, 12, 15, 0));
		assertFalse(defaultTest.hasFinishedEndpoint());
		defaultTest.finish(LocalDateTime.of(2015, 2, 12, 16, 0));
		assertTrue(defaultTest.hasFinishedEndpoint());
	}
	
	@Test
	public void finishedEndpointTestOther() throws ResourceUnavailableException, IllegalArgumentException, IllegalStateException{
		defaultTest.plan(LocalDateTime.of(2015, 2, 12, 15, 0),concreteResDef,devList);
		defaultTest.execute(LocalDateTime.of(2015, 2, 12, 15, 0));
		assertFalse(defaultTest.hasFinishedEndpoint());
		defaultTest.fail(LocalDateTime.of(2015, 2, 12, 16, 0));
		Task alt = new Task("alternative", 80, 5, resMan, 
				defaultTest.getPrerequisites(),
				resDef, 
				defaultTest);
		alt.plan(LocalDateTime.of(2015, 2, 13, 16, 0),concreteResDef,devList);
		alt.execute(LocalDateTime.of(2015, 2, 13, 16, 0));
		assertFalse(defaultTest.hasFinishedEndpoint());
		alt.finish(LocalDateTime.of(2015, 2, 14, 16, 0));
		assertTrue(defaultTest.hasFinishedEndpoint());
	}
	
//	@Test(expected=IllegalStateException.class)
//	public void replaceWithTestNotFailed(){
//		Task temp = new Task("temp",20,3,new ResourceManager(new ArrayList<ResourcePrototype>()),new ArrayList<Task>(),new HashMap<ResourceView,Integer>(),null);
//		assertFalse(defaultTest.isFailed());
//		defaultTest.replaceWith(temp);
//	}
	
//	@Test(expected=IllegalStateException.class)
//	public void replaceWithTestNull() throws ResourceUnavailableException, IllegalArgumentException, IllegalStateException{
//		defaultTest.plan(LocalDateTime.of(2015, 2, 11, 16, 0),concreteResDef,devList);
//		defaultTest.execute(LocalDateTime.of(2015, 2, 11, 16, 0));
//		defaultTest.fail(LocalDateTime.of(2015, 2, 12, 16, 0));
//		Task temp = new Task("temp",20,3,new ResourceManager(new ArrayList<ResourcePrototype>()),new ArrayList<Task>(),new HashMap<ResourceView,Integer>(),null);
//		Task temp2 = new Task("temp",20,3,new ResourceManager(new ArrayList<ResourcePrototype>()),new ArrayList<Task>(),new HashMap<ResourceView,Integer>(),null);
//		defaultTest.replaceWith(temp);
//		defaultTest.replaceWith(temp2);
//	}
	
//	@Test(expected=IllegalArgumentException.class)
//	public void setBeginTimeTestNull(){
//		defaultTest.setBeginTime(null);
//	}
//	
//	@Test(expected=IllegalStateException.class)
//	public void setBeginTimeTestAlreadySet(){
//		defaultTest.plan(LocalDateTime.of(2015, 2, 11, 16, 0),concreteResDef,devList);
//		assertTrue(defaultTest.setBeginTime(LocalDateTime.of(2015, 2, 11, 16, 0)));
//		defaultTest.setBeginTime(LocalDateTime.of(2015, 2, 14, 16, 0));
//	}
	
//	@Test(expected=IllegalArgumentException.class)
//	public void setEndTimeTestNull(){
//		defaultTest.setEndTime(null);
//	}
//	
//	@Test(expected=IllegalStateException.class)
//	public void setEndTimeTestAlreadySet(){
//		defaultTest.plan(LocalDateTime.of(2015, 2, 12, 15, 0),concreteResDef,devList);
//		defaultTest.execute(LocalDateTime.of(2015, 2, 12, 15, 0));
//		assertTrue(defaultTest.fail(LocalDateTime.of(2015, 2, 12, 16, 0)));
//		defaultTest.setEndTime(LocalDateTime.of(2015, 2, 14, 16, 0));
//	}
	
	@Test(expected=IllegalArgumentException.class)
	public void registerTestSelf(){
		defaultTest.register(defaultTest);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void registerTestNull(){
		defaultTest.register(null);
	}
	
	@Test
	public void registerTestValid(){
		Task temp = new Task("temp",20,3,new ResourceManager(new ArrayList<ResourcePrototype>()),new ArrayList<Task>(),
				new HashMap<ResourceView,Integer>(),null);
		defaultTest.register(temp);
	}
	
	@Test
	public void createTaskWithPrerequisites(){
		ArrayList<Task> pre = new ArrayList<>();
		pre.add(defaultTest);
		Task temp = new Task("temp",20,3,new ResourceManager(new ArrayList<ResourcePrototype>()),pre,
				new HashMap<ResourceView,Integer>(),null);
		assertEquals(1, temp.getPrerequisites().size());
		assertTrue(temp.getPrerequisites().contains(defaultTest));
	}
	
	@Test
	public void getTimeSpentTestTaskNotStarted(){
		LocalDateTime currentTime = LocalDateTime.of(2015, 2, 11, 16, 0);
		assertEquals(new TimeSpan(0),defaultTest.getTimeSpent(currentTime));
	}
	
	@Test
	public void getTimeSpentTestTaskHasEnded() throws ResourceUnavailableException, IllegalArgumentException, IllegalStateException{
		LocalDateTime beginTime = LocalDateTime.of(2015, 2, 11, 14, 0);
		LocalDateTime endTime = LocalDateTime.of(2015, 2, 11, 15, 30);
		LocalDateTime currentTime = LocalDateTime.of(2015, 2, 12, 14, 0);
		defaultTest.plan(beginTime,concreteResDef,devList);
		defaultTest.execute(beginTime);
		defaultTest.fail(endTime);
		assertEquals(new TimeSpan(90),defaultTest.getTimeSpent(currentTime));
	}
	
	@Test
	public void getTimeSpentTestTaskExecuting() throws ResourceUnavailableException, IllegalArgumentException, IllegalStateException{
		LocalDateTime beginTime = LocalDateTime.of(2015, 2, 11, 14, 0);
		LocalDateTime currentTime = LocalDateTime.of(2015, 2, 11, 15, 0);
		defaultTest.plan(beginTime,concreteResDef,devList);
		defaultTest.execute(beginTime);
		assertEquals(new TimeSpan(60),defaultTest.getTimeSpent(currentTime));
	}
	
	@Test
	public void getTimeSpentTestTaskIsAlternativeAndFinsihed() throws ResourceUnavailableException, IllegalArgumentException, IllegalStateException{
		LocalDateTime beginTimeDef = LocalDateTime.of(2015, 2, 11, 14, 0);
		LocalDateTime endTimeDef = LocalDateTime.of(2015, 2, 11, 15, 30);
		LocalDateTime currentTime = LocalDateTime.of(2015, 2, 12, 16, 0);
		defaultTest.plan(beginTimeDef,concreteResDef,devList);
		defaultTest.execute(beginTimeDef);
		defaultTest.fail(endTimeDef);
		assertEquals(new TimeSpan(90),defaultTest.getTimeSpent(currentTime));
		
		Task alt = new Task("alt",60,3,resMan,new ArrayList<Task>(),
				new HashMap<ResourceView,Integer>(),defaultTest);
		assertEquals(defaultTest, alt.getAlternativeFor());
		LocalDateTime beginTimeAlt = LocalDateTime.of(2015, 2, 12, 14, 0);
		LocalDateTime endTimeAlt = LocalDateTime.of(2015, 2, 12, 15, 0);
		alt.plan(beginTimeAlt,concreteResDef,devList);
		alt.execute(beginTimeAlt);
		alt.finish(endTimeAlt);
		assertEquals(defaultTest, alt.getAlternativeFor());
		assertEquals(new TimeSpan(150),alt.getTimeSpent(currentTime));
	}
	
	@Test
	public void getTimeSpentTestTaskIsAlternativeAndExecuting() throws ResourceUnavailableException, IllegalArgumentException, IllegalStateException{
		LocalDateTime beginTimeDef = LocalDateTime.of(2015, 2, 11, 14, 0);
		LocalDateTime endTimeDef = LocalDateTime.of(2015, 2, 11, 15, 30);
		LocalDateTime currentTime = LocalDateTime.of(2015, 2, 12, 14, 30);
		defaultTest.plan(beginTimeDef,concreteResDef,devList);
		defaultTest.execute(beginTimeDef);
		defaultTest.fail(endTimeDef);
		assertEquals(new TimeSpan(90),defaultTest.getTimeSpent(currentTime));
		
		Task alt = new Task("alt",60,3,resMan,new ArrayList<Task>(),
				new HashMap<ResourceView,Integer>(),defaultTest);
		assertEquals(defaultTest, alt.getAlternativeFor());
		LocalDateTime beginTimeAlt = LocalDateTime.of(2015, 2, 12, 14, 0);
		alt.plan(beginTimeAlt,concreteResDef,devList);
		alt.execute(beginTimeAlt);
		assertEquals(defaultTest, alt.getAlternativeFor());
		assertEquals(new TimeSpan(120),alt.getTimeSpent(currentTime));
	}
}
