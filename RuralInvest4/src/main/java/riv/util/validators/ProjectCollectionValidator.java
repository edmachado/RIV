package riv.util.validators;

import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import riv.objects.project.Block;
import riv.objects.project.BlockLabour;
import riv.objects.project.BlockWithout;
import riv.objects.project.Project;
import riv.objects.project.ProjectItemAsset;
import riv.objects.project.ProjectItemAssetWithout;
import riv.objects.project.ProjectItemLabour;
import riv.objects.project.ProjectItemLabourWithout;
import riv.objects.project.ProjectItemNongenLabour;
import riv.objects.project.ProjectItemPersonnel;
import riv.objects.project.ProjectItemPersonnelWithout;
import riv.web.config.RivConfig;

public class ProjectCollectionValidator implements Validator {
	RivConfig rivConfig;
	MessageSource messageSource;
	
	Integer step;
	
	@SuppressWarnings("unused")
	private ProjectCollectionValidator(){}; // must call parameterized constructor
	public ProjectCollectionValidator(Integer step, RivConfig rivConfig, MessageSource messageSource) {
		this.step=step;
		this.rivConfig=rivConfig;
		this.messageSource=messageSource;
	}
	
	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
	    return Project.class.equals(clazz);  
	}  
	
	public void validate(Object obj, Errors errors) {
		Project project = (Project)obj;
		switch (step) {
			case 1:
				for (ProjectItemAsset a : project.getAssets()) {
					if (a.getMaintCost()==null || a.getMaintCost()<0) {
						errors.rejectValue("assets["+a.getOrderBy()+"].maintCost", "error.requiredNonNegative");
					}
				}
				break;
			case 2:
				for (ProjectItemAssetWithout a : project.getAssetsWithout()) {
					if (a.getMaintCost()==null || a.getMaintCost()<0) {
						errors.rejectValue("assetsWithout["+a.getOrderBy()+"].maintCost", "error.requiredNonNegative");
					}
				}
				break;
			case 3:
				for (ProjectItemLabour l : project.getLabours()) {
					ValidateUtils.rejectIfEmptyOrNegativeInSet(l, "labours", "unitNum", "projectInvestLabour.unitNum", errors);
					ValidateUtils.rejectIfEmptyOrNegativeInSet(l, "labours", "unitCost", "projectInvestLabour.unitCost", errors);
					ValidateUtils.rejectIfEmptyOrNegativeInSet(l, "labours", "ownResources", "projectInvestLabour.ownResources", errors);
				}			
				break;
			case 4:
				for (ProjectItemLabourWithout l : project.getLaboursWithout()) {
					ValidateUtils.rejectIfEmptyOrNegativeInSet(l, "laboursWithout", "unitNum", "projectInvestLabour.unitNum", errors);
					ValidateUtils.rejectIfEmptyOrNegativeInSet(l, "laboursWithout", "unitCost", "projectInvestLabour.unitCost", errors);
					ValidateUtils.rejectIfEmptyOrNegativeInSet(l, "laboursWithout", "ownResources", "projectInvestLabour.ownResources", errors);
				}			
				break;
			case 5:
				if (project.getIncomeGen()) {
					for (ProjectItemPersonnel g : project.getPersonnels()) {
						ValidateUtils.rejectIfEmptyOrNegativeInSet(g, "personnels", "unitCost", "projectGeneralPersonnel.unitCost", errors);
						ValidateUtils.rejectChildValueIfEmptyOrNegativeInSet(g.getYears().get(0), 0, "unitNum", "projectGeneralPersonnel.unitNum", "personnels["+g.getOrderBy()+"].years[0].unitNum", errors);
					}
				} else {
					for (ProjectItemNongenLabour l : project.getNongenLabours()) {
						ValidateUtils.rejectIfEmptyOrNegativeInSet(l, "nongenLabours", "unitNum", "projectNongenLabour.unitNum", errors);
						ValidateUtils.rejectIfEmptyOrNegativeInSet(l, "nongenLabours", "unitCost", "projectNongenLabour.unitCost", errors);
					}
				}
				break;
			case 6:
				if (project.getIncomeGen()) {
					for (ProjectItemPersonnelWithout g : project.getPersonnelWithouts()) {
						ValidateUtils.rejectIfEmptyOrNegativeInSet(g, "personnelWithouts", "unitCost", "projectGeneralPersonnel.unitCost", errors);
						ValidateUtils.rejectChildValueIfEmptyOrNegativeInSet(g.getYears().get(0), 0, "unitNum", "projectGeneralPersonnel.unitNum", "personnelWithouts["+g.getOrderBy()+"].years[0].unitNum", errors);
					}
				}
				break;
			case 7:
				for (Block b : project.getBlocks()) {
					for (BlockLabour l : b.getLabours()) {
						ValidateUtils.rejectChildValueIfEmptyOrNegativeInSet(l, l.getOrderBy(), "unitNum", "projectBlockLabour.unitNum", "blocks["+b.getOrderBy()+"].labours["+l.getOrderBy()+"].unitNum", errors);
						ValidateUtils.rejectChildValueIfEmptyOrNegativeInSet(l, l.getOrderBy(), "unitCost", "projectBlockLabour.unitCost", "blocks["+b.getOrderBy()+"].labours["+l.getOrderBy()+"].unitCost", errors);
					}
				}
				break;
			case 8:
				for (BlockWithout b : project.getBlocksWithout()) {
					for (BlockLabour l : b.getLabours()) {
						ValidateUtils.rejectChildValueIfEmptyOrNegativeInSet(l, l.getOrderBy(), "unitNum", "projectBlockLabour.unitNum", "blocks["+b.getOrderBy()+"].labours["+l.getOrderBy()+"].unitNum", errors);
						ValidateUtils.rejectChildValueIfEmptyOrNegativeInSet(l, l.getOrderBy(), "unitCost", "projectBlockLabour.unitCost", "blocksWithout["+b.getOrderBy()+"].labours["+l.getOrderBy()+"].unitCost", errors);
					}
				}
				break;
		}
	}
}
