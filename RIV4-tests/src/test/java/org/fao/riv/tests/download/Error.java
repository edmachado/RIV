package org.fao.riv.tests.download;

import static net.sourceforge.jwebunit.junit.JWebUnit.assertTitleEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLink;
import static net.sourceforge.jwebunit.junit.JWebUnit.closeBrowser;
import static net.sourceforge.jwebunit.junit.JWebUnit.getMessage;
import static net.sourceforge.jwebunit.junit.JWebUnit.getTestingEngine;
import static net.sourceforge.jwebunit.junit.JWebUnit.gotoPage;
import static net.sourceforge.jwebunit.junit.JWebUnit.saveAs;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.fao.riv.tests.utils.WebTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class Error extends WebTestUtil {

	@Rule
    public TemporaryFolder folder = new TemporaryFolder();
	
	 @BeforeClass 
	 public static void setUpClass() {      
	       System.out.println("     test Error");
	 }
	
	 @Before
	 public void before() {
		 login();
		    
	 }
	
	@After
    public void close() {
		clickLink("logoff");
		closeBrowser();
    }
	
	@Test
	public void testError() throws Exception {
		gotoPage(getTestingEngine().getPageURL().toString().replace("/home", "/help/error"));
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("error.unknown"));
		clickLink("getLog");
		File f = folder.newFile("errorlog.zip");
		saveAs(f);
		assertTrue(isZipFile(f));
	}
}