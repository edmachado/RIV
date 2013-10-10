package org.fao.riv.tests.dataentry;

import static net.sourceforge.jwebunit.junit.JWebUnit.assertElementNotPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertLinkPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTablePresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextInTable;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextNotInTable;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTitleEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickButtonWithText;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLink;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLinkWithImage;
import static net.sourceforge.jwebunit.junit.JWebUnit.closeBrowser;
import static net.sourceforge.jwebunit.junit.JWebUnit.getMessage;
import static net.sourceforge.jwebunit.junit.JWebUnit.getTestContext;
import static net.sourceforge.jwebunit.junit.JWebUnit.selectOption;
import static net.sourceforge.jwebunit.junit.JWebUnit.setTextField;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.fao.riv.tests.utils.WebTestUtil;

public class User extends WebTestUtil {
	
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
	
	@Ignore
	@Test
	public void changePasswordForExistingUser() {
		//TODO: implement
	}
}