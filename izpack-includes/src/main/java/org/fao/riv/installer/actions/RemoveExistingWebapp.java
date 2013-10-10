package org.fao.riv.installer.actions;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.data.PanelActionConfiguration;
import com.izforge.izpack.api.handler.AbstractUIHandler;
import com.izforge.izpack.data.PanelAction;

public class RemoveExistingWebapp implements PanelAction {
	
	@Override
	public void executeAction(InstallData iData, AbstractUIHandler uih) {
		File webapp = new File(iData.getVariable("INSTALL_PATH")+"/webapp");
		if (webapp.exists()) {
			System.out.println("Removing "+webapp.toString());
			boolean success = FileUtils.deleteQuietly(webapp);
			System.out.println("success: "+success);
		}
	}

	@Override
	public void initialize(PanelActionConfiguration pac) {	}
	
}