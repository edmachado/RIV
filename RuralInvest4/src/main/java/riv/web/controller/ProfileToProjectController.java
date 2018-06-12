package riv.web.controller;
 
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import riv.objects.FilterCriteria;
import riv.objects.config.User;
import riv.objects.project.Project;
import riv.objects.project.ProjectItemGeneral;
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
import riv.util.CurrencyFormat;
import riv.util.validators.ProjectCollectionValidator;
import riv.web.config.RivConfig;
import riv.web.service.AttachTools;
import riv.web.service.DataService;

@Controller
@RequestMapping({"/profileToProject"})
public class ProfileToProjectController {
	static final Logger LOG = LoggerFactory.getLogger(ProfileToProjectController.class);
	
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
	@Autowired
	Validator validator;
	
	@InitBinder("project")
	protected void initBinder(WebDataBinder binder, @PathVariable Integer step, HttpServletRequest request) {
		
		validator = new ProjectCollectionValidator(step, rivConfig, messageSource);
		binder.setValidator(validator);
		DecimalFormat df = rivConfig.getSetting().getCurrencyFormatter().getDecimalFormat(CurrencyFormat.ALL);
		CustomNumberEditor customNumberEditor = new CustomNumberEditor(Double.class, df, true);
		CustomNumberEditor cne2 = new CustomNumberEditor(Double.class, rivConfig.getSetting().getDecimalFormat(), true);
		
		if (step<3) { // assets
			binder.registerCustomEditor(Double.class, "assets.maintCost", customNumberEditor);
			binder.registerCustomEditor(Double.class, "assetsWithout.maintCost", customNumberEditor);
		} else if (step<5) { // invest labours
			binder.registerCustomEditor(Double.class, "labours.unitNum", cne2);
			binder.registerCustomEditor(Double.class, "labours.unitCost", customNumberEditor);
			binder.registerCustomEditor(Double.class, "labours.ownResources", customNumberEditor);
			binder.registerCustomEditor(Double.class, "laboursWithout.unitNum", cne2);
			binder.registerCustomEditor(Double.class, "laboursWithout.unitCost", customNumberEditor);
			binder.registerCustomEditor(Double.class, "laboursWithout.ownResources", customNumberEditor);
		} else if (step<7) {
			binder.registerCustomEditor(Double.class, "personnels.unitNum", cne2);
			binder.registerCustomEditor(Double.class, "personnels.unitCost", customNumberEditor);
			binder.registerCustomEditor(Double.class, "personnelWithouts.unitNum", cne2);
			binder.registerCustomEditor(Double.class, "personnelWithouts.unitCost", customNumberEditor);
		} else { // blocks
			binder.registerCustomEditor(BigDecimal.class, "blocks.labours.unitCost", customNumberEditor);
			binder.registerCustomEditor(BigDecimal.class, "blocks.labours.unitNum", cne2);
			binder.registerCustomEditor(BigDecimal.class, "blocksWithout.labours.unitCost", customNumberEditor);
	        binder.registerCustomEditor(BigDecimal.class, "blocksWithout.labours.unitNum", cne2);
		}
	}

	@ModelAttribute("project")
	public Project project(@PathVariable Integer id,  @PathVariable Integer step, HttpServletRequest request) {
		Project p = dataService.getProject(id, -1);
		return p;
	}
	
	@RequestMapping(value="/step{step}/{id}", method=RequestMethod.GET)
	public String getProject(@PathVariable Integer step, @PathVariable Integer id, @ModelAttribute Project project, Model model, HttpServletRequest request) {
		if (project==null) { return "noProbase"; }
		if (project.getProfileUpgrade()!=step) {
			return "redirect:../step"+project.getProfileUpgrade()+"/"+project.getProjectId();
		}
		setupPageAttributes(project, model, step, request);
		return getView(project, step);
	}
	
