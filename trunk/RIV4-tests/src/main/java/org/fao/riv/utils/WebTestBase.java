package org.fao.riv.utils;

import static net.sourceforge.jwebunit.junit.JWebUnit.assertFormElementPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertImageValid;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertLinkPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertLinkPresentWithImage;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTablePresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTableRowCountEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTableRowsEqual;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextInTable;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTitleEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickButton;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickElementByXPath;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLink;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLinkWithImage;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickRadioOption;
import static net.sourceforge.jwebunit.junit.JWebUnit.getElementsByXPath;
import static net.sourceforge.jwebunit.junit.JWebUnit.getMessage;
import static net.sourceforge.jwebunit.junit.JWebUnit.getTestContext;
import static net.sourceforge.jwebunit.junit.JWebUnit.getTestingEngine;
import static net.sourceforge.jwebunit.junit.JWebUnit.gotoPage;
import static net.sourceforge.jwebunit.junit.JWebUnit.selectOptionByValue;
import static net.sourceforge.jwebunit.junit.JWebUnit.setTextField;
import static net.sourceforge.jwebunit.junit.JWebUnit.setWorkingForm;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class WebTestBase {
	public void assertCheckboxSelected(String name, boolean checked) {
		if (checked) {
			net.sourceforge.jwebunit.junit.JWebUnit.assertCheckboxSelected(name);
		} else {
			net.sourceforge.jwebunit.junit.JWebUnit.assertCheckboxNotSelected(name);
		}
	}
	
	public void goHome() {
		clickLink("goHome");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("mainMenu.home"));
	}
	
	public void deleteTableItems(String tableId) {
		while (getElementsByXPath("//table[@id='"+tableId+"']//a[img[@src[substring(., string-length() -9) = 'delete.gif']]]").size()>0) {
			clickElementByXPath("(//table[@id='"+tableId+"']//a[img[@src[substring(., string-length() -9) = 'delete.gif']]])[1]");
		}
	}
	
	public void importFile(File file) {
		setWorkingForm("form");
    	setTextField("file",file.getAbsolutePath());
    	rivSubmitForm();
    	file=null;
	}
	
	public void testAttachFile(String step1, String attachPageTitle) {
		// go back to step 1
			clickLink("step1");
			assertTitleEquals(step1);
			clickLink("attachFile");
			assertTitleEquals(attachPageTitle);
			importFile(ImportFile.ProfileIgV16.getFile());
			assertTitleEquals(step1);
			assertTableRowCountEquals("attachedFiles", 2);
			assertTableRowsEqual("attachedFiles", 0, new String[][] {
					{"",""},{"profile-ig-1.6.riv",""}
				});
	}
	
	private String cellValueFromXls(File f, int sheetNo, int row, int cell) {
		FileInputStream in=null;
		XSSFWorkbook workbook=null;
		String value=null;
		try {
			in = new FileInputStream(f);
			workbook  = new XSSFWorkbook(in);
			Sheet sheet = workbook.getSheetAt(sheetNo);
			value = sheet.getRow(row).getCell(cell).getStringCellValue();
		} catch (Exception e) {
			fail("Failure testing Excel file: "+e.getMessage());
		} finally {
			try {
				in.close();
				in=null;
				workbook=null;
			} catch (IOException e) {
				fail("Couldn't close Excel file. "+e.getMessage());
			}
		}
		
		return value;
	}
	
	protected void testXls(File f, String title) {
		String value = cellValueFromXls(f, 0, 0, 0);
		assertTrue(value.equals(title));
	}
	
	public void rivSubmitForm() {
		clickButton("submit");
	}
	
	/*
	 * 2-letter language code
	 */
	protected void setLanguage(String language) {
		// set language
		clickLink("welcome");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("mainMenu.config.users.addEdit"));
		
		getTestContext().setResourceBundleName("messages/messages_"+language);
		
		selectOptionByValue("lang", language);
		rivSubmitForm();
		String newTitle = getMessage("ruralInvest")+" :: "+getMessage("user.users");
		assertTitleEquals(newTitle);
	}
	
	public void goToPro(boolean project, boolean incGen, boolean complete) {
		goToResults(project, incGen, complete);
		assertLinkPresentWithImage("edit.png");
		clickLinkWithImage("edit.png");
		if (project) {
			assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("project.step1"));
		} else {
			assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("profile.step1"));
		}
	}
	
	public void goToResults(boolean project, boolean incGen, boolean complete) {
		String type= project ? incGen ? "igpj" : "nigpj" : incGen ? "igpf" : "nigpf";
		type=complete? type+"_yes" : type+"_no";
		
		clickLink("goHome");
		clickLink(type);
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("search.searchResults"));
	}
	
	public void reset() {
		clickLink("gotoSettings");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("mainMenu.config"));
		assertLinkPresent("reset");
		clickLink("reset");	
	}
	
	public void deletePros(boolean project, boolean incGen) {
		String url = String.format("/help/deleteAll?type=%s&ig=%s", 
				project ? "project" : "profile", Boolean.toString(incGen));
		gotoPage(getTestingEngine().getPageURL().toString().replace("/home", url));
		assertTitleEquals("RuralInvest :: Home");
	}
	
	public void importProject(ImportFile file, String type, boolean isGeneric, boolean missingCapital, String projName) {
		importProject( file.getFile(),  type,  isGeneric,  missingCapital,  projName);
	}
	public void importProject(ImportFile file, String type, boolean isGeneric, boolean missingCapital, String projName, boolean isCopy) {
		importProject( file.getFile(),  type,  isGeneric,  missingCapital,  projName, isCopy);
	}
	public void importProject(File file, String type, boolean isGeneric, boolean missingCapital, String projName) {
		importProject(file, type, isGeneric, missingCapital, projName, false);
	}
	private void importProject(File file, String type, boolean isGeneric, boolean missingCapital, String projName, boolean isCopy) {
		System.out.println("importing "+file.getAbsolutePath());
		boolean isIG = type.startsWith("ig");
		String resultsTitle = getMessage("ruralInvest")+" :: "+getMessage("search.searchResults");
		String[] titles = projectStepTitles(isIG);
		
		clickLink("goHome");
		
		// import file
		clickLink("importProjectIg");
		importFile(file);
		if (isGeneric) {
			assertTitleEquals(getMessage("ruralInvest")+" :: Import");
			assertFormElementPresent("genericExchange");
			rivSubmitForm();
		} else if (isCopy) {
			assertTitleEquals(getMessage("ruralInvest")+" :: Import");
			clickRadioOption("overwriteOk", "false");
			rivSubmitForm();
		}
		
		// go through all steps
		for (int i=0; i<13; i++) {
			assertTitleEquals(titles[i]);
			if (i==10 && missingCapital) {
				setTextField("capitalOwn", "100");
			}
			rivSubmitForm();
		}
		
		assertTitleEquals(resultsTitle);
		
		// check that profile visible in results
		assertTablePresent("results");
		assertTextInTable("results",projName);
	}
	
	
	public void importProfile(ImportFile file, String type, boolean isGeneric, boolean missingBenefFamilies, String profName) {
		importProfile( file.getFile(),  type,  isGeneric,  missingBenefFamilies,  profName);	
	}
	public void importProfile(File file, String type, boolean isGeneric, boolean missingBenefFamilies, String profName) {
				
		System.out.println("importing "+file.toString());
		boolean isIG = type.startsWith("ig");
		
		String[] titles = profileStepTitles(isIG);

		clickLink("goHome");
		
		// import file
		clickLink("importProfileIg");
		importFile(file);
		if (isGeneric) {
			assertTitleEquals(getMessage("ruralInvest")+" :: Import");
			assertFormElementPresent("genericExchange");
			rivSubmitForm();
		}
		
		// go through all steps
		for (int i=0; i<9; i++) {
			assertTitleEquals(titles[i]);
			if (i==0 && missingBenefFamilies) {
				setTextField("benefFamilies", "12");
			}
			rivSubmitForm();
		}
		
		// should be back at home
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("search.searchResults"));
		
		// check that profile visible in results
		assertTablePresent("results");
		assertTextInTable("results",profName);
	}
	
	protected void importSettings(ImportFile file) {
		System.out.println("importing "+file.toString());
		//login();
		
		// remove existing profiles, projects and AppConfigs
		reset();
		
    	clickLink("gotoImportSettings");
    	assertTitleEquals(getMessage("ruralInvest")+" :: Import");
    	importFile(file.getFile());
    	assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("mainMenu.config"));
    	assertImageValid("orgLogo", null);
	}
	
    
    protected String[] projectStepTitles(boolean incomeGen) {
    	String[] titles = new String[13];
		for (int i=0;i<13;i++) {
			if ((i==10 &! incomeGen) || (i==9 && incomeGen)) {
				titles[i] = getMessage("ruralInvest")+" :: "+getMessage("reference.reference");
			} else if (!incomeGen && (i==3 || i==8 || i==9 || i==10)) {
				titles[i] = getMessage("ruralInvest")+" :: "+getMessage("project.step"+(i+1)+".nongen");
			} else {
				titles[i] = getMessage("ruralInvest")+" :: "+getMessage("project.step"+(i+1));
			}
		}
		return titles;
    }
    
    protected String[] profileStepTitles(boolean incomeGen) {
    	String[] titles = new String[9];
 		for (int i=0;i<9;i++) {
 			if (i==6) {
 				titles[i]=getMessage("ruralInvest")+" :: "+getMessage("reference.reference");
 			} else if (i==5 &! incomeGen) {
 				titles[i]= getMessage("ruralInvest")+" :: "+getMessage("profile.step"+(i+1)+".nongen");
 			} else {
 				titles[i]= getMessage("ruralInvest")+" :: "+getMessage("profile.step"+(i+1));
 			}
 		}
 		return titles;
    }
    
      
	protected String extractPdfText(File f) throws IOException {
		   PDDocument pdfDocument = PDDocument.load(f);
		   try {
		      return new PDFTextStripper().getText(pdfDocument);
		   } finally {
		      pdfDocument.close();
		   }
		}
	

	// Source: http://notepad2.blogspot.co.il/2012/07/java-detect-if-stream-or-file-is-zip.html
	 public byte[] MAGIC = { 'P', 'K', 0x3, 0x4 };
	/**
	  * Test if a file is a zip file.
	  *
	  * @param f
	  *            the file to test.
	  * @return
	  */
	 public boolean isZipFile(File f) {
	 
	  boolean isZip = true;
	  byte[] buffer = new byte[MAGIC.length];
	  try {
	   RandomAccessFile raf = new RandomAccessFile(f, "r");
	   raf.readFully(buffer);
	   for (int i = 0; i < MAGIC.length; i++) {
	    if (buffer[i] != MAGIC[i]) {
	     isZip = false;
	     break;
	    }
	   }
	   raf.close();
	  } catch (Throwable e) {
	   isZip = false;
	  }
	  return isZip;
	 }
}
