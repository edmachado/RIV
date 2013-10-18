package riv.web.controller;
 
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import riv.objects.profile.Profile;
import riv.objects.profile.ProfileProduct;
import riv.objects.profile.ProfileProductBase;
import riv.objects.profile.ProfileResult;
import riv.objects.project.Block;
import riv.objects.project.BlockBase;
import riv.objects.project.Project;
import riv.objects.project.ProjectResult;
import riv.util.ExcelWrapper;
import riv.web.service.DataService;
import riv.web.service.ExcelWorksheetBuilder;
 
@Controller
@RequestMapping({"/report"})
public class ExcelReportController {
	@Autowired
	private DataService dataService;
	@Autowired
	private ExcelWorksheetBuilder ewb;
	
	@RequestMapping(value="/{id}/projectSummary.xlsx", method=RequestMethod.GET)
	public void projectSummary(@PathVariable int id, HttpServletResponse response) throws IOException {
		Project p = dataService.getProject(id, -1);
		ProjectResult pr = dataService.getProjectResult(id);
		ExcelWrapper report = ewb.create();
		ewb.getProjectSummary(report, p, pr);
		response.setHeader("Content-disposition", "attachment; filename=projectSummary.xlsx");
		report.getWorkbook().write(response.getOutputStream());
		report.getWorkbook().dispose();
	  }
	
	@RequestMapping(value="/{id}/projectDescription.xlsx", method=RequestMethod.GET)
	public void projectDescription(@PathVariable int id, HttpServletResponse response) throws IOException {
		Project p = dataService.getProject(id, 1);
		ExcelWrapper report = ewb.create();
		ewb.getProjectGeneralDescription(report, p);
		response.setHeader("Content-disposition", "attachment; filename=projectDescription.xlsx");
		report.getWorkbook().write(response.getOutputStream());
		report.getWorkbook().dispose();
	}
	
	@RequestMapping(value="{id}/projectInvestDetail.xlsx", method=RequestMethod.GET)
	public void projectInvestDetail(@PathVariable int id, @RequestParam(required=false) String template, HttpServletResponse response) throws IOException {
		Project p = dataService.getProject(id, 7);
		ExcelWrapper report = ewb.create();
		ewb.getProjectInvestmentDetail(report, p, false, template!=null);
		if (p.isWithWithout()) {
			ewb.getProjectInvestmentDetail(report, p, true, template!=null);
		}
		response.setHeader("Content-disposition", "attachment; filename=projectInvestDetail.xlsx");
		report.getWorkbook().write(response.getOutputStream());
		report.getWorkbook().dispose();
	}
	
	@RequestMapping(value="{id}/projectGeneralDetail.xlsx", method=RequestMethod.GET)
	public void projectGeneralDetail(@PathVariable int id, @RequestParam(required=false) String template, HttpServletResponse response) throws IOException {
		Project p = dataService.getProject(id, 8);
		ExcelWrapper report = ewb.create();
		if (p.getIncomeGen()) {
			ewb.getProjectGeneralDetail(report, p, template!=null);
		} else {
			ewb.getProjectGeneralDetailNongen(report, p, template!=null);
		}
		response.setHeader("Content-disposition", "attachment; filename=projectGeneralDetail.xlsx");
		report.getWorkbook().write(response.getOutputStream());
		report.getWorkbook().dispose();
	}
	
	@RequestMapping(value="{id}/projectProduction.xlsx", method=RequestMethod.GET)
	public void projectProduction(@PathVariable int id, HttpServletResponse response) throws IOException {
		Project p = dataService.getProject(id, 9);
		ExcelWrapper report = ewb.create();
		ewb.getProjectProduction(report, p);
		response.setHeader("Content-disposition", "attachment; filename=projectProduction.xlsx");
		report.getWorkbook().write(response.getOutputStream());
		report.getWorkbook().dispose();
	}
	
	@RequestMapping(value="{id}/projectContributions.xlsx", method=RequestMethod.GET)
	public void projectContributions(@PathVariable int id, @RequestParam(required=false) String template, HttpServletResponse response) throws IOException {
		Project p = dataService.getProject(id, 10);
		ExcelWrapper report = ewb.create();
		ewb.getProjectContributions(report, p, template!=null);
		response.setHeader("Content-disposition", "attachment; filename=projectContributions.xlsx");
		report.getWorkbook().write(response.getOutputStream());
		report.getWorkbook().dispose();
	}
	
