package test.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import company.taskMan.resource.ResourceManager;
import company.taskMan.resource.ResourcePrototype;
import company.taskMan.resource.ResourceView;
import company.taskMan.resource.user.User;
import company.taskMan.resource.user.UserPrototype;
import company.taskMan.task.Task;

import exceptions.NoSuchResourceException;
import exceptions.ResourceUnavailableException;
import exceptions.UnexpectedViewContentException;

public class ResourceManagerTest {
	
	private ResourceManager resMan;
	
//	private List<ResourceView> concreteResDef, devList;
//	private Map<ResourceView,Integer> resDef;
//	private ResourceView carDef;	//car0
//	private ResourceView boardDef;	//board0
	private ResourceView weer, blunderbus;
	
	private final Optional<LocalTime> emptyAvailabilityPeriodStart = Optional.empty(),
			emptyAvailabilityPeriodEnd = Optional.empty();
	
	@Before
	public void initialize() {
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
		
	}
	
	@Test
	public void createResourcePrototypeTestSuccess(){
		Optional<LocalTime> start = Optional.of(LocalTime.of(10, 0));
		Optional<LocalTime> end = Optional.of(LocalTime.of(14, 0));
		resMan.createResourcePrototype("Pencil", start,end);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createResourcePrototypeTestBadDate1(){
		Optional<LocalTime> start = Optional.of(LocalTime.of(10, 0));
		Optional<LocalTime> nul = Optional.empty();
		resMan.createResourcePrototype("fail", start, nul);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createResourcePrototypeTestBadDate2(){
		Optional<LocalTime> end = Optional.of(LocalTime.of(14, 0));
		Optional<LocalTime> nul = Optional.empty();
		resMan.createResourcePrototype("fail", nul, end);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createResourcePrototypeTestBadDate3(){
		Optional<LocalTime> start = Optional.of(LocalTime.of(10, 0));
		Optional<LocalTime> end = Optional.of(LocalTime.of(14, 0));
		resMan.createResourcePrototype("fail", end, start);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void createResourcePrototypeTestBadName(){
		Optional<LocalTime> start = Optional.of(LocalTime.of(10, 0));
		Optional<LocalTime> end = Optional.of(LocalTime.of(14, 0));
		resMan.createResourcePrototype(null, start, end);
	}
	
	@Test
	public void declareConcreteResourceTestSuccess(){
		Optional<LocalTime> start = Optional.of(LocalTime.of(10, 0));
		Optional<LocalTime> end = Optional.of(LocalTime.of(14, 0));
		resMan.createResourcePrototype("Pencil", start,end);
		
		resMan.declareConcreteResource("pencil1", resMan.getResourcePrototypes().get(2));
	}
	
	@Test(expected=UnexpectedViewContentException.class)
	public void declareConcreteResourceTestFailBadPrototype(){
		resMan.declareConcreteResource("pencil1", new ResourceView(new ResourcePrototype("fail",null)));
	}
	
	@Test
	public void createDevSuccess(){
		assertTrue(resMan.createDeveloper("testDev"));
	}
	
	@Test
	public void createDevFailNull(){
		assertFalse(resMan.createDeveloper(null));
	}
	
	@Test
	public void pickDevsTestSucess() throws ResourceUnavailableException, UnexpectedViewContentException, IllegalArgumentException{
		Task testTask = new Task("Descr", 60, 0, resMan, new ArrayList<Task>(), new HashMap<ResourceView, Integer>(), null);
		LocalDateTime start = LocalDateTime.of(2015, 4, 4, 10, 0);
		LocalDateTime end = LocalDateTime.of(2015, 4, 4, 12, 0);
		List<ResourceView> devs = new ArrayList<>();
		
		devs.add(weer);
		List<User> res = resMan.pickDevs(devs, testTask, start, end,true);
		assertEquals("Weer",res.get(0).getName());
		assertEquals(1, res.size());
	}
	
	@Test
	public void pickDevsTestFailEmptyList() throws ResourceUnavailableException, UnexpectedViewContentException, IllegalArgumentException{
		Task testTask = new Task("Descr", 60, 0, resMan, new ArrayList<Task>(), new HashMap<ResourceView, Integer>(), null);
		LocalDateTime start = LocalDateTime.of(2015, 4, 4, 10, 0);
		LocalDateTime end = LocalDateTime.of(2015, 4, 4, 12, 0);
		List<ResourceView> devs = new ArrayList<>();
		
		List<User> res = resMan.pickDevs(devs, testTask, start, end,true);
		assertEquals(0,res.size());
	}
	
	@Test(expected = UnexpectedViewContentException.class)
	public void pickDevsTestFailNonExistentUser() throws ResourceUnavailableException, UnexpectedViewContentException, IllegalArgumentException{
		Task testTask = new Task("Descr", 60, 0, resMan, new ArrayList<Task>(), new HashMap<ResourceView, Integer>(), null);
		LocalDateTime start = LocalDateTime.of(2015, 4, 4, 10, 0);
		LocalDateTime end = LocalDateTime.of(2015, 4, 4, 12, 0);
		List<ResourceView> devs = new ArrayList<>();
		
		devs.add(new ResourceView(new User("Fail", new UserPrototype())));
		resMan.pickDevs(devs, testTask, start, end,true);
	}
	
	@Test
	public void getPossibleStartingTimesTestSuccessSimple() throws ResourceUnavailableException, NoSuchResourceException, IllegalArgumentException {
		//Simple Test without planned resources
		Task testTask = new Task("Descr", 60, 0, resMan, new ArrayList<Task>(), new HashMap<ResourceView, Integer>(), null);
		LocalDateTime currentTime = LocalDateTime.of(2015, 04, 23, 10, 0);
		
		List<LocalDateTime> possibleTimes = resMan.getPossibleStartingTimes(
				testTask, new ArrayList<ResourceView>(), currentTime, 10);
		
		assertEquals(10,possibleTimes.size());
		assertTrue(possibleTimes.contains(LocalDateTime.of(2015, 04, 23, 10, 0)));
		assertTrue(possibleTimes.contains(LocalDateTime.of(2015, 04, 23, 11, 0)));
		assertTrue(possibleTimes.contains(LocalDateTime.of(2015, 04, 23, 12, 0)));
		assertFalse(possibleTimes.contains(LocalDateTime.of(2015, 04, 23, 17, 0)));
		assertFalse(possibleTimes.contains(LocalDateTime.of(2015, 04, 23, 18, 0)));
		assertFalse(possibleTimes.contains(LocalDateTime.of(2015, 04, 24, 7, 0)));
		assertTrue(possibleTimes.contains(LocalDateTime.of(2015, 04, 24, 8, 0)));
	}
	
	@Test
	public void getPossibleStartingTimesTestSuccessCurrentTimeNonWorking() throws ResourceUnavailableException, NoSuchResourceException, IllegalArgumentException {
		//Test with currenTime being a non working time
		Task testTask = new Task("Descr", 60, 0, resMan, new ArrayList<Task>(), new HashMap<ResourceView, Integer>(), null);
		LocalDateTime currentTime = LocalDateTime.of(2015, 04, 22, 18, 0);
		
		List<LocalDateTime> possibleTimes = resMan.getPossibleStartingTimes(
				testTask, new ArrayList<ResourceView>(), currentTime, 10);
		
		assertEquals(10,possibleTimes.size());
		assertFalse(possibleTimes.contains(LocalDateTime.of(2015, 04, 23, 7, 0)));
		assertTrue(possibleTimes.contains(LocalDateTime.of(2015, 04, 23, 8, 0)));
		assertTrue(possibleTimes.contains(LocalDateTime.of(2015, 04, 23, 12, 0)));
		assertFalse(possibleTimes.contains(LocalDateTime.of(2015, 04, 23, 17, 0)));
		assertFalse(possibleTimes.contains(LocalDateTime.of(2015, 04, 23, 18, 0)));
		assertFalse(possibleTimes.contains(LocalDateTime.of(2015, 04, 24, 7, 0)));
		assertTrue(possibleTimes.contains(LocalDateTime.of(2015, 04, 24, 8, 0)));
	}

	@Test
	public void getPossibleStartingTimesTestSuccessReqResourcesOccupied() throws ResourceUnavailableException, IllegalArgumentException, IllegalStateException{
		//The required resources are occupied by another task, 
		//the method must give a time after the planned time of the other task.
		
		//Set up first task
		HashMap<ResourceView,Integer> reqRes1 = new HashMap<ResourceView, Integer>();
		reqRes1.put(resMan.getResourcePrototypes().get(0), 4);
		reqRes1.put(resMan.getResourcePrototypes().get(1), 4);
		Task firstTask = new Task("first", 60, 0, resMan, new ArrayList<Task>(), reqRes1, null);
		
		//Set up second Task
		HashMap<ResourceView,Integer> reqRes2 = new HashMap<>();
		reqRes2.put(resMan.getResourcePrototypes().get(0), 3);
		reqRes2.put(resMan.getResourcePrototypes().get(1), 3);
		Task secondTask = new Task("first", 60, 0, resMan, new ArrayList<Task>(), reqRes2, null);
		LocalDateTime currentTime = LocalDateTime.of(2015, 04, 23, 10, 0);
		
		//Create assigned resources
		ArrayList<ResourceView> concRes = new ArrayList<>();
		concRes.add(resMan.getResourcePrototypes().get(0));
		concRes.add(resMan.getResourcePrototypes().get(0));
		concRes.add(resMan.getResourcePrototypes().get(0));
		concRes.add(resMan.getResourcePrototypes().get(0));
		concRes.add(resMan.getResourcePrototypes().get(1));
		concRes.add(resMan.getResourcePrototypes().get(1));
		concRes.add(resMan.getResourcePrototypes().get(1));
		concRes.add(resMan.getResourcePrototypes().get(1));
		
		//Plan first task
		ArrayList<ResourceView> devs = new ArrayList<>();
		devs.add(weer);
		firstTask.plan(LocalDateTime.of(2015, 04, 23, 10, 0), concRes, devs);
		
		ArrayList<ResourceView> conRes2 = new ArrayList<>();
		conRes2.add(resMan.getResourcePrototypes().get(0));
		conRes2.add(resMan.getResourcePrototypes().get(0));
		conRes2.add(resMan.getResourcePrototypes().get(0));
		conRes2.add(resMan.getResourcePrototypes().get(1));
		conRes2.add(resMan.getResourcePrototypes().get(1));
		conRes2.add(resMan.getResourcePrototypes().get(1));
		
		List<LocalDateTime> possibleTimes = resMan.getPossibleStartingTimes(
				secondTask, conRes2, currentTime, 10);
		assertEquals(10,possibleTimes.size());
		assertFalse(possibleTimes.contains(LocalDateTime.of(2015, 04, 23, 7, 0)));
		assertFalse(possibleTimes.contains(LocalDateTime.of(2015, 04, 23, 10, 0)));
		assertTrue(possibleTimes.contains(LocalDateTime.of(2015, 04, 23, 12, 0)));
		assertFalse(possibleTimes.contains(LocalDateTime.of(2015, 04, 23, 17, 0)));
		assertFalse(possibleTimes.contains(LocalDateTime.of(2015, 04, 23, 18, 0)));
		assertFalse(possibleTimes.contains(LocalDateTime.of(2015, 04, 24, 7, 0)));
		assertTrue(possibleTimes.contains(LocalDateTime.of(2015, 04, 24, 8, 0)));
	}
	
	@Test
	public void getPossibleStartingTimeTestSuccessReqDeveloperAlreadyAssigned() throws ResourceUnavailableException, IllegalArgumentException, IllegalStateException {
		Task plannedTask = new Task("already planned", 120, 0, resMan, new ArrayList<Task>(), new HashMap<ResourceView, Integer>(), null);
		LocalDateTime currentTime = LocalDateTime.of(2015, 04, 23, 10, 0);
		
		plannedTask.plan(currentTime, new ArrayList<ResourceView>(), Lists.newArrayList(weer));
		
		Task toPlan = new Task("to plan", 60, 0, resMan, new ArrayList<Task>(), new HashMap<ResourceView, Integer>(), null);
		List<LocalDateTime> possibleTimes = resMan.getPossibleStartingTimes(
				toPlan, Lists.newArrayList(weer), currentTime, 3);
		assertEquals(3,possibleTimes.size());
		assertFalse(possibleTimes.contains(currentTime));
		assertTrue(possibleTimes.contains(LocalDateTime.of(2015, 04, 23, 12, 0)));
		assertTrue(possibleTimes.contains(LocalDateTime.of(2015, 04, 23, 13, 0)));
		assertTrue(possibleTimes.contains(LocalDateTime.of(2015, 04, 23, 14, 0)));
		
		Task toPlanAgain = new Task("to plan again", 60, 0, resMan, new ArrayList<Task>(), new HashMap<ResourceView, Integer>(), null);
		possibleTimes = resMan.getPossibleStartingTimes(
				toPlanAgain, Lists.newArrayList(blunderbus), currentTime, 3);
		assertEquals(3,possibleTimes.size());
		assertTrue(possibleTimes.contains(currentTime));
		assertTrue(possibleTimes.contains(LocalDateTime.of(2015, 04, 23, 11, 0)));
		assertTrue(possibleTimes.contains(LocalDateTime.of(2015, 04, 23, 12, 0)));
	}
	
	@Test
	public void getPossibleStartingTimesTestSuccessReqResourcesHaveAvailability() throws ResourceUnavailableException, NoSuchResourceException, IllegalArgumentException{
		//The required resources have an availability period
		
		//Create new resource
		Optional<LocalTime> start = Optional.of(LocalTime.of(8, 0));
		Optional<LocalTime> end = Optional.of(LocalTime.of(12, 0));
		resMan.createResourcePrototype("pencil", start, end);
		assertEquals(3, resMan.getResourcePrototypes().size());
		resMan.declareConcreteResource("pencil1", resMan.getResourcePrototypes().get(2));
		
		//Create task
		HashMap<ResourceView,Integer> reqRes1 = new HashMap<ResourceView, Integer>();
		reqRes1.put(resMan.getResourcePrototypes().get(2), 1);
		Task testTask = new Task("test", 60, 0, resMan, new ArrayList<Task>(), reqRes1, null);
		
		LocalDateTime currentTime = LocalDateTime.of(2015, 04, 23, 10, 0);
		List<LocalDateTime> possibleTimes = resMan.getPossibleStartingTimes(
				testTask, Lists.newArrayList(resMan.getResourcePrototypes().get(2)), currentTime, 10);
		assertEquals(10,possibleTimes.size());
		assertFalse(possibleTimes.contains(LocalDateTime.of(2015, 04, 23, 7, 0)));
		assertTrue(possibleTimes.contains(LocalDateTime.of(2015, 04, 23, 10, 0)));
		assertTrue(possibleTimes.contains(LocalDateTime.of(2015, 04, 23, 11, 0)));
		assertFalse(possibleTimes.contains(LocalDateTime.of(2015, 04, 23, 12, 0)));
		assertFalse(possibleTimes.contains(LocalDateTime.of(2015, 04, 23, 15, 0)));
		assertTrue(possibleTimes.contains(LocalDateTime.of(2015, 04, 24, 8, 0)));
		assertTrue(possibleTimes.contains(LocalDateTime.of(2015, 04, 24, 11, 0)));
		assertFalse(possibleTimes.contains(LocalDateTime.of(2015, 04, 24, 12, 0)));

	}
}
