package riv.web.controller;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import riv.objects.FinanceMatrix;
import riv.objects.FinanceMatrix.ProjectScenario;
import riv.objects.ProfileMatrix;
import riv.objects.profile.Profile;
import riv.objects.profile.ProfileProduct;
import riv.objects.profile.ProfileProductBase;
import riv.objects.project.Block;
import riv.objects.project.BlockBase;
import riv.objects.project.Project;
import riv.objects.project.ProjectResult;
import riv.util.ExcelWrapper;
import riv.util.RivRuntimeException;
import riv.web.config.RivConfig;
import riv.web.service.DataService;
import riv.web.service.ExcelWorksheetBuilder;

@Controller
@RequestMapping({ "/report" })
public class ExcelReportController {
	@Autowired
	private DataService dataService;
	@Autowired
	private ExcelWorksheetBuilder ewb;
	@Autowired
	private RivConfig rivConfig;

	@RequestMapping(value = "/{id}/projectSummary.xlsx", method = RequestMethod.GET)
	public void projectSummary(@PathVariable int id, HttpServletResponse response) {
		Project p = dataService.getProject(id, -1);
		ProjectResult pr = dataService.getProjectResult(id);
		ExcelWrapper report = ewb.create();
		try {
			ewb.projectSummary(report, p, pr);
			response.setHeader("Content-disposition", "attachment; filename=projectSummary.xlsx");
			report.getWorkbook().write(response.getOutputStream());
		} catch (Exception e) {
			throw new RivRuntimeException("Error creating report.",e);
		} finally {
			report.getWorkbook().dispose();
		}
	}

	@RequestMapping(value = "/{id}/projectDescription.xlsx", method = RequestMethod.GET)
	public void projectDescription(@PathVariable int id, HttpServletResponse response) {
		Project p = dataService.getProject(id, 1);
		ExcelWrapper report = ewb.create();
		
		try {
			ewb.projectGeneralDescription(report, p);
			response.setHeader("Content-disposition", "attachment; filename=projectDescription.xlsx");
			report.getWorkbook().write(response.getOutputStream());
		} catch (Exception e) {
			throw new RivRuntimeException("Error creating report.",e);
		} finally {
			report.getWorkbook().dispose();
		}
	}

	@RequestMapping(value = "{id}/projectInvestDetail.xlsx", method = RequestMethod.GET)
	public void projectInvestDetail(@PathVariable int id, @RequestParam(required = false) String template, HttpServletResponse response) {
		Project p = dataService.getProject(id, 7);
		ExcelWrapper report = ewb.create();
		
		try {	
			ewb.projectInvestmentDetail(report, p, false, template != null);
			if (p.isWithWithout()) {
				ewb.projectInvestmentDetail(report, p, true, template != null);
			}
			response.setHeader("Content-disposition", "attachment; filename=projectInvestDetail.xlsx");
			report.getWorkbook().write(response.getOutputStream());
		} catch (Exception e) {
			throw new RivRuntimeException("Error creating report.",e);
		} finally {
			report.getWorkbook().dispose();
		}
	}

	@RequestMapping(value = "{id}/projectGeneralDetail.xlsx", method = RequestMethod.GET)
	public void projectGeneralDetail(@PathVariable int id, @RequestParam(required = false) String template, HttpServletResponse response) {
		Project p = dataService.getProject(id, 8);
		ExcelWrapper report = ewb.create();
		
		try {
			if (p.getIncomeGen()) {
				ewb.projectGeneralDetail(report, p, false, template != null);
				if (p.isWithWithout()) {
					ewb.projectGeneralDetail(report, p, true, template != null);
				}
			} else {
				ewb.projectGeneralDetailNongen(report, p, template != null);
			}
			
			response.setHeader("Content-disposition", "attachment; filename=projectGeneralDetail.xlsx");
			
			report.getWorkbook().write(response.getOutputStream());
		} catch (Exception e) {
			throw new RivRuntimeException("Error creating report.",e);
		} finally {
			report.getWorkbook().dispose();
		}
	}

