package riv.util.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import riv.objects.profile.*;

public class ProfileProductItemValidator implements Validator {
	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return ProfileProductItem.class.isAssignableFrom(clazz);
	}
	public void validate(Object obj, Errors errors) {
		ProfileProductItem i = (ProfileProductItem)obj;
		if (obj.getClass().isAssignableFrom(ProfileProductIncome.class)){
			if (i.getProfileProduct().getProfile().getIncomeGen()) {
				ValidateUtils.rejectIfEmpty(i, "description", "profileProductIncome.desc", errors);
				ValidateUtils.rejectIfEmpty(i, "unitType", "profileProductIncome.unitType", errors);
				ValidateUtils.rejectIfEmptyOrNegative(i, "unitNum", "profileProductIncome.unitNum", errors);
				ValidateUtils.rejectIfEmptyOrNegative(i, "unitCost", "profileProductIncome.unitCost", errors);
				ValidateUtils.rejectIfEmptyOrNegative(i, "transport", "profileProductIncome.transport", errors);
				ValidateUtils.rejectIfNegative(i, "total", "profileProductIncome.total", errors);
			} else {
				ValidateUtils.rejectIfEmpty(i, "description", "profileProductIncome.desc", errors);
				ValidateUtils.rejectIfEmpty(i, "unitType", "profileProductIncome.unitType", errors);
				ValidateUtils.rejectIfEmptyOrNegative(i, "unitNum", "profileProductIncome.unitNum", errors);
				ValidateUtils.rejectIfEmptyOrNegative(i, "unitCost", "profileProductIncome.unitCost", errors);
				ValidateUtils.rejectIfNegative(i, "total", "profileProductIncome.total", errors);
			}
		} else if (obj.getClass().isAssignableFrom(ProfileProductInput.class)) {
			ValidateUtils.rejectIfEmpty(i, "description", "profileProductInput.desc", errors);
			ValidateUtils.rejectIfEmpty(i, "unitType", "profileProductInput.unitType", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitNum", "profileProductInput.unitNum", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitCost", "profileProductInput.unitCost", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "transport", "profileProductInput.transport", errors);
			ValidateUtils.rejectIfNegative(i, "total", "profileProductInput.total", errors);
		} else if (obj.getClass().isAssignableFrom(ProfileProductLabour.class)) {
			ValidateUtils.rejectIfEmpty(i, "description", "profileProductLabour.desc", errors);
			ValidateUtils.rejectIfEmpty(i, "unitType", "profileProductLabour.unitType", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitNum", "profileProductLabour.unitNum", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitCost", "profileProductLabour.unitCost", errors);
			ValidateUtils.rejectIfNegative(i, "total", "profileProductLabour.total", errors);
		}
	}
}
