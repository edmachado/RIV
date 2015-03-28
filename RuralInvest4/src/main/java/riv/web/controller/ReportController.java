package riv.web.controller;
 
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperExportManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import riv.objects.FinanceMatrix;
import riv.objects.FinanceMatrix.ProjectScenario;
import riv.objects.profile.Profile;
import riv.objects.profile.ProfileResult;
import riv.objects.project.Project;
import riv.objects.project.ProjectResult;
import riv.util.ReportWrapper;
import riv.web.config.RivConfig;
import riv.web.service.DataService;
import riv.web.service.PdfReportCreator;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;
 
@Controller
@RequestMapping({"/report"})
public class ReportController {
	@Autowired
	private PdfReportCreator reportCreator;
	@Autowired
	private DataService dataService;
	@Autowired
	private RivConfig rivConfig;
	
	public static final String MEDIA_TYPE_PDF = "application/pdf";
	
	@RequestMapping(value="/{id}/projectSummary.pdf", method=RequestMethod.GET)
	public void projectSummary(@PathVariable int id, HttpServletRequest request, HttpServletResponse response) {
		Project p = dataService.getProject(id, -1);
		ReportWrapper report = reportCreator.projectSummary(p, dataService.getProjectResult(id), 0);
		reportCreator.export(response, report);
	}
	
	@RequestMapping(value="/{id}/projectDescription.pdf", method=RequestMethod.GET)
	public void projectDescription(@PathVariable int id, HttpServletRequest request, HttpServletResponse response) {
		Project p = dataService.getProject(id, 1);
		ReportWrapper report = reportCreator.projectDescription(p, 0);
		reportCreator.export(response, report);
	}
	
	@RequestMapping(value="{id}/projectInvestDetail.pdf", method=RequestMethod.GET)
	public void projectInvestDetail(@PathVariable int id, HttpServletRequest request, HttpServletResponse response) {
		Project p = dataService.getProject(id, 7);
		ReportWrapper reportWith = reportCreator.projectInvestmentDetail(p, false, 0);
		
		if (!p.isWithWithout()) {
			reportCreator.export(response, reportWith);
		} else {
			ReportWrapper reportWithout = reportCreator.projectInvestmentDetail(p, true, reportWith.getJp().getPages().size());
			ArrayList<ReportWrapper> reports = new ArrayList<ReportWrapper>();
			reports.add(reportWith);
			reports.add(reportWithout);
			concatReports(reports, response, "projectInvestDetail.pdf");
		}
	}
	
	@RequestMapping(value="{id}/projectGeneralDetail.pdf", method=RequestMethod.GET)
	public void projectGeneralDetail(@PathVariable int id, HttpServletRequest request, HttpServletResponse response) {
		Project p = dataService.getProject(id, 8);
		if (p.getIncomeGen()) {
			ReportWrapper with = reportCreator.projectGeneralDetail(p, false, 0);
			if (p.isWithWithout()) {
				ReportWrapper without = reportCreator.projectGeneralDetail(p, true, with.getJp().getPages().size());
				ArrayList<ReportWrapper> reports = new ArrayList<ReportWrapper>();
				reports.add(with);
				reports.add(without);
				concatReports(reports, response, "projectGeneralDetail.pdf");
			} else {
				reportCreator.export(response, with);
			}
		} else {
			ReportWrapper report = reportCreator.projectGeneralNongen(p, 0);
			reportCreator.export(response, report);
		}
	}
	
	@RequestMapping(value="{id}/projectProduction.pdf", method=RequestMethod.GET)
	public void projectProduction(@PathVariable int id, HttpServletRequest request, HttpServletResponse response) {
		Project p = dataService.getProject(id, 9);
		ReportWrapper reportWith = reportCreator.projectProduction(p, 0, false);
		if (!p.isWithWithout()) {
			reportCreator.export(response, reportWith);
		} else {
			ReportWrapper reportWithout = reportCreator.projectProduction(p, reportWith.getJp().getPages().size(), true);
			ArrayList<ReportWrapper> reports = new ArrayList<ReportWrapper>();
			reports.add(reportWith);
			reports.add(reportWithout);
			concatReports(reports, response, "projectProduction.pdf");
		}
	}
	
