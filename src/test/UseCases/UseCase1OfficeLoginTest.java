package test.UseCases;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

import userInterface.IFacade;
import userInterface.TaskManException;

import company.BranchManager;
import company.taskMan.resource.ResourceView;

public class UseCase1OfficeLoginTest {

	private final LocalDateTime startDate = LocalDateTime.of(2015, 2, 9, 8, 0);
	private IFacade branchManager;
	
	@Before
	public final void initialize() {
		branchManager = new BranchManager(startDate);
		
		branchManager.initializeBranch("Leuven");
		branchManager.initializeBranch("Aarschot");
		
		branchManager.createDeveloper("Joran");
		branchManager.createDeveloper("Eli");
		branchManager.selectBranch(branchManager.getBranches().get(0));
		branchManager.createDeveloper("Tom");
	}

	@Test
	public void succesCaseTest(){
		branchManager.changeToUser(branchManager.getPossibleUsers().get(1));
		assertEquals(branchManager.getCurrentUser().getName(),"Tom");		
	}
	
	@Test
	public void branchSwitchSuccesCaseTest(){
		branchManager.selectBranch(branchManager.getBranches().get(1));
		branchManager.changeToUser(branchManager.getPossibleUsers().get(1));
		assertEquals(branchManager.getCurrentUser().getName(),"Joran");
		branchManager.changeToUser(branchManager.getPossibleUsers().get(2));
		assertEquals(branchManager.getCurrentUser().getName(),"Eli");
	}
	
	@Test(expected = TaskManException.class)
	public void wrongBranchFailed(){
		ResourceView user = branchManager.getPossibleUsers().get(1);
		branchManager.selectBranch(branchManager.getBranches().get(1));
		branchManager.changeToUser(user);
	}
	
	@Test(expected = TaskManException.class)
	public void nullUserFailed(){
		branchManager.changeToUser(null);
	}
}
