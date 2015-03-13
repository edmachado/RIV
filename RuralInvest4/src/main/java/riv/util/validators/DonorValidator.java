package riv.util.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import riv.objects.project.*;

public class DonorValidator implements Validator {
	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return Donor.class.isAssignableFrom(clazz);
	}
	public void validate(Object obj, Errors errors) {
		Donor d = (Donor)obj;
		ValidateUtils.rejectIfEmpty(d, "description", "project.donor.description", errors);
	}
}