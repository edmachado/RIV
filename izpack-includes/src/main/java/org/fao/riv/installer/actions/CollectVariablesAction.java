package org.fao.riv.installer.actions;

import java.io.File;
import java.net.URISyntaxException;

import org.fao.riv.installer.OS;
import org.fao.riv.installer.util.WindowsService;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.data.PanelActionConfiguration;
import com.izforge.izpack.api.handler.AbstractUIHandler;
import com.izforge.izpack.data.PanelAction;

/*
 * In theory this data collection should be done with conditions in the installer description.
 * But the behavior is not correct, so we're doing it here.
 */
public class CollectVariablesAction implements PanelAction {
	
	@Override
	public void executeAction(InstallData iData, AbstractUIHandler uih) {
		iData.setVariable("RIV3_INSTALL_PATH", iData.getVariable("DEFAULT_INSTALL_PATH"));
		
		// check if services exist
		if (OS.isWindows()) {
			iData.setVariable("riv3service", String.valueOf(WindowsService.riv3Exists));
			iData.setVariable("riv4service", String.valueOf(WindowsService.riv4Exists));
            // check if databases exist
            String riv3path = iData.getVariable("RIV3_INSTALL_PATH")+"/jakarta-tomcat/webapps/ROOT/WEB-INF/data/riv.data";
            String riv3installed = String.valueOf(new File(riv3path).exists());
            System.out.println("looking for: "+riv3path);
            iData.setVariable("riv3installed", riv3installed);
            System.out.println("riv3installed:"+riv3installed);

            String riv4path = iData.getVariable("DEFAULT_INSTALL_PATH")+"4/webapp/WEB-INF/data/riv.data";
            String riv4installed = String.valueOf(new File(riv4path).exists());
            System.out.println("looking for: "+riv4path);
            iData.setVariable("riv4installed", riv4installed);
            System.out.println("riv4installed:"+riv4installed);

        } else if (OS.isMac()){
			iData.setVariable("riv3service", String.valueOf(false));
            iData.setVariable("riv3installed", String.valueOf(false));

            String rivDaemon = "/Library/LaunchDaemons/org.fao.riv.plist";
            iData.setVariable("riv4service", String.valueOf(new File(rivDaemon).exists()));

            String rivData = "/Library/RuralInvest/webapp/WEB-INF/data/riv.data";
            String riv4installed = String.valueOf(new File(rivDaemon).exists());
            System.out.println("looking for: "+rivData);
            iData.setVariable("riv4installed", riv4installed);
            System.out.println("riv4installed:"+riv4installed);
		}
		System.out.println("riv3service:"+iData.getVariable("riv3service"));
		System.out.println("riv4service:"+iData.getVariable("riv4service"));
		
		// check if admin version
		String jarBase=null;
		try {
			jarBase = getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		if (jarBase!=null) {
			System.out.println("jarBase: "+jarBase);
//			windows jar is in main folder, macos in /lib
			String subPath = OS.isWindows() ? "/lib/av.riv" : "/av.riv";
			String isAdmin=String.valueOf(new File(new File(jarBase).getParent()+subPath).exists());
			System.out.println("isAdmin:"+isAdmin);
			iData.setVariable("IS_ADMIN", isAdmin);
		}
	}

	@Override
	public void initialize(PanelActionConfiguration arg0) {
	}
	
}