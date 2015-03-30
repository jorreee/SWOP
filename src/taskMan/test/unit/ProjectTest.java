package taskMan.test.unit;

import java.time.LocalDateTime;

import org.junit.*;

import static org.junit.Assert.*; 
import taskMan.Project;

public class ProjectTest {
//TODO
	
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
}
