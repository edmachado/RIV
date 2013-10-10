package riv.web.controller;
 
import java.math.BigDecimal;
import java.text.DecimalFormat;

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
import riv.objects.profile.ProfileProductIncome;
import riv.objects.profile.ProfileProductInput;
import riv.objects.profile.ProfileProductItem;
import riv.objects.profile.ProfileProductLabour;
import riv.objects.reference.ReferenceCost;
import riv.objects.reference.ReferenceIncome;
import riv.objects.reference.ReferenceItem;
import riv.objects.reference.ReferenceLabour;
import riv.util.CurrencyFormat;
import riv.util.validators.ProfileProductItemValidator;
import riv.web.config.RivConfig;
import riv.web.service.DataService;
 
@Controller
@RequestMapping({"/profile/prodItem"})
public class ProfileProductItemController {
	@Autowired
    private DataService dataService;
	@Autowired
	private RivConfig rivConfig;
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(new ProfileProductItemValidator());
		DecimalFormat df = rivConfig.getSetting().getCurrencyFormatter().getDecimalFormat(CurrencyFormat.ALL);
		CustomNumberEditor customNumberEditor = new CustomNumberEditor(BigDecimal.class, df, true);
        binder.registerCustomEditor(BigDecimal.class, "unitCost", customNumberEditor);
        binder.registerCustomEditor(BigDecimal.class, "transport", customNumberEditor);
        binder.registerCustomEditor(BigDecimal.class, "total", customNumberEditor);
        
