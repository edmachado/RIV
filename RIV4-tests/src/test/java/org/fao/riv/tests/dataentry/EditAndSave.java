package org.fao.riv.tests.dataentry;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

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
		
		// step 7
		clickLink("step7");
		testEditSaveTable("project.step7", "project.invest", "assetsTable");
		testEditSaveTable("project.step7", "project.invest", "LabourTable");
		testEditSaveTable("project.step7", "project.invest", "ServicesTable");
		testEditSaveTable("project.step7", "project.invest", "assetsTableWo");
		testEditSaveTable("project.step7", "project.invest", "LabourTableWo");
		testEditSaveTable("project.step7", "project.invest", "ServicesTableWo");
		
		// step 8
		clickLink("step8");
		testEditSaveTable("project.step8", "projectGeneralSupplies", "suppliesTable");
		testEditSaveTable("project.step8", "projectGeneralSupplies", "suppliesWoTable");
		testEditSaveTable("project.step8", "projectGeneralPersonnel", "personnelTable");
		testEditSaveTable("project.step8", "projectGeneralPersonnel", "personnelWoTable");
		
		// step 9
		clickLink("step9");
		testEditSaveTable("project.step9", "projectBlockIncome", "incomeTable0");
		testEditSaveTable("project.step9", "projectBlockInput", "inputTable0");
		testEditSaveTable("project.step9", "projectBlockLabour", "labourTable0");
		int withs = getElementsByXPath("//div[@id='tabs-with']/div/div/fieldset").size();
		clickLink("ui-id-2");
		testEditSaveTable("project.step9", "projectBlockIncome", "incomeTable"+withs);
		testEditSaveTable("project.step9", "projectBlockInput", "inputTable"+withs);
		testEditSaveTable("project.step9", "projectBlockLabour", "labourTable"+withs);
	}
	
	@Test
	public void NigProject() throws Exception {
		deletePros(true, false);
		
		importProject(ImportFile.ProjectNig40, "nigpj", false, false, "Example Case: Community Earth Dam");
		clickLinkWithImage("edit.png");
		
		// step 7
		clickLink("step7");
		testEditSaveTable("project.step7", "project.invest", "assetsTable");
		testEditSaveTable("project.step7", "project.invest", "LabourTable");
		testEditSaveTable("project.step7", "project.invest", "ServicesTable");
		
		// step 8
		clickLink("step8");
		testEditSaveTable("project.step8", "project.step8", "inputTable");
		testEditSaveTable("project.step8", "project.step8", "labourTable");
		testEditSaveTable("project.step8", "project.step8", "generalTable");
		
		// step 9
		clickLink("step9");
		testEditSaveTable("project.step9.nongen", "projectActivityCharge", "incomeTable0");
		testEditSaveTable("project.step9.nongen", "projectBlockInput", "inputTable0");
		testEditSaveTable("project.step9.nongen", "projectBlockLabour", "labourTable0");
		
		// step 10
		clickLink("step10");
		testEditSaveTable("project.step10.nongen", "project.step10.nongen", "contributionTable");
	}
	
	@Test
	public void IgProfile() throws Exception {
		deletePros(false, true);
		
		importProfile(ImportFile.ProfileIgV40, "igpf_no", false, false, "T3st Irrigation project");
		clickLinkWithImage("edit.png");
		
		// step 4
		clickLink("step4");
		testEditSaveTable("profile.step4", "profile.incomeGen", "goodsListTable");
		testEditSaveTable("profile.step4", "profile.incomeGen", "labourListTable");
		
		// step 5
		clickLink("step5");
		testEditSaveTable("profile.step5", "profile.incomeGen", "generalTable");
		testEditSaveTable("profile.step5", "profile.incomeGen", "generalTableWo");
		
		// step 6
		clickLink("step6");
		testEditSaveTable("profile.step6", "profileProductIncome", "incomeTable0");
		testEditSaveTable("profile.step6", "profileProductInput", "inputTable0");
		testEditSaveTable("profile.step6", "profileProductLabour", "labourTable0");
	}
	
	@Test
	public void NigProfile() throws Exception {
		deletePros(false, false);
		
		importProfile(ImportFile.ProfileNig40, "nigpf_no", false, false, "Community Health Centre");
		clickLinkWithImage("edit.png");
		
		// step 4
		clickLink("step4");
		testEditSaveTable("profile.step4", "profile.nonIncomeGen", "goodsListTable");
		testEditSaveTable("profile.step4", "profile.nonIncomeGen", "labourListTable");
		
		// step 5
		clickLink("step5");
		testEditSaveTable("profile.step5", "profile.nonIncomeGen", "generalTable");
		
		// step 6
		clickLink("step6");
		testEditSaveTable("profile.step6.nongen", "profileActivityCharge", "incomeTable0");
		testEditSaveTable("profile.step6.nongen", "profileProductInput", "inputTable0");
		testEditSaveTable("profile.step6.nongen", "profileProductLabour", "labourTable0");
	}
	
	private void testEditSaveTable(String tablePageTitleKey, String itemPageTitleKey, String tableName) {
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage(tablePageTitleKey));
		String xpath = "//table[@id='"+tableName+"']/tbody/tr/td/a[img[@alt='View/Edit item']]";
		// open item
		clickElementByXPath(xpath);
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage(itemPageTitleKey));
		// save item
		rivSubmitForm();
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage(tablePageTitleKey));
	}
}