	@RequestMapping(value = "{id}/projectProduction.xlsx", method = RequestMethod.GET)
	public void projectProduction(@PathVariable int id, HttpServletResponse response) {
		Project p = dataService.getProject(id, 9);
		ExcelWrapper report = ewb.create();
		try {
			ewb.projectProduction(report, p);

			response.setHeader("Content-disposition", "attachment; filename=projectProduction.xlsx");
			report.getWorkbook().write(response.getOutputStream());
		} catch (Exception e) {
			throw new RivRuntimeException("Error creating report.",e);
		} finally {
			report.getWorkbook().dispose();
		}
	}

	@RequestMapping(value = "{id}/projectContributions.xlsx", method = RequestMethod.GET)
	public void projectContributions(@PathVariable int id, @RequestParam(required = false) String template, HttpServletResponse response) {
		Project p = dataService.getProject(id, 10);
		ExcelWrapper report = ewb.create();
		try {
			ewb.projectContributions(report, p, template != null);
			response.setHeader("Content-disposition",
					"attachment; filename=projectContributions.xlsx");
			report.getWorkbook().write(response.getOutputStream());
		} catch (Exception e) {
			throw new RivRuntimeException("Error creating report.",e);
		} finally {
			report.getWorkbook().dispose();
		}
	}

	@RequestMapping(value = "{id}/projectChronology.xlsx", method = RequestMethod.GET)
	public void projectChronology(@PathVariable int id, HttpServletResponse response) {
		Project p = dataService.getProject(id, 9);
		ExcelWrapper report = ewb.create();
		try {
			ewb.projectChronology(report, p);
			response.setHeader("Content-disposition",
					"attachment; filename=projectChronology.xlsx");
			report.getWorkbook().write(response.getOutputStream());
		} catch (Exception e) {
			throw new RivRuntimeException("Error creating report.",e);
		} finally {
			report.getWorkbook().dispose();
		}
	}

	@RequestMapping(value = "{id}/projectBlocks.xlsx", method = RequestMethod.GET)
	public void projectBlocks(@PathVariable int id, HttpServletResponse response) {
		Project p = dataService.getProject(id, 9);
		ExcelWrapper report = ewb.create();
		try {
			ewb.blocks(report, p, false);
			if (p.isWithWithout()) {
				ewb.blocks(report, p, true);
			}
			response.setHeader("Content-disposition",
					"attachment; filename=projectBlocks.xlsx");
			report.getWorkbook().write(response.getOutputStream());
		} catch (Exception e) {
			throw new RivRuntimeException("Error creating report.",e);
		} finally {
			report.getWorkbook().dispose();
		}
	}

	@RequestMapping(value = "{id}/projectBlock.xlsx", method = RequestMethod.GET)
	public void oneBlock(@PathVariable int id, @RequestParam(required = false) String template, HttpServletResponse response) {
		BlockBase block = template != null ? new Block() : dataService
				.getBlock(id, "all");
		boolean isIg = template != null ? Boolean.parseBoolean(template)
				: block.getProject().getIncomeGen();
		ExcelWrapper report = ewb.create();
		try {
			ewb.block(report, block, isIg);
			response.setHeader("Content-disposition",
					"attachment; filename=block.xlsx");
			report.getWorkbook().write(response.getOutputStream());
		} catch (Exception e) {
			throw new RivRuntimeException("Error creating report.",e);
		} finally {
			report.getWorkbook().dispose();
		}
	}

