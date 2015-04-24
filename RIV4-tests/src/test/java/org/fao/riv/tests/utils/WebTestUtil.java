package org.fao.riv.tests.utils;

import static net.sourceforge.jwebunit.junit.JWebUnit.assertFormElementPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertFormPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertImageValid;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertLinkPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertLinkPresentWithImage;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertRadioOptionSelected;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertSelectedOptionEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTableNotPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTablePresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTableRowCountEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTableRowsEqual;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextFieldEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextInElement;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextInTable;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTitleEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.beginAt;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickButton;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickElementByXPath;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLink;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLinkWithImage;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickRadioOption;
import static net.sourceforge.jwebunit.junit.JWebUnit.getElementById;
import static net.sourceforge.jwebunit.junit.JWebUnit.getElementsByXPath;
import static net.sourceforge.jwebunit.junit.JWebUnit.getMessage;
import static net.sourceforge.jwebunit.junit.JWebUnit.getTestContext;
import static net.sourceforge.jwebunit.junit.JWebUnit.getTestingEngine;
import static net.sourceforge.jwebunit.junit.JWebUnit.gotoPage;
import static net.sourceforge.jwebunit.junit.JWebUnit.selectOptionByValue;
import static net.sourceforge.jwebunit.junit.JWebUnit.setBaseUrl;
import static net.sourceforge.jwebunit.junit.JWebUnit.setTextField;
import static net.sourceforge.jwebunit.junit.JWebUnit.setWorkingForm;
import static net.sourceforge.jwebunit.junit.JWebUnit.submit;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.concurrent.Callable;

import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;
import net.sourceforge.jwebunit.junit.JWebUnit;
import net.sourceforge.jwebunit.util.TestingEngineRegistry;

import org.apache.catalina.LifecycleException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.fao.riv.tests.TestApp;
import org.fao.riv.tests.utils.InputParam.InputParamType;
import org.junit.Before;

public class WebTestUtil {
	@Before
    public void prepare() throws LifecycleException {
		JWebUnit.setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_HTMLUNIT);
		
		setBaseUrl(TestApp.appURL);
		getTestContext().setResourceBundleName("messages/messages");
	 }

//	@After
//	public void logout() throws LifecycleException {
		//clickLink("logoff");
		//TestApp.tomcatStop();
