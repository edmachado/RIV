package org.fao.riv.tests.download;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(value = Suite.class)
@SuiteClasses(value = { ProfilePdf.class, ProjectPdf.class, Error.class, Settings.class, ExportGeneric.class })
public class DownloadSuite {
	
	 @BeforeClass 
	    public static void before() {      
	        System.out.println("Beginning download tests");
	    }
	 
	 @AfterClass
	 public static void after() {      
	        System.out.println("concluded download tests");
	    }
}