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

public class ImportProject extends WebTestUtil {

	@Rule
    public TemporaryFolder folder = new TemporaryFolder();
	
	@BeforeClass 
	 public static void setUpClass() {      
	       System.out.println("     test ImportProject");
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
	
	@Test
	public void importProjectIg16() {
		importProject(ImportFile.ProjectV16, "igpj", false, true, "Test IG Project");
	}
	
	@Test
	public void importProjectIg20() {
		importProject(ImportFile.ProjectV20, "igpj", false, false, "Tomate Curungueo");
	}

	@Test
	public void importProjectIg31Generic() {
		importProject(ImportFile.ProjectV31Generic, "igpj", true, false, "EXPLOTACION DE BOVINOS PARA DOBLE PROPOSITO");
	}
	
	@Test
	public void importProjectNig16() {
		importProject(ImportFile.ProjectNig16, "nigpj", false, false, "Access Road & Bridge");
	}
	
	@Test
	public void importProjectIg41NoCycles() {
		importProject(ImportFile.ProjectV41NoCycles, "igpj", false, false, "Цех по производству мелкотоварных молочных продуктов");
		
	}
	
	@Test
	public void importProjectIg40() throws URISyntaxException, IOException {
		importProject(ImportFile.ProjectV40, "igpj", false, false, "T3st Santa Cruz River Transport");
		clickLinkWithImage("edit.png");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("project.step1"));
		verifyProject("dataentry/projectIg", 0);
		
		// export to .riv file
		getTestContext().setResourceBundleName("messages/messages");
		assertLinkPresentWithImage("export_riv.gif");
		String pageUrl = getTestingEngine().getPageURL().toURI().toString();
		clickLinkWithImage("export_riv.gif");
		clickLink("dd_local");
		File f = folder.newFile("project.riv"); 
		saveAs(f);
		assertTrue(isZipFile(f));
		
		// delete existing project
		gotoPage(pageUrl);
		assertLinkPresentWithImage("delete.gif");
		clickLinkWithImage("delete.gif");
		clickButtonWithText("Delete item");
		assertLinkNotPresentWithImage("delete.gif");
		
		// import exported file and verify data is the same
		importProject(f, "igpj", false, false, "T3st Santa Cruz River Transport");
		clickLinkWithImage("edit.png");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("project.step1"));
		verifyProject("dataentry/projectIg", 0);
	}
	
	@Test
	public void importProjectIg41() throws URISyntaxException, IOException {
		importProject(ImportFile.ProjectV41, "igpj", false, false, "T3st Santa Cruz River Transport");
		clickLinkWithImage("edit.png");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("project.step1"));
		verifyProject("dataentry/projectIg", 0);
		
		// export to .riv file
		getTestContext().setResourceBundleName("messages/messages");
		assertLinkPresentWithImage("export_riv.gif");
		String pageUrl = getTestingEngine().getPageURL().toURI().toString();
		clickLinkWithImage("export_riv.gif");
		clickLink("dd_local");
		File f = folder.newFile("project.riv"); 
		saveAs(f);
		assertTrue(isZipFile(f));
		
		// delete existing profile
		gotoPage(pageUrl);
		assertLinkPresentWithImage("delete.gif");
		clickLinkWithImage("delete.gif");
		clickButtonWithText("Delete item");
		assertLinkNotPresentWithImage("delete.gif");
		
		// import exported file and verify data is the same
		importProject(f, "igpj", false, false, "T3st Santa Cruz River Transport");
		clickLinkWithImage("edit.png");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("project.step1"));
		verifyProject("dataentry/projectIg", 0);
	}

	@Test
	public void importProjectNig41() throws URISyntaxException, IOException {
		importProject(ImportFile.ProjectNig41, "nigpj", false, false, "Example Case: Community Earth Dam");
		clickLinkWithImage("edit.png");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("project.step1"));
		verifyProjectNig("dataentry/projectNig", 0, false);
		
		// export to .riv file
		getTestContext().setResourceBundleName("messages/messages");
		assertLinkPresentWithImage("export_riv.gif");
		String pageUrl = getTestingEngine().getPageURL().toURI().toString();
		clickLinkWithImage("export_riv.gif");
		clickLink("dd_local");
		File f = folder.newFile("project.riv"); 
		saveAs(f);
		assertTrue(isZipFile(f));
		
		// delete existing profile
		gotoPage(pageUrl);
		assertLinkPresentWithImage("delete.gif");
		clickLinkWithImage("delete.gif");
		clickButtonWithText("Delete item");
		assertLinkNotPresentWithImage("delete.gif");
		
		// import exported file and verify data is the same
		importProject(f, "nigpj", false, false, "Example Case: Community Earth Dam");
		clickLinkWithImage("edit.png");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("project.step1"));
		verifyProjectNig("dataentry/projectNig", 0, false);
	}
	
	@Test
	public void importProjectNig40() throws URISyntaxException, IOException {
		importProject(ImportFile.ProjectNig40, "nigpj", false, false, "Example Case: Community Earth Dam");
		clickLinkWithImage("edit.png");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("project.step1"));
		verifyProjectNig("dataentry/projectNig", 0, true);
		
		// export to .riv file
		getTestContext().setResourceBundleName("messages/messages");
		assertLinkPresentWithImage("export_riv.gif");
		String pageUrl = getTestingEngine().getPageURL().toURI().toString();
		clickLinkWithImage("export_riv.gif");
		clickLink("dd_local");
		File f = folder.newFile("project.riv"); 
		saveAs(f);
		assertTrue(isZipFile(f));
		
		// delete existing profile
		gotoPage(pageUrl);
		assertLinkPresentWithImage("delete.gif");
		clickLinkWithImage("delete.gif");
		clickButtonWithText("Delete item");
		assertLinkNotPresentWithImage("delete.gif");
		
		// import exported file and verify data is the same
		importProject(f, "nigpj", false, false, "Example Case: Community Earth Dam");
		clickLinkWithImage("edit.png");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("project.step1"));
		verifyProjectNig("dataentry/projectNig", 0, true);
	}
}