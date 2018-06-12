package riv.util.validators;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.Size;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.Errors;

import riv.objects.OrderByable;

/**
 * @author barzecharya
 *
 */
public class ValidateUtils {
	static final Logger LOG = LoggerFactory.getLogger(ValidateUtils.class);
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
				if (size!=null) {
					length = size.max();
				}
			} catch (NoSuchFieldException ex2) {
				LOG.trace("Field not found.", ex2.getMessage());
			}	
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
					if (propertyValue.length()>maxLength) {
						if (noResource) {
							errors.rejectValue(fieldName, "error.maxLength", new Object[] {fieldCode,String.valueOf(maxLength)}, "\""+fieldCode+"\" is required");
						} else {
							errors.rejectValue(fieldName, "error.maxLength", new Object[] {new DefaultMessageSourceResolvable(new String[] {fieldCode}),String.valueOf(maxLength)}, "\""+fieldName+"\" is required");
						}
					}
				
				} catch (NoSuchMethodException ex2) { LOG.trace(ex2.getMessage());
				} catch (IllegalAccessException ex2) { LOG.trace(ex2.getMessage());
				} catch (InvocationTargetException ex2) { LOG.trace(ex2.getMessage());
				} catch (NullPointerException ex2) { LOG.trace(ex2.getMessage()); }
			}
		}
	}
	
	public static void rejectIfEmpty(Object bean, String fieldName, String fieldCode, Errors errors) {
		Object propertyValue=null;
		try {
			propertyValue=PropertyUtils.getProperty(bean, fieldName);
		} catch (NoSuchMethodException ex2) { LOG.trace(ex2.getMessage());
		} catch (IllegalAccessException ex2) { LOG.trace(ex2.getMessage());
		} catch (InvocationTargetException ex2) { LOG.trace(ex2.getMessage());
		} catch (NullPointerException ex2) { LOG.trace(ex2.getMessage()); }
		
		if (propertyValue == null || propertyValue.equals("")) {
			errors.rejectValue(fieldName, "error.fieldRequired", new Object[] {new DefaultMessageSourceResolvable(new String[] {fieldCode})}, "\""+fieldName+"\" is required");
		}
		enforceLength(bean, fieldName, fieldCode, errors);
	}
	
	public static void rejectIfEmptyNoResource(Object bean, String fieldName, String fieldDesc, Errors errors) {
		String propertyValue=null;
		try {
			propertyValue = PropertyUtils.getProperty(bean, fieldName).toString();
		} catch (NoSuchMethodException ex2) { LOG.trace(ex2.getMessage());
		} catch (IllegalAccessException ex2) { LOG.trace(ex2.getMessage());
		} catch (InvocationTargetException ex2) { LOG.trace(ex2.getMessage());
		} catch (NullPointerException ex2) { LOG.trace(ex2.getMessage()); }
		
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
		Double propertyValue=null;
		try {
			propertyValue=Double.parseDouble(PropertyUtils.getProperty(bean, fieldName).toString());
		} catch (NoSuchMethodException ex2) { LOG.trace(ex2.getMessage());
		} catch (IllegalAccessException ex2) { LOG.trace(ex2.getMessage());
		} catch (InvocationTargetException ex2) { LOG.trace(ex2.getMessage());
		} catch (NullPointerException ex2) { LOG.trace(ex2.getMessage()); }
		
		rejectIfNegativeFromValue(propertyValue, bean, fieldName, fieldCode, errors, scale);
	}
	public static void rejectIfNegativeFromValue(Double property, Object bean, String fieldName, String fieldCode, Errors errors) {
		rejectIfNegativeFromValue(property, bean, fieldName, fieldCode, errors, 4); // default scale of 4
	}
	public static void rejectIfNegativeFromValue(Double property, Object bean, String fieldName, String fieldCode, Errors errors, int scale) {
		if (property==null)  {
			errors.rejectValue(fieldName, "error.fieldRequired", new Object[] {new DefaultMessageSourceResolvable(new String[] {fieldCode})}, "\""+fieldName+"\" is required");
		} else if (round(property, scale) < 0 ) { 
			errors.rejectValue(fieldName, "error.requiredNonNegative", new Object[] {new DefaultMessageSourceResolvable(new String[] {fieldCode})}, "\""+fieldName+"\" must be non-negative");
		}
	}

	private static double round(double d, int scale) {
		 BigDecimal bd = new BigDecimal(Double.toString(d));
		    bd = bd.setScale(scale,BigDecimal.ROUND_HALF_UP);
		    return bd.doubleValue();
	 }
	
	public static void rejectIfEmptyOrNegativeInSet(OrderByable bean, String setName, String fieldName, String fieldCode, Errors errors) {
		String errorField = setName+"["+bean.getOrderBy()+"]."+fieldName;
		double propertyValue=0;
		try {
			propertyValue=Double.parseDouble(PropertyUtils.getProperty(bean, fieldName).toString());
		} catch (Exception e) {	
			errors.rejectValue(errorField, "error.fieldRequired", new Object[] {new DefaultMessageSourceResolvable(new String[] {fieldCode})}, "\""+fieldName+"\" is required");
			return;
		}
		if (propertyValue < 0) {
			errors.rejectValue(errorField, "error.requiredNonNegative", new Object[] {new DefaultMessageSourceResolvable(new String[] {fieldCode})}, "\""+fieldName+"\" must be non-negative");
		}
	}
	
	public static boolean rejectChildValueIfEmptyOrNegativeInSet(Object bean, int childKey, String fieldName, String fieldCode, String parentFieldName, Errors errors) {
		double propertyValue;
		try {
			propertyValue=Double.parseDouble(PropertyUtils.getProperty(bean, fieldName).toString());
		} catch (Exception e) {
			errors.rejectValue(parentFieldName, "error.fieldRequired", new Object[] {new DefaultMessageSourceResolvable(fieldCode)}, "\""+fieldName+"\" is required");
			return true;
		}
		if (propertyValue < 0) {
			errors.rejectValue(parentFieldName, "error.requiredNonNegative", new Object[] {new DefaultMessageSourceResolvable(fieldCode)}, "\""+fieldName+"\" cannot be negative");
		}
		
		return false;
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
	
	public static boolean rejectChildValueIfEmptyOrNegative(Object bean, int childKey, String fieldName, String fieldCode, String parentFieldName, Errors errors) {
		double propertyValue;
		try {
			propertyValue=Double.parseDouble(PropertyUtils.getProperty(bean, fieldName).toString());
		} catch (Exception e) {
			errors.rejectValue(parentFieldName, "error.perYearCost.fieldRequired", new Object[] {childKey+1, new DefaultMessageSourceResolvable(fieldCode)}, "\""+fieldName+"\" is required");
			return true;
		}
		if (propertyValue < 0) {
			errors.rejectValue("years", "error.perYearCost.requiredNonNegative", new Object[] {(childKey+1), new DefaultMessageSourceResolvable(fieldCode)}, "\""+fieldName+"\" cannot be negative");
		}
		
		return false;
	}
	
	public static boolean rejectMapValueIfEmptyOrNegative(@SuppressWarnings("rawtypes") Map map, String mapName, Object key, String fieldName, Errors errors) {
		double propertyValue=0;
		try {
			propertyValue=Double.parseDouble(map.get(key).toString());
		} catch (Exception e) {
			errors.rejectValue(mapName+"["+key.toString()+"]", "error.fieldRequired", new Object[] {fieldName}, "\""+fieldName+"\" is required");
			return true;
		}
		if (propertyValue < 0) {
			errors.rejectValue(mapName+"["+key.toString()+"]", "error.requiredNonNegative", new Object[] {fieldName}, "\""+fieldName+"\" must be non-negative");
			return true;
		}
		return false;
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
				catch (Exception e) { LOG.trace(e.getMessage());;}
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
