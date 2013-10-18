package org.fao.riv.installer.actions;

import java.io.IOException;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.data.PanelActionConfiguration;
import com.izforge.izpack.api.handler.AbstractUIHandler;
import com.izforge.izpack.data.PanelAction;

public class LaunchUrlAction implements PanelAction {
	
	@Override
	public void executeAction(InstallData iData, AbstractUIHandler uih) {
		String urlPath =  "\""+iData.getVariable("INSTALL_PATH")+"/service/launchUrl.bat\"";
		System.out.println("launching "+urlPath);
		try {
			Runtime.getRuntime().exec(urlPath);
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
	}

	@Override
	public void initialize(PanelActionConfiguration arg0) { }
	
}