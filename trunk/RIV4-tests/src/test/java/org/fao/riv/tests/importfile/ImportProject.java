package org.fao.riv.tests.importfile;


import static net.sourceforge.jwebunit.junit.JWebUnit.clickLink;
import static net.sourceforge.jwebunit.junit.JWebUnit.closeBrowser;

import java.io.IOException;
import java.net.URISyntaxException;

import org.fao.riv.tests.WebTest;
import org.fao.riv.utils.ImportFile;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ImportProject extends WebTest {

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
	}
	
	@Test
	public void importProjectIg41() throws URISyntaxException, IOException {
		importProject(ImportFile.ProjectV41, "igpj", false, false, "T3st Santa Cruz River Transport");
		
	}

	@Test
	public void importProjectNig41() throws URISyntaxException, IOException {
		importProject(ImportFile.ProjectNig41, "nigpj", false, false, "Example Case: Community Earth Dam");
	}
	
	@Test
	public void importProjectNig40() throws URISyntaxException, IOException {
		importProject(ImportFile.ProjectNig40, "nigpj", false, false, "Example Case: Community Earth Dam");
	}
}