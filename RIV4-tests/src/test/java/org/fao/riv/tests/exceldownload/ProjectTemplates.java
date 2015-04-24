package org.fao.riv.tests.exceldownload;

import static net.sourceforge.jwebunit.junit.JWebUnit.assertLinkPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTitleEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLink;
import static net.sourceforge.jwebunit.junit.JWebUnit.closeBrowser;
import static net.sourceforge.jwebunit.junit.JWebUnit.getMessage;
import static net.sourceforge.jwebunit.junit.JWebUnit.saveAs;

import java.io.File;
import java.io.IOException;

import org.fao.riv.tests.WebTest;
import org.fao.riv.utils.ImportFile;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ProjectTemplates extends WebTest {
	@Rule
    public TemporaryFolder folder = new TemporaryFolder();
	
	 @BeforeClass 
	 public static void setUpClass() {      
	       System.out.println("     test ProjectTemplates");
	 }
	
	@Before
	public void before() {
		login();
		goHome();
	}
	
	@After
    public void after() {
		setLanguage("en");
		clickLink("logoff");
        closeBrowser();
    }
	
	private void testProject(boolean isIG) throws IOException {
		String[] titles = projectStepTitles(isIG);
		
		// edit profile
		goToPro(true, isIG, true);
		
		// investment template
		clickLink("step7");
		assertTitleEquals(titles[6]);
		assertLinkPresent("downloadTemplate");
		clickLink("downloadTemplate");
		
		File f = folder.newFile();
		saveAs(f);
		testXls(f, isIG ? getMessage("project.report.investDetail")+ " " + getMessage("project.with") : getMessage("project.report.investDetail")); 
		
		// general template
		assertLinkPresent("step8");
		clickLink("step8");
		assertTitleEquals(titles[7]);
		assertLinkPresent("downloadTemplate");
		clickLink("downloadTemplate");
		f = folder.newFile();
		saveAs(f);
		testXls(f, getMessage("project.report.generalCostsDetail"));
		
		// block template
		assertLinkPresent("step9");
		clickLink("step9");
		assertTitleEquals(titles[8]);
		assertLinkPresent("downloadTemplate");
		clickLink("downloadTemplate");
		f = folder.newFile();
		saveAs(f);
		testXls(f, (isIG ? getMessage("projectBlockIncome") : getMessage("projectActivityCharge")) + " ("+getMessage("units.perUnitperCycle")+")");
		
		// contributions
		if (!isIG) {
			assertLinkPresent("step10");
			clickLink("step10");
			assertTitleEquals(titles[9]);
			assertLinkPresent("downloadTemplate");
			clickLink("downloadTemplate");
			f = folder.newFile();
			saveAs(f);
			testXls(f, getMessage("project.report.contributions"));
		}
	}	
	
	@Test
	public void projectIGi18n() throws Exception {
		deletePros(true, true);
		importProject(ImportFile.ProjectV40, "igpj", false, false, "T3st Santa Cruz River Transport");
		
		String[] langs = {"en","es","fr","ru","pt","mn","ar"};//,"tr"};
		for (String lang : langs) {
			System.out.println("testing "+lang);
			clickLink("goHome");
			setLanguage(lang);
			testProject(true);
		}
	}
	
	@Test
	public void projectNigi18n() throws Exception {
		deletePros(true, false);
		importProject(ImportFile.ProjectNig40, "nigpj", false, false, "Example Case: Community Earth Dam");
		
		String[] langs = {"en","es","fr","ru","pt","mn"};//,"ar","tr"};
		for (String lang : langs) {
			System.out.println("testing "+lang);
			setLanguage(lang);
			testProject(false);
//			setLanguage("en");
		}
	}
}