package riv.util.validators;

import org.springframework.validation.Validator;
import org.springframework.validation.Errors;

import riv.objects.project.*;

public class BlockItemValidator implements Validator {
	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return BlockItem.class.isAssignableFrom(clazz);
	}
	public void validate(Object obj, Errors errors) {
		BlockItem i = (BlockItem)obj;
		if (obj.getClass().isAssignableFrom(BlockIncome.class)){
			if (i.getBlock().getProject().getIncomeGen()) {
				ValidateUtils.rejectIfEmpty(i, "description", "projectBlockIncome.desc", errors);
				ValidateUtils.rejectIfEmpty(i, "unitType", "projectBlockIncome.unitType", errors);
				ValidateUtils.rejectIfEmptyOrNegative(i, "unitNum", "projectBlockIncome.unitNum", errors);
				ValidateUtils.rejectIfEmptyOrNegative(i, "qtyIntern", "projectBlockIncome.qtyIntern", errors);
				ValidateUtils.rejectIfEmptyOrNegative(i, "unitCost", "projectBlockIncome.unitCost", errors);
				ValidateUtils.rejectIfEmptyOrNegative(i, "transport", "projectBlockIncome.transport", errors);
				
				ValidateUtils.rejectIfNegative(i, "extern", "projectBlockIncome.qtyExtern", errors);
				ValidateUtils.rejectIfNegative(i, "total", "projectBlockIncome.total", errors);
				ValidateUtils.rejectIfNegative(i, "totalCash", "projectBlockIncome.totalCash", errors);
			} else {
				ValidateUtils.rejectIfEmpty(i, "description", "projectActivityCharge.desc", errors);
				ValidateUtils.rejectIfEmpty(i, "unitType", "projectActivityCharge.unitType", errors);
				ValidateUtils.rejectIfEmptyOrNegative(i, "unitNum", "projectActivityCharge.unitNum", errors);
				ValidateUtils.rejectIfEmptyOrNegative(i, "unitCost", "projectActivityCharge.unitCost", errors);
				ValidateUtils.rejectIfNegative(i, "total", "projectActivityCharge.total", errors);
			}
		} else if (obj.getClass().isAssignableFrom(BlockInput.class)) {
			ValidateUtils.rejectIfEmpty(i, "description", "projectBlockInput.desc", errors);
			ValidateUtils.rejectIfEmpty(i, "unitType", "projectBlockInput.unitType", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitNum", "projectBlockInput.unitNum", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "qtyIntern", "projectBlockInput.qtyIntern", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitCost", "projectBlockInput.unitCost", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "transport", "projectBlockInput.transport", errors);
			
			ValidateUtils.rejectIfNegative(i, "extern", "projectBlockInput.qtyExtern", errors);
			ValidateUtils.rejectIfNegative(i, "total", "projectBlockInput.total", errors);
			ValidateUtils.rejectIfNegative(i, "totalCash", "projectBlockInput.totalCash", errors);
		} else if (obj.getClass().isAssignableFrom(BlockLabour.class)) {
			ValidateUtils.rejectIfEmpty(i, "description", "projectBlockLabour.desc", errors);
			ValidateUtils.rejectIfEmpty(i, "unitType", "projectBlockLabour.unitType", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitNum", "projectBlockLabour.unitNum", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "qtyIntern", "projectBlockLabour.qtyIntern", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitCost", "projectBlockLabour.unitCost", errors);
			
			ValidateUtils.rejectIfNegative(i, "extern", "projectBlockLabour.qtyExtern", errors);
			ValidateUtils.rejectIfNegative(i, "total", "projectBlockLabour.total", errors);
			ValidateUtils.rejectIfNegative(i, "totalCash", "projectBlockLabour.totalCash", errors);
		}
	}
}
