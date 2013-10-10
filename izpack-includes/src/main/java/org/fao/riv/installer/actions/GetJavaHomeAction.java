package org.fao.riv.installer.actions;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.data.PanelActionConfiguration;
import com.izforge.izpack.api.handler.AbstractUIHandler;
import com.izforge.izpack.data.PanelAction;

public class GetJavaHomeAction implements PanelAction {
	
	@Override
	public void executeAction(InstallData iData, AbstractUIHandler uih) {
		String javaHome=System.getProperty("java.home");
		javaHome.substring(0, javaHome.lastIndexOf("\\"));
		iData.setVariable("java_home", javaHome);
	}

	@Override
	public void initialize(PanelActionConfiguration arg0) {
		// TODO Auto-generated method stub
	}
	
}