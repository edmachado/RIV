package org.fao.riv.tests.exceldownload;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

import java.io.File;
import java.io.IOException;

import org.fao.riv.tests.utils.CompareExcel;
import org.fao.riv.tests.utils.ImportFile;
import org.fao.riv.tests.utils.WebTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ProjectReportValues extends WebTestUtil {
	@Rule
    public TemporaryFolder folder = new TemporaryFolder();
	
	 @BeforeClass 
	 public static void setUpClass() {      
	       System.out.println("     test ProjectReportValues");
	 }
	
	@Before
	public void before() {
		login();
		clickLink("gotoSettings");
		setTextField("discountRate", "3");
		rivSubmitForm();
		goHome();
	}
	
	@After
    public void close() {
		clickLink("gotoSettings");
		setTextField("discountRate", "10");
		rivSubmitForm();
		clickLink("logoff");
        closeBrowser();
    }
	
	@Test
	public void testProjectIg() throws IOException {
		deletePros(true, true);
		
		importProject(ImportFile.ProjectTanzaniaRiv, "igpj", false, false, "Dinos Tanzania case");
		goToPro(true,true,true);
		clickLink("step13");
		
		// get complete report
		clickLink("xls_complete");
		File complete = folder.newFile("complete.xlsx");
		saveAs(complete);
		
		// compare cash flow first year 
		// stand-alone
		clickLink("xls_cash_flow_first");
		File f = folder.newFile(); 
		saveAs(f);
		CompareExcel comp = new CompareExcel(f, ImportFile.ProjectTanzaniaCashFlowFirst.getFile());
		comp.CompareSheet(0, 0, 17, 15);
		// complete
		comp = new CompareExcel(complete, ImportFile.ProjectTanzaniaCashFlowFirst.getFile());
		comp.CompareSheet(10, 0, 17, 15);
		// stand-alone vs complete
		comp = new CompareExcel(complete, f);
		comp.CompareSheet(10, 0, 17, 5);
				
		// compare cash flow
		// stand-alone
		clickLink("xls_cash_flow");
		f = folder.newFile(); 
		saveAs(f);
		comp = new CompareExcel(f, ImportFile.ProjectTanzaniaCashFlow.getFile());
		comp.CompareSheet(0, 0, 46, 25);
		// complete
		comp = new CompareExcel(complete, ImportFile.ProjectTanzaniaCashFlow.getFile());
		comp.CompareSheet(12, 0, 46, 25);
		// stand-alone vs complete
		comp = new CompareExcel(complete, f);
		comp.CompareSheet(12, 0, 46, 25);
		
		
		// profitability
		// stand-alone
		clickLink("xls_profitability");
		f = folder.newFile(); 
		saveAs(f);
		comp = new CompareExcel(ImportFile.ProjectTanzaniaProfitability.getFile(), f);
		comp.CompareSheet(0, 2, 33, 22);
		// complete
		comp = new CompareExcel(ImportFile.ProjectTanzaniaProfitability.getFile(), complete);
		comp.CompareSheet(0, 16, 33, 22);
		// stand-alone vs complete
		comp = new CompareExcel(complete, f);
		comp.CompareSheet(16, 2, 33, 22);
		
		// for other reports, compare stand-alone with complete
		// summary
		clickLink("xls_summary");
		f = folder.newFile();
		saveAs(f);
		comp= new CompareExcel(complete, f);
		comp.CompareSheet(0, 0, 60, 5);
		
		// description
		clickLink("xls_description");
		f = folder.newFile();
		saveAs(f);
		comp= new CompareExcel(complete, f);
		comp.CompareSheet(1, 0, 61, 6);
		
		// invest with
		clickLink("xls_invest");
		f = folder.newFile();
		saveAs(f);
		comp= new CompareExcel(complete, f);
		comp.CompareSheet(2, 0, 30, 12);
		// invest without
		comp.CompareSheet(3, 1, 30, 12);
		
		// general
		clickLink("xls_general");
		f = folder.newFile();
		saveAs(f);
		comp= new CompareExcel(complete, f);
		comp.CompareSheet(4, 0, 50, 8);
		
		// prod pattern
		clickLink("xls_prod_pattern");
		f = folder.newFile();
		saveAs(f);
		comp= new CompareExcel(complete, f);
		comp.CompareSheet(5, 0, 11, 33);
		
		// chronology
		clickLink("xls_chronology");
		f = folder.newFile();
		saveAs(f);
		comp= new CompareExcel(complete, f);
		comp.CompareSheet(6, 0, 22, 26);
		
		// blocks with
		clickLink("xls_blocks");
		f = folder.newFile();
		saveAs(f);
		comp= new CompareExcel(complete, f);
		comp.CompareSheet(7, 0, 85, 11);
		// invest without
		comp.CompareSheet(8, 1, 25, 11);
		
		// parameters
		clickLink("xls_parameters");
		f = folder.newFile();
		saveAs(f);
		comp= new CompareExcel(complete, f);
		comp.CompareSheet(9, 0, 30, 4);
		
		// recommendation
		clickLink("xls_recommendation");
		f = folder.newFile();
		saveAs(f);
		comp= new CompareExcel(complete, f);
		comp.CompareSheet(17, 0, 20, 2);
	}
}