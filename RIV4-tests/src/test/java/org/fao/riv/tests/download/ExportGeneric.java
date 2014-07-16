package org.fao.riv.tests.download;

import static net.sourceforge.jwebunit.junit.JWebUnit.assertLinkPresentWithImage;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLink;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLinkWithImage;
import static net.sourceforge.jwebunit.junit.JWebUnit.closeBrowser;
import static net.sourceforge.jwebunit.junit.JWebUnit.getTestingEngine;
import static net.sourceforge.jwebunit.junit.JWebUnit.gotoPage;
import static net.sourceforge.jwebunit.junit.JWebUnit.saveAs;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.fao.riv.tests.utils.ImportFile;
import org.fao.riv.tests.utils.WebTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ExportGeneric  extends WebTestUtil {
	@Rule
    public TemporaryFolder folder = new TemporaryFolder();
	
	@BeforeClass 
	 public static void setUpClass() {      
	       System.out.println("     test ExportGeneric");
	 }
	
	@Before
	public void deleteExisting() {
		login();
		deletePros(false, true);
		deletePros(false, false);

		deletePros(true, true);
		deletePros(true, false);
	}
	
	@After
    public void close() {
		clickLink("logoff");
        closeBrowser();
    }
	
	@Test
	public void exportProfileIg() throws IOException, URISyntaxException {
		// import
		importProfile(ImportFile.ProfileIgV40, "igpf_no", false, false, "T3st Irrigation project");
		
		// export to generic .riv file
		assertLinkPresentWithImage("export_riv.gif");
		String pageUrl = getTestingEngine().getPageURL().toURI().toString();
		clickLinkWithImage("export_riv.gif");
		clickLink("dd_generic");
		File f = folder.newFile("profile.riv"); 
		saveAs(f);
		assertTrue(isZipFile(f));
		
		// send browser back to results page
		gotoPage(pageUrl);
	}
	
	@Test
	public void exportProfileNig() throws URISyntaxException, IOException {
		// import 
		importProfile(ImportFile.ProfileNig40, "nigpf_no", false, false, "Community Health Centre");

		// export to generic .riv file
		assertLinkPresentWithImage("export_riv.gif");
		String pageUrl = getTestingEngine().getPageURL().toURI().toString();
		clickLinkWithImage("export_riv.gif");
		clickLink("dd_generic");
		File f = folder.newFile("profile.riv"); 
		saveAs(f);
		assertTrue(isZipFile(f));
		
		// send browser back to results page
		gotoPage(pageUrl);
	}
	
	@Test
	public void exportProjectIg() throws URISyntaxException, IOException {
		// import
		importProject(ImportFile.ProjectV40, "igpj", false, false, "T3st Santa Cruz River Transport");

		// export to generic .riv file
		assertLinkPresentWithImage("export_riv.gif");
		String pageUrl = getTestingEngine().getPageURL().toURI().toString();
		clickLinkWithImage("export_riv.gif");
		clickLink("dd_generic");
		File f = folder.newFile("project.riv"); 
		saveAs(f);
		assertTrue(isZipFile(f));
		
		// send browser back to results page
		gotoPage(pageUrl);
	}
	
	@Test
	public void exportProjectNig() throws URISyntaxException, IOException {
		// import
		importProject(ImportFile.ProjectNig40, "nigpj", false, false, "Example Case: Community Earth Dam");
		
		// export to generic .riv file
		assertLinkPresentWithImage("export_riv.gif");
		String pageUrl = getTestingEngine().getPageURL().toURI().toString();
		clickLinkWithImage("export_riv.gif");
		clickLink("dd_generic");
		File f = folder.newFile("project.riv"); 
		saveAs(f);
		assertTrue(isZipFile(f));
		
		// send browser back to results page
		gotoPage(pageUrl);
	}
}
