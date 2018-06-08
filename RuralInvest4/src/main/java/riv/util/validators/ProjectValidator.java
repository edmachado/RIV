package riv.util.validators;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import riv.objects.FinanceMatrix;
import riv.objects.config.Setting;
import riv.objects.project.Block;
import riv.objects.project.BlockWithout;
import riv.objects.project.Project;
import riv.web.config.RivConfig;

public class ProjectValidator implements Validator {
	RivConfig rivConfig;
	MessageSource messageSource;
	
	Integer step;
	
	@SuppressWarnings("unused")
	private ProjectValidator(){}; // must call parameterized constructor
	public ProjectValidator(Integer step, RivConfig rivConfig, MessageSource messageSource) {
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
			ValidateUtils.rejectIfEmpty(project, "projectName", "project.projectName", errors);
			ValidateUtils.rejectIfEmptyOrNegative(project, "duration", "project.duration", errors);
			ValidateUtils.rejectIfEmptyOrNegative(project, "exchRate", "project.exchRate", errors);
			if (project.getIncomeGen()) {
				ValidateUtils.rejectIfEmptyOrNegative(project, "inflationAnnual", "project.inflationAnnual", errors);
			}
			ValidateUtils.rejectIfZeroOrNegative(project, "duration", "project.duration", errors);
			Setting setting = rivConfig.getSetting();
			Locale locale = new Locale(setting.getLang());
			String loc1 = messageSource.getMessage("location1", new Object[0], setting.getLocation1(), locale);
			String loc2 = messageSource.getMessage("location2", new Object[0], setting.getLocation2(), locale);
			String loc3 = messageSource.getMessage("location3", new Object[0], setting.getLocation3(), locale);
			ValidateUtils.rejectIfEmptyNoResource(project, "location1", loc1, errors);
			ValidateUtils.rejectIfEmptyNoResource(project, "location2", loc2, errors);
			ValidateUtils.rejectIfEmptyNoResource(project, "location3", loc3, errors);
			
			// project duration
			if (project.getDuration()!=null && project.getDuration()>setting.getMaxDuration())
				errors.rejectValue("duration", "error.durationExceeds", "The duration exceeds the maximum length set in the software configuration.");
			
			if (project.getIncomeGen() && errors.getFieldErrorCount("duration")==0) {
				if (project.getLoan1Duration()!=null && project.getLoan1Duration()>project.getDuration()) {
					errors.rejectValue("loan1Duration", "error.loanTooLong", "The loan cannot extend beyond the end of the project");
				}
				if (project.getLoan2Duration()!=null && project.getLoan2InitPeriod()!=null && project.getLoan2Duration()>project.getDuration()-project.getLoan2InitPeriod()+1) {
					errors.rejectValue("loan2Duration", "error.loanTooLong", "The loan cannot extend beyond the end of the project");
				}
			}
			break; 
		case 2:
			ValidateUtils.rejectIfEmpty(project, "benefName", "project.benefName", errors);
			ValidateUtils.rejectIfEmptyOrNegative(project, "beneDirectNum","project.benefFamilies", errors);
			ValidateUtils.rejectIfEmptyOrNegative(project, "beneDirectMen","project.benefMen", errors);
			ValidateUtils.rejectIfEmptyOrNegative(project, "beneDirectWomen","project.benefWomen", errors);
			ValidateUtils.rejectIfEmptyOrNegative(project, "beneDirectChild","project.benefChild", errors);
			ValidateUtils.rejectIfEmptyOrNegative(project, "beneIndirectNum","project.benefFamilies", errors);
			ValidateUtils.rejectIfEmptyOrNegative(project, "beneIndirectMen","project.benefMen", errors);
			ValidateUtils.rejectIfEmptyOrNegative(project, "beneIndirectWomen","project.benefWomen", errors);
			ValidateUtils.rejectIfEmptyOrNegative(project, "beneIndirectChild","project.benefChild", errors);
			ValidateUtils.rejectIfEmpty(project, "benefDesc", "project.benefDesc", errors);
			
			if (project.getBeneDirectChild()!=null && project.getBeneDirectMen()!=null && project.getBeneDirectWomen()!=null &&
					project.getBeneDirectChild()+project.getBeneDirectMen()+project.getBeneDirectWomen()==0)
				errors.rejectValue("beneDirectMen", "error.benefRequired", "There must be at least one direct beneficiary.");
				
		break;
		case 3:
			ValidateUtils.rejectIfEmpty(project, "justification", "project.justification", errors);
			ValidateUtils.rejectIfEmpty(project, "projDesc", "project.projectDescription", errors);
			ValidateUtils.rejectIfEmpty(project, "activities", "project.activities", errors);
		break;
		case 4:
			ValidateUtils.rejectIfEmpty(project, "technology", "project.technology", errors);
			ValidateUtils.rejectIfEmpty(project, "requirements", "project.requirements", errors);
			break;
		case 5:	
			ValidateUtils.rejectIfEmpty(project, "market", "project.market", errors);
			ValidateUtils.rejectIfEmpty(project, "enviroImpact", "project.enviroImpact", errors);
			if (!project.getIncomeGen()) {
				ValidateUtils.rejectIfEmpty(project, "sustainability", "project.sustainability", errors);		
			}
			break;
		case 6:
			ValidateUtils.rejectIfEmpty(project, "organization", "project.organization", errors);
			ValidateUtils.rejectIfEmpty(project, "assumptions", "project.assumptions", errors);
			break;
		case 7:
			if (project.getIncomeGen()) {
				if (project.getAssets().size()==0) ValidateUtils.rejectEmptyTable("assets","projectInvestAsset",errors);
				if (project.getLabours().size()==0) ValidateUtils.rejectEmptyTable("labours","projectInvestLabour",errors);
				if (project.getServices().size()==0) ValidateUtils.rejectEmptyTable("services","projectInvestService",errors);
			} else {
				if (project.getAssets().size()==0) ValidateUtils.rejectEmptyTable("assets","projectInvestAssetNongen",errors);
				if (project.getLabours().size()==0) ValidateUtils.rejectEmptyTable("labours","projectInvestLabourNongen",errors);
				if (project.getServices().size()==0) ValidateUtils.rejectEmptyTable("services","projectInvestServiceNongen",errors);
				
				//in case projects were imported from version 1.2 or lower
				ValidateUtils.rejectTableIfFieldsMissing("assets", "projectInvestAssetNongen", project.getAssets(), new String[] {"econLife","maintCost","yearBegin"}, errors);
				ValidateUtils.rejectTableIfFieldsMissing("assets", "projectInvestLabourNongen", project.getLabours(), new String[] {"yearBegin"}, errors);
				ValidateUtils.rejectTableIfFieldsMissing("assets", "projectInvestServiceNongen", project.getServices(), new String[] {"yearBegin"}, errors);
			}
			break;
		case 8:
			if (project.getIncomeGen()) {
				if (project.getGenerals().size()==0) ValidateUtils.rejectEmptyTable("generals","projectGeneralSupplies",errors);
				if (project.getPersonnels().size()==0) ValidateUtils.rejectEmptyTable("personnels","projectGeneralPersonnel",errors);
			} else {
				if (project.getNongenMaterials().size()==0) ValidateUtils.rejectEmptyTable("nongenMaterials","projectNongenInput",errors);
				if (project.getNongenLabours().size()==0) ValidateUtils.rejectEmptyTable("nongenLabours","projectNongenLabour",errors);
				if (project.getNongenMaintenance().size()==0) ValidateUtils.rejectEmptyTable("nongenMaintenance","projectNongenGeneral",errors);
			}
			break;
		case 9:
			String noBlockError = project.getIncomeGen() ? "error.noProjectBlock" : "error.noProjectActivity";
			String noBlockErrorText = project.getIncomeGen() ? "This project must contain at least one block" : "This project must contain at least one activity";
			String noTableError = project.getIncomeGen() ? "error.blockNoTable" : "error.activityNoTable";
			if (project.getBlocks().size()==0) {
				errors.rejectValue("blocks", noBlockError, noBlockErrorText);
			} else { // check block's chronology, prod pattern and tables
				for (Block block : project.getBlocks()) {
					if (project.getIncomeGen() && block.getChrons().size()==0) {
						errors.reject("error.block.noChronology", 
								new Object[] {block.getDescription()}, "\""+block.getDescription()+"\": specify chronology");
					}
					if (block.getPatterns().size()==0) {
						errors.reject(project.getIncomeGen() ? "error.block.noPattern" : "error.activity.noPattern", 
								new Object[] {block.getDescription()}, "\""+block.getDescription()+"\": specify production/activity pattern");
					}
					
					if (block.getIncomes().size()==0) {
						if (project.getIncomeGen()) {
							ValidateUtils.rejectBlockEmptyTable(block.getDescription(), "Income", "projectBlockIncome", noTableError, errors);
						} else {
							ValidateUtils.rejectBlockEmptyTable(block.getDescription(), "User charge", "projectActivityCharge", noTableError, errors);
						}
					}
					if (block.getInputs().size()==0)
						ValidateUtils.rejectBlockEmptyTable(block.getDescription(), "Input", "projectBlockInput", noTableError, errors);
					if (block.getLabours().size()==0)
						ValidateUtils.rejectBlockEmptyTable(block.getDescription(), "Labour", "projectBlockLabour", noTableError, errors);					
				}
				for (BlockWithout block : project.getBlocksWithout()) {
					if (project.getIncomeGen() && block.getChrons().size()==0) {
						errors.reject("error.block.noChronology", 
								new Object[] {block.getDescription()}, "\""+block.getDescription()+"\": specify chronology");
					}
					if (block.getPatterns().size()==0) {
						errors.reject(project.getIncomeGen() ? "error.block.noPattern" : "error.activity.noPattern", 
								new Object[] {block.getDescription()}, "\""+block.getDescription()+"\": specify production/activity pattern");
					}
					if (block.getIncomes().size()==0) {
						if (project.getIncomeGen())
							ValidateUtils.rejectBlockEmptyTable(block.getDescription(), "Income", "projectBlockIncome", noTableError, errors);
						else
							ValidateUtils.rejectBlockEmptyTable(block.getDescription(), "User charge", "projectActivityCharge", noTableError, errors);
					}
					if (block.getInputs().size()==0)
						ValidateUtils.rejectBlockEmptyTable(block.getDescription(), "Input", "projectBlockInput", noTableError, errors);
					if (block.getLabours().size()==0)
						ValidateUtils.rejectBlockEmptyTable(block.getDescription(), "Labour", "projectBlockLabour", noTableError, errors);					
				}
			}
			break;
		case 10:
//			if (!project.getIncomeGen()) {
//				if (project.getContributions().size()==0) ValidateUtils.rejectEmptyTable("contributions","projectContribution",errors);
//			}
			break;
		case 11:
			if (project.getIncomeGen()) {
				ValidateUtils.rejectIfEmptyOrNegative(project, "loan1Interest", "project.loan.interest", errors);
				ValidateUtils.rejectIfEmptyOrNegative(project, "loan1Duration", "project.loan.duration", errors);
				ValidateUtils.rejectIfEmptyOrNegative(project, "loan1GraceCapital", "project.loan.graceCapital", errors);
				ValidateUtils.rejectIfEmptyOrNegative(project, "loan1GraceInterest", "project.loan.graceInterest", errors);
				ValidateUtils.rejectIfEmptyOrNegative(project, "loan2Amt", "project.loan.amount", errors);
				ValidateUtils.rejectIfEmptyOrNegative(project, "loan2Interest", "project.loan.interest", errors);
				ValidateUtils.rejectIfEmptyOrNegative(project, "loan2Duration", "project.loan.duration", errors);
				ValidateUtils.rejectIfEmptyOrNegative(project, "loan2GraceCapital", "project.loan.graceCapital", errors);
				ValidateUtils.rejectIfEmptyOrNegative(project, "loan2GraceInterest", "project.loan.graceInterest", errors);
				ValidateUtils.rejectIfZeroOrNegative(project, "loan2InitPeriod", "project.loan2InitPeriod", errors);
				ValidateUtils.rejectIfEmptyOrNegative(project, "capitalInterest", "project.capitalInterest", errors);
				ValidateUtils.rejectIfEmptyOrNegative(project, "capitalDonate", "project.capitalDonate", errors);
				ValidateUtils.rejectIfEmptyOrNegative(project, "capitalOwn", "project.capitalOwn", errors);
				ValidateUtils.rejectIfEmptyOrNegativeOrOverMax(project, "loan1PaymentsPerYear", "project.loan.paymentsPerYear", 12.0, errors);
				ValidateUtils.rejectIfEmptyOrNegativeOrOverMax(project, "loan2PaymentsPerYear", "project.loan.paymentsPerYear", 12.0, errors);
				
				if (project.getLoan1GraceInterest()!=null && project.getLoan1GraceCapital()!=null
						&& project.getLoan1GraceInterest()>project.getLoan1GraceCapital()) {
					errors.rejectValue("loan1GraceInterest", "error.graceInterestGreaterThanCapital", "The grace period for the interest on a loan cannot be longer than the grace period for the capital.");
				}
				if (project.getLoan2GraceInterest()!=null && project.getLoan2GraceCapital()!=null
						&& project.getLoan2GraceInterest()>project.getLoan2GraceCapital()) {
					errors.rejectValue("loan2GraceInterest", "error.graceInterestGreaterThanCapital", "The grace period for the interest on a loan cannot be longer than the grace period for the capital.");
				}
				
				if (!errors.hasFieldErrors("loan1PaymentsPerYear")) {
					ValidateUtils.rejectIfZeroOrNegative(project, "loan1PaymentsPerYear", "project.loan.paymentsPerYear", errors);
				}
				if (!errors.hasFieldErrors("loan2PaymentsPerYear")) {
					ValidateUtils.rejectIfZeroOrNegative(project, "loan2PaymentsPerYear", "project.loan.paymentsPerYear", errors);
				}
				
				// calculated values
				ValidateUtils.rejectIfEmptyOrNegative(project, "loan1Amt", "project.loan.amount", errors);
				
				if (!errors.hasFieldErrors("loan1Duration")) { 
					if (project.getLoan1Duration()>project.getDuration()) {
							errors.rejectValue("loan1Duration", "error.loanTooLong", "The loan cannot extend beyond the end of the project");
					} 
					if (!errors.hasFieldErrors("loan1GraceCapital") && project.getLoan1GraceCapital()>project.getDuration()) {
						errors.rejectValue("loan1GraceCapital", "error.loan.pastProjectDuration", "Cannot exceed project duration");
					}
					if (!errors.hasFieldErrors("loan1GraceInterest") && project.getLoan1GraceInterest()>project.getDuration()) {
						errors.rejectValue("loan1GraceInterest", "error.loan.pastProjectDuration", "Cannot exceed project duration");
					}
				}
				
				if (!errors.hasFieldErrors("loan2Duration")) { 
					if (project.getLoan2Duration()+project.getLoan2InitPeriod()-1>project.getDuration()) {
							errors.rejectValue("loan2Duration", "error.loanTooLong", "The loan cannot extend beyond the end of the project");	
					}
					if (!errors.hasFieldErrors("loan2GraceCapital") && project.getLoan2GraceCapital()+project.getLoan2InitPeriod()-1>project.getDuration()) {
						errors.rejectValue("loan2GraceCapital", "error.loan.pastProjectDuration", "Cannot exceed project duration");
					}
					if (!errors.hasFieldErrors("loan2GraceInterest") && project.getLoan2GraceInterest()+project.getLoan2InitPeriod()-1>project.getDuration()) {
						errors.rejectValue("loan2GraceInterest", "error.loan.pastProjectDuration", "Cannot exceed project duration");
					}
				}
				
				if (!errors.hasErrors()) {
					// calculate the working capital fields and validate them
					FinanceMatrix matrix = new FinanceMatrix(project, rivConfig.getSetting().getDiscountRate(), rivConfig.getSetting().getDecimalLength());
					project.setWcFinancePeriod(matrix.getWcPeriod());
					project.setWcAmountRequired(matrix.getWcValue());
					ValidateUtils.rejectIfNegative(project, "wcAmountRequired", "project.amtRequired", errors);
					ValidateUtils.rejectIfNegative(project, "wcAmountFinanced", "project.amtFinanced", errors, rivConfig.getSetting().getDecimalLength());
					ValidateUtils.rejectIfNegative(project, "wcFinancePeriod", "project.period", errors);
				}
			}
			break;
		}
	}
}
