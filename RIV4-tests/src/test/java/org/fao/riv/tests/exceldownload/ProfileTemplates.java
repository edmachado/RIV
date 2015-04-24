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

public class ProfileTemplates extends WebTestUtil {
	@Rule
    public TemporaryFolder folder = new TemporaryFolder();
	
	 @BeforeClass 
	 public static void setUpClass() {      
	       System.out.println("     test ProfileTemplates");
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
	
	private void testProfile(boolean isIG) throws IOException {
		String[] titles = profileStepTitles(isIG);
		String[] reportTitles = ProfileXls.reportTitles(isIG, isIG);
		
		goToPro(false, isIG, true);
		
		// investment template
		clickLink("step4");
		assertTitleEquals(titles[3]);
		assertLinkPresent("downloadTemplate");
		clickLink("downloadTemplate");
		File f = folder.newFile();
		saveAs(f);
		testXls(f, reportTitles[1]); 
		
		// general template
		assertLinkPresent("step5");
		clickLink("step5");
		assertTitleEquals(titles[4]);
		assertLinkPresent("downloadTemplate");
		clickLink("downloadTemplate");
		f = folder.newFile();
		saveAs(f);
		testXls(f, getMessage("profile.report.costsDetail"));
		
		// product template
		assertLinkPresent("step6");
		clickLink("step6");
		assertTitleEquals(titles[5]);
		assertLinkPresent("downloadTemplate");
		clickLink("downloadTemplate");
		f = folder.newFile();
		saveAs(f);
		testXls(f, isIG 
				? getMessage("profileProductIncome") + " ("+getMessage("units.perUnitperCycle")+")"
				: getMessage("profileActivityCharge") + " ("+getMessage("units.perUnitperCycle")+")"
			);
	}
	
	
	
	@Test
	public void profileIGi18n() throws Exception {
		deletePros(false, true);
		importProfile(ImportFile.ProfileIgV40, "igpf_no", false, false, "T3st Irrigation project");
		
		String[] langs = {"en","es","fr","ru","pt","mn","ar"};//,"tr"};
		for (String lang : langs) {
			System.out.println("testing "+lang);
			clickLink("goHome");
			setLanguage(lang);
			testProfile(true);
		}
	}
	
	@Test
	public void profileNigI18n() throws IOException {
		deletePros(false, false);
		importProfile(ImportFile.ProfileNig40, "nigpf_no", false, false, "Community Health Centre");
		
		String[] langs = {"en","es","fr","ru","pt","mn","ar"};//,"tr"};
		for (String lang : langs) {
			System.out.println("testing "+lang);
			clickLink("goHome");
			setLanguage(lang);
			testProfile(false);
		}
	}
	
	
}