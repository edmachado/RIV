package org.fao.riv.tests.excelimport;


import static net.sourceforge.jwebunit.junit.JWebUnit.assertTitleEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLink;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickRadioOption;
import static net.sourceforge.jwebunit.junit.JWebUnit.closeBrowser;
import static net.sourceforge.jwebunit.junit.JWebUnit.getMessage;
import static net.sourceforge.jwebunit.junit.JWebUnit.getTestContext;
import static net.sourceforge.jwebunit.junit.JWebUnit.getTestingEngine;
import static net.sourceforge.jwebunit.junit.JWebUnit.gotoPage;
import static net.sourceforge.jwebunit.junit.JWebUnit.setTextField;

import org.fao.riv.tests.utils.ImportFile;
import org.fao.riv.tests.utils.WebTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class ProjectExcelImport extends WebTestUtil {
	String igTitle;
	
	 @BeforeClass 
	 public static void setUpClass() {      
	       System.out.println("     test Project Excel import");
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

	@Ignore
	@Test
	public void IgExcelImport() throws Exception {
		deletePros(true, true);
		
		String[] titles = projectStepTitles(true);
		getTestContext().setResourceBundleName("dataentry/projectIg");

		clickLink("goHome");
		
		// import file
		clickLink("importProjectIg");
		importFile(ImportFile.ProjectV20.getFile());
		
		// go through all steps
		for (int i=0; i<6; i++) {
			assertTitleEquals(titles[i]);
			if (i==0) {
				clickRadioOption("withWithout", getMessage("step1.withWithout"));
			}
			rivSubmitForm();
		}
		
		// STEP 7
		// import file
		clickLink("importExcel");
		setTextField("qqfile", ImportFile.ProjectXlsInvest.getFile().getAbsolutePath());
		gotoPage(getTestingEngine().getPageURL().toString());
		
		// check if import was successful
		verifyProjectTablesStep7();
		
		rivSubmitForm();
		assertTitleEquals(titles[7]);
		
		// STEP 8
		clickLink("importExcel");
		setTextField("qqfile", ImportFile.ProjectXlsGeneral.getFile().getAbsolutePath());
		gotoPage(getTestingEngine().getPageURL().toString());
		verifyProjectTablesStep8();
		
		rivSubmitForm();
		assertTitleEquals(titles[8]);
		
		// STEP 9
		clickLink("upload0");
		setTextField("qqfile", ImportFile.ProjectXlsBlock.getFile().getAbsolutePath());
		gotoPage(getTestingEngine().getPageURL().toString());
		verifyBlockTables(1);
		
		rivSubmitForm();
		assertTitleEquals(titles[9]);
		
    	getTestContext().setResourceBundleName("messages/messages");
	}
	
	@Ignore
	@Test
	public void NigExcelImport() throws Exception {
		deletePros(true, false);
		
		String[] titles = projectStepTitles(false);
		getTestContext().setResourceBundleName("dataentry/projectNig");

		clickLink("goHome");
		
		// import file
		clickLink("importProjectNig");
		importFile(ImportFile.ProjectNig16.getFile());
		
		// go through all steps
		for (int i=0; i<6; i++) {
			assertTitleEquals(titles[i]);
			rivSubmitForm();
		}
		
		// STEP 7
		// import file
		clickLink("importExcel");
		setTextField("qqfile", ImportFile.ProjectXlsInvestNig.getFile().getAbsolutePath());
		gotoPage(getTestingEngine().getPageURL().toString());
		verifyProjectNigTablesStep7();
		rivSubmitForm();
		assertTitleEquals(titles[7]);
		
		// STEP 8
		clickLink("importExcel");
		setTextField("qqfile", ImportFile.ProjectXlsGeneralNig.getFile().getAbsolutePath());
		gotoPage(getTestingEngine().getPageURL().toString());
		verifyProjectNigTablesStep8();
		rivSubmitForm();
		assertTitleEquals(titles[8]);
		
		// STEP 9
		clickLink("upload0");
		setTextField("qqfile", ImportFile.ProjectXlsBlockNig.getFile().getAbsolutePath());
		gotoPage(getTestingEngine().getPageURL().toString());
		verifyProjectNigTablesStep9(1);
		rivSubmitForm();
		assertTitleEquals(titles[9]);
		
		// STEP 10
		clickLink("importExcel");
		setTextField("qqfile", ImportFile.ProjectXlsContributions.getFile().getAbsolutePath());
		gotoPage(getTestingEngine().getPageURL().toString());
		verifyProjectNigTablesStep10();
		rivSubmitForm();
		assertTitleEquals(titles[10]);
		
    	getTestContext().setResourceBundleName("messages/messages");
	}
}