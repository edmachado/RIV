package org.fao.riv.tests.dataentry;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.fao.riv.tests.utils.ImportFile;
import org.fao.riv.tests.utils.WebTestUtil;

public class RoundingProblem extends WebTestUtil {
	
	 @BeforeClass 
	 public static void start() {      
	       System.out.println("     test RoundingProblem");
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
	public void testProfile() throws Exception {
		String[] titles = profileStepTitles(true);
		deletePros(false, true);
		// import complete profile
		importProfile(ImportFile.ProfileIgV40, "igpf_no", false, false, "T3st Irrigation project");
		// edit project
		goToPro(false, true, true);
		// test if rounding error is thrown for following case
		clickLink("step4");
		clickLink("editLabour0");
		setTextField("unitNum", "72");
		setTextField("unitCost","9.04");
		setTextField("ownResource","650.88");
		rivSubmitForm();
		assertElementNotPresent("errorbox");
		assertTitleEquals(titles[3]);
	}
	
}