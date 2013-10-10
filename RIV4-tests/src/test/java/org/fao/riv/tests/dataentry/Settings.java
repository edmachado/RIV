package org.fao.riv.tests.dataentry;

import static net.sourceforge.jwebunit.junit.JWebUnit.assertElementNotPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertImageValid;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTitleEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.checkCheckbox;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLink;
import static net.sourceforge.jwebunit.junit.JWebUnit.closeBrowser;
import static net.sourceforge.jwebunit.junit.JWebUnit.getMessage;
import static net.sourceforge.jwebunit.junit.JWebUnit.getTestContext;
import static net.sourceforge.jwebunit.junit.JWebUnit.selectOption;
import static net.sourceforge.jwebunit.junit.JWebUnit.setTextField;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.fao.riv.tests.utils.ImportFile;
import org.fao.riv.tests.utils.TestTable;
import org.fao.riv.tests.utils.WebTestUtil;

public class Settings extends WebTestUtil {
	
	 @BeforeClass 
	 public static void setUpClass() {      
	       System.out.println("     test Settings");
	 }
	
	@Before
	public void deleteExisting() {
		login();
		// remove existing profiles, projects and settings
		deletePros(false, true, false);
		deletePros(false, true, true);
		deletePros(false, false, false);
		deletePros(false, false, true);
		deletePros(true, true, false);
		deletePros(true, true, true);
		deletePros(true, false, false);
		deletePros(true, false, true);
		deleteAppConfigs();
	}
	
	@After
    public void close() {

		// reset settings for other tests
		deleteAppConfigs();
		importSettings(ImportFile.Settings20);
		
		
		clickLink("logoff");
		closeBrowser();
    }
	
