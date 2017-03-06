package org.fao.riv.installer.actions;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.data.PanelActionConfiguration;
import com.izforge.izpack.api.handler.AbstractUIHandler;
import com.izforge.izpack.data.PanelAction;
import com.izforge.izpack.util.Platform;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class RemoveExistingUninstallEntriesAction implements PanelAction {

    public static final int HKEY_LOCAL_MACHINE = 0x80000002;
    private static final String[] KEYS = {
            "Software\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall",
            "Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall"
    };

    public static final int REG_SUCCESS = 0;

    private static final int KEY_READ = 0x20019;
    private static Preferences userRoot = Preferences.userRoot();
    private static Preferences systemRoot = Preferences.systemRoot();
    private static Class<? extends Preferences> userClass = userRoot.getClass();
    private static Method regOpenKey = null;
    private static Method regCloseKey = null;
    private static Method regQueryInfoKey = null;
    private static Method regEnumKeyEx = null;
    private static Method regDeleteKey = null;

    static {
        try {
            regOpenKey = userClass.getDeclaredMethod("WindowsRegOpenKey",
                                                     new Class[]{int.class, byte[].class,
                                                                 int.class});
            regOpenKey.setAccessible(true);
            regCloseKey = userClass.getDeclaredMethod("WindowsRegCloseKey",
                                                      new Class[]{int.class});
            regCloseKey.setAccessible(true);
            regQueryInfoKey = userClass.getDeclaredMethod("WindowsRegQueryInfoKey1",
                                                          new Class[]{int.class});
            regQueryInfoKey.setAccessible(true);
            regEnumKeyEx = userClass.getDeclaredMethod(
                    "WindowsRegEnumKeyEx", new Class[]{int.class, int.class,
                                                       int.class});
            regEnumKeyEx.setAccessible(true);
            regDeleteKey = userClass.getDeclaredMethod(
                    "WindowsRegDeleteKey", new Class[]{int.class,
                                                       byte[].class});
            regDeleteKey.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete previous RuralInvest installation items in Windows Control Panel.
     *
     * @param iData provided by izPack
     * @param uih   provided by izPack
     */
    @Override
    public void executeAction(InstallData iData, AbstractUIHandler uih) {
        if (iData.getPlatform().getName().equals(Platform.Name.WINDOWS)) {
            for (String key : KEYS) {
                try {
                    removeFromKey(key);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void initialize(PanelActionConfiguration pac) {
    }

    private void removeFromKey(String key) throws Exception{
        try {
            List<String> results = new ArrayList<String>();
            int[] handles = (int[]) regOpenKey.invoke(systemRoot, new Object[]{
                    HKEY_LOCAL_MACHINE, toCstr(key), new Integer(KEY_READ)
            });
            if (handles[1] != REG_SUCCESS) {
                return;
            }
            int[] info = (int[]) regQueryInfoKey.invoke(systemRoot,
                                                        new Object[]{
                                                                new Integer(handles[0])});

            int count = info[0];
            int maxlen = info[3];
            for (int index = 0; index < count; index++) {
                byte[] name = (byte[]) regEnumKeyEx.invoke(systemRoot, new Object[]{
                        new Integer
                                (handles[0]), new Integer(index), new Integer(maxlen + 1)
                });
                results.add(new String(name).trim());
            }
            regCloseKey.invoke(systemRoot, new Object[]{new Integer(handles[0])});

            for (String subkey : results) {
                if (subkey.startsWith("RuralInvest")) {
                    Object[] deleteKey = new Object[]{HKEY_LOCAL_MACHINE,
                                                      toCstr(String.format("%s\\%s", key,
                                                                           subkey))};
                    int rc = ((Integer) regDeleteKey.invoke(systemRoot, deleteKey))
                            .intValue();
                    if (rc != REG_SUCCESS) {
                        throw new IllegalArgumentException("rc=" + rc + "  key=" + subkey);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new Exception(e);
        } catch (InvocationTargetException e) {
            throw new Exception(e);
        }

    }

    private static byte[] toCstr(String str) {
        byte[] result = new byte[str.length() + 1];

        for (int i = 0; i < str.length(); i++) {
            result[i] = (byte) str.charAt(i);
        }
        result[str.length()] = 0;
        return result;
    }
}