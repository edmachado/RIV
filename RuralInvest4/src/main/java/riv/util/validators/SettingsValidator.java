package riv.util.validators;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.Validator;
import org.springframework.validation.Errors;

import riv.objects.config.Setting;

public class SettingsValidator implements Validator {
	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return Setting.class.isAssignableFrom(clazz);
	}
	public void validate(Object obj, Errors errors) {
		Setting s = (Setting)obj;
		ValidateUtils.rejectIfEmpty(s, "orgName", "settings.organization", errors);
		ValidateUtils.rejectIfEmpty(s, "location1", "settings.location1", errors);
		ValidateUtils.rejectIfEmpty(s, "location2", "settings.location2", errors);
		ValidateUtils.rejectIfEmpty(s, "location3", "settings.location3", errors);
		ValidateUtils.rejectIfEmpty(s, "currencyName", "settings.currency.name", errors);
		ValidateUtils.rejectIfEmpty(s, "currencySym", "settings.currency.symbol", errors);
		ValidateUtils.rejectIfEmptyOrNegative(s, "decimalLength", "settings.currency.decimalLength", errors);
		ValidateUtils.rejectIfEmpty(s, "decimalSeparator", "settings.currency.decimalSeparator", errors);
		ValidateUtils.rejectIfEmpty(s, "thousandSeparator", "settings.currency.thousandsSeparator", errors);
		ValidateUtils.rejectIfEmptyOrNegative(s, "exchRate", "settings.exchRate", errors);
		ValidateUtils.rejectIfEmptyOrNegative(s, "discountRate", "settings.discountRate", errors);
		ValidateUtils.rejectIfEmptyOrNegative(s, "maxDuration", "settings.maxDuration", errors);
		ValidateUtils.rejectIfEmptyOrNegative(s, "loan1Max", "settings.loan.maxDuration", errors);
		ValidateUtils.rejectIfEmptyOrNegative(s, "loan1GraceCapital", "settings.loan.graceCapital", errors);
		ValidateUtils.rejectIfEmptyOrNegative(s, "loan1GraceInterest", "settings.loan.graceInterest", errors);
		ValidateUtils.rejectIfEmptyOrNegative(s, "loan2Max", "settings.loan.maxDuration", errors);
		ValidateUtils.rejectIfEmptyOrNegative(s, "loan2GraceCapital", "settings.loan.graceCapital", errors);
		ValidateUtils.rejectIfEmptyOrNegative(s, "loan2GraceInterest", "settings.loan.graceInterest", errors);
	
		//TODO: max for decimallength:2, exchrate:9999, discountrate:1000, 
		if (s.getLoan1Max()!=null && s.getMaxDuration()!=null
				&& s.getLoan1Max()>s.getMaxDuration()) {
			errors.rejectValue("loan1Max", "error.maxLoanLength", new Object[] {new DefaultMessageSourceResolvable(new String[] {"settings.loan.maxDuration"})}, "{0}: The length of the loan cannot be greater than the maximum project duration.");
		}
		if (s.getLoan2Max()!=null && s.getMaxDuration()!=null
				&& s.getLoan2Max()>s.getMaxDuration()) {
			errors.rejectValue("loan2Max", "error.maxLoanLength", new Object[] {new DefaultMessageSourceResolvable(new String[] {"settings.loan.maxDuration"})}, "{0}: The length of the loan cannot be greater than the maximum project duration.");
		}
		
		if (s.getAdmin1Enabled()) {
			ValidateUtils.rejectIfEmpty(s, "admin1Title", "customFields.appConfig1", errors);
		}
		if (s.getAdmin2Enabled()) {
			ValidateUtils.rejectIfEmpty(s, "admin2Title", "customFields.appConfig1", errors);
		}
		if (s.isAdminMisc1Enabled()) {
			ValidateUtils.rejectIfEmpty(s, "adminMisc1Title", "adminText.title", errors);
		}
		if (s.isAdminMisc2Enabled()) {
			ValidateUtils.rejectIfEmpty(s, "adminMisc2Title", "adminText.title", errors);
		}
		if (s.isAdminMisc3Enabled()) {
			ValidateUtils.rejectIfEmpty(s, "adminMisc3Title", "adminText.title", errors);
		}
	}
}
