package riv.web.service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceResourceBundle;
import org.springframework.stereotype.Component;

import riv.objects.FilterCriteria;
import riv.objects.FinanceMatrix;
import riv.objects.FinanceMatrix.ProjectScenario;
import riv.objects.ProfileMatrix;
import riv.objects.ProjectFinanceNongen;
import riv.objects.ProjectMonthsInYear;
import riv.objects.profile.Profile;
import riv.objects.profile.ProfileResult;
import riv.objects.project.BlockBase;
import riv.objects.project.BlockChron;
import riv.objects.project.Donor;
import riv.objects.project.Project;
import riv.objects.project.ProjectItem;
import riv.objects.project.ProjectItemContribution;
import riv.objects.project.ProjectItemLabour;
import riv.objects.project.ProjectItemService;
import riv.objects.project.ProjectResult;
import riv.util.ReportLoader;
import riv.util.ReportSource;
import riv.util.ReportWrapper;
import riv.web.config.RivConfig;

@Component
public class PdfReportCreator {
	static final Logger LOG = LoggerFactory.getLogger(PdfReportCreator.class);
	
	@Autowired
	RivConfig rivConfig;
	@Autowired
	MessageSource messageSource;
	@Autowired
	ServletContext ctx;
	@Autowired
	ReportLoader reportLoader;
	
	public static final String MEDIA_TYPE_PDF = "application/pdf";
	
	public List<ReportWrapper> profileComplete(Profile profile, HttpServletResponse response) {
		ProfileMatrix matrix = new ProfileMatrix(profile);
		ArrayList<ReportWrapper> reports = new ArrayList<ReportWrapper>();
		int page=0;
		
		ReportWrapper cover = profileCompleteCover(profile, page);
		page=page+cover.getJp().getPages().size();
		reports.add(cover);
		
		ReportWrapper summary = profileSummary(profile, page);
		page=page+summary.getJp().getPages().size();
		reports.add(summary);
		
		
		ReportWrapper invest = profileInvest(profile,false, page);
		page = page+invest.getJp().getPages().size();
		reports.add(invest);
		if (profile.getWithWithout()) {
			ReportWrapper investWithout = profileInvest(profile,true, page);
			page = page+investWithout.getJp().getPages().size();
			reports.add(investWithout);
		}

		ReportWrapper general = profileGeneral(profile, page);
		page = page+general.getJp().getPages().size();
		reports.add(general);
		ReportWrapper product = profileProducts(profile, page, false);
		page = page+product.getJp().getPages().size();
		reports.add(product);
		if (profile.getWithWithout()) {
			ReportWrapper productWithout = profileProducts(profile, page, true);
			page = page+productWithout.getJp().getPages().size();
			reports.add(productWithout);
		}
		ReportWrapper anal = profilePrelimAnalysis(profile, matrix, ProjectScenario.With, page);
		page = page+anal.getJp().getPages().size();
		reports.add(anal);
		if (profile.getWithWithout()) {
			ReportWrapper analWithout = profilePrelimAnalysis(profile, matrix, ProjectScenario.Without, page);
			page = page+analWithout.getJp().getPages().size();
			reports.add(analWithout);
			ReportWrapper analIncr = profilePrelimAnalysis(profile, matrix, ProjectScenario.Incremental, page);
			page = page+analIncr.getJp().getPages().size();
			reports.add(analIncr);
		}
		ReportWrapper rec = profileRecommendation(profile, page);
		page = page+rec.getJp().getPages().size();
		reports.add(rec);
		
		return reports;
	}
	
	public ReportWrapper profileCompleteCover(Profile profile, int startPage) {
		ReportWrapper report = new ReportWrapper("/reports/coverReport.jasper", false, profile, "profileCompleteCover.pdf", startPage);
		
		report.getParams().put("reportnameA", "A: "+translate("profile.report.summary"));
		report.getParams().put("reportnameB", "B: "+translate("profile.report.investDetail"));
		report.getParams().put("reportnameC", "C: "+translate("profile.report.costsDetail"));
		String reportCode_product = profile.getIncomeGen() ? "profile.report.productDetail" : "profile.report.productDetailNongen";
		report.getParams().put("reportnameD", "D: "+translate(reportCode_product));
		String reportCode_preliminary = profile.getIncomeGen() ? "profile.report.preliminary" : "profile.report.benefits";
		report.getParams().put("reportnameE", "E: "+translate(reportCode_preliminary));
		report.getParams().put("reportnameF", "F: "+translate("profile.report.recommendation"));
		report.getParams().put("reportnameG", "");
		report.getParams().put("reportnameH", "");
		report.getParams().put("reportnameI", "");
		report.getParams().put("reportnameJ", "");
		report.getParams().put("reportnameK", "");
		report.getParams().put("reportnameL", "");
		report.getParams().put("proType", profile.getIncomeGen() ?  translate("profile.incomeGen") : translate("profile.nonIncomeGen")) ;
		report.getParams().put("projectName", profile.getProfileName());
		report.getParams().put("reportname", translate("profile.report.complete"));
		report.getParams().put("incomeGen", profile.getIncomeGen());
		
		runReport(report);
		return report;
	}
	
	public ReportWrapper profileRecommendation(Profile profile, int startPage) {
		ReportWrapper report = new ReportWrapper("/reports/profile/profileRecommendation.jasper", false, profile, "profileRecommendation.pdf", startPage);
		report.getParams().put("reportname", "F: "+translate("profile.report.recommendation"));
		runReport(report);
		return report;
	}
	