	@RequestMapping(value = "{id}/projectParameters.xlsx", method = RequestMethod.GET)
	public void projectParameters(@PathVariable int id, HttpServletResponse response) {
		Project p = dataService.getProject(id, -1);
		ProjectResult pr = dataService.getProjectResult(id);
		ExcelWrapper report = ewb.create();
		try {
			ewb.projectParameters(report, p, pr);
			response.setHeader("Content-disposition",
					"attachment; filename=projectParameters.xlsx");
			report.getWorkbook().write(response.getOutputStream());
		} catch (Exception e) {
			throw new RivRuntimeException("Error creating report.",e);
		} finally {
			report.getWorkbook().dispose();
		}
	}
	
	@RequestMapping(value = "{id}/projectAmortization.xlsx", method = RequestMethod.GET)
	public void projectAmortization(@PathVariable int id, HttpServletResponse response) {
		Project p = dataService.getProject(id, -1);
		FinanceMatrix matrix = new FinanceMatrix(p, rivConfig.getSetting().getDiscountRate(), rivConfig.getSetting().getDecimalLength());
		ExcelWrapper report = ewb.create();
		try {
			ewb.projectAmortization(report, p, matrix, true);
			ewb.projectAmortization(report, p, matrix, false);
			response.setHeader("Content-disposition",
					"attachment; filename=projectAmortization.xlsx");
			report.getWorkbook().write(response.getOutputStream());
		} catch (Exception e) {
			throw new RivRuntimeException("Error creating report.",e);
		} finally {
			report.getWorkbook().dispose();
		}
	}

	@RequestMapping(value = "{id}/projectCashFlow.xlsx", method = RequestMethod.GET)
	public void projectCashFlow(@PathVariable int id, HttpServletResponse response) {
		Project p = dataService.getProject(id, -1);
		FinanceMatrix matrix = new FinanceMatrix(p, rivConfig.getSetting().getDiscountRate(), rivConfig.getSetting().getDecimalLength());
		ExcelWrapper report = ewb.create();
		try {
			ewb.projectCashFlow(report, p, matrix, false);
			if (p.isWithWithout()) {
				ewb.projectCashFlow(report, p, matrix, true);
			}
			response.setHeader("Content-disposition",
					"attachment; filename=projectCashFlow.xlsx");
			report.getWorkbook().write(response.getOutputStream());
		} catch (Exception e) {
			throw new RivRuntimeException("Error creating report.",e);
		} finally {
			report.getWorkbook().dispose();
		}
	}

	@RequestMapping(value = "{id}/projectCashFlowNongen.xlsx", method = RequestMethod.GET)
	public void projectCashFlowNongen(@PathVariable int id, HttpServletResponse response) {
		Project p = dataService.getProject(id, -1);
		ProjectResult pr = dataService.getProjectResult(id);
		ExcelWrapper report = ewb.create();
		try {
			ewb.projectSustainability(report, p, pr);
			response.setHeader("Content-disposition",
					"attachment; filename=projectCashFlowNongen.xlsx");
			report.getWorkbook().write(response.getOutputStream());
		} catch (Exception e) {
			throw new RivRuntimeException("Error creating report.",e);
		} finally {
			report.getWorkbook().dispose();
		}
	}

	@RequestMapping(value = "{id}/projectWorkingCapital.xlsx", method = RequestMethod.GET)
	public void projectWorkingCapital(@PathVariable int id, HttpServletResponse response) {
		Project p = dataService.getProject(id, -1);
		ProjectResult pr = dataService.getProjectResult(id);
		ExcelWrapper report = ewb.create();
		try {
			ewb.projectWorkingCapital(report, p, pr, false, rivConfig.getSetting().getDecimalLength());
			if (p.isWithWithout()) {
				ewb.projectWorkingCapital(report, p, pr, true, rivConfig.getSetting().getDecimalLength());
			}
			response.setHeader("Content-disposition", "attachment; filename=projectWorkingCapital.xlsx");
			report.getWorkbook().write(response.getOutputStream());
		} catch (Exception e) {
			throw new RivRuntimeException("Error creating report.",e);
		} finally {
			report.getWorkbook().dispose();
		}
	}

