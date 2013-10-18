package riv.web.config;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DriverCleanup implements javax.servlet.ServletContextListener { // On application shutdown 
	static final Logger LOG = LoggerFactory.getLogger(DriverCleanup.class);
	
	public void contextDestroyed(ServletContextEvent event) { 
		Enumeration<Driver> drivers = DriverManager.getDrivers(); 
		while (drivers.hasMoreElements()) { 
			Driver driver = drivers.nextElement(); 
			// We search for driver that was loaded by this web application 
			if (driver.getClass().getClassLoader() == this.getClass().getClassLoader()) { 
				try { 
					DriverManager.deregisterDriver(driver);
	                LOG.info(String.format("deregistering jdbc driver: %s", driver)); 
				} catch (SQLException e) { 
	                LOG.error(String.format("Error deregistering driver %s", driver), e);
				} 
			} 
		} 
	} 
	
	public void contextInitialized(ServletContextEvent event) { // Nothing to do here 
	} 
}
