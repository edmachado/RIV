package org.fao.riv.tests.dataentry;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.fao.riv.tests.utils.ImportFile;
import org.fao.riv.tests.utils.WebTestUtil;

public class ProjectCompleteToInProgress extends WebTestUtil {
	
	 @BeforeClass 
	 public static void start() {      
	       System.out.println("     test ProjectIgFromCompleteToInProgress");
	 }

	@Before
	public void before() {
		login();
	}
	
	@After
    public void close() {
		clickLink("logoff");
		closeBrowser();
    }
	
	
	@Test
	public void testProfileIg() throws Exception {
		deletePros(false, true);
		importProfile(ImportFile.ProfileIgV40, "igpf_no", false, false, "T3st Irrigation project");
		goToPro(false, true, true);
		
		clickLink("step6");
		assertLinkPresent("step7");
		
		clickLink("delete0");
		clickButtonWithText("Delete item");
		
		assertLinkNotPresent("step7");
		rivSubmitForm();
		assertElementPresent("errorbox");
	}
	
	@Test
	public void testProfileNig() throws Exception {
		deletePros(false, false);
		importProfile(ImportFile.ProfileNig40, "nigpf_no", false, false, "Community Health Centre");
		goToPro(false, false, true);
		
		clickLink("step6");
		assertLinkPresent("step7");
		
		for (int i=0;i<2;i++) {
			clickLink("delete0");
			clickButtonWithText("Delete item");
		}
		
		assertLinkNotPresent("step7");
		rivSubmitForm();
		assertElementPresent("errorbox");
	}
	
	@Test
	public void testProjectIg() throws Exception {
		deletePros(true, true);
		// import complete project
		importProject(ImportFile.ProjectV40, "igpj", false, false, "T3st Santa Cruz River Transport");
		// edit project
		goToPro(true, true, true);
		
		clickLink("step9");
		assertLinkPresent("step10");
		
		for (int i=0;i<3;i++) {
			clickLink("delete0");
			clickButtonWithText("Delete item");
		}
		assertLinkNotPresent("step10");
		rivSubmitForm();
		assertElementPresent("errorbox");
	}
	
	@Test
	public void testProjectNig() throws Exception {
		deletePros(true, false);
		// import complete project
		importProject(ImportFile.ProjectNig40, "nigpj", false, false, "Example Case: Community Earth Dam");
		// edit project
		goToPro(true, false, true);
		
		clickLink("step9");
		assertLinkPresent("step10");
		
		clickLink("delete0");
		clickButtonWithText("Delete item");
		
		assertLinkNotPresent("step10");
		rivSubmitForm();
		assertElementPresent("errorbox");
	}
}