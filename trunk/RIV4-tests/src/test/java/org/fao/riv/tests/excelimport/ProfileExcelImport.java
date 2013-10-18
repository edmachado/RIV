package org.fao.riv.tests.excelimport;

import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextNotPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTitleEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLink;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickRadioOption;
import static net.sourceforge.jwebunit.junit.JWebUnit.closeBrowser;
import static net.sourceforge.jwebunit.junit.JWebUnit.getMessage;
import static net.sourceforge.jwebunit.junit.JWebUnit.getTestContext;
import static net.sourceforge.jwebunit.junit.JWebUnit.getTestingEngine;
import static net.sourceforge.jwebunit.junit.JWebUnit.gotoPage;
import static net.sourceforge.jwebunit.junit.JWebUnit.setTextField;

import java.util.concurrent.Callable;

import org.fao.riv.tests.utils.ImportFile;
import org.fao.riv.tests.utils.InputParam.InputParamType;
import org.fao.riv.tests.utils.TestTable;
import org.fao.riv.tests.utils.WebTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class ProfileExcelImport extends WebTestUtil {
	String igTitle;
	
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
	
	@Ignore
	@Test
	public void IgExcelImportDataError() throws Exception {
		deletePros(false, true);
		
		String[] titles = new String[9];
		for (int i=0;i<9;i++) {
			titles[i]= getMessage("ruralInvest")+" :: "+getMessage("profile.step"+(i+1));
		}

		clickLink("goHome");
		
		// import file
		clickLink("importProfileIg");
		importFile(ImportFile.ProfileIgV20.getFile());
		
		// go through all steps
		for (int i=0; i<3; i++) {
			assertTitleEquals(titles[i]);
			if (i==0) {
				setTextField("benefFamilies", "12");
			}
			rivSubmitForm();
		}
		
		// STEP 4
		// import file
		clickLink("importExcel");
		assertTextNotPresent(getMessage("import.excel.error.datatype"));
		setTextField("qqfile", ImportFile.ProfileXlsInvestErrorData.getFile().getAbsolutePath());
		assertTextPresent(getMessage("import.excel.error.datatype"));
	}

	@Ignore
	@Test
	public void IgExcelImport() throws Exception {
		deletePros(false, true);
		
		String[] titles = new String[9];
		for (int i=0;i<9;i++) {
			titles[i]= getMessage("ruralInvest")+" :: "+getMessage("profile.step"+(i+1));
		}
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
		
		// STEP 4
		// import file
		clickLink("importExcel");
		setTextField("qqfile", ImportFile.ProfileXlsInvest.getFile().getAbsolutePath());
		gotoPage(getTestingEngine().getPageURL().toString());
		verifyProfileTablesStep4();
		rivSubmitForm();
		assertTitleEquals(titles[4]);
		
		// STEP 5
		clickLink("importExcel");
		setTextField("qqfile", ImportFile.ProfileXlsGeneral.getFile().getAbsolutePath());
		gotoPage(getTestingEngine().getPageURL().toString());
		verifyProfileTablesStep5();
		rivSubmitForm();
		assertTitleEquals(titles[5]);
		
		// STEP 6
		clickLink("upload0");
		setTextField("qqfile", ImportFile.ProfileXlsProduct.getFile().getAbsolutePath());
		gotoPage(getTestingEngine().getPageURL().toString());
		verifyProfileTablesStep6(1);
		rivSubmitForm();
		assertTitleEquals(titles[6]);
		
		
    	getTestContext().setResourceBundleName("messages/messages");
		
	}
	
	@Ignore
	@Test
	public void NigExcelImport() throws Exception {
		deletePros(false, false);
		
		String[] titles = new String[9];
		for (int i=0;i<9;i++) {
			titles[i]= getMessage("ruralInvest")+" :: "+getMessage("profile.step"+(i+1));
		}
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
		
		// STEP 4
		// import file
		clickLink("importExcel");
		setTextField("qqfile", ImportFile.ProfileXlsInvestNig.getFile().getAbsolutePath());
		gotoPage(getTestingEngine().getPageURL().toString());
		verifyProfileNigTablesStep4();
		rivSubmitForm();
		
		// STEP 5
		clickLink("importExcel");
		setTextField("qqfile", ImportFile.ProfileXlsGeneralNig.getFile().getAbsolutePath());
		gotoPage(getTestingEngine().getPageURL().toString());
		verifyProfileNigTablesStep5();
		rivSubmitForm();
		
		// STEP 6
		clickLink("upload0");
		setTextField("qqfile", ImportFile.ProfileXlsProductNig.getFile().getAbsolutePath());
		gotoPage(getTestingEngine().getPageURL().toString());
		verifyProfileNigTablesStep6(1);
		rivSubmitForm();
		assertTitleEquals(titles[6]);
		
		
    	getTestContext().setResourceBundleName("messages/messages");
		
	}
}