package org.fao.riv.utils;

import java.io.File;

public enum ImportFile {
	Logo("image.jpg"), 
	
	Settings16("settings/settings-1.6.riv"), 
	Settings20("settings/settings-2.0.riv"),
	Settings32("settings/settings-3.2.riv"),
	Settings40("settings/settings-4.0.riv"),
	
	ProfileIgV16("profile/profile-ig-1.6.riv"), 
	ProfileIgV20("profile/profile-ig-2.0.riv"), 
	ProfileIgV21("profile/profile-ig-2.1.riv"), 
	ProfileIgV22("profile/profile-ig-2.2.riv"),
	ProfileIgV40("profile/profile-ig-4.0.riv"),
	ProfileIgV42("profile/profile-ig-4.2.riv"),
	ProfileIgV21Generic("profile/profile-ig-2.1-generic.riv"),
	ProfileIgV22Generic("profile/profile-ig-2.2-generic.riv"), 
	ProfileNig16("profile/profile-nig-1.6.riv"),
	ProfileNig40("profile/profile-nig-4.0.riv"),
	ProfileXlsInvest("profile/profile-invest.xlsx"),
	ProfileXlsInvestNig("profile/profile-invest-nig.xlsx"),
	ProfileXlsInvestErrorLogic("profile/profile-invest-error-logic.xlsx"),
	ProfileXlsInvestErrorData("profile/profile-invest-error-data.xlsx"),
	ProfileXlsGeneral("profile/profile-general.xlsx"),
	ProfileXlsGeneralNig("profile/profile-general-nig.xlsx"),
	ProfileXlsProduct("profile/product.xlsx"),
	ProfileXlsProductNig("profile/product-nig.xlsx"),
	
	ProjectV16("project/project-ig-1.6.riv"), 
	ProjectV20("project/project-ig-2.0.riv"), 
	ProjectV20WithReferenceLinks("project/project-ig-2.0-with-links.riv"), 
	ProjectV31Generic("project/project-ig-3.1-generic.riv"),
	ProjectV40("project/project-ig-4.0.riv"), 
	ProjectV41("project/project-ig-4.1.riv"), 
	ProjectV41NoCycles("project/project-ig-4.1-no-cycles.riv"),
	ProjectNig16("project/project-nig-1.6.riv"), 
	ProjectNig40("project/project-nig-4.0.riv"),
	ProjectNig41("project/project-nig-4.1.riv"),
	ProjectXlsInvest("project/project-invest.xlsx"),
	ProjectXlsInvestNoWithout("project/project-invest-no-without.xlsx"),
	ProjectXlsGeneral("project/project-general.xlsx"),
	ProjectXlsGeneralNoWithout("project/project-general-no-without.xlsx"),
	ProjectXlsInvestNig("project/project-invest-nig.xlsx"),
	ProjectXlsGeneralNig("project/project-general-nig.xlsx"),
	ProjectXlsBlock("project/block.xlsx"),
	ProjectXlsBlockNig("project/block-nig.xlsx"),
	ProjectXlsContributions("project/contributions.xlsx"),
	ProjectXlsContributionsSimplified("project/contributions-simplified.xlsx"),
	
	ProjectTanzaniaRiv("project/Tanzania/Tanzania_case.riv"),
	ProjectTanzaniaCashFlowFirst("project/Tanzania/projectCashFlowFirst.xlsx"),
	ProjectTanzaniaCashFlow("project/Tanzania/projectCashFlow.xlsx"),
	ProjectTanzaniaProfitability("project/Tanzania/projectProfitability.xlsx");
	
	private final String fileName;
	private ImportFile(String fileName) {
		this.fileName=fileName;
	}
	
	@Override public String toString() {
		return this.fileName;
	}
	
	public File getFile() {
		File file = new File(String.format("%s/%s/%s", System.getProperty("user.dir"),"src/test/resources/imports",this.fileName));
    	return file;
	}
}