	@RequestMapping(value = "{id}/projectProfitability.xlsx", method = RequestMethod.GET)
	public void projectProfitability(@PathVariable int id, HttpServletResponse response) {
		Project p = dataService.getProject(id, -1);
		ProjectResult pr = dataService.getProjectResult(id);
		FinanceMatrix matrix = new FinanceMatrix(p, rivConfig.getSetting().getDiscountRate(), rivConfig.getSetting().getDecimalLength());
		ExcelWrapper report = ewb.create();
		try {
			ewb.projectProfitability(report, p, pr, matrix, ProjectScenario.With);
			if (p.isWithWithout()) {
				ewb.projectProfitability(report, p, pr, matrix, ProjectScenario.Without);
				ewb.projectProfitability(report, p, pr, matrix, ProjectScenario.Incremental);
			}
			response.setHeader("Content-disposition", "attachment; filename=projectProfitability.xlsx");
			report.getWorkbook().write(response.getOutputStream());
		} catch (Exception e) {
			throw new RivRuntimeException("Error creating report.",e);
		} finally {
			report.getWorkbook().dispose();
		}
	}

	@RequestMapping(value = "/{id}/projectRecommendation.xlsx", method = RequestMethod.GET)
	public void projectRecommendation(@PathVariable int id, HttpServletResponse response) {
		Project p = dataService.getProject(id, 12);
		ExcelWrapper report = ewb.create();
		try {
			ewb.projectRecommendation(report, p);
			response.setHeader("Content-disposition", "attachment; filename=projectRecommendation.xlsx");
			report.getWorkbook().write(response.getOutputStream());
		} catch (Exception e) {
			throw new RivRuntimeException("Error creating report.",e);
		} finally {
			report.getWorkbook().dispose();
		}
	}

	@RequestMapping(value = "/{id}/projectComplete.xlsx", method = RequestMethod.GET)
	public void completeProjectReport(@PathVariable int id, HttpServletResponse response) {
		Project project = dataService.getProject(id, -1);
		ProjectResult result = dataService.getProjectResult(id);
		 
		FinanceMatrix matrix= project.getIncomeGen() ? new FinanceMatrix(project, rivConfig.getSetting().getDiscountRate(), rivConfig.getSetting().getDecimalLength()) : null;
		ExcelWrapper report = ewb.create();
		try {
			report.setCompleteReport(true);

			Sheet summary = ewb.projectSummary(report, project, result);
			ewb.projectGeneralDescription(report, project);
			ewb.projectInvestmentDetail(report, project, false, false);
			if (project.isWithWithout()) {
				ewb.projectInvestmentDetail(report, project, true, false);
			}
			if (project.getIncomeGen()) {
				ewb.projectGeneralDetail(report, project, false, false);
				if (project.isWithWithout()) {
					ewb.projectGeneralDetail(report, project, true, false);
				}
			} else {
				ewb.projectGeneralDetailNongen(report, project, false);
			}
			ewb.projectProduction(report, project);

			if (project.getIncomeGen()) {
				ewb.projectChronology(report, project);
				ewb.blocks(report, project, false);
				if (project.isWithWithout()) {
					ewb.blocks(report, project, true);
				}
				ewb.projectParameters(report, project, result);
				// working capital
				ewb.projectWorkingCapital(report, project, result, false, rivConfig.getSetting().getDecimalLength());
				if (project.isWithWithout()) {
					ewb.projectWorkingCapital(report, project, result, true, rivConfig.getSetting().getDecimalLength());
				}
				
				ewb.projectAmortization(report, project, matrix, true);
				ewb.projectAmortization(report, project, matrix, false);
				
				ewb.projectCashFlow(report, project, matrix, false);
				if (project.isWithWithout()) {
					ewb.projectCashFlow(report, project, matrix, true);
				}
				ewb.projectProfitability(report, project, result, matrix, ProjectScenario.With);
				if (project.isWithWithout()) {
					ewb.projectProfitability(report, project, result, matrix, ProjectScenario.Without);
					ewb.projectProfitability(report, project, result, matrix, ProjectScenario.Incremental);
				}
			} else {
				ewb.blocks(report, project, false);
				ewb.projectContributions(report, project, false);
				ewb.projectSustainability(report, project, result);
			}
			ewb.projectRecommendation(report, project);
			
			for (int i=1; i<report.getWorkbook().getNumberOfSheets(); i++) {
				report.getWorkbook().getSheetAt(i).setSelected(false);
			}
			summary.setSelected(true);

			response.setHeader("Content-disposition", "attachment; filename=projectComplete.xlsx");
			report.getWorkbook().write(response.getOutputStream());
		} catch (Exception e) {
			throw new RivRuntimeException("Error creating report", e);
		} finally {
			report.getWorkbook().dispose();
		}
	}

