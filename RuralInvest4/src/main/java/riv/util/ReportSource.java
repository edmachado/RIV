package riv.util;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings("rawtypes")
public class ReportSource implements JRRewindableDataSource {
	static final Logger LOG = LoggerFactory.getLogger(ReportSource.class);
	
	protected HashMap fieldsToIdxMap=new HashMap();
	protected Iterator iterator;
	protected Object currentValue;
	Collection collection;
	Map map;
	
	public ReportSource(Collection list) {
		this.collection=list;
		this.iterator = list.iterator();
	}
	public ReportSource(Map list) {
		this.map=list;
		this.iterator = list.values().iterator();
	}
	
	private Object nestedFieldValue(Object object, String field) {
		Object value = null;
		// MAP VALUES
		if (field.indexOf("map__")>-1) {
			try {
				Method getter = PropertyUtils.getReadMethod(PropertyUtils.getPropertyDescriptor(object, field.substring(5, field.length())));
				value = getter.invoke(object, (Object[])null);
			} catch (Exception ex) {
				LOG.warn("map "+field+" does not exist.", ex);
			}
		} else if (field.indexOf("__")>-1) { // NESTED VALUES
			try {
				Method nestedGetter = PropertyUtils.getReadMethod(PropertyUtils.getPropertyDescriptor(object, field.substring(0,field.indexOf("__"))));
				Object nestedObject = nestedGetter.invoke(object, (Object[])null);
				
				value = nestedFieldValue(nestedObject, field.substring(field.indexOf("__")+2, field.length()));
			} catch (Exception ex) {
				LOG.warn("nested field "+field+" does not exist.", ex);
			}
		} else { // OTHER VALUES
			try {
				Method getter = PropertyUtils.getReadMethod(PropertyUtils.getPropertyDescriptor(object, field));
				value = getter.invoke(object, (Object[])null);

				if(Collection.class.isAssignableFrom(getter.getReturnType())) {
					return new ReportSource((Collection)value);
				}
				if (Map.class.isAssignableFrom(getter.getReturnType())) {
					return new ReportSource((Map)value);
				}
			} catch (Exception ex) {
				LOG.trace("property "+field+" does not exist.", ex);
			}
		}
		if (value instanceof String) {
			return convertHtmlToUnicode((String)value);
		} else {
			return value;
		}
	}
	
	public Object getCurrentValue() throws JRException {
		return currentValue;
	}
	public boolean next() throws JRException {
		currentValue = iterator.hasNext() ? iterator.next() : null;
		return currentValue != null;
	}
	public Object callMoveFirst() throws JRException {
		this.moveFirst();
		return null;
	}
	public void moveFirst() throws JRException {
		if (collection!=null) {
			this.iterator = collection.iterator();
		} else if (map!=null) {
			this.iterator=map.values().iterator();
		}
	}

	public Object getFieldValue(JRField field) throws JRException {
		return nestedFieldValue(currentValue, field.getName());
	}
	
	private String convertHtmlToUnicode(String html) {
		int begU = html.indexOf("&#");
		if (begU==-1) {
			return html;
		} else {
			String before = html.substring(0,begU);
			int endU=html.indexOf(";", begU);
			char uni = (char)Integer.parseInt(html.substring(begU+2,endU));
			String after = html.substring(endU+1, html.length());
			return before+uni+convertHtmlToUnicode(after);
		}
	}
	

}
