package org.fao.riv.tests.calculations;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(value = Suite.class)
@SuiteClasses(value = { Incremental.class })
public class CalculationSuite {
	
	 @BeforeClass 
	    public static void before() {      
	        System.out.println("Beginning calculation tests");
	    }
	 
	 @AfterClass
	 public static void after() {      
	        System.out.println("concluded calculation tests");
	    }
}