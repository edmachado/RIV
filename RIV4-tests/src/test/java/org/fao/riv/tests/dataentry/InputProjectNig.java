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

public class InputProjectNig extends WebTestUtil {
	 @BeforeClass 
	 public static void start() {      
	       System.out.println("     test InputProjectNig");
	 }
	
	 @Before
	 public void deleteExisting() {
		login();
		deletePros(true, false); 
	 }
	 
	 @After
	    public void close() {
			clickLink("logoff");
			closeBrowser();
	    }
	 
	@Test
	public void createProject() throws Exception {
		String attachTitle = getMessage("ruralInvest")+" :: "+getMessage("attach.new");
		String resultsTitle = getMessage("ruralInvest")+" :: "+getMessage("search.searchResults");
		String blockTitle = getMessage("ruralInvest")+" :: "+getMessage("projectActivity.name");
		String[] titles = new String[13];
		for (int i=0;i<13;i++) {
			titles[i]= 
					i==3 || i==8 || i==9 || i==10 ? getMessage("ruralInvest")+" :: "+getMessage("project.step"+(i+1)+".nongen") :
					getMessage("ruralInvest")+" :: "+getMessage("project.step"+(i+1));
		}

		goHome();
		
		getTestContext().setResourceBundleName("dataentry/projectNig");
		clickLink("newNigProject");
		assertTitleEquals(titles[0]);
		
		// should not be able to attach file yet
		assertLinkNotPresent("attachFile");
				
		// input data
		String projName = getMessage("step1.projectName");
		setTextField("projectName", projName);
		setTextField("userCode", getMessage("step1.userCode"));
		setTextField("exchRate", getMessage("step1.exchRate"));
		setTextField("duration", getMessage("step1.duration"));
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
		setTextField("beneDirectMen",getMessage("step2.beneDirectMen"));
		setTextField("beneDirectWomen", getMessage("step2.beneDirectWomen"));
		setTextField("beneDirectChild", getMessage("step2.beneDirectChild"));
		// TODO: test autocalc on benefDirectTotal
		setTextField("beneDirectNum",getMessage("step2.beneDirectNum"));
		setTextField("beneIndirectMen",getMessage("step2.beneIndirectMen"));
		setTextField("beneIndirectWomen",getMessage("step2.beneIndirectWomen"));
		setTextField("beneIndirectChild",getMessage("step2.beneIndirectChild"));
		// TODO: test autocalc on benefDirectTotal
		setTextField("beneIndirectNum",getMessage("step2.beneIndirectNum"));
		setTextField("benefDesc",getMessage("step2.benefDesc"));
		rivSubmitForm();
		assertTitleEquals(titles[2]);
		
		// STEP 3
		setTextField("justification",getMessage("step3.justification"));
		setTextField("projDesc",getMessage("step3.projDesc"));
		setTextField("activities",getMessage("step3.activities"));
		rivSubmitForm();
		assertTitleEquals(titles[3]);
		
		// STEP 4
		setTextField("technology",getMessage("step4.technology"));
		setTextField("requirements",getMessage("step4.requirements"));
		rivSubmitForm();
		assertTitleEquals(titles[4]);
		
		// STEP 5
		setTextField("sustainability",getMessage("step5.sustainability"));
		setTextField("enviroImpact",getMessage("step5.enviroImpact"));
		setTextField("market",getMessage("step5.market"));
		rivSubmitForm();
		assertTitleEquals(titles[5]);

		// STEP 6
		setTextField("organization",getMessage("step6.organization"));
		setTextField("assumptions",getMessage("step6.assumptions"));
		rivSubmitForm();
		assertTitleEquals(titles[6]);
		
		// STEP 7
		// assets
		TestTable tt = new TestTable("assetsTable", "step7.asset.", "newAsset", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}});
		tt.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost");
		tt.addParam("total", InputParamType.TEXT, true).addParam("ownResources").addParam("donated").addParam("financed", InputParamType.TEXT, true);
		tt.addParam("econLife").addParam("maintCost").addParam("salvage").addParam("replace", InputParamType.CHECKBOX, false).addParam("yearBegin")
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testWithInput();
		
		// labour
		tt = new TestTable("LabourTable", "step7.labour.", "newLabour", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType", InputParamType.SELECT, false).addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true).addParam("ownResources").addParam("donated").addParam("financed", InputParamType.TEXT, true).addParam("yearBegin")
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testWithInput();
		
		// service
		tt = new TestTable("ServicesTable", "step7.service.", "newService", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true).addParam("ownResources").addParam("donated").addParam("financed", InputParamType.TEXT, true).addParam("yearBegin")
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testWithInput();
		
		rivSubmitForm();
		assertTitleEquals(titles[7]);
		
		// STEP 8
		tt = new TestTable("inputTable", "step8.input", "addMaterial", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true)
		.addParam("statePublic").addParam("other1").addParam("ownResource", InputParamType.TEXT, true)
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testWithInput();
		
		tt = new TestTable("labourTable", "step8.labour", "addLabour", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType", InputParamType.SELECT, false).addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true)
		.addParam("statePublic").addParam("other1").addParam("ownResource", InputParamType.TEXT, true)
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testWithInput();
		
		tt = new TestTable("generalTable", "step8.general", "addMaintenance", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true)
		.addParam("statePublic").addParam("other1").addParam("ownResource", InputParamType.TEXT, true)
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testWithInput();
		
		rivSubmitForm();
		assertTitleEquals(titles[8]);
		
		// STEP 9
		//TODO: test clone block
		//TODO: test delete block
		// add blocks
		int i=1;
		boolean nextItem=true;
		while (nextItem) {
			clickLink("addBlock");
			assertTitleEquals(blockTitle);
			setTextField("description", getMessage("step9.block."+i+".description"));
			setTextField("unitType", getMessage("step9.block."+i+".unitType"));
			setTextField("cycleLength", getMessage("step9.block."+i+".cycleLength"));
			selectOption("lengthUnit", getMessage("step9.block."+i+".lengthUnit"));
			setTextField("cyclePerYear", getMessage("step9.block."+i+".cyclePerYear"));
			
			// production pattern 
			for (int x=1; x<=20; x++) { 
				String value = getMessage("step9.block."+i+".pat"+x);
				setTextField("pat"+x,value);
				assertTextFieldEquals("pat"+x, value);
			}
			
			rivSubmitForm();
			assertTitleEquals(titles[8]);
			
			// income
			tt = new TestTable("incomeTable"+(i-1), "step9.block"+i+".income", "newIncome"+(i-1), true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
			.addParam("description").addParam("unitType")
			.addParam("unitNum").addParam("unitCost")
			.addParam("total", InputParamType.TEXT, true)
			.addParam("linked", InputParamType.LINKED, false)
			.addBlanks(5);
			tt.testWithInput();
			
			// input
			tt = new TestTable("inputTable"+(i-1), "step9.block"+i+".input", "newInput"+(i-1), true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
			.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost").addParam("qtyIntern").addParam("extern", InputParamType.TEXT, true)
			.addParam("transport").addParam("total", InputParamType.TEXT, true).addParam("totalCash", InputParamType.TEXT, true)
			.addParam("linked", InputParamType.LINKED, false)
			.addBlanks(5);
			tt.testWithInput();
			
			// labour
			tt = new TestTable("labourTable"+(i-1), "step9.block"+i+".labour", "newLabour"+(i-1), true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
			.addParam("description").addParam("unitType", InputParamType.SELECT, false)
			.addParam("unitNum")
			.addParam("unitCost").addParam("qtyIntern").addParam("extern", InputParamType.TEXT, true)
			.addParam("total", InputParamType.TEXT, true).addParam("totalCash", InputParamType.TEXT, true)
			.addParam("linked", InputParamType.LINKED, false)
			.addBlanks(5);
			tt.testWithInput();
			
			//TODO: Test Excel download
			
			i++;
			try {
				getMessage("step9.block."+i+".withoutProject");
			} catch (Exception e) {
				nextItem=false;
			}
		}
		rivSubmitForm();
		assertTitleEquals(titles[9]);
		
		
		// STEP 10
		tt = new TestTable("contributionTable", "step10.contribution", "newContrib", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}});
		tt.addParam("description")
		.addParam("contribType", InputParamType.SELECT, false)
		.addParam("unitType")
		.addParam("unitNum").addParam("unitCost");
		tt.addParam("total", InputParamType.TEXT, true)
		.addBlanks(5);
		tt.testWithInput();
		
		rivSubmitForm();
		assertTitleEquals(titles[10]);
		
		// STEP 11
		// reference income
		verifyProjectNigTablesStep11();
		rivSubmitForm();
		assertTitleEquals(titles[11]);
		
		// STEP 12
		clickRadioOption("reccCode", getMessage("step12.reccCode"));
		setTextField("reccDate", getMessage("step12.reccDate"));
		setTextField("reccDesc", getMessage("step12.reccDesc"));
		rivSubmitForm();
		assertTitleEquals(titles[12]);
		
		// STEP 13 
		rivSubmitForm();
		assertTitleEquals(resultsTitle);
		
		//Check new project exists in results table
		assertTableRowCountEquals("results", 6);
		assertTextInTable("results", projName);
	
		// CLONE PROJECT
		assertLinkPresentWithImage("edit.png");
		clickLinkWithImage("edit.png");
		assertTitleEquals(titles[0]);
		clickLinkWithImage("duplicate.gif");
		assertTitleEquals(titles[0]);
		assertImagePresentPartial("locked.gif", null);
		
		// step 1
		verifyProjectNig("dataentry/projectNig");
		
		//Check new project exists in results table
		assertTableRowCountEquals("results", 7);
		assertTextInTable("results", projName);
	}
}