	@RequestMapping(value="{id}/projectContributions.pdf", method=RequestMethod.GET)
	public void projectContributions(@PathVariable int id, HttpServletRequest request, HttpServletResponse response) {
		Project p = dataService.getProject(id, 10);
		ReportWrapper report = reportCreator.projectContributions(p, 0);
		reportCreator.export(response, report);
	}
	
	@RequestMapping(value="{id}/projectChronology.pdf", method=RequestMethod.GET)
	public void projectChronology(@PathVariable int id, HttpServletRequest request, HttpServletResponse response) {
		Project p = dataService.getProject(id, 9);
		ReportWrapper reportWith = reportCreator.projectChronology(p, 0, false);
		
		if (!p.isWithWithout()) {
			reportCreator.export(response, reportWith);
		} else {
			ReportWrapper reportWithout = reportCreator.projectChronology(p, reportWith.getJp().getPages().size(), true);
			ArrayList<ReportWrapper> reports = new ArrayList<ReportWrapper>();
			reports.add(reportWith);
			reports.add(reportWithout);
			concatReports(reports, response, "projectBlock.pdf");
		}
		
	}
	
	@RequestMapping(value="{id}/projectBlock.pdf", method=RequestMethod.GET)
	public void projectBlock(@PathVariable int id, HttpServletRequest request, HttpServletResponse response) {
		Project p = dataService.getProject(id, 9);
		ReportWrapper with = reportCreator.projectBlock(p, 0, false);
		
		if (!p.isWithWithout()) {
			reportCreator.export(response, with);
		} else {
			ReportWrapper without = reportCreator.projectBlock(p, with.getJp().getPages().size(), true);
			ArrayList<ReportWrapper> reports = new ArrayList<ReportWrapper>();
			reports.add(with);
			reports.add(without);
			concatReports(reports, response, "projectBlock.pdf");
		}
	}
	
	@RequestMapping(value="{id}/projectParameters.pdf", method=RequestMethod.GET)
	public void projectParameters(@PathVariable int id, HttpServletRequest request, HttpServletResponse response) {
		Project p = dataService.getProject(id, -1);
		ProjectResult pr = dataService.getProjectResult(id);
		ReportWrapper report = reportCreator.projectParameters(p, pr, 0);
		reportCreator.export(response, report);
	}
	
	@RequestMapping(value="{id}/projectCashFlow.pdf", method=RequestMethod.GET)
	public void projectCashFlow(@PathVariable int id, HttpServletRequest request, HttpServletResponse response) {
		Project p = dataService.getProject(id, -1);
		FinanceMatrix matrix = new FinanceMatrix(p, rivConfig.getSetting().getDiscountRate());
		ReportWrapper with = reportCreator.projectCashFlow(p, 0, matrix, false);
		if (!p.isWithWithout()) {
			reportCreator.export(response, with);
		} else {
			ReportWrapper without = reportCreator.projectCashFlow(p, with.getJp().getPages().size(), matrix, true);
			ArrayList<ReportWrapper> reports = new ArrayList<ReportWrapper>();
			reports.add(with);
			reports.add(without);
			concatReports(reports, response, "projectCashFlow.pdf");
		}
	}
	
	@RequestMapping(value="{id}/projectCashFlowNongen.pdf", method=RequestMethod.GET)
	public void projectCashFlowNongen(@PathVariable int id, HttpServletRequest request, HttpServletResponse response) {
		Project p = dataService.getProject(id, -1);
		ReportWrapper report = reportCreator.projectCashFlowNongen(p, 0);
		reportCreator.export(response, report);
	}
	
