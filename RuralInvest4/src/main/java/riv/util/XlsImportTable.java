package riv.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.Validator;

import riv.objects.OrderByable;
import riv.objects.project.HasPerYearItems;
import riv.objects.project.PerYearItem;
import riv.util.ExcelImportException.ErrorType;

public class XlsImportTable<E extends OrderByable> {
	static final Logger LOG = LoggerFactory.getLogger(XlsImportTable.class);
	
	private ArrayList<E> items = new ArrayList<E>();
	private int startRow;
	private int startWhenColumnIsNumeric;
	private ArrayList<XlsImportColumn> columns;
	private Validator validator;
	private boolean required=true;
	Class<? extends OrderByable> clazz;
	
	public XlsImportTable(Class<? extends OrderByable> clazz, int startRow, int startWhenColumnIsNumeric, Validator validator) {
		this.clazz=clazz;
		this.setStartRow(startRow);
		this.startWhenColumnIsNumeric=startWhenColumnIsNumeric;
		this.columns = new ArrayList<XlsImportColumn>();
		this.validator=validator;
	}
	
	public void setRequired(boolean required) {
		this.required=required;
	}
	
	public XlsImportTable<E> addPerYearColumns(int columnBegin, int numYears, String property) {
		for (int i=0;i<numYears;i++) {
			XlsImportColumn c = new XlsImportColumn(columnBegin+i,property,true);
			c.isPerYear=true;
			c.year=i;
			c.totalYears=numYears;
			columns.add(c);
		}
		return this;
	}
	
	public XlsImportTable<E> addColumn(int column, String property, boolean numeric) {
		XlsImportColumn c = new XlsImportColumn(column,property,numeric);
		columns.add(c);
		return this;
	}
	
	public XlsImportTable<E> addBooleanColumn(int column, String property) {
		XlsImportColumn c = new XlsImportColumn(column,property,false);
		c.isBoolean=true;
		columns.add(c);
		return this;
	}
	
