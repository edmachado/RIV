package org.fao.riv.tests.exceldownload;

import static net.sourceforge.jwebunit.junit.JWebUnit.assertTitleEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLink;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLinkWithImage;
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
	
	@Test
	public void projectIGi18n() throws IOException {
		String[] langs = {"en","es","fr","ru","pt","mn"};//,"tr","ar"};
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