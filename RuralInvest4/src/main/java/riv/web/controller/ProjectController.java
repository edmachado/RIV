package riv.web.controller;
 
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.safehaus.uuid.UUIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import riv.objects.AttachedFile;
import riv.objects.FilterCriteria;
import riv.objects.FinanceMatrix;
import riv.objects.FinanceMatrix.ProjectScenario;
import riv.objects.ProjectFinanceNongen;
import riv.objects.config.AppConfig1;
import riv.objects.config.AppConfig2;
import riv.objects.config.Beneficiary;
import riv.objects.config.EnviroCategory;
import riv.objects.config.FieldOffice;
import riv.objects.config.ProjectCategory;
import riv.objects.config.Setting;
import riv.objects.config.Status;
import riv.objects.config.User;
import riv.objects.project.Donor;
import riv.objects.project.HasPerYearItems;
import riv.objects.project.PerYearItem;
import riv.objects.project.Project;
import riv.objects.project.ProjectResult;
import riv.util.CurrencyFormat;
import riv.util.CurrencyFormatter;
import riv.util.validators.ProjectValidator;
import riv.web.config.RivConfig;
import riv.web.config.RivLocaleResolver;
import riv.web.editors.AppConfigEditor;
import riv.web.service.AttachTools;
import riv.web.service.DataService;

@Controller
@RequestMapping({"/project"})
public class ProjectController {
	static final Logger LOG = LoggerFactory.getLogger(ProjectController.class);
	
	@Autowired
    private DataService dataService;
	@Autowired
    private RivConfig rivConfig;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private RivLocaleResolver localeResolver;
	@Autowired
	FilterCriteria filter;
	@Autowired
	AttachTools attachTools;
	
	@RequestMapping(value="/step{step}/{id}/project.properties", method=RequestMethod.GET)
	public void projectRivExport(@PathVariable int step, @PathVariable int id, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String lineSeparator = System.getProperty("line.separator");
		Project p = dataService.getProject(id, -1, true);
		response.setHeader("Content-disposition", "attachment; filename=project.properties");
		Setting setting = ((RivConfig)request.getAttribute("rivConfig")).getSetting();
		CurrencyFormatter cf = setting.getCurrencyFormatter();
		
		byte[] output = p.testFile(rivConfig).getBytes("UTF8");
		response.getOutputStream().write(output);
		
		ProjectResult pr = p.getProjectResult(setting);
		StringBuilder sb = new StringBuilder();
		
		sb.append("result.investTotal="+cf.formatCurrency(pr.getInvestmentTotal(), CurrencyFormat.ALL)+lineSeparator);
		sb.append("result.investOwn="+cf.formatCurrency(pr.getInvestmentOwn(), CurrencyFormat.ALL)+lineSeparator);
		sb.append("result.investDonate="+cf.formatCurrency(pr.getInvestmentDonated(), CurrencyFormat.ALL)+lineSeparator);
		sb.append("result.investFinance="+cf.formatCurrency(pr.getInvestmentFinanced(), CurrencyFormat.ALL)+lineSeparator);
		sb.append("result.employ="+cf.formatCurrency(pr.getAnnualEmployment(), CurrencyFormat.ALL)+lineSeparator);
		sb.append("result.income="+cf.formatCurrency(pr.getAnnualNetIncome(), CurrencyFormat.ALL)+lineSeparator);
		sb.append("result.wcTotal="+cf.formatCurrency(pr.getWorkingCapital(), CurrencyFormat.ALL)+lineSeparator);
		sb.append("result.wcOwn="+cf.formatCurrency(pr.getWcOwn(), CurrencyFormat.ALL)+lineSeparator);
		sb.append("result.wcDonate="+cf.formatCurrency(pr.getWcDonated(), CurrencyFormat.ALL)+lineSeparator);
		sb.append("result.wcFinance="+cf.formatCurrency(pr.getWcFinanced(), CurrencyFormat.ALL)+lineSeparator);
		sb.append("result.costTotal="+cf.formatCurrency(pr.getTotalCosts(), CurrencyFormat.ALL)+lineSeparator);
		sb.append("result.costOwn="+cf.formatCurrency(pr.getTotalCostsOwn(), CurrencyFormat.ALL)+lineSeparator);
		sb.append("result.costDonate="+cf.formatCurrency(pr.getTotalCostsDonated(), CurrencyFormat.ALL)+lineSeparator);
		sb.append("result.costFinance="+cf.formatCurrency(pr.getTotalCostsFinanced(), CurrencyFormat.ALL)+lineSeparator);
		
		sb.append("step11.period="+pr.getWcPeriod()+lineSeparator);
		sb.append("step11.periodAvg="+pr.getWcPeriodAvg()+lineSeparator);
		sb.append("step11.amtRequired="+cf.formatCurrency(pr.getWorkingCapital(), CurrencyFormat.ALL)+lineSeparator);
		sb.append("step11.amtFinanced="+cf.formatCurrency(pr.getWcFinanced(), CurrencyFormat.ALL)+lineSeparator);

		if (p.getIncomeGen()) { 
			sb.append("result.npvAll="+cf.formatCurrency(pr.getNpv(), CurrencyFormat.ALL)+lineSeparator);
			sb.append("result.irrAll="+((pr.getIrr().doubleValue()>1000 || pr.getIrr().doubleValue()<-1000)?"Undefined":pr.getIrr())+lineSeparator);
			sb.append("result.npvApplicant="+cf.formatCurrency(pr.getNpvWithDonation(), CurrencyFormat.ALL)+lineSeparator);
			sb.append("result.irrApplicant="+((pr.getIrrWithDonation().doubleValue()>1000 || pr.getIrrWithDonation().doubleValue()<-1000)?"Undefined":pr.getIrrWithDonation())+lineSeparator);
		} 

		sb.append("result.investDirect="+cf.formatCurrency(pr.getInvestPerBenefDirect(), CurrencyFormat.ALL)+lineSeparator);
		sb.append("result.investIndirect="+cf.formatCurrency(pr.getInvestPerBenefIndirect(), CurrencyFormat.ALL)+lineSeparator);
		sb.append("result.benefDirect="+pr.getBeneDirect()+lineSeparator);
		sb.append("result.benefIndirect="+pr.getBeneIndirect()+lineSeparator);
			
		output = sb.toString().getBytes("UTF8");
		response.getOutputStream().write(output);
	}
	
