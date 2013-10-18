package org.fao.riv.tests.dataentry;

import static net.sourceforge.jwebunit.junit.JWebUnit.assertImagePresentPartial;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertLinkNotPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertLinkPresentWithImage;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTableRowCountEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextInTable;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTitleEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLink;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLinkWithImage;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickRadioOption;
import static net.sourceforge.jwebunit.junit.JWebUnit.closeBrowser;
import static net.sourceforge.jwebunit.junit.JWebUnit.getMessage;
import static net.sourceforge.jwebunit.junit.JWebUnit.getTestContext;
import static net.sourceforge.jwebunit.junit.JWebUnit.setTextField;

import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.fao.riv.tests.utils.InputParam.InputParamType;
import org.fao.riv.tests.utils.TestTable;
import org.fao.riv.tests.utils.WebTestUtil;

public class InputProfileNig extends WebTestUtil {
	String pageTitle;
	
	 @BeforeClass 
	 public static void setUpClass() {      
	       System.out.println("     test InputProfileNig");
	 }
	
	@Before
	public void deleteExisting() {
		login();
		deletePros(false, false);
	}
	
	@After
    public void close() {
		clickLink("logoff");
        closeBrowser();
    }
	
	@Test
	public void createProfile() throws Exception {
		String resultsTitle = getMessage("ruralInvest")+" :: "+getMessage("search.searchResults");
		String activitiesTitle = getMessage("ruralInvest")+" :: "+getMessage("profile.step6.nongen");
		String attachTitle = getMessage("ruralInvest")+" :: "+getMessage("attach.new");
		String[] titles = profileStepTitles(false);
		
		goHome();
		
		getTestContext().setResourceBundleName("dataentry/profileNig");
		clickLink("newNigProfile");
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
		
		//TODO: set non-default checkbox and select values
		rivSubmitForm();
		assertTitleEquals(titles[1]);
		
		// ATTACH EXTERNAL FILE
		testAttachFile(titles[0], attachTitle);
				
		// STEP 2
		clickLink("step2");
		assertTitleEquals(titles[1]);
		setTextField("benefName",getMessage("step2.benefName"));
		setTextField("benefDesc",getMessage("step2.benefDesc"));
		setTextField("projDesc",getMessage("step2.projDesc"));
		setTextField("sourceFunds", getMessage("step2.sourceFunds"));
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
		tt.addParam("econLife").addParam("salvage").addParam("reserve").addParam("linked", InputParamType.LINKED, false)
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
		rivSubmitForm();
		assertTitleEquals(titles[5]);
		
		// STEP 6
		// add products
		//TODO: download excel template and check
		//TODO: test clone product
		//TODO: test delete product
		int i=1;
		boolean nextItem=true;
		while (nextItem) {
			clickLink("addProduct");
			assertTitleEquals(activitiesTitle);
			setTextField("description", getMessage("step6.product."+i+".description"));
			setTextField("unitType", getMessage("step6.product."+i+".unitType"));
			setTextField("unitNum", getMessage("step6.product."+i+".unitNum"));
			setTextField("cycleLength", getMessage("step6.product."+i+".cycleLength"));
			setTextField("cyclePerYear", getMessage("step6.product."+i+".cyclePerYear"));
			// non-default select option
			rivSubmitForm();
			
			// income
			tt = new TestTable("incomeTable"+(i-1), "step6.product."+i+".income.", "newIncome"+(i-1), true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
			.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
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
		verifyProfileNigTablesStep7();
		
		//TODO: test add, delete, copy and move reference item

		rivSubmitForm();
		assertTitleEquals(titles[7]);
		
		// STEP 8
		//TODO: add recommendation
		clickRadioOption("reccCode", getMessage("step8.reccCode"));
		setTextField("reccDate", getMessage("step8.reccDate"));
		setTextField("reccDesc", getMessage("step8.reccDesc"));
		rivSubmitForm();
		assertTitleEquals(titles[8]);
		
		// STEP 9 
		rivSubmitForm();
		assertTitleEquals(resultsTitle);
		
		//Check new profile exists in results table
		assertTableRowCountEquals("results", 6);
		assertTextInTable("results", profName);
		
		
		// CLONE PROFILE
		assertLinkPresentWithImage("edit.png");
		clickLinkWithImage("edit.png");
		assertTitleEquals(titles[0]);
		clickLink("clone");
		assertTitleEquals(titles[0]);
		assertImagePresentPartial("locked.gif", null);
		
		verifyProfileNig("dataentry/profileNig");
				
		//Check new profile exists in results table
		assertTableRowCountEquals("results", 7);
		assertTextInTable("results", profName);
	}
}