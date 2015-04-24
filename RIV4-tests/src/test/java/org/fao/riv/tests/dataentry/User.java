package org.fao.riv.tests.dataentry;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.fao.riv.tests.TestApp;
import org.fao.riv.tests.WebTest;

public class User extends WebTest {
	
	 @BeforeClass 
	 public static void setUpClass() {      
	       System.out.println("     test User");
	 }
	
	@Before
	public void deleteExisting() {
		login();
	}
	
	@After
    public void close() {
		clickLink("logoff");
		closeBrowser();
    }
	
	@Test
	public void addUser() throws Exception {
		String usersTitle = getMessage("ruralInvest")+" :: "+getMessage("user.users");
		String userTitle = getMessage("ruralInvest")+" :: "+getMessage("mainMenu.config.users.addEdit");
		getTestContext().setResourceBundleName("dataentry/user");
		
		// go to users page
		clickLink("gotoUsers");
		assertTitleEquals(usersTitle);
		assertTablePresent("users");
		assertTextNotInTable("users", getMessage("name"));
		assertLinkPresent("addUser");
		clickLink("addUser");
		assertTitleEquals(userTitle);
	
		// add new user and confirm it's been added
		setTextField("username", getMessage("username"));
		setTextField("password", getMessage("password"));
		setTextField("passwordRepeat", getMessage("password"));
		setTextField("description", getMessage("name"));
		selectOption("lang", getMessage("lang"));
		setTextField("organization", getMessage("org"));
		setTextField("location", getMessage("location"));
		setTextField("telephone", getMessage("tel"));
		setTextField("email", getMessage("email"));
		rivSubmitForm();
		assertElementNotPresent("errorbox");assertTitleEquals(usersTitle);
		assertTablePresent("users");
		assertTextInTable("users", getMessage("name"));
		
		// delete new user and confirm
		clickLinkWithImage("delete.gif", 0);
		clickButtonWithText("Delete item");
		assertTablePresent("users");
		assertTextNotInTable("users", getMessage("name"));
	}
	
	@Test
	public void changePasswordForExistingUser() {
		String username="asdfasdf";
		String password="zvxvczxv";
		
		// change username and password for current user
		changeUsername(username, password);
		clickLink("logoff");
		assertTitleEquals("RuralInvest login");

		// make sure old username doesn't work anymore
		setTextField("j_username", TestApp.username);
	    setTextField("j_password", TestApp.password);
		submit();
		assertTitleEquals("RuralInvest login");
		assertTextPresent(getMessage("login.incorrect"));
		setTextField("j_username", username);
	    setTextField("j_password", password);
	    submit();
	    assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("mainMenu.home"));
	    
	    // reset correct username and password
	    changeUsername(TestApp.username, TestApp.password);
	    clickLink("logoff");
		assertTitleEquals("RuralInvest login");
		setTextField("j_username", TestApp.username);
	    setTextField("j_password", TestApp.password);
		submit();
	    assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("mainMenu.home"));
	}
	
	private void changeUsername(String username, String password) {
		clickLink("welcome");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("mainMenu.config.users.addEdit"));
		clickLink("changeUser");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("mainMenu.config.users.addEdit"));
		assertTextPresent("Change username and password");
		setTextField("username",username);
		setTextField("password",password);
		setTextField("passwordRepeat",password);
		assertTextFieldEquals("passwordRepeat",password);
		rivSubmitForm();
		assertElementNotPresent("errorbox");
		assertTitleEquals(getMessage("ruralInvest")+" :: "+getMessage("user.users"));
	}
}