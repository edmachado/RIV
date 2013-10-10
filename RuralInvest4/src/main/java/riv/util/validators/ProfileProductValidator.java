package riv.util.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import riv.objects.profile.*;

public class ProfileProductValidator implements Validator {
	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return ProfileProduct.class.isAssignableFrom(clazz);
	}
	public void validate(Object obj, Errors errors) {
		ProfileProduct pp = (ProfileProduct)obj;
		if (pp.getProfile().getIncomeGen()) {
			ValidateUtils.rejectIfEmpty(pp, "description", "profileProduct.desc", errors);
			ValidateUtils.rejectIfEmpty(pp, "unitType", "profileProduct.prodUnit", errors);
			ValidateUtils.rejectIfEmptyOrNegative(pp, "unitNum", "profileProduct.numUnits", errors);
			ValidateUtils.rejectIfEmptyOrNegative(pp, "cycleLength", "profileProduct.cycleLength", errors);
			ValidateUtils.rejectIfEmptyOrNegative(pp, "cyclePerYear", "profileProduct.cycles", errors);
		} else {
			ValidateUtils.rejectIfEmpty(pp, "description", "profileActivity.desc", errors);
			ValidateUtils.rejectIfEmpty(pp, "unitType", "profileActivity.prodUnit", errors);
			ValidateUtils.rejectIfEmptyOrNegative(pp, "unitNum", "profileActivity.numUnits", errors);
			ValidateUtils.rejectIfEmptyOrNegative(pp, "cycleLength", "profileActivity.cycleLength", errors);
			ValidateUtils.rejectIfEmptyOrNegative(pp, "cyclePerYear", "profileActivity.cycles", errors);			
		}

		// total length of cycles cannot be longer than 1 year
		if (pp.getCyclePerYear()!=null) {
			double cycleLength = 0.0; // cycle length expressed in years
			switch (pp.getLengthUnit()) {
				case 0:  // months
					cycleLength=pp.getCyclePerYear()*pp.getCycleLength()/12.0;
					break;
				case 1: // weeks
					cycleLength=pp.getCyclePerYear()*pp.getCycleLength()/52.0;
					break;
				case 2: // calendar days
					cycleLength=pp.getCyclePerYear()*pp.getCycleLength()/365.0;
					break;
				case 3: // working days
					cycleLength=pp.getCyclePerYear()*pp.getCycleLength()/260.0;
					break;
			}

			if (cycleLength>1.0) errors.reject("error.block.cyclesPerYear");
		}
	}
}
