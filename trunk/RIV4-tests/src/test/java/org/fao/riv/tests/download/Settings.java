package org.fao.riv.tests.download;

import static net.sourceforge.jwebunit.junit.JWebUnit.assertTitleEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLink;
import static net.sourceforge.jwebunit.junit.JWebUnit.closeBrowser;
import static net.sourceforge.jwebunit.junit.JWebUnit.getMessage;
import static net.sourceforge.jwebunit.junit.JWebUnit.saveAs;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.fao.riv.tests.utils.WebTestUtil;

public class Settings extends WebTestUtil {

	@Rule
    public TemporaryFolder folder = new TemporaryFolder();
	
	 @BeforeClass 
	 public static void setUpClass() {      
	       System.out.println("     test downloadSettings");
	 }
	
	 @Before
	 public void before() {
		 login();
	 }
	
	@After
    public void close() {
		//clickLink("logoff");
		closeBrowser();
    }
	
	@Test
	public void testSettings() throws Exception {
		clickLink("gotoSettings");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("mainMenu.config"));
		clickLink("export");
		File f = folder.newFile("settings.riv");
		saveAs(f);
		assertTrue(isZipFile(f));
	}

}