package riv.util.validators;

import org.springframework.validation.Validator;
import org.springframework.validation.Errors;

import riv.objects.HasDonations;
import riv.objects.project.*;

public class BlockItemValidator implements Validator {
	private Boolean incomeGen;
	private boolean fromExcel;
	
	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return BlockItem.class.isAssignableFrom(clazz);
	}
	public void setIncomeGen(boolean incomeGen) {
		this.incomeGen=incomeGen;
	}
	public void setFromExcel(boolean fromExcel) {
		this.fromExcel=fromExcel;
	}
	
	public void validate(Object obj, Errors errors) {
		BlockItem i = (BlockItem)obj;
		boolean isIg = incomeGen!=null ? incomeGen : i.getBlock().getProject().getIncomeGen();
		if (obj instanceof BlockIncome){
			if (isIg) {
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
		} else if (obj instanceof BlockInput) {
			ValidateUtils.rejectIfEmpty(i, "description", "projectBlockInput.desc", errors);
			ValidateUtils.rejectIfEmpty(i, "unitType", "projectBlockInput.unitType", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitNum", "projectBlockInput.unitNum", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "qtyIntern", "projectBlockInput.qtyIntern", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitCost", "projectBlockInput.unitCost", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "transport", "projectBlockInput.transport", errors);
			
			ValidateUtils.rejectIfNegative(i, "extern", "projectBlockInput.qtyExtern", errors);
			ValidateUtils.rejectIfNegative(i, "total", "projectBlockInput.total", errors);
			ValidateUtils.rejectIfNegative(i, "totalCash", "projectBlockInput.totalCash", errors);
		} else if (obj instanceof BlockLabour) {
			ValidateUtils.rejectIfEmpty(i, "description", "projectBlockLabour.desc", errors);
			ValidateUtils.rejectIfEmpty(i, "unitType", "projectBlockLabour.unitType", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitNum", "projectBlockLabour.unitNum", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "qtyIntern", "projectBlockLabour.qtyIntern", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitCost", "projectBlockLabour.unitCost", errors);
			
			ValidateUtils.rejectIfNegative(i, "extern", "projectBlockLabour.qtyExtern", errors);
			ValidateUtils.rejectIfNegative(i, "total", "projectBlockLabour.total", errors);
			ValidateUtils.rejectIfNegative(i, "totalCash", "projectBlockLabour.totalCash", errors);
		}
		
		if (!isIg && obj instanceof HasDonations) {
			HasDonations hd = (HasDonations)i;
//			if (donors==null) { donors=i.getProject().getDonors(); }
			if (fromExcel) {
				ValidateUtils.rejectMapValueIfEmptyOrNegative(hd.getDonations(), "donations", 0,  "projectBlockInput.donated", errors);
			} else {
				for (Donor d : i.getBlock().getProject().getDonors()) {
					ValidateUtils.rejectMapValueIfEmptyOrNegative(hd.getDonations(), "donations", d.getOrderBy(), d.getDescription(), errors);
				}
			}
		}
	}
}
