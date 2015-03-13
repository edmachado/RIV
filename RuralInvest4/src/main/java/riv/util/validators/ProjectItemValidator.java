package riv.util.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import riv.objects.HasDonations;
import riv.objects.project.*;

public class ProjectItemValidator implements Validator {
	private Boolean incomeGen;
	private Integer duration;
	
	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return ProjectItem.class.isAssignableFrom(clazz);
	}
	public void setIncomeGen(boolean incomeGen) {
		this.incomeGen=incomeGen;
	}
	public void setDuration(int duration) {
		this.duration=duration;
	}
	public void validate(Object obj, Errors errors) {
		ProjectItem i = (ProjectItem)obj;
		boolean isIncomeGen = incomeGen!=null ? incomeGen : i.getProject().getIncomeGen();
		if (duration==null) { duration=i.getProject().getDuration(); }
		
		String nongen = isIncomeGen ? "" : "Nongen";
		if (obj instanceof ProjectInvestment) {
			String type;
			if (obj instanceof ProjectItemAsset || obj instanceof ProjectItemAssetWithout) {
				type="projectInvestAsset";
			} else if (obj instanceof ProjectItemLabour || obj instanceof ProjectItemLabourWithout) {
				type="projectInvestLabour";
			} else {
				type="projectInvestService";
			}
			ProjectInvestment inv = (ProjectInvestment)i;
			
			ValidateUtils.rejectIfEmpty(i, "description", type+nongen+".description", errors);
			ValidateUtils.rejectIfEmpty(i, "unitType", type+nongen+".unitType", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitNum", type+nongen+".unitNum", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitCost", type+nongen+".unitCost", errors);

			if (!errors.hasFieldErrors("unitNum") &! errors.hasFieldErrors("unitCost")) {
				ValidateUtils.rejectIfNegativeFromValue(i.getUnitNum()*i.getUnitCost(), i, "total", type+".totalCost", errors);
			}
			ValidateUtils.rejectIfEmptyOrNegative(i, "ownResources", type+nongen+".ownResources", errors);
			
			boolean donationError = false;
			boolean thisResult; Double donations=0.0;
			HasDonations hd = (HasDonations)i;
			
			for (Donor d : i.getProject().getDonors()) {
				thisResult = ValidateUtils.rejectMapValueIfEmptyOrNegative(hd.getDonations(), "donations", d.getOrderBy(), d.getDescription(), errors);
				if (thisResult) { 
					donationError=true; 
				} else {
					donations+=hd.getDonations().get(d.getOrderBy());
				}
			}
			if (!donationError) {
				ValidateUtils.rejectIfNegativeFromValue(donations, i, "donated", type+nongen+".donated", errors);
			}
			
			ValidateUtils.rejectIfZeroOrNegative(i, "yearBegin", type+".yearBegin", errors);
			
			if (!errors.hasFieldErrors("unitNum") &! errors.hasFieldErrors("unitCost") &!donationError &!errors.hasFieldErrors("ownResources")) {
				ValidateUtils.rejectIfNegativeFromValue(inv.getTotal()-donations-inv.getOwnResources(), i, "financed", type+".financed", errors);
			}
		
			if (obj instanceof ProjectItemAsset || obj instanceof ProjectItemAssetWithout) {
				ValidateUtils.rejectIfZeroOrNegative(i, "econLife", "projectInvestAsset.econLife", errors);
				ValidateUtils.rejectIfEmptyOrNegative(i, "maintCost", "projectInvestAsset.maintCost", errors);
				ValidateUtils.rejectIfEmptyOrNegative(i, "salvage", "projectInvestAsset.salvage", errors);
			}	
			
			if (inv.getYearBegin()!=null && inv.getYearBegin()>duration) {
				errors.rejectValue("yearBegin", "error.assetYearExceeds", "The first year of the asset exceeds the project duration.");
			}
			
		} else if (obj instanceof ProjectItemGeneral || obj instanceof ProjectItemGeneralWithout) {
			ValidateUtils.rejectIfEmpty(i, "description", "projectGeneralSupplies.description", errors);
			ValidateUtils.rejectIfEmpty(i, "unitType", "projectGeneralSupplies.unitType", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitNum", "projectGeneralSupplies.unitNum", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitCost", "projectGeneralSupplies.unitCost", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "ownResources", "projectGeneralSupplies.ownResources", errors);
			ValidateUtils.rejectIfNegative( i, "total", "projectGeneralSupplies.totalCost", errors);
			ValidateUtils.rejectIfNegative(i, "external", "projectGeneralSupplies.external", errors);
		} else if (obj instanceof ProjectItemPersonnel || obj instanceof ProjectItemPersonnelWithout) {
			ValidateUtils.rejectIfEmpty(i, "description", "projectGeneralPersonnel.description", errors);
			ValidateUtils.rejectIfEmpty(i, "unitType", "projectGeneralPersonnel.unitType", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitNum", "projectGeneralPersonnel.unitNum", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitCost", "projectGeneralPersonnel.unitCost", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "ownResources", "projectGeneralPersonnel.ownResources", errors);
			ValidateUtils.rejectIfNegative(i, "total", "projectGeneralPersonnel.totalCost", errors);
			ValidateUtils.rejectIfNegative(i, "external", "projectGeneralPersonnel.external", errors);
		} else if (obj instanceof ProjectItemContribution) {
			ValidateUtils.rejectIfEmpty(i, "description", "projectContribution.description", errors);
			ValidateUtils.rejectIfEmpty(i, "unitType", "projectContribution.unitType", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitNum", "projectContribution.unitNum", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitCost", "projectContribution.unitCost", errors);	
			ValidateUtils.rejectIfNegative(i, "total", "projectContribution.totalCost", errors);		
		} else {
			String itemType;
			if (i.getClass().isAssignableFrom(ProjectItemNongenMaterials.class)){
				itemType="projectNongenInput";
			} else if (i.getClass().isAssignableFrom(ProjectItemNongenLabour.class)){
				itemType="projectNongenLabour";
			} else { // Maintenance
				itemType="projectNongenGeneral";
			}
			ValidateUtils.rejectIfEmpty(i, "description", itemType+".description", errors);
			ValidateUtils.rejectIfEmpty(i, "unitType", itemType+".unitType", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitNum", itemType+".unitNum", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitCost", itemType+".unitCost", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "donated", itemType+".donated", errors);
			HasDonations hd = (HasDonations)i;
			for (Donor d : i.getProject().getDonors()) {
				ValidateUtils.rejectMapValueIfEmptyOrNegative(hd.getDonations(), "donations", d.getOrderBy(), d.getDescription(), errors);
			}
			ValidateUtils.rejectIfNegative(i, "total", itemType+".total", errors);
			ValidateUtils.rejectIfNegative(i, "ownResource", itemType+".ownResource", errors);
		}
	}
}