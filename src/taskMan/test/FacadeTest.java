package taskMan.test;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import taskMan.TaskMan;

public class FacadeTest {
	
	private TaskMan taskMan;
	
	//TODO Alles door Facade testen ipv TaskMan?
	
	@Before
	public final void initialize() {
		taskMan = new TaskMan();
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
