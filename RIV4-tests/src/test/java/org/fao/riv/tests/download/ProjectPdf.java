package org.fao.riv.tests.download;

import static net.sourceforge.jwebunit.junit.JWebUnit.assertTitleEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLink;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLinkWithImage;
import static net.sourceforge.jwebunit.junit.JWebUnit.closeBrowser;
import static net.sourceforge.jwebunit.junit.JWebUnit.getMessage;
import static net.sourceforge.jwebunit.junit.JWebUnit.saveAs;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.fao.riv.tests.utils.ImportFile;
import org.fao.riv.tests.utils.WebTestUtil;

public class ProjectPdf extends WebTestUtil {
	@Rule
    public TemporaryFolder folder = new TemporaryFolder();
	
	 @BeforeClass 
	 public static void setUpClass() {      
	       System.out.println("     test ProjectPdf");
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
			clickLinkWithImage("pdf.gif", i);
			File f = folder.newFile(i+".pdf"); 
			saveAs(f);
			String contents = extractPdfText(f);
			org.junit.Assert.assertTrue(contents.contains(titles[i]));
		}
	}
	
	@Test
	public void projectIg() throws IOException {
		testProject(ImportFile.ProjectV20, "igpj", false, false, "Tomate Curungueo");
	}
	
	@Test
	public void projectNig() throws IOException {
		testProject(ImportFile.ProjectNig16, "nigpj", false, false, "Access Road & Bridge");
	}
	
	private String[] reportTitles(boolean isIG) {
		String[] titles;
		
		if (isIG) {
			titles = new String[13];
			titles[0]="A: "+getMessage("project.report.summary");
			titles[1]="B: "+getMessage("project.report.general");
			titles[2]="C: "+getMessage("project.report.investDetail");
			titles[3]="D: "+getMessage("project.report.generalCostsDetail");
			titles[4]="E: "+getMessage("project.report.production");
			titles[5]="F: "+getMessage("project.report.chronology");
			titles[6]="G: "+getMessage("project.report.blockDetail");
			titles[7]="H: "+getMessage("project.report.parameters");
			titles[8]="I: "+getMessage("project.report.cashFlowFirst");
			titles[9]="J: "+getMessage("project.report.cashFlow");
			titles[10]="K: "+getMessage("project.report.profitability");
			titles[11]="L: "+getMessage("project.report.recommendation");
			titles[12] = getMessage("project.report.complete");
		} else {
			titles = new String[10];
			titles[0]="A: "+getMessage("project.report.summary");
			titles[1]="B: "+getMessage("project.report.general");
			titles[2]="C: "+getMessage("project.report.investDetail");
			titles[3]="D: "+getMessage("project.report.generalCostsDetail");
			titles[4]="E: "+getMessage("project.report.production.nonGen");
			titles[5]="F: "+getMessage("project.report.activityDetail");
			titles[6]="G: "+getMessage("project.report.contributions");
			titles[7]="H: "+getMessage("project.report.cashFlowNongen");
			titles[8]="I: "+getMessage("project.report.recommendation");
			titles[9] = getMessage("project.report.complete");
		}
		return titles;
	}
}