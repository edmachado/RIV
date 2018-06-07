package org.fao.riv.installer.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class WindowsService {
	public static final boolean riv3Exists = isServiceExist("RuralInvest");
	public static final boolean riv4Exists = isServiceExist("RuralInvest4");
	
	private static final String REGQUERY_UTIL = "reg query \"HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Services\\";

	public static void removeService(String service) {
            System.out.println("removing service " + service);
            String cmd = "sc delete \"" + service + "\"";
            System.out.println(cmd);
            try {
                Runtime.getRuntime().exec(cmd);
            } catch (IOException e) {
                e.printStackTrace(System.out);
            }
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace(System.out);
            }
            if (isServiceActive(service))
                System.out.println("Couldn't remove service.");
	}
	
	public static void stopService(String service) {
            System.out.println("NET STOP " + service);
            try {
                Runtime.getRuntime().exec("NET STOP " + service);
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }
    }
	
	public static boolean isServiceExist(String serviceName)  {
            try {
                Process
                        process =
                        Runtime.getRuntime().exec(String.format("sc query %s", serviceName));
                StreamReader reader = new StreamReader(process.getInputStream());

                reader.start();

                process.waitFor();
                reader.join();
                String out = reader.getResult();
                return out.contains("SERVICE_NAME:") && out.contains(serviceName) && out
                        .contains("TYPE") && !out.contains("FAILED");
            } catch (Exception e) {
                e.printStackTrace(System.out);
                return false;
            }
	}
	
	public static boolean isServiceActive(String service) {
            try {
                Process process = Runtime.getRuntime().exec(REGQUERY_UTIL + service);
                StreamReader reader = new StreamReader(process.getInputStream());

                reader.start();
                process.waitFor();
                reader.join();

                String result = reader.getResult();
                if (result != null && !result.equals("")) {
                    System.out.println(service + " is active");
                    return true;
                } else {
                    System.out.println(service + " is not active");
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
	}
	
	static class StreamReader extends Thread {
        private InputStream is;
        private StringWriter sw;

        StreamReader(InputStream is)  {
            this.is = is;
            sw = new StringWriter();
        }

        public void run() {
            try {
                int c;
                while ((c = is.read()) != -1)
                    sw.write(c);

            } catch (IOException e) {
                e.printStackTrace(System.out);
            }
        }

        String getResult() {
            return sw.toString();
        }
    }

  
}