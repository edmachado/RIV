package riv.web.controller;
 
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
import riv.objects.profile.ProfileItem;
import riv.objects.profile.ProfileItemGeneral;
import riv.objects.profile.ProfileItemGeneralWithout;
import riv.objects.profile.ProfileItemGood;
import riv.objects.profile.ProfileItemGoodWithout;
import riv.objects.profile.ProfileItemLabour;
import riv.objects.profile.ProfileItemLabourWithout;
import riv.objects.reference.ReferenceCost;
import riv.objects.reference.ReferenceItem;
import riv.objects.reference.ReferenceLabour;
import riv.util.CurrencyFormat;
import riv.util.validators.ProfileItemValidator;
import riv.web.config.RivConfig;
import riv.web.service.DataService;
 
@Controller
@RequestMapping({"/profile/item"})
public class ProfileItemController {
	@Autowired
    private DataService dataService;
	@Autowired
	private RivConfig rivConfig;
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(new ProfileItemValidator());
		
		DecimalFormat df = rivConfig.getSetting().getCurrencyFormatter().getDecimalFormat(CurrencyFormat.ALL);
		CustomNumberEditor currencyEditor = new CustomNumberEditor(Double.class, df, true);
        binder.registerCustomEditor(Double.class, "unitCost", currencyEditor);
        binder.registerCustomEditor(Double.class, "ownResource", currencyEditor);
        binder.registerCustomEditor(Double.class, "total", currencyEditor);
        binder.registerCustomEditor(Double.class, "reserve", currencyEditor);
        binder.registerCustomEditor(Double.class, "salvage", currencyEditor);
        binder.registerCustomEditor(Double.class, "donated", currencyEditor);
        
