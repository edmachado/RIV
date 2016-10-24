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

import riv.objects.HasDonations;
import riv.objects.config.User;
import riv.objects.project.BlockBase;
import riv.objects.project.BlockIncome;
import riv.objects.project.BlockInput;
import riv.objects.project.BlockItem;
import riv.objects.project.BlockLabour;
import riv.objects.project.Donor;
import riv.objects.project.Project;
import riv.objects.reference.ReferenceCost;
import riv.objects.reference.ReferenceIncome;
import riv.objects.reference.ReferenceItem;
import riv.objects.reference.ReferenceLabour;
import riv.util.CurrencyFormat;
import riv.util.validators.BlockItemValidator;
import riv.web.config.RivConfig;
import riv.web.service.DataService;
 
@Controller
@RequestMapping({"/project/blockItem"})
public class BlockItemController {
	@Autowired
    private DataService dataService;
	@Autowired
	private RivConfig rivConfig;
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(new BlockItemValidator());
  
		DecimalFormat df = rivConfig.getSetting().getCurrencyFormatter().getDecimalFormat(CurrencyFormat.ALL);
		CustomNumberEditor currencyEditor = new CustomNumberEditor(BigDecimal.class, df, true);
        binder.registerCustomEditor(BigDecimal.class, "unitCost", currencyEditor);
        binder.registerCustomEditor(BigDecimal.class, "transport", currencyEditor);
        binder.registerCustomEditor(BigDecimal.class, "total", currencyEditor);
        binder.registerCustomEditor(BigDecimal.class, "totalCash", currencyEditor);
		CustomNumberEditor currencyEditor2 = new CustomNumberEditor(Double.class, df, true);
        binder.registerCustomEditor(Double.class, "donated", currencyEditor2);
        binder.registerCustomEditor(Double.class, "donations", currencyEditor2);
		
