package org.fao.riv.tests;

import static net.sourceforge.jwebunit.junit.JWebUnit.assertFormElementPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertFormPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextFieldEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTitleEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.beginAt;
import static net.sourceforge.jwebunit.junit.JWebUnit.getMessage;
import static net.sourceforge.jwebunit.junit.JWebUnit.getTestContext;
import static net.sourceforge.jwebunit.junit.JWebUnit.setBaseUrl;
import static net.sourceforge.jwebunit.junit.JWebUnit.setTextField;
import static net.sourceforge.jwebunit.junit.JWebUnit.submit;
import net.sourceforge.jwebunit.junit.JWebUnit;
import net.sourceforge.jwebunit.util.TestingEngineRegistry;

import org.apache.catalina.LifecycleException;
import org.fao.riv.utils.WebTestBase;
import org.junit.Before;

public class WebTest extends WebTestBase {
	@Before
    public void prepare() throws LifecycleException {
		JWebUnit.setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_HTMLUNIT);
		
		setBaseUrl(TestApp.appURL);
		getTestContext().setResourceBundleName("messages/messages");
	 }
	
	public void login() {
		beginAt("/");
		assertFormPresent("login");
		assertFormElementPresent("j_username");
	    assertFormElementPresent("j_password");
	    setTextField("j_username", TestApp.username);
	    setTextField("j_password", TestApp.password);
	    assertTextFieldEquals("j_username", TestApp.username);
	    submit();
	    assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("mainMenu.home"));
	}

//	@After
//	public void logout() throws LifecycleException {
		//clickLink("logoff");
		//TestApp.tomcatStop();
//	}
	
	
	
	
	}
