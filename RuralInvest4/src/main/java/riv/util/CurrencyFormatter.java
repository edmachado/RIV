package riv.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.lang.Double;
import java.math.BigDecimal;

public class CurrencyFormatter {
	private String thousandSeparator;
	private String decimalSeparator;
	private int decimalLength;
	DecimalFormat dfAll;
	DecimalFormat dfNoThou;
	DecimalFormat dfNoDec;
	DecimalFormat dfInteger;
	DecimalFormatSymbols dfs;
	
	public String getThousandSeparator() {
		return thousandSeparator;
	}

	public String getDecimalSeparator() {
		return decimalSeparator;
	}

	public int getDecimalLength() {
		return decimalLength;
	}

	public CurrencyFormatter(String thousandSeparator, String decimalSeparator, int decimalLength) {
		this.thousandSeparator=thousandSeparator;
		this.decimalSeparator=decimalSeparator;
		this.decimalLength=decimalLength;
		
		dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator(decimalSeparator.charAt(0));
		dfs.setGroupingSeparator(thousandSeparator.charAt(0));
		dfAll = new DecimalFormat(createFormatString(true, true), dfs);
		dfNoThou = new DecimalFormat(createFormatString(true, false), dfs);
		dfNoDec = new DecimalFormat(createFormatString(false, true), dfs);
		dfInteger = new DecimalFormat(createFormatString(false, false), dfs);
	}
	
	public DecimalFormat getDecimalFormat(CurrencyFormat format) {
		switch (format) {
			case NODECIMALS:return dfNoDec;
			case NOTHOUSANDS:return dfNoThou;
			case INTEGER:return dfInteger;
			default: return dfAll;
		}
	}
	
	private String createFormatString(boolean showDecimals, boolean showThousands) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<decimalLength; i++) sb.append("0");
		
		String format;
		if (showDecimals && showThousands) {
			format = (decimalLength!=0) ? "#,##0."+sb.toString() : "#,##0"+sb.toString();
		} else if (showDecimals &! showThousands){
			format = (decimalLength!=0) ? "###0."+sb.toString() : "###0"+sb.toString();
		} else if (showThousands) {
			format = "#,##0";
		} else {
			format ="###0";
		}
		
		return format;
	}
	
	public String formatCurrency(Double value, CurrencyFormat format) {
		if (value.isNaN() || value.isInfinite()) { 
			return "error"; 
		} else {
			switch (format) {
				case ALL:return dfAll.format(value);
				case NODECIMALS:return dfNoDec.format(value);
				case NOTHOUSANDS:return dfNoThou.format(value);
				case INTEGER:return dfInteger.format(value);
				default: return "";
			}
		}
	}
	public String formatCurrency(Integer value, CurrencyFormat format) {
		return formatCurrency(value.intValue(), format);
	}
	public String formatCurrency(BigDecimal value, CurrencyFormat format) {
		return formatCurrency(value.doubleValue(), format);
	}

}

	