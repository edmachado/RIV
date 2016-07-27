package riv.web.controller;
 
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import riv.objects.HasDonations;
import riv.objects.config.User;
import riv.objects.project.Donor;
import riv.objects.project.Project;
import riv.objects.project.ProjectItem;
import riv.objects.project.ProjectItemAsset;
import riv.objects.project.ProjectItemAssetWithout;
import riv.objects.project.ProjectItemContribution;
import riv.objects.project.ProjectItemGeneral;
import riv.objects.project.ProjectItemGeneralBase;
import riv.objects.project.ProjectItemGeneralPerYear;
import riv.objects.project.ProjectItemGeneralWithout;
import riv.objects.project.ProjectItemLabour;
import riv.objects.project.ProjectItemLabourWithout;
import riv.objects.project.ProjectItemNongenLabour;
import riv.objects.project.ProjectItemNongenMaintenance;
import riv.objects.project.ProjectItemNongenMaterials;
import riv.objects.project.ProjectItemPersonnel;
import riv.objects.project.ProjectItemPersonnelWithout;
import riv.objects.project.ProjectItemService;
import riv.objects.project.ProjectItemServiceWithout;
import riv.objects.reference.ReferenceCost;
import riv.objects.reference.ReferenceIncome;
import riv.objects.reference.ReferenceItem;
import riv.objects.reference.ReferenceLabour;
import riv.util.CurrencyFormat;
import riv.util.validators.ProjectItemValidator;
import riv.web.config.RivConfig;
import riv.web.service.DataService;

@Controller
@RequestMapping({"/project/item"})
public class ProjectItemController {
	static final Logger LOG = LoggerFactory.getLogger(ProjectItemController.class);
			
	@Autowired
    private DataService dataService;
	@Autowired
	private RivConfig rivConfig;
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(new ProjectItemValidator());
		
		DecimalFormat df = rivConfig.getSetting().getCurrencyFormatter().getDecimalFormat(CurrencyFormat.ALL);
		CustomNumberEditor customNumberEditor = new CustomNumberEditor(Double.class, df, true);
        binder.registerCustomEditor(Double.class, "unitCost", customNumberEditor);
        binder.registerCustomEditor(Double.class, "ownResources", customNumberEditor);
        binder.registerCustomEditor(Double.class, "ownResource", customNumberEditor); // NIG general costs
        binder.registerCustomEditor(Double.class, "total", customNumberEditor);
        binder.registerCustomEditor(Double.class, "maintCost", customNumberEditor);
        binder.registerCustomEditor(Double.class, "salvage", customNumberEditor);
        binder.registerCustomEditor(Double.class, "donated", customNumberEditor);
        binder.registerCustomEditor(Double.class, "financed", customNumberEditor);
        binder.registerCustomEditor(Double.class, "amount", customNumberEditor);
        binder.registerCustomEditor(Double.class, "donations", customNumberEditor);
        binder.registerCustomEditor(Double.class, "external", customNumberEditor);

        binder.registerCustomEditor(Double.class, "years.unitCost", customNumberEditor);
        binder.registerCustomEditor(Double.class, "years.ownResources", customNumberEditor);
        binder.registerCustomEditor(Double.class, "years.total", customNumberEditor);
        binder.registerCustomEditor(Double.class, "years.external", customNumberEditor);
        
