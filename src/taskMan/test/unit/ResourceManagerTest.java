package taskMan.test.unit;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import taskMan.Task;
import taskMan.resource.ResourceManager;
import taskMan.resource.ResourcePrototype;
import taskMan.resource.user.User;
import taskMan.view.ResourceView;

public class ResourceManagerTest {
	
	private ResourceManager resMan;
	
	private List<ResourceView> concreteResDef, devList;
	private Map<ResourceView,Integer> resDef;
	private ResourceView carDef;	//car0
	private ResourceView boardDef;	//board0
	private ResourceView weer, blunderbus;
	
	private final Optional<LocalTime> emptyAvailabilityPeriodStart = Optional.empty(),
			emptyAvailabilityPeriodEnd = Optional.empty(),
			availabilityPeriod14 = Optional.of(LocalTime.of(14,0)),
			availabilityPeriod17 = Optional.of(LocalTime.of(17,0));
	
	
	@Before
	public void initialize() {
		//Prepare the resources and developers
		resMan = new ResourceManager();
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
	public void getPossibleStartingTimesTest() {
		Task testTask = new Task("Descr", 60, 0, resMan, new ArrayList<Task>(), new HashMap<ResourceView, Integer>(), null);
		LocalDateTime currentTime = LocalDateTime.of(2015, 04, 23, 10, 0);
		
		assertEquals(LocalDateTime.of(2015, 04, 23, 10, 0), resMan.getPossibleStartingTimes(testTask, new ArrayList<ResourceView>(), currentTime, 1).get(0));
		assertTrue(resMan.getPossibleStartingTimes(testTask, new ArrayList<ResourceView>(), currentTime, 3).contains(LocalDateTime.of(2015, 04, 23, 10, 0)));
		assertTrue(resMan.getPossibleStartingTimes(testTask, new ArrayList<ResourceView>(), currentTime, 3).contains(LocalDateTime.of(2015, 04, 23, 11, 0)));
		assertTrue(resMan.getPossibleStartingTimes(testTask, new ArrayList<ResourceView>(), currentTime, 3).contains(LocalDateTime.of(2015, 04, 23, 12, 0)));
	}
	
	@Test
	public void createResourcePrototypeTestSuccess(){
		Optional<LocalTime> start = Optional.of(LocalTime.of(10, 0));
		Optional<LocalTime> end = Optional.of(LocalTime.of(14, 0));
		assertTrue(resMan.createResourcePrototype("Pencil", start,end));
	}
	
	@Test
	public void createResourcePrototypeTestBadDates(){
		Optional<LocalTime> start = Optional.of(LocalTime.of(10, 0));
		Optional<LocalTime> end = Optional.of(LocalTime.of(14, 0));
		Optional<LocalTime> nul = Optional.empty();
		assertFalse(resMan.createResourcePrototype("fail", start, nul));
		assertFalse(resMan.createResourcePrototype("fail", nul, end));
		assertFalse(resMan.createResourcePrototype("fail", end, start));
	}
	
	@Test
	public void createResourcePrototypeTestBadName(){
		Optional<LocalTime> start = Optional.of(LocalTime.of(10, 0));
		Optional<LocalTime> end = Optional.of(LocalTime.of(14, 0));
		assertFalse(resMan.createResourcePrototype(null, start, end));
	}
	
	@Test
	public void declareConcreteResourceTestSuccess(){
		Optional<LocalTime> start = Optional.of(LocalTime.of(10, 0));
		Optional<LocalTime> end = Optional.of(LocalTime.of(14, 0));
		assertTrue(resMan.createResourcePrototype("Pencil", start,end));
		
		assertTrue(resMan.declareConcreteResource("pencil1", resMan.getResourcePrototypes().get(2)));
	}
	
	@Test
	public void declareConcreteResourceTestFailBadPrototype(){
		assertFalse(resMan.declareConcreteResource("pencil1", new ResourceView(new ResourcePrototype("fail",null))));
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
	public void pickDevsTestSucess(){
		Task testTask = new Task("Descr", 60, 0, resMan, new ArrayList<Task>(), new HashMap<ResourceView, Integer>(), null);
		LocalDateTime start = LocalDateTime.of(2015, 4, 4, 10, 0);
		LocalDateTime end = LocalDateTime.of(2015, 4, 4, 12, 0);
		List<ResourceView> devs = new ArrayList<>();
		
		devs.add(weer);
		List<User> res = resMan.pickDevs(devs, testTask, start, end);
		assertEquals("Weer",res.get(0).getName());
		assertEquals(1, res.size());
	}
	
	@Test
	public void pickDevsTestFailEmptyList(){
		Task testTask = new Task("Descr", 60, 0, resMan, new ArrayList<Task>(), new HashMap<ResourceView, Integer>(), null);
		LocalDateTime start = LocalDateTime.of(2015, 4, 4, 10, 0);
		LocalDateTime end = LocalDateTime.of(2015, 4, 4, 12, 0);
		List<ResourceView> devs = new ArrayList<>();
		
		List<User> res = resMan.pickDevs(devs, testTask, start, end);
		assertEquals(0,res.size());
	}

}
