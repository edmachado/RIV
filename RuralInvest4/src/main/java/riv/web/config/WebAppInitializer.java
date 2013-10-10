package riv.web.config;

import java.io.InputStream;
import java.util.EnumSet;
import java.util.Properties;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.WebAppRootListener;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

import ch.qos.logback.ext.spring.web.LogbackConfigListener;

import com.opensymphony.module.sitemesh.filter.PageFilter;

@SuppressWarnings("deprecation")
public class WebAppInitializer implements WebApplicationInitializer {
	static final Logger LOG = LoggerFactory.getLogger(WebAppInitializer.class);
	
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		// Create the root appcontext
		   AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		   rootContext.register(AppConfig.class);

		   servletContext.addListener(new WebAppRootListener());
		   servletContext.setInitParameter("webAppRootKey", getRootKey());
		   servletContext.setInitParameter("logbackConfigLocation", "/WEB-INF/classes/logging.xml");
		   LogbackConfigListener logbacklistener = new LogbackConfigListener();
		   servletContext.addListener(logbacklistener); 
		   servletContext.addListener(new DriverCleanup());
		   
		   
		   // Manage the lifecycle of the root appcontext
		   servletContext.addListener(new ContextLoaderListener(rootContext));
		   //servletContext.setInitParameter("defaultHtmlEscape", "true");
		   servletContext.addListener(new DriverCleanup());
		      
		// The main Spring MVC servlet.
		   ServletRegistration.Dynamic springapp = servletContext.addServlet(
		      "springapp", new DispatcherServlet(rootContext));
				springapp.setLoadOnStartup(1);
		   Set<String> mappingConflicts = springapp.addMapping("/");
		   
		   if (!mappingConflicts.isEmpty()) {
			      for (String s : mappingConflicts) {
			        LOG.warn("Mapping conflict: " + s);
			      }
			      throw new IllegalStateException("'springapp' cannot be mapped to '/' under Tomcat versions <= 7.0.14");
			   }
		   
		  FilterRegistration.Dynamic urlRewrite = servletContext.addFilter("UrlRewriteFilter", new UrlRewriteFilter());
		   			urlRewrite.setInitParameter("confPath","/WEB-INF/urlrewrite.xml");
		   			urlRewrite.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
		   
		   
		   FilterRegistration.Dynamic fr = servletContext.addFilter("encodingFilter",  
				      new CharacterEncodingFilter());
				   fr.setInitParameter("encoding", "UTF-8");
				   fr.setInitParameter("forceEncoding", "true");
				   fr.addMappingForUrlPatterns(null, true, "/*");
				   
			FilterRegistration.Dynamic secFilter = servletContext.addFilter("securityFilter", new DelegatingFilterProxy());
				   secFilter.setInitParameter("targetBeanName", "springSecurityFilterChain");
				   secFilter.addMappingForUrlPatterns(null, true, "/*");
				   
			FilterRegistration.Dynamic rivFilter = servletContext.addFilter("RivInjectionFilter", new DelegatingFilterProxy());
			rivFilter.setInitParameter("targetBeanName", "rivInjectFilter");
			rivFilter.addMappingForUrlPatterns(null, true, "/config/*");
			rivFilter.addMappingForUrlPatterns(null, true, "/profile/*");
			rivFilter.addMappingForUrlPatterns(null, true, "/project/*");
			rivFilter.addMappingForUrlPatterns(null, true, "/search/*");
			rivFilter.addMappingForUrlPatterns(null, true, "/report/*");
			rivFilter.addMappingForUrlPatterns(null, true, "/home");
			rivFilter.addMappingForUrlPatterns(null, true, "/help/*");
			rivFilter.addMappingForUrlPatterns(null, true, "/import/*");
			
			FilterRegistration.Dynamic siteMeshFilter = servletContext.addFilter("sitemesh", new PageFilter());
			siteMeshFilter.addMappingForUrlPatterns(null, true, "/config/*");
			siteMeshFilter.addMappingForUrlPatterns(null, true, "/profile/*");
			siteMeshFilter.addMappingForUrlPatterns(null, true, "/project/*");
			siteMeshFilter.addMappingForUrlPatterns(null, true, "/search/*");
			siteMeshFilter.addMappingForUrlPatterns(null, true, "/help/*");
	}
	
	private String getRootKey() {
		Properties prop = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();           
		InputStream stream = loader.getResourceAsStream("application.properties");
		String key=null;
		try {
			prop.load(stream);
			key = prop.getProperty("rootKey");
			stream.close();
		} catch  (Exception e) {
			throw new RuntimeException("Cannot load webapprootkey", e);
		}
		return key;
	}
}
