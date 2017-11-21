package org.fao.riv.installer.actions;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.data.PanelActionConfiguration;
import com.izforge.izpack.api.handler.AbstractUIHandler;
import com.izforge.izpack.data.PanelAction;
import com.izforge.izpack.util.Platform;

public class GetJavaHomeAction implements PanelAction {
	
	@Override
	public void executeAction(InstallData iData, AbstractUIHandler uih) {
		String javaHome=System.getProperty("java.home");
		if (iData.getPlatform().getName().equals(Platform.Name.WINDOWS)) {
			// TODO this should only be executed on Windows, however it's redundant because
			// doesn't assign the substring to any String.
			javaHome.substring(0, javaHome.lastIndexOf("\\"));
		}
		iData.setVariable("java_home", javaHome);
	}

	@Override
	public void initialize(PanelActionConfiguration arg0) {
	}
	
}