        CustomNumberEditor numberEditor = new CustomNumberEditor(Double.class, rivConfig.getSetting().getDecimalFormat(), true);
		binder.registerCustomEditor(Double.class, "unitNum", numberEditor);
		binder.registerCustomEditor(Double.class, "econLife", numberEditor);
	}
	
	@ModelAttribute("profileItem")
	public ProfileItem getItem(@PathVariable Integer id, @RequestParam(required=false) String type, @RequestParam(required=false) Integer profileId)  {
		ProfileItem pi;
		if (id != -1) pi= dataService.getProfileItem(id);
		else {
			Profile p; 
			if (type.equals("good")) {
				p= dataService.getProfile(profileId, 4);
				pi = new ProfileItemGood();
				pi.setOrderBy(p.getGlsGoods().size());
			} else if (type.equals("goodWithout")) {
				p= dataService.getProfile(profileId, 4);
				pi = new ProfileItemGoodWithout();
				pi.setOrderBy(p.getGlsGoodsWithout().size());
			} else if (type.equals("labour")) {
				p= dataService.getProfile(profileId, 4);
				pi = new ProfileItemLabour();
				pi.setOrderBy(p.getGlsLabours().size());
			} else if (type.equals("labourWithout")) {
				p= dataService.getProfile(profileId, 4);
				pi = new ProfileItemLabourWithout();
				pi.setOrderBy(p.getGlsLaboursWithout().size());
			} else if (type.equals("general")){
				p= dataService.getProfile(profileId, 5);
				pi = new ProfileItemGeneral();
				pi.setOrderBy(p.getGlsGeneral().size());
			} else { // general without
				p= dataService.getProfile(profileId, 5);
				pi = new ProfileItemGeneralWithout();
				pi.setOrderBy(p.getGlsGeneralWithout().size());
			}
			pi.setProfile(p);
		}
		return pi;
	}
	
    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    public String getItem(@ModelAttribute ProfileItem profileItem, Model model, HttpServletRequest request) {
    	setupPageAttributes(profileItem, model, request);
    	return form(profileItem);
    }
    
    @RequestMapping(value="/{id}", method=RequestMethod.POST)
	public String saveProfileItem(@RequestParam Integer linkedToId, @RequestParam(required=false) Boolean addLink, HttpServletRequest request,
			@Valid @ModelAttribute ProfileItem profileItem, BindingResult result, Model model) {
    	if (result.hasErrors()) {
			setupPageAttributes(profileItem, model, request);
			return form(profileItem);
		} else {
			checkLinked(profileItem, linkedToId, addLink);
			dataService.storeProfileItem(profileItem);
			return "redirect:"+successView(profileItem);
		}
    }
    
    @RequestMapping(value="/{id}/delete", method=RequestMethod.GET)
    public String delete(@ModelAttribute ProfileItem profileItem) {
    	String view = "../"+successView(profileItem);
    	dataService.deleteProfileItem(profileItem);
		return "redirect:"+view;
    }
    
    @RequestMapping(value="/{id}/move", method=RequestMethod.GET)
    public String move(@ModelAttribute ProfileItem profileItem, @RequestParam Boolean up) {
    	String view = "../"+successView(profileItem);
    	dataService.moveProfItem(profileItem, up);
    	return "redirect:"+view;
    }
    
    @RequestMapping(value="/{id}/copy", method=RequestMethod.GET)
    public String copy(@ModelAttribute ProfileItem profileItem) {
    	ProfileItem newItem = profileItem.copy();
    	if (newItem.getClass()==ProfileItemGood.class) {
    		newItem.setOrderBy(newItem.getProfile().getGlsGoods().size());
    	} else if (newItem.getClass()==ProfileItemGoodWithout.class) {
    		newItem.setOrderBy(newItem.getProfile().getGlsGoodsWithout().size());
    	} else if (newItem.getClass()==ProfileItemGeneral.class) {
    		newItem.setOrderBy(newItem.getProfile().getGlsGeneral().size());
    	} else if (newItem.getClass()==ProfileItemGeneralWithout.class) {
    		newItem.setOrderBy(newItem.getProfile().getGlsGeneralWithout().size());
    	} else if (newItem.getClass()==ProfileItemLabour.class) { 
    		newItem.setOrderBy(newItem.getProfile().getGlsLabours().size());
    	} else {
    		newItem.setOrderBy(newItem.getProfile().getGlsLaboursWithout().size());
    	}
    	dataService.storeProfileItem(newItem);
    	String view = "../"+successView(profileItem);
    	return "redirect:"+view;
    }
    
    private String successView(ProfileItem pi) {
    	int step= (pi.getClass().isAssignableFrom(ProfileItemGeneral.class) || pi.getClass().isAssignableFrom(ProfileItemGeneralWithout.class))?5:4;
    	int profileId = pi.getProfile().getProfileId();
    	String wo = pi.getClass().isAssignableFrom(ProfileItemGoodWithout.class) || pi.getClass().isAssignableFrom(ProfileItemLabourWithout.class) || pi.getClass().isAssignableFrom(ProfileItemGeneralWithout.class) ? "#without" : "";
    	return "../step"+step+"/"+profileId+wo;
    }
    
    private void checkLinked(ProfileItem item, Integer linkedToId, Boolean addLink) {
    	if (addLink!=null) {
    		ReferenceItem ref = null;
			if (item.getClass()==ProfileItemGood.class || item.getClass()==ProfileItemGoodWithout.class) {
				ref = new ReferenceCost();
				ref.setOrderBy(item.getProfile().getRefCosts().size());
			} else if (item.getClass()==ProfileItemGeneral.class || 
					item.getClass()==ProfileItemGeneralWithout.class) {
				ref = new ReferenceCost();
				ref.setOrderBy(item.getProfile().getRefCosts().size());
			} else {
				ref = new ReferenceLabour();
				ref.setOrderBy(item.getProfile().getRefLabours().size());
			}
			ref.setDescription(item.getDescription());
			ref.setProfile(item.getProfile());
			ref.setUnitCost(item.getUnitCost());
			ref.setUnitType(item.getUnitType());
			dataService.storeReferenceItem(ref);
			item.setLinkedTo(ref);
    	} else if (linkedToId!=null) {
    		item.setLinkedTo(dataService.getReferenceItem(linkedToId));
    	} else {
    		item.setLinkedTo(null);
    	}
    }
		
    private String form(ProfileItem pi) {
    	String form;
    	if (pi.getClass()==ProfileItemGood.class || pi.getClass()==ProfileItemGoodWithout.class) form="profile/profile4good";
    	else if (pi.getClass()==ProfileItemLabour.class || pi.getClass()==ProfileItemLabourWithout.class) form="profile/profile4labour";
    	else form="profile/profile5general";
    	return form;
    }
    
    private void setupPageAttributes(ProfileItem pi, Model model, HttpServletRequest request) {
		Profile p = pi.getProfile();
    	User u = (User)request.getAttribute("user");
		model.addAttribute("accessOK", p.isShared() || p.getTechnician().getUserId().equals(u.getUserId()));
		int curStep = (pi.getClass()==ProfileItemGeneral.class || pi.getClass()==ProfileItemGeneralWithout.class) ? 5 : 4;
		model.addAttribute("currentStep", curStep);
		model.addAttribute("currentId",p.getProfileId());
		model.addAttribute("wizardStep",p.getWizardStep());
		if (p.getIncomeGen()) {
			model.addAttribute("menuType","profile");
		} else {
			model.addAttribute("menuType","profileNoninc");
		}
		if (pi.getClass().isAssignableFrom(ProfileItemGeneralWithout.class)) {
			model.addAttribute("without",true);
		}
	}
   
}