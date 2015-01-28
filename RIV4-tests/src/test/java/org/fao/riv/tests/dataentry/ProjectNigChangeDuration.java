package org.fao.riv.tests.dataentry;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.fao.riv.tests.utils.ImportFile;
import org.fao.riv.tests.utils.WebTestUtil;

public class ProjectNigChangeDuration extends WebTestUtil {
	
	 @BeforeClass 
	 public static void start() {      
	       System.out.println("     test ProjectIgChangeDuration");
	 }

	@Before
	public void deleteExisting() {
		login();
		deletePros(true, false);
	}
	
	@After
    public void close() {
		clickLink("logoff");
		closeBrowser();
    }
	
	@Test
	public void changeDuration() throws Exception {
		// import complete project
		importProject(ImportFile.ProjectNig41, "nigpj", false, false, "Example Case: Community Earth Dam");
		// edit project
		goToPro(true, false, true);
		
		// project base: duration 15 years
		// 1. verify all 15 years of project pattern
		checkContributions(15);
		
		//2. lower duration to 10 years and check
		setDuration(10);
		checkContributions(10);
		
		//4. set to 14 and check
		setDuration(14);
		checkContributions(14);
	}
	
	private void setDuration(int duration) {
		clickLink("step1");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("project.step1"));
		setTextField("duration", String.valueOf(duration));
		rivSubmitForm();assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("project.step2"));
		clickLink("step1");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("project.step1"));
		assertTextFieldEquals("duration", String.valueOf(duration));
	}
	
	private void checkContributions(int expectedNum) {
		clickLink("step10");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("project.step10.nongen"));
//		int existing = getElementsByXPath("//table[@id='prodPattern0']/tbody/tr/td[@class='production-year']").size();
//		org.junit.Assert.assertTrue(existing==expectedNum);
	}
}