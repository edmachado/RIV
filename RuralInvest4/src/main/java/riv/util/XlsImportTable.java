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
		c.numeric=numeric;
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
	
	@SuppressWarnings("unchecked")
	public List<E> readTable(Sheet sheet, MessageSource messageSource) throws ExcelImportException {
		if (sheet!=null) {
			int rowNum = skipTo(sheet, startRow, startWhenColumnIsNumeric);
			boolean goOn=true;
			while(goOn) {
				if (sheet.getRow(rowNum).getCell(3).getCellType()==Cell.CELL_TYPE_NUMERIC) {
					E item;
					try {
						item =  (E) clazz.newInstance();
					} catch (Exception e) {
						throw new RuntimeException("Programming error: class passed to readTable should be same as parameterized generic!", e);
					}
					try {
						for (XlsImportColumn col : columns) {
							if (col.numeric) {
								setObjectProperty(item, col.property, sheet.getRow(rowNum).getCell(col.column).getNumericCellValue());
							} else {
								setObjectProperty(item, col.property, sheet.getRow(rowNum).getCell(col.column).getStringCellValue());
							}
						}
					} catch (IllegalStateException e) {
						String error = 
								messageSource.getMessage("import.excel.error", new Object[] {rowNum+1}, LocaleContextHolder.getLocale()) +
								messageSource.getMessage("import.excel.error.datatype", new Object[0], LocaleContextHolder.getLocale());
						throw new ExcelImportException(error);
					}
					
					Map<String, String> map = new HashMap<String, String>();
					MapBindingResult errors = new MapBindingResult(map, clazz.getName());
					validator.validate(item, errors);
					if (errors.hasErrors()) {
						 String error = 
								messageSource.getMessage("import.excel.error", new Object[] {rowNum+1}, LocaleContextHolder.getLocale()) +
								errors.getAllErrors().get(0).getDefaultMessage().replace("\"", "\\\"");
						throw new ExcelImportException(error);
					}
					
					item.setOrderBy(items.size());
					items.add((E)item);
					rowNum++;
				} else {
					goOn=false;
				}
			}
		}
		
		return items;
	}
	
	private int skipTo(Sheet sheet, int rowNum, int column) {
		boolean goOn=true;
		while (goOn) {
			if (sheet.getRow(rowNum).getCell(3)==null || sheet.getRow(rowNum).getCell(3).getCellType()!=Cell.CELL_TYPE_NUMERIC) {
				rowNum++;
			} else {
				goOn=false;
			}
		}
		return rowNum;
	}
	
	public class XlsImportColumn {
		public int column;
		public String property;
		public boolean numeric;
	}
}