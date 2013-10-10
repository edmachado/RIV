package org.fao.riv.tests.download;

import static net.sourceforge.jwebunit.junit.JWebUnit.assertFormElementPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertFormPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextFieldEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTitleEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.beginAt;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLink;
import static net.sourceforge.jwebunit.junit.JWebUnit.closeBrowser;
import static net.sourceforge.jwebunit.junit.JWebUnit.getMessage;
import static net.sourceforge.jwebunit.junit.JWebUnit.saveAs;
import static net.sourceforge.jwebunit.junit.JWebUnit.setTextField;
import static net.sourceforge.jwebunit.junit.JWebUnit.submit;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.fao.riv.tests.TestApp;
import org.fao.riv.tests.utils.WebTestUtil;

public class Error extends WebTestUtil {

	@Rule
    public TemporaryFolder folder = new TemporaryFolder();
	
	 @BeforeClass 
	 public static void setUpClass() {      
	       System.out.println("     test Error");
	 }
	
	 @Before
	 public void login() {
		 beginAt("/help/error");
			assertFormPresent("login");
			assertFormElementPresent("j_username");
		    assertFormElementPresent("j_password");
		    setTextField("j_username", TestApp.username);
		    setTextField("j_password", TestApp.password);
		    assertTextFieldEquals("j_username", TestApp.username);
		    submit();
		    assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("error.unknown"));
	 }
	
	@After
    public void close() {
		clickLink("logoff");
		closeBrowser();
    }
	
	@Test
	public void testError() throws Exception {
		clickLink("getLog");
		File f = folder.newFile("errorlog.zip");
		saveAs(f);
		assertTrue(isZipFile(f));
	}
}