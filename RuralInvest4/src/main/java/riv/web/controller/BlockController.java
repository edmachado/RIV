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
import riv.objects.project.BlockBase;
import riv.objects.project.BlockChron;
import riv.objects.project.BlockPattern;
import riv.objects.project.BlockWithout;
import riv.objects.project.Project;
import riv.objects.project.Block;
import riv.util.validators.BlockValidator;
import riv.web.config.RivConfig;
import riv.web.service.DataService;
 
@Controller
@RequestMapping({"/project/block"})
public class BlockController {
	@Autowired
    private DataService dataService;
	@Autowired
	private RivConfig rivConfig;
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(new BlockValidator());
		CustomNumberEditor decimalEditor = new CustomNumberEditor(Double.class, rivConfig.getSetting().getDecimalFormat(), true);
        binder.registerCustomEditor(Double.class, "cycleLength", decimalEditor);
	}
	
	@ModelAttribute("block")
	public BlockBase getItem(@PathVariable Integer id, @RequestParam(required=false) Integer projectId, @RequestParam(required=false) String without) throws Exception {
		BlockBase pp;
		if (id!=-1) {
			pp =dataService.getBlock(id);
		} else {
			Project p = dataService.getProject(projectId, 9);
			if (without==null) {
				pp = new Block();
				pp.setOrderBy(p.getBlocks().size());
			} else {
				pp = new BlockWithout();
				pp.setOrderBy(p.getBlocksWithout().size());
			}
			pp.setProject(p);
		}
		return pp;
	}
	
    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    public String getItem(@ModelAttribute("block") BlockBase block, Model model, HttpServletRequest request) {
    	setModelAttributes(block, model, request);
    	return "project/project9block";
    }
    
    private void setModelAttributes(BlockBase block, Model model, HttpServletRequest request) {
    	model.addAttribute("accessOK",block.getProject().isShared() || ((User)request.getAttribute("user")).getUserId().equals(block.getProject().getTechnician().getUserId()));
    	model.addAttribute("currentStep",9);
    	model.addAttribute("currentId",block.getProject().getProjectId());
    	model.addAttribute("wizardStep",block.getProject().getWizardStep());
    	model.addAttribute("isWithout", block.getClass()==BlockWithout.class);
    	if (block.getProject().getIncomeGen()) {
    		model.addAttribute("menuType","project");
    	} else {
    		model.addAttribute("menuType","projectNoninc");
    	}
    }
    
    @RequestMapping(value="/{id}", method=RequestMethod.POST)
	public String saveProjectItem(@Valid @ModelAttribute("block") BlockBase block, BindingResult result, Model model, HttpServletRequest request) {
    	if (block.isNoCycles()) {
    		block.setLengthUnit(0); // 0="month(s)"
    		block.setCycleLength(12.0); // 12 months = 1 year-long cycle
    		block.setCyclePerYear(1);
    		block.setCycleFirstYear(1);
    		block.setCycleFirstYearIncome(1);
    	}
    	
    	updateProductionPattern(block, request, result);
    	updateChronology(block, request, result);
    	
    	if (result.hasErrors()) {
    		setModelAttributes(block, model, request);
			return "project/project9block";
		} else {
			dataService.storeBlock(block);
			return "redirect:"+successView(block);
		}
    }
    
    @RequestMapping(value="/{id}/clone", method=RequestMethod.GET)
    public String clone(@ModelAttribute("block") BlockBase block, HttpServletRequest request) {
    	String view;
    	User u = (User) request.getAttribute("user");
    	Project p = dataService.getProject(block.getProject().getProjectId(), 9);
    	if (p.isShared() || p.getTechnician().getUserId().equals(u.getUserId())) {
    		BlockBase bb = dataService.getBlock(block.getBlockId(), "all");
    		BlockBase newBlock = bb.copy();
    		newBlock.setOrderBy(newBlock.getClass()==Block.class ? p.getBlocks().size() : p.getBlocksWithout().size());
    		p.addBlock(newBlock);
    		dataService.storeBlock(newBlock);
    		view = "../"+newBlock.getBlockId()+"?rename=true";
    	} else {
    		view = "../"+successView(block);
    	}
    	
    	return "redirect:"+view;
    }
    
    @RequestMapping(value="/{id}/delete", method=RequestMethod.GET)
    public String delete(@ModelAttribute("block") BlockBase block) {
    	String view = "../"+successView(block);
    	dataService.deleteBlock(block);
    	// if last block has been removed, can't be considered complete anymore
    	if (block.getClass()==Block.class && block.getOrderBy()==0) {
    		Project p = dataService.getProject(block.getProject().getProjectId(), 9);
    		if (p.getBlocks().size()==0) {
    			p.setWizardStep(9);
    			dataService.storeProject(p, false);
    			dataService.deleteProjectResult(p.getProjectId());
    		}
    	}
    	return "redirect:"+view;
    }
    
    @RequestMapping(value="/{id}/move", method=RequestMethod.GET)
    public String move(@ModelAttribute("block") BlockBase block, @RequestParam Boolean up) {
    	String view = "../"+successView(block);
    	dataService.moveBlock(block, up);
    	return "redirect:"+view;
    }
    
    private String successView(BlockBase b) {
    	return "../step9/"+b.getProject().getProjectId()+"#b"+b.getBlockId();
    }
    
   private void updateProductionPattern(BlockBase pb, HttpServletRequest request, BindingResult result) {
    	for (int i=1; i<=pb.getProject().getDuration(); i++) {
			if (request.getParameter("pat"+i).equals("")) {
				// error if no data entered
				result.reject("error.noProdPattern", new Object[] {i}, "Production pattern missing for year "+i);
				
			} else {
				// error if not number
				try {
					String qtyText = request.getParameter("pat"+i).replace(rivConfig.getSetting().getDecimalSeparator(), ".").replace(rivConfig.getSetting().getThousandSeparator(), "");
					double qty = Double.parseDouble(qtyText);
					if (pb.getPatterns().get(i) != null)
						pb.getPatterns().get(i).setQty(qty);
					else {
						BlockPattern pattern = new BlockPattern();
						pattern.setYearNum(i);
						pattern.setQty(qty);
						pb.addPattern(pattern);
					}
				} catch (Exception e) {	
					result.reject("error.noProdPattern", new Object[] {i}, "Production pattern missing for year "+i);
				}
			}
		}
    }
   
    private void updateChronology(BlockBase pb, HttpServletRequest request, BindingResult result) {
    	boolean productionNeeded=pb.getPatterns().get(1).getQty()>0;
    	
    	// update chronology if income gen
		if (pb.getProject().getIncomeGen()) {
			for (int i=0;i<3;i++) {
				for (int j=0;j<12;j++) {
					for (int k=0;k<2;k++) {
						String key=i+"-"+j+"-"+k;
						boolean selected = request.getParameter("ch"+key).equals("true");
						if (selected &! pb.getChrons().containsKey(key)) {
							BlockChron pbc = new BlockChron();
							pbc.setChronId(key);
							pb.addChron(pbc);
						} else if (pb.getChrons().containsKey(key) &! selected) {
							pb.getChrons().remove(key);
						}
						if (productionNeeded && i==0 && selected) {
							productionNeeded=false;
						}
					}
				}
			}
			if (productionNeeded) { result.reject("error.noChronology"); }
		}
    }
}