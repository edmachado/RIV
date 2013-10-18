package riv.util.validators;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import riv.objects.config.Setting;
import riv.objects.profile.Profile;
import riv.objects.profile.ProfileProduct;
import riv.objects.profile.ProfileProductBase;
import riv.web.config.RivConfig;

public class ProfileValidator implements Validator {
	RivConfig rivConfig;
	MessageSource messageSource;
	
	Integer step;
	
	@SuppressWarnings("unused")
	private ProfileValidator(){}; // must call parameterized constructor
	public ProfileValidator(Integer step, RivConfig rivConfig, MessageSource messageSource) {
		this.step=step;
		this.rivConfig=rivConfig;
		this.messageSource=messageSource;
	}
	
	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
	    return Profile.class.equals(clazz);  
	}  
	
	public void validate(Object obj, Errors errors) {
		Profile profile = (Profile)obj;
		switch (step) {
		case 1:
			ValidateUtils.rejectIfEmpty(profile, "profileName", "profile.profileName", errors);
			ValidateUtils.rejectIfZeroOrNegative(profile, "exchRate", "profile.exchRate", errors);
			ValidateUtils.rejectIfZeroOrNegative(profile, "benefNum", "profile.benefNum", errors);
			ValidateUtils.rejectIfEmptyOrNegative(profile, "benefFamilies", "profile.benefFamilies", errors);
			Setting setting = rivConfig.getSetting();
			Locale locale = new Locale(setting.getLang());
			String loc1 = messageSource.getMessage("location1", new Object[0], setting.getLocation1(), locale);
			String loc2 = messageSource.getMessage("location2", new Object[0], setting.getLocation2(), locale);
			String loc3 = messageSource.getMessage("location3", new Object[0], setting.getLocation3(), locale);
			ValidateUtils.rejectIfEmptyNoResource(profile, "location1", loc1, errors);
			ValidateUtils.rejectIfEmptyNoResource(profile, "location2", loc2, errors);
			ValidateUtils.rejectIfEmptyNoResource(profile, "location3", loc3, errors);
			break;
		case 2:
			ValidateUtils.rejectIfEmpty(profile, "benefName", "profile.benefName", errors);
			ValidateUtils.rejectIfEmpty(profile, "benefDesc", "profile.benefDesc", errors);
			ValidateUtils.rejectIfEmpty(profile, "projDesc", "profile.objective", errors);
			if (!profile.getIncomeGen()) {
				ValidateUtils.rejectIfEmpty(profile, "sourceFunds", "profile.sourceFunds", errors);
			}
			break;
		case 3:
			ValidateUtils.rejectIfEmpty(profile, "market", "profile.market", errors);
			ValidateUtils.rejectIfEmpty(profile, "enviroImpact", "profile.enviroImpact", errors);
			break;
		case 4:
			if (profile.getGlsGoods().size()==0) { ValidateUtils.rejectEmptyTable("glsGoods","profileGoods",errors); }
			if (profile.getGlsLabours().size()==0) { ValidateUtils.rejectEmptyTable("glsLabours","profileLabour",errors); }
			break;
		case 5:
			if (profile.getGlsGeneral().size()==0) { ValidateUtils.rejectEmptyTable("glsGeneral","profileGeneral",errors); }
			break;
		case 6:
			String noProductError = profile.getIncomeGen() ? "error.noProfileProduct" : "error.noProfileActivity";
			String noProductErrorText = profile.getIncomeGen() ? "This profile must contain at least one product" : "This profile must contain at least one activity";
			String noTableError = profile.getIncomeGen() ? "error.productNoTable" : "error.activityNoTable";
			
			if (profile.getProducts().size()==0) {
				errors.rejectValue("products", noProductError, noProductErrorText);
			} else { // check product subtables
				for (ProfileProductBase prod : profile.getProducts()) {
					if (prod.getProfileIncomes().size()==0) {
						if (profile.getIncomeGen()) {
							ValidateUtils.rejectBlockEmptyTable(prod.getDescription(), "Income", "profileProductIncome", noTableError, errors);
						} else {
							ValidateUtils.rejectBlockEmptyTable(prod.getDescription(), "User charge", "profileActivityCharge", noTableError, errors);
						}
					}
					if (prod.getProfileInputs().size()==0) {
						ValidateUtils.rejectBlockEmptyTable(prod.getDescription(), "Input", "profileProductInput", noTableError, errors);
					}
					if (prod.getProfileLabours().size()==0) {
						ValidateUtils.rejectBlockEmptyTable(prod.getDescription(), "Labour", "profileProductLabour", noTableError, errors);	
					}
				}
			}
			break;
		case 8:
			ValidateUtils.enforceLength(profile, "reccDesc", "profile.justification", errors);
			break;
		}
	}
}