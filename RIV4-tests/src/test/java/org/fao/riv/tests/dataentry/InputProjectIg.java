package org.fao.riv.tests.dataentry;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

import java.io.File;
import java.util.concurrent.Callable;

import org.fao.riv.tests.utils.ImportFile;
import org.fao.riv.tests.utils.InputParam.InputParamType;
import org.fao.riv.tests.utils.TestTable;
import org.fao.riv.tests.utils.WebTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class InputProjectIg extends WebTestUtil {
	@Rule
    public TemporaryFolder folder = new TemporaryFolder(new File(this.getClass().getResource("/dataentry").getFile()));
	
	 @BeforeClass 
	 public static void start() {      
	       System.out.println("     test InputProjectIg");
	 }

	@Before
	public void deleteExisting() {
		login();
		deletePros(true, true);
	}
	
	@After
    public void close() {
		clickLink("logoff");
		closeBrowser();
    }
	@Test
	public void upgradeProfile() throws Exception {
		//String resultsTitle = getMessage("ruralInvest")+" :: "+getMessage("search.searchResults");
		String[] titles = new String[13];
		for (int i=0;i<13;i++) {
			titles[i]=getMessage("ruralInvest")+" :: "+getMessage("project.step"+(i+1));
		}

		deletePros(false, true);
		clickLink("goHome");
		importProfile(ImportFile.ProfileIgV22, "igpf", false, false, "Artesanal Clothing Workshop");
		goToPro(false, true, true);

		getTestContext().setResourceBundleName("dataentry/projectIg");
		
		// upgrade
		clickLinkWithImage("upgrade.gif");
		assertTitleEquals(titles[0]);
		
		// step 1
		setTextField("inflationAnnual", getMessage("step1.inflationAnnual"));
		rivSubmitForm();
		assertTitleEquals(titles[1]);
		
		// step 2
		setTextField("beneDirectMen",getMessage("step2.beneDirectMen"));
		setTextField("beneDirectWomen", getMessage("step2.beneDirectWomen"));
		setTextField("beneDirectChild", getMessage("step2.beneDirectChild"));
		setTextField("beneIndirectMen",getMessage("step2.beneIndirectMen"));
		setTextField("beneIndirectWomen",getMessage("step2.beneIndirectWomen"));
		setTextField("beneIndirectChild",getMessage("step2.beneIndirectChild"));
		setTextField("beneIndirectNum",getMessage("step2.beneIndirectNum"));
		rivSubmitForm();
		assertTitleEquals(titles[2]);
		
		// step 3
		setTextField("justification",getMessage("step3.justification"));
		setTextField("activities",getMessage("step3.activities"));
		rivSubmitForm();
		assertTitleEquals(titles[3]);
		
		// STEP 4
		setTextField("technology",getMessage("step4.technology"));
		setTextField("requirements",getMessage("step4.requirements"));
		rivSubmitForm();
		assertTitleEquals(titles[4]);
		
		// STEP 5
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
		// with project items
		tt = new TestTable("suppliesTable", "step8.supply.", "newSupply", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true).addParam("ownResources").addParam("external", InputParamType.TEXT, true)
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testWithInput();
		
		tt = new TestTable("personnelTable", "step8.personnel.", "newPersonnel", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType", InputParamType.SELECT, false).addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true).addParam("ownResources").addParam("external", InputParamType.TEXT, true)
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testWithInput();
				
		rivSubmitForm();
		assertTitleEquals(titles[8]);
		
		// STEP 9
		//TODO: complete test implementation
	}
	
	@Test
	public void addBlockToCompleteProject() throws Exception {
		String blockTitleWith = getMessage("ruralInvest")+" :: "+getMessage("projectBlock.name")+" ("+getMessage("projectBlock.with.with")+")";
		String[] titles = projectStepTitles(true);
		
		importProject(ImportFile.ProjectV40, "igpj", false, false, "T3st Santa Cruz River Transport");
		clickLinkWithImage("edit.png");
		assertTitleEquals(titles[0]);
		clickLink("step9");
		assertTitleEquals(titles[8]);
		
		getTestContext().setResourceBundleName("dataentry/projectIg");
		
		int i=1;
		clickLink("addBlock");
		assertTitleEquals(blockTitleWith);
		setTextField("description", getMessage("step9.block."+i+".description"));
		setTextField("unitType", getMessage("step9.block."+i+".unitType"));
		setTextField("cycleLength", getMessage("step9.block."+i+".cycleLength"));
		selectOption("lengthUnit", getMessage("step9.block."+i+".lengthUnit"));
		setTextField("cyclePerYear", getMessage("step9.block."+i+".cyclePerYear"));
		setTextField("cycleFirstYear", getMessage("step9.block."+i+".cycleFirstYear"));
		setTextField("cycleFirstYearIncome", getMessage("step9.block."+i+".cycleFirstYearIncome"));
		
		
		for (int h=0; h<3; h++) {
			for (int j=0; j<12; j++) {
				for (int k=0; k<2; k++) {
					if (getMessage("step9.block."+i+".ch"+h+"-"+j+"-"+k).equals("true"))
						clickElementByXPath("//table[@id='blockChron']/tbody/tr/td[@id='3-"+h+"-"+j+"-"+k+"']");
				}
			}
		}
		
		// production pattern
		int x=1;
		boolean nextPat=true;
		while (nextPat) {
			setTextField("pat"+x,getMessage("step9.block."+i+".pat"+x));
			x++;
			try {
				getMessage("step9.block."+i+".pat"+x);
			} catch (Exception e) {
				nextPat=false;
			}
		}
		
		rivSubmitForm();

		assertTitleEquals(titles[8]);
	}
	
	@Test
	public void createProject() throws Exception {
		createProject("dataentry/projectIg", true, false);
	}
	
	@Test
	public void exportProperties() throws Exception {
		// import project
		importProject(ImportFile.ProjectV41, "igpj", false, false, "T3st Santa Cruz River Transport");
		clickLinkWithImage("edit.png");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("project.step1"));
		
		// download properties file
		assertLinkPresent("properties");
		clickLink("properties");
		
		String filename="project.properties";
		File f = folder.newFile(filename); 
		saveAs(f);
		
		// import from properties file
		createProject("dataentry/"+folder.getRoot().getName()+"/project", false, true);
		assertLinkPresentWithImage("edit.png", 1);
		clickLinkWithImage("edit.png", 1);
		
		// verify
		verifyProject("dataentry/projectIg", 1);
	}
		
	private void createProject(String resourceBundle, boolean testClone, boolean preexisting) throws Exception {
		String attachTitle = getMessage("ruralInvest")+" :: "+getMessage("attach.new");
		String resultsTitle = getMessage("ruralInvest")+" :: "+getMessage("search.searchResults");
		String blockTitleWith = getMessage("ruralInvest")+" :: "+getMessage("projectBlock.name")+" ("+getMessage("projectBlock.with.with")+")";
		String blockTitleWithout = getMessage("ruralInvest")+" :: "+getMessage("projectBlock.name")+" ("+getMessage("projectBlock.with.without")+")";
		String[] titles = projectStepTitles(true);
		
		goHome();
		
		getTestContext().setResourceBundleName(resourceBundle);
		clickLink("newIgProject");
		assertTitleEquals(titles[0]);
		
		// should not be able to attach file yet
		assertLinkNotPresent("attachFile");		
		
		// input data
		String projName = getMessage("step1.projectName");
		setTextField("projectName", projName);
		setTextField("userCode", getMessage("step1.userCode"));
		setTextField("inflationAnnual", getMessage("step1.inflationAnnual"));
		setTextField("exchRate", getMessage("step1.exchRate"));
		setTextField("duration", getMessage("step1.duration"));
		setTextField("location1", getMessage("step1.location1"));
		setTextField("location2", getMessage("step1.location2"));
		setTextField("location3", getMessage("step1.location3"));
		
		// checkbox and select values
		clickRadioOption("withWithout", getMessage("step1.withWithout"));
		selectOptionByValue("startupMonth", getMessage("step1.startupMonth"));
		selectOptionByValue("beneficiary", getMessage("step1.benefTypeId"));
		selectOptionByValue("enviroCategory", getMessage("step1.enviroId"));
		selectOptionByValue("projCategory", getMessage("step1.projCatId"));
		selectOptionByValue("fieldOffice", getMessage("step1.officeId"));
		selectOptionByValue("status", getMessage("step1.statusId"));
		assertSelectedOptionValueEquals("status", getMessage("step1.statusId"));
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
		// TODO: test autocalc on benefIndirectTotal
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
		
		
		// assets without project
		tt = new TestTable("assetsTableWo", "step7.assetWo.", "newAssetWo", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}});
		tt.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost");
		tt.addParam("total", InputParamType.TEXT, true).addParam("ownResources").addParam("donated").addParam("financed", InputParamType.TEXT, true);
		tt.addParam("econLife").addParam("maintCost").addParam("salvage").addParam("replace", InputParamType.CHECKBOX, false).addParam("yearBegin")
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testWithInput();
		
		// labour without project
		tt = new TestTable("LabourTableWo", "step7.labourWo.", "newLabourWo", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType", InputParamType.SELECT, false).addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true).addParam("ownResources").addParam("donated").addParam("financed", InputParamType.TEXT, true).addParam("yearBegin")
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testWithInput();
		
		// service without project
		tt = new TestTable("ServicesTableWo", "step7.serviceWo.", "newServiceWo", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true).addParam("ownResources").addParam("donated").addParam("financed", InputParamType.TEXT, true).addParam("yearBegin")
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testWithInput();
		
		rivSubmitForm();
		assertTitleEquals(titles[7]);
		
		// STEP 8
		// with project items
		tt = new TestTable("suppliesTable", "step8.supply.", "newSupply", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true).addParam("ownResources").addParam("external", InputParamType.TEXT, true)
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testWithInput();
		
		tt = new TestTable("personnelTable", "step8.personnel.", "newPersonnel", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType", InputParamType.SELECT, false).addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true).addParam("ownResources").addParam("external", InputParamType.TEXT, true)
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testWithInput();
		
		// without project items
		tt = new TestTable("suppliesWoTable", "step8.supplyWo.", "newSupplyWo", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true).addParam("ownResources").addParam("external", InputParamType.TEXT, true)
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testWithInput();
		
		tt = new TestTable("personnelWoTable", "step8.personnelWo.", "newPersonnelWo", true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType", InputParamType.SELECT, false).addParam("unitNum").addParam("unitCost")
		.addParam("total", InputParamType.TEXT, true).addParam("ownResources").addParam("external", InputParamType.TEXT, true)
		.addParam("linked", InputParamType.LINKED, false)
		.addBlanks(5);
		tt.testWithInput();
		
		rivSubmitForm();
		assertTitleEquals(titles[8]);
		
		// STEP 9
		// add blocks
		int blockNum = addBlocks(true, 0, blockTitleWithout, blockTitleWith, titles[8]);

		// test clone and delete block
		assertLinkNotPresent("delete"+blockNum);
		clickLinkWithImage("duplicate.gif", 0);
		assertTitleEquals(blockTitleWith);
		rivSubmitForm();
		assertTitleEquals(titles[8]);
		assertLinkPresent("delete"+blockNum);
		clickLink("delete"+blockNum);
		clickButtonWithText("Delete item");
		assertLinkNotPresent("delete"+blockNum);
		
		// add without blocks
		clickLink("ui-id-2");
		addBlocks(false, blockNum, blockTitleWithout, blockTitleWith, titles[8]);
		
		// go on to next step
		rivSubmitForm();
		assertTitleEquals(titles[9]);
		
		// STEP 10
		// reference income table
		tt = new TestTable("IncomeTable", "step10.income.", "addIncome", false, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitCost").addParam("transport")
		.addBlanks(4);
		tt.testOutput();
		
		// reference cost table
		tt = new TestTable("GoodsTable", "step10.input.", "addInput", false, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitCost").addParam("transport")
		.addBlanks(4);
		tt.testOutput();
		
		// reference labour table
		tt = new TestTable("LabourTable", "step10.labour.", "addLabour", false, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
		.addParam("description").addParam("unitType").addParam("unitCost")
		.addBlanks(4);
		tt.testOutput();
				

		rivSubmitForm();
		assertTitleEquals(titles[10]);
		
		// STEP 11
		setTextField("loan1Interest", getMessage("step11.loan1Interest"));
		setTextField("loan1Duration", getMessage("step11.loan1Duration"));
		setTextField("loan1GraceCapital", getMessage("step11.loan1GraceCapital"));
		setTextField("loan1GraceInterest", getMessage("step11.loan1GraceInterest"));
		setTextField("loan2Amt", getMessage("step11.loan2Amt"));
		setTextField("loan2Interest", getMessage("step11.loan2Interest"));
		setTextField("loan2Duration", getMessage("step11.loan2Duration"));
		setTextField("loan2GraceCapital", getMessage("step11.loan2GraceCapital"));
		setTextField("loan2GraceInterest", getMessage("step11.loan2GraceInterest"));
		setTextField("loan2InitPeriod", getMessage("step11.loan2InitPeriod"));
		
		setTextField("capitalInterest", getMessage("step11.capitalInterest"));
		setTextField("capitalDonate", getMessage("step11.capitalDonate"));
		setTextField("capitalOwn", getMessage("step11.capitalOwn"));		

		//TODO: call autocalc methods and assert amtReqd, amtFinanced, fin period
//		HtmlUnitElementImpl htmlUnit = (HtmlUnitElementImpl) getElementById("loan2Amt");
//		
//		try {
//			((HtmlElement)htmlUnit.getHtmlElement()).mouseOver();
//			assertTextFieldEquals("loan1Amt", "step11.loan1amt");
//		} catch (Exception e) {
//			e.printStackTrace(System.out);
//		}
		
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
//		String xpath = "//table[@id='results']/tbody/tr/td[. = '"+getMessage("step1.projectName")+"']";
//		getElementsByXPath("xpath");
		assertTableRowCountEquals("results", preexisting ? 7 : 6);
		assertTextInTable("results", projName);
	
		if (testClone) {
			// CLONE PROJECT
			assertLinkPresentWithImage("edit.png");
			clickLinkWithImage("edit.png");
			assertTitleEquals(titles[0]);
			clickLinkWithImage("duplicate.gif");
			assertTitleEquals(titles[0]);
			assertImagePresentPartial("locked.gif", null);
			
			verifyProject(resourceBundle,2);
			
			//Check new project exists in results table
			clickLink("allIgpj");
			assertTitleEquals(resultsTitle);
			assertTableRowCountEquals("results", 7);
		}
	}
	
	private int addBlocks(boolean with, int withs, String blockTitleWithout, String blockTitleWith, String titles8) throws Exception {
		int i=1;
		boolean nextItem=true;
		while (nextItem) {
			if (!with) {
				clickLink("addBlockWithout");
				assertTitleEquals(blockTitleWithout);
			} else {
				clickLink("addBlock");
				assertTitleEquals(blockTitleWith);
			}
			
			assertTablePresent("blockChron");
			assertTablePresent("prodPattern"+(i-1+withs));
			
			String propertyBase=with?"step9.block."+i+".":"step9.blockWo."+i+".";
			
			setTextField("description", getMessage(propertyBase+"description"));
			setTextField("unitType", getMessage(propertyBase+"unitType"));
			setTextField("cycleLength", getMessage(propertyBase+"cycleLength"));
			selectOption("lengthUnit", getMessage(propertyBase+"lengthUnit"));
			setTextField("cyclePerYear", getMessage(propertyBase+"cyclePerYear"));
			setTextField("cycleFirstYear", getMessage(propertyBase+"cycleFirstYear"));
			setTextField("cycleFirstYearIncome", getMessage(propertyBase+"cycleFirstYearIncome"));
			
			for (int h=0; h<3; h++) {
				for (int j=0; j<12; j++) {
					for (int k=0; k<2; k++) {
						if (getMessage(propertyBase+"ch"+h+"-"+j+"-"+k).equals("true"))
							clickElementByXPath("//table[@id='blockChron']/tbody/tr/td[@id='"+(i-1+withs)+"-"+h+"-"+j+"-"+k+"']");
					}
				}
			}
			
			// production pattern
			int x=1;
			boolean nextPat=true;
			while (nextPat) {
				setTextField("pat"+x,getMessage(propertyBase+"pat"+x));
				x++;
				try {
					getMessage(propertyBase+"pat"+x);
				} catch (Exception e) {
					nextPat=false;
				}
			}
			
			rivSubmitForm();
			assertTitleEquals(titles8);
			assertTextPresent(getMessage(propertyBase+"description"));
			assertElementPresent("prodPattern"+(i-1+withs));
			
			// income
			TestTable tt = new TestTable("incomeTable"+(i-1+withs), propertyBase+"income.", "newIncome"+(i-1+withs), true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
			.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost").addParam("qtyIntern").addParam("qtyExtern", InputParamType.TEXT, true)
			.addParam("transport").addParam("total", InputParamType.TEXT, true).addParam("totalCash", InputParamType.TEXT, true)
			.addParam("linked", InputParamType.LINKED, false)
		.	addBlanks(5);
			tt.testWithInput();
			
			// input
			tt = new TestTable("inputTable"+(i-1+withs), propertyBase+"input.", "newInput"+(i-1+withs), true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
			.addParam("description").addParam("unitType").addParam("unitNum").addParam("unitCost").addParam("qtyIntern").addParam("qtyExtern", InputParamType.TEXT, true)
			.addParam("transport").addParam("total", InputParamType.TEXT, true).addParam("totalCash", InputParamType.TEXT, true)
			.addParam("linked", InputParamType.LINKED, false)
			.addBlanks(5);
			tt.testWithInput();
			
			// labour
			tt = new TestTable("labourTable"+(i-1+withs), propertyBase+"labour.", "newLabour"+(i-1+withs), true, new Callable<Void>() {public Void call() { rivSubmitForm(); return null;}})
			.addParam("description").addParam("unitType", InputParamType.SELECT, false).addParam("unitNum").addParam("unitCost").addParam("qtyIntern").addParam("qtyExtern", InputParamType.TEXT, true)
			.addParam("total", InputParamType.TEXT, true).addParam("totalCash", InputParamType.TEXT, true)
			.addParam("linked", InputParamType.LINKED, false)
			.addBlanks(5);
			tt.testWithInput();
			
			i++;
			try {
				String check = with ? "step9.block."+i+".description" :
					"step9.blockWo."+i+".description";						
				getMessage(check);
			} catch (Exception e) {
				nextItem=false;
			}
		}

		return i-1;
	}
}