package org.fao.riv.tests.exceldownload;

import static net.sourceforge.jwebunit.junit.JWebUnit.assertLinkPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTitleEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLink;
import static net.sourceforge.jwebunit.junit.JWebUnit.closeBrowser;
import static net.sourceforge.jwebunit.junit.JWebUnit.getMessage;
import static net.sourceforge.jwebunit.junit.JWebUnit.saveAs;

import java.io.File;
import java.io.IOException;

import org.fao.riv.tests.utils.ImportFile;
import org.fao.riv.tests.utils.WebTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ProjectTemplates extends WebTestUtil {
	String igTitle;
	
	@Rule
    public TemporaryFolder folder = new TemporaryFolder();
	
	 @BeforeClass 
	 public static void setUpClass() {      
	       System.out.println("     test ProjectTemplates");
	 }
	
	@Before
	public void before() {
		login();
		importSettings(ImportFile.Settings20);
	}
	
	@After
    public void after() {
		clickLink("logoff");
        closeBrowser();
    }
	
	private void testProject(ImportFile file, String type, boolean isGeneric, boolean missingCapital, String projName) throws IOException {
		boolean isIG = type.startsWith("ig");
		String[] titles = projectStepTitles(isIG);
		
		// import complete project
		importProject(file, type, isGeneric, missingCapital, projName);
		// edit profile
		goToPro(true, isIG, true);
		
		// investment template
		clickLink("step7");
		assertTitleEquals(titles[6]);
		assertLinkPresent("downloadTemplate");
		clickLink("downloadTemplate");
		File f = folder.newFile("invest.xlsx");
		saveAs(f);
		testXls(f, isIG ? getMessage("project.report.investDetail")+ " " + getMessage("project.with") : getMessage("project.report.investDetail")); 
		
		// general template
		assertLinkPresent("step8");
		clickLink("step8");
		assertTitleEquals(titles[7]);
		assertLinkPresent("downloadTemplate");
		clickLink("downloadTemplate");
		f = folder.newFile("general.xlsx");
		saveAs(f);
		testXls(f, getMessage("project.report.generalCostsDetail"));
		
		// block template
		assertLinkPresent("step9");
		clickLink("step9");
		assertTitleEquals(titles[8]);
		assertLinkPresent("downloadTemplate");
		clickLink("downloadTemplate");
		f = folder.newFile("block.xlsx");
		saveAs(f);
		testXls(f, isIG ? getMessage("projectBlockIncome") : getMessage("projectActivityCharge"));
		
		// contributions
		if (!isIG) {
			assertLinkPresent("step10");
			clickLink("step10");
			assertTitleEquals(titles[9]);
			assertLinkPresent("downloadTemplate");
			clickLink("downloadTemplate");
			f = folder.newFile("contributions.xlsx");
			saveAs(f);
			testXls(f, getMessage("project.step10.nongen"));
		}
	}	
	
	@Test
	public void projectIgInEnglish() throws IOException {
		testProject(ImportFile.ProjectV40, "igpj", false, false, "T3st Santa Cruz River Transport");
	}
	
	@Test
	public void projectIgInSpanish() throws IOException {
		setLanguage("es");
		testProject(ImportFile.ProjectV40, "igpj", false, false, "T3st Santa Cruz River Transport");
		setLanguage("en");
	}
	
	@Test
	public void projectNig() throws IOException {
		testProject(ImportFile.ProjectNig40, "nigpj", false, false, "Example Case: Community Earth Dam");
	}
	
	@Test
	public void projectNigSpanish() throws IOException {
		setLanguage("es");
		testProject(ImportFile.ProjectNig40, "nigpj", false, false, "Example Case: Community Earth Dam");
		setLanguage("en");
	}
}