package org.fao.riv.tests.dataentry;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.fao.riv.tests.WebTest;
import org.fao.riv.utils.ImportFile;

public class ProjectIgRemoveWithWithout extends WebTest {
	
	 @BeforeClass 
	 public static void start() {      
	       System.out.println("     test ProjectIgRemoveWithWithout");
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
	public void test() throws Exception {
		// import complete project
		importProject(ImportFile.ProjectV40, "igpj", false, false, "T3st Santa Cruz River Transport");
		// edit project
		goToPro(true, true, true);
		String[] titles = projectStepTitles(true);
		
		assertRadioOptionNotSelected("withWithout", "false");
		assertRadioOptionSelected("withWithout", "true");
		clickRadioOption("withWithout", "false");
		assertRadioOptionSelected("withWithout", "false");
		assertRadioOptionNotSelected("withWithout", "true");
		rivSubmitForm();

		assertTitleEquals(titles[1]);
	}
}