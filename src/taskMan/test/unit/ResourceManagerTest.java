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
	
	
	
	

}