	@InitBinder("project")
	protected void initBinder(WebDataBinder binder, @PathVariable Integer step, HttpServletRequest request) {
		binder.setValidator(new ProjectValidator(step, rivConfig, messageSource));

		if (step==1) {
			binder.registerCustomEditor(FieldOffice.class, "fieldOffice", new AppConfigEditor(rivConfig, FieldOffice.class));
			binder.registerCustomEditor(Status.class, "status", new AppConfigEditor(rivConfig, Status.class));
			binder.registerCustomEditor(Beneficiary.class, "beneficiary", new AppConfigEditor(rivConfig, Beneficiary.class));
			binder.registerCustomEditor(EnviroCategory.class, "enviroCategory", new AppConfigEditor(rivConfig, EnviroCategory.class));
			binder.registerCustomEditor(ProjectCategory.class, "projCategory", new AppConfigEditor(rivConfig, ProjectCategory.class));
			binder.registerCustomEditor(AppConfig1.class, "appConfig1", new AppConfigEditor(rivConfig, AppConfig1.class));
			binder.registerCustomEditor(AppConfig2.class, "appConfig2", new AppConfigEditor(rivConfig, AppConfig2.class));
		
			DecimalFormat df = rivConfig.getSetting().getDecimalFormat();
			CustomNumberEditor customNumberEditor = new CustomNumberEditor(Double.class, df, true);
			binder.registerCustomEditor(Double.class, "exchRate", customNumberEditor);
			binder.registerCustomEditor(Double.class, "inflationAnnual", customNumberEditor);
		} else if (step==11) {
			CustomNumberEditor currencyEditor = new CustomNumberEditor(Double.class, rivConfig.getSetting().getCurrencyFormatter().getDecimalFormat(CurrencyFormat.ALL), true);
			CustomNumberEditor decimalEditor = new CustomNumberEditor(Double.class, rivConfig.getSetting().getDecimalFormat(), true);
	       
			binder.registerCustomEditor(Double.class, "loan1Amt", currencyEditor);
			binder.registerCustomEditor(Double.class, "loan1Interest", decimalEditor);
			binder.registerCustomEditor(Double.class, "capitalInterest", decimalEditor);
			binder.registerCustomEditor(Double.class, "capitalDonate", currencyEditor);
			binder.registerCustomEditor(Double.class, "capitalOwn", currencyEditor);
			binder.registerCustomEditor(Double.class, "wcAmountRequired", currencyEditor);
			binder.registerCustomEditor(Double.class, "wcAmountFinanced", currencyEditor);
			binder.registerCustomEditor(Double.class, "loan2Amt", currencyEditor);
			binder.registerCustomEditor(Double.class, "loan2Interest", decimalEditor);
			
		} else if (step==12) {
			CustomDateEditor de = new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true);
			binder.registerCustomEditor(java.util.Date.class, de);
		}
	}
	
	@ModelAttribute("project")
	public Project project(@PathVariable Integer id,  @PathVariable Integer step, HttpServletRequest request) {
		Project p;
		
		if (id==-1) {
			p=new Project();
			p.setUniqueId(UUIDGenerator.getInstance().generateTimeBasedUUID().toByteArray());
			User u = (User) request.getAttribute("user");
			if (request.getParameter("incgen").equals("true")) { p.setIncomeGen(true); }
			p.setWizardStep(1);
			p.setTechnician(u);
			p.setExchRate(rivConfig.getSetting().getExchRate());
			p.setPrepDate(new Date());
			p.setCreatedBy(u.getDescription() + " ("+u.getOrganization()+")");
			p.setLastUpdate(p.getPrepDate());
			p.setLastUpdateBy(u.getDescription() + " ("+u.getOrganization()+")");
			p.setFieldOffice(rivConfig.getFieldOffices().get(-4));
			p.setBeneficiary(rivConfig.getBeneficiaries().get(-3));
			p.setEnviroCategory(rivConfig.getEnviroCategories().get(-6));
			if (p.getIncomeGen()) {
				p.setProjCategory(rivConfig.getCategories().get(-5));
			} else {
				p.setProjCategory(rivConfig.getCategories().get(-2));
			}
			if (rivConfig.getStatuses().containsKey(-20)) {
				p.setStatus(rivConfig.getStatuses().get(-20));
			} else {
				p.setStatus(rivConfig.getStatuses().get(-7));
			}
			if (rivConfig.getSetting().getAdmin1Enabled()) {
				p.setAppConfig1(rivConfig.getAppConfig1s().get(-8));
			}
			if (rivConfig.getSetting().getAdmin2Enabled()) {
				p.setAppConfig2(rivConfig.getAppConfig2s().get(-9));
			}
			p.setDonors(new java.util.HashSet<Donor>());
			Donor d = new Donor();
			d.setDescription("unspecified");
			d.setContribType(4);
			d.setNotSpecified(true);
			d.setOrderBy(0);
			p.addDonor(d);
		} else { 
			p= dataService.getProject(id, step);
		}
		return p;
	}
	
	@RequestMapping(value="/step{step}/{id}", method=RequestMethod.GET)
	public String getProject(@PathVariable Integer step, @PathVariable Integer id, @ModelAttribute Project project, Model model, HttpServletRequest request) {
		if (project==null) { return "noProbase"; }
		setupPageAttributes(project, model, step, request);
		return getView(project, step);
	}
	
	private String getView(Project project, int step) {
		String view="";
		if (project.getIncomeGen()) {
			view = step!=10 ? "project/project"+step : "reference/listRefs";
		} else {
			if (step==8) { view = "project/project8nongen"; }
			else if (step==10) { view = "project/project10nongen"; }
			else if (step==11) { view = "reference/listRefs"; }
			else { view = "project/project"+step; }
		}
		return view;
	}
	
	private void updateWizardStep(Project project, Integer currentStep) {
		if (project.getWizardStep()!=null && currentStep==project.getWizardStep()) { // currently saving highest-reached wizard step
			Integer newWizardStep = null;
			if ((project.getIncomeGen() && currentStep==11) 
					|| (!project.getIncomeGen() && currentStep==10)) {
				newWizardStep = null;
			} else {
				newWizardStep = currentStep+1;
			}
			
			project.setWizardStep(newWizardStep);
		}
	}
	
	private void setQuickAnalysis(Project project, Locale locale) {
		String skip=messageSource.getMessage("project.quickAnalysis",new Object[]{},locale);
		project.setBenefName(skip);
		project.setBenefDesc(skip);
		project.setJustification(skip);
		project.setProjDesc(skip);
		project.setActivities(skip);
		if (rivConfig.getSetting().isAdminMisc1Enabled()) {
			project.setAdminMisc1(skip);
		}
		if (rivConfig.getSetting().isAdminMisc2Enabled()) {
			project.setAdminMisc2(skip);
		}
		if (rivConfig.getSetting().isAdminMisc2Enabled()) {
			project.setAdminMisc2(skip);
		}
		project.setTechnology(skip);
		project.setRequirements(skip);
		project.setEnviroImpact(skip);
		project.setMarket(skip);
		project.setOrganization(skip);
		project.setAssumptions(skip);
		if (!project.getIncomeGen()) {
			project.setSustainability(skip);
		}
	}
	
	@RequestMapping(value="/step{step}/{id}", method=RequestMethod.POST)
	public String saveProject(@PathVariable Integer step, @PathVariable Integer id, HttpServletRequest request, 
			@RequestParam(required=false) Boolean quickAnalysis, 
			@Valid Project project, BindingResult result, Model model,
            final RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			setupPageAttributes(project, model, step, request);
			return "project/project"+step + ((!project.getIncomeGen() && step==8) ? "nongen" : "");
		} else {
			updateWizardStep(project, step);
			
			// quick analysis (no qualitative analysis)
			if(quickAnalysis!=null) {
				if (step==1) {
					setQuickAnalysis(project, localeResolver.resolveLocale(request));
					redirectAttributes.addFlashAttribute("quickAnalysis",true);
				} else if (step==2) {
					project.setWizardStep(7);
					step=6;
				}
			}

			// should project results be calculated?
			boolean calculateResult = project.getWizardStep()==null && (
					step==1 || step==2
					|| (project.getIncomeGen() && step==11)
					|| (!project.getIncomeGen() && step==10)
				);
			dataService.storeProject(project, calculateResult);
			 
			return "redirect:../step"+(step+1)+"/"+project.getProjectId();
		}
	}
	
	@RequestMapping(value="step{step}/{id}/clone", method=RequestMethod.GET)
	public String clone(@PathVariable Integer step, @PathVariable Integer id, @ModelAttribute Project p, HttpServletRequest request) {
		Project newProj = dataService.getProject(p.getProjectId(), -1, true).copy(false, null);
		newProj.importRefLinks();
		newProj.setUniqueId(UUIDGenerator.getInstance().generateTimeBasedUUID().toByteArray());		
		newProj.setPrepDate(new Date());
		User u = (User) request.getAttribute("user");
		newProj.setCreatedBy(u.getDescription() + " ("+u.getOrganization()+")");
		newProj.setTechnician((User)request.getAttribute("user"));
		newProj.setWizardStep(1);
		
		dataService.storeProject(newProj, false);
		attachTools.copyAttached(p, newProj);
		return "redirect:../../../project/step1/"+newProj.getProjectId()+"?rename=true";
	}
	
	@RequestMapping(value="step{step}/{id}/perYearContributions", method=RequestMethod.GET)
	public String perYearContributions(@PathVariable Integer step, @PathVariable Integer id, @RequestParam(required=true) Boolean simple, 
			@ModelAttribute Project p, HttpServletRequest request) {
		
		dataService.simplifyContributions(p, simple);
		p=dataService.getProject(id, step);
		p.setPerYearContributions(!simple);
		dataService.storeProject(p, p.getWizardStep()==null);
		
		return "redirect:../../../project/step10/"+p.getProjectId();
	}
	
	@RequestMapping(value="step{step}/{id}/perYearGenerals", method=RequestMethod.GET)
	public String perYearGenerals(@PathVariable Integer step, @PathVariable Integer id, @RequestParam(required=true) Boolean simple, 
			@ModelAttribute Project p, HttpServletRequest request) {
		
		// TODO check accessOK
		
		dataService.simplifyGeneralCosts(p, simple);
		p=dataService.getProject(id, step);
		p.setPerYearGeneralCosts(!simple);
		dataService.storeProject(p, p.getWizardStep()==null);
		
		return "redirect:../../../project/step8/"+p.getProjectId();
	}
	
	@RequestMapping(value="step{step}/{id}/delete", method=RequestMethod.GET)
	public String delete(@PathVariable Integer step, @PathVariable Integer id, @ModelAttribute Project p) {
		boolean isComplete=p.getWizardStep()==null;
		String objType=p.getIncomeGen() ? "igpj" : "nigpj";
		
		dataService.deleteProject(p);
		
		filter = new FilterCriteria();
		filter.setObjType(objType);
		filter.setUnfinished(!isComplete);
		return "redirect:../../../search/results";
	}
	
	@SuppressWarnings("rawtypes")
	private Map<Integer, ArrayList<PerYearItem>> generalCostsForTable(int duration, boolean isPerYear, Set<? extends HasPerYearItems> hasItems) {
		Map<Integer, ArrayList<PerYearItem>> itemsForTable = new HashMap<Integer, ArrayList<PerYearItem>>();
		for (int i=0; i<duration; i++) {
			itemsForTable.put(i, new ArrayList<PerYearItem>());
		}
		
		for (HasPerYearItems<?> has : hasItems) {
			for (PerYearItem y : has.getYears().values()) {
				if (y.getYear()==0 || isPerYear) {
					itemsForTable.get(y.getYear()).add(y);
				}
			}
		}
		return itemsForTable;
	}
	
	private void setupPageAttributes(Project p, Model model, Integer step, HttpServletRequest request) {
		User u = (User)request.getAttribute("user");
		model.addAttribute("accessOK", p.isShared() || p.getTechnician().getUserId().equals(u.getUserId()));
		model.addAttribute("currentStep", step.toString());
		model.addAttribute("currentId",p.getProjectId());
		model.addAttribute("wizardStep",p.getWizardStep());
		if (p.getIncomeGen()) {
			model.addAttribute("menuType","project");
		} else {
			model.addAttribute("menuType","projectNoninc");
		}
		
		// step-specific data for views
		
		// attached files
		if ((step==1 || step==13) && p.getProjectId()!=null) {
			long dirSize=0L;
			List<AttachedFile> files = attachTools.getAttached(p.getProjectId(), true, false);
			for (AttachedFile pf : files) {
				dirSize+=pf.getLength();
			}
			model.addAttribute("files",files);
			model.addAttribute("dirSize", attachTools.humanReadableInt(dirSize));
			model.addAttribute("freeSpace", attachTools.humanReadableInt(AttachTools.dirSizeLimit-dirSize));
		}
		
		// per-year general cost tables
		if (step==8 && p.getIncomeGen()) {
			model.addAttribute("generalsForTable", generalCostsForTable(p.getDuration(), p.isPerYearGeneralCosts(), p.getGenerals()));
			model.addAttribute("generalsWoForTable", generalCostsForTable(p.getDuration(), p.isPerYearGeneralCosts(), p.getGeneralWithouts()));
			model.addAttribute("personnelsForTable", generalCostsForTable(p.getDuration(), p.isPerYearGeneralCosts(), p.getPersonnels()));
			model.addAttribute("personnelsWoForTable", generalCostsForTable(p.getDuration(), p.isPerYearGeneralCosts(), p.getPersonnelWithouts()));
		}
		
		if (step==12 || step==13 || (!p.getIncomeGen() && step==10)) {
			HashMap<Integer, Donor> donors = new HashMap<Integer, Donor>();
			for (Donor d : p.getDonors()) {
				donors.put(d.getOrderBy(), d);
			}
			model.addAttribute("donors",donors);
			model.addAttribute("summary",p.getDonationSummary());
		}
			
		if (!p.getIncomeGen() && step==10) {
			// get yearly cash flow total
			ArrayList<ProjectFinanceNongen> data = ProjectFinanceNongen.analyzeProject(p);
			model.addAttribute("years",data);
		} else if (p.getIncomeGen() && (step==11 || step==12 || step==13)) {
			FinanceMatrix matrix = new FinanceMatrix(p, rivConfig.getSetting().getDiscountRate(), rivConfig.getSetting().getDecimalLength());
			
			int period; double amount; double periodAvg;
			if (p.getWizardStep()==null) {
				ProjectResult pr = dataService.getProjectResult(p.getProjectId());
				period = pr.getWcPeriod();
				amount = pr.getWorkingCapital();
				periodAvg = pr.getWcPeriodAvg();
				model.addAttribute("result",pr);	
			} else {
				period = matrix.getWcPeriod();
				periodAvg = matrix.getWcPeriodAvg();
				amount= matrix.getWcValue();
			}
			p.setWcFinancePeriod(period);
			p.setWcAmountRequired(amount);
			p.setWcFinancePeriodAvg(periodAvg);
			
			if (step==12 || step==13) {
				model.addAttribute("profitabilitySummary", matrix.getSummary(false, ProjectScenario.Incremental));
				model.addAttribute("profitabilitySummaryWith", matrix.getSummary(false, ProjectScenario.With));
				model.addAttribute("profitabilitySummaryWithout", matrix.getSummary(false, ProjectScenario.Without));
				model.addAttribute("cashFlowSummary",matrix.getSummary(true, ProjectScenario.With));
				model.addAttribute("cashFlowSummaryWithout",matrix.getSummary(true, ProjectScenario.Without));
			}
			
//			model.addAttribute("firstYear", matrix.getFirstYearData()[0].getCumulative());
			model.addAttribute("firstYearSummary",matrix.getFirstYearData()[0].getSummary());
			
		} else if (!p.getIncomeGen() && (step==12 || step==13)) {
			ProjectResult pr = dataService.getProjectResult(p.getProjectId());
			model.addAttribute("result",pr);
			List<double[]> cfSummary = ProjectFinanceNongen.getSummary(ProjectFinanceNongen.analyzeProject(p));
			model.addAttribute("cashFlowSummary",cfSummary);
		}
	}
}
