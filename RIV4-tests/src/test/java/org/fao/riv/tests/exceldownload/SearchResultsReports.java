package org.fao.riv.tests.exceldownload;

import static net.sourceforge.jwebunit.junit.JWebUnit.assertLinkPresentWithImage;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLink;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLinkWithImage;
import static net.sourceforge.jwebunit.junit.JWebUnit.closeBrowser;
import static net.sourceforge.jwebunit.junit.JWebUnit.getMessage;
import static net.sourceforge.jwebunit.junit.JWebUnit.saveAs;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.fao.riv.tests.utils.ImportFile;
import org.fao.riv.tests.utils.WebTestUtil;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class SearchResultsReports extends WebTestUtil {
	String igTitle;
	
	@Rule
    public TemporaryFolder folder = new TemporaryFolder();
	
	 @BeforeClass 
	 public static void setUpClass() {      
	       System.out.println("     test SearchResultsReports");
	 }
	
	@After
    public void close() {
		clickLink("logoff");
        closeBrowser();
    }
	
	@Test
	public void profileIGResults() throws IOException {
		login();
		deletePros(false, true);
		
		// import a few profiles
		importProfile(ImportFile.ProfileIgV22, "igpf", false, false, "Artesanal Clothing Workshop");
		importProfile(ImportFile.ProfileIgV20, "igpf_no", false, true, "Bagre Tanchachin");
		// importProfile ends at results page
		
		// test pdf
		assertLinkPresentWithImage("pdf.gif");
		clickLinkWithImage("pdf.gif");
		File f = folder.newFile("resultsIgpf.pdf"); 
		saveAs(f);
		String contents = extractPdfText(f);
		assertTrue(contents.contains(getMessage("profile.report.results.IG")));

		// test xls
		assertLinkPresentWithImage("xls.gif");
		clickLinkWithImage("xls.gif");
		f = folder.newFile("resultsIgpf.xls"); 
		saveAs(f);
		testXls(f, getMessage("profile.report.results.IG"));
	}
	
	@Test
	public void profileNIGResults() throws IOException {
		login();
		deletePros(false, false);
		
		// import a nig profiles
		importProfile(ImportFile.ProfileNig16, "nigpf_no", false, true, "Community Health Centre");
		// importProfile ends at results page
		
		// test pdf
		assertLinkPresentWithImage("pdf.gif");
		clickLinkWithImage("pdf.gif");
		File f = folder.newFile("resultsNigpf.pdf"); 
		saveAs(f);
		String contents = extractPdfText(f);
		assertTrue(contents.contains(getMessage("profile.report.results.NIG")));

		// test xls
		assertLinkPresentWithImage("xls.gif");
		clickLinkWithImage("xls.gif");
		f = folder.newFile("resultsNigpf.xls"); 
		saveAs(f);
		testXls(f, getMessage("profile.report.results.NIG"));
	}
	
	@Test
	public void projectIGResults() throws IOException {
		login();
		deletePros(true, true);
		
		// import a few projects
		importProject(ImportFile.ProjectV16, "igpj", false, true, "Test IG Project");
		importProject(ImportFile.ProjectV20, "igpj", false, false, "Tomate Curungueo");
		importProject(ImportFile.ProjectV31Generic, "igpj", true, false, "EXPLOTACION DE BOVINOS PARA DOBLE PROPOSITO");
		// ends at results page
		
		// test pdf
		assertLinkPresentWithImage("pdf.gif");
		clickLinkWithImage("pdf.gif");
		File f = folder.newFile("resultsIgpj.pdf"); 
		saveAs(f);
		String contents = extractPdfText(f);
		assertTrue(contents.contains(getMessage("project.report.results.IG")));

		// test xls
		assertLinkPresentWithImage("xls.gif");
		clickLinkWithImage("xls.gif");
		f = folder.newFile("resultsIgpj.xls"); 
		saveAs(f);
		testXls(f, getMessage("project.report.results.IG"));
	}
	
	@Test
	public void projectNIGResults() throws IOException {
		login();
		deletePros(true, false);
		
		// import a few projects
		importProject(ImportFile.ProjectNig16, "nigpj", false, false, "Access Road & Bridge");
		// ends at results page
		
		// test pdf
		assertLinkPresentWithImage("pdf.gif");
		clickLinkWithImage("pdf.gif");
		File f = folder.newFile("resultsNigpj.pdf"); 
		saveAs(f);
		String contents = extractPdfText(f);
		assertTrue(contents.contains(getMessage("project.report.results.NIG")));

		// test xls
		assertLinkPresentWithImage("xls.gif");
		clickLinkWithImage("xls.gif");
		f = folder.newFile("resultsNigpj.xls"); 
		saveAs(f);
		testXls(f, getMessage("project.report.results.NIG"));
	}
}