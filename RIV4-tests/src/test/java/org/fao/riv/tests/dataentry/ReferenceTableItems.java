package org.fao.riv.tests.dataentry;

import static net.sourceforge.jwebunit.junit.JWebUnit.assertLinkPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTitleEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickElementByXPath;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLink;
import static net.sourceforge.jwebunit.junit.JWebUnit.closeBrowser;
import static net.sourceforge.jwebunit.junit.JWebUnit.getElementsByXPath;
import static net.sourceforge.jwebunit.junit.JWebUnit.getMessage;
import static net.sourceforge.jwebunit.junit.JWebUnit.getTestContext;

import java.util.concurrent.Callable;

import org.fao.riv.tests.utils.ImportFile;
import org.fao.riv.tests.utils.TestTable;
import org.fao.riv.tests.utils.WebTestUtil;
import org.fao.riv.tests.utils.InputParam.InputParamType;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class ReferenceTableItems extends WebTestUtil {
	 @BeforeClass 
	 public static void start() {      
	       System.out.println("     test ReferenceTableItems");
	 }
	
	 @Before
	 public void deleteExisting() {
		login();
	 }
	 
	 @After
	    public void close() {
			clickLink("logoff");
			closeBrowser();
	    }
	 
	 @Test
	 public void testIGproject() throws Exception {
		deletePros(true, true);
		importProject(ImportFile.ProjectV41, "igpj", false, false, "T3st Santa Cruz River Transport");
		goToPro(true, true, true);
		
		clickLink("step10");
		assertLinkPresent("step11");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("project.step10"));
		
		testReferenceItems(true, true);
	 }
	 
	 @Test
	 public void testNIGproject() throws Exception {
		deletePros(true, false);
		importProject(ImportFile.ProjectNig41, "nigpj", false, false, "Example Case: Community Earth Dam");
		goToPro(true, false, true);
			
		clickLink("step11");
		assertLinkPresent("step12");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("project.step11.nongen"));
		
		testReferenceItems(true, false);
	 }
	 
	 @Test
	 public void testIGprofile() throws Exception {
		deletePros(false, true);
		importProfile(ImportFile.ProfileIgV40, "igpf_no", false, false, "T3st Irrigation project");
		goToPro(false, true, true);
			
		clickLink("step7");
		assertLinkPresent("step8");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("profile.step7"));
		
		testReferenceItems(false, true);
	 }
	 
	 @Test
	 public void testNIGprofile() throws Exception {
		 deletePros(false, false);
		 importProfile(ImportFile.ProfileNig40, "nigpf_no", false, false, "Community Health Centre");
		 
		 goToPro(false, false, true);
			
		clickLink("step7");
		assertLinkPresent("step8");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("profile.step7"));

		testReferenceItems(false, false);
	 }

}