	public ReportWrapper profileProducts(Profile profile, int startPage, boolean without) {
		ReportWrapper report = new ReportWrapper("/reports/profile/profileProduct.jasper", true, without ? profile.getProductsWithout() : profile.getProducts(), "profileProducts.pdf", startPage);
		
		JasperReport jrInc = reportLoader.compileReport("/reports/profile/profileProductIncome.jasper");
		report.getParams().put("incomeSubReport", jrInc);
		JasperReport jrInp = reportLoader.compileReport("/reports/profile/profileProductInput.jasper");
		report.getParams().put("inputSubReport", jrInp);
		JasperReport jrLab = reportLoader.compileReport("/reports/profile/profileProductLabour.jasper");
		report.getParams().put("labourSubReport", jrLab);
		
		report.getParams().put("profileName", profile.getProfileName());
		report.getParams().put("incomeGen", profile.getIncomeGen());
		report.getParams().put("lengthUnits", this.lengthUnits());
		String reportTitle = profile.getIncomeGen() ? translate("profile.report.productDetail") : translate("profile.report.productDetailNongen");
		if (profile.getWithWithout()) {
			if (!without) {
				reportTitle = reportTitle+" ("+translate("profileProduct.with.with")+")";
			} else {
				reportTitle = reportTitle+" ("+translate("profileProduct.with.without")+")";
			}
		}
		report.getParams().put("reportTitle", reportTitle);
		report.getParams().put("reportname", "D: "+reportTitle);
		runReport(report);
		return report;
	}
	
	public ReportWrapper profilePrelimAnalysis(Profile p, ProfileMatrix matrix, ProjectScenario scenario, int startPage) {
		ReportWrapper report = new ReportWrapper("/reports/profile/profilePrelimAnalysis.jasper", false, p, "profilePrelimAnalysis.pdf", startPage);
		
		
		String title = p.getIncomeGen() ? translate("profile.report.preliminary") : translate("profile.report.benefits");
		if (scenario==ProjectScenario.With) {
			if (p.getWithWithout()) { title+= " "+translate("project.with"); }
		} else if (scenario==ProjectScenario.Without) {
			title += " "+translate("project.without");
		} else { // incremental
			title += " "+translate("project.incremental");
		}
		report.getParams().put("title", title);
		
		String reportCode = p.getIncomeGen() ? "profile.report.preliminary" : "profile.report.benefits";
		report.getParams().put("reportname", "E: "+translate(reportCode));
		
		if (scenario==ProjectScenario.With) {
			report.getParams().put("investTotal", matrix.totalInvestmentWith);
			report.getParams().put("investOwn", matrix.totalOwnResourcesWith);
			report.getParams().put("totalIncome", matrix.totalIncomeWith);
			report.getParams().put("costOper", matrix.operationCostWith);
			report.getParams().put("costGeneral", matrix.generalCostWith);
			report.getParams().put("annualReserve", matrix.annualReserveWith);
		} else if (scenario==ProjectScenario.Without) {
			report.getParams().put("investTotal", matrix.totalInvestmentWithout);
			report.getParams().put("investOwn", matrix.totalOwnResourcesWithout);
			report.getParams().put("totalIncome", matrix.totalIncomeWithout);
			report.getParams().put("costOper", matrix.operationCostWithout);
			report.getParams().put("costGeneral", matrix.generalCostWithout);
			report.getParams().put("annualReserve", matrix.annualReserveWithout);
		} else { // incremental
			report.getParams().put("investTotal", matrix.totalInvestmentWith-matrix.totalInvestmentWithout);
			report.getParams().put("investOwn", matrix.totalOwnResourcesWith-matrix.totalOwnResourcesWithout);
			report.getParams().put("totalIncome", matrix.totalIncomeWith-matrix.totalIncomeWithout);
			report.getParams().put("costOper", matrix.operationCostWith-matrix.operationCostWithout);
			report.getParams().put("costGeneral", matrix.generalCostWith-matrix.generalCostWithout);
			report.getParams().put("annualReserve", matrix.annualReserveWith-matrix.annualReserveWithout);
		}
		runReport(report);
		return report;
	}
	
	public ReportWrapper profileInvest(Profile profile, boolean withoutProject, int startPage) {
		ReportWrapper report = new ReportWrapper("/reports/profile/profileInvest.jasper", true, profile, "profileInvest.pdf", startPage);
		
		JasperReport jrGoods = reportLoader.compileReport("/reports/profile/profileInvestGoods.jasper");
		report.getParams().put("goodsSubReport", jrGoods);
		JasperReport jrLabour = reportLoader.compileReport("/reports/profile/profileInvestLabour.jasper");
		report.getParams().put("labourSubReport", jrLabour);
		
		report.getParams().put("reportname", "B: "+translate("profile.report.investDetail"));
		report.getParams().put("without", withoutProject);
		
		runReport(report);
		return report;
	}
	
	public ReportWrapper profileGeneral(Profile profile, int startPage) {
		ReportWrapper report = new ReportWrapper("/reports/profile/profileGeneral.jasper", true, profile, "profileGeneral.pdf", startPage);
		
		JasperReport jrSub = reportLoader.compileReport("/reports/profile/profileGeneralSubreport.jasper");
		report.getParams().put("generalSubreport", jrSub);
		
		report.getParams().put("reportname", "C: "+translate("profile.report.costsDetail"));
		runReport(report);
		return report;
	}
	