        CustomNumberEditor cne2 = new CustomNumberEditor(BigDecimal.class, rivConfig.getSetting().getDecimalFormat(), true);
        binder.registerCustomEditor(BigDecimal.class, "unitNum", cne2);
	}
	
	@ModelAttribute("profileProductItem")
	public ProfileProductItem getItem(@PathVariable Integer id, @RequestParam(required=false) Integer productId, @RequestParam(required=false) String itemType) {
		ProfileProductItem pi;
		if (id!=-1) {
			pi = dataService.getProfileProductItem(id);
		} else {
			ProfileProduct pp = dataService.getProfileProduct(productId, itemType);
			if (itemType.equals("income")) {
				pi = new ProfileProductIncome();
				pi.setOrderBy(pp.getProfileIncomes().size());
			} else if (itemType.equals("input")) {
				pi = new ProfileProductInput();
				pi.setOrderBy(pp.getProfileInputs().size());
			} else {
				pi = new ProfileProductLabour();
				pi.setOrderBy(pp.getProfileLabours().size());
			}
			pi.setProfileProduct(pp);
		}
		return pi;
	}
	
    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    public String getItem(@ModelAttribute ProfileProductItem profileProductItem, Model model, HttpServletRequest request) {
    	setupPageAttributes(profileProductItem, model, request);
    	return form(profileProductItem);
    }
    
    @RequestMapping(value="/{id}", method=RequestMethod.POST)
	public String saveProfileItem(@RequestParam Integer linkedToId, @RequestParam(required=false) Boolean addLink, HttpServletRequest request,
			@Valid @ModelAttribute ProfileProductItem profileProductItem, BindingResult result, Model model) {
    	if (result.hasErrors()) {
			setupPageAttributes(profileProductItem, model, request);
			return form(profileProductItem);
		} else {
			checkLinked(profileProductItem, linkedToId, addLink);
			dataService.storeProfileProductItem(profileProductItem);
			return "redirect:"+successView(profileProductItem);
		}
    }
    
    @RequestMapping(value="/{id}/delete", method=RequestMethod.GET)
    public String delete(@ModelAttribute ProfileProductItem profileProductItem) {
    	String view = "../"+successView(profileProductItem);
    	dataService.deleteProfileProductItem(profileProductItem);
    	return "redirect:"+view;
    }
    
    @RequestMapping(value="/{id}/move", method=RequestMethod.GET)
    public String move(@ModelAttribute ProfileProductItem profileProductItem, @RequestParam Boolean up) {
    	String view = "../"+successView(profileProductItem);
    	dataService.moveProfProdItem(profileProductItem, up);
    	return "redirect:"+view;
    }
    
    @RequestMapping(value="/{id}/copy", method=RequestMethod.GET)
    public String copy(@ModelAttribute ProfileProductItem profileProductItem) {
    	ProfileProductItem newItem = profileProductItem.copy();
    	if (newItem.getClass().isAssignableFrom(ProfileProductIncome.class)) {
    		newItem.setOrderBy(newItem.getProfileProduct().getProfileIncomes().size());
    		newItem.getProfileProduct().addProfileIncome((ProfileProductIncome)newItem);
    	} else if (newItem.getClass().isAssignableFrom(ProfileProductInput.class)) {
    		newItem.setOrderBy(newItem.getProfileProduct().getProfileInputs().size());
    		newItem.getProfileProduct().addProfileInput((ProfileProductInput)newItem);
    	} else { // labour
    		newItem.setOrderBy(newItem.getProfileProduct().getProfileLabours().size());
    		newItem.getProfileProduct().addProfileLabour((ProfileProductLabour)newItem);
    	}
    	dataService.storeProfileProductItem(newItem);
    	String view = "../"+successView(profileProductItem);
    	return "redirect:"+view;
    }
    
    private String successView(ProfileProductItem pi) {
    	ProfileProduct pp = pi.getProfileProduct();
    	return "../step6/"+pp.getProfile().getProfileId()+"#b"+pp.getProductId();
    }
    
    private void checkLinked(ProfileProductItem item, Integer linkedToId, Boolean addLink) {
    	if (addLink!=null) {
    		ReferenceItem ref = null;
			if (item.getClass().isAssignableFrom(ProfileProductIncome.class)) {
				ref = new ReferenceIncome();
				ref.setOrderBy(item.getProfileProduct().getProfile().getRefIncomes().size());
				if (item.getProfileProduct().getProfile().getIncomeGen()) {
					((ReferenceIncome)ref).setTransport(((ProfileProductIncome)item).getTransport().doubleValue());
				}
			} else if (item.getClass().isAssignableFrom(ProfileProductInput.class)) {
				ref = new ReferenceCost();
				ref.setOrderBy(item.getProfileProduct().getProfile().getRefCosts().size());
				((ReferenceCost)ref).setTransport(((ProfileProductInput)item).getTransport().doubleValue());
				
			} else {
				ref = new ReferenceLabour();
				ref.setOrderBy(item.getProfileProduct().getProfile().getRefLabours().size());
			}
			ref.setDescription(item.getDescription());
			ref.setProfile(item.getProfileProduct().getProfile());
			ref.setUnitCost(item.getUnitCost().doubleValue());
			ref.setUnitType(item.getUnitType());
			
			dataService.storeReferenceItem(ref);
			item.setLinkedTo(ref);
    	} else if (linkedToId!=null) {
    		item.setLinkedTo(dataService.getReferenceItem(linkedToId));
    	} else {
    		item.setLinkedTo(null);
    	}
    }
		
    private String form(ProfileProductItem pi) {
    	String form;
    	if (pi.getClass().isAssignableFrom(ProfileProductIncome.class)) {
    		form="profile/profile6income";
    	} else if (pi.getClass().isAssignableFrom(ProfileProductLabour.class)) {
    		form="profile/profile6labour";
    	} else {
    		form="profile/profile6input";
    	}
    	return form;
    }
    
    private void setupPageAttributes(ProfileProductItem pi, Model model, HttpServletRequest request) {
		Profile p = pi.getProfileProduct().getProfile();
    	User u = (User)request.getAttribute("user");
		model.addAttribute("accessOK", p.isShared() || p.getTechnician().getUserId().equals(u.getUserId()));
		model.addAttribute("currentStep", 6);
		model.addAttribute("currentId",p.getProfileId());
		model.addAttribute("wizardStep",p.getWizardStep());
		if (p.getIncomeGen()) model.addAttribute("menuType","profile");
		else model.addAttribute("menuType","profileNoninc");
	}
   
}