	@RequestMapping(value = "/{id}/profileSummary.xlsx", method = RequestMethod.GET)
	public void profileSummary(@PathVariable int id, HttpServletResponse response) {
		Profile p = dataService.getProfile(id, 1);
		ExcelWrapper report = ewb.create();
		try {
			ewb.profileSummary(report, p);
			response.setHeader("Content-disposition", "attachment; filename=profileSummary.xlsx");
			report.getWorkbook().write(response.getOutputStream());
		} catch (Exception e) {
			throw new RivRuntimeException("Error creating report", e);
		} finally {
			report.getWorkbook().dispose();
		}
	}

	@RequestMapping(value = "/{id}/profileInvest.xlsx", method = RequestMethod.GET)
	public void profileInvest(@PathVariable int id, @RequestParam(required = false) String template, HttpServletResponse response) {
		Profile p = dataService.getProfile(id, 4);
		ExcelWrapper report = ewb.create();
		try {
			ewb.profileInvest(report, p, false, template != null);
			if (p.getWithWithout()) {
				ewb.profileInvest(report, p, true, template != null);
			}
			response.setHeader("Content-disposition", "attachment; filename=profileInvest.xlsx");
			report.getWorkbook().write(response.getOutputStream());
		} catch (Exception e) {
			throw new RivRuntimeException("Error creating report", e);
		} finally {
			report.getWorkbook().dispose();
		}
	}

	@RequestMapping(value = "/{id}/profileGeneral.xlsx", method = RequestMethod.GET)
	public void profileGeneral(@PathVariable int id, @RequestParam(required = false) String template, HttpServletResponse response) {
		Profile p = dataService.getProfile(id, 5);
		ExcelWrapper report = ewb.create();
		try {
			ewb.profileGeneral(report, p, template != null);
			response.setHeader("Content-disposition", "attachment; filename=profileGeneral.xlsx");
			report.getWorkbook().write(response.getOutputStream());
		} catch (Exception e) {
			throw new RivRuntimeException("Error creating report", e);
		} finally {
			report.getWorkbook().dispose();
		}
	}

	@RequestMapping(value = "/{id}/profileProducts.xlsx", method = RequestMethod.GET)
	public void profileProducts(@PathVariable int id, @RequestParam(required = false) String template, HttpServletResponse response) {
		Profile p = dataService.getProfile(id, 6);
		ExcelWrapper report = ewb.create();
		try {
			ewb.profileProducts(report, p, false);
			if (p.getWithWithout()) {
				ewb.profileProducts(report, p, true);
			}
			response.setHeader("Content-disposition", "attachment; filename=profileProducts.xlsx");
			report.getWorkbook().write(response.getOutputStream());
		} catch (Exception e) {
			throw new RivRuntimeException("Error creating report", e);
		} finally {
			report.getWorkbook().dispose();
		}
	}

