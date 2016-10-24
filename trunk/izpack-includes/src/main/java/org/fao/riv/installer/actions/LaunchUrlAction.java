package org.fao.riv.installer.actions;

import java.io.IOException;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.data.PanelActionConfiguration;
import com.izforge.izpack.api.handler.AbstractUIHandler;
import com.izforge.izpack.data.PanelAction;
import com.izforge.izpack.util.Platform;

public class LaunchUrlAction implements PanelAction {
	
	@Override
	public void executeAction(InstallData iData, AbstractUIHandler uih) {
		String urlPath = getPath(iData);
		if (urlPath != null) {
			System.out.println("launching " + urlPath);
			try {
				Runtime.getRuntime().exec(urlPath);
			} catch (IOException e) {
				e.printStackTrace(System.out);
			}
		}
	}

	private String getPath(InstallData iData) {
		String path = null;
		if (iData.getPlatform().getName().equals(Platform.Name.WINDOWS)) {
			path = "\"" + iData.getVariable("INSTALL_PATH") + "/service/launchUrl.bat\"";
		} else if (iData.getPlatform().getName().equals(Platform.Name.MAC_OSX)) {
            path = iData.getVariable("INSTALL_PATH") + "/bin/riv.sh";
		}
		return path;
	}

	@Override
	public void initialize(PanelActionConfiguration arg0) { }
	
}