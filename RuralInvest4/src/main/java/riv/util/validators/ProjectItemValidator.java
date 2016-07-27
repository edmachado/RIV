package riv.util.validators;

import java.util.Set;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import riv.objects.HasDonations;
import riv.objects.project.*;

public class ProjectItemValidator implements Validator {
	private Boolean incomeGen;
	private Integer duration;
	private Set<Donor> donors;
	private boolean fromExcel;

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
	public void setDonors(Set<Donor> donors) {
		this.donors=donors;
	}
	public void setFromExcel(boolean fromExcel) {
		this.fromExcel = fromExcel;
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
			
			HasDonations hd = (HasDonations)i;
			if (donors==null) { donors=i.getProject().getDonors(); }
			if (fromExcel) {
				ValidateUtils.rejectMapValueIfEmptyOrNegative(hd.getDonations(), "donations", 0,  type+nongen+".donated", errors);
			} else {
				for (Donor d : donors) {
					ValidateUtils.rejectMapValueIfEmptyOrNegative(hd.getDonations(), "donations", d.getOrderBy(), d.getDescription(), errors);
				}
			}
			
			ValidateUtils.rejectIfZeroOrNegative(i, "yearBegin", type+".yearBegin", errors);
			
			if (!errors.hasFieldErrors("ownResources")) {
				ValidateUtils.rejectIfNegativeFromValue(inv.getTotal()-inv.getDonated()-inv.getOwnResources(), i, "financed", type+".financed", errors); //donations-
			}
		
			if (obj instanceof ProjectItemAsset || obj instanceof ProjectItemAssetWithout) {
				ValidateUtils.rejectIfZeroOrNegative(i, "econLife", "projectInvestAsset.econLife", errors);
				ValidateUtils.rejectIfEmptyOrNegative(i, "maintCost", "projectInvestAsset.maintCost", errors);
				ValidateUtils.rejectIfEmptyOrNegative(i, "salvage", "projectInvestAsset.salvage", errors);
			}	
			
			if (inv.getYearBegin()!=null && inv.getYearBegin()>duration) {
				errors.rejectValue("yearBegin", "error.assetYearExceeds", "The first year of the asset exceeds the project duration.");
			}
			
		} else if (obj instanceof ProjectItemGeneralBase) {
			String type = obj instanceof ProjectItemGeneral || obj instanceof ProjectItemGeneralWithout ? "projectGeneralSupplies" : "projectGeneralPersonnel";
			ValidateUtils.rejectIfEmpty(i, "description", type+".description", errors);
			ValidateUtils.rejectIfEmpty(i, "unitType", type+".unitType", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitCost", type+".unitCost", errors);
			
			for (int y=0;y<i.getProject().getDuration(); y++) {
				if (y==0 || i.getProject().isPerYearGeneralCosts()) {
					ProjectItemGeneralPerYear py = ((ProjectItemGeneralBase)i).getYears().get(y);
					ValidateUtils.rejectChildValueIfEmptyOrNegative(py, y, "unitNum", type+".unitNum", "years", errors);
					ValidateUtils.rejectChildValueIfEmptyOrNegative(py, y, "total", type+".totalCost", "years", errors);
					ValidateUtils.rejectChildValueIfEmptyOrNegative(py, y, "ownResources", type+".ownResources", "years", errors);
					ValidateUtils.rejectChildValueIfEmptyOrNegative(py, y, "external", type+".external", "years", errors);			
				}
			}
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
			
			HasDonations hd = (HasDonations)i;
			if (donors==null) { donors=i.getProject().getDonors(); }
			if (fromExcel) {
				ValidateUtils.rejectMapValueIfEmptyOrNegative(hd.getDonations(), "donations", 0,  itemType+nongen+".donated", errors);
			} else {
				for (Donor d : donors) {
					ValidateUtils.rejectMapValueIfEmptyOrNegative(hd.getDonations(), "donations", d.getOrderBy(), d.getDescription(), errors);
				}
			}
			
			ValidateUtils.rejectIfNegative(i, "total", itemType+".total", errors);
			ValidateUtils.rejectIfNegative(i, "ownResource", itemType+".ownResource", errors);
		}
	}
}