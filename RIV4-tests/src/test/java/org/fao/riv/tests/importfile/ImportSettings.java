package org.fao.riv.tests.importfile;

import static net.sourceforge.jwebunit.junit.JWebUnit.clickLink;
import static net.sourceforge.jwebunit.junit.JWebUnit.closeBrowser;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.fao.riv.tests.WebTest;
import org.fao.riv.utils.ImportFile;

public class ImportSettings extends WebTest {
	
	@BeforeClass 
	 public static void setUpClass() {      
	       System.out.println("     test ImportSettings");
	 }
	
	@Before
	public void before() {
		login();	
	}
	
	@After
    public void close() {
		// Settings20 have right setting for import profile and import project tests
		importSettings(ImportFile.Settings20);
		clickLink("logoff");
        closeBrowser();
    }
	
//	@AfterClass
//	public static void tearDown() {
//		importSettings(ImportFile.Settings20);
//		
//	}
	
	@Test
    public void import1o6() {
		importSettings(ImportFile.Settings16);
		//TODO: test data fields
    }
    
    @Test
    public void import2o0() {
		importSettings(ImportFile.Settings20);
    	//TODO: test data fields
    }
    
    @Test
    public void import3o2() throws Exception {
		importSettings(ImportFile.Settings32);
		validateSettings("v32");
    }
    
    @Test
    public void import4o0() throws Exception {
		importSettings(ImportFile.Settings40);
		validateSettings("v40");
    }
}