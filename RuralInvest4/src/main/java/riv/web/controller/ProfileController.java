package riv.web.controller;
 
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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

import riv.objects.AttachedFile;
import riv.objects.FilterCriteria;
import riv.objects.config.FieldOffice;
import riv.objects.config.Status;
import riv.objects.config.User;
import riv.objects.profile.Profile;
import riv.objects.project.Project;
import riv.util.validators.ProfileValidator;
import riv.web.config.RivConfig;
import riv.web.editors.AppConfigEditor;
import riv.web.service.AttachTools;
import riv.web.service.DataService;

@Controller
@RequestMapping({"/profile"})
public class ProfileController {
	static final Logger LOG = LoggerFactory.getLogger(ProfileController.class);
	
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
	
	@InitBinder("profile")
	protected void initBinder(WebDataBinder binder, @PathVariable Integer step, HttpServletRequest request) {
		binder.setValidator(new ProfileValidator(step, rivConfig, messageSource));

		if (step==1) {
			binder.registerCustomEditor(FieldOffice.class, "fieldOffice", new AppConfigEditor(rivConfig, FieldOffice.class));
			request.setAttribute("fieldOfficeValues", rivConfig.getFieldOffices().values());
			binder.registerCustomEditor(Status.class, "status", new AppConfigEditor(rivConfig, Status.class));
			request.setAttribute("statusValues", rivConfig.getStatuses().values());
			
			DecimalFormat df = rivConfig.getSetting().getDecimalFormat();
			CustomNumberEditor customNumberEditor = new CustomNumberEditor(Double.class, df, true);
			binder.registerCustomEditor(Double.class, "exchRate", customNumberEditor);
			binder.registerCustomEditor(Double.class, "benefNum", customNumberEditor);
			binder.registerCustomEditor(Double.class, "benefFamilies", customNumberEditor);
		} else if (step==8) {
			CustomDateEditor de = new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true);
			binder.registerCustomEditor(java.util.Date.class, de);
		}
	}
	
	@ModelAttribute("profile")
	public Profile profile(@PathVariable Integer id, @PathVariable Integer step, HttpServletRequest request) {
		Profile p;
		if (id==-1) {
			p=new Profile();
			p.setUniqueId(UUIDGenerator.getInstance().generateTimeBasedUUID().toByteArray());
			User u = (User) request.getAttribute("user");
			if (request.getParameter("incgen").equals("true")) { p.setIncomeGen(true); }
			p.setWizardStep(1);
			p.setTechnician(u);
			p.setPrepDate(new Date());
			p.setCreatedBy(u.getDescription() + " ("+u.getOrganization()+")");
			p.setFieldOffice(rivConfig.getFieldOffices().get(-4));
			if (rivConfig.getStatuses().containsKey(-20)) {
				p.setStatus(rivConfig.getStatuses().get(-20));
			} else {
				p.setStatus(rivConfig.getStatuses().get(-7));
			}
			
		} else {
			p=dataService.getProfile(id, step);
		}
		return p;
	}
	
	@RequestMapping(value="step{step}/{id}/clone", method=RequestMethod.GET)
	public String clone(@PathVariable Integer step, @PathVariable Integer id, @ModelAttribute Profile profile, HttpServletRequest request) {
		Profile newProf = dataService.getProfile(profile.getProfileId(), -1).copy(false);
		newProf.importRefLinks();
		newProf.setUniqueId(UUIDGenerator.getInstance().generateTimeBasedUUID().toByteArray());		
		newProf.setPrepDate(new Date());
		User u = (User) request.getAttribute("user");
		newProf.setCreatedBy(u.getDescription() + " ("+u.getOrganization()+")");
		newProf.setTechnician((User)request.getAttribute("user"));
		newProf.setWizardStep(1);
		
		dataService.storeProfile(newProf, false);
		attachTools.copyAttached(profile, newProf);
		return "redirect:../../../profile/step1/"+newProf.getProfileId()+"?rename=true";
	}
	
	@RequestMapping(value="step{step}/{id}/upgrade", method=RequestMethod.GET)
	public String upgradeToProject(@PathVariable Integer step, @PathVariable Integer id, @ModelAttribute Profile profile, HttpServletRequest request) {
		User u = (User) request.getAttribute("user");
		Project proj = dataService.getProfile(id, -1).convertToProject();
		proj.setTechnician(u);
		proj.setDuration(rivConfig.getSetting().getMaxDuration());
		dataService.storeProject(proj, false);
		attachTools.copyAttached(profile, proj);
		return "redirect:../../../project/step1/"+proj.getProjectId();
	}
	
	@RequestMapping(value="step{step}/{id}/delete", method=RequestMethod.GET)
	public String delete(@PathVariable Integer step, @PathVariable Integer id, @ModelAttribute Profile profile) {
		boolean isComplete=profile.getWizardStep()==null;
		String objType=profile.getIncomeGen() ? "igpf" : "nigpf";
		
		dataService.deleteProfile(profile);
		
		filter = new FilterCriteria();
		filter.setObjType(objType);
		filter.setUnfinished(!isComplete);
		return "redirect:../../../search/results";
	}
	
	@RequestMapping(value="/step{step}/{id}", method=RequestMethod.GET)
	public String getProfile(@PathVariable Integer step, @PathVariable Integer id, @ModelAttribute Profile profile, Model model, HttpServletRequest request) {
		setupPageAttributes(profile, model, step, request);
		return step!=7 ? "profile/profile"+step : "reference/listRefs";
	}
	
	@RequestMapping(value="/step{step}/{id}", method=RequestMethod.POST)
	public String saveProfile(@PathVariable Integer step, @PathVariable Integer id, HttpServletRequest request,
			@Valid @ModelAttribute Profile profile, BindingResult result, Model model) {
		if (result.hasErrors()) {
			setupPageAttributes(profile, model, step, request);
			return "profile/profile"+step;
		} else {
			Integer newWizardStep;
			boolean calculateResult=false;
			
			// set wizard step and determine whether to calculate profileResult
			if (profile.getWizardStep()!=null  // profile still incomplete
					&& step==profile.getWizardStep()) { // on highest step reached by profile
				if (step==6) { // after step six mark as complete
					newWizardStep=null;
					calculateResult=true;
				} else { // raise step by one
					newWizardStep=step+1;
				}
				profile.setWizardStep(newWizardStep);
			} else if (profile.getWizardStep()==null && step==1) { // only step 1 affects profileResult
				calculateResult=true;
			}
			dataService.storeProfile(profile, calculateResult);

			return "redirect:../step"+(step+1)+"/"+profile.getProfileId();
		}
	}
	
	private void setupPageAttributes(Profile p, Model model, Integer step, HttpServletRequest request) {
		User u = (User)request.getAttribute("user");
		model.addAttribute("accessOK", p.isShared() || p.getTechnician().getUserId().equals(u.getUserId()));
		model.addAttribute("currentStep", step.toString());
		model.addAttribute("currentId",p.getProfileId());
		model.addAttribute("wizardStep",p.getWizardStep());
		if (p.getIncomeGen()) { 
			model.addAttribute("menuType","profile");
		} else { 
			model.addAttribute("menuType","profileNoninc");
		}
		if (step==1 && p.getProfileId()!=null) {
			long dirSize=0L;
			List<AttachedFile> files = attachTools.getAttached(p.getProfileId(), false, false);
			for (AttachedFile pf : files) {
				dirSize+=pf.getLength();
			}
			model.addAttribute("files",files);
			model.addAttribute("dirSize", attachTools.humanReadableInt(dirSize));
			model.addAttribute("freeSpace", attachTools.humanReadableInt(AttachTools.dirSizeLimit-dirSize));
		}
		if (step==9) {
			model.addAttribute("result",dataService.getProfileResult(p.getProfileId()));
		}
		
	}
}
