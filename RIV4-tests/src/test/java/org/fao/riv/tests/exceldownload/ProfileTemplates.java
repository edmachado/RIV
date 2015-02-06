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
	String igTitle;
	
	@Rule
    public TemporaryFolder folder = new TemporaryFolder();
	
	 @BeforeClass 
	 public static void setUpClass() {      
	       System.out.println("     test ProfileTemplates");
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
	
	private void testProfile(ImportFile file, String type, boolean isGeneric, boolean missingBenefFamilies, String profName) throws IOException {
		boolean isIG = type.startsWith("ig");
		String[] titles = profileStepTitles(isIG);
		
		// import complete project
		importProfile(file, type, isGeneric, missingBenefFamilies, profName);
		// edit profile
		goToPro(false, isIG, true);
		
		// investment template
		clickLink("step4");
		assertTitleEquals(titles[3]);
		assertLinkPresent("downloadTemplate");
		clickLink("downloadTemplate");
		File f = folder.newFile("invest.xlsx");
		saveAs(f);
		testXls(f, getMessage("profile.report.investDetail")); 
		
		// general template
		assertLinkPresent("step5");
		clickLink("step5");
		assertTitleEquals(titles[4]);
		assertLinkPresent("downloadTemplate");
		clickLink("downloadTemplate");
		f = folder.newFile("general.xlsx");
		saveAs(f);
		testXls(f, getMessage("profile.report.costsDetail"));
		
		// product template
		assertLinkPresent("step6");
		clickLink("step6");
		assertTitleEquals(titles[5]);
		assertLinkPresent("downloadTemplate");
		clickLink("downloadTemplate");
		f = folder.newFile("block.xlsx");
		saveAs(f);
		testXls(f, isIG 
				? getMessage("profileProductIncome") 
				: getMessage("profileActivityCharge")
			);
	}
	
	@Test
	public void profileIg() throws IOException {
		testProfile(ImportFile.ProfileIgV40, "igpf_no", false, false, "T3st Irrigation project");
	}
	
	@Test
	public void profileNig() throws IOException {
		testProfile(ImportFile.ProfileNig40, "nigpf_no", false, false, "Community Health Centre");
	}
	
	
}