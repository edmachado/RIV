package riv.web.controller;
 
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
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

import riv.objects.AttachedFile;
import riv.objects.FilterCriteria;
import riv.objects.config.AppConfig1;
import riv.objects.config.AppConfig2;
import riv.objects.config.Beneficiary;
import riv.objects.config.EnviroCategory;
import riv.objects.config.FieldOffice;
import riv.objects.config.ProjectCategory;
import riv.objects.config.Status;
import riv.objects.config.User;
import riv.objects.project.Project;
import riv.objects.project.ProjectFinanceNongen;
import riv.objects.project.ProjectFirstYear;
import riv.objects.project.ProjectResult;
import riv.util.CurrencyFormat;
import riv.util.validators.ProjectValidator;
import riv.web.config.RivConfig;
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
	FilterCriteria filter;
	@Autowired
	AttachTools attachTools;
	
	@InitBinder
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
			binder.registerCustomEditor(BigDecimal.class, "wcAmountRequired", currencyEditor);
			binder.registerCustomEditor(BigDecimal.class, "wcAmountFinanced", currencyEditor);
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
			p.setPrepDate(new Date());
			p.setCreatedBy(u.getDescription() + " ("+u.getOrganization()+")");
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
		} else { 
			p= dataService.getProject(id, step);
		}
		return p;
	}
	
	@RequestMapping(value="/step{step}/{id}", method=RequestMethod.GET)
	public String getProject(@PathVariable Integer step, @PathVariable Integer id, @ModelAttribute Project project, Model model, HttpServletRequest request) {
		setupPageAttributes(project, model, step, request);
		return getView(project, step);
	}
	
	private String getView(Project project, int step) {
		String view;
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
	
	@RequestMapping(value="/step{step}/{id}", method=RequestMethod.POST)
	public String saveProject(@PathVariable Integer step, @PathVariable Integer id, HttpServletRequest request, @RequestParam(required=false) Integer oldDuration,
			@Valid Project project, BindingResult result, Model model) {
		if (result.hasErrors()) {
			setupPageAttributes(project, model, step, request);
			return "project/project"+step;
		} else {
			updateWizardStep(project, step);
			
			boolean durationChanged = step==1 && oldDuration!=project.getDuration();
			boolean withWithoutChanged = step==1 && project.getIncomeGen() && Boolean.parseBoolean(request.getParameter("oldWithWithout"))!=project.isWithWithout();
			
			// should project results be calculated?
			boolean calculateResult = project.getWizardStep()==null && (
					step==1 || step==2
					|| (project.getIncomeGen() && step==11)
					|| (!project.getIncomeGen() && step==10)
				);
			
			// special case:
			// project duration or with/without has changed
			if (id!=-1 && (durationChanged || withWithoutChanged)) {
				dataService.storeProject(project, false);
				if (durationChanged) {
					dataService.updatePatternLength(project.getProjectId(), project.getDuration(), oldDuration);
				}
				if (withWithoutChanged) {
					dataService.updateBlocksWithWithout(project.getProjectId(), project.isWithWithout());
				}
				if (project.getWizardStep()==null) {
					dataService.storeProjectResult(project.getProjectId());
				}
			// otherwise just save the project
			} else {
				dataService.storeProject(project, calculateResult);
			}
			
			return "redirect:../step"+(step+1)+"/"+project.getProjectId();
		}
	}
	
	@RequestMapping(value="step{step}/{id}/clone", method=RequestMethod.GET)
	public String clone(@PathVariable Integer step, @PathVariable Integer id, @ModelAttribute Project p, HttpServletRequest request) {
		Project newProj = dataService.getProject(p.getProjectId(), -1).copy(false);
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
	
	@RequestMapping(value="step{step}/{id}/copyContrib/{sourceYear}/{targetYear}", method=RequestMethod.GET)
	public String copyContributions(@PathVariable Integer step, @PathVariable Integer id, 
			@PathVariable Integer sourceYear, @PathVariable Integer targetYear, 
			@ModelAttribute Project p, HttpServletRequest request) {
		dataService.copyContributions(p, sourceYear, targetYear);
		dataService.storeProject(p, p.getWizardStep()==null);
		return "redirect:../../../"+p.getProjectId();
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

		if (step==1 && p.getProjectId()!=null) {
			long dirSize=0L;
			List<AttachedFile> files = attachTools.getAttached(p.getProjectId(), true, false);
			for (AttachedFile pf : files) {
				dirSize+=pf.getLength();
			}
			model.addAttribute("files",files);
			model.addAttribute("dirSize", attachTools.humanReadableInt(dirSize));
			model.addAttribute("freeSpace", attachTools.humanReadableInt(AttachTools.dirSizeLimit-dirSize));
		} else if (step==10 &! p.getIncomeGen()) {
			// get yearly cash flow total
			ArrayList<ProjectFinanceNongen> data = ProjectFinanceNongen.analyzeProject(p);
			model.addAttribute("years",data);
			// group contributions by year
			model.addAttribute("contribsByYear", p.getContributionsByYear());
		} else if (step==11 && p.getIncomeGen()) {
		
			int period;
			double amount;
			
			if (p.getWizardStep()==null) {
				ProjectResult pr = dataService.getProjectResult(p.getProjectId());
				period = pr.getWcPeriod();
				amount = pr.getWorkingCapital();
			} else {
				ProjectFirstYear pfy = new ProjectFirstYear(p);
				double[] pfyResults = ProjectFirstYear.WcAnalysis(pfy);
				period = (int)pfyResults[0];
				amount=-1*pfyResults[1];
			}
			p.setWcFinancePeriod(period);
			p.setWcAmountRequired(new BigDecimal(amount));	
		}
	}
}
