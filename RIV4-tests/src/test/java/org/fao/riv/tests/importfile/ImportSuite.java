package org.fao.riv.tests.importfile;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(value = Suite.class) 
@SuiteClasses(value = { ImportSettings.class, ImportProfile.class, ImportProject.class})
public class ImportSuite {
	 @BeforeClass 
	 public static void before() { 
		 System.out.println("Beginning import file tests");
	 }
	 
	 @AfterClass
	 public static void after() {
		 System.out.println("concluded import file tests");
	 }
}