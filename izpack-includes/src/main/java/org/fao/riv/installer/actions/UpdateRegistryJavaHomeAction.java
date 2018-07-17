package org.fao.riv.installer.actions;

import org.fao.riv.installer.util.WinRegistry;

import com.izforge.izpack.panels.process.AbstractUIProcessHandler;

public class UpdateRegistryJavaHomeAction  {

    private static final String KEYPATH = "Software\\Apache Software Foundation\\Procrun 2.0\\RuralInvest4\\Parameters\\Java";

    public void run (AbstractUIProcessHandler uih, String[] args) {
        String jvm = args[0]+"\\bin\\client\\jvm.dll";
        uih.logOutput("Updating Java path.", false);
        
        try {
        	WinRegistry.writeStringValue(WinRegistry.HKEY_LOCAL_MACHINE, KEYPATH, "Jvm", jvm);
        } catch (Exception e) {
        	System.out.println(e);
            e.printStackTrace();
        }
    }

}