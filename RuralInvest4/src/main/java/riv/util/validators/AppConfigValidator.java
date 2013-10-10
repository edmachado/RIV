package riv.util.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import riv.objects.config.*;

public class AppConfigValidator implements Validator {
	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return AppConfig.class.isAssignableFrom(clazz);
	}

	public void validate(Object obj, Errors errors) {
		AppConfig ac = (AppConfig)obj;
		String desc="";
		if (Beneficiary.class.isInstance(ac))
			desc = "beneficiary.description";
		else if (FieldOffice.class.isInstance(ac))
			desc="fieldOffice.description";
		else if (ProjectCategory.class.isInstance(ac))
			desc="projectCategory.description";
		else if (EnviroCategory.class.isInstance(ac))
			desc="enviroCategory.description";
		else if (Status.class.isInstance(ac))
			desc="projectStatus.description";
		ValidateUtils.rejectIfEmpty(ac, "description", desc, errors);
	}
}