//	}
	
	public void goHome() {
		clickLink("goHome");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("mainMenu.home"));
	}
	
	public void deleteTableItems(String tableId) {
		while (getElementsByXPath("//table[@id='"+tableId+"']//a[img[@src[substring(., string-length() -9) = 'delete.gif']]]").size()>0) {
			clickElementByXPath("(//table[@id='"+tableId+"']//a[img[@src[substring(., string-length() -9) = 'delete.gif']]])[1]");
		}
	}
	
	public void assertCheckboxSelected(String name, boolean checked) {
		if (checked) {
			net.sourceforge.jwebunit.junit.JWebUnit.assertCheckboxSelected(name);
		} else {
			net.sourceforge.jwebunit.junit.JWebUnit.assertCheckboxNotSelected(name);
		}
	}
	
	public void login() {
		beginAt("/");
		assertFormPresent("login");
		assertFormElementPresent("j_username");
	    assertFormElementPresent("j_password");
	    setTextField("j_username", TestApp.username);
	    setTextField("j_password", TestApp.password);
	    assertTextFieldEquals("j_username", TestApp.username);
	    submit();
	    assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("mainMenu.home"));
	}
	
	public void importFile(File file) {
		setWorkingForm("form");
    	setTextField("file",file.getAbsolutePath());
    	rivSubmitForm();
    	file=null;
	}
	
	public void testAttachFile(String step1, String attachPageTitle) {
		// go back to step 1
			clickLink("step1");
			assertTitleEquals(step1);
			clickLink("attachFile");
			assertTitleEquals(attachPageTitle);
			importFile(ImportFile.ProfileIgV16.getFile());
			assertTitleEquals(step1);
			assertTableRowCountEquals("attachedFiles", 2);
			assertTableRowsEqual("attachedFiles", 0, new String[][] {
					{"",""},{"profile-ig-1.6.riv",""}
				});
	}
	
	private String cellValueFromXls(File f, int sheetNo, int row, int cell) {
		FileInputStream in=null;
		XSSFWorkbook workbook=null;
		String value=null;
		try {
			in = new FileInputStream(f);
			workbook  = new XSSFWorkbook(in);
			Sheet sheet = workbook.getSheetAt(sheetNo);
			value = sheet.getRow(row).getCell(cell).getStringCellValue();
		} catch (Exception e) {
			fail("Failure testing Excel file: "+e.getMessage());
		} finally {
			try {
				in.close();
				in=null;
				workbook=null;
			} catch (IOException e) {
				fail("Couldn't close Excel file. "+e.getMessage());
			}
		}
		
		return value;
	}
	
	protected void testXls(File f, String title) {
		String value = cellValueFromXls(f, 0, 0, 0);
		assertTrue(value.equals(title));
	}
	
	public void rivSubmitForm() {
		clickButton("submit");
	}
	
	/*
	 * 2-letter language code
	 */
	protected void setLanguage(String language) {
		// set language
		clickLink("welcome");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("mainMenu.config.users.addEdit"));
		
		getTestContext().setResourceBundleName("messages/messages_"+language);
		
		selectOptionByValue("lang", language);
		rivSubmitForm();
		String newTitle = getMessage("ruralInvest")+" :: "+getMessage("user.users");
		assertTitleEquals(newTitle);
	}
	
	public void goToPro(boolean project, boolean incGen, boolean complete) {
		goToResults(project, incGen, complete);
		assertLinkPresentWithImage("edit.png");
		clickLinkWithImage("edit.png");
		if (project) {
			assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("project.step1"));
		} else {
			assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("profile.step1"));
		}
	}
	
	public void goToResults(boolean project, boolean incGen, boolean complete) {
		String type= project ? incGen ? "igpj" : "nigpj" : incGen ? "igpf" : "nigpf";
		type=complete? type+"_yes" : type+"_no";
		
		clickLink("goHome");
		clickLink(type);
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("search.searchResults"));
	}
	
	public void reset() {
		clickLink("gotoSettings");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("mainMenu.config"));
		assertLinkPresent("reset");
		clickLink("reset");	
	}
	
	public void deletePros(boolean project, boolean incGen) {
		String url = String.format("/help/deleteAll?type=%s&ig=%s", 
				project ? "project" : "profile", Boolean.toString(incGen));
		gotoPage(getTestingEngine().getPageURL().toString().replace("/home", url));
		assertTitleEquals("RuralInvest :: Home");
	}
	
	public void importProject(ImportFile file, String type, boolean isGeneric, boolean missingCapital, String projName) {
		importProject( file.getFile(),  type,  isGeneric,  missingCapital,  projName);
	}
	public void importProject(ImportFile file, String type, boolean isGeneric, boolean missingCapital, String projName, boolean isCopy) {
		importProject( file.getFile(),  type,  isGeneric,  missingCapital,  projName, isCopy);
	}
	public void importProject(File file, String type, boolean isGeneric, boolean missingCapital, String projName) {
		importProject(file, type, isGeneric, missingCapital, projName, false);
	}
	private void importProject(File file, String type, boolean isGeneric, boolean missingCapital, String projName, boolean isCopy) {
		System.out.println("importing "+file.getAbsolutePath());
		boolean isIG = type.startsWith("ig");
		String resultsTitle = getMessage("ruralInvest")+" :: "+getMessage("search.searchResults");
		String[] titles = projectStepTitles(isIG);
		
		clickLink("goHome");
		
		// import file
		clickLink("importProjectIg");
		importFile(file);
		if (isGeneric) {
			assertTitleEquals(getMessage("ruralInvest")+" :: Import");
			assertFormElementPresent("genericExchange");
			rivSubmitForm();
		} else if (isCopy) {
			assertTitleEquals(getMessage("ruralInvest")+" :: Import");
			clickRadioOption("overwriteOk", "false");
			rivSubmitForm();
		}
		
		// go through all steps
		for (int i=0; i<13; i++) {
			assertTitleEquals(titles[i]);
			if (i==10 && missingCapital) {
				setTextField("capitalOwn", "100");
			}
			rivSubmitForm();
		}
		
		assertTitleEquals(resultsTitle);
		
		// check that profile visible in results
		assertTablePresent("results");
		assertTextInTable("results",projName);
	}
	
	
	public void importProfile(ImportFile file, String type, boolean isGeneric, boolean missingBenefFamilies, String profName) {
		importProfile( file.getFile(),  type,  isGeneric,  missingBenefFamilies,  profName);	
	}
	public void importProfile(File file, String type, boolean isGeneric, boolean missingBenefFamilies, String profName) {
				
		System.out.println("importing "+file.toString());
		boolean isIG = type.startsWith("ig");
		
		String[] titles = profileStepTitles(isIG);

		clickLink("goHome");
		
		// import file
		clickLink("importProfileIg");
		importFile(file);
		if (isGeneric) {
			assertTitleEquals(getMessage("ruralInvest")+" :: Import");
			assertFormElementPresent("genericExchange");
			rivSubmitForm();
		}
		
		// go through all steps
		for (int i=0; i<9; i++) {
			assertTitleEquals(titles[i]);
			if (i==0 && missingBenefFamilies) {
				setTextField("benefFamilies", "12");
			}
			rivSubmitForm();
		}
		
		// should be back at home
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("search.searchResults"));
		
		// check that profile visible in results
		assertTablePresent("results");
		assertTextInTable("results",profName);
	}
	
	protected void importSettings(ImportFile file) {
		System.out.println("importing "+file.toString());
		login();
		
		// remove existing profiles, projects and AppConfigs
		reset();
		
    	clickLink("gotoImportSettings");
    	assertTitleEquals(getMessage("ruralInvest")+" :: Import");
    	importFile(file.getFile());
    	assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("mainMenu.config"));
    	assertImageValid("orgLogo", null);
	}
	
    protected void validateSettings(String version) throws Exception {
    	HashMap<String, String> titles = new HashMap<String, String>();
    	titles.put("settings", getMessage("ruralInvest")+" :: "+getMessage("mainMenu.config"));
    	titles.put("offices", getMessage("ruralInvest")+" :: "+getMessage("fieldOffice.fieldOffices"));
    	titles.put("categories", getMessage("ruralInvest")+" :: "+getMessage("projectCategory.categories"));
    	titles.put("beneficiaries", getMessage("ruralInvest")+" :: "+getMessage("beneficiary.beneficiaries"));
    	titles.put("enviroCategories", getMessage("ruralInvest")+" :: "+getMessage("enviroCategory.categories"));
    	titles.put("statuses", getMessage("ruralInvest")+" :: "+getMessage("projectStatus.statuses"));
    	
    	getTestContext().setResourceBundleName("dataentry/settings");
    	
    	clickLink("gotoSettings");
		assertTitleEquals(titles.get("settings"));
		
		assertTextFieldEquals("orgName", getMessage(version+".orgName"));
		assertSelectedOptionEquals("lang", getMessage(version+".lang"));
		assertTextFieldEquals("discountRate", getMessage(version+".discountRate"));
		assertTextFieldEquals("maxDuration", getMessage(version+".maxDuration"));
		assertTextFieldEquals("currencyName", getMessage(version+".currencyName"));
		assertTextFieldEquals("currencySym", getMessage(version+".currencySym"));
		assertTextFieldEquals("exchRate", getMessage(version+".exchRate"));
		assertTextFieldEquals("decimalLength", getMessage(version+".decimalLength"));
		assertTextFieldEquals("decimalSeparator", getMessage(version+".decimalSeparator"));
		assertTextFieldEquals("thousandSeparator", getMessage(version+".thousandSeparator"));
		assertTextFieldEquals("location1", getMessage(version+".location1"));
		assertTextFieldEquals("location2", getMessage(version+".location2"));
		assertTextFieldEquals("location3", getMessage(version+".location3"));
		assertTextFieldEquals("loan1Max", getMessage(version+".loan1Max"));
		assertTextFieldEquals("loan1GraceCapital", getMessage(version+".loan1GraceCapital"));
		assertTextFieldEquals("loan1GraceInterest", getMessage(version+".loan1GraceInterest"));
		assertTextFieldEquals("loan2Max", getMessage(version+".loan2Max"));
		assertTextFieldEquals("loan2GraceCapital", getMessage(version+".loan2GraceCapital"));
		assertTextFieldEquals("loan2GraceInterest", getMessage(version+".loan2GraceInterest"));
		assertTextFieldEquals("link1Text", getMessage(version+".link1Text"));
		assertTextFieldEquals("link1Url", getMessage(version+".link1Url"));
		assertTextFieldEquals("link2Text", getMessage(version+".link2Text"));
		assertTextFieldEquals("link2Url", getMessage(version+".link2Url"));
		assertTextFieldEquals("link3Text", getMessage(version+".link3Text"));
		assertTextFieldEquals("link3Url", getMessage(version+".link3Url"));
		assertTextFieldEquals("link4Text", getMessage(version+".link4Text"));
		assertTextFieldEquals("link4Url", getMessage(version+".link4Url"));
		assertTextFieldEquals("admin1Title", getMessage(version+".admin1Title"));
		assertTextFieldEquals("admin1Help", getMessage(version+".admin1Help"));
		assertCheckboxSelected("admin1Enabled", Boolean.parseBoolean(getMessage(version+".admin1Enabled")));
		assertTextFieldEquals("admin2Title", getMessage(version+".admin2Title"));
		assertTextFieldEquals("admin2Help", getMessage(version+".admin2Help"));
		assertCheckboxSelected("admin2Enabled", Boolean.parseBoolean(getMessage(version+".admin2Enabled")));
		assertTextFieldEquals("adminMisc1Title", getMessage(version+".adminMisc1Title"));
		assertTextFieldEquals("adminMisc1Help", getMessage(version+".adminMisc1Help"));
		assertCheckboxSelected("adminMisc1Enabled", Boolean.parseBoolean(getMessage(version+".adminMisc1Enabled")));
		assertTextFieldEquals("adminMisc2Title", getMessage(version+".adminMisc2Title"));
		assertTextFieldEquals("adminMisc2Help", getMessage(version+".adminMisc2Help"));
		assertCheckboxSelected("adminMisc2Enabled", Boolean.parseBoolean(getMessage(version+".adminMisc2Enabled")));
		assertTextFieldEquals("adminMisc3Title", getMessage(version+".adminMisc3Title"));
		assertTextFieldEquals("adminMisc3Help", getMessage(version+".adminMisc3Help"));
		assertCheckboxSelected("adminMisc3Enabled", Boolean.parseBoolean(getMessage(version+".adminMisc3Enabled")));
		
		assertImageValid("orgLogo", null);
		//TODO actually compare the logo file
		
		// Field Offices
		clickLink("gotoOffices");
		assertTitleEquals(titles.get("offices"));
		TestTable tt = new TestTable("row", version+".office.", "addNewAppConfig", false, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addBlanks(2);
		tt.testOutput();
		
		// Project categories
		clickLink("gotoCategories");
		assertTitleEquals(titles.get("categories"));
		tt = new TestTable("row", version+".category.", "addNewAppConfig", false, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addCheckboxParam("incomeGen", new String[] {"Income generating","Non income generating"}).addBlanks(2);
		tt.testOutput();
		
		// Beneficiaries
		clickLink("gotoBenefs");
		assertTitleEquals(titles.get("beneficiaries"));
		tt = new TestTable("row", version+".beneficiary.", "addNewAppConfig", false, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addBlanks(2);
		tt.testOutput();
		
		// Environmental categories
		clickLink("gotoEnviros");
		assertTitleEquals(titles.get("enviroCategories"));
		tt = new TestTable("row", version+".enviroCat.", "addNewAppConfig", false, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addBlanks(2);
		tt.testOutput();
		
		// Project statuses
		clickLink("gotoStatuses");
		assertTitleEquals(titles.get("statuses"));
		tt = new TestTable("row", version+".status.", "addNewAppConfig", false, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addBlanks(2);
		tt.testOutput();
		
		getTestContext().setResourceBundleName("messages/messages");
    }
    
    protected String[] projectStepTitles(boolean incomeGen) {
    	String[] titles = new String[13];
		for (int i=0;i<13;i++) {
			if ((i==10 &! incomeGen) || (i==9 && incomeGen)) {
				titles[i] = getMessage("ruralInvest")+" :: "+getMessage("reference.reference");
			} else if (!incomeGen && (i==3 || i==8 || i==9 || i==10)) {
				titles[i] = getMessage("ruralInvest")+" :: "+getMessage("project.step"+(i+1)+".nongen");
			} else {
				titles[i] = getMessage("ruralInvest")+" :: "+getMessage("project.step"+(i+1));
			}
		}
		return titles;
    }
    
    protected String[] profileStepTitles(boolean incomeGen) {
    	String[] titles = new String[9];
 		for (int i=0;i<9;i++) {
 			if (i==6) {
 				titles[i]=getMessage("ruralInvest")+" :: "+getMessage("reference.reference");
 			} else if (i==5 &! incomeGen) {
 				titles[i]= getMessage("ruralInvest")+" :: "+getMessage("profile.step"+(i+1)+".nongen");
 			} else {
 				titles[i]= getMessage("ruralInvest")+" :: "+getMessage("profile.step"+(i+1));
 			}
 		}
 		return titles;
    }
    
    protected void verifyProjectNigTablesStep10(int year) {
    	TestTable tt = new TestTable("contributionTable"+year, "step10.year."+year+".contribution.", "newContrib", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}});
		tt.addParam("description")
		.addParam("donorOrderBy", InputParamType.SELECT, false)
//		.addParam("contribType", InputParamType.SELECT, false)
		.addParam("unitType")
		.addParam("unitNum").addParam("unitCost");
		tt.addParam("total", InputParamType.TEXT, true)
		.addBlanks(5);
		tt.testOutput();
    }
    
    protected void verifyProjectNigTablesStep11() {
    	// reference income
		TestTable tt = new TestTable("IncomeTable", "step11.income.", "addIncome", false, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitCost")//.addParam("transport")
		.addBlanks(4);
		tt.testOutput();
		
		// reference cost table
		tt = new TestTable("GoodsTable", "step11.input.", "addInput", false, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitCost").addParam("transport")
		.addBlanks(4);
		tt.testOutput();
		
		// reference labour table
		tt = new TestTable("LabourTable", "step11.labour.", "addLabour", false, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitCost")
		.addBlanks(4);
		tt.testOutput();
    }
    
    protected void verifyProjectNig(String properties, int indexResult, boolean simpleContributions) {
		getTestContext().setResourceBundleName("messages/messages");
    	String resultsTitle = getMessage("ruralInvest")+" :: "+getMessage("search.searchResults");
		String[] titles = projectStepTitles(false);

    	getTestContext().setResourceBundleName(properties);
    	assertTextFieldEquals("projectName",  getMessage("step1.projectName"));
		assertTextFieldEquals("userCode", getMessage("step1.userCode"));
		assertTextFieldEquals("exchRate", getMessage("step1.exchRate"));
		assertTextFieldEquals("duration", getMessage("step1.duration"));
		assertTextFieldEquals("location1", getMessage("step1.location1"));
		assertTextFieldEquals("location2", getMessage("step1.location2"));
		assertTextFieldEquals("location3", getMessage("step1.location3"));
		
		//TODO: set non-default checkbox and select values
		rivSubmitForm();
		assertTitleEquals(titles[1]);
		
		// STEP 2
		assertTextFieldEquals("benefName",getMessage("step2.benefName"));
		assertTextFieldEquals("beneDirectMen",getMessage("step2.beneDirectMen"));
		assertTextFieldEquals("beneDirectWomen", getMessage("step2.beneDirectWomen"));
		assertTextFieldEquals("beneDirectChild", getMessage("step2.beneDirectChild"));
		// TODO: test autocalc on benefDirectTotal
		assertTextFieldEquals("beneDirectNum",getMessage("step2.beneDirectNum"));
		assertTextFieldEquals("beneIndirectMen",getMessage("step2.beneIndirectMen"));
		assertTextFieldEquals("beneIndirectWomen",getMessage("step2.beneIndirectWomen"));
		assertTextFieldEquals("beneIndirectChild",getMessage("step2.beneIndirectChild"));
		// TODO: test autocalc on benefIndirectTotal
		assertTextFieldEquals("beneIndirectNum",getMessage("step2.beneIndirectNum"));
		assertTextFieldEquals("benefDesc",getMessage("step2.benefDesc"));
		rivSubmitForm();
		assertTitleEquals(titles[2]);
		
		// STEP 3
		assertTextFieldEquals("justification",getMessage("step3.justification"));
		assertTextFieldEquals("projDesc",getMessage("step3.projDesc"));
		assertTextFieldEquals("activities",getMessage("step3.activities"));
		rivSubmitForm();
		assertTitleEquals(titles[3]);
		
		// STEP 4
		assertTextFieldEquals("technology",getMessage("step4.technology"));
		assertTextFieldEquals("requirements",getMessage("step4.requirements"));
		rivSubmitForm();
		assertTitleEquals(titles[4]);
		
		// STEP 5
		assertTextFieldEquals("sustainability",getMessage("step5.sustainability"));
		assertTextFieldEquals("enviroImpact",getMessage("step5.enviroImpact"));
		assertTextFieldEquals("market",getMessage("step5.market"));
		rivSubmitForm();
		assertTitleEquals(titles[5]);
	
		// STEP 6
		assertTextFieldEquals("organization",getMessage("step6.organization"));
		assertTextFieldEquals("assumptions",getMessage("step6.assumptions"));
		rivSubmitForm();
		assertTitleEquals(titles[6]);
		
		// STEP 7
		verifyProjectNigTablesStep7();
		
		rivSubmitForm();
		assertTitleEquals(titles[7]);
		
		// STEP 8
		verifyProjectNigTablesStep8();
		rivSubmitForm();
		assertTitleEquals(titles[8]);
				
		// STEP 9
		int blocks = Integer.parseInt(getMessage("step9.block.count"));
		for (int i=1; i<=blocks; i++) {
			assertTextPresent(getMessage("step9.block."+i+".description"));
			assertTextInElement(i-1+"unitType", getMessage("step9.block."+i+".unitType"));
			assertTextInElement(i-1+"cycleLength", getMessage("step9.block."+i+".cycleLength"));
			assertTextInElement(i-1+"lengthUnit", getMessage("step9.block."+i+".lengthUnit"));
			assertTextInElement(i-1+"cyclePerYear", getMessage("step9.block."+i+".cyclePerYear"));
			
			// production pattern 
			for (int x=1; x<16; x++) {
				assertTextInElement("0prod"+x,getMessage("step9.block."+i+".pat"+x));
			}
			
			verifyProjectNigTablesStep9(i);
		}
		
		rivSubmitForm();
		assertTitleEquals(titles[9]);
		
		// STEP 10
		if (simpleContributions) {
			verifyProjectNigTablesStep10(1);
		} else {
			for (int year=1;year<=Integer.parseInt(getMessage("step1.duration"));year++) {
				verifyProjectNigTablesStep10(year);
			}
		}
		rivSubmitForm();
		assertTitleEquals(titles[10]);
		
		// STEP 11
		verifyProjectNigTablesStep11();
	
		rivSubmitForm();
		assertTitleEquals(titles[11]);
	
		// STEP 12
		assertRadioOptionSelected("reccCode", getMessage("step12.reccCode"));
		assertTextFieldEquals("reccDate", getMessage("step12.reccDate"));
		assertTextFieldEquals("reccDesc", getMessage("step12.reccDesc"));

		clickLink("showIndicators");
		verifyProjectIndicators(false);
		rivSubmitForm();
		assertTitleEquals(titles[12]);
		
		// STEP 13 
		verifyProjectIndicators(false);
		
		rivSubmitForm();
		assertTitleEquals(resultsTitle);
		
		// check indicators
		Table result = new Table();
		Row testRow = new Row();
		testRow.appendCell(getMessage("step1.projectName"));
		testRow.appendCell(getMessage("step1.userCode"));
		testRow.appendCell(getMessage("step1.technician"));
		testRow.appendCell(getMessage("step1.office"));
		testRow.appendCell(getMessage("step1.status"));
		testRow.appendCell(getMessage("step1.projCat"));
		testRow.appendCell(getMessage("step1.benefType"));
		testRow.appendCell(getMessage("step1.enviroCat"));
		testRow.appendCell(getMessage("result.investTotal"));
		testRow.appendCell(getMessage("result.investOwn"));
		testRow.appendCell(getMessage("result.investDonate"));
		testRow.appendCell(getMessage("result.investFinance"));
		testRow.appendCell(getMessage("result.employ"));
		testRow.appendCell(getMessage("result.investDirect"));
		testRow.appendCell(getMessage("result.investIndirect"));
		testRow.appendCell(getMessage("result.benefDirect"));
		testRow.appendCell(getMessage("result.benefIndirect"));
		testRow.appendCell("");
		testRow.appendCell("");
		testRow.appendCell("");
		result.appendRow(testRow);
		assertTableRowsEqual("results", 5+indexResult, result);
	}
    protected void verifyProjectNigTablesStep9(int i) {
    	// income
		TestTable tt = new TestTable("incomeTable"+(i-1), "step9.block."+i+".income.", "newIncome"+(i-1), true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType")
		.addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true)
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
		
		// input
		tt = new TestTable("inputTable"+(i-1), "step9.block."+i+".input.", "newInput"+(i-1), true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost").addParam("qtyIntern").addParam("qtyExtern", InputParamType.TEXT, true)
		.addParam("transport").addParam("total", InputParamType.TEXT, true)
		.addCollectionParam("donations", "donated", Integer.parseInt(getMessage("step2.donor.count")))
		.addParam("totalCash", InputParamType.TEXT, true)
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
		
		// labour
		tt = new TestTable("labourTable"+(i-1), "step9.block."+i+".labour.", "newLabour"+(i-1), true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType", InputParamType.SELECT, false)
		.addParam("unitNum")
		.addParam("unitCost").addParam("qtyIntern").addParam("qtyExtern", InputParamType.TEXT, true)
		.addParam("total", InputParamType.TEXT, true)
		.addCollectionParam("donations", "donated", Integer.parseInt(getMessage("step2.donor.count")))
		.addParam("totalCash", InputParamType.TEXT, true)
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
	
    }
    
    protected void verifyProjectNigTablesStep8() {
    	TestTable tt = new TestTable("inputTable", "step8.input", "addMaterial", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true)
		.addCollectionParam("donations", "donated", Integer.parseInt(getMessage("step2.donor.count")))
//		.addParam("statePublic").addParam("other1")
		.addParam("ownResource", InputParamType.TEXT, true)
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
		
		tt = new TestTable("labourTable", "step8.labour", "addLabour", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType", InputParamType.SELECT, false).addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true)
		.addCollectionParam("donations", "donated", Integer.parseInt(getMessage("step2.donor.count")))
//		.addParam("statePublic").addParam("other1")
		.addParam("ownResource", InputParamType.TEXT, true)
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
		
		tt = new TestTable("generalTable", "step8.general", "addMaintenance", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true)
		.addCollectionParam("donations", "donated", Integer.parseInt(getMessage("step2.donor.count")))
//		.addParam("statePublic").addParam("other1")
		.addParam("ownResource", InputParamType.TEXT, true)
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
    }
    
    protected void verifyProjectNigTablesStep7() {
    	// assets
		TestTable tt = new TestTable("assetsTable", "step7.asset.", "newAsset", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}});
		tt.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost");
		tt.addParam("total", InputParamType.TEXT, true).addParam("ownResources").addParam("donated").addParam("financed", InputParamType.TEXT, true);
		tt.addParam("econLife").addParam("maintCost").addParam("salvage").addParam("replace", InputParamType.CHECKBOX, false).addParam("yearBegin")
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
		
		// labour
		tt = new TestTable("LabourTable", "step7.labour.", "newLabour", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType", InputParamType.SELECT, false).addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true).addParam("ownResources").addParam("donated").addParam("financed", InputParamType.TEXT, true).addParam("yearBegin")
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
		
		// service
		tt = new TestTable("ServicesTable", "step7.service.", "newService", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true).addParam("ownResources").addParam("donated").addParam("financed", InputParamType.TEXT, true).addParam("yearBegin")
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
	tt.testOutput();
    }
    protected void verifyProjectTablesStep7(boolean hasWithout) {
    	TestTable tt = new TestTable("assetsTable", "step7.asset.", "newAsset", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}});
		tt.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost");
		tt.addParam("total", InputParamType.TEXT, true).addParam("ownResources").addParam("donated").addParam("financed", InputParamType.TEXT, true);
		tt.addParam("econLife").addParam("maintCost").addParam("salvage").addParam("replace", InputParamType.CHECKBOX, false).addParam("yearBegin")
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
		
		// labour
		tt = new TestTable("LabourTable", "step7.labour.", "newLabour", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType", InputParamType.SELECT, false).addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true).addParam("ownResources").addParam("donated").addParam("financed", InputParamType.TEXT, true).addParam("yearBegin")
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
		
		// service
		tt = new TestTable("ServicesTable", "step7.service.", "newService", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true).addParam("ownResources").addParam("donated").addParam("financed", InputParamType.TEXT, true).addParam("yearBegin")
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
		
		if (hasWithout) {
			// assets without project
			tt = new TestTable("assetsTableWo", "step7.assetWo.", "newAssetWo", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}});
			tt.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost");
			tt.addParam("total", InputParamType.TEXT, true).addParam("ownResources").addParam("donated").addParam("financed", InputParamType.TEXT, true);
			tt.addParam("econLife").addParam("maintCost").addParam("salvage").addParam("replace", InputParamType.CHECKBOX, false).addParam("yearBegin")
			.addParam("linked", InputParamType.LINKED, false)
			.addBlanks(5);
			tt.testOutput();
			
			// labour without project
			tt = new TestTable("LabourTableWo", "step7.labourWo.", "newLabourWo", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
			.addParam("description").addParam("unitType", InputParamType.SELECT, false).addParam("unitNum").addParam("unitCost")
			.addParam("total", InputParamType.TEXT, true).addParam("ownResources").addParam("donated").addParam("financed", InputParamType.TEXT, true).addParam("yearBegin")
			.addParam("linked", InputParamType.LINKED, false)
			.addBlanks(5);
			tt.testOutput();
			
			// service without project
			tt = new TestTable("ServicesTableWo", "step7.serviceWo.", "newServiceWo", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
			.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
			.addParam("total", InputParamType.TEXT, true).addParam("ownResources").addParam("donated").addParam("financed", InputParamType.TEXT, true).addParam("yearBegin")
			.addParam("linked", InputParamType.LINKED, false)
			.addBlanks(5);
			tt.testOutput();
		} else {
			assertTableNotPresent("assetsTableWo");
			assertTableNotPresent("LabourTableWo");
			assertTableNotPresent("ServicesTableWo");
		}
		
    }
    
    private void verifyProjectIndicators(boolean ig) {
    	assertTextInElement("result_projectName", getMessage("step1.projectName"));
		assertTextInElement("result_userCode", getMessage("step1.userCode"));
		assertTextInElement("result_technician", getMessage("step1.technician"));
		assertTextInElement("result_fieldOffice", getMessage("step1.office"));
		assertTextInElement("result_status", getMessage("step1.status"));
		assertTextInElement("result_category", getMessage("step1.projCat"));
		assertTextInElement("result_benefType", getMessage("step1.benefType"));
		assertTextInElement("result_enviroCat", getMessage("step1.enviroCat"));
//		assertTextInElement("result_appConfig1", getMessage(""));
//		assertTextInElement("result_appConfig2", getMessage(""));
		assertTextInElement("result_investTotal", getMessage("result.investTotal"));
		assertTextInElement("result_investOwn", getMessage("result.investOwn"));
		assertTextInElement("result_investDonated", getMessage("result.investDonate"));
		assertTextInElement("result_investmentFinanced", getMessage("result.investFinance"));
		assertTextInElement("result_annualEmployment", getMessage("result.employ"));
		if (ig) {
			assertTextInElement("result_annualIncome", getMessage("result.income"));
			assertTextInElement("result_workCapital", getMessage("result.wcTotal"));
			assertTextInElement("result_wcOwn", getMessage("result.wcOwn"));
			assertTextInElement("result_wcDonated", getMessage("result.wcDonate"));
			assertTextInElement("result_wcFinanced", getMessage("result.wcFinance"));
			assertTextInElement("result_totalCosts", getMessage("result.costTotal"));
			assertTextInElement("result_totalCostsOwn", getMessage("result.costOwn"));
			assertTextInElement("result_totalCostsDonated", getMessage("result.costDonate"));
			assertTextInElement("result_totalCostsFinanced", getMessage("result.costFinance"));
			assertTextInElement("result_npv", getMessage("result.npvAll"));
			assertTextInElement("result_irr", getMessage("result.irrAll"));
			assertTextInElement("result_npvWithDonation", getMessage("result.npvApplicant"));
			assertTextInElement("result_irrWithDonation", getMessage("result.irrApplicant"));
		} else {
			assertTextInElement("result_investPerDirect", getMessage("result.investDirect"));
			assertTextInElement("result_investPerIndirect", getMessage("result.investIndirect"));
			assertTextInElement("result_beneDirect", getMessage("result.benefDirect"));
			assertTextInElement("result_beneIndirect", getMessage("result.benefIndirect"));
		}
    }
    
    protected void verifyProjectTablesStep8(boolean hasWithout) {
    	// with project items
		TestTable tt = new TestTable("suppliesTable", "step8.supply.", "newSupply", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true).addParam("ownResources").addParam("external", InputParamType.TEXT, true)
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
		
		tt = new TestTable("personnelTable", "step8.personnel.", "newPersonnel", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType", InputParamType.SELECT, false).addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true).addParam("ownResources").addParam("external", InputParamType.TEXT, true)
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
		
		if (hasWithout) {
			// without project items
			tt = new TestTable("suppliesWoTable", "step8.supplyWo.", "newSupplyWo", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
			.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
			.addParam("total", InputParamType.TEXT, true).addParam("ownResources").addParam("external", InputParamType.TEXT, true)
			.addParam("linked", InputParamType.LINKED, false)
			.addBlanks(5);
			tt.testOutput();
			
			tt = new TestTable("personnelWoTable", "step8.personnelWo.", "newPersonnelWo", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
			.addParam("description").addParam("unitType", InputParamType.SELECT, false).addParam("unitNum").addParam("unitCost")
			.addParam("total", InputParamType.TEXT, true).addParam("ownResources").addParam("external", InputParamType.TEXT, true)
			.addParam("linked", InputParamType.LINKED, false)
			.addBlanks(5);
			tt.testOutput();
		} else {
			assertTableNotPresent("suppliesWoTable");
			assertTableNotPresent("personnelWoTable");
		}
    }
    
    protected void verifyProjectTablesStep9() {
    	TestTable tt = new TestTable("IncomeTable", "step10.income.", "addIncome", false, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitCost").addParam("transport")
		.addBlanks(4);
		tt.testOutput();
		
		// reference cost table
		tt = new TestTable("GoodsTable", "step10.input.", "addInput", false, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitCost").addParam("transport")
		.addBlanks(4);
		tt.testOutput();
		
		// reference labour table
		tt = new TestTable("LabourTable", "step10.labour.", "addLabour", false, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitCost")
		.addBlanks(4);
		tt.testOutput();
    }
    
    protected void verifyBlockTables(int i, String propertyBase) {
    	// income
		TestTable tt = new TestTable("incomeTable"+(i-1), propertyBase+"income.", "newIncome"+(i-1), true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost").addParam("qtyIntern").addParam("qtyExtern", InputParamType.TEXT, true)
		.addParam("transport").addParam("total", InputParamType.TEXT, true).addParam("totalCash", InputParamType.TEXT, true)
		.addParam("linked", InputParamType.LINKED, false)
	.	addBlanks(5);
		tt.testOutput();
		
		// input
		tt = new TestTable("inputTable"+(i-1), propertyBase+"input.", "newInput"+(i-1), true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost").addParam("qtyIntern").addParam("qtyExtern", InputParamType.TEXT, true)
		.addParam("transport").addParam("total", InputParamType.TEXT, true).addParam("totalCash", InputParamType.TEXT, true)
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
		
		// labour
		tt = new TestTable("labourTable"+(i-1), propertyBase+"labour.", "newLabour"+(i-1), true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType", InputParamType.SELECT, false).addParam("unitNum").addParam("unitCost").addParam("qtyIntern").addParam("qtyExtern", InputParamType.TEXT, true)
		.addParam("total", InputParamType.TEXT, true).addParam("totalCash", InputParamType.TEXT, true)
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
    }
    
    protected void verifyProject(String properties, int indexResult) {
		getTestContext().setResourceBundleName("messages/messages");
		String resultsTitle = getMessage("ruralInvest")+" :: "+getMessage("search.searchResults");
		String[] titles = projectStepTitles(true);

		getTestContext().setResourceBundleName(properties);
		
		// step 1
		assertTitleEquals(titles[0]);
		assertTextFieldEquals("projectName", getMessage("step1.projectName"));
		assertTextFieldEquals("userCode", getMessage("step1.userCode"));
		assertTextFieldEquals("inflationAnnual", getMessage("step1.inflationAnnual"));
		assertTextFieldEquals("exchRate", getMessage("step1.exchRate"));
		assertTextFieldEquals("duration", getMessage("step1.duration"));
		assertTextFieldEquals("location1", getMessage("step1.location1"));
		assertTextFieldEquals("location2", getMessage("step1.location2"));
		assertTextFieldEquals("location3", getMessage("step1.location3"));
		//assertRadioOptionSelected("withWithout", getMessage("step1.withWithout"));
		rivSubmitForm();
		assertTitleEquals(titles[1]);
		
		// step 2
		assertTextFieldEquals("benefName",getMessage("step2.benefName"));
		assertTextFieldEquals("beneDirectMen",getMessage("step2.beneDirectMen"));
		assertTextFieldEquals("beneDirectWomen", getMessage("step2.beneDirectWomen"));
		assertTextFieldEquals("beneDirectChild", getMessage("step2.beneDirectChild"));
		// TODO: test autocalc on benefDirectTotal
		assertTextFieldEquals("beneDirectNum",getMessage("step2.beneDirectNum"));
		assertTextFieldEquals("beneIndirectMen",getMessage("step2.beneIndirectMen"));
		assertTextFieldEquals("beneIndirectWomen",getMessage("step2.beneIndirectWomen"));
		assertTextFieldEquals("beneIndirectChild",getMessage("step2.beneIndirectChild"));
		// TODO: test autocalc on benefIndirectTotal
		assertTextFieldEquals("beneIndirectNum",getMessage("step2.beneIndirectNum"));
		assertTextFieldEquals("benefDesc",getMessage("step2.benefDesc"));
		rivSubmitForm();
		assertTitleEquals(titles[2]);
		
		// step 3
		assertTextFieldEquals("justification",getMessage("step3.justification"));
		assertTextFieldEquals("projDesc",getMessage("step3.projDesc"));
		assertTextFieldEquals("activities",getMessage("step3.activities"));
		rivSubmitForm();
		assertTitleEquals(titles[3]);
		
		// STEP 4
		assertTextFieldEquals("technology",getMessage("step4.technology"));
		assertTextFieldEquals("requirements",getMessage("step4.requirements"));
		rivSubmitForm();
		assertTitleEquals(titles[4]);
		
		// STEP 5
		assertTextFieldEquals("enviroImpact",getMessage("step5.enviroImpact"));
		assertTextFieldEquals("market",getMessage("step5.market"));
		rivSubmitForm();
		assertTitleEquals(titles[5]);

		// STEP 6
		assertTextFieldEquals("organization",getMessage("step6.organization"));
		assertTextFieldEquals("assumptions",getMessage("step6.assumptions"));
		rivSubmitForm();
		assertTitleEquals(titles[6]);
		
		// STEP 7
		// assets
		verifyProjectTablesStep7(getMessage("step1.withWithout").equals("true"));
		rivSubmitForm();
		assertTitleEquals(titles[7]);
		
		// STEP 8
		verifyProjectTablesStep8(getMessage("step1.withWithout").equals("true"));
		rivSubmitForm();
		assertTitleEquals(titles[8]);
		
		// STEP 9
		int withBlocks=verifyBlocks(true,0);
		clickLink("ui-id-2");
		verifyBlocks(false, withBlocks);
		
		rivSubmitForm();
		assertTitleEquals(titles[9]);
		
		// STEP 10
		// reference income table
		verifyProjectTablesStep9();
		rivSubmitForm();
		assertTitleEquals(titles[10]);
	
		
		// STEP 11
		// calculated fields
		assertTextFieldEquals("loan1Amt", getMessage("step11.loan1amt"));
		assertTextFieldEquals("wcAmountRequired", getMessage("step11.amtRequired"));
		assertTextFieldEquals("wcAmountFinanced", getMessage("step11.amtFinanced"));
		assertTextFieldEquals("wcFinancePeriod", getMessage("step11.period"));
		
		// input fields
		assertTextFieldEquals("loan1Interest", getMessage("step11.loan1Interest"));
		assertTextFieldEquals("loan1Duration", getMessage("step11.loan1Duration"));
		assertTextFieldEquals("loan1GraceCapital", getMessage("step11.loan1GraceCapital"));
		assertTextFieldEquals("loan1GraceInterest", getMessage("step11.loan1GraceInterest"));
		assertTextFieldEquals("loan2Amt", getMessage("step11.loan2Amt"));
		assertTextFieldEquals("loan2Interest", getMessage("step11.loan2Interest"));
		assertTextFieldEquals("loan2Duration", getMessage("step11.loan2Duration"));
		assertTextFieldEquals("loan2GraceCapital", getMessage("step11.loan2GraceCapital"));
		assertTextFieldEquals("loan2GraceInterest", getMessage("step11.loan2GraceInterest"));
		assertSelectedOptionEquals("loan2InitPeriod", getMessage("step11.loan2InitPeriod"));
		assertTextFieldEquals("capitalInterest", getMessage("step11.capitalInterest"));
		assertTextFieldEquals("capitalDonate", getMessage("step11.capitalDonate"));
		assertTextFieldEquals("capitalOwn", getMessage("step11.capitalOwn"));
		
		rivSubmitForm();
		assertTitleEquals(titles[11]);
		
		// STEP 12
		assertRadioOptionSelected("reccCode", getMessage("step12.reccCode"));
		assertTextFieldEquals("reccDate", getMessage("step12.reccDate"));
		assertTextFieldEquals("reccDesc", getMessage("step12.reccDesc"));
		
		clickLink("showIndicators");
		verifyProjectIndicators(true);
		rivSubmitForm();
		assertTitleEquals(titles[12]);
		
		// STEP 13
		verifyProjectIndicators(true);
		rivSubmitForm();
		assertTitleEquals(resultsTitle);
		
		// check indicators on search results screen
		Table result = new Table();
		Row testRow = new Row();
		testRow.appendCell(getMessage("step1.projectName"));
		testRow.appendCell(getMessage("step1.userCode"));
		testRow.appendCell(getMessage("step1.technician"));
		testRow.appendCell(getMessage("step1.office"));
		testRow.appendCell(getMessage("step1.status"));
		testRow.appendCell(getMessage("step1.projCat"));
		testRow.appendCell(getMessage("step1.benefType"));
		testRow.appendCell(getMessage("step1.enviroCat"));
		testRow.appendCell(getMessage("result.investTotal"));
		testRow.appendCell(getMessage("result.investOwn"));
		testRow.appendCell(getMessage("result.investDonate"));
		testRow.appendCell(getMessage("result.investFinance"));
		testRow.appendCell(getMessage("result.employ"));
		testRow.appendCell(getMessage("result.income"));
		testRow.appendCell(getMessage("result.wcTotal"));
		testRow.appendCell(getMessage("result.wcOwn"));
		testRow.appendCell(getMessage("result.wcDonate"));
		testRow.appendCell(getMessage("result.wcFinance"));
		testRow.appendCell(getMessage("result.costTotal"));
		testRow.appendCell(getMessage("result.costOwn"));
		testRow.appendCell(getMessage("result.costDonate"));
		testRow.appendCell(getMessage("result.costFinance"));
		testRow.appendCell(getMessage("result.npvAll"));
		testRow.appendCell(getMessage("result.irrAll"));
		testRow.appendCell(getMessage("result.npvApplicant"));
		testRow.appendCell(getMessage("result.irrApplicant"));
		testRow.appendCell(getMessage("result.investDirect"));
		testRow.appendCell(getMessage("result.investIndirect"));
		testRow.appendCell(getMessage("result.benefDirect"));
		testRow.appendCell(getMessage("result.benefIndirect"));
		testRow.appendCell("");
		testRow.appendCell("");
		testRow.appendCell("");
		result.appendRow(testRow);
		assertTableRowsEqual("results", 5+indexResult, result);
	}
    
    protected int verifyBlocks(boolean with, int withBlocks) {
    	int num = with ? Integer.parseInt(getMessage("step9.block.count")) : Integer.parseInt(getMessage("step9.blockWo.count"));
    	int duration = Integer.parseInt(getMessage("step1.duration"));
		
    	for (int i=1; i<=num; i++) {
	    	String base = with ? "step9.block."+i+"." : "step9.blockWo."+(i)+".";
	    	assertTextPresent(getMessage(base+"description"));
			assertTextInElement(i-1+withBlocks+"unitType", getMessage(base+"unitType"));
			assertTextInElement(i-1+withBlocks+"cycleLength", getMessage(base+"cycleLength"));
			assertTextInElement(i-1+withBlocks+"lengthUnit", getMessage(base+"lengthUnit"));
			assertTextInElement(i-1+withBlocks+"cyclePerYear", getMessage(base+"cyclePerYear"));
			assertTextInElement(i-1+withBlocks+"cycleFirstYear", getMessage(base+"cycleFirstYear"));
			assertTextInElement(i-1+withBlocks+"cycleFirstYearIncome", getMessage(base+"cycleFirstYearIncome"));
			
			for (int h=0; h<3; h++) {
				for (int j=0; j<12; j++) {
					for (int k=0; k<2; k++) {
						boolean shouldBeSelected = getMessage(base+"ch"+h+"-"+j+"-"+k).equals("true");
						boolean isSelected =  getElementById(i-1+withBlocks+"-"+h+"-"+j+"-"+k).getAttribute("style")!=null;
						assertTrue(shouldBeSelected==isSelected);
					}
				}
			}
			
			// production pattern 
			for (int x=1; x<=duration; x++) {
				assertTextInElement(i-1+withBlocks+"prod"+x,getMessage(base+"pat"+x));
			}
			
			verifyBlockTables(i+withBlocks, base);
			
		}
		return num;
    }
    
    protected void verifyProfileTablesStep4() {
    	// goods table
		TestTable tt = new TestTable("goodsListTable", "step4.good.", "newGood", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}});
		tt.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost");
		tt.addParam("unitTotal", InputParamType.TEXT, true).addParam("ownResource").addParam("donated", InputParamType.TEXT, true);
		tt.addParam("econLife").addParam("salvage").addParam("reserve")
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
		
		tt = new TestTable("labourListTable", "step4.labour.", "newLabour", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
		.addParam("unitTotal", InputParamType.TEXT, true).addParam("ownResource").addParam("donated", InputParamType.TEXT, true)
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
    }
    
    protected void verifyProfileTablesStep5() {
    	TestTable tt = new TestTable("generalTable", "step5.general.", "newGeneral", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true).addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
		
		tt = new TestTable("generalTableWo", "step5.generalWo.", "newGeneralWo", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true).addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
    }
    
    public void verifyProfileTablesStep6(int i) {
    	// income
		TestTable tt = new TestTable("incomeTable"+(i-1), "step6.product."+i+".income.", "newIncome"+(i-1), true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost").addParam("transport")
		.addParam("total", InputParamType.TEXT, true).addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
		
		// input
		tt = new TestTable("inputTable"+(i-1), "step6.product."+i+".input.", "newInput"+(i-1), true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost").addParam("transport")
		.addParam("total", InputParamType.TEXT, true).addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
		
		// input
		tt = new TestTable("labourTable"+(i-1), "step6.product."+i+".labour.", "newLabour"+(i-1), true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true).addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
    }
    protected void verifyProfile(String properties, int indexResult) {
    	getTestContext().setResourceBundleName("messages/messages");
    	
    	String resultsTitle = getMessage("ruralInvest")+" :: "+getMessage("search.searchResults");
		String[] titles = profileStepTitles(true);
		
    	getTestContext().setResourceBundleName(properties);
    	assertTextFieldEquals("profileName", getMessage("step1.profileName"));
		assertTextFieldEquals("exchRate", getMessage("step1.exchRate"));
		assertTextFieldEquals("benefNum", getMessage("step1.benefNum"));
		assertTextFieldEquals("benefFamilies", getMessage("step1.benefFamilies"));
		assertTextFieldEquals("location1", getMessage("step1.location1"));
		assertTextFieldEquals("location2", getMessage("step1.location2"));
		assertTextFieldEquals("location3", getMessage("step1.location3"));
		assertRadioOptionSelected("withWithout", getMessage("step1.withWithout"));
		
		rivSubmitForm();
		assertTitleEquals(titles[1]);
		assertTextFieldEquals("benefName",getMessage("step2.benefName"));
		assertTextFieldEquals("benefDesc",getMessage("step2.benefDesc"));
		assertTextFieldEquals("projDesc",getMessage("step2.projDesc"));
		
		rivSubmitForm();
		assertTitleEquals(titles[2]);
		assertTextFieldEquals("market",getMessage("step3.market"));
		assertTextFieldEquals("enviroImpact",getMessage("step3.enviroImpact"));
		
		// STEP 4
		rivSubmitForm();
		assertTitleEquals(titles[3]);
		
		verifyProfileTablesStep4();
		
		// STEP 5
		rivSubmitForm();
		assertTitleEquals(titles[4]);
		verifyProfileTablesStep5();
		
		// STEP 6
		rivSubmitForm();
		assertTitleEquals(titles[5]);
		
		for (int i=1; i<=Integer.parseInt(getMessage("step6.product.count")); i++) {
			boolean without = Boolean.parseBoolean(getMessage("step6.product."+i+".withoutProject"));
			if (without) {
				clickLink("ui-id-2");
			}
			
			assertTextInElement(i-1+"description", getMessage("step6.product."+i+".description"));
			assertTextInElement(i-1+"unitType", getMessage("step6.product."+i+".unitType"));
			assertTextInElement(i-1+"unitNum", getMessage("step6.product."+i+".unitNum"));
			assertTextInElement(i-1+"cycleLength", getMessage("step6.product."+i+".cycleLength"));
			assertTextInElement(i-1+"cyclePerYear", getMessage("step6.product."+i+".cyclePerYear"));
			
			verifyProfileTablesStep6(i);
		}
		

		// STEP 7
		rivSubmitForm();
		assertTitleEquals(titles[6]);
		
		// reference income table
		TestTable tt = new TestTable("IncomeTable", "step7.income.", "addIncome", false, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitCost").addParam("transport")
		.addBlanks(4);
		tt.testOutput();
		
		// reference cost table
		tt = new TestTable("GoodsTable", "step7.input.", "addInput", false, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitCost").addParam("transport")
		.addBlanks(4);
		tt.testOutput();
		
		// reference labour table
		tt = new TestTable("LabourTable", "step7.labour.", "addLabour", false, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitCost")
		.addBlanks(4);
		tt.testOutput();
		
		// STEP 8
		rivSubmitForm();
		assertTitleEquals(titles[7]);
		assertRadioOptionSelected("reccCode", getMessage("profile.step8.reccCode"));
		assertTextFieldEquals("reccDate", getMessage("profile.step8.reccDate"));
		assertTextFieldEquals("reccDesc", getMessage("profile.step8.reccDesc"));
		
		// STEP 9
		rivSubmitForm();
		assertTitleEquals(titles[8]);
		verifyProfileIndicators(true);
		
		// check indicators
		rivSubmitForm();
		assertTitleEquals(resultsTitle);
		
		Table result = new Table();
		Row testRow = new Row();
		testRow.appendCell(getMessage("step1.profileName"));
		testRow.appendCell(getMessage("step1.technician"));
		testRow.appendCell(getMessage("step1.status"));
		testRow.appendCell(getMessage("step1.office"));
		testRow.appendCell(getMessage("result.benefs"));
		testRow.appendCell(getMessage("result.investTotal"));
		testRow.appendCell(getMessage("result.investOwn"));
		testRow.appendCell(getMessage("result.investDonate"));
		testRow.appendCell(getMessage("result.income"));
		testRow.appendCell(getMessage("result.yearsRecover"));
		testRow.appendCell("");
		testRow.appendCell("");
		testRow.appendCell("");
		result.appendRow(testRow);
		assertTableRowsEqual("results", 4+indexResult, result);
    }
    
    private void verifyProfileIndicators(boolean ig) {
    	assertTextInElement("result_profileName", getMessage("step1.profileName"));
		assertTextInElement("result_technician", getMessage("step1.technician"));
		assertTextInElement("result_status", getMessage("step1.status"));
		assertTextInElement("result_office", getMessage("step1.office"));
		assertTextInElement("result_investTotal", getMessage("result.investTotal"));
		assertTextInElement("result_investOwn", getMessage("result.investOwn"));
		assertTextInElement("result_investDonated", getMessage("result.investDonate"));
		if (ig) {
			assertTextInElement("result_incomeAfterAnnual", getMessage("result.income"));
			assertTextInElement("result_yearsToRecover", getMessage("result.yearsRecover"));
		} else {
			assertTextInElement("result_investPerBenef", getMessage("result.incPerBenef"));
			assertTextInElement("result_costPerBenef", getMessage("result.costPerBenef"));
		}
    }
    
    protected void verifyProfileNigTablesStep4() {
    	// goods & services
		TestTable tt = new TestTable("goodsListTable", "step4.good.", "newGood", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}});
		tt.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost");
		tt.addParam("unitTotal", InputParamType.TEXT, true).addParam("ownResource").addParam("donated", InputParamType.TEXT, true);
		tt.addParam("econLife").addParam("salvage").addParam("reserve").addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
		
		// labour
		tt = new TestTable("labourListTable", "step4.labour.", "newLabour", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
		.addParam("unitTotal", InputParamType.TEXT, true).addParam("ownResource").addParam("donated", InputParamType.TEXT, true)
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
    }
    
    protected void verifyProfileNigTablesStep5() {
    	TestTable tt = new TestTable("generalTable", "step5.general.", "newGeneral", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true).addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
    }
    
    protected void verifyProfileNigTablesStep6(int i) {
    	// income
		TestTable tt = new TestTable("incomeTable"+(i-1), "step6.product."+i+".income.", "newIncome"+(i-1), true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true).addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
		
		// input
		tt = new TestTable("inputTable"+(i-1), "step6.product."+i+".input.", "newInput"+(i-1), true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost").addParam("transport")
		.addParam("total", InputParamType.TEXT, true).addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
		
		// input
		tt = new TestTable("labourTable"+(i-1), "step6.product."+i+".labour.", "newLabour"+(i-1), true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true).addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
    }
    
    protected void verifyProfileNigTablesStep7() {
    	TestTable tt = new TestTable("IncomeTable", "step7.income.", "addIncome", false, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitCost")//.addParam("transport")
		.addBlanks(4);
		tt.testOutput();
		
		// reference cost table
		tt = new TestTable("GoodsTable", "step7.input.", "addInput", false, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitCost").addParam("transport")
		.addBlanks(4);
		tt.testOutput();
		
		// reference labour table
		tt = new TestTable("LabourTable", "step7.labour.", "addLabour", false, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitCost")
		.addBlanks(4);
		tt.testOutput();
    }
    
    protected void verifyProfileNig(String properties, int indexResult) {

		getTestContext().setResourceBundleName("messages/messages");
		
    	String resultsTitle = getMessage("ruralInvest")+" :: "+getMessage("search.searchResults");
		String[] titles = profileStepTitles(false);

		getTestContext().setResourceBundleName(properties);
    	// step 1
		assertTextFieldEquals("profileName", getMessage("step1.profileName"));
		assertTextFieldEquals("exchRate", getMessage("step1.exchRate"));
		assertTextFieldEquals("benefNum", getMessage("step1.benefNum"));
		assertTextFieldEquals("benefFamilies", getMessage("step1.benefFamilies"));
		assertTextFieldEquals("location1", getMessage("step1.location1"));
		assertTextFieldEquals("location2", getMessage("step1.location2"));
		assertTextFieldEquals("location3", getMessage("step1.location3"));
		rivSubmitForm();
		assertTitleEquals(titles[1]);
		
		// step 2
		assertTextFieldEquals("benefName",getMessage("step2.benefName"));
		assertTextFieldEquals("benefDesc",getMessage("step2.benefDesc"));
		assertTextFieldEquals("projDesc",getMessage("step2.projDesc"));
		assertTextFieldEquals("sourceFunds", getMessage("step2.sourceFunds"));
		rivSubmitForm();
		assertTitleEquals(titles[2]);
		
		// STEP 3
		assertTextFieldEquals("market",getMessage("step3.market"));
		assertTextFieldEquals("enviroImpact",getMessage("step3.enviroImpact"));
		rivSubmitForm();
		assertTitleEquals(titles[3]);

		// step4
		verifyProfileNigTablesStep4();
		
		rivSubmitForm();
		assertTitleEquals(titles[4]);
		
		// STEP 5
		verifyProfileNigTablesStep5();
		rivSubmitForm();
		assertTitleEquals(titles[5]);
		
		// STEP 6
		for (int i=1; i<=2; i++) {
			assertTextInElement(i-1+"description", getMessage("step6.product."+i+".description"));
			assertTextInElement(i-1+"unitType", getMessage("step6.product."+i+".unitType"));
			assertTextInElement(i-1+"unitNum", getMessage("step6.product."+i+".unitNum"));
			assertTextInElement(i-1+"cycleLength", getMessage("step6.product."+i+".cycleLength"));
			assertTextInElement(i-1+"cyclePerYear", getMessage("step6.product."+i+".cyclePerYear"));
			
			verifyProfileNigTablesStep6(i);
		}
		rivSubmitForm();
		assertTitleEquals(titles[6]);
		
		// STEP 7
		// reference income table
		verifyProfileNigTablesStep7();
		
		// STEP 8
		rivSubmitForm();
		assertTitleEquals(titles[7]);
		assertRadioOptionSelected("reccCode", getMessage("step8.reccCode"));
		assertTextFieldEquals("reccDate", getMessage("step8.reccDate"));
		assertTextFieldEquals("reccDesc", getMessage("step8.reccDesc"));
		
		// STEP 9
		rivSubmitForm();
		assertTitleEquals(titles[8]);
		verifyProfileIndicators(false);
		
		rivSubmitForm();
		assertTitleEquals(resultsTitle);
		
		// check indicators
		Table result = new Table();
		Row testRow = new Row();
		testRow.appendCell(getMessage("step1.profileName"));
		testRow.appendCell(getMessage("step1.technician"));
		testRow.appendCell(getMessage("step1.status"));
		testRow.appendCell(getMessage("step1.office"));
		testRow.appendCell(getMessage("result.benefs"));
		testRow.appendCell(getMessage("result.investTotal"));
		testRow.appendCell(getMessage("result.investOwn"));
		testRow.appendCell(getMessage("result.investDonate"));
		testRow.appendCell(getMessage("result.incPerBenef"));
		testRow.appendCell(getMessage("result.costPerBenef"));
		testRow.appendCell("");
		testRow.appendCell("");
		testRow.appendCell("");
		result.appendRow(testRow);
		assertTableRowsEqual("results", 4+indexResult, result);
    }
    
	protected String extractPdfText(File f) throws IOException {
		   PDDocument pdfDocument = PDDocument.load(f);
		   try {
		      return new PDFTextStripper().getText(pdfDocument);
		   } finally {
		      pdfDocument.close();
		   }
		}
	

	// Source: http://notepad2.blogspot.co.il/2012/07/java-detect-if-stream-or-file-is-zip.html
	 public byte[] MAGIC = { 'P', 'K', 0x3, 0x4 };
	/**
	  * Test if a file is a zip file.
	  *
	  * @param f
	  *            the file to test.
	  * @return
	  */
	 public boolean isZipFile(File f) {
	 
	  boolean isZip = true;
	  byte[] buffer = new byte[MAGIC.length];
	  try {
	   RandomAccessFile raf = new RandomAccessFile(f, "r");
	   raf.readFully(buffer);
	   for (int i = 0; i < MAGIC.length; i++) {
	    if (buffer[i] != MAGIC[i]) {
	     isZip = false;
	     break;
	    }
	   }
	   raf.close();
	  } catch (Throwable e) {
	   isZip = false;
	  }
	  return isZip;
	 }
	}
