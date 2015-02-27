package org.fao.riv.tests.excelimport;


import static net.sourceforge.jwebunit.junit.JWebUnit.*;

import java.io.File;

import org.fao.riv.tests.utils.ImportFile;
import org.fao.riv.tests.utils.WebTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ProjectExcelImport extends WebTestUtil {
	String igTitle;
	
	@Rule
    public TemporaryFolder folder = new TemporaryFolder();
	
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

	@Test
	public void IgExcelImport() throws Exception {
		deletePros(true, true);
		
		String[] titles = projectStepTitles(true);
		getTestContext().setResourceBundleName("dataentry/projectIg");

		clickLink("goHome");
		
		// import file
		clickLink("importProjectIg");
		importFile(ImportFile.ProjectV20.getFile());
		
		String hasWithout = getMessage("step1.withWithout");
		// go through all steps
		for (int i=0; i<6; i++) {
			assertTitleEquals(titles[i]);
			if (i==0) {
				clickRadioOption("withWithout", hasWithout);
			}
			rivSubmitForm();
		}
		
		String url;
		
		// STEP 7
		url=getTestingEngine().getPageURL().toString();
	
		// download template
		clickLink("downloadTemplate");
		File fTemplate = folder.newFile("invest-template.xlsx");
		saveAs(fTemplate);
		gotoPage(url);
		assertTitleEquals(titles[6]);
		// re-upload template to zero tables
		clickLink("importExcel");
		setTextField("qqfile", fTemplate.getAbsolutePath());
		gotoPage(url);
		assertTitleEquals(titles[6]);
		assertTableNotPresent("assetsTable");
		assertTableNotPresent("LabourTable");
		assertTableNotPresent("ServicesTable");
	
		// import file
		clickLink("importExcel");
		setTextField("qqfile", ImportFile.ProjectXlsInvest.getFile().getAbsolutePath());
		gotoPage(url);
		verifyProjectTablesStep7(hasWithout.equals("true"));
		
		// export
		clickLink("downloadExcel");
		File f = folder.newFile("step7.xlsx"); 
		saveAs(f);
		gotoPage(url);
		
		// re-upload template to zero tables
		clickLink("importExcel");
		setTextField("qqfile", fTemplate.getAbsolutePath());
		gotoPage(url);
		assertTitleEquals(titles[6]);
		assertTableNotPresent("assetsTable");
		assertTableNotPresent("LabourTable");
		assertTableNotPresent("ServicesTable");
		
		// import
		clickLink("importExcel");
		setTextField("qqfile", f.getAbsolutePath());
		gotoPage(url);
		verifyProjectTablesStep7(hasWithout.equals("true"));
		
		rivSubmitForm();
		assertTitleEquals(titles[7]);
		
		// STEP 8
		url=getTestingEngine().getPageURL().toString();
		
		// download template
		clickLink("downloadTemplate");
		fTemplate = folder.newFile("general-template.xlsx");
		saveAs(fTemplate);
		gotoPage(url);
		assertTitleEquals(titles[7]);
		
		// re-upload template to zero tables
		clickLink("importExcel");
		setTextField("qqfile", fTemplate.getAbsolutePath());
		gotoPage(url);
		assertTitleEquals(titles[7]);
		assertTableNotPresent("assetsTable");
		assertTableNotPresent("LabourTable");
		assertTableNotPresent("ServicesTable");
		
		
		clickLink("importExcel");
		setTextField("qqfile", ImportFile.ProjectXlsGeneral.getFile().getAbsolutePath());
		gotoPage(url);
		verifyProjectTablesStep8(hasWithout.equals("true"));
		
		// export and import again
		clickLink("downloadExcel");
		File f2 = folder.newFile("step8.xlsx"); 
		saveAs(f2);
		gotoPage(url);
		clickLink("importExcel");
		setTextField("qqfile", f2.getAbsolutePath());
		gotoPage(url);
		verifyProjectTablesStep8(hasWithout.equals("true"));
		
		rivSubmitForm();
		assertTitleEquals(titles[8]);
		
		// STEP 9
		url=getTestingEngine().getPageURL().toString();
		clickLink("upload0");
		setTextField("qqfile", ImportFile.ProjectXlsBlock.getFile().getAbsolutePath());
		gotoPage(url);
		verifyBlockTables(1,"step9.block.1.");
		
		// export and import again
		clickLink("downloadExcel0");
		File f3 = folder.newFile("step9.xlsx"); 
		saveAs(f3);
		gotoPage(url);
		clickLink("upload0");
		setTextField("qqfile", f3.getAbsolutePath());
		gotoPage(url);
		verifyBlockTables(1,"step9.block.1.");
		
		rivSubmitForm();
		assertTitleEquals(titles[9]);
		
    	getTestContext().setResourceBundleName("messages/messages");
	}
	
	@Test
	public void IgExcelImportNoWithout() throws Exception {
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
				clickRadioOption("withWithout", "false");
			}
			rivSubmitForm();
		}
		
		String url;
		
		// STEP 7
		// import file
		url=getTestingEngine().getPageURL().toString();
		clickLink("importExcel");
		setTextField("qqfile", ImportFile.ProjectXlsInvestNoWithout.getFile().getAbsolutePath());
		gotoPage(url);
		verifyProjectTablesStep7(false);
		
		// export and import again
		clickLink("downloadExcel");
		File f = folder.newFile("step7.xlsx"); 
		saveAs(f);
		gotoPage(url);
		clickLink("importExcel");
		setTextField("qqfile", f.getAbsolutePath());
		gotoPage(url);
		verifyProjectTablesStep7(false);
		
		rivSubmitForm();
		assertTitleEquals(titles[7]);
		
		// STEP 8
		url=getTestingEngine().getPageURL().toString();
		clickLink("importExcel");
		setTextField("qqfile", ImportFile.ProjectXlsGeneralNoWithout.getFile().getAbsolutePath());
		gotoPage(url);
		verifyProjectTablesStep8(false);
		
		// export and import again
		clickLink("downloadExcel");
		File f2 = folder.newFile("step8.xlsx"); 
		saveAs(f2);
		gotoPage(url);
		clickLink("importExcel");
		setTextField("qqfile", f2.getAbsolutePath());
		gotoPage(url);
		verifyProjectTablesStep8(false);
		
		rivSubmitForm();
		assertTitleEquals(titles[8]);
		
		
    	getTestContext().setResourceBundleName("messages/messages");
	}
	
	@Test
	public void NigExcelImport() throws Exception {
		deletePros(true, false);
		
		String[] titles = projectStepTitles(false);
		getTestContext().setResourceBundleName("dataentry/projectNig");

		clickLink("goHome");
		
		// import file
		clickLink("importProjectNig");
		importFile(ImportFile.ProjectNig41.getFile());
		
		// go through all steps
		for (int i=0; i<6; i++) {
			assertTitleEquals(titles[i]);
			rivSubmitForm();
		}
		
		String url;
		
		// STEP 7
		// import file
		url = getTestingEngine().getPageURL().toString();
		// download template
		clickLink("downloadTemplate");
		File fTemplate = folder.newFile("investTemplate.xlsx");
		saveAs(fTemplate);
		gotoPage(url);
		assertTitleEquals(titles[6]);
		// re-upload template to zero tables
		clickLink("importExcel");
		setTextField("qqfile", fTemplate.getAbsolutePath());
		gotoPage(url);
		assertTitleEquals(titles[6]);
		assertTableNotPresent("assetsTable");
		assertTableNotPresent("LabourTable");
		assertTableNotPresent("ServicesTable");
				
		//import data
		clickLink("importExcel");
		setTextField("qqfile", ImportFile.ProjectXlsInvestNig.getFile().getAbsolutePath());
		gotoPage(url);
		verifyProjectNigTablesStep7();
		
		// export and import again
		clickLink("downloadExcel");
		File f = folder.newFile("step7.xlsx"); 
		saveAs(f);
		gotoPage(url);
		clickLink("importExcel");
		setTextField("qqfile", f.getAbsolutePath());
		gotoPage(url);
		verifyProjectNigTablesStep7();
		
		rivSubmitForm();
		assertTitleEquals(titles[7]);
		
		// STEP 8
		url=getTestingEngine().getPageURL().toString();
		// download template
		clickLink("downloadTemplate");
		fTemplate = folder.newFile("generalTemplate.xlsx");
		saveAs(fTemplate);
		gotoPage(url);
		assertTitleEquals(titles[7]);
		// re-upload template to zero tables
		clickLink("importExcel");
		setTextField("qqfile", fTemplate.getAbsolutePath());
		gotoPage(url);
		assertTitleEquals(titles[7]);
		assertTableNotPresent("inputTable");
		assertTableNotPresent("labourTable");
		assertTableNotPresent("generalTable");
		
		// upload data and verify
		clickLink("importExcel");
		setTextField("qqfile", ImportFile.ProjectXlsGeneralNig.getFile().getAbsolutePath());
		gotoPage(url);
		verifyProjectNigTablesStep8();
		
		// export and import again
		clickLink("downloadExcel");
		File f2 = folder.newFile("step8.xlsx"); 
		saveAs(f2);
		gotoPage(url);
		clickLink("importExcel");
		setTextField("qqfile", f2.getAbsolutePath());
		gotoPage(url);
		verifyProjectNigTablesStep8();
		
		rivSubmitForm();
		assertTitleEquals(titles[8]);
		
		// STEP 9
		url=getTestingEngine().getPageURL().toString();
		
		// download template
		clickLink("downloadTemplate");
		fTemplate = folder.newFile("blockTemplate.xlsx");
		saveAs(fTemplate);
		gotoPage(url);
		assertTitleEquals(titles[8]);
		// re-upload template to zero tables
		clickLink("upload0");
		setTextField("qqfile", fTemplate.getAbsolutePath());
		gotoPage(url);
		assertTitleEquals(titles[8]);
		assertTableNotPresent("incomeTable0");
		assertTableNotPresent("inputTable0");
		assertTableNotPresent("labourTable0");
		
		// upload data
		clickLink("upload0");
		setTextField("qqfile", ImportFile.ProjectXlsBlockNig.getFile().getAbsolutePath());
		gotoPage(url);
		verifyProjectNigTablesStep9(1);
		
		// export and import again
		clickLink("downloadExcel0");
		File f3 = folder.newFile("step9.xlsx"); 
		saveAs(f3);
		gotoPage(url);
		clickLink("upload0");
		setTextField("qqfile", f3.getAbsolutePath());
		gotoPage(url);
		verifyProjectNigTablesStep9(1);
		
		rivSubmitForm();
		assertTitleEquals(titles[9]);
		
		// STEP 10
		url=getTestingEngine().getPageURL().toString();
		
		// download template
		clickLink("downloadTemplate");
		fTemplate = folder.newFile("contributionsTemplate.xlsx");
		saveAs(fTemplate);
		gotoPage(url);
		assertTitleEquals(titles[9]);
		// re-upload template to zero tables
		clickLink("importExcel");
		setTextField("qqfile", fTemplate.getAbsolutePath());
		gotoPage(url);
		assertTitleEquals(titles[9]);
		for (int year=1;year<=10;year++) {
			assertTableNotPresent("contributionTable"+year);
		}
		
		// import data
		clickLink("importExcel");
		setTextField("qqfile", ImportFile.ProjectXlsContributions.getFile().getAbsolutePath());
		gotoPage(getTestingEngine().getPageURL().toString());
		verifyProjectNigTablesStep10(1);
		
		// test simplified export
		clickRadioOption("simpleApproach", "true");
		clickButtonWithText("Confirm");
		assertTablePresent("contributionTable1");
		assertTableNotPresent("contributionTable2");
		
		// download template
		clickLink("downloadTemplate");
		fTemplate = folder.newFile("contributionsTemplate-simplified.xlsx");
		saveAs(fTemplate);
		gotoPage(url);
		assertTitleEquals(titles[9]);
		// re-upload template to zero tables
		clickLink("importExcel");
		setTextField("qqfile", fTemplate.getAbsolutePath());
		gotoPage(url);
		assertTitleEquals(titles[9]);
		assertTableNotPresent("contributionTable1");
		
		// import data
		clickLink("importExcel");
		setTextField("qqfile", ImportFile.ProjectXlsContributionsSimplified.getFile().getAbsolutePath());
		gotoPage(getTestingEngine().getPageURL().toString());
		assertTitleEquals(titles[9]);
		assertTablePresent("contributionTable1");
		
		// back to per-year and verify
		clickRadioOption("simpleApproach", "true");
		verifyProjectNigTablesStep10(1);
		rivSubmitForm();
		assertTitleEquals(titles[10]);
		
    	getTestContext().setResourceBundleName("messages/messages");
	}
}