package riv.util.validators;

import org.springframework.validation.Validator;
import org.springframework.validation.Errors;

import riv.objects.project.BlockBase;

public class BlockValidator implements Validator {
	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return BlockBase.class.isAssignableFrom(clazz);
	}
	public void validate(Object obj, Errors errors) {
		BlockBase b = (BlockBase)obj;
		if (b.getProject().getIncomeGen()) {
			ValidateUtils.rejectIfEmpty(b, "description", "projectBlock.name", errors);
			ValidateUtils.rejectIfEmpty(b, "unitType", "projectBlock.prodUnit", errors);
			if (b.isCycles()) {
				ValidateUtils.rejectIfEmptyOrNegative(b, "cycleLength", "projectBlock.cycleLength", errors);
				ValidateUtils.rejectIfEmptyOrNegative(b, "cyclePerYear", "projectBlock.cyclePerYear", errors);
				ValidateUtils.rejectIfEmptyOrNegative(b, "cycleFirstYear", "projectBlock.cycleFirstYear", errors);
				ValidateUtils.rejectIfEmptyOrNegative(b, "cycleFirstYearIncome", "projectBlock.cycleFirstYearIncome", errors);
			}
			// chronology validated in controller
		} else {
			ValidateUtils.rejectIfEmpty(b, "description", "projectActivity.name", errors);
			ValidateUtils.rejectIfEmpty(b, "unitType", "projectActivity.prodUnit", errors);
			if (b.isCycles()) {
				ValidateUtils.rejectIfEmptyOrNegative(b, "cycleLength", "projectActivity.cycleLength", errors);
				ValidateUtils.rejectIfEmptyOrNegative(b, "cyclePerYear", "projectActivity.cyclePerYear", errors);
			}
		}
		
		// length can't be longer than 1 year
		if (b.isCycles() &! errors.hasFieldErrors("cycleLength")) {
			if (b.getCyclePerYear()!=null && b.getCycleFirstYear()!=null && b.getCycleFirstYearIncome()!=null) {
				double cycleLength = 0.0; // cycle length expressed in years
				double cycleLengthFirstYear = 0.0; // cycle length expressed in years
				double cycleLengthFirstYearIncome = 0.0; // cycle length expressed in years
				switch (b.getLengthUnit()) {
					case 0:  // months
						cycleLength=b.getCyclePerYear()*b.getCycleLength()/12.0;
						cycleLengthFirstYear=b.getCycleFirstYear()*b.getCycleLength()/12.0;
						cycleLengthFirstYearIncome=b.getCycleFirstYearIncome()*b.getCycleLength()/12.0;
						break;
					case 1: // weeks
						cycleLength=b.getCyclePerYear()*b.getCycleLength()/52.0;
						cycleLengthFirstYear=b.getCycleFirstYear()*b.getCycleLength()/52.0;
						cycleLengthFirstYearIncome=b.getCycleFirstYearIncome()*b.getCycleLength()/52.0;
						break;
					case 2: // calendar days
						cycleLength=b.getCyclePerYear()*b.getCycleLength()/365.0;
						cycleLengthFirstYear=b.getCycleFirstYear()*b.getCycleLength()/365.0;
						cycleLengthFirstYearIncome=b.getCycleFirstYearIncome()*b.getCycleLength()/365.0;
						break;
					case 3: // working days
						cycleLength=b.getCyclePerYear()*b.getCycleLength()/260.0;
						cycleLengthFirstYear=b.getCycleFirstYear()*b.getCycleLength()/260.0;
						cycleLengthFirstYearIncome=b.getCycleFirstYearIncome()*b.getCycleLength()/260.0;
						break;
				}
	
				if (cycleLength>1.0) errors.rejectValue("cyclePerYear","error.block.cyclesPerYear");
				if (cycleLengthFirstYear>1.0) errors.rejectValue("cycleFirstYear","error.block.cyclesPerFirstYear");
				if (cycleLengthFirstYearIncome>1.0) errors.rejectValue("cycleFirstYearIncome","error.block.cyclesPerFirstYearIncome");
			}
		}
	}
}
