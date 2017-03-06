package org.fao.riv.installer.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class HttpPorts {
	public static ArrayList<Integer> availablePorts(int START_PORT, int END_PORT) {
		boolean isConnected = false;
		
		ArrayList<Integer> portList = new ArrayList<Integer>();
		int tmpPort = START_PORT;
		
		 
		while (tmpPort<=END_PORT 
				&& portList.size()==0// only collect one port, no need for others
			) {
            String testUrl = "http://localhost:" + tmpPort;
            URL url = null;
            HttpURLConnection connection = null;
            try {
                url = new URL(testUrl);
                
                connection = (HttpURLConnection) url.openConnection();
                System.out.println("try to connect at port:" + tmpPort);
                connection.connect();
                System.out.println("skipped:" + tmpPort);
                isConnected = true;
            } catch (MalformedURLException e) {
                isConnected = false;
            } catch (IOException e) {
                isConnected = false;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (!isConnected) {
                    System.out.println("added:" + tmpPort);
                    portList.add(tmpPort);
                }
                tmpPort++;
            }
		}
		return portList;
	}
}