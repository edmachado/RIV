package riv.web.controller;
 
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

import riv.objects.config.User;
import riv.objects.profile.Profile;
import riv.objects.profile.ProfileProduct;
import riv.objects.profile.ProfileProductBase;
import riv.objects.profile.ProfileProductWithout;
import riv.util.validators.ProfileProductValidator;
import riv.web.config.RivConfig;
import riv.web.service.DataService;
 
@Controller
@RequestMapping({"/profile/product"})
public class ProfileProductController {
	@Autowired
    private DataService dataService;
	@Autowired
	private RivConfig rivConfig;
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(new ProfileProductValidator());
		CustomNumberEditor customNumberEditor = new CustomNumberEditor(Double.class, rivConfig.getSetting().getDecimalFormat(), true);
		binder.registerCustomEditor(Double.class, "unitNum", customNumberEditor);
		binder.registerCustomEditor(Double.class, "cycleLength", customNumberEditor);
		binder.registerCustomEditor(Double.class, "cyclePerYear", customNumberEditor);
	}
	
	@ModelAttribute("profileProduct")
	public ProfileProductBase getItem(@PathVariable Integer id, @RequestParam(required=false) Integer profileId, @RequestParam(required=false) String without) throws Exception {
		ProfileProductBase pp;
		if (id!=-1) {
			pp =dataService.getProfileProduct(id);
		} else {
			Profile p = dataService.getProfile(profileId, 6);
			if (without==null) {
				pp = new ProfileProduct();
				pp.setOrderBy(p.getProducts().size());
			} else {
				pp = new ProfileProductWithout();
				pp.setOrderBy(p.getProductsWithout().size());
			}
			pp.setProfile(p);
		}
		return pp;
	}
	
    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    public String getItem(@ModelAttribute("profileProduct") ProfileProductBase profileProduct, Model model, HttpServletRequest request) {
    	setModelAttributes(profileProduct, model, request);
    	return "profile/profile6desc";
    }
    
    private void setModelAttributes(ProfileProductBase pp, Model model, HttpServletRequest request) {
    	model.addAttribute("accessOK",pp.getProfile().getShared() || ((User)request.getAttribute("user")).getUserId().equals(pp.getProfile().getTechnician().getUserId()));
    	model.addAttribute("currentStep",6);
    	model.addAttribute("currentId",pp.getProfile().getProfileId());
    	model.addAttribute("wizardStep",pp.getProfile().getWizardStep());
    	model.addAttribute("isWithout", pp.getClass()==ProfileProductWithout.class);
    	if (pp.getProfile().getIncomeGen()) {
    		model.addAttribute("menuType","profile");
    	} else {
    		model.addAttribute("menuType","profileNoninc");
    	}
    }
    
    @RequestMapping(value="/{id}", method=RequestMethod.POST)
	public String saveProfileItem(@Valid @ModelAttribute("profileProduct") ProfileProductBase profileProduct, BindingResult result, Model model, HttpServletRequest request) {
    	if (result.hasErrors()) {
    		setModelAttributes(profileProduct, model, request);
			return "profile/profile6desc";
		} else {
			dataService.storeProfileProduct(profileProduct);
			return "redirect:"+successView(profileProduct);
		}
    }
    
    @RequestMapping(value="/{id}/clone", method=RequestMethod.GET)
    public String clone(@ModelAttribute("profileProduct") ProfileProductBase profileProduct, HttpServletRequest request) {
    	String view;
    	User u = (User) request.getAttribute("user");
    	Profile p = dataService.getProfile(profileProduct.getProfile().getProfileId(), 6);
    	if (p.isShared() || p.getTechnician().getUserId().equals(u.getUserId())) {
    		profileProduct = dataService.getProfileProduct(profileProduct.getProductId(), "all");
    		ProfileProductBase newPP = profileProduct.copy(profileProduct.getClass());
    		newPP.setOrderBy(newPP.getClass()==ProfileProduct.class ? p.getProducts().size() : p.getProductsWithout().size());
    		p.addProfileProduct(newPP);
    		dataService.storeProfileProduct(newPP);
    		view = "../"+newPP.getProductId()+"?rename=true";
    	} else {
    		view = "../"+successView(profileProduct);
    	}
    	
    	return "redirect:"+view;
    }
    
    @RequestMapping(value="/{id}/delete", method=RequestMethod.GET)
    public String delete(@ModelAttribute("profileProduct") ProfileProductBase profileProduct) {
    	String view = "../"+successView(profileProduct);
    	dataService.deleteProfileProduct(profileProduct);
    	
    	// if last product has been removed, can't be considered complete anymore
    	if (profileProduct.getClass()==ProfileProduct.class && profileProduct.getOrderBy()==0) {
    		Profile p = dataService.getProfile(profileProduct.getProfile().getProfileId(), 6);
    		if (p.getProducts().size()==0) {
    			p.setWizardStep(6);
    			dataService.storeProfile(p, false);
    			dataService.deleteProfileResult(p.getProfileId());
    		}
    	}
    	
    	return "redirect:"+view;
    }
    
    @RequestMapping(value="/{id}/move", method=RequestMethod.GET)
    public String move(@ModelAttribute("profileProduct") ProfileProductBase profileProduct, @RequestParam Boolean up) {
    	String view = "../"+successView(profileProduct);
    	dataService.moveProfileProduct(profileProduct, up);
    	return "redirect:"+view;
    }
    
    private String successView(ProfileProductBase pp) {
    	return "../step6/"+pp.getProfile().getProfileId()+"#b"+pp.getProductId();
    }
   
}