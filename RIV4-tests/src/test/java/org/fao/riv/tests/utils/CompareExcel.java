package org.fao.riv.tests.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;

import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;

public class CompareExcel {
	private File fBook1;
	private File fBook2;
	
	public CompareExcel(File fBook1, File fBook2) {
		this.fBook1=fBook1;
		this.fBook2=fBook2;
	}
	
	public void CompareSheet(int sheetIndex1, int sheetIndex2, int rows, int columns) {
		FileInputStream fis1=null;
		FileInputStream fis2=null;
		XSSFWorkbook book1;
		XSSFWorkbook book2;
		try {
			fis1 = new FileInputStream(fBook1);
			fis2 = new FileInputStream(fBook2);
			book1 = new XSSFWorkbook(fis1);
			book2 = new XSSFWorkbook(fis2);
//			book1= WorkbookFactory.create(fBook1);
//			book2= WorkbookFactory.create(fBook2);
			
			XSSFSheet sheet1 = book1.getSheetAt(sheetIndex1);
			XSSFSheet sheet2 = book2.getSheetAt(sheetIndex2);
			
			CompareSheets(sheet1, sheet2, rows, columns);
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fis1.close();
				fis2.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			book1=null;
			book2=null;
		}
	}
	
	private void CompareSheets(XSSFSheet sheet1, XSSFSheet sheet2, int rows, int cols) {
		FormulaEvaluator eval1 = sheet1.getWorkbook().getCreationHelper().createFormulaEvaluator();
		FormulaEvaluator eval2 = sheet2.getWorkbook().getCreationHelper().createFormulaEvaluator();
		
		for (int rowNum=0;rowNum<rows;rowNum++) {
			XSSFRow row1 = sheet1.getRow(rowNum);
			XSSFRow row2 = sheet2.getRow(rowNum);
			if (row1==null || !row1.cellIterator().hasNext()) {
				Assert.assertTrue("Rows are not equal: row "+rowNum, row2==null || !row2.cellIterator().hasNext());
			} else {
				for (int col=0;col<cols;col++) {
					try {
						if (row1.getCell(col)==null || row1.getCell(col).getCellType()==Cell.CELL_TYPE_BLANK) {
							Assert.assertTrue("Cells are not equal: row "+rowNum+" col "+col, row2.getCell(col)==null||row2.getCell(col).getCellType()==Cell.CELL_TYPE_BLANK);
						} else {
							Assert.assertTrue("Cells are not equal: row "+rowNum+" col "+col, row2.getCell(col)!=null&&row2.getCell(col).getCellType()!=Cell.CELL_TYPE_BLANK);
							
							XSSFCell cell1 = row1.getCell(col);
							XSSFCell cell2 = row2.getCell(col);

							if (cell1.getCellType()==Cell.CELL_TYPE_NUMERIC || cell1.getCellType()==Cell.CELL_TYPE_FORMULA) {
								try {
									Assert.assertEquals("Cells are not equal: row "+rowNum+" col "+col, cellAsDouble(cell1, eval1), cellAsDouble(cell2, eval2), 0.02);
								} catch (NotImplementedException e) {
									System.out.println("Function not implemented in POI. " +e.getMessage());
								}
							} else if (cell1.getCellType()==Cell.CELL_TYPE_STRING) {
								Assert.assertEquals("Cells are not equal: row "+rowNum+" col "+col, cell1.getStringCellValue(), cell2.getStringCellValue());
							} else if (cell1.getCellType()==Cell.CELL_TYPE_ERROR){
								Assert.assertTrue("Cells are not equal: row "+rowNum+" col "+col, cell2.getCellType()==Cell.CELL_TYPE_ERROR);
							} else {
								Assert.assertTrue("unexpected! sheet "+sheet1.getSheetName()+" row "+rowNum+" col "+col, false);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Assert.assertTrue("problem! row "+rowNum+" col "+col, false);
					}
				}
			}
		}
	}
	
	private double cellAsDouble(XSSFCell cell, FormulaEvaluator eval) {
		if (cell.getCellType()==Cell.CELL_TYPE_FORMULA){
			DataFormatter formatter = new DataFormatter();
			String begin = formatter.formatCellValue(cell, eval).replaceAll(",","").replaceAll("%", ""); 
			if (begin.equals("#DIV/0!")) { return 0.0; }
			BigDecimal bd = new BigDecimal(Double.parseDouble(begin));
			bd.setScale(2,BigDecimal.ROUND_HALF_UP);
			return bd.doubleValue();
		} else if (cell.getCellType()==Cell.CELL_TYPE_NUMERIC) {
			BigDecimal bd = new BigDecimal(cell.getNumericCellValue());
			bd.setScale(2,BigDecimal.ROUND_HALF_UP);
			return bd.doubleValue();
		}  else {
			Assert.assertTrue("problem! row "+cell.getRowIndex()+" col "+cell.getColumnIndex(), false);
			return 0.0;
		}
	}
}