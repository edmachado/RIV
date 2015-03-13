package riv.util.validators;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.Size;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.Errors;

public class ValidateUtils {
	
	private static Integer getLengthFromAnnotation(Object bean, String fieldName) {
		Integer length=null;
		try {
			Field field = bean.getClass().getDeclaredField(fieldName);
			if (field.getType().equals(String.class)) {
				Size size = field.getAnnotation(Size.class);
				length = size.max();
			}
		} catch (Exception ex) {
			try {
				Field field = bean.getClass().getSuperclass().getDeclaredField(fieldName);
				Size size = field.getAnnotation(Size.class);
				length = size.max();
			} catch (Exception ex2) {;}	
		}
		return length;
	}
	public static void enforceLength(Object bean, String fieldName, String fieldCode, Errors errors) {
		enforceLength(bean, fieldName, fieldCode, errors, false);
	}
	private static void enforceLength(Object bean, String fieldName, String fieldCode, Errors errors, boolean noResource) {
		if (errors.getFieldError(fieldName)==null) { // don't enforce if field already flagged as error
			Integer maxLength = getLengthFromAnnotation(bean, fieldName);
			if (maxLength!=null) {
				String propertyValue=null;
				try {
					propertyValue=(String)PropertyUtils.getProperty(bean, fieldName);
				} catch (Exception e) {	; }
				if (propertyValue.length()>maxLength) {
					if (noResource) {
						errors.rejectValue(fieldName, "error.maxLength", new Object[] {fieldCode,String.valueOf(maxLength)}, "\""+fieldCode+"\" is required");
					} else {
						errors.rejectValue(fieldName, "error.maxLength", new Object[] {new DefaultMessageSourceResolvable(new String[] {fieldCode}),String.valueOf(maxLength)}, "\""+fieldName+"\" is required");
					}
				}
			}
		}
	}
	
//	private static Long getMaxFromAnnotation(Object bean, String fieldName) {
//		Long max=null;
//		try {
//			Field field = bean.getClass().getDeclaredField(fieldName);
//			if (field.getType().equals(String.class)) {
//				Max maxAnnotation = field.getAnnotation(Max.class);
//				max = maxAnnotation.value();
//			}
//		} catch (Exception ex) {
//			try {
//				Field field = bean.getClass().getSuperclass().getDeclaredField(fieldName);
//				Max maxAnnotation = field.getAnnotation(Max.class);
//				max = maxAnnotation.value();
//			} catch (Exception ex2) {;}	
//		}
//		return max;
//	}
//	
//	private static void enforceMax(Object bean, String fieldName, String fieldCode, Errors errors) {
//		if (errors.getFieldError(fieldName)==null) { // don't enforce if field already flagged as error
//			Long max = getMaxFromAnnotation(bean, fieldName);
//			if (max!=null) {
//				Double propertyValue=null;
//				try {
//					propertyValue=Double.parseDouble(PropertyUtils.getProperty(bean, fieldName).toString());
//				} catch (Exception e) {	; }
//				if (propertyValue>max) {
//					errors.rejectValue(fieldName, "error.max", new Object[] {new DefaultMessageSourceResolvable(new String[] {fieldCode}),String.valueOf(max)}, "\""+fieldName+"\" is required");
//				}
//			}
//		}
//	}
	
	public static void rejectIfEmpty(Object bean, String fieldName, String fieldCode, Errors errors) {
		Object propertyValue=null;
		try {
			propertyValue=PropertyUtils.getProperty(bean, fieldName);
		} catch (Exception e) {	; }
		if (propertyValue == null || propertyValue.equals("")) {
			errors.rejectValue(fieldName, "error.fieldRequired", new Object[] {new DefaultMessageSourceResolvable(new String[] {fieldCode})}, "\""+fieldName+"\" is required");
		}
		enforceLength(bean, fieldName, fieldCode, errors);
	}
	
	public static void rejectIfEmptyNoResource(Object bean, String fieldName, String fieldDesc, Errors errors) {
		String propertyValue=null;
		try {
			propertyValue = PropertyUtils.getProperty(bean, fieldName).toString();
		} catch (Exception e) { }
		
		if (propertyValue == null || propertyValue.trim().isEmpty()) {
			errors.rejectValue(fieldName, "error.fieldRequired", new Object[] {fieldDesc}, "\""+fieldDesc+"\" is required");	
		} else {
			enforceLength(bean, fieldName, fieldDesc, errors, true);
		}
	}
	
	public static void rejectIfNegative(Object bean, String fieldName, String fieldCode, Errors errors) {
		rejectIfNegative(bean, fieldName, fieldCode, errors, 4);// default scale of 4
	}
	public static void rejectIfNegative(Object bean, String fieldName, String fieldCode, Errors errors, int scale) {
		double propertyValue=0;
		boolean isMissing = false;
		try {
			propertyValue=Double.parseDouble(PropertyUtils.getProperty(bean, fieldName).toString());
		} catch (Exception e) {	
			isMissing=true;
		}
		if (isMissing)  {
			errors.rejectValue(fieldName, "error.fieldRequired", new Object[] {new DefaultMessageSourceResolvable(new String[] {fieldCode})}, "\""+fieldName+"\" is required");
		} else if (round(propertyValue, scale) < 0 ) { 
			errors.rejectValue(fieldName, "error.requiredNonNegative", new Object[] {new DefaultMessageSourceResolvable(new String[] {fieldCode})}, "\""+fieldName+"\" must be non-negative");
		}
	}
	
