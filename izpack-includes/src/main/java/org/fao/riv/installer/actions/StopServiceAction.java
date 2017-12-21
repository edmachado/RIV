package org.fao.riv.installer.actions;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.data.PanelActionConfiguration;
import com.izforge.izpack.api.handler.AbstractUIHandler;
import com.izforge.izpack.data.PanelAction;

import org.fao.riv.installer.OS;
import org.fao.riv.installer.util.WindowsService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class StopServiceAction implements PanelAction {

    @Override
    public void executeAction(InstallData iData, AbstractUIHandler uih) {
        if (OS.isWindows()) {
            if (iData.getVariable("riv4service").equals("true")) {

                String service = "RuralInvest4";
                if (WindowsService.isServiceActive(service)) {
                    System.out.println("Stopping service " + service);
                    WindowsService.stopService(service);
                } else {
                    System.out.println(
                            "No need to stop service " + service + ". The service is not running.");
                }
            }
        } else if (OS.isMac()) {
            if (iData.getVariable("riv4service").equals("true")) {
                try {
                    Runtime.getRuntime().exec("launchctl unload /Library/LaunchDaemons/org.fao.riv.plist");
                } catch (IOException e) {
                    e.printStackTrace(System.out);
                }
            }
        }
        try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			e.printStackTrace(System.out);
		}
    }

    @Override
    public void initialize(PanelActionConfiguration arg0) {
    }


}