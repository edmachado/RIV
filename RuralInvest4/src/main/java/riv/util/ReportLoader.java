package riv.util;

import java.io.File;

import javax.servlet.ServletContext;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class ReportLoader {
	static final Logger LOG = LoggerFactory.getLogger(ReportLoader.class);
	
	@Autowired
	ServletContext sc;
	
	@Cacheable("jrTemplates")
	public JasperReport compileReport(String template) {
		try {
			return (JasperReport)JRLoader.loadObject(new File(sc.getRealPath("/WEB-INF/classes"+template)));
		} catch (JRException e) {
			LOG.error("Error getting jasper report file.",e);
			throw new RuntimeException("Error getting jasper report file.",e); 
		}
	}
}
