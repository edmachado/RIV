package org.fao.riv.installer.actions;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.data.PanelActionConfiguration;
import com.izforge.izpack.api.handler.AbstractUIHandler;
import com.izforge.izpack.data.PanelAction;

public class BackupRiv3Action implements PanelAction {
	String installPath;
	String riv3Path; 
	final String oldService="RuralInvest";
	
	@Override
	public void executeAction(InstallData iData, AbstractUIHandler uih) {
		if (iData.getRules().isConditionTrue("dbExists-old")) {
			System.out.println("Migrating from RIV3");
			// setup variables
			installPath = iData.getVariable("INSTALL_PATH");
			riv3Path = iData.getVariable("RIV3_INSTALL_PATH");
			
			// stop old service
//			System.out.println("Stopping "+oldService+" service.");
			
			// backup old database
			File oldPath = new File(riv3Path+"/jakarta-tomcat/webapps/ROOT/WEB-INF/data");
			File newPath = new File(installPath+"/previous/riv3");
			System.out.println("Backup RIV3 database");
			newPath.mkdirs();
			System.out.println("Copying from " + oldPath + "  to " + newPath);
			try {
				FileUtils.copyDirectory(oldPath, newPath);
				new File(installPath+"/previous/riv3/riv.lck").delete();
			} catch (IOException e) {
				e.printStackTrace(System.out);
			}
			
			// copy orgLogo file (to be then moved to db)
			oldPath = new File(riv3Path+"/jakarta-tomcat/webapps/ROOT/img/userLogo");
			System.out.println("Copying from " + oldPath + "  to " + newPath);
			try {
				FileUtils.copyFileToDirectory(oldPath, newPath);
			} catch (IOException e) {
				e.printStackTrace(System.out);
			}
			
			iData.setVariable("MIGRATED_DB", newPath.toString());
		} else {
			System.out.println("not migrating from RIV3");
		}
	}

	@Override
	public void initialize(PanelActionConfiguration pac) { }
	
}