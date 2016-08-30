package riv.util;

import java.util.HashMap;

public class NavItems {
	private HashMap<String, String[][]> items;
	
	public NavItems() {
		items = new HashMap<String, String[][]>();
		String[][] steps = null;
		
		// menu "config"
		steps = new String[10][2];
		steps[0][0] = "mainMenu.config.settings";
		steps[0][1] = "config/settings";
		steps[1][0] = "mainMenu.config.users";
		steps[1][1] = "config/user";
		steps[2][0] = "mainMenu.config.offices";
		steps[2][1] = "config/office";
		steps[3][0] = "mainMenu.config.categories";
		steps[3][1] = "config/category";
		steps[4][0] = "mainMenu.config.beneficiaries";
		steps[4][1] = "config/beneficiary";
		steps[5][0] = "mainMenu.config.enviroCategories";
		steps[5][1] = "config/enviroCategory";
		steps[6][0] = "mainMenu.config.statuses";
		steps[6][1] = "config/status";
		steps[7][0] = "mainMenu.config.columns";
		steps[7][1] = "config/indicators";
		steps[8][0] = "";
		steps[8][1] = "config/appConfig1";
		steps[9][0] = "";
		steps[9][1] = "config/appConfig2";
		getItems().put("config", steps);
		
		// menu "profile"
		steps = new String[9][2];
		steps[0][0] = "profile.step1";
		steps[0][1] = "profile/step1/";
		steps[1][0] = "profile.step2";
		steps[1][1] = "profile/step2/";
		steps[2][0] = "profile.step3";
		steps[2][1] = "profile/step3/";
		steps[3][0] = "profile.step4";
		steps[3][1] = "profile/step4/";
		steps[4][0] = "profile.step5";
		steps[4][1] = "profile/step5/";
		steps[5][0] = "profile.step6";
		steps[5][1] = "profile/step6/";
		steps[6][0] = "profile.step7";
		steps[6][1] = "profile/step7/";
		steps[7][0] = "profile.step8";
		steps[7][1] = "profile/step8/";
		steps[8][0] = "profile.step9";
		steps[8][1] = "profile/step9/";
		getItems().put("profile", steps);
		
		// menu "profileNoninc"
		steps = new String[9][2];
		steps[0][0] = "profile.step1";
		steps[0][1] = "profile/step1/";
		steps[1][0] = "profile.step2";
		steps[1][1] = "profile/step2/";
		steps[2][0] = "profile.step3";
		steps[2][1] = "profile/step3/";
		steps[3][0] = "profile.step4";
		steps[3][1] = "profile/step4/";
		steps[4][0] = "profile.step5";
		steps[4][1] = "profile/step5/";
		steps[5][0] = "profile.step6.nongen";
		steps[5][1] = "profile/step6/";
		steps[6][0] = "profile.step7";
		steps[6][1] = "profile/step7/";
		steps[7][0] = "profile.step8";
		steps[7][1] = "profile/step8/";
		steps[8][0] = "profile.step9";
		steps[8][1] = "profile/step9/";
		getItems().put("profileNoninc", steps);
		
//		 menu "project"
		steps = new String[13][2];
		steps[0][0] = "project.step1";
		steps[0][1] = "project/step1/";
		steps[1][0] = "project.step2";
		steps[1][1] = "project/step2/";
		steps[2][0] = "project.step3";
		steps[2][1] = "project/step3/";
		steps[3][0] = "project.step4";
		steps[3][1] = "project/step4/";
		steps[4][0] = "project.step5";
		steps[4][1] = "project/step5/";
		steps[5][0] = "project.step6";
		steps[5][1] = "project/step6/";
		steps[6][0] = "project.step7";
		steps[6][1] = "project/step7/";
		steps[7][0] = "project.step8";
		steps[7][1] = "project/step8/";
		steps[8][0] = "project.step9";
		steps[8][1] = "project/step9/";
		steps[9][0] = "project.step10";
		steps[9][1] = "project/step10/";
		steps[10][0] = "project.step11";
		steps[10][1] = "project/step11/";
		steps[11][0] = "project.step12";
		steps[11][1] = "project/step12/";
		steps[12][0] = "project.step13";
		steps[12][1] = "project/step13/";
		getItems().put("project", steps);
		
//		 menu "projectNoninc"
		steps = new String[13][2];
		steps[0][0] = "project.step1";
		steps[0][1] = "project/step1/";
		steps[1][0] = "project.step2";
		steps[1][1] = "project/step2/";
		steps[2][0] = "project.step3";
		steps[2][1] = "project/step3/";
		steps[3][0] = "project.step4.nongen";
		steps[3][1] = "project/step4/";
		steps[4][0] = "project.step5";
		steps[4][1] = "project/step5/";
		steps[5][0] = "project.step6";
		steps[5][1] = "project/step6/";
		steps[6][0] = "project.step7";
		steps[6][1] = "project/step7/";
		steps[7][0] = "project.step8";
		steps[7][1] = "project/step8/";
		steps[8][0] = "project.step9.nongen";
		steps[8][1] = "project/step9/";
		steps[9][0] = "project.step10.nongen";
		steps[9][1] = "project/step10/";
		steps[10][0] = "project.step11.nongen";
		steps[10][1] = "project/step11/";
		steps[11][0] = "project.step12";
		steps[11][1] = "project/step12/";
		steps[12][0] = "project.step13";
		steps[12][1] = "project/step13/";
		getItems().put("projectNoninc", steps);
		
		// menu "search"
		steps = new String[2][2];
		steps[0][0] = "search.createFilter";
		steps[0][1] = "search";
		steps[1][0] = "search.searchResults";
		steps[1][1] = "search/results";
		getItems().put("search", steps);
		
//		menu "help"
		steps = new String[4][2];
		steps[0][0] = "mainMenu.help.manual";
		steps[0][1] = "help/manuals";
		steps[1][0] = "mainMenu.help.faq";
		steps[1][1] = "help/faq";
		steps[2][0] = "mainMenu.help.terms";
		steps[2][1] = "help/terms";
		steps[3][0] = "mainMenu.help.about";
		steps[3][1] = "help/about";
		getItems().put("help", steps);
		
		// menu admin
		steps = new String[1][2];
		steps[0][0] = "admin.page";
		steps[0][1] = "config/admin";
		getItems().put("admin", steps);
	}

	public String[][] getNavItems(String menuType) {
		return items.get(menuType);
	}

	public HashMap<String, String[][]> getItems() {
		return items;
	}
}