	@RequestMapping(value="{id}/projectChronology.xlsx", method=RequestMethod.GET)
	public void projectChronology(@PathVariable int id, HttpServletResponse response) throws IOException {
		Project p = dataService.getProject(id, 9);
		ExcelWrapper report = ewb.create();
		ewb.getProjectChronology(report, p);
		response.setHeader("Content-disposition", "attachment; filename=projectChronology.xlsx");
		report.getWorkbook().write(response.getOutputStream());
		report.getWorkbook().dispose();
	}
	
	@RequestMapping(value="{id}/projectBlocks.xlsx", method=RequestMethod.GET)
	public void projectBlocks(@PathVariable int id, HttpServletResponse response) throws IOException {
		Project p = dataService.getProject(id, 9);
		ExcelWrapper report = ewb.create();
		ewb.getBlocks(report, p, false);
		if (p.isWithWithout()) {
			ewb.getBlocks(report, p, true);
		}
		response.setHeader("Content-disposition", "attachment; filename=projectBlocks.xlsx");
		report.getWorkbook().write(response.getOutputStream());
		report.getWorkbook().dispose();
	}
	
	@RequestMapping(value="{id}/projectBlock.xlsx", method=RequestMethod.GET)
	public void oneBlock(@PathVariable int id, @RequestParam(required=false) String template, HttpServletResponse response) throws IOException {
		BlockBase block = template!=null ? new Block() : dataService.getBlock(id, "all");
		boolean isIg = template!=null ? Boolean.parseBoolean(template) : block.getProject().getIncomeGen();
		ExcelWrapper report = ewb.create();
		ewb.getBlock(report, block, isIg);
		response.setHeader("Content-disposition", "attachment; filename=block.xlsx");
		report.getWorkbook().write(response.getOutputStream());
		report.getWorkbook().dispose();
	}
	
	
	
	@RequestMapping(value="{id}/projectParameters.xlsx", method=RequestMethod.GET)
	public void projectParameters(@PathVariable int id, HttpServletResponse response) throws IOException {
		Project p = dataService.getProject(id, -1);
		ExcelWrapper report = ewb.create();
		ewb.getProjectParameters(report, p);
		response.setHeader("Content-disposition", "attachment; filename=projectParameters.xlsx");
		report.getWorkbook().write(response.getOutputStream());
		report.getWorkbook().dispose();
	}
	
	@RequestMapping(value="{id}/projectCashFlow.xlsx", method=RequestMethod.GET)
	public void projectCashFlow(@PathVariable int id, HttpServletResponse response) throws IOException {
		Project p = dataService.getProject(id, -1);
		ProjectResult pr = dataService.getProjectResult(id);
		ExcelWrapper report = ewb.create();
		ewb.getProjectCashFlow(report, p, pr);
		response.setHeader("Content-disposition", "attachment; filename=projectCashFlow.xlsx");
		report.getWorkbook().write(response.getOutputStream());
		report.getWorkbook().dispose();
	}
	
	@RequestMapping(value="{id}/projectCashFlowNongen.xlsx", method=RequestMethod.GET)
	public void projectCashFlowNongen(@PathVariable int id, HttpServletResponse response) throws IOException {
		Project p = dataService.getProject(id, -1);
		ProjectResult pr = dataService.getProjectResult(id);
		ExcelWrapper report = ewb.create();
		ewb.getProjectSustainability(report, p, pr);
		response.setHeader("Content-disposition", "attachment; filename=projectCashFlowNongen.xlsx");
		report.getWorkbook().write(response.getOutputStream());
		report.getWorkbook().dispose();
	}
	
	@RequestMapping(value="{id}/projectCashFlowFirst.xlsx", method=RequestMethod.GET)
	public void projectCashFlowFirst(@PathVariable int id, HttpServletResponse response) throws IOException {
		Project p = dataService.getProject(id, -1);
		ProjectResult pr = dataService.getProjectResult(id);
		ExcelWrapper report = ewb.create();
		ewb.getProjectCashFlowFirst(report, p, pr);
		response.setHeader("Content-disposition", "attachment; filename=projectCashFlowFirst.xlsx");
		report.getWorkbook().write(response.getOutputStream());
		report.getWorkbook().dispose();
	}
	
