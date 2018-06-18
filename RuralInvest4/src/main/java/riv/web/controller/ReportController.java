package riv.web.controller;
 
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperExportManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import riv.objects.FinanceMatrix;
import riv.objects.FinanceMatrix.ProjectScenario;
import riv.objects.ProfileMatrix;
import riv.objects.ProjectMonthsInYear;
import riv.objects.profile.Profile;
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
	static final Logger LOG = LoggerFactory.getLogger(ReportController.class);
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
	
	@RequestMapping(value="{id}/projectAmortization.pdf", method=RequestMethod.GET)
	public void projectAmortization(@PathVariable int id, HttpServletRequest request, HttpServletResponse response) {
		Project p = dataService.getProject(id, -1);
		FinanceMatrix matrix = new FinanceMatrix(p, rivConfig.getSetting().getDiscountRate(), rivConfig.getSetting().getDecimalLength());
		ReportWrapper loan1 = reportCreator.projectAmortization(p, 0, matrix, true);
		ReportWrapper loan2 = reportCreator.projectAmortization(p, loan1.getJp().getPages().size(), matrix, false);
		ArrayList<ReportWrapper> reports = new ArrayList<ReportWrapper>();
		reports.add(loan1);
		reports.add(loan2);
		concatReports(reports, response, "projectAmortization.pdf");
	}
	
	@RequestMapping(value="{id}/projectCashFlow.pdf", method=RequestMethod.GET)
	public void projectCashFlow(@PathVariable int id, HttpServletRequest request, HttpServletResponse response) {
		Project p = dataService.getProject(id, -1);
		FinanceMatrix matrix = new FinanceMatrix(p, rivConfig.getSetting().getDiscountRate(), rivConfig.getSetting().getDecimalLength());
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
	
	@RequestMapping(value="{id}/projectWorkingCapital.pdf", method=RequestMethod.GET)
	public void projectWorkingCapital(@PathVariable int id, HttpServletRequest request, HttpServletResponse response) {
		Project p = dataService.getProject(id, -1);
		ProjectResult pr = dataService.getProjectResult(id);
		
		ArrayList<ReportWrapper> reports = new ArrayList<ReportWrapper>();
		int page=0;
		
		int years = (pr.getWcPeriod()-1)/12+1;
		ProjectMonthsInYear[] monthsWith = ProjectMonthsInYear.getProjectPerMonths(p, false, rivConfig.getSetting().getDecimalLength());
		for (int i=1;i<=years;i++) {
			ReportWrapper with = reportCreator.projectWorkingCapital(p, pr, monthsWith, page, false, i);
			page+=with.getJp().getPages().size();
			reports.add(with);
		}
		
		if (p.isWithWithout()) {
			ProjectMonthsInYear[] monthsWithout = ProjectMonthsInYear.getProjectPerMonths(p, true, rivConfig.getSetting().getDecimalLength());
			//	only show year 1
			ReportWrapper without = reportCreator.projectWorkingCapital(p, pr, monthsWithout, page, true, 1);
			page+=without.getJp().getPages().size();
			reports.add(without);
		}
			
		concatReports(reports, response, "projectWorkingCapital.pdf");
	}
	
	@RequestMapping(value="{id}/projectProfitability.pdf", method=RequestMethod.GET)
	public void projectProfitability(@PathVariable int id, HttpServletRequest request, HttpServletResponse response) {
		Project p = dataService.getProject(id, -1);
		FinanceMatrix matrix = new FinanceMatrix(p, rivConfig.getSetting().getDiscountRate(), rivConfig.getSetting().getDecimalLength());
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
		   FinanceMatrix matrix = p.getIncomeGen() ? new FinanceMatrix(p, rivConfig.getSetting().getDiscountRate(), rivConfig.getSetting().getDecimalLength()) : null;
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
	   Profile p = dataService.getProfile(id, -1);
	   ProfileMatrix matrix = new ProfileMatrix(p);
	   ReportWrapper report = reportCreator.profilePrelimAnalysis(p, matrix, ProjectScenario.With, 0);
	   if (!p.getWithWithout()) {
		   reportCreator.export(response, report);
	   } else {
		   int page  = report.getJp().getPages().size();
		   ReportWrapper reportWithout = reportCreator.profilePrelimAnalysis(p, matrix, ProjectScenario.Without, page);
		   page += reportWithout.getJp().getPages().size();
		   ReportWrapper reportIncr = reportCreator.profilePrelimAnalysis(p, matrix, ProjectScenario.Incremental, page);
		   ArrayList<ReportWrapper> reports = new ArrayList<ReportWrapper>();
			reports.add(report);
			reports.add(reportWithout);
			reports.add(reportIncr);
			concatReports(reports, response, "profilePrelimAnalysis.pdf");
	   }
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
	   List<ReportWrapper> reports = reportCreator.profileComplete(p, response);
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
			} catch (NullPointerException e) {
				LOG.error(e.getMessage(), e);
//				e.printStackTrace(System.out);
                throw new RuntimeException(e);
            } catch (Exception e) {
            	LOG.error(e.getMessage(), e);
			}
		}
		if (copier!=null) {
			copier.close();
		}
		if (document!=null) {
			document.close();
		}
	}
}
