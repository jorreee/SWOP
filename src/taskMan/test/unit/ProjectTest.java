package taskMan.test.unit;

import java.time.LocalDateTime;

import org.junit.*;

import static org.junit.Assert.*; 
import taskMan.Project;
import taskMan.state.*;

public class ProjectTest {
//TODO
	
	private Project defaultProject;
	
	@Before
	public void initialize(){
		defaultProject = new Project(1,"defaultProject","default",
				LocalDateTime.of(2015, 2, 11, 16, 0),LocalDateTime.of(2015, 2, 17, 16, 0));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ConstrFailID(){
		new Project(-1,"fail","fail",LocalDateTime.of(2015, 2, 11, 16, 0),
				LocalDateTime.of(2015, 2, 12, 16, 0));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ConstrFailName(){
		new Project(1,null,"fail",LocalDateTime.of(2015, 2, 11, 16, 0),
				LocalDateTime.of(2015, 2, 12, 16, 0));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ConstrFailDescription(){
		new Project(1,"fail",null,LocalDateTime.of(2015, 2, 11, 16, 0),
				LocalDateTime.of(2015, 2, 12, 16, 0));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ConstrFailCreationTimeNull(){
		new Project(1,"fail","fail",null,
				LocalDateTime.of(2015, 2, 12, 16, 0));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ConstrFailDueTimeNull(){
		new Project(1,"fail","fail",LocalDateTime.of(2015, 2, 11, 16, 0),
				null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ConstrFailDueBeforeCreation(){
		new Project(1,"fail","fail",LocalDateTime.of(2015, 2, 12, 16, 0),
				LocalDateTime.of(2015, 2, 11, 16, 0));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ConstrFailDueEqualsCreation(){
		new Project(1,"fail","fail",LocalDateTime.of(2015, 2, 12, 16, 0),
				LocalDateTime.of(2015, 2, 12, 16, 0));
	}
	
	@Test
	public void getIDTest(){
		assertEquals(1, defaultProject.getID());
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
	public void setFinishedTest(){
		assertFalse(defaultProject.isFinished());
		defaultProject.setProjectStatus(new FinishedProject(defaultProject));
		assertTrue(defaultProject.isFinished());
	}
	
}