	@RequestMapping(value="{id}/projectProfitability.xlsx", method=RequestMethod.GET)
	public void projectProfitability(@PathVariable int id, HttpServletResponse response) throws IOException {
		Project p = dataService.getProject(id, -1);
		ProjectResult pr = dataService.getProjectResult(id);
		ExcelWrapper report = ewb.create();
		ewb.getProjectProfitability(report, p, pr);
		response.setHeader("Content-disposition", "attachment; filename=projectProfitability.xlsx");
		report.getWorkbook().write(response.getOutputStream());
		report.getWorkbook().dispose();
	}
	
	@RequestMapping(value="/{id}/projectRecommendation.xlsx", method=RequestMethod.GET)
	   public void projectRecommendation(@PathVariable int id, HttpServletResponse response) throws IOException {
		   Project p = dataService.getProject(id, 12);
		   ExcelWrapper report = ewb.create();
			ewb.getProjectRecommendation(report, p);
			response.setHeader("Content-disposition", "attachment; filename=projectRecommendation.xlsx");
			report.getWorkbook().write(response.getOutputStream());
			report.getWorkbook().dispose();
	   }
	
	@RequestMapping(value="/{id}/projectComplete.xlsx", method=RequestMethod.GET)
	   public void completeProjectReport(@PathVariable int id, HttpServletResponse response) throws Exception {
		   Project project = dataService.getProject(id, -1);
		   ProjectResult result = dataService.getProjectResult(id);
		   ExcelWrapper report = ewb.create();
		   report.setCompleteReport(true);
		   
		   Sheet summary = ewb.getProjectSummary(report, project, result);
		   ewb.getProjectGeneralDescription(report, project);
		   ewb.getProjectInvestmentDetail(report, project, false, false);
		   if (project.isWithWithout()) {
			   ewb.getProjectInvestmentDetail(report, project, true, false);
		   }
		  if (project.getIncomeGen()) {
		   ewb.getProjectGeneralDetail(report, project, false);
		  } else {
			  ewb.getProjectGeneralDetailNongen(report, project, false);
		  }
		   ewb.getProjectProduction(report, project);
			
		   if (project.getIncomeGen()) {
			   ewb.getProjectChronology(report, project);
			   ewb.getBlocks(report, project, false);
			   if (project.isWithWithout()) {
				   ewb.getBlocks(report, project, true);
			   }
			   ewb.getProjectParameters(report, project);
			   ewb.getProjectCashFlowFirst(report, project, result);
			   ewb.getProjectCashFlow(report, project, result);
			   ewb.getProjectProfitability(report, project, result);
		   } else {
			   ewb.getBlocks(report, project, false);
			   ewb.getProjectContributions(report, project, false);
			   ewb.getProjectSustainability(report, project, result);
		   }
		   ewb.getProjectRecommendation(report, project);
		   summary.setSelected(true);
		   
		   response.setHeader("Content-disposition", "attachment; filename=projectComplete.xlsx");
		   report.getWorkbook().write(response.getOutputStream());
		   report.getWorkbook().dispose();
	   }
	
   @RequestMapping(value="/{id}/profileSummary.xlsx", method=RequestMethod.GET)
   public void profileSummary(@PathVariable int id, HttpServletResponse response) throws IOException  {
	   Profile p = dataService.getProfile(id, 1);
	   ExcelWrapper report = ewb.create();
	   ewb.getProfileSummary(report, p);
	   response.setHeader("Content-disposition", "attachment; filename=profileSummary.xlsx");
	   report.getWorkbook().write(response.getOutputStream());
		report.getWorkbook().dispose();
   }
   
   @RequestMapping(value="/{id}/profileInvest.xlsx", method=RequestMethod.GET)
   public void profileInvest(@PathVariable int id, @RequestParam(required=false) String template, HttpServletResponse response) throws IOException  {
	   Profile p = dataService.getProfile(id, 4);
	   ExcelWrapper report = ewb.create();
	   ewb.getProfileInvest(report, p, template!=null);
	   response.setHeader("Content-disposition", "attachment; filename=profileInvest.xlsx");
	   report.getWorkbook().write(response.getOutputStream());
		report.getWorkbook().dispose();
   }
   