	public ReportWrapper profileSummary(Profile profile, int startPage) {
		ReportWrapper report = new ReportWrapper("/reports/profile/profileSummary.jasper", false, profile, "profileSummary.pdf", startPage);
		report.getParams().put("reportname", "A: "+translate("profile.report.summary"));
		
		runReport(report);
		return report;
	}
	
	public ReportWrapper profileList(List<ProfileResult> results, FilterCriteria filter) {
		ReportWrapper report = new ReportWrapper("/reports/profile/profileList.jasper", true, results, "profileResults.pdf", 0);
		report.getParams().put("filter", filter);
		
		runReport(report);
		return report;
	}
	
	public ReportWrapper projectList(List<ProjectResult> results, FilterCriteria filter) {
		ReportWrapper report = new ReportWrapper("/reports/project/projectList.jasper", true, null, "projectResults.pdf", 0);
		report.getParams().put("filter", filter);
		
		JasperReport jrSub1 = reportLoader.compileReport("/reports/project/projectListSub1.jasper");
		report.getParams().put("projectList1", jrSub1);
		JasperReport jrSub2 = reportLoader.compileReport("/reports/project/projectListSub2.jasper");
		report.getParams().put("projectList2", jrSub2);
		
		report.getParams().put("data1", new ReportSource(results));
		report.getParams().put("data2", new ReportSource(results));
		
		runReport(report);
		return report;
	}
	
	public List<ReportWrapper> projectComplete(Project project, ProjectResult pr, FinanceMatrix matrix, HttpServletResponse response) {
		ArrayList<ReportWrapper> reports = new ArrayList<ReportWrapper>();
		int page=0;
		
		ReportWrapper cover = projectCompleteCover(project, page);
		page=page+cover.getJp().getPages().size();
		reports.add(cover);
		ReportWrapper summary = projectSummary(project, pr, page);
		page=page+summary.getJp().getPages().size();
		reports.add(summary);
		ReportWrapper general = projectDescription(project, page);
		page=page+general.getJp().getPages().size();
		reports.add(general);
		ReportWrapper investWith = projectInvestmentDetail(project, false, page);
		page=page+investWith.getJp().getPages().size();
		reports.add(investWith);
		if (project.isWithWithout()) {
			ReportWrapper investWithout = projectInvestmentDetail(project, true, page);
			page=page+investWithout.getJp().getPages().size();
			reports.add(investWithout);
		}
		if (project.getIncomeGen()) {
			ReportWrapper genDetailWith = projectGeneralDetail(project, false, page);
			page=page+genDetailWith.getJp().getPages().size();
			reports.add(genDetailWith);
			if (project.isWithWithout()) {
				ReportWrapper genDetailWithout = projectGeneralDetail(project, true, page);
				page=page+genDetailWithout.getJp().getPages().size();
				reports.add(genDetailWithout);
			}
		} else {
			ReportWrapper genDetail = projectGeneralNongen(project, page);
			page=page+genDetail.getJp().getPages().size();
			reports.add(genDetail);
		}
		
		ReportWrapper production = projectProduction(project, page, false);
		page=page+production.getJp().getPages().size();
		reports.add(production);
		if (project.isWithWithout()) {
			ReportWrapper productionWithout = projectProduction(project, page, true);
			page=page+productionWithout.getJp().getPages().size();
			reports.add(productionWithout);
		}
		
		
		if (project.getIncomeGen()) {
			ReportWrapper chron = projectChronology(project, page, false);
			page=page+chron.getJp().getPages().size();
			reports.add(chron);
			if (project.isWithWithout()) {
				ReportWrapper chronWithout = projectChronology(project, page, true);
				page=page+chronWithout.getJp().getPages().size();
				reports.add(chronWithout);
			}
		}
		ReportWrapper blocks = projectBlock(project, page, false);
		page=page+blocks.getJp().getPages().size();
		reports.add(blocks);
		
		if (project.isWithWithout()) {
			ReportWrapper blocksWithout = projectBlock(project, page, true);
			page=page+blocksWithout.getJp().getPages().size();
			reports.add(blocksWithout);
		}

		if (project.getIncomeGen()) {
			ReportWrapper params = projectParameters(project, pr, page);
			page=page+params.getJp().getPages().size();
			reports.add(params);
			
			ProjectMonthsInYear[] monthsWith = ProjectMonthsInYear.getProjectPerMonths(project, false, rivConfig.getSetting().getDecimalLength());
			for (int i=1;i<=project.getDuration();i++) {
				ReportWrapper wcWith = projectWorkingCapital(project, pr, monthsWith, page, false, i);
				page=page+wcWith.getJp().getPages().size();
				reports.add(wcWith);
			}
			
			ProjectMonthsInYear[] monthsWithout = ProjectMonthsInYear.getProjectPerMonths(project, true, rivConfig.getSetting().getDecimalLength());
			for (int i=1;i<=project.getDuration();i++) {
				if (project.isWithWithout()) {
					ReportWrapper wcWithout = projectWorkingCapital(project, pr, monthsWithout, page, true, i);
					page+=wcWithout.getJp().getPages().size();
					reports.add(wcWithout);
				}
			}
			
			ReportWrapper loan1 = projectAmortization(project, page, matrix, true);
			page+=loan1.getJp().getPages().size();
			reports.add(loan1);
			ReportWrapper loan2 = projectAmortization(project, page, matrix, false);
			page+=loan2.getJp().getPages().size();
			reports.add(loan2);
			
			
			ReportWrapper cf = projectCashFlow(project, page, matrix, false);
			page=page+cf.getJp().getPages().size();
			reports.add(cf);
			if (project.isWithWithout()) {
				ReportWrapper cfWithout = projectCashFlow(project, page, matrix, true);
				page+=cfWithout.getJp().getPages().size();
				reports.add(cfWithout);
			}
			ReportWrapper profitWith = projectProfitability(project, pr, page, matrix, ProjectScenario.With);
			page=page+profitWith.getJp().getPages().size();
			reports.add(profitWith);
			if (project.isWithWithout()) {
				ReportWrapper profitWithout = projectProfitability(project, pr, page, matrix, ProjectScenario.Without);
				page=page+profitWithout.getJp().getPages().size();
				reports.add(profitWithout);
				ReportWrapper profit = projectProfitability(project, pr, page, matrix, ProjectScenario.Incremental);
				page=page+profit.getJp().getPages().size();
				reports.add(profit);
			}
		} else {
			ReportWrapper cont = projectContributions(project, page);
			page=page+cont.getJp().getPages().size();
			reports.add(cont);
			ReportWrapper cashNongen = projectCashFlowNongen(project, page);
			page=page+cashNongen.getJp().getPages().size();
			reports.add(cashNongen);
		}
		ReportWrapper recc = projectRecommendation(project, page);
		page=page+recc.getJp().getPages().size();
		reports.add(recc);
		return reports;
	}
	
