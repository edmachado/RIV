package org.fao.riv.tests.web;

import org.junit.*;

import org.fao.riv.tests.utils.WebTestUtil;
import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class ApplicationRunning extends WebTestUtil {
	final String LOGIN = "/login.jsp";
	@Test
    public void testAppRunning() {
        beginAt(LOGIN);
        assertTitleEquals("RuralInvest login");
	}
    
    @Test
    public void testLogin() {
    	login();
        assertLinkPresent("logoff");
    }
}
