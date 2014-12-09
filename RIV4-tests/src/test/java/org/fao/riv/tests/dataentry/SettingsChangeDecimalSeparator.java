package org.fao.riv.tests.dataentry;

import static net.sourceforge.jwebunit.junit.JWebUnit.assertElementNotPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextFieldEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTitleEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLink;
import static net.sourceforge.jwebunit.junit.JWebUnit.closeBrowser;
import static net.sourceforge.jwebunit.junit.JWebUnit.getMessage;
import static net.sourceforge.jwebunit.junit.JWebUnit.setTextField;

import org.fao.riv.tests.utils.ImportFile;
import org.fao.riv.tests.utils.WebTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SettingsChangeDecimalSeparator extends WebTestUtil {
	
	 @BeforeClass 
	 public static void setUpClass() {      
	       System.out.println("     test SettingsChangeDecimalSeparator");
	 }
	
	@Before
	public void before() {
		login();
	}
	
	@After
    public void close() {
		importSettings(ImportFile.Settings20);
		clickLink("logoff");
		closeBrowser();
    }
	
	@Test
	public void testSettingsChangeDecimalSeparator() throws Exception {
		// change decimal separator, exchange rate and discount rate in settings
		String settingsTitle = getMessage("ruralInvest")+" :: "+getMessage("mainMenu.config");
		
    	clickLink("gotoSettings");
		assertTitleEquals(settingsTitle);
		
		setTextField("decimalSeparator", ",");
		setTextField("thousandSeparator", ".");
		setTextField("discountRate", "1,1");
		setTextField("exchRate", "2,2");
		
		rivSubmitForm();
		assertTitleEquals(settingsTitle);
		assertElementNotPresent("errorbox");
		assertTextFieldEquals("decimalSeparator", ",");
		assertTextFieldEquals("thousandSeparator", ".");
		assertTextFieldEquals("discountRate", "1,1");
		assertTextFieldEquals("exchRate", "2,2");
	}
}