	public ReportWrapper projectCompleteCover(Project project, int startPage) {
		ReportWrapper report = new ReportWrapper("/reports/coverReport.jasper", false, project, "projectCompleteCover.pdf", startPage);
		
		if (project.getIncomeGen()) {
			report.getParams().put("reportnameA", "A: "+translate("project.report.summary"));
			report.getParams().put("reportnameB", "B: "+translate("project.report.general"));
			report.getParams().put("reportnameC", "C: "+translate("project.report.investDetail"));
			report.getParams().put("reportnameD", "D: "+translate("project.report.generalCostsDetail"));
			report.getParams().put("reportnameE", "E: "+translate("project.report.production"));
			report.getParams().put("reportnameF", "F: "+translate("project.report.chronology"));
			report.getParams().put("reportnameG", "G: "+translate("project.report.blockDetail"));
			report.getParams().put("reportnameH", "H: "+translate("project.report.parameters"));
			report.getParams().put("reportnameI", "I: "+translate("project.report.workingcapital"));
			report.getParams().put("reportnameJ", "J: "+translate("project.report.amortization.title"));
			report.getParams().put("reportnameK", "K: "+translate("project.report.cashFlow"));
			report.getParams().put("reportnameL", "L: "+translate("project.report.profitability"));
			report.getParams().put("reportnameM", "M: "+translate("project.report.recommendation"));
			report.getParams().put("proType", translate("project.incomeGen"));
		} else {
			report.getParams().put("reportnameA", "A: "+translate("project.report.summary"));
			report.getParams().put("reportnameB", "B: "+translate("project.report.general"));
			report.getParams().put("reportnameC", "C: "+translate("project.report.investDetail"));
			report.getParams().put("reportnameD", "D: "+translate("project.report.generalCostsDetail"));
			report.getParams().put("reportnameE", "E: "+translate("project.report.production.nonGen"));
			report.getParams().put("reportnameF", "F: "+translate("project.report.activityDetail"));
			report.getParams().put("reportnameG", "G: "+translate("project.report.contributions"));
			report.getParams().put("reportnameH", "H: "+translate("project.report.cashFlowNongen"));
			report.getParams().put("reportnameI", "I: "+translate("project.report.recommendation"));
			report.getParams().put("reportnameJ", "");
			report.getParams().put("reportnameK", "");
			report.getParams().put("reportnameL", "");
			report.getParams().put("proType", translate("project.nonIncomeGen"));
		}			
		
		report.getParams().put("reportname", translate("project.report.complete") ) ;
		report.getParams().put("projectName", project.getProjectName());
		report.getParams().put("incomeGen", project.getIncomeGen());

		
		runReport(report);
		return report;
	}
	
	public ReportWrapper projectRecommendation(Project project, int startPage) {
		ReportWrapper report = new ReportWrapper("/reports/project/projectRecommendation.jasper", false, project, "projectRecommendation.pdf", startPage);
		String reportletter = project.getIncomeGen() ? "L: " : "I: ";
		report.getParams().put("reportname", reportletter+translate("project.report.recommendation"));
		runReport(report);
		return report;
	}
	
	public ReportWrapper projectProfitability(Project project, ProjectResult result, int startPage, FinanceMatrix matrix, ProjectScenario scenario) {
		ReportWrapper report = new ReportWrapper("/reports/project/projectProfitability.jasper", true, matrix.getYearlyData(), "projectProfitability.pdf", startPage);
		
		JasperReport jrIndicators = reportLoader.compileReport("/reports/project/projectProfitabilityIndicators.jasper");
		report.getParams().put("indicators", jrIndicators);
		
		report.getParams().put("scenario",scenario);
		report.getParams().put("projectName", project.getProjectName());
		report.getParams().put("incomeGen", project.getIncomeGen());
		report.getParams().put("npv", matrix.getNpv(true, scenario));
		report.getParams().put("irr", matrix.getIrr(true, scenario));
		report.getParams().put("npvNoDonation", matrix.getNpv(false, scenario));
		report.getParams().put("irrNoDonation", matrix.getIrr(false, scenario));
		
		double[] payback=matrix.getPayback(project.getDuration(), scenario);
		report.getParams().put("paybackBefore", payback[0]);
		report.getParams().put("paybackAfter", payback[1]);
		
		
		String title;
		if (scenario==ProjectScenario.With) {
			if (project.isWithWithout()) {
				title=translate("project.report.profitability") + " "+translate("project.with");
			} else {
				title=translate("project.report.profitability");
			}
		} else if (scenario==ProjectScenario.Without) {
			title=translate("project.report.profitability")+ " "+translate("project.without");
		} else { // incremental
			title=translate("project.report.profitability") + " "+translate("project.incremental");
		}
		report.getParams().put("reportname", "M: "+translate("project.report.profitability"));
		report.getParams().put("title", title);
		
		runReport(report);
		return report;
	}
	
