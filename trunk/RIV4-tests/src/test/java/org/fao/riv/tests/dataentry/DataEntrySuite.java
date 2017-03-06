package org.fao.riv.tests.dataentry;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(value = Suite.class)
@SuiteClasses(value = { EditAndSave.class, InputProfileIg.class, InputProfileNig.class, InputProjectIg.class, InputProjectNig.class, 
		Settings.class, SetToNotShared.class, User.class,
		IgProjectNoCycles.class, ReferenceTableItems.class})
public class DataEntrySuite {
	
	 @BeforeClass 
	    public static void before() {      
	        System.out.println("Beginning data entry tests");
	    }
	 
	 @AfterClass
	 public static void after() {      
	        System.out.println("Concluded data entry tests");
	    }
}