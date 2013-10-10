package org.fao.riv.tests.calculations;

import static net.sourceforge.jwebunit.junit.JWebUnit.clickLink;
import static net.sourceforge.jwebunit.junit.JWebUnit.closeBrowser;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.fao.riv.tests.utils.WebTestUtil;

public class Incremental extends WebTestUtil {
	
	 @BeforeClass 
	 public static void setUpClass() {      
	       System.out.println("     test incremental");
	 }
	
	@Before
	public void deleteExisting() {
		login();
		// delete IG projects
		deletePros(true, true, false);
		deletePros(true, true, true);
	}
	
	@After
    public void close() {
		clickLink("logoff");
        closeBrowser();
    }
	
	@Test
	@Ignore
	public void testIncremental() {
		
	}
}