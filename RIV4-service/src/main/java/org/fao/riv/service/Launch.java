package org.fao.riv.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.net.URLDecoder;

import javax.servlet.ServletException;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.startup.Tomcat;

/**
 * @author Bar Zecharya
 */
public class Launch  {
	private static FileHandler logfile;
	private static Logger LOGGER;
	private static Tomcat tomcat;
	private static String basePath;
	private static int port;
	
    public static void main(String[] args) {
    	System.out.println("Starting...");
		if ("start".equals(args[0])) start(args);
		else if ("stop".equals(args[0])) stop(args);
		else start(new String[] {args[0]}); // so you can just pass port number
    }
    
    public static void stop(String[] args) {
    	System.out.println("Stopping...");
    	if (tomcat != null && tomcat.getServer() != null && tomcat.getServer().getState()!= LifecycleState.DESTROYED) {
			try {
				if (tomcat.getServer().getState() != LifecycleState.STOPPED)
					tomcat.stop();
				tomcat.destroy();
			} catch (LifecycleException e) {
				e.printStackTrace(System.out);
			}
    	}
    	unlockDb();
    }
    
    public static void start(String[] args) {
    	System.out.println("Starting...");
    	if (tomcat==null) {
	    	LOGGER = Logger.getLogger(Launch.class .getName());
	    	LOGGER.setLevel(Level.SEVERE);
	    	try {
				logfile = new FileHandler("service.log");
			} catch (Exception e1) {
				e1.printStackTrace(System.out);
			}
	    	logfile.setFormatter(new SimpleFormatter());
	    	LOGGER.addHandler(logfile);
	    	
	    	tomcat = new Tomcat();

			// set paths
        	basePath = new File(new File(Launch.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent()).getParent();
        	try {
				basePath = URLDecoder.decode(basePath, "utf-8");
			} catch (UnsupportedEncodingException e1) {
				LOGGER.warning("Error url decoding base path.");
			}
        	System.out.println("basepath="+basePath);
			String webappPath = basePath+System.getProperty("file.separator")+"webapp";
			
	        try {
				// get port
				getPort(args);
				tomcat.setPort(port);
				
				// start tomcat
				unlockDb();
				System.out.println("configuring app with basedir: " + webappPath.toString());
				tomcat.getConnector().setURIEncoding("UTF-8");
				Context myapp = tomcat.addWebapp("/RuralInvest", webappPath.toString());
				myapp.setTldValidation(false);
				tomcat.getHost().setAutoDeploy(true);
				tomcat.getHost().setDeployOnStartup(true);
				tomcat.start();
				tomcat.getServer().await();
			} catch (ServletException e) {
				// error adding webapp to tomcat
				LOGGER.severe("Error adding webapp to tomcat. "+e.getMessage());
				e.printStackTrace(System.out);
			} catch (LifecycleException e) {
				// Error when starting tomcat
				LOGGER.severe("Error when starting tomcat. "+e.getMessage());
				e.printStackTrace(System.out);
			}
    	}
    }
    
    private static void unlockDb() {
    	String lock = basePath+System.getProperty("file.separator")+"webapp"+System.getProperty("file.separator")+"WEB-INF"+System.getProperty("file.separator")+"data"+System.getProperty("file.separator")+"riv.lck";
    	LOGGER.info("Deleting "+lock);
    	File dbLck = new File(lock);
    	if (dbLck.exists()) {
    		dbLck.delete();
    	} else {
    		LOGGER.info("No lock file to delete.");
    	}
    }
    
    private static void getPort(String[] args)  {
    	String p = args[0].replace("-port", "").trim();
		System.out.println(p);
		port=Integer.parseInt(p);
    }
}
