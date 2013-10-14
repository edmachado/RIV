package riv.util.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import riv.objects.reference.ReferenceCost;
import riv.objects.reference.ReferenceIncome;
import riv.objects.reference.ReferenceItem;
import riv.objects.reference.ReferenceLabour;

public class RefItemValidator implements Validator {
	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return ReferenceItem.class.isAssignableFrom(clazz);
	}
	public void validate(Object obj, Errors errors) {
		ReferenceItem i = (ReferenceItem)obj; 
		if (obj.getClass().isAssignableFrom(ReferenceCost.class)){
			ValidateUtils.rejectIfEmpty(i, "description", "reference.cost.description", errors);
			ValidateUtils.rejectIfEmpty(i, "unitType", "reference.cost.unitType", errors);
			ValidateUtils.rejectIfNegative(i, "unitCost", "reference.cost.unitCost", errors);
			ValidateUtils.rejectIfNegative(i, "transport", "reference.cost.transport", errors);
		} else if (obj.getClass().isAssignableFrom(ReferenceIncome.class)){
			ValidateUtils.rejectIfEmpty(i, "description", "reference.income.description", errors);
			ValidateUtils.rejectIfEmpty(i, "unitType", "reference.income.unitType", errors);
			ValidateUtils.rejectIfNegative(i, "unitCost", "reference.income.unitCost", errors);
			if (i.getProbase().getIncomeGen()) {
				ValidateUtils.rejectIfNegative(i, "transport", "reference.income.transport", errors);
			}
		} else if (obj.getClass().isAssignableFrom(ReferenceLabour.class)){
			ValidateUtils.rejectIfEmpty(i, "description", "reference.labour.description", errors);
			ValidateUtils.rejectIfEmpty(i, "unitType", "reference.labour.unitType", errors);
			ValidateUtils.rejectIfNegative(i, "unitCost", "reference.labour.unitCost", errors);
		} 
	}
}