        CustomNumberEditor cne2 = new CustomNumberEditor(Double.class, rivConfig.getSetting().getDecimalFormat(), true);
        binder.registerCustomEditor(Double.class, "unitNum", cne2);
        binder.registerCustomEditor(Double.class, "years.unitNum", cne2);
	}
	
	@ModelAttribute("projectItem")
	public ProjectItem getItem(@PathVariable Integer id, @RequestParam(required=false) String type, @RequestParam(required=false) Integer projectId, @RequestParam(required=false) Integer year)  {
		ProjectItem pi;
		if (id != -1) { pi= dataService.getProjectItem(id); }
		else {
			Project p; 
			if (type.equals("asset")) {
				p= dataService.getProject(projectId, 7);
				pi = new ProjectItemAsset();
				pi.setOrderBy(p.getAssets().size());
			} else if (type.equals("assetWithout")) {
				p= dataService.getProject(projectId, 7);
				pi = new ProjectItemAssetWithout();
				pi.setOrderBy(p.getAssetsWithout().size());
			} else if (type.equals("labour")) {
				p= dataService.getProject(projectId, 7);
				pi = new ProjectItemLabour();
				pi.setOrderBy(p.getLabours().size());
			} else if (type.equals("labourWithout")) {
				p= dataService.getProject(projectId, 7);
				pi = new ProjectItemLabourWithout();
				pi.setOrderBy(p.getLaboursWithout().size());
			} else if (type.equals("service")){
				p= dataService.getProject(projectId, 7);
				pi = new ProjectItemService();
				pi.setOrderBy(p.getServices().size());
			} else if (type.equals("serviceWithout")){
				p= dataService.getProject(projectId, 7);
				pi = new ProjectItemServiceWithout();
				pi.setOrderBy(p.getServicesWithout().size());
			} else if (type.equals("projectGeneralPersonnel")) {
				p = dataService.getProject(projectId, 8);
				pi = new ProjectItemPersonnel();
				pi.setOrderBy(p.getPersonnels().size());
				addPerYearItems((ProjectItemPersonnel)pi, p);
			} else if (type.equals("projectGeneralPersonnelWithout")) {
				p = dataService.getProject(projectId, 8);
				pi = new ProjectItemPersonnelWithout();
				pi.setOrderBy(p.getPersonnelWithouts().size());
				addPerYearItems((ProjectItemPersonnelWithout)pi, p);
			} else if (type.equals("projectGeneralSupplies")) {
				p = dataService.getProject(projectId, 8);
				pi = new ProjectItemGeneral();
				pi.setOrderBy(p.getGenerals().size());
				addPerYearItems((ProjectItemGeneral)pi, p);
			} else if (type.equals("projectGeneralSuppliesWithout")) {
				p = dataService.getProject(projectId, 8);
				pi = new ProjectItemGeneralWithout();
				pi.setOrderBy(p.getGeneralWithouts().size());
				addPerYearItems((ProjectItemGeneralWithout)pi, p);
			} else if (type.equals("nongenLabour")) {
				p = dataService.getProject(projectId, 8);
				pi = new ProjectItemNongenLabour();
				pi.setOrderBy(p.getNongenLabours().size());
			} else if (type.equals("nongenMaterial")) {
				p = dataService.getProject(projectId, 8);
				pi = new ProjectItemNongenMaterials();
				pi.setOrderBy(p.getNongenMaterials().size());
			} else if (type.equals("nongenMaintenance")) {
				p = dataService.getProject(projectId, 8);
				pi = new ProjectItemNongenMaintenance();
				pi.setOrderBy(p.getNongenMaintenance().size());
			} else { // contribution
				p = dataService.getProject(projectId, 10);
				pi = new ProjectItemContribution();
				((ProjectItemContribution)pi).setYear(year);
				pi.setOrderBy(p.getContributionsByYear().get(year).size());
			}
			pi.setProject(p);
		}
		return pi;
	}
	
	private void addPerYearItems(ProjectItemGeneralBase g, Project p) {
		g.setYears(new HashMap<Integer,ProjectItemGeneralPerYear>());
		for (int i=0;i<p.getDuration();i++) {
			if (i==0||p.isPerYearGeneralCosts()) {
				ProjectItemGeneralPerYear y = new ProjectItemGeneralPerYear();
				y.setGeneral(g);
				y.setYear(i);
				y.setUnitNum(0.0);
				y.setOwnResources(0.0);
				g.getYears().put(i, y);
			}
		}
	}
	
    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    public String getItem(@ModelAttribute ProjectItem projectItem, Model model, HttpServletRequest request) {
    	setupPageAttributes(projectItem, model, request);
    	return form(projectItem);
    }
    
    private void addPerYearErrors(List<FieldError> errors, Model model) {
    	ArrayList<String> errs = new ArrayList<String>();
		for (FieldError fe : errors) {
			int year = (Integer)fe.getArguments()[0]-1;
			String code = ((DefaultMessageSourceResolvable)fe.getArguments()[1]).getCodes()[0];
			String type = code.substring(code.lastIndexOf(".")+1);
			errs.add("years"+year+type);
    	}
    	model.addAttribute("yearsErrors", errs);
    }
    
    @RequestMapping(value="/{id}", method=RequestMethod.POST)
	public String saveProjectItem(@RequestParam Integer linkedToId, @RequestParam(required=false) Boolean addLink, @RequestParam(required=false) Boolean allYears, HttpServletRequest request,
			@Valid @ModelAttribute ProjectItem projectItem, BindingResult result, Model model) {
    	
    	if (result.hasErrors()) {
			setupPageAttributes(projectItem, model, request);
			addPerYearErrors(result.getFieldErrors("years"), model);
			return form(projectItem);
		} else {
			if (projectItem instanceof HasDonations) {
				HasDonations hd = (HasDonations)projectItem;
				for (Donor donor : projectItem.getProject().getDonors()) {
					if (hd.getDonations().get(donor.getOrderBy())==0.0) {
						hd.getDonations().remove(donor.getOrderBy());
					}
				}
			}
			
			checkLinked(projectItem, linkedToId, addLink);
			
			dataService.storeProjectItem(projectItem);
			
			if (allYears!=null) { // save nig project contribution to all years of project
				Project p = projectItem.getProject();
				ProjectItemContribution c2;
				for (int i=1; i<= p.getDuration();i++) {
					if (i!=((ProjectItemContribution)projectItem).getYear()) {
						c2 = (ProjectItemContribution)projectItem.copy();
						c2.setYear(i);
						c2.setOrderBy(p.getContributionsByYear().get(i).size());
						dataService.storeProjectItem(c2, false);
						p.addContribution(c2);
					}
				}
				dataService.storeProject(p, p.getWizardStep()==null);
			}
			
			return "redirect:"+successView(projectItem);
		}
    }
    
    @RequestMapping(value="/{id}/delete", method=RequestMethod.GET)
    public String delete(@ModelAttribute ProjectItem projectItem) {
    	String view = "../"+successView(projectItem);
    	dataService.deleteProjectItem(projectItem);
		return "redirect:"+view;
    }
    
    @RequestMapping(value="/{id}/move", method=RequestMethod.GET)
    public String move(@ModelAttribute ProjectItem projectItem, @RequestParam Boolean up) {
    	String view = "../"+successView(projectItem);
    	dataService.moveProjectItem(projectItem, up);
    	return "redirect:"+view;
    }
    
    @RequestMapping(value="/{id}/copy", method=RequestMethod.GET)
    public String copy(@ModelAttribute ProjectItem projectItem) {
    	ProjectItem newItem = projectItem.copy();
    	if (newItem instanceof ProjectItemAsset) {
    		newItem.setOrderBy(newItem.getProject().getAssets().size());
    	} else if (newItem instanceof ProjectItemAssetWithout) {
    		newItem.setOrderBy(newItem.getProject().getAssetsWithout().size());
    	} else if (newItem instanceof ProjectItemContribution) {
    		newItem.setOrderBy(newItem.getProject().getContributionsByYear().get(((ProjectItemContribution)projectItem).getYear()).size());
    	} else if (newItem instanceof ProjectItemGeneral) { 
    		newItem.setOrderBy(newItem.getProject().getGenerals().size());
    	} else if (newItem instanceof ProjectItemGeneralWithout) {
    		newItem.setOrderBy(newItem.getProject().getGeneralWithouts().size());
    	} else if (newItem instanceof ProjectItemLabour) {
    		newItem.setOrderBy(newItem.getProject().getLabours().size());
    	} else if (newItem instanceof ProjectItemLabourWithout) {
    		newItem.setOrderBy(newItem.getProject().getLaboursWithout().size());
    	} else if (newItem instanceof ProjectItemPersonnel) {
    		newItem.setOrderBy(newItem.getProject().getPersonnels().size());
    	} else if (newItem instanceof ProjectItemPersonnelWithout) {
    		newItem.setOrderBy(newItem.getProject().getPersonnelWithouts().size());
    	} else if (newItem instanceof ProjectItemNongenLabour) {
    		newItem.setOrderBy(newItem.getProject().getNongenLabours().size());
    	} else if (newItem instanceof ProjectItemNongenMaterials) { 
    		newItem.setOrderBy(newItem.getProject().getNongenMaterials().size());
    	} else if (newItem instanceof ProjectItemNongenMaintenance) {
    		newItem.setOrderBy(newItem.getProject().getNongenMaintenance().size());
    	} else if (newItem instanceof ProjectItemService) {
    		newItem.setOrderBy(newItem.getProject().getServices().size());
    	} else { // serviceWithout
    		newItem.setOrderBy(newItem.getProject().getServicesWithout().size());
    	}
    	dataService.storeProjectItem(newItem);
    	String view = "../"+successView(projectItem);
    	return "redirect:"+view;
    }
    
    private int currentStep(ProjectItem pi) {
    	int step;
    	if (pi instanceof ProjectItemContribution)  {
    		step=10;
    	} else if (pi instanceof ProjectItemAsset ||
    			pi instanceof ProjectItemLabour ||
    			pi instanceof ProjectItemService ||
    			pi instanceof ProjectItemAssetWithout ||
    			pi instanceof ProjectItemLabourWithout ||
    			pi instanceof ProjectItemServiceWithout){
    		step = 7;
    	} else {
    		step = 8;
    	}
    	return step;
    }
    private String successView(ProjectItem pi) {
    	int projectId = pi.getProject().getProjectId();
    	return "../step"+currentStep(pi)+"/"+projectId;
    }
    private String form(ProjectItem pi) {
    	String form;
    	if (pi instanceof ProjectItemAsset || pi instanceof ProjectItemAssetWithout) {
    		form="project/project7asset";
    	} else if (pi instanceof ProjectItemLabour || pi instanceof ProjectItemLabourWithout) {
    		form="project/project7labour";
    	} else if (pi instanceof ProjectItemService || pi instanceof ProjectItemServiceWithout) {
    		form="project/project7service";
    	} else if (pi instanceof ProjectItemPersonnel ||
    			pi instanceof ProjectItemPersonnelWithout ||
    			pi instanceof ProjectItemGeneral ||
    			pi instanceof ProjectItemGeneralWithout) {
    		form = "project/project8general";
    	} else if (pi instanceof ProjectItemNongenLabour) {
    		form = "project/project8nongenLabour";
    	} else if (pi instanceof ProjectItemNongenMaintenance) {
    		form = "project/project8nongenMaintenance";
    	} else if (pi instanceof ProjectItemNongenMaterials) {
    		form = "project/project8nongenMaterial";
    	} else { // contribution
    		form = "project/project10contrib";
    	}
    	return form;
    }
    
    private void checkLinked(ProjectItem item, Integer linkedToId, Boolean addLink) {
    	if (addLink!=null) {
    		ReferenceItem ref = null;
			if (item.getClass()==ProjectItemContribution.class) {
				ref = new ReferenceIncome();
				ref.setOrderBy(item.getProject().getRefIncomes().size());
			} else if (item.getClass()==ProjectItemLabour.class ||
					item.getClass()==ProjectItemLabourWithout.class ||
					item.getClass()==ProjectItemPersonnel.class ||
					item.getClass()==ProjectItemPersonnelWithout.class ||
					item.getClass()==ProjectItemNongenLabour.class){
				ref = new ReferenceLabour();
				ref.setOrderBy(item.getProject().getRefLabours().size());
			} else {
				ref = new ReferenceCost();
				ref.setOrderBy(item.getProject().getRefCosts().size());
			}
			ref.setDescription(item.getDescription());
			ref.setProject(item.getProject());
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
    
    private void setupPageAttributes(ProjectItem pi, Model model, HttpServletRequest request) {
		Project p = pi.getProject();
    	User u = (User)request.getAttribute("user");
		model.addAttribute("accessOK", p.isShared() || p.getTechnician().getUserId().equals(u.getUserId()));
		model.addAttribute("currentStep", currentStep(pi));
		model.addAttribute("currentId",p.getProjectId());
		model.addAttribute("wizardStep",p.getWizardStep());
		if (p.getIncomeGen()) { model.addAttribute("menuType","project"); }
		else { model.addAttribute("menuType","projectNoninc"); }
		if (pi instanceof ProjectItemGeneralWithout 
				|| pi instanceof ProjectItemPersonnelWithout
				|| pi instanceof ProjectItemAssetWithout
				|| pi instanceof ProjectItemLabourWithout
				|| pi instanceof ProjectItemServiceWithout
			) {
			model.addAttribute("without",true);
		}
		if (pi instanceof ProjectItemGeneral || pi instanceof ProjectItemGeneralWithout) {
			model.addAttribute("type", "projectGeneralSupplies");
		} else if (pi instanceof ProjectItemPersonnel || pi instanceof ProjectItemPersonnelWithout) {
			model.addAttribute("type","projectGeneralPersonnel");
		}
		if (pi instanceof HasDonations) {
			HasDonations hd = (HasDonations)pi;
			for (Donor donor : pi.getProject().getDonors()) {
				if (!hd.getDonations().containsKey(donor.getOrderBy())) {
					hd.getDonations().put(donor.getOrderBy(), 0.0);
				}
			}
		}
	}
   
}