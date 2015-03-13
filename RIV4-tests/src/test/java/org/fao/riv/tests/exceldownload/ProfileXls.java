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

public class ProfileXls extends WebTestUtil {
	String igTitle;
	
	@Rule
    public TemporaryFolder folder = new TemporaryFolder();
	
	 @BeforeClass 
	 public static void setUpClass() {      
	       System.out.println("     test ProfileXls");
	 }
	
	@Before
	public void deleteExisting() {
		login();
		clickLink("goHome");
	}
	
	@After
    public void close() {
		setLanguage("en");
		clickLink("logoff");
        closeBrowser();
    }
	
	private void testProfile(boolean isIG) throws IOException {
		goToPro(false, isIG, true);
		
		clickLink("step9");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("profile.step9"));
		
		String[] titles = reportTitles(isIG, true);
		// seven reports
		for (int i=0;i<7;i++) {
			clickLinkWithImage("xls.gif", i);
			File f = folder.newFile(); 
			saveAs(f);
			testXls(f, titles[i]);
			f.delete();
			f=null;
		}
	}
	
	@Test
	public void profileIgI18n() throws IOException {
		deletePros(false, true);
		importProfile(ImportFile.ProfileIgV40, "igpf_no", false, false, "T3st Irrigation project");
		
		//TODO: enable turkish when translations are complete
		String[] langs = {"en","es","fr","ru","pt","mn","ar"};//,"tr"
		for (String lang : langs) {
			System.out.println("testing "+lang);
			setLanguage(lang);
			testProfile(true);
		}
	}
	
	@Test
	public void profileNgI18n() throws IOException {
		deletePros(false, false);
		importProfile(ImportFile.ProfileNig16, "nigpf", false, true, "Community Health Centre");
		
		//TODO: enable turkish when translations are complete
		String[] langs = {"en","es","fr","ru","pt","mn","ar"};//,"tr""};
		for (String lang : langs) {
			System.out.println("testing "+lang);
			setLanguage(lang);
			testProfile(false);
		}	
	}
	
	protected static String[] reportTitles(boolean isIG, boolean withWithout) {
		String[] titles = new String[7];
		titles[0]=getMessage("profile.report.summary");
		titles[1]=!isIG ? getMessage("profile.report.investDetail") : withWithout ? getMessage("profile.report.investDetail") + " " +getMessage("project.with"): getMessage("profile.report.investDetail");
		titles[2]=getMessage("profile.report.costsDetail");
		String reportCode_product = isIG ? "profile.report.productDetail" : "profile.report.productDetailNongen";
		titles[3]=isIG? "("+getMessage("profileProduct.with.with")+") "+getMessage(reportCode_product) : getMessage(reportCode_product);
		String reportCode_preliminary = isIG ? "profile.report.preliminary" : "profile.report.benefits";
		titles[4]=getMessage(reportCode_preliminary);
		titles[5]=getMessage("profile.report.recommendation");
		titles[6]=getMessage("profile.report.summary");
		
		return titles;
	}
}