	@RequestMapping(value = "{id}/profileProduct.xlsx", method = RequestMethod.GET)
	public void oneProduct(@PathVariable int id, @RequestParam(required = false) String template, HttpServletResponse response) {
		ProfileProductBase p = template != null ? new ProfileProduct()
				: dataService.getProfileProduct(id, "all");
		boolean isIg = template != null ? Boolean.parseBoolean(template) : p
				.getProfile().getIncomeGen();
		ExcelWrapper report = ewb.create();
		try {
			ewb.profileProduct(report, p, isIg);
			response.setHeader("Content-disposition", "attachment; filename=profileProduct.xlsx");
			report.getWorkbook().write(response.getOutputStream());
		} catch (Exception e) {
			throw new RivRuntimeException("Error creating report", e);
		} finally {
			report.getWorkbook().dispose();
		}
	}

	@RequestMapping(value = "/{id}/profilePrelimAnalysis.xlsx", method = RequestMethod.GET)
	public void profilePrelimAnalysis(@PathVariable int id, HttpServletResponse response) {
		Profile p = dataService.getProfile(id, -1);
		ProfileMatrix matrix = new ProfileMatrix(p);
		ExcelWrapper report = ewb.create();
		try {
			ewb.profilePrelimAnalysis(report, p, ProjectScenario.With, matrix);
			if (p.getWithWithout()) {
				ewb.profilePrelimAnalysis(report, p, ProjectScenario.Without, matrix);
				ewb.profilePrelimAnalysis(report, p, ProjectScenario.Incremental, matrix);
			}
			response.setHeader("Content-disposition", "attachment; filename=profilePrelimAnalysis.xlsx");
			report.getWorkbook().write(response.getOutputStream());
		} catch (Exception e) {
			throw new RivRuntimeException("Error creating report", e);
		} finally {
			report.getWorkbook().dispose();
		}
	}

	@RequestMapping(value = "/{id}/profileRecommendation.xlsx", method = RequestMethod.GET)
	public void profileRecommendation(@PathVariable int id, HttpServletResponse response) {
		Profile p = dataService.getProfile(id, 8);
		ExcelWrapper report = ewb.create();
		try {
			ewb.profileRecommendation(report, p);
			response.setHeader("Content-disposition", "attachment; filename=profileRecommendation.xlsx");
			report.getWorkbook().write(response.getOutputStream());
		} catch (Exception e) {
			throw new RivRuntimeException("Error creating report", e);
		} finally {
			report.getWorkbook().dispose();
		}
	}

	@RequestMapping(value = "/{id}/profileComplete.xlsx", method = RequestMethod.GET)
	public void completeReport(@PathVariable int id, HttpServletResponse response) {
		Profile p = dataService.getProfile(id, -1);
		ProfileMatrix matrix = new ProfileMatrix(p);
		ExcelWrapper report = ewb.create();
		try {
			report.setCompleteReport(true);
			ewb.profileSummary(report, p);
			ewb.profileInvest(report, p, false, false);
			if (p.getWithWithout()) {
				ewb.profileInvest(report, p, true, false);
			}
			ewb.profileGeneral(report, p, false);
			ewb.profileProducts(report, p, false);
			if (p.getWithWithout()) {
				ewb.profileProducts(report, p, true);
			}
			ewb.profilePrelimAnalysis(report, p, ProjectScenario.With, matrix);
			if (p.getWithWithout()) {
				ewb.profilePrelimAnalysis(report, p, ProjectScenario.Without, matrix);
				ewb.profilePrelimAnalysis(report, p, ProjectScenario.Incremental, matrix);
			}
			ewb.profileRecommendation(report, p);
			
			for (int i=1; i<report.getWorkbook().getNumberOfSheets(); i++) {
				report.getWorkbook().getSheetAt(i).setSelected(false);
			}
			
			response.setHeader("Content-disposition", "attachment; filename=profileComplete.xlsx");
			report.getWorkbook().write(response.getOutputStream());
		} catch (Exception e) {
			throw new RivRuntimeException("Error creating report", e);
		} finally {
			report.getWorkbook().dispose();
		}
	}
}