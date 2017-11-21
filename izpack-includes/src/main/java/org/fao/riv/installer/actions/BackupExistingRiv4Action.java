package org.fao.riv.installer.actions;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.data.PanelActionConfiguration;
import com.izforge.izpack.api.handler.AbstractUIHandler;
import com.izforge.izpack.data.PanelAction;

public class BackupExistingRiv4Action implements PanelAction {
	
	@Override
	public void executeAction(InstallData iData, AbstractUIHandler uih) {
		
		Calendar cal = Calendar.getInstance();
		String installPath = iData.getVariable("INSTALL_PATH");
		File oldPath = new File(installPath+"/webapp/WEB-INF/data");
		File newPath = new File(installPath+"/previous/"+
				cal.get(Calendar.YEAR)+cal.get(Calendar.MONTH)+cal.get(Calendar.DATE)+cal.get(Calendar.HOUR_OF_DAY)+cal.get(Calendar.MINUTE)+cal.get(Calendar.SECOND));
	
		try {
			if (oldPath.exists())  {
				System.out.println("Backup existing RIV4");
				newPath.mkdirs();
				System.out.println("Copying from " + oldPath + "  to " + newPath);
				FileUtils.copyDirectory(oldPath, newPath);
				iData.setVariable("MIGRATED_DB", newPath.toString());
			} else {
				System.out.println("no "+oldPath + " to copy.");
			}
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
	}


	@Override
	public void initialize(PanelActionConfiguration pac) {
	}
	
}