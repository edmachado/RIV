package org.fao.riv.tests.exceldownload;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

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

public class ProjectXls extends WebTestUtil {
	String igTitle;
	
	@Rule
    public TemporaryFolder folder = new TemporaryFolder();
	
	 @BeforeClass 
	 public static void setUpClass() {      
	       System.out.println("     test ProjectXls");
	 }
	
	@Before
	public void deleteExisting() {
		login();
		deletePros(true, true);
		deletePros(true, false);
	}
	
	@After
    public void close() {
		clickLink("logoff");
        closeBrowser();
    }
	
	private void testProject(ImportFile file, String type, boolean isGeneric, boolean missingCapital, String projName) throws IOException {
		// import complete project
		importProject(file, type, isGeneric, missingCapital, projName);
		boolean isIG = type.startsWith("ig");
		// edit profile
		goToPro(true, isIG, true);
		
		clickLink("step13");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("project.step13"));
		
		String[] titles = reportTitles(isIG);
		// reports
		for (int i=0;i<titles.length;i++) {
			clickLinkWithImage("xls.gif", i);
			File f = folder.newFile(i+".xls"); 
			saveAs(f);
			testXls(f, titles[i]);
			f.delete();
		}
	}
	
//	@Ignore
	@Test
	public void testProjectWith40InvestmentItems() throws IOException {
		String[] titles = projectStepTitles(true);
		importProject(ImportFile.ProjectV41, "igpj", false, false, "T3st Santa Cruz River Transport");
		clickLinkWithImage("edit.png");
		assertTitleEquals(titles[0]);
		assertLinkPresent("step7");
		clickLink("step7");
		assertTitleEquals(titles[6]);
		
		for (int i=0;i<40;i++) {
			assertLinkPresentWithImage("duplicate.gif");
			clickLinkWithImage("duplicate.gif");
		}
		
		assertLinkPresent("step13");
		clickLink("step13");
		assertTitleEquals(titles[12]);
		
		assertLinkPresent("xls_complete");
		clickLink("xls_complete");
		File f = folder.newFile("complete_40_investments.xls"); 
		saveAs(f);
		
		//String value = cellValueFromXls(f, 11, 5, 1);
//		org.junit.Assert.assertTrue(false);  
		
//		f.delete();
		
	}
	
	@Test
	public void projectIGi18n() throws IOException {
		String[] langs = {"en","es","fr","ru","pt","mn","ar"};//,"tr"};
		for (String lang : langs) {
			System.out.println("testing "+lang);
			clickLink("goHome");
			deletePros(true, true);
			setLanguage(lang);
			testProject(ImportFile.ProjectV20, "igpj", false, false, "Tomate Curungueo");
			setLanguage("en");
		}
	}
	
	@Test
	public void projectNig() throws IOException {
		testProject(ImportFile.ProjectNig16, "nigpj", false, false, "Access Road & Bridge");
	}
	
	private String[] reportTitles(boolean isIG) {
		String[] titles;
		
		if (isIG) {
			titles = new String[13];
			titles[0]=getMessage("project.report.summary");
			titles[1]=getMessage("project.report.general");
			titles[2]=getMessage("project.report.investDetail");
			titles[3]=getMessage("project.report.generalCostsDetail");
			titles[4]=getMessage("project.report.production");
			titles[5]=getMessage("project.report.chronology");
			titles[6]=getMessage("project.report.blockDetail");
			titles[7]=getMessage("project.report.parameters");
			titles[8]=getMessage("project.report.cashFlowFirst");
			titles[9]=getMessage("project.report.cashFlow");
			titles[10]=getMessage("project.report.profitability");
			titles[11]=getMessage("project.report.recommendation");
			titles[12] = getMessage("project.report.summary");
		} else {
			titles = new String[10];
			titles[0]=getMessage("project.report.summary");
			titles[1]=getMessage("project.report.general");
			titles[2]=getMessage("project.report.investDetail");
			titles[3]=getMessage("project.report.generalCostsDetail");
			titles[4]=getMessage("project.report.production.nonGen");
			titles[5]=getMessage("project.report.activityDetail");
			titles[6]=getMessage("project.report.contributions");
			titles[7]=getMessage("project.report.cashFlowNongen");
			titles[8]=getMessage("project.report.recommendation");
			titles[9] = getMessage("project.report.summary");
		}
		return titles;
	}
}