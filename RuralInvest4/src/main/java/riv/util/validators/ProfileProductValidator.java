package riv.util.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import riv.objects.profile.*;

public class ProfileProductValidator implements Validator {
	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return ProfileProductBase.class.isAssignableFrom(clazz);
	}
	public void validate(Object obj, Errors errors) {
		ProfileProductBase pp = (ProfileProductBase)obj;
		String type = pp.getProfile().getIncomeGen() ? "profileProduct" : "profileActivity";
		ValidateUtils.rejectIfEmpty(pp, "description", type+".desc", errors);
		ValidateUtils.rejectIfEmpty(pp, "unitType", type+".prodUnit", errors);
		ValidateUtils.rejectIfEmptyOrNegative(pp, "unitNum", type+".numUnits", errors);
		ValidateUtils.rejectIfEmptyOrNegative(pp, "cycleLength", type+".cycleLength", errors);
		ValidateUtils.rejectIfEmptyOrNegative(pp, "cyclePerYear", type+".cycles", errors);

		if (pp.isCycles()) {
			// total length of cycles cannot be longer than 1 year
			if (!errors.hasFieldErrors("cycleLength") && pp.getCyclePerYear()!=null) {
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
	
				if (cycleLength>1.0) {
					errors.rejectValue("cyclePerYear", "error.block.cyclesPerYear");
				}
			}
		}
	}
}
