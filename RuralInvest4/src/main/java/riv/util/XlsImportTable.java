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
import riv.util.ExcelImportException.ErrorType;

public class XlsImportTable<E extends OrderByable> {
	static final Logger LOG = LoggerFactory.getLogger(XlsImportTable.class);
	
	private ArrayList<E> items = new ArrayList<E>();
	private int startRow;
	private int startWhenColumnIsNumeric;
	private ArrayList<XlsImportColumn> columns;
	private Validator validator;
	Class<? extends OrderByable> clazz;
	
	public XlsImportTable(Class<? extends OrderByable> clazz, int startRow, int startWhenColumnIsNumeric, Validator validator) {
		this.clazz=clazz;
		this.setStartRow(startRow);
		this.startWhenColumnIsNumeric=startWhenColumnIsNumeric;
		this.columns = new ArrayList<XlsImportColumn>();
		this.validator=validator;
	}
	
	public XlsImportTable<E> addColumn(int column, String property, boolean numeric) {
		XlsImportColumn c = new XlsImportColumn();
		c.column=column;
		c.property=property;
		c.isNumeric=numeric;
		columns.add(c);
		return this;
	}
	
	public XlsImportTable<E> addBooleanColumn(int column, String property) {
		XlsImportColumn c = new XlsImportColumn();
		c.column=column;
		c.property=property;
		c.isBoolean=true;
		columns.add(c);
		return this;
	}
	
	public XlsImportTable<E> addSelectColumn(int column, String property, @SuppressWarnings("rawtypes") Map options) {
		XlsImportColumn c = new XlsImportColumn();
		c.column=column;
		c.property=property;
		c.isSelect=true;
		c.options=options;
		columns.add(c);
		return this;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public void setObjectProperty(E o, String property, Object value) {
		try {
			BeanUtils.setProperty(o, property, value);
		} catch (Exception e) {
			throw new RuntimeException("Programming error: property should be valid and accessible.", e);
		}
	}
	
	public List<E> readTable(Sheet sheet, MessageSource messageSource) throws ExcelImportException {
		if (sheet!=null) {
			int rowNum = skipTo(sheet, startRow, startWhenColumnIsNumeric, messageSource);
			while(rowHasData(sheet, rowNum, messageSource)) {
				E item = getObjectFromRow(sheet, rowNum, messageSource);
				
				Map<String, String> map = new HashMap<String, String>();
				MapBindingResult errors = new MapBindingResult(map, clazz.getName());
				validator.validate(item, errors);
				if (errors.hasErrors()) {
					String errorMsg = 
							messageSource.getMessage("import.excel.error", new Object[] {(rowNum+1)}, LocaleContextHolder.getLocale()) +
							errors.getAllErrors().get(0).getDefaultMessage().replace("\"", "\\\"");
					throw new ExcelImportException(errorMsg);
					//.createValidationException(rowNum+1, errors.getAllErrors().get(0).getDefaultMessage().replace("\"", "\\\""), messageSource, LocaleContextHolder.getLocale());
				}
				
				item.setOrderBy(items.size());
				items.add((E)item);
				rowNum++;
			}
		}
		
		return items;
	}

	 // if the first two cells are blank, consider as no data and the table is considered finished
	private boolean rowHasData(Sheet sheet, int rowNum, MessageSource messageSource) throws ExcelImportException {
		Row row; Cell cell1; Cell cell2;
		row = sheet.getRow(rowNum);
		if (row==null) { return false; }
		cell1 = row.getCell(0);
		cell2 = row.getCell(1);
		if (
				(cell1==null || cell1.getCellType()==Cell.CELL_TYPE_BLANK || cell1.getStringCellValue().isEmpty())
				&& (cell2==null || cell2.getCellType()==Cell.CELL_TYPE_BLANK || cell2.getStringCellValue().isEmpty())
			){
			return false;
		}
		// throw exception if cell 1 (always "description") is not text
		if (cell1!=null && cell1.getCellType()!=Cell.CELL_TYPE_STRING) {
			throw ExcelImportException.createExcelException(ErrorType.EXPECTED_TEXT, rowNum+1, 0, messageSource, LocaleContextHolder.getLocale());
		}
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
			 if (cell==null &! column.isBoolean) { // check that cell isn't null, except for boolean
				throw ExcelImportException.createExcelException(ErrorType.NO_CELL, rowNum+1, column.column, messageSource, LocaleContextHolder.getLocale());
			 }
			 
			 if (column.isBoolean) {
				 boolean yes = cell!=null && cell.getCellType()!=Cell.CELL_TYPE_BLANK;
				 setObjectProperty(item, column.property, yes);
			 } else if (column.isSelect) {
				Object value;
				try {
					value = cell.getStringCellValue();
				} catch (IllegalStateException e) {
					throw ExcelImportException.createExcelException(ErrorType.DATA_TYPE, rowNum+1, column.column, messageSource, LocaleContextHolder.getLocale());
				}
				boolean found=false;
				for (Object key : column.options.keySet()) {
					if (column.options.get(key).equals(value)) {
						value = key;
						found=true;
						break;
					}
				}
				// TODO: if options are in a different language the value will not be found
				if (!found) {
					throw ExcelImportException.createExcelException(ErrorType.NOT_IN_LIST, rowNum+1, column.column, messageSource, LocaleContextHolder.getLocale());
				}
				setObjectProperty(item, column.property, value);
			} else if (column.isNumeric) {
				double value;
				try {
					value = cell.getNumericCellValue();
				} catch (IllegalStateException e) {
					throw ExcelImportException.createExcelException(ErrorType.DATA_TYPE, rowNum+1, column.column, messageSource, LocaleContextHolder.getLocale());
				}
			
				if (column.property.equals("donations(0)")) {
					((riv.objects.HasDonations)item).getDonations().put(0, value);
				} else {
					setObjectProperty(item, column.property, value);
				}
			} else { // string cell
				if (cell.getCellType()==Cell.CELL_TYPE_NUMERIC) { // just in case, convert numeric value to string
					setObjectProperty(item, column.property, String.valueOf(cell.getNumericCellValue()));
				} else {
					setObjectProperty(item, column.property, cell.getStringCellValue());
				}
			}
			columnNum++; 
		}
		return item;
	}
	
	
	
	//Keep reading lines until you get to a formula cell 
	private int skipTo(Sheet sheet, int rowNum, int column, MessageSource messageSource) throws ExcelImportException {
		int skipped=0;
		Row row; Cell cell;
		while (skipped <=10) {
			row = sheet.getRow(rowNum);
			if (row!=null) {
				cell = sheet.getRow(rowNum).getCell(column);
				if (cell!=null && cell.getCellType()==Cell.CELL_TYPE_FORMULA) {
					return rowNum; // table has started
				}
			}
			
			// otherwise keep looking
			rowNum++;
			skipped++;
		}
		// more than 10 rows have been skipped, the sheet is not in the correct format
		throw ExcelImportException.createExcelException(ErrorType.NO_TABLE, rowNum-10, null, messageSource, LocaleContextHolder.getLocale());	
	}
	
	public class XlsImportColumn {
		public int column;
		public String property;
		public boolean isNumeric;
		public boolean isBoolean;
		public boolean isSelect;
		@SuppressWarnings("rawtypes")
		public Map options;
	}
}