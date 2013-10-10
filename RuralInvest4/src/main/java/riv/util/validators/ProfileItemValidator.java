package riv.util.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import riv.objects.profile.*;

public class ProfileItemValidator implements Validator {
	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return ProfileItem.class.isAssignableFrom(clazz);
	}
	public void validate(Object obj, Errors errors) {
		ProfileItem i = (ProfileItem)obj;
		if (obj.getClass().isAssignableFrom(ProfileItemGood.class)){
			ValidateUtils.rejectIfEmpty(i, "description", "profileGoods.description", errors);
			ValidateUtils.rejectIfEmpty(i, "unitType", "profileGoods.unitType", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitNum", "profileGoods.unitNum", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitCost", "profileGoods.unitCost", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "ownResource", "profileGoods.ownResource", errors);
			ValidateUtils.rejectIfZeroOrNegative(i, "econLife", "profileGoods.econLife", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "salvage", "profileGoods.salvage", errors);
			ValidateUtils.rejectIfNegative(i, "donated", "profileGoods.externalResources", errors);
			ValidateUtils.rejectIfNegative(i, "reserve", "profileGoods.reserve", errors);
			ValidateUtils.rejectIfNegative(i, "total", "profileGoods.totalCost", errors);
		} else if (obj.getClass().isAssignableFrom(ProfileItemLabour.class)) {
			ValidateUtils.rejectIfEmpty(i, "description", "profileLabour.description", errors);
			ValidateUtils.rejectIfEmpty(i, "unitType", "profileLabour.unitType", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitNum", "profileLabour.unitNum", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitCost", "profileLabour.unitCost", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "ownResource", "profileLabour.ownResource", errors);
			ValidateUtils.rejectIfNegative(i, "donated", "profileLabour.donated", errors);
			ValidateUtils.rejectIfNegative(i, "total", "profileLabour.totalCost", errors);
		} else {// if (obj.getClass().isAssignableFrom(ProfileItemGeneral.class)) {
			ValidateUtils.rejectIfEmpty(i, "description", "profileGeneral.description", errors);
			ValidateUtils.rejectIfEmpty(i, "unitType", "profileGeneral.unitType", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitNum", "profileGeneral.unitNum", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitCost", "profileGeneral.unitCost", errors);
			ValidateUtils.rejectIfNegative(i, "total", "profileGeneral.totalCost", errors);
		}
	}
}
