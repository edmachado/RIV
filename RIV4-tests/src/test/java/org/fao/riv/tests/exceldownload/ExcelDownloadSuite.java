package org.fao.riv.tests.exceldownload;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;



@RunWith(value = Suite.class)
@SuiteClasses(value = { ProfileXls.class, ProjectXls.class, SearchResultsReports.class })
public class ExcelDownloadSuite {
	
	 @BeforeClass 
	    public static void before() {      
	        System.out.println("Beginning xls download tests");
	    }
	 
	 @AfterClass
	 public static void after() {      
	        System.out.println("concluded xls download tests");
	    }
}