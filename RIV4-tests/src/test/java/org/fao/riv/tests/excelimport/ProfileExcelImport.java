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
		
		// This will make the ajax call synchronous - no more Thread.sleep() !
		// from http://nesbot.com/2011/10/16/play-framework-sample-app-JWebUnit-synchronous-ajax
//		if (getTestingEngine() instanceof HtmlUnitTestingEngineImpl) {
//			((HtmlUnitTestingEngineImpl)getTestingEngine()).getWebClient().setAjaxController(new NicelyResynchronizingAjaxController());
//		}
		
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
		//TODO: get jwebunit to recognize ajax callback
//		Thread.sleep(10000);
		assertTextPresent(getMessage("import.excel.error.datatype"));
	}

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
		
		// check if import was successful
		// goods table
		TestTable tt = new TestTable("goodsListTable", "step4.good.", "newGood", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}});
		tt.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost");
		tt.addParam("unitTotal", InputParamType.TEXT, true).addParam("ownResource").addParam("donated", InputParamType.TEXT, true);
		tt.addParam("econLife").addParam("salvage").addParam("reserve")
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
		
		tt = new TestTable("labourListTable", "step4.labour.", "newLabour", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}});
		tt.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost");
		tt.addParam("unitTotal", InputParamType.TEXT, true).addParam("ownResource").addParam("donated", InputParamType.TEXT, true)
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
		
		rivSubmitForm();
		
		// STEP 5
		setTextField("qqfile", ImportFile.ProfileXlsGeneral.getFile().getAbsolutePath());
		gotoPage(getTestingEngine().getPageURL().toString());
		tt = new TestTable("generalTable", "step5.general.", "newGeneral", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true).addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
		
		tt = new TestTable("generalTableWo", "step5.generalWo.", "newGeneralWo", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true).addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
		
		
    	getTestContext().setResourceBundleName("messages/messages");
		
	}
	
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
		
		// check if import was successful
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
		
		rivSubmitForm();
		
		// STEP 5
		setTextField("qqfile", ImportFile.ProfileXlsGeneralNig.getFile().getAbsolutePath());
		gotoPage(getTestingEngine().getPageURL().toString());
		tt = new TestTable("generalTable", "step5.general.", "newGeneral", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true).addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testOutput();
		
		
    	getTestContext().setResourceBundleName("messages/messages");
		
	}
}