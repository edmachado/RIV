package org.fao.riv.tests.dataentry;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.fao.riv.tests.utils.ImportFile;
import org.fao.riv.tests.utils.WebTestUtil;

public class ProjectIgChangeDuration extends WebTestUtil {
	
	 @BeforeClass 
	 public static void start() {      
	       System.out.println("     test ProjectIgChangeDuration");
	 }

	@Before
	public void deleteExisting() {
		login();
		deletePros(true, true);
	}
	
	@After
    public void close() {
		clickLink("logoff");
		closeBrowser();
    }
	
	@Test
	public void changeDuration() throws Exception {
		// import complete project
		importProject(ImportFile.ProjectV40, "igpj", false, false, "T3st Santa Cruz River Transport");
		// edit project
		goToPro(true, true, true);
		
		// project base: duration 15 years
		// 1. verify all 15 years of project pattern
		checkProdPatterns(15);
		
		//2. lower duration to 10 years and check
		setDuration(10);
		checkProdPatterns(10);
		
		//4. set to 14 and check
		setDuration(14);
		checkProdPatterns(14);
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
	
	private void checkProdPatterns(int expectedNum) {
		clickLink("step9");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("project.step9"));
		int existing = getElementsByXPath("//table[@id='prodPattern0']/tbody/tr/td[@class='production-year']").size();
		org.junit.Assert.assertTrue(existing==expectedNum);
	}
}