        CustomNumberEditor numberEditor = new CustomNumberEditor(BigDecimal.class, rivConfig.getSetting().getDecimalFormat(), true);
        binder.registerCustomEditor(BigDecimal.class, "unitNum", numberEditor);
        binder.registerCustomEditor(BigDecimal.class, "qtyIntern", numberEditor);
        binder.registerCustomEditor(BigDecimal.class, "extern", numberEditor);
	}
	
	@ModelAttribute("blockItem")
	public BlockItem getItem(@PathVariable Integer id, @RequestParam(required=false) Integer blockId, @RequestParam(required=false) String itemType) {
		BlockItem pi;
		if (id!=-1) {
			pi = dataService.getBlockItem(id);
		} else {
			BlockBase pp = dataService.getBlock(blockId, itemType);
			if (itemType.equals("income")) {
				pi = new BlockIncome();
				pi.setOrderBy(pp.getIncomes().size());
			} else if (itemType.equals("input")) {
				pi = new BlockInput();
				pi.setOrderBy(pp.getInputs().size());
			} else {
				pi = new BlockLabour();
				pi.setOrderBy(pp.getLabours().size());
			}
			pi.setBlock(pp);
		}
		return pi;
	}
	
    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    public String getItem(@ModelAttribute BlockItem blockItem, Model model, HttpServletRequest request) {
    	setupPageAttributes(blockItem, model, request);
    	return form(blockItem);
    }
    
    @RequestMapping(value="/{id}", method=RequestMethod.POST)
	public String saveProjectItem(@RequestParam Integer linkedToId, HttpServletRequest request,
			@RequestParam(required=false) Boolean addLink, //@RequestParam(required=false) String addTransport,
			@Valid @ModelAttribute BlockItem blockItem, BindingResult result, Model model) {
    	
    	if (result.hasErrors()) {
			setupPageAttributes(blockItem, model, request);
			return form(blockItem);
		} else {
			if (!blockItem.getProbase().getIncomeGen() && blockItem instanceof HasDonations) {
				HasDonations hd = (HasDonations)blockItem;
				for (Donor donor : blockItem.getBlock().getProject().getDonors()) {
					if (hd.getDonations().get(donor.getOrderBy())==0.0) {
						hd.getDonations().remove(donor.getOrderBy());
					}
				}
			}
			
			checkLinked(blockItem, linkedToId, addLink);//, Double.parseDouble(addTransport));
			dataService.storeBlockItem(blockItem);
			return "redirect:"+successView(blockItem);
		}
    }
    
    @RequestMapping(value="/{id}/delete", method=RequestMethod.GET)
    public String delete(@ModelAttribute BlockItem blockItem) {
    	String view = "../"+successView(blockItem);
    	dataService.deleteBlockItem(blockItem);
    	return "redirect:"+view;
    }
    
    @RequestMapping(value="/{id}/move", method=RequestMethod.GET)
    public String move(@ModelAttribute BlockItem blockItem, @RequestParam Boolean up) {
    	String view = "../"+successView(blockItem);
    	dataService.moveBlockItem(blockItem, up);
    	return "redirect:"+view;
    }
    
    @RequestMapping(value="/{id}/copy", method=RequestMethod.GET)
    public String copy(@ModelAttribute BlockItem blockItem) {
    	BlockItem newItem = blockItem.copy();
    	if (newItem instanceof BlockIncome) {
    		newItem.setOrderBy(newItem.getBlock().getIncomes().size());
    		newItem.getBlock().addIncome((BlockIncome)newItem);
    	} else if (newItem instanceof BlockInput) {
    		newItem.setOrderBy(newItem.getBlock().getInputs().size());
    		newItem.getBlock().addInput((BlockInput)newItem);
    	} else { // labour
    		newItem.setOrderBy(newItem.getBlock().getLabours().size());
    		newItem.getBlock().addLabour((BlockLabour)newItem);
    	}
    	dataService.storeBlockItem(newItem);
    	String view = "../"+successView(blockItem);
    	return "redirect:"+view;
    }
    
    private String successView(BlockItem pi) {
    	BlockBase b = pi.getBlock();
    	return "../step9/"+b.getProject().getProjectId()+"#b"+b.getBlockId();
    }
    
    private void checkLinked(BlockItem item, Integer linkedToId, Boolean addLink) {
    	if (addLink!=null) {
    		ReferenceItem ref = null;
			if (item instanceof BlockIncome) {
				ref = new ReferenceIncome();
				ref.setOrderBy(item.getBlock().getProject().getRefIncomes().size());
				if (item.getBlock().getProject().getIncomeGen()) {
					((ReferenceIncome)ref).setTransport(((BlockIncome)item).getTransport().doubleValue());
				}
			} else if (item instanceof BlockInput) {
				ref = new ReferenceCost();
				ref.setOrderBy(item.getBlock().getProject().getRefCosts().size());
				((ReferenceCost)ref).setTransport(((BlockInput)item).getTransport().doubleValue());
			} else {
				ref = new ReferenceLabour();
				ref.setOrderBy(item.getBlock().getProject().getRefLabours().size());
			}
			ref.setDescription(item.getDescription());
			ref.setProject(item.getBlock().getProject());
			ref.setUnitCost(item.getUnitCost().doubleValue());
			ref.setUnitType(item.getUnitType());
			
			dataService.storeReferenceItem(ref);
			item.setLinkedTo(ref);
    	} else if (linkedToId!=null) {
    		ReferenceItem ref = dataService.getReferenceItem(linkedToId);
    		item.setLinkedTo(ref);
    		//if (addTransport!=null) {
    			if (item instanceof BlockIncome && item.getProbase().getIncomeGen() && ((ReferenceIncome)ref).getTransport()==null) {
    				((ReferenceIncome)ref).setTransport(((BlockIncome)item).getTransport().doubleValue());
    			} else if (item instanceof BlockInput && ((ReferenceCost)ref).getTransport()==null) {
    				((ReferenceCost)ref).setTransport(((BlockInput)item).getTransport().doubleValue());
    			}
    			dataService.storeReferenceItem(ref);
    		//}
    	} else {
    		item.setLinkedTo(null);
    	}
    }
		
    private String form(BlockItem pi) {
    	String form;
    	if (pi.getClass()==BlockLabour.class) {
    		form="project/project9labour";
    	} else if (pi.getClass()==BlockInput.class) { 
    		form="project/project9input";
    	} else if (pi.getBlock().getProject().getIncomeGen()) { 
    		form="project/project9income";
    	} else {
    		form="project/project9userCharge";
    	}
    	return form;
    }
    
    private void setupPageAttributes(BlockItem pi, Model model, HttpServletRequest request) {
		Project p = pi.getBlock().getProject();
    	User u = (User)request.getAttribute("user");
		model.addAttribute("accessOK", p.isShared() || p.getTechnician().getUserId().equals(u.getUserId()));
		model.addAttribute("currentStep", 9);
		model.addAttribute("currentId",p.getProjectId());
		model.addAttribute("wizardStep",p.getWizardStep());
		if (p.getIncomeGen()) model.addAttribute("menuType","project");
		else model.addAttribute("menuType","projectNoninc");
		
		if (!pi.getProbase().getIncomeGen() && pi instanceof HasDonations) {
			HasDonations hd = (HasDonations)pi;
			for (Donor donor : pi.getBlock().getProject().getDonors()) {
				if (!hd.getDonations().containsKey(donor.getOrderBy())) {
					hd.getDonations().put(donor.getOrderBy(), 0.0);
				}
			}
		}
	}
   
}