	public XlsImportTable<E> addSelectColumn(int column, String property, @SuppressWarnings("rawtypes") Map options) {
		XlsImportColumn c = new XlsImportColumn(column,property,false);
		c.isSelect=true;
		c.options=options;
		columns.add(c);
		return this;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	private void setObjectProperty(Object o, String property, Object value) {
		try {
			BeanUtils.setProperty(o, property, value);
		} catch (Exception e) {
			throw new RuntimeException("Programming error: property should be valid and accessible.", e);
		}
	}
	
	public List<E> readTable(Sheet sheet, MessageSource messageSource) throws ExcelImportException {
		if (sheet!=null) {
			int rowNum = skipTo(sheet, startRow, messageSource);
			while(rowHasData(sheet, rowNum, messageSource)) {
				E item = getObjectFromRow(sheet, rowNum, messageSource);
				
				Map<String, String> map = new HashMap<String, String>();
				MapBindingResult errors = new MapBindingResult(map, clazz.getName());
				validator.validate(item, errors);
				if (errors.hasErrors()) {
					String errorMsg = 
							messageSource.getMessage("import.excel.error", new Object[] {(rowNum+1)}, LocaleContextHolder.getLocale()) +
							messageSource.getMessage(errors.getAllErrors().get(0), LocaleContextHolder.getLocale());//.replace("\"", "\\\"");
					throw new ExcelImportException(errorMsg);
					//.createValidationException(rowNum+1, errors.getAllErrors().get(0).getDefaultMessage().replace("\"", "\\\""), messageSource, LocaleContextHolder.getLocale());
				}
				
				item.setOrderBy(items.size());
				items.add((E)item);
				rowNum++;
			}
			
			if (items.size()==0 && required) {
				throw ExcelImportException.createExcelException(ErrorType.NO_TABLE, (rowNum+1), 0, messageSource, LocaleContextHolder.getLocale());
			}
		}
		
		return items;
	}

	 // if first X cells are blank, consider as no data and the table is considered finished
	private boolean rowHasData(Sheet sheet, int rowNum, MessageSource messageSource) throws ExcelImportException {
		Row row; Cell cell1; //Cell cell2;
		row = sheet.getRow(rowNum);
		if (row==null) { return false; }
		
		boolean cellsAreEmpty = true;
		for (int i=0;i<startWhenColumnIsNumeric-1;i++) {
			if (cellsAreEmpty==true) {
				cell1 = row.getCell(i);
				if (cell1!=null && cell1.getCellType()!=Cell.CELL_TYPE_BLANK) {
					if (cell1.getCellType()==Cell.CELL_TYPE_STRING && !cell1.getStringCellValue().isEmpty()) {
						cellsAreEmpty=false;
					} else if (cell1.getCellType()==Cell.CELL_TYPE_NUMERIC) {
						cellsAreEmpty=false;
					}
				}
			}
		}
		if (cellsAreEmpty) { return false; }
		
//		cell1 = row.getCell(0);
//		cell2 = row.getCell(1);
//		if (
//				(cell1==null || cell1.getCellType()==Cell.CELL_TYPE_BLANK || cell1.getStringCellValue().isEmpty())
//				&& (cell2==null || cell2.getCellType()==Cell.CELL_TYPE_BLANK || cell2.getStringCellValue().isEmpty())
//			){
//			return false;
//		}
		
		// throw exception if cell 1 (always "description") is not text
//		cell1 = row.getCell(0);
//		if (cell1!=null && cell1.getCellType()!=Cell.CELL_TYPE_STRING) {
//			throw ExcelImportException.createExcelException(ErrorType.EXPECTED_TEXT, rowNum+1, 0, messageSource, LocaleContextHolder.getLocale());
//		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private E getObjectFromRow(Sheet sheet, int rowNum, MessageSource messageSource) throws ExcelImportException {
		E item;
		try {
			item =  (E) clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Class passed to readTable should be same as parameterized generic!", e);
		}
		Row row=sheet.getRow(rowNum); 
		int columnNum=0;
		XlsImportColumn column;

		while (columnNum<columns.size()) {
			 column = columns.get(columnNum);
			 Cell cell = row.getCell(column.column);
			 if (column.isBoolean) {
				 boolean yes = cell!=null && cell.getCellType()!=Cell.CELL_TYPE_BLANK;
				 setObjectProperty(item, column.property, yes);
			 } else {
				if (cell==null) {
					throw ExcelImportException.createExcelException(ErrorType.NO_CELL, rowNum+1, column.column, messageSource, LocaleContextHolder.getLocale()); 	
				} else if (column.isSelect) {
					Object value;
					try {
						value = cell.getStringCellValue();
					} catch (IllegalStateException e) {
						throw ExcelImportException.createExcelException(ErrorType.DATA_TYPE, rowNum+1, column.column, messageSource, LocaleContextHolder.getLocale());
					} 
//					catch (NullPointerException e) {
//						throw ExcelImportException.createExcelException(ErrorType.NO_CELL, rowNum+1, column.column, messageSource, LocaleContextHolder.getLocale());
//					}
					
					boolean found=false;
					if (column.options.get(value)!=null) {
						value=column.options.get(value);
						found=true;
					}
					if (!found) {
						throw ExcelImportException.createExcelException(ErrorType.NOT_IN_LIST, rowNum+1, column.column, messageSource, LocaleContextHolder.getLocale());
					}
					
					setObjectProperty(item, column.property, value);
				} else if (column.isNumeric) {
					if (cell.getCellType()==Cell.CELL_TYPE_BLANK) {
						throw ExcelImportException.createExcelException(ErrorType.NO_CELL, rowNum+1, column.column, messageSource, LocaleContextHolder.getLocale());
					}
					double value;
					try {
						value = cell.getNumericCellValue();
					} catch (IllegalStateException e) {
						throw ExcelImportException.createExcelException(ErrorType.DATA_TYPE, rowNum+1, column.column, messageSource, LocaleContextHolder.getLocale());
					} 
//					catch (NullPointerException e) {
//						throw ExcelImportException.createExcelException(ErrorType.NO_CELL, rowNum+1, column.column, messageSource, LocaleContextHolder.getLocale());
//					}
				
					if (column.property.equals("donations(0)")) {
						((riv.objects.HasDonations)item).getDonations().put(0, value);
					} else if (column.isPerYear) {
						HasPerYearItems<PerYearItem> hpy = (HasPerYearItems<PerYearItem>)item;
						if (hpy.getYears().isEmpty()) {
							hpy.addYears(column.totalYears);
						}
						PerYearItem pyi = hpy.getYears().get(column.year);
						setObjectProperty(pyi, column.property, value);
					} else {
						setObjectProperty(item, column.property, value);
					}
				} else { // string cell
//					try {
						if (cell.getCellType()==Cell.CELL_TYPE_NUMERIC) { // just in case, convert numeric value to string
							setObjectProperty(item, column.property, String.valueOf(cell.getNumericCellValue()));
						} else {
							setObjectProperty(item, column.property, cell.getStringCellValue());
						}
//					} catch (NullPointerException e) {
//						throw ExcelImportException.createExcelException(ErrorType.NO_CELL, rowNum+1, column.column, messageSource, LocaleContextHolder.getLocale());
//					}
				}
			 }
			columnNum++; 
		}
		return item;
	}
	
	
	
	//Keep reading lines until you get to a formula cell 
	private int skipTo(Sheet sheet, int rowNum, MessageSource messageSource) throws ExcelImportException {
//		int column=startWhenColumnIsNumeric;
		int skipped=0;
		Row row; Cell cell;
		while (skipped <=4) {
			row = sheet.getRow(rowNum);
			if (row!=null) {
				cell = sheet.getRow(rowNum).getCell(startWhenColumnIsNumeric);
				if (cell!=null && (cell.getCellType()==Cell.CELL_TYPE_FORMULA || cell.getCellType()==Cell.CELL_TYPE_NUMERIC)) {
					return rowNum; // table has started
				}
			}
			
			// otherwise keep looking
			rowNum++;
			skipped++;
		}
		// more than 4 rows have been skipped, the sheet is not in the correct format
		throw ExcelImportException.createExcelException(ErrorType.NO_TABLE, rowNum-4, null, messageSource, LocaleContextHolder.getLocale());	
	}
	
	public class XlsImportColumn {
		public int column;
		public String property;
		public boolean isNumeric;
		public boolean isBoolean;
		public boolean isSelect;
		public boolean isPerYear;
		public int year;
		public int totalYears;
		@SuppressWarnings("rawtypes")
		public Map options;
		
		public XlsImportColumn(int column, String property, boolean isNumeric) {
			this.column=column; this.property=property; this.isNumeric=isNumeric;
		}
	}
	
//	protected class XlsImportColumnPerYear extends XlsImportColumn {
//		public int year;
//		public XlsImportColumnPerYear(int column, String property, boolean isNumeric, int year) {
//			super(column, property, isNumeric);
//			this.year=year;
//		}
//	}
}