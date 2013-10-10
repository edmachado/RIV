package org.fao.riv.tests.excelimport;


import static net.sourceforge.jwebunit.junit.JWebUnit.*;

import java.util.concurrent.Callable;

import net.sourceforge.jwebunit.htmlunit.HtmlUnitTestingEngineImpl;

import org.fao.riv.tests.utils.ImportFile;
import org.fao.riv.tests.utils.TestTable;
import org.fao.riv.tests.utils.WebTestUtil;
import org.fao.riv.tests.utils.InputParam.InputParamType;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;

public class ProfileExcelImport extends WebTestUtil {
	String igTitle;
	
	 @BeforeClass 
	 public static void setUpClass() {      
	       System.out.println("     test Profile Excel import");
	 }
	
	@Before
	public void deleteExisting() {
		login();
		deletePros(false, true, false);
		deletePros(false, true, true);
	}
	
	@After
    public void close() {
		clickLink("logoff");
		closeBrowser();
    }
	
	@Ignore
	@Test
	public void IgExcelImportDataError() throws Exception {
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
			}
			rivSubmitForm();
		}
		
		// STEP 4
		// import file
		clickLink("importExcel");
		setTextField("qqfile", ImportFile.ProfileXlsInvest.getFile().getAbsolutePath());
		
	}

	@Test
	public void IgExcelImport() throws Exception {
		
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

    	getTestContext().setResourceBundleName("messages/messages");
		
	}
}