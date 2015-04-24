package org.fao.riv.tests.excelimport;

import static net.sourceforge.jwebunit.junit.JWebUnit.assertElementPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertLinkPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTableNotPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextNotPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTitleEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLink;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickRadioOption;
import static net.sourceforge.jwebunit.junit.JWebUnit.closeBrowser;
import static net.sourceforge.jwebunit.junit.JWebUnit.getElementById;
import static net.sourceforge.jwebunit.junit.JWebUnit.getMessage;
import static net.sourceforge.jwebunit.junit.JWebUnit.getTestContext;
import static net.sourceforge.jwebunit.junit.JWebUnit.getTestingEngine;
import static net.sourceforge.jwebunit.junit.JWebUnit.gotoPage;
import static net.sourceforge.jwebunit.junit.JWebUnit.saveAs;
import static net.sourceforge.jwebunit.junit.JWebUnit.setTextField;

import java.io.File;

import net.sourceforge.jwebunit.htmlunit.HtmlUnitTestingEngineImpl;

import org.fao.riv.tests.WebTest;
import org.fao.riv.utils.ImportFile;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;

public class ProfileExcelImport extends WebTest {
	@Rule
    public TemporaryFolder folder = new TemporaryFolder();
	
	 @BeforeClass 
	 public static void setUpClass() {      
	       System.out.println("     test Profile Excel import");
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
	
//	@Ignore
//	@Test
	public void test() throws InterruptedException {
		gotoPage(getTestingEngine().getPageURL().toString()+"/test");
		assertLinkPresent("run");
		assertElementPresent("test");
		assertTextNotPresent("hello!");
		clickLink("run");
		Thread.sleep(2000);
		assertTextPresent("hello!");
	}
	
	@Ignore
	@Test
	public void IgExcelImportDataError() throws Exception {
		deletePros(false, true);
		
		String[] titles = profileStepTitles(true);

		clickLink("goHome");
		
		// import file
		clickLink("importProfileIg");
		importFile(ImportFile.ProfileIgV42.getFile());
		
		// go through all steps
		for (int i=0; i<3; i++) {
			assertTitleEquals(titles[i]);
			rivSubmitForm();
		}
		
		// STEP 4
		HtmlUnitTestingEngineImpl te = (HtmlUnitTestingEngineImpl)getTestingEngine();
		te.getWebClient().setAjaxController(new NicelyResynchronizingAjaxController());
		te.getWebClient().waitForBackgroundJavaScriptStartingBefore(10000);
		
		assertTitleEquals(titles[3]);
		assertLinkPresent("importExcel");
		clickLink("importExcel");
		assertElementPresent("upload-dialog");
		assertTextNotPresent(getMessage("import.excel.error.datatype"));
		setTextField("qqfile", ImportFile.ProfileXlsInvestErrorData.getFile().getAbsolutePath());
		te.getWebClient().waitForBackgroundJavaScript(10000);
//		Thread.sleep(10000);
		
		 boolean found = false;
	      for (int i = 0; i < 20; i++) {
	          if (getElementById("uploader-error-message").getTextContent().contains(getMessage("import.excel.error.datatype"))) {
	            found = true;
	            break;
	          }
	          synchronized (te.getCurrentWindow().getEnclosedPage()) {
	              try {
	            	  te.getCurrentWindow().getEnclosedPage().wait(1000);
	              } catch (InterruptedException e) {
	              }
	          }
	      }
	      org.junit.Assert.assertTrue("error message not found",found);
	}
	
	@Test
	public void IgExcelImportWithout() throws Exception {
		deletePros(false, true);
		
		String[] titles = profileStepTitles(true);
		getTestContext().setResourceBundleName("dataentry/profileIg");

		clickLink("goHome");
		
		// import file
		clickLink("importProfileIg");
		importFile(ImportFile.ProfileIgV42.getFile());
		
		// go through all steps
		for (int i=0; i<3; i++) {
			assertTitleEquals(titles[i]);
			rivSubmitForm();
		}
		
		String url;
		
		// STEP 4
		url=getTestingEngine().getPageURL().toString();
		clickLink("downloadTemplate");
		File fTemplate = folder.newFile("invest-template.xlsx");
		saveAs(fTemplate);
		gotoPage(url);
		assertTitleEquals(titles[3]);
		// re-upload template to zero tables
		clickLink("importExcel");
		setTextField("qqfile", fTemplate.getAbsolutePath());
		gotoPage(url);
		assertTitleEquals(titles[3]);
		assertTableNotPresent("goodsListTable");
		assertTableNotPresent("labourListTable");
		
		// import file
		clickLink("importExcel");
		setTextField("qqfile", ImportFile.ProfileXlsInvest.getFile().getAbsolutePath());
		gotoPage(url);
		verifyProfileTablesStep4();
		
		// export and import again
		clickLink("downloadExcel");
		File f = folder.newFile("step4.xlsx"); 
		saveAs(f);
		gotoPage(url);
		clickLink("importExcel");
		setTextField("qqfile", f.getAbsolutePath());
		gotoPage(url);
		verifyProfileTablesStep4();
		
		rivSubmitForm();
		assertTitleEquals(titles[4]);
		
		// STEP 5
		// import file
		url = getTestingEngine().getPageURL().toString();

		// download template
		clickLink("downloadTemplate");
		fTemplate = folder.newFile("general-template.xlsx");
		saveAs(fTemplate);
		gotoPage(url);
		assertTitleEquals(titles[4]);
		
		// re-upload template to zero tables
		clickLink("importExcel");
		setTextField("qqfile", fTemplate.getAbsolutePath());
		gotoPage(url);
		assertTitleEquals(titles[4]);
		assertTableNotPresent("generalTable");
		assertTableNotPresent("generalTableWo");
		
		clickLink("importExcel");
		setTextField("qqfile", ImportFile.ProfileXlsGeneral.getFile().getAbsolutePath());
		gotoPage(url);
		verifyProfileTablesStep5();
		
		// export and import again
		clickLink("downloadExcel");
		File f2 = folder.newFile("step5.xlsx"); 
		saveAs(f2);
		gotoPage(url);
		clickLink("importExcel");
		setTextField("qqfile", f2.getAbsolutePath());
		gotoPage(url);
		verifyProfileTablesStep5();
				
		rivSubmitForm();
		assertTitleEquals(titles[5]);
		
    	getTestContext().setResourceBundleName("messages/messages");
	}

	@Test
	public void IgExcelImport() throws Exception {
		deletePros(false, true);
		
		String[] titles = profileStepTitles(true);
		getTestContext().setResourceBundleName("dataentry/profileIg");

		clickLink("goHome");
		
		// import file
		clickLink("importProfileIg");
		importFile(ImportFile.ProfileIgV20.getFile());
		
		// go through all steps
		for (int i=0; i<3; i++) {
			assertTitleEquals(titles[i]);
			if (i==0) {
				setTextField("benefFamilies", "12");
				clickRadioOption("withWithout", getMessage("step1.withWithout"));
			}
			rivSubmitForm();
		}
		
		String url;
		
		// STEP 4
		
		url=getTestingEngine().getPageURL().toString();
		clickLink("downloadTemplate");
		File fTemplate = folder.newFile("invest-template.xlsx");
		saveAs(fTemplate);
		gotoPage(url);
		assertTitleEquals(titles[3]);
		// re-upload template to zero tables
		clickLink("importExcel");
		setTextField("qqfile", fTemplate.getAbsolutePath());
		gotoPage(url);
		assertTitleEquals(titles[3]);
		assertTableNotPresent("goodsListTable");
		assertTableNotPresent("labourListTable");
		
		// import file
		clickLink("importExcel");
		setTextField("qqfile", ImportFile.ProfileXlsInvest.getFile().getAbsolutePath());
		gotoPage(url);
		verifyProfileTablesStep4();
		
		// export and import again
		clickLink("downloadExcel");
		File f = folder.newFile("step4.xlsx"); 
		saveAs(f);
		gotoPage(url);
		clickLink("importExcel");
		setTextField("qqfile", f.getAbsolutePath());
		gotoPage(url);
		verifyProfileTablesStep4();
		
		rivSubmitForm();
		assertTitleEquals(titles[4]);
		
		// STEP 5
		// import file
		url = getTestingEngine().getPageURL().toString();

		// download template
		clickLink("downloadTemplate");
		fTemplate = folder.newFile("general-template.xlsx");
		saveAs(fTemplate);
		gotoPage(url);
		assertTitleEquals(titles[4]);
		
		// re-upload template to zero tables
		clickLink("importExcel");
		setTextField("qqfile", fTemplate.getAbsolutePath());
		gotoPage(url);
		assertTitleEquals(titles[4]);
		assertTableNotPresent("generalTable");
		assertTableNotPresent("generalTableWo");
		
		clickLink("importExcel");
		setTextField("qqfile", ImportFile.ProfileXlsGeneral.getFile().getAbsolutePath());
		gotoPage(url);
		verifyProfileTablesStep5();
		
		// export and import again
		clickLink("downloadExcel");
		File f2 = folder.newFile("step5.xlsx"); 
		saveAs(f2);
		gotoPage(url);
		clickLink("importExcel");
		setTextField("qqfile", f2.getAbsolutePath());
		gotoPage(url);
		verifyProfileTablesStep5();
				
		rivSubmitForm();
		assertTitleEquals(titles[5]);
		
		// STEP 6
		url = getTestingEngine().getPageURL().toString();

		// download template
		clickLink("downloadTemplate");
		fTemplate = folder.newFile("block-template.xlsx");
		saveAs(fTemplate);
		gotoPage(url);
		assertTitleEquals(titles[5]);
		
		// re-upload template to zero tables
		clickLink("upload0");
		setTextField("qqfile", fTemplate.getAbsolutePath());
		gotoPage(url);
		assertTitleEquals(titles[5]);
		assertTableNotPresent("incomeTable0");
		assertTableNotPresent("inputTable0");
		assertTableNotPresent("labourTable0");
		
		// import file
		url = getTestingEngine().getPageURL().toString();
		clickLink("upload0");
		setTextField("qqfile", ImportFile.ProfileXlsProduct.getFile().getAbsolutePath());
		gotoPage(url);
		verifyProfileTablesStep6(1);
		
		// export and import again
		clickLink("downloadExcel0");
		File f3 = folder.newFile("step6.xlsx"); 
		saveAs(f3);
		gotoPage(url);
		clickLink("upload0");
		setTextField("qqfile", f3.getAbsolutePath());
		gotoPage(url);
		verifyProfileTablesStep6(1);
				
		rivSubmitForm();
		assertTitleEquals(titles[6]);
		
    	getTestContext().setResourceBundleName("messages/messages");
	}
	
	@Test
	public void NigExcelImport() throws Exception {
		deletePros(false, false);
		
		String[] titles = profileStepTitles(false);
		getTestContext().setResourceBundleName("dataentry/profileNig");
		
		// import file
		clickLink("importProfileIg");
		importFile(ImportFile.ProfileNig16.getFile());
		// go through all steps
		for (int i=0; i<3; i++) {
			assertTitleEquals(titles[i]);
			if (i==0) {
				setTextField("benefFamilies", "12");
			}
			rivSubmitForm();
		}

		String url;
		
		// STEP 4
		url=getTestingEngine().getPageURL().toString();
		clickLink("downloadTemplate");
		File fTemplate = folder.newFile("invest-template.xlsx");
		saveAs(fTemplate);
		gotoPage(url);
		assertTitleEquals(titles[3]);
		// re-upload template to zero tables
		clickLink("importExcel");
		setTextField("qqfile", fTemplate.getAbsolutePath());
		gotoPage(url);
		assertTitleEquals(titles[3]);
		assertTableNotPresent("goodsListTable");
		assertTableNotPresent("labourListTable");
		
		// import file
		clickLink("importExcel");
		setTextField("qqfile", ImportFile.ProfileXlsInvestNig.getFile().getAbsolutePath());
		gotoPage(url);
		verifyProfileNigTablesStep4();
		
		// export and import again
		clickLink("downloadExcel");
		File f = folder.newFile("step4.xlsx"); 
		saveAs(f);
		gotoPage(url);
		clickLink("importExcel");
		setTextField("qqfile", f.getAbsolutePath());
		gotoPage(url);
		verifyProfileNigTablesStep4();
		
		rivSubmitForm();
		assertTitleEquals(titles[4]);
		
		// STEP 5
		url = getTestingEngine().getPageURL().toString();

		// download template
		clickLink("downloadTemplate");
		fTemplate = folder.newFile("general-template.xlsx");
		saveAs(fTemplate);
		gotoPage(url);
		assertTitleEquals(titles[4]);
		
		// re-upload template to zero tables
		clickLink("importExcel");
		setTextField("qqfile", fTemplate.getAbsolutePath());
		gotoPage(url);
		assertTitleEquals(titles[4]);
		assertTableNotPresent("generalTable");
		assertTableNotPresent("generalTableWo");
		
		clickLink("importExcel");
		setTextField("qqfile", ImportFile.ProfileXlsGeneralNig.getFile().getAbsolutePath());
		gotoPage(url);
		verifyProfileNigTablesStep5();
		
		// export and import again
		clickLink("downloadExcel");
		File f2 = folder.newFile("step5.xlsx"); 
		saveAs(f2);
		gotoPage(url);
		clickLink("importExcel");
		setTextField("qqfile", f2.getAbsolutePath());
		gotoPage(url);
		verifyProfileNigTablesStep5();
		
		rivSubmitForm();
		assertTitleEquals(titles[5]);
		
		// STEP 6
		url = getTestingEngine().getPageURL().toString();

		// download template
		clickLink("downloadTemplate");
		fTemplate = folder.newFile("block-template.xlsx");
		saveAs(fTemplate);
		gotoPage(url);
		assertTitleEquals(titles[5]);
		
		// re-upload template to zero tables
		clickLink("upload0");
		setTextField("qqfile", fTemplate.getAbsolutePath());
		gotoPage(url);
		assertTitleEquals(titles[5]);
		assertTableNotPresent("incomeTable0");
		assertTableNotPresent("inputTable0");
		assertTableNotPresent("labourTable0");
		
		clickLink("upload0");
		setTextField("qqfile", ImportFile.ProfileXlsProductNig.getFile().getAbsolutePath());
		gotoPage(url);
		verifyProfileNigTablesStep6(1);
		
		// export and import again
		clickLink("downloadExcel0");
		File f3 = folder.newFile("step6.xlsx"); 
		saveAs(f3);
		gotoPage(url);
		clickLink("upload0");
		setTextField("qqfile", f3.getAbsolutePath());
		gotoPage(url);
		verifyProfileNigTablesStep6(1);
		
		rivSubmitForm();
		assertTitleEquals(titles[6]);
		
		
    	getTestContext().setResourceBundleName("messages/messages");
	}
}