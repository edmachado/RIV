package riv.web.controller;
 
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

import riv.objects.config.User;
import riv.objects.project.Donor;
import riv.objects.project.Project;
import riv.util.validators.DonorValidator;
import riv.web.config.RivConfig;
import riv.web.service.DataService;
 
@Controller
@RequestMapping({"/project/donor"})
public class ProjectDonorController {
	@Autowired
    private DataService dataService;
	@Autowired
	private RivConfig rivConfig;
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(new DonorValidator());
	}
	
	@ModelAttribute("donor")
	public Donor getItem(@PathVariable Integer id, @RequestParam(required=false) String type, @RequestParam(required=false) Integer projectId)  {
		Donor d;
		if (id != -1) { d= dataService.getDonor(id); }
		else {
			Project p = dataService.getProject(projectId, 2);
			d = new Donor();
			d.setProject(p);
		}
		return d;
	}
	
    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    public String getItem(@ModelAttribute Donor donor, Model model, HttpServletRequest request) {
    	setupPageAttributes(donor, model, request);
    	return "project/donor";
    }
    
    @RequestMapping(value="/{id}", method=RequestMethod.POST)
	public String saveDonor(HttpServletRequest request, @Valid @ModelAttribute Donor donor, BindingResult result, Model model) {
    	if (result.hasErrors()) {
			setupPageAttributes(donor, model, request);
			return "project/donor";
		} else {
			dataService.storeDonor(donor);
			return "redirect:"+successView(donor);
		}
    }
    
    @RequestMapping(value="/{id}/delete", method=RequestMethod.GET)
    public String delete(@ModelAttribute Donor donor) {
    	String view = "../"+successView(donor);
    	dataService.deleteDonor(donor);
		return "redirect:"+view;
    }
    
    private String successView(Donor d) {
    	int projectId = d.getProject().getProjectId();
    	return "../step2/"+projectId;
    }
    
    private void setupPageAttributes(Donor d, Model model, HttpServletRequest request) {
		Project p = d.getProject();
    	User u = (User)request.getAttribute("user");
		model.addAttribute("accessOK", p.isShared() || p.getTechnician().getUserId().equals(u.getUserId()));
		model.addAttribute("currentStep", 2);
		model.addAttribute("currentId",p.getProjectId());
		model.addAttribute("wizardStep",p.getWizardStep());
		if (p.getIncomeGen()) {
			model.addAttribute("menuType","project");
		} else {
			model.addAttribute("menuType","projectNoninc");
		}
	}
}