	@RequestMapping(value="{id}/projectCashFlowFirst.pdf", method=RequestMethod.GET)
	public void projectCashFlowFirst(@PathVariable int id, HttpServletRequest request, HttpServletResponse response) {
		Project p = dataService.getProject(id, -1);
		ReportWrapper with = reportCreator.projectCashFlowFirst(p, 0, false);
		if (!p.isWithWithout()) {
			reportCreator.export(response, with);
		} else {
			ReportWrapper without = reportCreator.projectCashFlowFirst(p, 0, true);
			ArrayList<ReportWrapper> reports = new ArrayList<ReportWrapper>();
			reports.add(with);
			reports.add(without);
			concatReports(reports, response, "projectCashFlowFirst.pdf");
		}
	}
	
	@RequestMapping(value="{id}/projectProfitability.pdf", method=RequestMethod.GET)
	public void projectProfitability(@PathVariable int id, HttpServletRequest request, HttpServletResponse response) {
		Project p = dataService.getProject(id, -1);
		FinanceMatrix matrix = new FinanceMatrix(p, rivConfig.getSetting().getDiscountRate());
		ReportWrapper with = reportCreator.projectProfitability(p, dataService.getProjectResult(id), 0, matrix, ProjectScenario.With);
		
		if (!p.isWithWithout()) {
			reportCreator.export(response, with);
		} else {
			ReportWrapper without = reportCreator.projectProfitability(p, dataService.getProjectResult(id), with.getJp().getPages().size(), matrix, ProjectScenario.Without);
			ReportWrapper incremental = reportCreator.projectProfitability(p, dataService.getProjectResult(id), with.getJp().getPages().size()+without.getJp().getPages().size(), matrix, ProjectScenario.Incremental);
			
			ArrayList<ReportWrapper> reports = new ArrayList<ReportWrapper>();
			reports.add(with);
			reports.add(without);
			reports.add(incremental);
			concatReports(reports, response, "projectProfitability.pdf");
		}
	}
	
	@RequestMapping(value="/{id}/projectRecommendation.pdf", method=RequestMethod.GET)
	   public void projectRecommendation(@PathVariable int id, HttpServletRequest request, HttpServletResponse response) {
		   Project p = dataService.getProject(id, 12);
		   ReportWrapper report = reportCreator.projectRecommendation(p, 0);
		   reportCreator.export(response, report);
	   }
	
	@RequestMapping(value="/{id}/projectComplete.pdf", method=RequestMethod.GET)
	   public void completeProjectReport(@PathVariable int id, HttpServletRequest request, HttpServletResponse response) throws Exception {
		   Project p = dataService.getProject(id, -1);
		   ProjectResult pr = dataService.getProjectResult(id);
		   FinanceMatrix matrix = p.getIncomeGen() ? new FinanceMatrix(p, rivConfig.getSetting().getDiscountRate()) : null;
		   List<ReportWrapper> reports = reportCreator.projectComplete(p, pr, matrix, response);
		   concatReports(reports, response, "projectComplete.pdf");
	   }
	
   @RequestMapping(value="/{id}/profileSummary.pdf", method=RequestMethod.GET)
   public void profileSummary(@PathVariable int id, HttpServletRequest request, HttpServletResponse response)  {
	   Profile p = dataService.getProfile(id, 1);
	   ReportWrapper report = reportCreator.profileSummary(p, 0);
	   reportCreator.export(response, report);
   }
   
   @RequestMapping(value="/{id}/profileInvest.pdf", method=RequestMethod.GET)
   public void profileInvest(@PathVariable int id, HttpServletRequest request, HttpServletResponse response)  {
	   Profile p = dataService.getProfile(id, 4);
	   ReportWrapper reportWith = reportCreator.profileInvest(p, false, 0);
	   
	   if (!p.getWithWithout()) {
			reportCreator.export(response, reportWith);
		} else {
			ReportWrapper reportWithout = reportCreator.profileInvest(p, true, reportWith.getJp().getPages().size());
			ArrayList<ReportWrapper> reports = new ArrayList<ReportWrapper>();
			reports.add(reportWith);
			reports.add(reportWithout);
			concatReports(reports, response, "profileInvest.pdf");
		}
   }
   
