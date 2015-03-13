package riv.util.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
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
		if (obj.getClass().isAssignableFrom(ProjectItemAsset.class) || obj.getClass().isAssignableFrom(ProjectItemAssetWithout.class)){
			for (int donor_id : ((ProjectItemAsset)i).getDonations().keySet()) {
				ValidateUtils.rejectMapValueIfEmptyOrNegative(((ProjectItemAsset)i).getDonations(), "donations", donor_id, "donated", errors);
			}
			
			ValidateUtils.rejectIfEmpty(i, "description", "projectInvestAsset"+nongen+".description", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitNum", "projectInvestAsset"+nongen+".unitNum", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitCost", "projectInvestAsset"+nongen+".unitCost", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "ownResources", "projectInvestAsset"+nongen+".ownResources", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "donated", "projectInvestAsset"+nongen+".donated", errors);
			ValidateUtils.rejectIfZeroOrNegative(i, "econLife", "projectInvestAsset.econLife", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "maintCost", "projectInvestAsset.maintCost", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "salvage", "projectInvestAsset.salvage", errors);
			ValidateUtils.rejectIfZeroOrNegative(i, "yearBegin", "projectInvestAsset.yearBegin", errors);
			
			ValidateUtils.rejectIfNegative(i, "total", "projectInvestAsset.totalCost", errors);
			ValidateUtils.rejectIfNegative(i, "financed", "projectInvestAsset.financed", errors);
			
			if (obj.getClass().isAssignableFrom(ProjectItemAsset.class)) {
				if (((ProjectItemAsset)i).getYearBegin()!=null && ((ProjectItemAsset)i).getYearBegin()>duration) {
					errors.rejectValue("yearBegin", "error.assetYearExceeds", "The first year of the asset exceeds the project duration.");
				}
			} else {
				if (((ProjectItemAssetWithout)i).getYearBegin()!=null && ((ProjectItemAssetWithout)i).getYearBegin()>duration) {
					errors.rejectValue("yearBegin", "error.assetYearExceeds", "The first year of the asset exceeds the project duration.");
				}
			}
			
		} else if (obj.getClass().isAssignableFrom(ProjectItemLabour.class) || obj.getClass().isAssignableFrom(ProjectItemLabourWithout.class)) {
			ValidateUtils.rejectIfEmpty(i, "description", "projectInvestLabour"+nongen+".description", errors);
			ValidateUtils.rejectIfEmpty(i, "unitType", "projectInvestLabour"+nongen+".unitType", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitNum", "projectInvestLabour"+nongen+".unitNum", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitCost", "projectInvestLabour"+nongen+".unitCost", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "ownResources", "projectInvestLabour"+nongen+".ownResources", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "donated", "projectInvestLabour"+nongen+".donated", errors);
			ValidateUtils.rejectIfZeroOrNegative(i, "yearBegin", "projectInvestAsset.yearBegin", errors);
			ValidateUtils.rejectIfNegative(i, "total", "projectInvestAsset.totalCost", errors);
			ValidateUtils.rejectIfNegative(i, "financed", "projectInvestAsset.financed", errors);
		} else if (obj.getClass().isAssignableFrom(ProjectItemService.class) || obj.getClass().isAssignableFrom(ProjectItemServiceWithout.class)) {
			ValidateUtils.rejectIfEmpty(i, "description", "projectInvestService"+nongen+".description", errors);
			ValidateUtils.rejectIfEmpty(i, "unitType", "projectInvestService"+nongen+".unitType", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitNum", "projectInvestService"+nongen+".unitNum", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitCost", "projectInvestService"+nongen+".unitCost", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "ownResources", "projectInvestService"+nongen+".ownResources", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "donated", "projectInvestService"+nongen+".donated", errors);
			ValidateUtils.rejectIfZeroOrNegative(i, "yearBegin", "projectInvestService.yearBegin", errors);
			ValidateUtils.rejectIfNegative(i, "total", "projectInvestService.totalCost", errors);
			ValidateUtils.rejectIfNegative(i, "financed", "projectInvestService.financed", errors);
		} else if (obj.getClass().isAssignableFrom(ProjectItemGeneral.class)||obj.getClass().isAssignableFrom(ProjectItemGeneralWithout.class)) {
			ValidateUtils.rejectIfEmpty(i, "description", "projectGeneralSupplies.description", errors);
			ValidateUtils.rejectIfEmpty(i, "unitType", "projectGeneralSupplies.unitType", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitNum", "projectGeneralSupplies.unitNum", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitCost", "projectGeneralSupplies.unitCost", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "ownResources", "projectGeneralSupplies.ownResources", errors);
			ValidateUtils.rejectIfNegative(i, "total", "projectGeneralSupplies.totalCost", errors);
			ValidateUtils.rejectIfNegative(i, "external", "projectGeneralSupplies.external", errors);
		} else if (obj.getClass().isAssignableFrom(ProjectItemPersonnel.class)||obj.getClass().isAssignableFrom(ProjectItemPersonnelWithout.class)) {
			ValidateUtils.rejectIfEmpty(i, "description", "projectGeneralPersonnel.description", errors);
			ValidateUtils.rejectIfEmpty(i, "unitType", "projectGeneralPersonnel.unitType", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitNum", "projectGeneralPersonnel.unitNum", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitCost", "projectGeneralPersonnel.unitCost", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "ownResources", "projectGeneralPersonnel.ownResources", errors);
			ValidateUtils.rejectIfNegative(i, "total", "projectGeneralPersonnel.totalCost", errors);
			ValidateUtils.rejectIfNegative(i, "external", "projectGeneralPersonnel.external", errors);
		} else if (obj.getClass().isAssignableFrom(ProjectItemContribution.class)) {
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
			} else {// (i.getClass().isAssignableFrom(ProjectItemNongenMaintenance.class)){
				itemType="projectNongenGeneral";
			}
			ValidateUtils.rejectIfEmpty(i, "description", itemType+".description", errors);
			ValidateUtils.rejectIfEmpty(i, "unitType", itemType+".unitType", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitNum", itemType+".unitNum", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "unitCost", itemType+".unitCost", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "statePublic", itemType+".statePublic", errors);
			ValidateUtils.rejectIfEmptyOrNegative(i, "other1", itemType+".other1", errors);
			ValidateUtils.rejectIfNegative(i, "total", itemType+".total", errors);
			ValidateUtils.rejectIfNegative(i, "ownResource", itemType+".ownResource", errors);
		}
	}
}