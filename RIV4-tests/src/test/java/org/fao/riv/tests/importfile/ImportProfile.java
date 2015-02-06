package org.fao.riv.tests.importfile;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.fao.riv.tests.utils.ImportFile;
import org.fao.riv.tests.utils.WebTestUtil;

public class ImportProfile extends WebTestUtil {

	@Rule
    public TemporaryFolder folder = new TemporaryFolder();
	
	@BeforeClass 
	 public static void setUpClass() {      
	       System.out.println("     test ImportProfile");
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
	
	@Test
	public void importProfile16() {
		importProfile(ImportFile.ProfileIgV16, "igpf_no", false, true, "Programa de Producción Lechera");
	}
	
	@Test
	public void importProfile20() {
		importProfile(ImportFile.ProfileIgV20, "igpf_no", false, true, "Bagre Tanchachin");
	}
	
	@Test
	public void importProfile21() {
		importProfile(ImportFile.ProfileIgV21, "igpf_no", false, false, "Artesanal Clothing Workshop");
	}
	
	@Test
	public void importProfile22() {
		importProfile(ImportFile.ProfileIgV22, "igpf_no", false, false, "Artesanal Clothing Workshop");
	}
	
	@Test
	public void importProfileGeneric21() {
		importProfile(ImportFile.ProfileIgV21Generic, "igpf_no", true, false, "Artesanal Clothing Workshop");
	}
	
	@Test
	public void importProfileGeneric22() {
		importProfile(ImportFile.ProfileIgV22Generic, "igpf_no", true, false, "Artesanal Clothing Workshop");
	}
	
	@Test
	public void importProfileNig16() {
		importProfile(ImportFile.ProfileNig16, "nigpf_no", false, true, "Community Health Centre");
	}
	
	@Test
	public void importProfileIg40() throws IOException, URISyntaxException {
		importProfile(ImportFile.ProfileIgV40, "igpf_no", false, false, "T3st Irrigation project");
		// verify
		clickLinkWithImage("edit.png");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("project.step1"));
		verifyProfile("dataentry/profileIg40", 1);
	}
	
	@Test
	public void importProfileIg42() throws IOException, URISyntaxException {
		// import
		importProfile(ImportFile.ProfileIgV42, "igpf_no", false, false, "T3st Irrigation project");
		// verify
		clickLinkWithImage("edit.png");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("project.step1"));
		verifyProfile("dataentry/profileIg", 1);
		
		// export to .riv file
		getTestContext().setResourceBundleName("messages/messages");
		assertLinkPresentWithImage("export_riv.gif");
		String pageUrl = getTestingEngine().getPageURL().toURI().toString();
		clickLinkWithImage("export_riv.gif");
		clickLink("dd_local");
		File f = folder.newFile("profile.riv"); 
		saveAs(f);
		assertTrue(isZipFile(f));
		
		// delete existing profile
		gotoPage(pageUrl);
		assertLinkPresentWithImage("delete.gif");
		clickLinkWithImage("delete.gif");
		clickButtonWithText("Delete item");
		assertLinkNotPresentWithImage("delete.gif");
		
		// import exported file and verify data is the same
		importProfile(f, "igpf_no", false, false, "T3st Irrigation project");
		clickLinkWithImage("edit.png");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("project.step1"));
		verifyProfile("dataentry/profileIg", 1);
	}
	
	@Test
	public void importProfileNig40() throws URISyntaxException, IOException {
		importProfile(ImportFile.ProfileNig40, "nigpf_no", false, false, "Community Health Centre");
		clickLinkWithImage("edit.png");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("project.step1"));
		verifyProfileNig("dataentry/profileNig", 1);
		
		// export to .riv file
		getTestContext().setResourceBundleName("messages/messages");
		assertLinkPresentWithImage("export_riv.gif");
		String pageUrl = getTestingEngine().getPageURL().toURI().toString();
		clickLinkWithImage("export_riv.gif");
		clickLink("dd_local");
		File f = folder.newFile("profile.riv"); 
		saveAs(f);
		assertTrue(isZipFile(f));
		
		// delete existing profile
		gotoPage(pageUrl);
		assertLinkPresentWithImage("delete.gif");
		clickLinkWithImage("delete.gif");
		clickButtonWithText("Delete item");
		assertLinkNotPresentWithImage("delete.gif");
		
		// import exported file and verify data is the same
		importProfile(f, "nigpf_no", false, false, "Community Health Centre");
		clickLinkWithImage("edit.png");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("project.step1"));
		verifyProfileNig("dataentry/profileNig", 1);
		
	}
}