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

import org.fao.riv.tests.WebTest;
import org.fao.riv.utils.ImportFile;

public class ProfilePdf extends WebTest {
	@Rule
    public TemporaryFolder folder = new TemporaryFolder();
	
	 @BeforeClass 
	 public static void setUpClass() {      
	       System.out.println("     test ProfilePdf");
	 }
	
	@Before
	public void deleteExisting() {
		login();
		deletePros(false, true);
		deletePros(false, false);
	}
	
	@After
    public void close() {
		clickLink("logoff");
        closeBrowser();
    }
	
	private void testProfile(ImportFile file, String type, boolean isGeneric, boolean missingBenefFamilies, String profName) throws IOException {
		// import complete profile
		importProfile(file, type, isGeneric, missingBenefFamilies, profName);
		boolean isIG = type.startsWith("ig");
		// edit profile
		goToPro(false, isIG, true);
		
		clickLink("step9");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("profile.step9"));
		
		String[] titles = reportTitles(isIG);
		// seven reports
		for (int i=0;i<7;i++) {
			clickLinkWithImage("pdf.gif", i);
			File f = folder.newFile(i+".pdf"); 
			saveAs(f);
			String contents = extractPdfText(f);
			org.junit.Assert.assertTrue(contents.contains(titles[i]));
		}
	}
	
	@Test
	public void profileIg() throws IOException {
		testProfile(ImportFile.ProfileIgV22, "igpf", false, false, "Artesanal Clothing Workshop");
	}
	
	@Test
	public void profileNg() throws IOException {
		testProfile(ImportFile.ProfileNig16, "nigpf", false, true, "Community Health Centre");
	}
	
	private String[] reportTitles(boolean isIG) {
		String[] titles = new String[7];
		titles[0]="A: "+getMessage("profile.report.summary");
		titles[1]="B: "+getMessage("profile.report.investDetail");
		titles[2]="C: "+getMessage("profile.report.costsDetail");
		String reportCode_product = isIG ? "profile.report.productDetail" : "profile.report.productDetailNongen";
		titles[3]="D: "+getMessage(reportCode_product );
		String reportCode_preliminary = isIG ? "profile.report.preliminary" : "profile.report.benefits";
		titles[4]="E: "+getMessage(reportCode_preliminary);
		titles[5]="F: "+getMessage("profile.report.recommendation");
		titles[6]=getMessage("profile.report.complete");
		
		return titles;
	}
}