package riv.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import riv.util.ExcelLink;

public class ExcelWrapper {
	private SXSSFWorkbook workbook;
	private Map<Style, CellStyle> styles;
	private Map<ExcelLink, String> links;
	private boolean completeReport;
	private Map<Integer, ExcelBlockLink> blockLinks;
	private Map<Integer, ExcelBlockLink> blockLinksWithoutProject;

	public ExcelWrapper(String currencyPattern) {
		workbook = new SXSSFWorkbook();
		links = new HashMap<ExcelLink, String>();
		blockLinks = new HashMap<Integer, ExcelBlockLink>();
		blockLinksWithoutProject = new HashMap<Integer, ExcelBlockLink>();
		buildCellStyles(currencyPattern);
	}
	
	public String getLink(ExcelLink link) {
		return links.get(link);
	}
	public void addLink(ExcelLink link, String value) {
		links.put(link, value);
	}
	
	public Cell addSelectCell(Row row, int cellNum, Object value, Map<?,String> options, Sheet sheet) {
		XSSFDataValidationConstraint constraint = new XSSFDataValidationConstraint(options.values().toArray(new String[0]));
		CellRangeAddressList range = new CellRangeAddressList(row.getRowNum(), row.getRowNum(), cellNum, cellNum);
		DataValidationHelper helper = sheet.getDataValidationHelper();
		DataValidation dv = helper.createValidation(constraint, range);
		dv.setShowErrorBox(true);
		sheet.addValidationData(dv);
		
		return addTextCell(row, cellNum, options.get(value));
	}
	
	public void addTextCells(Row row, int cellNumMin, int cellNumMax) {
		for (int i=cellNumMin; i<=cellNumMax; i++) {
	 		Cell cell = row.createCell(i);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(new XSSFRichTextString(""));
		}
	}
	
	public Cell addTextCell(Row row, int cellNum, String value) {
		return addTextCell(row, cellNum, value, Style.NORMAL);
	}
	public Cell addTextCell(Row row, int cellNum, String value, Style style) {
		Cell cell = row.createCell(cellNum);
		cell.setCellType(Cell.CELL_TYPE_STRING);
		cell.setCellValue(new XSSFRichTextString(value));
		if (style!=Style.NORMAL) {
			cell.setCellStyle(styles.get(style));
		}
		return cell;
	}
	
	public Cell addBoolCell(Row row, int cellNum, boolean value) {
		Cell cell = row.createCell(cellNum);
		cell.setCellType(Cell.CELL_TYPE_STRING);
		if (value) { 
			cell.setCellValue("X");
		} else { 
			cell.setCellValue(" "); 
		}
		return cell;
	}
	
	public void addNumericCells(Row row, int cellNumMin, int cellNumMax) {
		for (int i=cellNumMin; i<=cellNumMax; i++) {
		 	Cell cell = row.createCell(i);
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		}
	}
	
	public Cell addNumericCell(Row row, int cellNum, double value) {
		return addNumericCell(row, cellNum, value, null);
	}
	public Cell addNumericCell(Row row, int cellNum, double value, Style style) {
		Cell cell = row.createCell(cellNum);
		cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		cell.setCellValue(value);
		if (style!=null) {
			cell.setCellStyle(styles.get(style));
		}
		return cell;
	}
	
	public Cell addNumericCell(Row row, int cellNum, BigDecimal value) {
		return addNumericCell(row, cellNum, value, null);
	}
	public Cell addNumericCell(Row row, int cellNum, BigDecimal value, Style style) {
		Cell cell = row.createCell(cellNum);
		cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		if (value != null)	cell.setCellValue(value.doubleValue());
		if (style!=null) {
			cell.setCellStyle(styles.get(style));
		}
		return cell;
	}
	