	private String getView(Project project, int step) {
		if (step<3) {
			return "profileToProject/assets";
		} else if (step<5) {
			return "profileToProject/labourInvest";
		} else if (step<7) {
			return project.getIncomeGen() ? "profileToProject/labourGeneral" : "profileToProject/labourGeneralNongen";
		} else {
			return "profileToProject/blocks";
		}
	}
	
	@RequestMapping(value="/step{step}/{id}", method=RequestMethod.POST)
	public String saveProject(@PathVariable Integer step, @PathVariable Integer id, HttpServletRequest request, 
			@Valid Project project, BindingResult result, Model model, 
			@RequestParam(required=false) String labourData, @RequestParam(required=false) String serviceData,
			@RequestParam(required=false) String generalData, @RequestParam(required=false) String personnelData,
			@RequestParam(required=false) String inputData, 
            final RedirectAttributes redirectAttributes) {
		
		if (step==3) {
			if (labourData!=null) {
				ObjectMapper mapper = new ObjectMapper();
				try (ByteArrayInputStream bais1 = new ByteArrayInputStream(URLDecoder.decode(labourData, "UTF-8").getBytes("UTF-8"));
						ByteArrayInputStream bais2 = new ByteArrayInputStream(URLDecoder.decode(serviceData, "UTF-8").getBytes("UTF-8"))) {
					List<ProjectItemLabour> ls = mapper.readValue(bais1,  new TypeReference<List<ProjectItemLabour>>(){});
					for (ProjectItemLabour l : ls) {
						ProjectItemLabour l2 = l.copy();
						l2.setOrderBy(project.getLabours().size());
						project.addLabour(l2);
					}
					
					List<ProjectItemService> ss = mapper.readValue(bais2,  new TypeReference<List<ProjectItemService>>(){});
					for (ProjectItemService s : ss) {
						ProjectItemService s2 = s.copy();
						s2.setOrderBy(project.getServices().size());
						project.addService(s2);
					}
					
					project.getLaboursFromProfile().clear();
				} catch (Exception e) {
					LOG.error("Exception upgrading in step 3", e);
				}
			}
			validator.validate(project, result);
		} else if (step==4) {
			if (labourData!=null) {
				ObjectMapper mapper = new ObjectMapper();
				try (ByteArrayInputStream bais1 = new ByteArrayInputStream(URLDecoder.decode(labourData, "UTF-8").getBytes("UTF-8"));
						ByteArrayInputStream bais2 = new ByteArrayInputStream(URLDecoder.decode(serviceData, "UTF-8").getBytes("UTF-8"))) {
					List<ProjectItemLabourWithout> ls = mapper.readValue(bais1,  new TypeReference<List<ProjectItemLabourWithout>>(){});
					for (ProjectItemLabourWithout l : ls) {
						ProjectItemLabourWithout l2 = l.copy();
						l2.setOrderBy(project.getLaboursWithout().size());
						project.addLabourWithout(l2);
					}
					
					List<ProjectItemServiceWithout> ss = mapper.readValue(bais2,  new TypeReference<List<ProjectItemServiceWithout>>(){});
					for (ProjectItemServiceWithout s : ss) {
						ProjectItemServiceWithout s2 = s.copy();
						s2.setOrderBy(project.getServicesWithout().size());
						project.addServiceWithout(s2);
					}
					
					project.getLaboursFromProfileWithout().clear();
					
				} catch (Exception e) {
					LOG.error("Exception upgrading in step 4", e);
				}
				validator.validate(project, result);
			}
		} else if (step==5 && project.getIncomeGen()) {
			if (generalData!=null) {
				ObjectMapper mapper = new ObjectMapper();
				try (ByteArrayInputStream bais1 = new ByteArrayInputStream(URLDecoder.decode(generalData, "UTF-8").getBytes("UTF-8"));
						ByteArrayInputStream bais2 = new ByteArrayInputStream(URLDecoder.decode(personnelData, "UTF-8").getBytes("UTF-8"))) {
					List<ProjectItemGeneral> ls = mapper.readValue(bais1,  new TypeReference<List<ProjectItemGeneral>>(){});
					for (ProjectItemGeneral l : ls) {
						ProjectItemGeneralPerYear py = new ProjectItemGeneralPerYear();
						py.setYear(0);
						py.setOwnResources(0.0);
						py.setUnitNum(l.getUnitNumJson());
						py.setParent(l);
						l.getYears().put(0, py);
						ProjectItemGeneral l2 = l.copy();
						l2.setOrderBy(project.getGenerals().size());
						project.addGeneral(l2);
					}
					
					List<ProjectItemPersonnel> ss = mapper.readValue(bais2,  new TypeReference<List<ProjectItemPersonnel>>(){});
					for (ProjectItemPersonnel s : ss) {
						ProjectItemGeneralPerYear py = new ProjectItemGeneralPerYear();
						py.setYear(0);
						py.setOwnResources(0.0);
						py.setUnitNum(s.getUnitNumJson());
						py.setParent(s);
						s.getYears().put(0, py);
						ProjectItemPersonnel s2 = s.copy();
						s2.setOrderBy(project.getPersonnels().size());
						project.addPersonnel(s2);
					}
					
					project.getGeneralsFromProfile().clear();
				} catch (Exception e) {
					LOG.error("Exception upgrading in step 5", e);
				}
				
				validator.validate(project, result);
			}
		} else if (step==6 && project.getIncomeGen()) {
			if (generalData!=null) {
				ObjectMapper mapper = new ObjectMapper();
				try (ByteArrayInputStream bais1 = new ByteArrayInputStream(URLDecoder.decode(generalData, "UTF-8").getBytes("UTF-8"));
						ByteArrayInputStream bais2 = new ByteArrayInputStream(URLDecoder.decode(personnelData, "UTF-8").getBytes("UTF-8"))) {
					List<ProjectItemGeneralWithout> ls = mapper.readValue(bais1,  new TypeReference<List<ProjectItemGeneralWithout>>(){});
					for (ProjectItemGeneralWithout l : ls) {
						ProjectItemGeneralPerYear py = new ProjectItemGeneralPerYear();
						py.setYear(0);
						py.setUnitNum(l.getUnitNumJson());
						py.setOwnResources(0.0);
						py.setParent(l);
						l.getYears().put(0, py);
						ProjectItemGeneralWithout l2 = l.copy();
						l2.setOrderBy(project.getGeneralWithouts().size());
						project.addGeneralWithout(l2);
					}
					
					List<ProjectItemPersonnelWithout> ss = mapper.readValue(bais2,  new TypeReference<List<ProjectItemPersonnelWithout>>(){});
					for (ProjectItemPersonnelWithout s : ss) {
						ProjectItemGeneralPerYear py = new ProjectItemGeneralPerYear();
						py.setYear(0);
						py.setUnitNum(s.getUnitNumJson());
						py.setOwnResources(0.0);
						py.setParent(s);
						s.getYears().put(0, py);
						ProjectItemPersonnelWithout s2 = s.copy();
						s2.setOrderBy(project.getPersonnelWithouts().size());
						project.addPersonnelWithout(s2);
					}
					project.getGeneralsFromProfileWithout().clear();
				} catch (Exception e) {
					LOG.error("Exception upgrading in step 6", e);
				}
				
				validator.validate(project, result);
			}
		} else if (step==5 && !project.getIncomeGen()) {
			if (generalData!=null) {
				ObjectMapper mapper = new ObjectMapper();
				try (ByteArrayInputStream bais1 = new ByteArrayInputStream(URLDecoder.decode(inputData, "UTF-8").getBytes("UTF-8"));
						ByteArrayInputStream bais2 = new ByteArrayInputStream(URLDecoder.decode(labourData, "UTF-8").getBytes("UTF-8"));
						ByteArrayInputStream bais3 = new ByteArrayInputStream(URLDecoder.decode(generalData, "UTF-8").getBytes("UTF-8"));) {
					List<ProjectItemNongenMaintenance> maints = mapper.readValue(bais1,  new TypeReference<List<ProjectItemNongenMaintenance>>(){});
					for (ProjectItemNongenMaintenance l : maints) {
						ProjectItemNongenMaintenance l2 = l.copy();
						l2.setOrderBy(project.getNongenMaintenance().size());
						project.addNongenMaintenance(l2);
					}
					
					List<ProjectItemNongenLabour> ls = mapper.readValue(bais2,  new TypeReference<List<ProjectItemNongenLabour>>(){});
					for (ProjectItemNongenLabour l : ls) {
						ProjectItemNongenLabour l2 = l.copy();
						l2.setOrderBy(project.getNongenLabours().size());
						project.addNongenLabour(l2);
					}
					
					List<ProjectItemNongenMaterials> mats = mapper.readValue(bais3,  new TypeReference<List<ProjectItemNongenMaterials>>(){});
					for (ProjectItemNongenMaterials l : mats) {
						ProjectItemNongenMaterials l2 = l.copy();
						l2.setOrderBy(project.getNongenMaterials().size());
						project.addNongenMaterial(l2);
					}
					
					project.getGeneralsFromProfile().clear();
				} catch (Exception e) {
					LOG.error("Exception upgrading in step 5 n-i-g", e);
				}
				
				validator.validate(project, result);
			}
		}
		
		if (result.hasErrors()) {
			if (step>=3 && step<=6) {
				dataService.storeProject(project, false);
			}
			setupPageAttributes(project, model, step, request);
			return getView(project, step);
		} else {
			switch (step) {
			case 1:
				project.setProfileUpgrade(
					(project.isWithWithout() && project.getAssetsWithout().size()>0) 
					? 2 : 3);
				break;
			case 2:
				project.setProfileUpgrade(3);
				break;
			case 3:
				project.setProfileUpgrade(
					(project.isWithWithout() && project.getLaboursFromProfileWithout().size()>0)
					? 4 : 5);
				break;
			case 4:
				project.setProfileUpgrade(5);
				break;
			case 5:
				project.setProfileUpgrade(
					(project.isWithWithout() && project.getGeneralsFromProfileWithout().size()>0)
					? 6 : 7);
				break;
			case 6:
				project.setProfileUpgrade(7);
				break;
			case 7:
				project.setProfileUpgrade(
					(project.isWithWithout() && project.getBlocksWithout().size()>0)
					? 8 : null);
				break;
			case 8:
				project.setProfileUpgrade(null);
				break;
			} 
			
			dataService.storeProject(project, false);
			
			if (project.getProfileUpgrade()==null) {
				return "redirect:../../project/step1/"+project.getProjectId();
			} else {
				return "redirect:../step"+ project.getProfileUpgrade() +"/"+project.getProjectId();
			}
		}
	}
	
	private void setupPageAttributes(Project p, Model model, Integer step, HttpServletRequest request) {
		User u = (User)request.getAttribute("user");
		int curStep;
		if (p.getProfileUpgrade()<3) { curStep=1; }
		else if (p.getProfileUpgrade()<5) { curStep=2; }
		else if (p.getProfileUpgrade()<7) { curStep=3; }
		else { curStep=4; }
		
		model.addAttribute("currentStep", curStep);
		model.addAttribute("accessOK", p.isShared() || p.getTechnician().getUserId().equals(u.getUserId()));
		model.addAttribute("currentId",p.getProjectId());
		model.addAttribute("wizardStep",-1);
		model.addAttribute("profToProj", true);
		model.addAttribute("menuType",p.getIncomeGen()?"profToProjIg":"profToProjNig");
	}
}
