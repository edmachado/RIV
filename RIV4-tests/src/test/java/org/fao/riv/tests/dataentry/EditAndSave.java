package org.fao.riv.tests.dataentry;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

import java.util.List;

import net.sourceforge.jwebunit.api.IElement;

import org.fao.riv.tests.utils.ImportFile;
import org.fao.riv.tests.utils.WebTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class EditAndSave extends WebTestUtil {
	String igTitle;
	
	 @BeforeClass 
	 public static void setUpClass() {      
	       System.out.println("     test EditAndSave");
	 }
	
	@Before
	public void login2() {
		login();
	}
	
	@After
    public void close() {
		clickLink("logoff");
		closeBrowser();
    }
	
	/*
	 * Tests editing and saving one item from each table
	 */
	@Test
	public void IgProject() throws Exception {
		deletePros(true, true);
		
		importProject(ImportFile.ProjectV40, "igpj", false, false, "T3st Santa Cruz River Transport");
		clickLinkWithImage("edit.png");
		
		String[] titles = projectStepTitles(true);
		for (int i=1;i<7;i++) {
			testPage(titles[i-1],titles[i], i>=3 && i<=7);
		}
		
		// step 7
		testEditSaveTable("project.step7", "projectInvestAsset", "assetsTable");
		testEditSaveTable("project.step7", "projectInvestLabour", "LabourTable");
		testEditSaveTable("project.step7", "projectInvestService", "ServicesTable");
		testEditSaveTable("project.step7", "projectInvestAsset", "assetsTableWo");
		testEditSaveTable("project.step7", "projectInvestLabour", "LabourTableWo");
		testEditSaveTable("project.step7", "projectInvestService", "ServicesTableWo");
		rivSubmitForm();
		assertTitleEquals(titles[7]);
		
		// step 8
		testEditSaveTable("project.step8", "projectGeneralSupplies", "suppliesTable");
		testEditSaveTable("project.step8", "projectGeneralSupplies", "suppliesWoTable");
		testEditSaveTable("project.step8", "projectGeneralPersonnel", "personnelTable");
		testEditSaveTable("project.step8", "projectGeneralPersonnel", "personnelWoTable");
		rivSubmitForm();
		assertTitleEquals(titles[8]);
		
		// step 9
		testEditSaveTable("project.step9", "projectBlockIncome", "incomeTable0");
		testEditSaveTable("project.step9", "projectBlockInput", "inputTable0");
		testEditSaveTable("project.step9", "projectBlockLabour", "labourTable0");
		int withs = getElementsByXPath("//div[@id='tabs-with']/div/div/fieldset").size();
		clickLink("ui-id-2");
		testEditSaveTable("project.step9", "projectBlockIncome", "incomeTable"+withs);
		testEditSaveTable("project.step9", "projectBlockInput", "inputTable"+withs);
		testEditSaveTable("project.step9", "projectBlockLabour", "labourTable"+withs);
		rivSubmitForm();
		assertTitleEquals(titles[9]);
		
		// step 10
		testEditSaveTable("reference.reference","reference.incomes","IncomeTable");
		testEditSaveTable("reference.reference","reference.costs","GoodsTable");
		testEditSaveTable("reference.reference","reference.labours","LabourTable");
		rivSubmitForm();
		//assertTitleEquals(titles[10]);
		
		// remaining pages
		testPage(titles[10],titles[11]);
	}
	
	@Test
	public void NigProject() throws Exception {
		deletePros(true, false);
		
		importProject(ImportFile.ProjectNig40, "nigpj", false, false, "Example Case: Community Earth Dam");
		clickLinkWithImage("edit.png");
		
		String[] titles = projectStepTitles(false);
		for (int i=1;i<7;i++) {
			testPage(titles[i-1],titles[i], i>=3 && i<=7);
		}
		
		// step 7
		testEditSaveTable("project.step7", "projectInvestAsset", "assetsTable");
		testEditSaveTable("project.step7", "projectInvestLabour", "LabourTable");
		testEditSaveTable("project.step7", "projectInvestService", "ServicesTable");
		rivSubmitForm();
		assertTitleEquals(titles[7]);
		
		// step 8
		testEditSaveTable("project.step8", "projectNongenInput", "inputTable");
		testEditSaveTable("project.step8", "projectNongenLabour", "labourTable");
		testEditSaveTable("project.step8", "projectNongenGeneral", "generalTable");
		rivSubmitForm();
		assertTitleEquals(titles[8]);
		
		// step 9
		testEditSaveTable("project.step9.nongen", "projectActivityCharge", "incomeTable0");
		testEditSaveTable("project.step9.nongen", "projectBlockInput", "inputTable0");
		testEditSaveTable("project.step9.nongen", "projectBlockLabour", "labourTable0");
		rivSubmitForm();
		assertTitleEquals(titles[9]);
		
		// step 10
		testEditSaveTable("project.step10.nongen", "project.step10.nongen", "contributionTable");
		rivSubmitForm();
		assertTitleEquals(titles[10]);
		
		// step 11
		testEditSaveTable("reference.reference","reference.incomes","IncomeTable");
		testEditSaveTable("reference.reference","reference.costs","GoodsTable");
		testEditSaveTable("reference.reference","reference.labours","LabourTable");
		rivSubmitForm();
		assertTitleEquals(titles[11]);
	}
	
	@Test
	public void IgProfile() throws Exception {
		deletePros(false, true);
		
		importProfile(ImportFile.ProfileIgV42, "igpf_no", false, false, "T3st Irrigation project");
		clickLinkWithImage("edit.png");
		
		String[] titles = profileStepTitles(true);
		for (int i=1;i<4;i++) {
			testPage(titles[i-1],titles[i], i==2 || i==3);
		}
		
		// step 4
		testEditSaveTable("profile.step4", "profile.incomeGen", "goodsListTable");
		testEditSaveTable("profile.step4", "profile.incomeGen", "labourListTable");
		testEditSaveTable("profile.step4", "profile.incomeGen", "goodsListWoTable");
		testEditSaveTable("profile.step4", "profile.incomeGen", "labourListWoTable");
		rivSubmitForm();
		assertTitleEquals(titles[4]);
		
		// step 5
		testEditSaveTable("profile.step5", "profile.incomeGen", "generalTable");
		testEditSaveTable("profile.step5", "profile.incomeGen", "generalTableWo");
		rivSubmitForm();
		assertTitleEquals(titles[5]);
		
		// step 6
		testEditSaveTable("profile.step6", "profileProductIncome", "incomeTable0");
		testEditSaveTable("profile.step6", "profileProductInput", "inputTable0");
		testEditSaveTable("profile.step6", "profileProductLabour", "labourTable0");
		rivSubmitForm();
		assertTitleEquals(titles[6]);
	}
	
	@Test
	public void NigProfile() throws Exception {
		deletePros(false, false);
		
		importProfile(ImportFile.ProfileNig40, "nigpf_no", false, false, "Community Health Centre");
		clickLinkWithImage("edit.png");
		
		String[] titles = profileStepTitles(false);
		for (int i=1;i<4;i++) {
			testPage(titles[i-1],titles[i], i==2 || i==3);
		}
		
		// step 4
		testEditSaveTable("profile.step4", "profile.nonIncomeGen", "goodsListTable");
		testEditSaveTable("profile.step4", "profile.nonIncomeGen", "labourListTable");
		rivSubmitForm();
		assertTitleEquals(titles[4]);
		
		// step 5
		testEditSaveTable("profile.step5", "profile.nonIncomeGen", "generalTable");
		rivSubmitForm();
		assertTitleEquals(titles[5]);
		
		// step 6
		testEditSaveTable("profile.step6.nongen", "profileActivityCharge", "incomeTable0");
		testEditSaveTable("profile.step6.nongen", "profileProductInput", "inputTable0");
		testEditSaveTable("profile.step6.nongen", "profileProductLabour", "labourTable0");
		rivSubmitForm();
		assertTitleEquals(titles[6]);
	}
	
	@Test
	public void appConfigs() {
		clickLink("gotoOffices");
		testEditSaveTable("fieldOffice.fieldOffices", "fieldOffice.addOffice", "row", true);
		clickLink("gotoCategories");
		testEditSaveTable("projectCategory.categories", "projectCategory.addCat", "row", true);
		clickLink("gotoBenefs");
		testEditSaveTable("beneficiary.beneficiaries", "beneficiary.addBenef", "row", true);
		clickLink("gotoEnviros");
		testEditSaveTable("enviroCategory.categories", "enviroCategory.addCat", "row", true);
		clickLink("gotoStatuses");
		testEditSaveTable("projectStatus.statuses", "projectStatus.addStatus", "row", true);
		
	}
	
	private void testPage(String pageTitle, String successPageTitle) {
		testPage(pageTitle, successPageTitle, false);
	}
	private void testPage(String pageTitle, String successPageTitle, boolean skipNegative) {
		assertTitleEquals(pageTitle);
		
		// url of open page
		String url = getTestingEngine().getPageURL().toString();
		
		//test with blanks, negative, 3001 characters
		clickLink("test-blanks");
		rivSubmitForm();
		assertElementPresent("errorbox");
		assertTitleEquals(pageTitle);
		
		if (!skipNegative) {
			clickLink("test-negative");
			rivSubmitForm();
			assertElementPresent("errorbox");
			assertTitleEquals(pageTitle);
		}
		
		clickLink("test-3001");
		rivSubmitForm();
		assertElementPresent("errorbox");
		assertTitleEquals(pageTitle);
		
		// save item
		gotoPage(url);
		rivSubmitForm();
		assertElementNotPresent("errorbox");
		assertTitleEquals(successPageTitle);
	}
	
	private void testEditSaveTable(String tablePageTitleKey, String itemPageTitleKey, String tableName) {
		testEditSaveTable(tablePageTitleKey, itemPageTitleKey, tableName, false);
	}
	private void testEditSaveTable(String tablePageTitleKey, String itemPageTitleKey, String tableName, boolean skipNegative) {
		String xpath = "//table[@id='"+tableName+"']/tbody/tr/td/a[img[@alt='View/Edit item']]";
		List<IElement> edits = getElementsByXPath(xpath); 
		
		if (edits.size()>0) {
			String tableTitle = getMessage("ruralInvest")+" :: "+getMessage(tablePageTitleKey);
			assertTitleEquals(tableTitle);
			// open item and test
			clickElementByXPath("("+xpath+")["+(edits.size())+"]");
			testPage(getMessage("ruralInvest")+" :: "+getMessage(itemPageTitleKey), tableTitle, skipNegative);
		}
	}
}