	public ReportWrapper projectWorkingCapital(Project project, ProjectResult pr, ProjectMonthsInYear[] months, int startPage, boolean without, int year) {
		ReportWrapper report = new ReportWrapper("/reports/project/projectWorkingCapital.jasper", true, project, "projectWorkingCapital.pdf", startPage);
		
		JasperReport jrIncome = reportLoader.compileReport("/reports/project/projectWorkingCapitalOpIncomes.jasper");
		report.getParams().put("opIncomesSubReport", jrIncome);
		JasperReport jrCosts = reportLoader.compileReport("/reports/project/projectWorkingCapitalOpCosts.jasper");
		report.getParams().put("opCostsSubReport", jrCosts);
		JasperReport jrTotals = reportLoader.compileReport("/reports/project/projectWorkingCapitalTotals.jasper");
		report.getParams().put("totalsSubReport", jrTotals);
		
		
		ProjectMonthsInYear.getProjectPerMonths(project, without, rivConfig.getSetting().getDecimalLength());
		report.getParams().put("firstYearData",  months[year-1]);
		report.getParams().put("months", months(project));
		report.getParams().put("year", year);
		
		// show final data
		if (!without && year==project.getDuration()) {
			report.getParams().put("showInfo", true);
			if (pr!=null) {
				report.getParams().put("amtRequired", pr.getWorkingCapital());
				report.getParams().put("period", pr.getWcPeriod());
				report.getParams().put("periodAvg", pr.getWcPeriodAvg());
			} else {
				FinanceMatrix matrix = new FinanceMatrix(project, rivConfig.getSetting().getDiscountRate(), rivConfig.getSetting().getDecimalLength()); 
				report.getParams().put("amtRequired", matrix.getWcValue());
				report.getParams().put("period", matrix.getWcPeriod());
				report.getParams().put("periodAvg", matrix.getWcPeriodAvg());
			}
		}
		
		String title;
		if (!without) {
			if (project.isWithWithout()) {
				title=translate("project.report.workingcapital") + " "+translate("project.with");
			} else {
				title=translate("project.report.workingcapital");
			}
		} else { // without
			title=translate("project.report.workingcapital") + " "+translate("project.without");
		}
		report.getParams().put("title", title);
		report.getParams().put("reportname", "I: "+translate("project.report.workingcapital"));
		
		runReport(report);
		return report;
	}
	
	public ReportWrapper projectCashFlowNongen(Project project, int startPage) {
		ArrayList<ProjectFinanceNongen> data = ProjectFinanceNongen.analyzeProject(project);
		
		ReportWrapper report = new ReportWrapper("/reports/project/projectCashFlowNongen.jasper", true, data, "projectCashFlowNongen.pdf", startPage);
		
		report.getParams().put("projectName", project.getProjectName());
		report.getParams().put("incomeGen", project.getIncomeGen());
		report.getParams().put("reportname", "H: "+translate("project.report.cashFlowNongen"));
		
		runReport(report);
		return report;
	}
	
	public ReportWrapper projectAmortization(Project project, int startPage, FinanceMatrix matrix, boolean loan1) {
		ReportWrapper report = new ReportWrapper("/reports/project/projectAmortization.jasper", true, loan1 ? matrix.getLoan1() : matrix.getLoan2(), "projectAmortization.pdf", startPage);
		
		report.getParams().put("projectName", project.getProjectName());
		report.getParams().put("incomeGen", project.getIncomeGen());
		report.getParams().put("isLoan1", loan1);
		report.getParams().put("periodsPerYear", loan1 ? project.getLoan1PaymentsPerYear() : project.getLoan2PaymentsPerYear());
		
		report.getParams().put("title",translate("project.report.amortization.title") + ": " + (loan1 ? translate("project.loan1") : translate("project.loan2")));
		report.getParams().put("reportname", "J: "+translate("project.report.amortization.title"));
		
		runReport(report);
		return report;
		
	}
	
	public ReportWrapper projectCashFlow(Project project, int startPage, FinanceMatrix matrix, boolean without) {
		ReportWrapper report = new ReportWrapper(without ? "/reports/project/projectCashFlowWithout.jasper" : "/reports/project/projectCashFlow.jasper", true, matrix.getYearlyData(), "projectCashFlow.pdf", startPage);
		
		report.getParams().put("projectName", project.getProjectName());
		report.getParams().put("incomeGen", project.getIncomeGen());
		
		String title;
		if (!without) {
			if (project.isWithWithout()) {
				title=translate("project.report.cashFlow") + " "+translate("project.with");
			} else {
				title=translate("project.report.cashFlow");
			}
		} else { // without
			title=translate("project.report.cashFlow") + " "+translate("project.without");
		}
		report.getParams().put("title", title);
		report.getParams().put("reportname", "K: "+translate("project.report.cashFlow"));
		
		runReport(report);
		return report;
	}
	