   @RequestMapping(value="/{id}/profileGeneral.xlsx", method=RequestMethod.GET)
   public void profileGeneral(@PathVariable int id, @RequestParam(required=false) String template, HttpServletResponse response) throws IOException  {
	   Profile p = dataService.getProfile(id, 5);
	   ExcelWrapper report = ewb.create();
	   ewb.getProfileGeneral(report, p, template!=null);
	   response.setHeader("Content-disposition", "attachment; filename=profileGeneral.xlsx");
	   report.getWorkbook().write(response.getOutputStream());
		report.getWorkbook().dispose();
   }
   
   @RequestMapping(value="/{id}/profileProducts.xlsx", method=RequestMethod.GET)
   public void profileProducts(@PathVariable int id, @RequestParam(required=false) String template, HttpServletResponse response) throws IOException  {
	   Profile p = dataService.getProfile(id, 6);
	   ExcelWrapper report = ewb.create();
	   ewb.getProfileProducts(report, p);
	   response.setHeader("Content-disposition", "attachment; filename=profileProducts.xlsx");
	   report.getWorkbook().write(response.getOutputStream());
		report.getWorkbook().dispose();
   }
   
   @RequestMapping(value="{id}/profileProduct.xlsx", method=RequestMethod.GET)
	public void oneProduct(@PathVariable int id, @RequestParam(required=false) String template, HttpServletResponse response) throws IOException {
		ProfileProductBase p = template!=null ? new ProfileProduct() : dataService.getProfileProduct(id, "all");
		boolean isIg = template!=null ? Boolean.parseBoolean(template) :p.getProfile().getIncomeGen();
		ExcelWrapper report = ewb.create();
		ewb.getProduct(report, p, isIg);
		response.setHeader("Content-disposition", "attachment; filename=profileProduct.xlsx");
		report.getWorkbook().write(response.getOutputStream());
		report.getWorkbook().dispose();
	}
   
   @RequestMapping(value="/{id}/profilePrelimAnalysis.xlsx", method=RequestMethod.GET)
   public void profilePrelimAnalysis(@PathVariable int id, HttpServletResponse response) throws IOException  {
	   ProfileResult pr = dataService.getProfileResult(id);
	   ExcelWrapper report = ewb.create();
	   ewb.getProfilePrelimAnalysis(report, pr);
	   response.setHeader("Content-disposition", "attachment; filename=profilePrelimAnalysis.xlsx");
	   report.getWorkbook().write(response.getOutputStream());
		report.getWorkbook().dispose();
   }
   
   @RequestMapping(value="/{id}/profileRecommendation.xlsx", method=RequestMethod.GET)
   public void profileRecommendation(@PathVariable int id, HttpServletResponse response) throws IOException {
	   Profile p = dataService.getProfile(id, 8);
	   ExcelWrapper report = ewb.create();
	   ewb.getProfileRecommendation(report, p);
	   response.setHeader("Content-disposition", "attachment; filename=profileRecommendation.xlsx");
	   report.getWorkbook().write(response.getOutputStream());
		report.getWorkbook().dispose();
   }
   
   @RequestMapping(value="/{id}/profileComplete.xlsx", method=RequestMethod.GET)
   public void completeReport(@PathVariable int id, HttpServletResponse response) throws Exception {
	   Profile p = dataService.getProfile(id, -1);
	   ProfileResult pr = dataService.getProfileResult(id);
	   ExcelWrapper report = ewb.create();
	   report.setCompleteReport(true);
	   ewb.getProfileSummary(report, p);
	   ewb.getProfileInvest(report, p, false);
	   ewb.getProfileGeneral(report, p, false);
	   ewb.getProfileProducts(report, p);
	   ewb.getProfilePrelimAnalysis(report, pr);
	   ewb.getProfileRecommendation(report, p);
	   response.setHeader("Content-disposition", "attachment; filename=profileComplete.xlsx");
	   report.getWorkbook().write(response.getOutputStream());
	   report.getWorkbook().dispose();
   }
}
