package org.fao.riv.installer.actions;

import org.fao.riv.installer.util.WindowsService;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.data.PanelActionConfiguration;
import com.izforge.izpack.api.handler.AbstractUIHandler;
import com.izforge.izpack.data.PanelAction;

public class StopWindowsServiceAction implements PanelAction {
	
	@Override
	public void executeAction(InstallData iData, AbstractUIHandler uih) {
		 if (System.getProperty("os.name").contains("Windows")) {
				 if (iData.getVariable("riv4service").equals("true")) {
			 
				String service = "RuralInvest4"; 
				if (WindowsService.isServiceActive(service)) {
					System.out.println("Stopping service "+service);
					WindowsService.stopService(service);
				} else 
					System.out.println("No need to stop service "+service+". The service is not running.");
			}
		 }
	}

	@Override
	public void initialize(PanelActionConfiguration arg0) { }
	
}