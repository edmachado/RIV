package org.fao.riv.tests.excelimport;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(value = Suite.class)
@SuiteClasses(value = {ProfileExcelImport.class, ProjectExcelImport.class })
public class ExcelImportSuite {
	
	 @BeforeClass 
	    public static void before() {      
	        System.out.println("Beginning xls import tests");
	    }
	 
	 @AfterClass
	 public static void after() {      
	        System.out.println("concluded xls import tests");
	    }
}