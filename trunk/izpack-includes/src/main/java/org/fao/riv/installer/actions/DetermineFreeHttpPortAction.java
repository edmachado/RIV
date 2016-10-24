package org.fao.riv.installer.actions;

import org.fao.riv.installer.util.HttpPorts;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.data.PanelActionConfiguration;
import com.izforge.izpack.api.handler.AbstractUIHandler;
import com.izforge.izpack.data.PanelAction;

public class DetermineFreeHttpPortAction implements PanelAction {
	public final static int START_PORT = 8085;
	public final static int END_PORT = 8090;
	
	@Override
	public void executeAction(InstallData iData, AbstractUIHandler uih) {
		// only necessary if service not yet installed
		if (iData.getVariable("riv4service").equals("false")) {
			String firstAvailablePort = HttpPorts.availablePorts(START_PORT, END_PORT).get(0).toString();
			iData.setVariable("HTTP_PORT_NO", firstAvailablePort);
		}
	}

	@Override
	public void initialize(PanelActionConfiguration pac) {	}
}