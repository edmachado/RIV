package org.fao.riv.tests.dataentry;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.fao.riv.tests.utils.ImportFile;
import org.fao.riv.tests.utils.WebTestUtil;

public class ProjectIgValidateEmptyBlock extends WebTestUtil {
	
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
	public void testValidation() throws Exception {
		String[] titles = projectStepTitles(true);
		String blockTitleWith = getMessage("ruralInvest")+" :: "+getMessage("projectBlock.name")+" ("+getMessage("projectBlock.with.with")+")";
		
		// import complete project
		importProject(ImportFile.ProjectV40, "igpj", false, false, "T3st Santa Cruz River Transport");
		// edit project
		goToPro(true, true, true);
		// add block and save without entering data
		clickLink("step9");
		assertTitleEquals(titles[8]);
		clickLink("addBlock");
		assertTitleEquals(blockTitleWith);
		rivSubmitForm();
		// expected result: same form, error box present
		assertTitleEquals(blockTitleWith);
		assertElementPresent("errorbox");
		
	}
	
	@Test
	public void testNoPayment() throws Exception {
		String[] titles = projectStepTitles(true);
		String blockTitleWith = getMessage("ruralInvest")+" :: "+getMessage("projectBlock.name")+" ("+getMessage("projectBlock.with.with")+")";
		
		// import complete project
		importProject(ImportFile.ProjectV40, "igpj", false, false, "T3st Santa Cruz River Transport");
		// edit project
		goToPro(true, true, true);
		clickLink("step9");
		assertTitleEquals(titles[8]);
		
		clickLinkWithImage("edit.png", 0);
		assertTitleEquals(blockTitleWith);
		
		// deselect all payments
		for (int j=0; j<12; j++) {
			for (int k=0; k<2; k++) {
				String xpath="//table[@id='blockChron']/tbody/tr/td[@id='0-2-"+j+"-"+k+"']";
				if (getElementsByXPath(xpath+"[@style='background:#e7ae0f;']").size()>0) {
					clickElementByXPath(xpath);
				}
			}
		}
		
		rivSubmitForm();
		
		// expected result: same form, error box present
		assertElementPresent("errorbox");
		assertTitleEquals(blockTitleWith);
		
	}
}