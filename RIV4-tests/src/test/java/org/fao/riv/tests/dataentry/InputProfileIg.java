package org.fao.riv.tests.dataentry;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.fao.riv.tests.utils.InputParam.InputParamType;
import org.fao.riv.tests.utils.TestTable;
import org.fao.riv.tests.utils.WebTestUtil;

public class InputProfileIg extends WebTestUtil {
	String igTitle;
	
	 @BeforeClass 
	 public static void setUpClass() {      
	       System.out.println("     test InputProfileIg");
	 }
	
	@Before
	public void deleteExisting() {
		login();
		deletePros(false, true);
	}
	
	@After
    public void close() {
		clickLink("logoff");
		closeBrowser();
    }
	
	@Test
	public void createProfile() throws Exception {
		String resultsTitle = getMessage("ruralInvest")+" :: "+getMessage("search.searchResults");
		String attachTitle = getMessage("ruralInvest")+" :: "+getMessage("attach.new");
		String[] titles = new String[9];
		for (int i=0;i<9;i++) {
			titles[i]=getMessage("ruralInvest")+" :: "+getMessage("profile.step"+(i+1));
		}
		clickLink("goHome");
		
		getTestContext().setResourceBundleName("dataentry/profileIg");
		clickLink("newIgProfile");
		assertTitleEquals(titles[0]);
		
		// should not be able to attach file yet
		assertLinkNotPresent("attachFile");
		
		// input data
		String profName = getMessage("step1.profileName");
		setTextField("profileName", profName);
		setTextField("exchRate", getMessage("step1.exchRate"));
		setTextField("benefNum", getMessage("step1.benefNum"));
		setTextField("benefFamilies", getMessage("step1.benefFamilies"));
		setTextField("location1", getMessage("step1.location1"));
		setTextField("location2", getMessage("step1.location2"));
		setTextField("location3", getMessage("step1.location3"));
		clickRadioOption("withWithout", getMessage("step1.withWithout"));
		
		//TODO: set non-default checkbox and select values
		rivSubmitForm();
		assertElementNotPresent("errorbox");
		assertTitleEquals(titles[1]);
		
		// ATTACH EXTERNAL FILE
		testAttachFile(titles[0], attachTitle);
		
		// STEP 2
		clickLink("step2");
		assertTitleEquals(titles[1]);
		setTextField("benefName",getMessage("step2.benefName"));
		setTextField("benefDesc",getMessage("step2.benefDesc"));
		setTextField("projDesc",getMessage("step2.projDesc"));
		rivSubmitForm();
		assertTitleEquals(titles[2]);
		
		// STEP 3
		setTextField("market",getMessage("step3.market"));
		setTextField("enviroImpact",getMessage("step3.enviroImpact"));
		rivSubmitForm();
		assertTitleEquals(titles[3]);
		
		// STEP 4
		// TODO: download excel template and check that it's correct
		// goods & services
		TestTable tt = new TestTable("goodsListTable", "step4.good.", "newGood", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}});
		tt.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost");
		tt.addParam("unitTotal", InputParamType.TEXT, true).addParam("ownResource").addParam("donated", InputParamType.TEXT, true);
		tt.addParam("econLife").addParam("salvage").addParam("reserve")
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testWithInput();
		
		// labour
		tt = new TestTable("labourListTable", "step4.labour.", "newLabour", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
		.addParam("unitTotal", InputParamType.TEXT, true).addParam("ownResource").addParam("donated", InputParamType.TEXT, true)
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testWithInput();
		
		rivSubmitForm();
		assertTitleEquals(titles[4]);
		
		// STEP 5
		// TODO: download excel template and check
		tt = new TestTable("generalTable", "step5.general.", "newGeneral", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true).addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testWithInput();
		
		tt = new TestTable("generalTableWo", "step5.generalWo.", "newGeneralWo", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true).addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testWithInput();
		
		rivSubmitForm();
		assertTitleEquals(titles[5]);
		
		// STEP 6
		//TODO: download excel template and check
		// add products
		//TODO: test clone product
		//TODO: test delete product
		int i=1;
		boolean nextItem=true;
		while (nextItem) {
			clickLink("addProduct");
			//assertTitleEquals(igTitle);
			setTextField("description", getMessage("step6.product."+i+".description"));
			setTextField("unitType", getMessage("step6.product."+i+".unitType"));
			setTextField("unitNum", getMessage("step6.product."+i+".unitNum"));
			setTextField("cycleLength", getMessage("step6.product."+i+".cycleLength"));
			setTextField("cyclePerYear", getMessage("step6.product."+i+".cyclePerYear"));
			// non-default select option
			rivSubmitForm();
			
			// income
			tt = new TestTable("incomeTable"+(i-1), "step6.product."+i+".income.", "newIncome"+(i-1), true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
			.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost").addParam("transport")
			.addParam("total", InputParamType.TEXT, true).addParam("linked", InputParamType.LINKED, false)
			.addBlanks(5);
			tt.testWithInput();
			
			// input
			tt = new TestTable("inputTable"+(i-1), "step6.product."+i+".input.", "newInput"+(i-1), true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
			.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost").addParam("transport")
			.addParam("total", InputParamType.TEXT, true).addParam("linked", InputParamType.LINKED, false)
			.addBlanks(5);
			tt.testWithInput();
			
			// input
			tt = new TestTable("labourTable"+(i-1), "step6.product."+i+".labour.", "newLabour"+(i-1), true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
			.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
			.addParam("total", InputParamType.TEXT, true).addParam("linked", InputParamType.LINKED, false)
			.addBlanks(5);
			tt.testWithInput();
			
			//TODO: Test Excel download
			
			i++;
			try {
				getMessage("step6.product."+i+".description");
			} catch (Exception e) {
				nextItem=false;
			}
		}
		rivSubmitForm();
		assertTitleEquals(titles[6]);
		
		// STEP 7
		// reference income table
		tt = new TestTable("IncomeTable", "step7.income.", "addIncome", false, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
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
		
		//TODO: test add, delete, copy and move reference item
		rivSubmitForm();
		assertTitleEquals(titles[7]);
		
		// STEP 8
		clickRadioOption("reccCode", getMessage("profile.step8.reccCode"));
		setTextField("reccDate", getMessage("profile.step8.reccDate"));
		setTextField("reccDesc", getMessage("profile.step8.reccDesc"));
		rivSubmitForm();
		assertTitleEquals(titles[8]);
		
		// STEP 9 
		rivSubmitForm();
		assertTitleEquals(resultsTitle);
		
		//Check new profile exists in results table
		assertTableRowCountEquals("results", 6);
		assertTextInTable("results", profName);
		
		//TEST CLONE PROFILE
		assertLinkPresentWithImage("edit.png");
		clickLinkWithImage("edit.png");
		assertTitleEquals(titles[0]);
		clickLink("clone");
		assertTitleEquals(titles[0]);
		assertImagePresentPartial("locked.gif", null);
		
		verifyProfile("dataentry/profileIg");
				
		//Check new profile exists in results table
		assertTableRowCountEquals("results", 7);
		assertTextInTable("results", profName);
	}
}