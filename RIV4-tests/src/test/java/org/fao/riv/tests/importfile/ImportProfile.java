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

public class ImportProfile extends WebTest {

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
	public void importProfileIG40() throws IOException, URISyntaxException {
		importProfile(ImportFile.ProfileIgV40, "igpf_no", false, false, "T3st Irrigation project");
	
	}
	
	@Test
	public void importProfileNig40() throws URISyntaxException, IOException {
		importProfile(ImportFile.ProfileNig40, "nigpf_no", false, false, "Community Health Centre");
	}
}