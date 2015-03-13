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
	@Rule
    public TemporaryFolder folder = new TemporaryFolder();
	
	 @BeforeClass 
	 public static void setUpClass() {      
	       System.out.println("     test ProjectXls");
	 }
	
	@Before
	public void before() {
		login();
		goHome();
	}
	
	@After
    public void close() {
		setLanguage("en");
		clickLink("logoff");
        closeBrowser();
    }
	
	private void testProject(boolean isIG) throws IOException {

		goToPro(true, isIG, true);
		
		clickLink("step13");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("project.step13"));
		
		String[] titles = reportTitles(isIG);
		// reports
		for (int i=0;i<titles.length;i++) {
			clickLinkWithImage("xls.gif", i);
			File f = folder.newFile(); 
			saveAs(f);
			testXls(f, titles[i]);
			f.delete();
		}
	}
	
	@Test
	public void projectIGi18n() throws IOException {
		deletePros(true, true);
		importProject(ImportFile.ProjectV20, "igpj", false, false, "Tomate Curungueo");
		
		String[] langs = {"en","es","fr","ru","pt","mn","ar"};//,"tr"};
		for (String lang : langs) {
			System.out.println("testing "+lang);
			setLanguage(lang);
			testProject(true);
		}
	}
	
	@Test
	public void projectNigI18n() throws IOException {
		deletePros(true, false);
		importProject(ImportFile.ProjectNig16, "nigpj", false, false, "Access Road & Bridge");
		
		String[] langs = {"en","es","fr","ru","pt","mn","ar"};//,"tr"};
		for (String lang : langs) {
			System.out.println("testing "+lang);
			setLanguage(lang);
			testProject(false);
		}
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