	public ReportWrapper projectParameters(Project project, ProjectResult pr, int startPage) {
		ReportWrapper report = new ReportWrapper("/reports/project/projectParameters.jasper", false, project, "projectParameters.pdf", startPage);
		report.getParams().put("financePrd", pr.getWcPeriod());
		report.getParams().put("amtRequired", pr.getWorkingCapital());
		report.getParams().put("financePrdAvg", pr.getWcPeriodAvg());
		report.getParams().put("investTotal", pr.getInvestmentFinanced());
		report.getParams().put("reportname", "H: "+translate("project.report.parameters"));
		
		runReport(report);
		return report;
	}
	
	
	public ReportWrapper projectBlock(Project project, int startPage, boolean withoutProject) {
		ReportWrapper report = new ReportWrapper("/reports/project/projectBlock.jasper", true, withoutProject ? project.getBlocksWithout() : project.getBlocks(), "projectBlock.pdf", startPage);
		
		JasperReport jrIncome = reportLoader.compileReport("/reports/project/projectBlockIncome.jasper");
		report.getParams().put("incomeSubReport", jrIncome);
		JasperReport jrInput = reportLoader.compileReport("/reports/project/projectBlockInput.jasper");
		report.getParams().put("inputSubReport", jrInput);
		JasperReport jrLabour = reportLoader.compileReport("/reports/project/projectBlockLabour.jasper");
		report.getParams().put("labourSubReport", jrLabour);
		
		report.getParams().put("projectName", project.getProjectName());
		report.getParams().put("incomeGen", project.getIncomeGen());
		report.getParams().put("labourTypes", this.labourTypes());
		report.getParams().put("lengthUnits", this.lengthUnits());
		report.getParams().put("withWithout", project.isWithWithout());
		String reportTitle = project.getIncomeGen() ? translate("project.report.blockDetail") : translate("project.report.activityDetail");
		if (project.isWithWithout()) {
			if (withoutProject) {
			    reportTitle = reportTitle + " ("+translate("projectBlock.with.without")+")";
			} else {
				 reportTitle = reportTitle + " ("+translate("projectBlock.with.with")+")";
			}
		}
		report.getParams().put("reportTitle", reportTitle);
		report.getParams().put("reportname", project.getIncomeGen() ? "G: "+reportTitle : "F: "+reportTitle);
		
		runReport(report);
		return report;
	}
	
	public ReportWrapper projectChronology(Project project, int startPage, boolean without) {
		ReportWrapper report = new ReportWrapper("/reports/project/projectChronology.jasper", true, 
				without ? project.getBlocksWithout() : project.getBlocks(), "projectChronology.pdf", startPage);
		
		HashMap<Integer, java.util.Map<String, BlockChron>> chrons = new HashMap<Integer, java.util.Map<String, BlockChron>>();
		for(BlockBase block : without ? project.getBlocksWithout() : project.getBlocks()) 
			chrons.put(block.getBlockId(), block.getChrons());
		
		report.getParams().put("withWithout", project.isWithWithout());
		report.getParams().put("without", without);
		report.getParams().put("projectName", project.getProjectName());
		report.getParams().put("incomeGen", project.getIncomeGen());
		report.getParams().put("chrons", chrons);
		report.getParams().put("months", months(project));
		report.getParams().put("reportname", "F: "+translate("project.report.chronology"));
		
		runReport(report);
		return report;
	}
	
	public ReportWrapper projectContributions(Project project, int startPage) {
		List<ProjectItemContribution> contribs = new ArrayList<ProjectItemContribution>(project.getContributions());
		Collections.sort(contribs);
		ReportWrapper report = new ReportWrapper(
				project.isPerYearContributions() ? "/reports/project/projectContributionsPerYear.jasper" : "/reports/project/projectContributions.jasper", 
				true, contribs, "projectContributions.pdf", startPage);
		
		if (project.isPerYearContributions()) {
			JasperReport jrPerYearData = reportLoader.compileReport("/reports/project/projectGeneralPerYearData.jasper");
			report.getParams().put("perYearDataSubreport", jrPerYearData);
		}
		
		Map<Integer, String> donorsByOrder = new HashMap<Integer,String>();
		for (Donor d : project.getDonors()) {
			String desc;
			if (d.getNotSpecified()) { desc = translate("project.donor.notSpecified"); }
			else if (d.getContribType()==4 && d.getDescription().equals("state-public")) { desc = translate("project.donor.statePublic"); }
			else { desc = d.getDescription(); }
			donorsByOrder.put(d.getOrderBy(), desc);
		}
		
		report.getParams().put("projectName", project.getProjectName());
		report.getParams().put("incomeGen", project.getIncomeGen());
		report.getParams().put("donors", donorsByOrder);
		report.getParams().put("perYearContributions", project.isPerYearContributions());
		String reportName = project.getIncomeGen() ? "F: "+translate("project.report.contributions") : "G: "+translate("project.report.contributions");
		report.getParams().put("reportname", reportName);
		
		runReport(report);
		return report;
	}
	
