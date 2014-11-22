package riv.web.controller;

import java.text.DecimalFormat;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
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

import riv.objects.Probase;
import riv.objects.config.User;
import riv.objects.profile.Profile;
import riv.objects.project.Project;
import riv.objects.reference.ReferenceCost;
import riv.objects.reference.ReferenceIncome;
import riv.objects.reference.ReferenceItem;
import riv.objects.reference.ReferenceLabour;
import riv.util.CurrencyFormat;
import riv.util.validators.RefItemValidator;
import riv.web.config.RivConfig;
import riv.web.service.DataService;
 
@Controller
@RequestMapping({"/profile/refItem","/project/refItem"})
public class ReferenceItemController {
	@Autowired
    private DataService dataService;
	@Autowired
	private RivConfig rivConfig;
	
	@InitBinder("referenceItem")
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(new RefItemValidator());
		DecimalFormat df = rivConfig.getSetting().getCurrencyFormatter().getDecimalFormat(CurrencyFormat.ALL);
		CustomNumberEditor customNumberEditor = new CustomNumberEditor(Double.class, df, true);
        binder.registerCustomEditor(Double.class, "unitCost", customNumberEditor);
        binder.registerCustomEditor(Double.class, "transport", customNumberEditor);
	}
	
	@ModelAttribute("referenceItem")
	public ReferenceItem getItem(@PathVariable Integer id, @RequestParam(required=false) String type, @RequestParam(required=false) Boolean isProject, @RequestParam(required=false) Integer proId)  {
		ReferenceItem ri;
		if (id != -1) {
			ri= dataService.getReferenceItem(id);
		} else {
			Probase p = isProject ? dataService.getProject(proId, 10) : dataService.getProfile(proId, 7);
			if (type.equals("income")) {
				ri = new ReferenceIncome();
				ri.setOrderBy(p.getRefIncomes().size());
			} else if (type.equals("cost")) {
				ri = new ReferenceCost();
				ri.setOrderBy(p.getRefCosts().size());
			} else {
				ri = new ReferenceLabour();
				ri.setOrderBy(p.getRefLabours().size());
			}
			if (isProject) { 
				ri.setProject((Project)p); 
			} else { 
				ri.setProfile((Profile)p); 
			}
		}
		return ri;
	}
	
    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    public String getItem(@ModelAttribute ReferenceItem referenceItem, Model model, HttpServletRequest request) {
    	setupPageAttributes(referenceItem, model, request);
    	return form(referenceItem);
    }
    
    @RequestMapping(value="/{id}", method=RequestMethod.POST)
	public String save(HttpServletRequest request, @PathVariable Integer id,
			@Valid @ModelAttribute ReferenceItem referenceItem, BindingResult result, Model model) {
    	if (result.hasErrors()) {
			setupPageAttributes(referenceItem, model, request);
			return form(referenceItem);
		} else {
			dataService.storeReferenceItem(referenceItem);
			if (id!=-1) { 
				dataService.updateReferenceLinks(referenceItem);
			}
			return "redirect:"+successView(referenceItem);
		}
    }
    
    @RequestMapping(value="/{id}/move", method=RequestMethod.GET)
    public String move(@ModelAttribute ReferenceItem referenceItem, @RequestParam Boolean up) {
    	String view = "../"+successView(referenceItem);
    	dataService.moveReferenceItem(referenceItem, up);
    	return "redirect:"+view;
    }
    

    @RequestMapping(value="/{id}/delete", method=RequestMethod.GET)
    public String delete(@PathVariable Integer id, @ModelAttribute ReferenceItem referenceItem) {
    	String view = "../"+successView(referenceItem);
    	dataService.deleteReferenceItem(referenceItem);
		return "redirect:"+view;
    }
    
    private String successView(ReferenceItem pi) {
    	Probase p = pi.getProbase();
    	int step= p.isProject() ? p.getIncomeGen() ? 10 : 11 :7;
    	return "../step"+step+"/"+p.getProId();
    }
    
    private String form(ReferenceItem pi) {
    	String form;
    	if (pi.getClass().isAssignableFrom(ReferenceIncome.class)) {
    		form="reference/income";
    	} else if (pi.getClass().isAssignableFrom(ReferenceCost.class)) {
    		form="reference/cost";
    	} else {
    		form="reference/labour";
    	}
    	return form;
    }
    
    private void setupPageAttributes(ReferenceItem pi, Model model, HttpServletRequest request) {
		Probase p = pi.getProbase();
    	User u = (User)request.getAttribute("user");
		model.addAttribute("accessOK", p.isShared() || p.getTechnician().getUserId().equals(u.getUserId()));
		int curStep = p.isProject() ? p.getIncomeGen() ? 10 : 11 : 7;
		model.addAttribute("currentStep", curStep);
		model.addAttribute("currentId",p.getProId());
		model.addAttribute("wizardStep",p.getWizardStep());
		String menuType = p.isProject() ? "project" : "profile";
		if (p.getIncomeGen()) { 
			menuType=menuType+"Noninc";
		} 
		model.addAttribute("menuType",menuType);
	}
   
}