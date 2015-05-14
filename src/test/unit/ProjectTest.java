package test.unit;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import company.taskMan.project.Project;
import company.taskMan.project.TaskView;
import company.taskMan.resource.ResourceManager;
import company.taskMan.resource.ResourceView;
import exceptions.ResourceUnavailableException;

public class ProjectTest {
//TODO
	
	private ResourceManager resMan;
	private Project defaultProject;
	
	@Before
	public void initialize(){
		defaultProject = new Project("defaultProject","default",
				LocalDateTime.of(2015, 2, 11, 16, 0),LocalDateTime.of(2015, 2, 17, 16, 0));
		resMan = new ResourceManager();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ConstrFailName(){
		new Project(null,"fail",LocalDateTime.of(2015, 2, 11, 16, 0),
				LocalDateTime.of(2015, 2, 12, 16, 0));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ConstrFailDescription(){
		new Project("fail",null,LocalDateTime.of(2015, 2, 11, 16, 0),
				LocalDateTime.of(2015, 2, 12, 16, 0));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ConstrFailCreationTimeNull(){
		new Project("fail","fail",null,
				LocalDateTime.of(2015, 2, 12, 16, 0));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ConstrFailDueTimeNull(){
		new Project("fail","fail",LocalDateTime.of(2015, 2, 11, 16, 0),
				null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ConstrFailDueBeforeCreation(){
		new Project("fail","fail",LocalDateTime.of(2015, 2, 12, 16, 0),
				LocalDateTime.of(2015, 2, 11, 16, 0));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ConstrFailDueEqualsCreation(){
		new Project("fail","fail",LocalDateTime.of(2015, 2, 12, 16, 0),
				LocalDateTime.of(2015, 2, 12, 16, 0));
	}
	
	@Test
	public void getNameTest(){
		assertEquals("defaultproject", defaultProject.getName().toLowerCase());
	}
	
	@Test
	public void getDescriptionTest(){
		assertEquals("default", defaultProject.getDescription().toLowerCase());
	}
	
	@Test
	public void getCreationTimeTest(){
		assertEquals(LocalDateTime.of(2015, 2, 11, 16, 0), 
				defaultProject.getCreationTime());
	}
	
	@Test
	public void getDueTimeTest(){
		assertEquals(LocalDateTime.of(2015, 2, 17, 16, 0), 
				defaultProject.getDueTime());
	}
	
	@Test
	public void getStateTest(){
		assertEquals("ongoing", defaultProject.getStatus().toLowerCase());
	}
	
	@Test
	public void createTaskTest() throws ResourceUnavailableException, IllegalArgumentException, IllegalStateException {
		defaultProject.createTask("test", 60, 5, resMan, new ArrayList<TaskView>(), 
				new HashMap<ResourceView, Integer>(), null, null, null, null, null, null);
	}
	
	@Test
	public void createTaskTestFinishedTask() throws ResourceUnavailableException, IllegalArgumentException, IllegalStateException{
		defaultProject.createTask("test", 60, 5, resMan, new ArrayList<TaskView>(), 
				new HashMap<ResourceView, Integer>(), null, "finished", 
				LocalDateTime.of(2015,  4, 29, 10, 0), LocalDateTime.of(2015,  4, 29, 11, 0), 
				LocalDateTime.of(2015,  4, 29, 10, 0), new ArrayList<ResourceView>());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void setEndTimeFailNull(){
		defaultProject.setEndTime(null);
	}
	
	@Test
	public void setEndTimeFailAlreadySet(){
		defaultProject.setEndTime(LocalDateTime.of(2015, 4, 29, 10, 0));
		defaultProject.setEndTime(LocalDateTime.of(2015, 4, 29, 10, 0));
	}
}