	@Test
	public void addSettings() throws Exception {
		
		
		// add new settings
		String settingsTitle = getMessage("ruralInvest")+" :: "+getMessage("mainMenu.config");
		HashMap<String, String> titles = new HashMap<String, String>();
    	titles.put("offices", getMessage("ruralInvest")+" :: "+getMessage("fieldOffice.fieldOffices"));
    	titles.put("categories", getMessage("ruralInvest")+" :: "+getMessage("projectCategory.categories"));
    	titles.put("beneficiaries", getMessage("ruralInvest")+" :: "+getMessage("beneficiary.beneficiaries"));
    	titles.put("enviroCategories", getMessage("ruralInvest")+" :: "+getMessage("enviroCategory.categories"));
    	titles.put("statuses", getMessage("ruralInvest")+" :: "+getMessage("projectStatus.statuses"));
    	getTestContext().setResourceBundleName("dataentry/settings");
		
    	clickLink("gotoSettings");
		assertTitleEquals(settingsTitle);
		
		setTextField("orgName", getMessage("v40.orgName"));
		selectOption("lang", getMessage("v40.lang"));
		setTextField("discountRate", getMessage("v40.discountRate"));
		setTextField("maxDuration", getMessage("v40.maxDuration"));
		setTextField("currencyName", getMessage("v40.currencyName"));
		setTextField("currencySym", getMessage("v40.currencySym"));
		setTextField("exchRate", getMessage("v40.exchRate"));
		setTextField("decimalLength", getMessage("v40.decimalLength"));
		setTextField("decimalSeparator", getMessage("v40.decimalSeparator"));
		setTextField("thousandSeparator", getMessage("v40.thousandSeparator"));
		setTextField("location1", getMessage("v40.location1"));
		setTextField("location2", getMessage("v40.location2"));
		setTextField("location3", getMessage("v40.location3"));
		setTextField("loan1Max", getMessage("v40.loan1Max"));
		setTextField("loan1GraceCapital", getMessage("v40.loan1GraceCapital"));
		setTextField("loan1GraceInterest", getMessage("v40.loan1GraceInterest"));
		setTextField("loan2Max", getMessage("v40.loan2Max"));
		setTextField("loan2GraceCapital", getMessage("v40.loan2GraceCapital"));
		setTextField("loan2GraceInterest", getMessage("v40.loan2GraceInterest"));
		setTextField("link1Text", getMessage("v40.link1Text"));
		setTextField("link1Url", getMessage("v40.link1Url"));
		setTextField("link2Text", getMessage("v40.link2Text"));
		setTextField("link2Url", getMessage("v40.link2Url"));
		setTextField("link3Text", getMessage("v40.link3Text"));
		setTextField("link3Url", getMessage("v40.link3Url"));
		setTextField("link4Text", getMessage("v40.link4Text"));
		setTextField("link4Url", getMessage("v40.link4Url"));
		setTextField("admin1Title", getMessage("v40.admin1Title"));
		setTextField("admin1Help", getMessage("v40.admin1Help"));
		if (Boolean.parseBoolean(getMessage("v40.admin1Enabled"))) { checkCheckbox("admin1Enabled"); }
		setTextField("admin2Title", getMessage("v40.admin2Title"));
		setTextField("admin2Help", getMessage("v40.admin2Help"));
		if (Boolean.parseBoolean(getMessage("v40.admin2Enabled"))) { checkCheckbox("admin2Enabled"); }
		setTextField("adminMisc1Title", getMessage("v40.adminMisc1Title"));
		setTextField("adminMisc1Help", getMessage("v40.adminMisc1Help"));
		if (Boolean.parseBoolean(getMessage("v40.adminMisc1Enabled"))) { checkCheckbox("adminMisc1Enabled"); }
		setTextField("adminMisc2Title", getMessage("v40.adminMisc2Title"));
		setTextField("adminMisc2Help", getMessage("v40.adminMisc2Help"));
		if (Boolean.parseBoolean(getMessage("v40.adminMisc2Enabled"))) { checkCheckbox("adminMisc2Enabled"); }
		setTextField("adminMisc3Title", getMessage("v40.adminMisc3Title"));
		setTextField("adminMisc3Help", getMessage("v40.adminMisc3Help"));
		if (Boolean.parseBoolean(getMessage("v40.adminMisc3Enabled"))) { checkCheckbox("adminMisc3Enabled"); }
		
		// org logo
		File file = ImportFile.Logo.getFile();
		setTextField("tempLogo",file.getAbsolutePath());
    	
		rivSubmitForm();
		assertTitleEquals(settingsTitle);
		assertElementNotPresent("errorbox");
		
		// Field Offices
		clickLink("gotoOffices");
		assertTitleEquals(titles.get("offices"));
		TestTable tt = new TestTable("row", "v40.office.", "addNewAppConfig", false, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addBlanks(2);
		tt.setIgnoreInputRows(new int[]{3});
		tt.testWithInput();
		
		// Project categories
		clickLink("gotoCategories");
		assertTitleEquals(titles.get("categories"));
		tt = new TestTable("row", "v40.category.", "addNewAppConfig", false, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addCheckboxParam("incomeGen", new String[] {"Income generating","Non income generating"})
		.addBlanks(2);
		tt.setIgnoreInputRows(new int[]{1,2});
		tt.testWithInput();
		
		// Beneficiaries
		clickLink("gotoBenefs");
		assertTitleEquals(titles.get("beneficiaries"));
		tt = new TestTable("row", "v40.beneficiary.", "addNewAppConfig", false, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addBlanks(2);
		tt.setIgnoreInputRows(new int[]{4});
		tt.testWithInput();
		
		// Environmental categories
		clickLink("gotoEnviros");
		assertTitleEquals(titles.get("enviroCategories"));
		tt = new TestTable("row", "v40.enviroCat.", "addNewAppConfig", false, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addBlanks(2);
		tt.setIgnoreInputRows(new int[]{2});
		tt.testWithInput();
		
		// Project statuses
		clickLink("gotoStatuses");
		assertTitleEquals(titles.get("statuses"));
		tt = new TestTable("row", "v40.status.", "addNewAppConfig", false, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addBlanks(2);
		tt.setIgnoreInputRows(new int[]{3});
		tt.testWithInput();
		
		getTestContext().setResourceBundleName("messages/messages");
		
		// validate that settings have been entered correctly
		validateSettings("v40");
		
	}
	
	@Test
	public void saveSettings() throws Exception {
		String settingsTitle = getMessage("ruralInvest")+" :: "+getMessage("mainMenu.config");
		clickLink("gotoSettings");
		assertTitleEquals(settingsTitle);
		rivSubmitForm();
		assertElementNotPresent("errorbox");
    	assertImageValid("orgLogo", null);
	}
}