	public class PatternsPerYear {
		int year;
		List<PatternQty> qtys;
		public List getQtys() {
			return qtys;
		}
		public int getYear() {
			return year;
		}
		public PatternsPerYear (int year, List<PatternQty> qs) {
			this.year=year;
			qtys=qs;
		}
	} 
	public class PatternQty {
		private double qty;
		public PatternQty(double q) {
			this.qty=q;
		}
		public double getQty() {
			return qty;
		}
	}
	
	
	public ReportWrapper projectProduction(Project project, int startPage, boolean without) {
		Set<? extends BlockBase> blocks = without ? project.getBlocksWithout() : project.getBlocks();
		
		List<PatternsPerYear> patternsPerYear = new ArrayList<PatternsPerYear>(project.getDuration());
		
		for (int i=0;i<project.getDuration();i++) {
			ArrayList<PatternQty> patterns = new ArrayList<PatternQty>(blocks.size());
			for (BlockBase b : blocks) {
				patterns.add(new PatternQty(b.getPatterns().get(i+1).getQty()));
			}
			patternsPerYear.add(new PatternsPerYear(i+1,patterns));
		}
		
		ReportWrapper report = new ReportWrapper("/reports/project/projectProductionYears.jasper", true, patternsPerYear, "projectProduction.pdf", startPage);
		
		JasperReport jrQtys = reportLoader.compileReport("/reports/project/projectProductionQtys.jasper");
		report.getParams().put("qtysSubReport", jrQtys);
		
		JasperReport jrBlocks = reportLoader.compileReport("/reports/project/projectProductionBlocks.jasper");
		report.getParams().put("blocksSubReport", jrBlocks);
		
		report.getParams().put("blocks", new ReportSource(blocks));
		report.getParams().put("without", without);
		report.getParams().put("projectName", project.getProjectName());
		report.getParams().put("incomeGen", project.getIncomeGen());
		report.getParams().put("duration", project.getDuration());
		report.getParams().put("withWithout", project.isWithWithout());
		report.getParams().put("lengthUnits", lengthUnits());
		String reportName = project.getIncomeGen() ? translate("project.report.production") : translate("project.report.production.nonGen");
		report.getParams().put("reportname", "E: "+reportName);
		
		
		runReport(report);
		return report;
	}
	
	public ReportWrapper projectGeneralDetail(Project project, boolean withoutProject, int startPage) {
		ReportWrapper report;
		
		report = new ReportWrapper("/reports/project/projectGeneral.jasper", true, project, "projectGeneralDetail.pdf", startPage);
	
		JasperReport jrSupplies = project.isPerYearGeneralCosts()
				? reportLoader.compileReport("/reports/project/projectGeneralPerYear.jasper")
				: reportLoader.compileReport("/reports/project/projectGeneralDetail.jasper");
		report.getParams().put("suppliesSubReport", jrSupplies);
		
		JasperReport jrPersonnel = project.isPerYearGeneralCosts()
				? reportLoader.compileReport("/reports/project/projectGeneralPerYear.jasper")
				:  reportLoader.compileReport("/reports/project/projectGeneralDetail.jasper");
		report.getParams().put("personnelSubReport", jrPersonnel);
		
		if (project.isPerYearGeneralCosts()) {
			JasperReport jrPerYearData = reportLoader.compileReport("/reports/project/projectGeneralPerYearData.jasper");
			report.getParams().put("perYearDataSubreport", jrPerYearData);
		}
		
		report.getParams().put("withoutProject", withoutProject);
		
		report.getParams().put("labourTypes", this.labourTypes());
		report.getParams().put("reportname", "D: "+translate("project.report.generalCostsDetail"));
		
		runReport(report);
		return report;
	}
	
	public ReportWrapper projectGeneralNongen(Project project, int startPage) {
			ReportWrapper report = new ReportWrapper("/reports/project/projectGeneralNongen.jasper", true, project, "projectGeneralDetail.pdf", startPage);
			JasperReport jrAssets = reportLoader.compileReport("/reports/project/projectGeneralNongenAssets.jasper");
			report.getParams().put("assetsSubReport", jrAssets);
			JasperReport jrLabour = reportLoader.compileReport("/reports/project/projectGeneralNongenLabour.jasper");
			report.getParams().put("labourSubReport", jrLabour);
			JasperReport jrService = reportLoader.compileReport("/reports/project/projectGeneralNongenService.jasper");
			report.getParams().put("serviceSubReport", jrService);
		report.getParams().put("labourTypes", this.labourTypes());
		report.getParams().put("reportname", "D: "+translate("project.report.generalCostsDetail"));
		
		runReport(report);
		return report;
	}
	
	public ReportWrapper projectInvestmentDetail(Project project, boolean withoutProject, int startPage) {
		ReportWrapper report = new ReportWrapper("/reports/project/projectInvestDetail.jasper", true, project, "projectInvestmentDetail.pdf", startPage);
		
		JasperReport jrAssets = reportLoader.compileReport("/reports/project/projectInvestDetailAssets.jasper");
		report.getParams().put("assetsSubReport", jrAssets);
		JasperReport jrLabour = reportLoader.compileReport("/reports/project/projectInvestDetailLabour.jasper");
		report.getParams().put("labourSubReport", jrLabour);
		JasperReport jrService = reportLoader.compileReport("/reports/project/projectInvestDetailService.jasper");
		report.getParams().put("serviceSubReport", jrService);

		report.getParams().put("labourTypes", this.labourTypes());
		report.getParams().put("reportname", "C: "+translate("project.report.investDetail"));
		report.getParams().put("without", withoutProject);
		
		runReport(report);
		return report;
	}
	
