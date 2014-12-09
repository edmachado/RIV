package org.fao.riv.tests.dataentry;

import static net.sourceforge.jwebunit.junit.JWebUnit.clickLink;
import static net.sourceforge.jwebunit.junit.JWebUnit.closeBrowser;

import org.fao.riv.tests.utils.ImportFile;
import org.fao.riv.tests.utils.WebTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class IgProjectNoCycles extends WebTestUtil {
	
	 @BeforeClass 
	 public static void start() {      
	       System.out.println("     test IgProjectNoCycles");
	 }

	@Before
	public void deleteExisting() {
		login();
		deletePros(true, true);
	}
	
	@After
   public void close() {
		clickLink("logoff");
		closeBrowser();
   }
	
	@Test
	public void noCycles() {
		importProject(ImportFile.ProjectV41NoCycles, "igpj", false, false, "Цех по производству мелкотоварных молочных продуктов");
		
	}
	

}
