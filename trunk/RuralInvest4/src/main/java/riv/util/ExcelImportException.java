package riv.util;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public class ExcelImportException extends Exception {
	private static final long serialVersionUID = 1L;

	public static ExcelImportException createExcelException(ErrorType type, Integer row, Integer column, MessageSource messageSource, Locale locale) {
		String message;
		switch (type) {
		case NO_TABLE:
			message = messageSource.getMessage("import.excel.error.noTable", new Object[] {row}, locale);
			break;
		case NOT_IN_LIST:
			message = messageSource.getMessage("import.excel.error.selectList", new Object[] {getColumn(column) + row}, LocaleContextHolder.getLocale());
			break;
		case NO_CELL:
			message= messageSource.getMessage("import.excel.error.noData", new Object[] {getColumn(column) + row}, LocaleContextHolder.getLocale());
			break;
		case DATA_TYPE:
		case EXPECTED_TEXT:
			message= messageSource.getMessage("import.excel.error.datatype", new Object[] {getColumn(column) + row}, LocaleContextHolder.getLocale());
			break;
		default:
			message="unknown error";
			break;
		}
		return new ExcelImportException(message);
	}
	
	public ExcelImportException(String message) {
		super(message);
	}
	
	public enum ErrorType {
		NO_TABLE,
		NO_CELL,
		DATA_TYPE,
		EXPECTED_TEXT,
		NOT_IN_LIST;
	}
	
	private static String getColumn(int number){
		String converted = "";
        // Repeatedly divide the number by 26 and convert the
        // remainder into the appropriate letter.
        while (number >= 0) {
            int remainder = number % 26;
            converted = (char)(remainder + 'A') + converted;
            number = (number / 26) - 1;
        }
        return converted;
    }
}