	public ReportWrapper projectDescription(Project project, int startPage) {
		ReportWrapper report = new ReportWrapper("/reports/project/projectDescription.jasper", false, project, "projectDescription.pdf", startPage);
		report.getParams().put("reportname", "B: "+translate("project.report.general"));
		
		runReport(report);
		return report;
	}
	
	public ReportWrapper projectSummary(Project project, ProjectResult pr, int startPage) {
		ReportWrapper report = new ReportWrapper("/reports/project/projectSummary.jasper", false, project, "projectSummary.pdf", startPage);
		report.getParams().put("result", pr);
		report.getParams().put("reportname", "A: "+translate("project.report.summary"));
		
		if (project.getIncomeGen()) {
			report.getParams().put("yearsNeg", pr.getNegativeYears());
		}
			
		double investAssets = 0.0;
		double investLabour = 0.0;
		double investService= 0.0;
		
		for (ProjectItem asset : project.getAssets())
			investAssets += asset.getUnitNum()*asset.getUnitCost();
		for (ProjectItemLabour labour : project.getLabours())
			investLabour += labour.getUnitNum()*labour.getUnitCost();
		for (ProjectItemService service : project.getServices())
			investService += service.getUnitNum()*service.getUnitCost();
		
		report.getParams().put("investAssets", investAssets);
		report.getParams().put("investLabour", investLabour);
		report.getParams().put("investService", investService);
			
		runReport(report);
		return report;
	}
	
	private void addHeader(ReportWrapper report) {
		String layout = report.isLandscape() ? "landscape" : "portrait";
		JasperReport jrHeader = reportLoader.compileReport("/reports/header_"+layout+".jasper");
		report.getParams().put("header", jrHeader);
		JasperReport jrFooter = reportLoader.compileReport("/reports/footer_"+layout+".jasper");
		report.getParams().put("footer", jrFooter);
	}
	
	private void runReport(ReportWrapper report) {
		addHeader(report);
		Locale locale = LocaleContextHolder.getLocale();
		report.getParams().put("REPORT_RESOURCE_BUNDLE", new MessageSourceResourceBundle(messageSource, locale));
		report.getParams().put("rivConfig", rivConfig);
		report.getParams().put("startPage", report.getStartPage());
		
//		JRSwapFile swapFile = new JRSwapFile("tmp", 1024, 1024);
//		JRSwapFileVirtualizer virtualizer = new JRSwapFileVirtualizer(2, swapFile, true);
//		JRFileVirtualizer virtualizer = new JRFileVirtualizer(2, System
//                .getProperty("java.io.tmpdir"));
//		virtualizer.setReadOnly(true);
//		report.getParams().put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		
		
		String template = locale.getLanguage().equals("ar") 
				? report.getReportTemplate().replace(".jasper", "_ar.jasper")
				: report.getReportTemplate();
		JasperReport jr = reportLoader.compileReport(template);
		JasperPrint jp=null;
		try {
			jp= JasperFillManager.fillReport(jr, report.getParams(), report.getDataSource());
		} catch (Exception e) {
			throw new RuntimeException("Error running report "+report.getReportTemplate()+"\n",e);
		} finally {
//			virtualizer.cleanup();
		}
		report.setJp(jp);
	}
	
	private HashMap<Integer, String> lengthUnits() {
		HashMap<Integer, String> lengthUnits = new HashMap<Integer, String>();
		lengthUnits.put(0, translate("units.months"));
		lengthUnits.put(1, translate("units.weeks"));
		lengthUnits.put(2, translate("units.days.calendar"));
		lengthUnits.put(3, translate("units.days.week"));
		return lengthUnits;
	}
	
	private HashMap<String, String> labourTypes(){
		HashMap<String, String>labourTypes=new HashMap<String, String>();
		labourTypes.put("0", translate("units.pyears"));
		labourTypes.put("1", translate("units.pmonths"));
		labourTypes.put("2", translate("units.pweeks"));
		labourTypes.put("3", translate("units.pdays"));
		return labourTypes;
	}
	
	private ArrayList<String> months(Project project) {
		ArrayList<String> months = new ArrayList<String>();
		for (int i=0;i<12;i++) {
			String monthName= (i+project.getStartupMonth()<=12)
					? translate("calendar.month."+(i+project.getStartupMonth()))
					: translate("calendar.month."+((i+project.getStartupMonth())%12));
			months.add(monthName);
		}
		return months;
	}
	
	
	
	public void export(HttpServletResponse response, ReportWrapper report) {
		// export report
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JRPdfExporter exporter = new JRPdfExporter();
		// Here we assign the parameters jp and baos to the exporter
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, report.getJp());
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
		try {
			exporter.exportReport();
		} catch (JRException e) {
			LOG.error("Error exporting report to output stream.",e);
		}
				
		response.setHeader("Content-Disposition", "inline; filename="+ report.getFilename());
		response.setContentType(MEDIA_TYPE_PDF);
		response.setContentLength(baos.size());
		try {
			try {
				ServletOutputStream outputStream = response.getOutputStream();
				baos.writeTo(outputStream);
				outputStream.flush();
			} finally {
				baos.close();
			}
		} catch (Exception e) {
			LOG.error("Unable to write report to the output stream.",e);
			throw new RuntimeException(e);
		}
		report=null;
	}
	
	private String translate(String messageCode) {
		return messageSource.getMessage(messageCode, new Object[0], "", LocaleContextHolder.getLocale());
	}
}
