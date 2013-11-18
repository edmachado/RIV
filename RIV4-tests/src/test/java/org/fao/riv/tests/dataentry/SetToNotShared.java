package org.fao.riv.tests.dataentry;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.fao.riv.tests.utils.ImportFile;
import org.fao.riv.tests.utils.WebTestUtil;

public class SetToNotShared extends WebTestUtil {
	
	 @BeforeClass 
	 public static void start() {      
	       System.out.println("     test SetToShared");
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
	public void testProject() throws Exception {
		String[] titles = projectStepTitles(true);
		
		deletePros(true, true);
		// import complete project
		importProject(ImportFile.ProjectV40, "igpj", false, false, "T3st Santa Cruz River Transport");
		// edit project
		goToPro(true, true, true);
		clickRadioOption("shared", "false");
		rivSubmitForm();
		assertTitleEquals(titles[1]);
	}
	
	@Test
	public void testProfile() throws Exception {
		String[] titles = profileStepTitles(true);
		deletePros(false, true);
		// import complete profile
		importProfile(ImportFile.ProfileIgV40, "igpf_no", false, false, "T3st Irrigation project");
		// edit project
		goToPro(false, true, true);
		clickRadioOption("shared", "false");
		rivSubmitForm();
		assertTitleEquals(titles[1]);
	}
	
}