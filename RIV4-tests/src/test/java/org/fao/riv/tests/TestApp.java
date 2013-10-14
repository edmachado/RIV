package org.fao.riv.tests;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import org.fao.riv.tests.dataentry.DataEntrySuite;
import org.fao.riv.tests.download.DownloadSuite;
import org.fao.riv.tests.exceldownload.ExcelDownloadSuite;
import org.fao.riv.tests.excelimport.ExcelImportSuite;
import org.fao.riv.tests.importfile.ImportSuite;

@RunWith(Suite.class)
@SuiteClasses({ ImportSuite.class, DataEntrySuite.class, ExcelDownloadSuite.class, ExcelImportSuite.class, DownloadSuite.class }) //  ,  CalculationSuite.class })
public class TestApp {
	public static String baseUrl;//"http://apps3.fao.org/riv-qa";//"http://172.16.110.128:8085/RuralInvest";//
	public static String username="initialUser";
	public static String password="initialPassword";
	public static String buildLang="qa";
	private static boolean ownTomcat=false;

	private static Tomcat tomcat;
	private static String basePath;
	private static String webappPath;
	
	public static String appURL="http://localhost:8080/RuralInvest";
	
	
    @BeforeClass
    public static void setupWebserver() throws LifecycleException {
    	System.out.println("Getting parameters");
    	baseUrl = System.getProperty("base-url") != null ? System.getProperty("base-url") : baseUrl;
    	username = System.getProperty("username") != null ? System.getProperty("username") : "initialUser";
    	password = System.getProperty("password") != null ? System.getProperty("password") : "initialPassword";
    	ownTomcat = System.getProperty("own-tomcat") != null ? Boolean.parseBoolean(System.getProperty("own-tomcat")) : ownTomcat;
    	buildLang = System.getProperty("build-lang") != null ? System.getProperty("build-lang") : buildLang;
    	System.out.println(baseUrl);
    	
    	System.out.println("Setting up tomcat and war");
    	String relPath = TestApp.class.getProtectionDomain().getCodeSource().getLocation().getFile();
    	basePath = new File(new File(relPath).getParent()).getParent();
    	if (ownTomcat) {
	    	setupWar();
	   	 	setupTomcat();
	   	 	tomcat.start();
	   	 	appURL = "http://localhost:"+tomcat.getConnector().getLocalPort()+"/RuralInvest";
        } else {
        	appURL = TestApp.baseUrl;
        }
		System.out.println("App URL: "+appURL);
    }
    
    @AfterClass
    public static void teardownWebserver() {
    	 if (ownTomcat) {
			 System.out.println("Shutting down tomcat.");
		        try {
		        	if (tomcat.getServer()!=null && tomcat.getServer().getState() != LifecycleState.DESTROYED) {
		        		if (tomcat.getServer().getState() != LifecycleState.STOPPED) {
		        			tomcat.stop();
		        		}
		        		tomcat.destroy();
		        	}
		        	FileUtils.deleteDirectory(new File(webappPath));
		    	} catch (LifecycleException e) {
		    		e.printStackTrace(System.out);
		    	} catch (IOException e) {
					e.printStackTrace(System.out);
				} 
	        }
    }

    private static void setupDb() {
    	// copy data-qa to data
		try {
			File targetData = new File(webappPath+"/WEB-INF/data");
			FileUtils.deleteDirectory(targetData);
			File dataDir = new File(new File(basePath).getParent()+"/RuralInvest4/src/main/appdata/data-"+buildLang);
	    	FileUtils.copyDirectory(dataDir, targetData);
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}	
    }
    
    private static void setupWar() {
		String sourceWarPath = new File(basePath).getParent()+"/RuralInvest4"+"/target/riv-"+buildLang+"/";
		webappPath = System.getProperty("java.io.tmpdir");
		try {
			// copy war
			FileUtils.deleteDirectory(new File(webappPath));
			FileUtils.copyDirectory(new File(sourceWarPath), new File(webappPath));
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
		setupDb();
	 }
    
    private static void setupTomcat() {
    	tomcat = new Tomcat();

        try {
			//System.out.println("basepath="+basePath);
			System.out.println("webapp location="+webappPath);
			
			tomcat.setPort(0);
			tomcat.getConnector().setURIEncoding("UTF-8");
			tomcat.setBaseDir(webappPath);
			tomcat.addWebapp("/RuralInvest", webappPath);
			tomcat.getHost().setAutoDeploy(true);
			tomcat.getHost().setDeployOnStartup(true);
		} catch (ServletException e) {
			e.printStackTrace(System.out);
		}
    }
}