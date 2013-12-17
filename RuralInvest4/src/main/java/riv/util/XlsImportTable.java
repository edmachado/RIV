package riv.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.Validator;

import riv.objects.OrderByable;

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
		this.startRow = startRow;
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
	
	public void setObjectProperty(E o, String property, Object value) {
		try {
			BeanUtils.setProperty(o, property, value);
		} catch (Exception e) {
			throw new RuntimeException("Programming error: property should be valid and accessible.", e);
		}
	}
	
	private boolean cellIsTextAndNotBlank(Sheet sheet, MessageSource messageSource, int rowNum, int cell) throws ExcelImportException {
		boolean rowOk;
		 // if the first cell (always "description") is blank, the table is considered finished
		try {
			rowOk = sheet.getRow(rowNum)!=null && sheet.getRow(rowNum).getCell(0)!=null 
					&& sheet.getRow(rowNum).getCell(0).getCellType()!=Cell.CELL_TYPE_BLANK
					&! sheet.getRow(rowNum).getCell(0).getStringCellValue().isEmpty();
		} catch (NullPointerException e) {
			e.printStackTrace(System.out);
			throw new ExcelImportException(messageSource.getMessage("import.excel.error", new Object[] {(rowNum+1)}, LocaleContextHolder.getLocale())
					+messageSource.getMessage("import.error.excel.read", new Object[0], LocaleContextHolder.getLocale()));
		} catch (IllegalStateException e) {
			e.printStackTrace(System.out);
			String error = 
					messageSource.getMessage("import.excel.error", new Object[] {(rowNum+1)}, LocaleContextHolder.getLocale()) +
					messageSource.getMessage("import.excel.error.datatype", new Object[0], LocaleContextHolder.getLocale());
			throw new ExcelImportException(error);
		}
		return rowOk;
	}
	
	@SuppressWarnings("unchecked")
	private E getObjectFromRow(Sheet sheet, int rowNum, MessageSource messageSource) throws ExcelImportException {
		E item;
		try {
			item =  (E) clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Programming error: class passed to readTable should be same as parameterized generic!", e);
		}
		try {
			for (XlsImportColumn column : columns) {
				if (column.isBoolean) {
					boolean yes = sheet.getRow(rowNum).getCell(column.column)!=null && sheet.getRow(rowNum).getCell(column.column).getCellType()!=Cell.CELL_TYPE_BLANK;
					setObjectProperty(item, column.property, yes);
				} else if (column.isSelect) {
					Object value = sheet.getRow(rowNum).getCell(column.column).getStringCellValue();
					for (Object key : column.options.keySet()) {
						if (column.options.get(key).equals(value)) {
							value = key;
							break;
						}
					}
					setObjectProperty(item, column.property, value);
				} else if (column.isNumeric) {
					setObjectProperty(item, column.property, sheet.getRow(rowNum).getCell(column.column).getNumericCellValue());
				} else {
					setObjectProperty(item, column.property, sheet.getRow(rowNum).getCell(column.column).getStringCellValue());
				}
			}
		} catch (IllegalStateException e) {
			e.printStackTrace(System.out);
			String error = 
					messageSource.getMessage("import.excel.error", new Object[] {(rowNum+1)}, LocaleContextHolder.getLocale()) +
					messageSource.getMessage("import.excel.error.datatype", new Object[0], LocaleContextHolder.getLocale());
			throw new ExcelImportException(error);
		} catch (NullPointerException e) {
			e.printStackTrace(System.out);
			String error = "Error occurred reading line "+(rowNum+1);
			throw new ExcelImportException(error);
		}
		return item;
	}
	
	public List<E> readTable(Sheet sheet, MessageSource messageSource) throws ExcelImportException {
		if (sheet!=null) {
			int rowNum = skipTo(sheet, startRow, startWhenColumnIsNumeric, messageSource);
			// if the first cell (always "description") is blank, the table is considered finished
			while(cellIsTextAndNotBlank(sheet, messageSource, rowNum, 0)) {
				E item = getObjectFromRow(sheet, rowNum, messageSource);
				
				Map<String, String> map = new HashMap<String, String>();
				MapBindingResult errors = new MapBindingResult(map, clazz.getName());
				validator.validate(item, errors);
				if (errors.hasErrors()) {
					 String error = 
							messageSource.getMessage("import.excel.error", new Object[] {(rowNum+1)}, LocaleContextHolder.getLocale()) +
							errors.getAllErrors().get(0).getDefaultMessage().replace("\"", "\\\"");
					throw new ExcelImportException(error);
				}
				
				item.setOrderBy(items.size());
				items.add((E)item);
				rowNum++;
			}
		}
		
		return items;
	}
	
	/* 
	 * Keep reading lines until you get to a numeric cell 
	 */
	private int skipTo(Sheet sheet, int rowNum, int column, MessageSource messageSource) throws ExcelImportException {
		boolean goOn=true;
		int skipped=0;
		while (goOn) {
			boolean skipRow;
			try {
				skipRow = sheet.getRow(rowNum)==null || sheet.getRow(rowNum).getCell(column)==null
						|| sheet.getRow(rowNum).getCell(column).getCellType()!=Cell.CELL_TYPE_FORMULA ;
			} catch (NullPointerException e) {
				e.printStackTrace(System.out);
				throw new ExcelImportException(messageSource.getMessage("import.excel.error", new Object[] {rowNum+1}, LocaleContextHolder.getLocale())
						+messageSource.getMessage("import.error.excel.read", new Object[0], LocaleContextHolder.getLocale()));
			}
			if (skipRow) {
				rowNum++;
				if (skipped++ >10) { // skipping too many rows, problem with format
					throw new ExcelImportException(messageSource.getMessage("import.excel.error", new Object[] {rowNum+1}, LocaleContextHolder.getLocale())
							+messageSource.getMessage("import.error.excel.read", new Object[0], LocaleContextHolder.getLocale()));
				}
			} else {
				goOn=false;
			}
		}
		return rowNum;
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