	public Cell addFormulaCell(Row row, int cellNum, String formula) {
		return addFormulaCell(row, cellNum, formula, null);
	}
	public Cell addFormulaCell(Row row, int cellNum, String formula, Style style) {
		Cell cell = row.createCell(cellNum);
		if (formula != null){
			cell.setCellFormula(formula);
		}
		if (style!=null) {
			cell.setCellStyle(styles.get(style));
		}
		
		return cell;
	}
	

	public enum Style {
		NORMAL, TITLE, H2, LABEL, LABEL_RIGHT, CURRENCY, CURRENCYUSD, DATE, PERCENT, BIGTEXT, CENTERED, HIDDEN;
	}
	
	private void buildCellStyles(String currencyPattern) {
		styles = new HashMap<Style, CellStyle>();
		
		CellStyle centered = workbook.createCellStyle();
		centered.setAlignment(CellStyle.ALIGN_CENTER);
		styles.put(Style.CENTERED, centered);
		
		CellStyle styleLabel = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		styleLabel.setFont(font);
		styles.put(Style.LABEL, styleLabel);
		
		CellStyle styleLabelRight = workbook.createCellStyle();
		styleLabelRight.setAlignment(CellStyle.ALIGN_RIGHT);
		Font fontLabelRight = workbook.createFont();
		fontLabelRight.setBoldweight(Font.BOLDWEIGHT_BOLD);
		styleLabelRight.setFont(fontLabelRight);
		styles.put(Style.LABEL_RIGHT, styleLabelRight);
		
		CellStyle title = workbook.createCellStyle();
		Font fontTitle = workbook.createFont();
		fontTitle.setBoldweight(Font.BOLDWEIGHT_BOLD);
		fontTitle.setFontHeightInPoints((short)20);
		fontTitle.setColor(IndexedColors.GREY_40_PERCENT.index);
		title.setFont(fontTitle);
		styles.put(Style.TITLE, title);
		
		CellStyle h2 = workbook.createCellStyle();
		Font fontH2 = workbook.createFont();
		fontH2.setBoldweight(Font.BOLDWEIGHT_BOLD);
		fontH2.setFontHeightInPoints((short)14);
		h2.setFont(fontH2);
		styles.put(Style.H2, h2);
		
		CellStyle currency = workbook.createCellStyle();
		DataFormat format = workbook.createDataFormat();
		currency.setDataFormat(format.getFormat(currencyPattern));
	    styles.put(Style.CURRENCY, currency);
	    
		CellStyle currencyUsd = workbook.createCellStyle();
		currencyUsd.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
	    styles.put(Style.CURRENCYUSD, currencyUsd);
	    
	    CellStyle date = workbook.createCellStyle();
	    date.setDataFormat((short)1);
	    styles.put(Style.DATE, date);
	    
	    CellStyle percent = workbook.createCellStyle();
	    percent.setDataFormat(workbook.createDataFormat().getFormat("0%"));
	    styles.put(Style.PERCENT, percent);
	    
	    CellStyle bigText = workbook.createCellStyle();
	    bigText.setWrapText(true);
	    bigText.setVerticalAlignment(CellStyle.VERTICAL_TOP);
	    styles.put(Style.BIGTEXT, bigText);
	    
	    CellStyle hidden = workbook.createCellStyle();
	    hidden.setDataFormat(workbook.createDataFormat().getFormat(";;;"));
	    styles.put(Style.HIDDEN, hidden);
	}
	
	public SXSSFWorkbook getWorkbook() {
		return workbook;
	}
	public Map<Style, CellStyle> getStyles() {
		return styles;
	}

	public boolean isCompleteReport() {
		return completeReport;
	}

	public void setCompleteReport(boolean completeReport) {
		this.completeReport = completeReport;
	}

	public Map<Integer, ExcelBlockLink> getBlockLinks() {
		return blockLinks;
	}

	public Map<Integer, ExcelBlockLink> getBlockLinksWithoutProject() {
		return blockLinksWithoutProject;
	}
}