	private static double round(double d, int scale) {
		 BigDecimal bd = new BigDecimal(Double.toString(d));
		    bd = bd.setScale(scale,BigDecimal.ROUND_HALF_UP);
		    return bd.doubleValue();
	 }
	
	public static void rejectIfEmptyOrNegativeOrOverMax(Object bean, String fieldName, String fieldCode, Double max, Errors errors) {
		double propertyValue=0;
		try {
			propertyValue=Double.parseDouble(PropertyUtils.getProperty(bean, fieldName).toString());
		} catch (Exception e) {	
			errors.rejectValue(fieldName, "error.fieldRequired", new Object[] {new DefaultMessageSourceResolvable(new String[] {fieldCode})}, "\""+fieldName+"\" is required");
			return;
		}
		if (propertyValue < 0) {
			errors.rejectValue(fieldName, "error.requiredNonNegative", new Object[] {new DefaultMessageSourceResolvable(new String[] {fieldCode})}, "\""+fieldName+"\" must be non-negative");
		}
		if (max!=null) {
			if (round(propertyValue, 4)>max) {
				errors.rejectValue(fieldName, "error.max", new Object[] {new DefaultMessageSourceResolvable(new String[] {fieldCode}),String.valueOf(max)}, "\""+fieldName+"\" is required");
			}
		}
	}
	public static void rejectIfEmptyOrNegative(Object bean, String fieldName, String fieldCode, Errors errors) {
		rejectIfEmptyOrNegativeOrOverMax(bean, fieldName, fieldCode, null, errors);
	}
	public static void rejectMapValueIfEmptyOrNegative(@SuppressWarnings("rawtypes") Map map, String mapName, Object key, String fieldName, Errors errors) {
		double propertyValue=0;
		try {
			propertyValue=Double.parseDouble(map.get(key).toString());
		} catch (Exception e) {
			errors.rejectValue(mapName+"["+key.toString()+"]", "error.fieldRequired", new Object[] {fieldName}, "\""+fieldName+"\" is required");
			return;
		}
		if (propertyValue < 0) {
			errors.rejectValue(mapName+"["+key.toString()+"]", "error.requiredNonNegative", new Object[] {fieldName}, "\""+fieldName+"\" must be non-negative");
		}
	}
	
	public static void rejectIfZeroOrNegative(Object bean, String fieldName, String fieldCode, Errors errors) {
		double propertyValue=0;
		try {
			propertyValue=Double.parseDouble(PropertyUtils.getProperty(bean, fieldName).toString());
		} catch (Exception e) {	
			errors.rejectValue(fieldName, "error.fieldRequired", new Object[] {new DefaultMessageSourceResolvable(new String[] {fieldCode})}, "\""+fieldName+"\" is required.");
			return; 
		}
		if (propertyValue <= 0) {
			errors.rejectValue(fieldName, "error.positiveNumber", new Object[] {new DefaultMessageSourceResolvable(new String[] {fieldCode})}, "\""+fieldName+"\" must be a positive number.");
		}
	}
	
	// should be "rejectEmptyCollection"
	public static void rejectEmptyTable(String tablePropertyName, String fieldCode, Errors errors) {
		errors.rejectValue(tablePropertyName, "error.tableEmpty", 
				new Object[] {new DefaultMessageSourceResolvable(new String[] {fieldCode})}, "\""+fieldCode+"\": this table cannot be empty");
	}
	
	// should be "rejectCollectionIfItemFieldsMissing"
	@SuppressWarnings("rawtypes")
	public static void rejectTableIfFieldsMissing(String tableName, String fieldCode, Set set, String[] fields, Errors errors) {
		boolean missingData=false;
		Iterator i = set.iterator();
		while (!missingData && i.hasNext()) {
			Object o = i.next();
			for (String field : fields) {
				try { if (PropertyUtils.getProperty(o, field)==null) { missingData=true; break; } }
				catch (Exception e) {;}
			}
		}
		if (missingData) {
			errors.rejectValue(tableName, "error.tableMissingData",
					new Object[] {new DefaultMessageSourceResolvable(new String[] {fieldCode})}, 
					"\""+fieldCode+"\": Required fields in this table are missing.  This may be because the data was imported from a previous version of RuralInvest.");
		}	
	}
	
	public static void rejectBlockEmptyTable(String blockName, String tableName, String tableCode, String errorCode, Errors errors) {
		errors.reject(errorCode, 
			new Object[] {blockName, new DefaultMessageSourceResolvable(new String[] {tableCode})}, "\""+tableName+"\": this table cannot be empty");
	}
}
