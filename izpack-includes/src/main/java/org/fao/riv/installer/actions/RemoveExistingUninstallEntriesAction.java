package org.fao.riv.installer.actions;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.data.PanelActionConfiguration;
import com.izforge.izpack.api.handler.AbstractUIHandler;
import com.izforge.izpack.data.PanelAction;

public class RemoveExistingUninstallEntriesAction implements PanelAction {
	
	@Override
	public void executeAction(InstallData iData, AbstractUIHandler uih) {
		// if (windows) {
		//  look in registry Computer/HKLM/Software/Microsoft/Windows/CurrentVersion/Uninstall
		//  for (each subkey) {
		//       if (displayname.beginsWith("RuralInvest") {
		// 			remove key
		//		 }
		//    }
		// }
	}

	@Override
	public void initialize(PanelActionConfiguration pac) {	}
}