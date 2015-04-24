package org.fao.riv.tests.dataentry;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;
import static org.junit.Assert.assertEquals;

import org.fao.riv.tests.utils.ImportFile;
import org.fao.riv.tests.utils.WebTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class NigProjectContributions extends WebTestUtil {
	 @BeforeClass 
	 public static void start() {      
	       System.out.println("     test NigProjectContributions");
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
	public void testCopyYearContributions() {
		// import complete project
		importProject(ImportFile.ProjectNig40, "nigpj", false, false, "Example Case: Community Earth Dam");
		// edit project
		goToPro(true, false, true);
		
		clickLink("step10");
		assertLinkPresent("step11");
		
		// set contribution to "per-year"
		clickRadioOption("simpleApproach", "false");
		assertLinkPresent("step11");

		getTestContext().setResourceBundleName("dataentry/projectNig");

		// delete contributions for year 2 and on
		int duration=Integer.parseInt(getMessage("step1.duration"));
		for (int d=2;d<=duration;d++) {
			while (getElementsByXPath("//table[@id='contributionTable"+d+"']//a[img[@src[substring(., string-length() -9) = 'delete.gif']]]").size()>0) {
				clickElementByXPath("(//table[@id='contributionTable"+d+"']//a[img[@src[substring(., string-length() -9) = 'delete.gif']]])[1]");
			}
		}	
		
		// copy year 1 to year 2
		clickLink("copyYear1");
		setTextField("targetYear", "2");
		clickButton("copyYearButton");
		verifyProjectNigTablesStep10(2);
		
		
	}
	 
	 @Test
	 public void testContributionSummary() {
		 String title10=getMessage("ruralInvest")+" :: "+getMessage("project.step10.nongen");
			
		 importProject(ImportFile.ProjectNig40, "nigpj", false, false, "Example Case: Community Earth Dam");
		goToPro(true, false, true);
		clickLink("step10");
		assertTitleEquals(title10);

		getTestContext().setResourceBundleName("dataentry/projectNig");
		assertEquals(Integer.parseInt(getMessage("step2.donor.count"))+1, getElementsByXPath("//div[@id='summaryContributions']/div/table").size());
	 }
	
	@Test
	public void testAddContributionForAllYears() throws Exception {
		importProject(ImportFile.ProjectNig40, "nigpj", false, false, "Example Case: Community Earth Dam");
		// edit project
		goToPro(true, false, true);
		
		clickLink("step10");
		assertLinkPresent("step11");
		
		// set contribution to "per-year"
		clickRadioOption("simpleApproach", "false");
		assertLinkPresent("step11");

		getTestContext().setResourceBundleName("dataentry/projectNig");
		
		int duration=Integer.parseInt(getMessage("step1.duration"));
		
		// delete existing contributions
		for (int d=1;d<=duration;d++) {
			while (getElementsByXPath("//table[@id='contributionTable"+d+"']//a[img[@src[substring(., string-length() -9) = 'delete.gif']]]").size()>0) {
				clickElementByXPath("(//table[@id='contributionTable"+d+"']//a[img[@src[substring(., string-length() -9) = 'delete.gif']]])[1]");
			}
		}
		
		// add new item
		clickLink("newContrib1");
		//TODO: assert page is right one
		setTextField("description", "a");
		setTextField("unitType", "b");
		setTextField("unitNum", "1");
		setTextField("unitCost", "1");
		checkCheckbox("allYears");
		clickButton("submit");
		
		// check all tables added
		assertLinkPresent("step11");
		for (int y=1;y<=duration;y++) {
			assertTablePresent("contributionTable"+y);
		}
		
		// check only one [duration] items added per table
		int itemsAdded = getElementsByXPath("//table[starts-with(@id,'contributionTable')]//a[img[@src[substring(., string-length() -9) = 'delete.gif']]]").size();
		org.junit.Assert.assertTrue(itemsAdded==duration);
	}
	 
}
