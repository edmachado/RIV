package org.fao.riv.tests.dataentry;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

import java.io.File;
import java.util.concurrent.Callable;

import org.junit.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.fao.riv.tests.WebTest;
import org.fao.riv.utils.ImportFile;
import org.fao.riv.utils.TestTable;
import org.fao.riv.utils.InputParam.InputParamType;

public class InputProfileIg extends WebTest {
	@Rule
    public TemporaryFolder folder = new TemporaryFolder(new File(this.getClass().getResource("/dataentry").getFile()));
	
	
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
	public void convertToProject() throws Exception {
		// import
		importProfile(ImportFile.ProfileIgV40, "igpf_no", false, false, "T3st Irrigation project");
		// convert to project
		clickLinkWithImage("edit.png");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("profile.step1"));
		clickLink("upgrade");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("project.step1"));
	}
	
	@Test
	public void fromWithToWithWithout() {
		// import
		importProfile(ImportFile.ProfileIgV40, "igpf_no", false, false, "T3st Irrigation project");
		// convert to project
		clickLinkWithImage("edit.png");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("profile.step1"));
		assertRadioOptionSelected("withWithout", "true");
		clickRadioOption("withWithout", "false");
		clickButtonWithText("close");
		rivSubmitForm();
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("profile.step2"));
	}
	
	@Test
	public void cloneProduct() {
		String[] titles = profileStepTitles(true);
		String blockTitleWith = getMessage("ruralInvest")+" :: "+getMessage("profile.step6")+" ("+getMessage("profileProduct.with.with")+")";
		
		importProfile(ImportFile.ProfileIgV40, "igpf_no", false, false, "T3st Irrigation project");
		clickLinkWithImage("edit.png");
		assertTitleEquals(titles[0]);
		clickLink("step6");
		assertTitleEquals(titles[5]);
		
		String xpth = "//table[@id='descriptionTable']//a/img[@src='../../img/delete.gif']";
		int deletes = getElementsByXPath(xpth).size();
		
		clickLinkWithImage("duplicate.gif", 0);
		assertTitleEquals(blockTitleWith);
		
		rivSubmitForm();
		assertTitleEquals(titles[5]);
		Assert.assertEquals(getElementsByXPath(xpth).size(),deletes+1);
	}
	
	@Test
	public void removeWithoutScenario() throws Exception {
		importProfile(ImportFile.ProfileIgV42, "igpf_no", false, false, "T3st Irrigation project");
		// edit project
		goToPro(false, true, true);
		String[] titles = profileStepTitles(true);
		
		assertRadioOptionNotSelected("withWithout", "false");
		assertRadioOptionSelected("withWithout", "true");
		clickRadioOption("withWithout", "false");
		assertRadioOptionSelected("withWithout", "false");
		assertRadioOptionNotSelected("withWithout", "true");
		rivSubmitForm();

		assertTitleEquals(titles[1]);
	}
	
	@Test
	public void addProductToCompleteProfile() throws Exception {
		String[] titles = profileStepTitles(true);
		String blockTitleWith = getMessage("ruralInvest")+" :: "+getMessage("profile.step6")+" ("+getMessage("profileProduct.with.with")+")";
		
		importProfile(ImportFile.ProfileIgV40, "igpf_no", false, false, "T3st Irrigation project");
		clickLinkWithImage("edit.png");
		assertTitleEquals(titles[0]);
		clickLink("step6");
		assertTitleEquals(titles[5]);
		
		clickLink("addProduct");
		assertTitleEquals(blockTitleWith);
		getTestContext().setResourceBundleName("dataentry/profileIg");
		int i=1;
		setTextField("description", getMessage("step6.product."+i+".description"));
		setTextField("unitType", getMessage("step6.product."+i+".unitType"));
		setTextField("unitNum", getMessage("step6.product."+i+".unitNum"));
		setTextField("cycleLength", getMessage("step6.product."+i+".cycleLength"));
		setTextField("cyclePerYear", getMessage("step6.product."+i+".cyclePerYear"));
		rivSubmitForm();
		assertTitleEquals(titles[5]);
	}
	
	 
	
	private void createProfile(String resourceBundle) throws Exception {
		String resultsTitle = getMessage("ruralInvest")+" :: "+getMessage("search.searchResults");
		String attachTitle = getMessage("ruralInvest")+" :: "+getMessage("attach.new");
		String[] titles = profileStepTitles(true);
		String blockTitleWith = getMessage("ruralInvest")+" :: "+getMessage("profile.step6")+" ("+getMessage("profileProduct.with.with")+")";
		String blockTitleWithout = getMessage("ruralInvest")+" :: "+getMessage("profile.step6")+" ("+getMessage("profileProduct.with.without")+")";
		
		clickLink("goHome");
		
		getTestContext().setResourceBundleName(resourceBundle);
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
		
		// goods & services without
		tt = new TestTable("goodsWoListTable", "step4.goodWo.", "newGoodWo", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}});
		tt.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost");
		tt.addParam("unitTotal", InputParamType.TEXT, true).addParam("ownResource").addParam("donated", InputParamType.TEXT, true);
		tt.addParam("econLife").addParam("salvage").addParam("reserve")
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testWithInput();
		
		// labour without
		tt = new TestTable("labourWoListTable", "step4.labourWo.", "newLabourWo", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
		.addParam("unitTotal", InputParamType.TEXT, true).addParam("ownResource").addParam("donated", InputParamType.TEXT, true)
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testWithInput();
		
		rivSubmitForm();
		assertTitleEquals(titles[4]);
		
		// STEP 5
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
		//TODO: test delete product
		// add products
		int products = Integer.parseInt(getMessage("step6.product.count"));
		for (int i=1;i<=products;i++) {
			boolean withoutProject = Boolean.parseBoolean(getMessage("step6.product."+i+".withoutProject")); 
			if (withoutProject) {
				clickLink("addProductWithout");
				assertTitleEquals(blockTitleWithout);
			} else {
				clickLink("addProduct");
				assertTitleEquals(blockTitleWith);
			}
			
			setTextField("description", getMessage("step6.product."+i+".description"));
			setTextField("unitType", getMessage("step6.product."+i+".unitType"));
			setTextField("unitNum", getMessage("step6.product."+i+".unitNum"));
			setTextField("cycleLength", getMessage("step6.product."+i+".cycleLength"));
			setTextField("cyclePerYear", getMessage("step6.product."+i+".cyclePerYear"));
			//TODO non-default select option
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
	}
	
		@Test
		public void createProfile() throws Exception {
			String resource="dataentry/profileIg";
			createProfile(resource);
	}
}
