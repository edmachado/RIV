package riv.util.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import riv.objects.config.User;

public class UserValidator implements Validator {
	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return User.class.isAssignableFrom(clazz);
	}

	public void validate(Object obj, Errors errors) {
		User user = (User)obj;
		ValidateUtils.rejectIfEmpty(user, "username", "user.username", errors);
		ValidateUtils.rejectIfEmpty(user, "description", "user.description", errors);
		ValidateUtils.rejectIfEmpty(user, "organization", "user.organization", errors);
		ValidateUtils.rejectIfEmpty(user, "location", "user.location", errors);
	}
}