   @RequestMapping(value="/{id}/profileGeneral.pdf", method=RequestMethod.GET)
   public void profileGeneral(@PathVariable int id, HttpServletRequest request, HttpServletResponse response)  {
	   Profile p = dataService.getProfile(id, 5);
	   ReportWrapper report = reportCreator.profileGeneral(p, 0);
	   reportCreator.export(response, report);
   }
   
   @RequestMapping(value="/{id}/profileProducts.pdf", method=RequestMethod.GET)
   public void profileProduct(@PathVariable int id, HttpServletRequest request, HttpServletResponse response)  {
	   Profile p = dataService.getProfile(id, 6);
	   ReportWrapper with = reportCreator.profileProducts(p, 0, false);
	   
	   if (!p.getWithWithout()) {
			reportCreator.export(response, with);
		} else {
			ReportWrapper without = reportCreator.profileProducts(p, with.getJp().getPages().size(), true);
			ArrayList<ReportWrapper> reports = new ArrayList<ReportWrapper>();
			reports.add(with);
			reports.add(without);
			concatReports(reports, response, "profileProducts.pdf");
		}
   }
   
   @RequestMapping(value="/{id}/profilePrelimAnalysis.pdf", method=RequestMethod.GET)
   public void profilePrelimAnalysis(@PathVariable int id, HttpServletRequest request, HttpServletResponse response)  {
	   ProfileResult pr = dataService.getProfileResult(id);
	   ReportWrapper report = reportCreator.profilePrelimAnalysis(pr, 0);
	   reportCreator.export(response, report);
   }
   
   @RequestMapping(value="/{id}/profileRecommendation.pdf", method=RequestMethod.GET)
   public void profileRecommendation(@PathVariable int id, HttpServletRequest request, HttpServletResponse response) {
	   Profile p = dataService.getProfile(id, 8);
	   ReportWrapper report = reportCreator.profileRecommendation(p, 0);
	   reportCreator.export(response, report);
   }
   
   @RequestMapping(value="/{id}/profileComplete.pdf", method=RequestMethod.GET)
   public void completeReport(@PathVariable int id, HttpServletRequest request, HttpServletResponse response) throws Exception {
	   Profile p = dataService.getProfile(id, -1);
	   ProfileResult pr = dataService.getProfileResult(id);
	   List<ReportWrapper> reports = reportCreator.profileComplete(p, pr, response);
	   concatReports(reports, response, "profileComplete.pdf");
   }
   
   private void concatReports(List<ReportWrapper> reports, HttpServletResponse response, String filename) {
		response.setHeader("Content-Disposition", "inline; filename="+ filename);
		response.setContentType(MEDIA_TYPE_PDF);
		
		boolean firstReport = true;				
		
		@SuppressWarnings("unused")
		int pageOffset = 0;
		Document document = null;
		PdfCopy copier = null;
		
		for (ReportWrapper report : reports) {			
			try {
				byte[] bytes = JasperExportManager.exportReportToPdf(report.getJp());
				PdfReader reader = new PdfReader(bytes);
								
				reader.consolidateNamedDestinations();
				int n = reader.getNumberOfPages();
				pageOffset += n;
				
				if (firstReport) {
					firstReport=false;
	                // step 1: creation of a document-object
	                document = new Document(reader.getPageSizeWithRotation(1));
	                // step 2: we create a writer that listens to the document
	                copier = new PdfCopy(document, response.getOutputStream());
	                // step 3: we open the document
	                document.open();
	            }
				// step 4: add page
				com.lowagie.text.pdf.PdfImportedPage page;
	            for (int i = 0; i < n; ) {
	                ++i;
	                page = copier.getImportedPage(reader, i);
	                copier.addPage(page);
	            }
			} catch (Exception e) {
				//String mess = e.getMessage();
			}
		}
		copier.close();
		document.close();
	}
}
