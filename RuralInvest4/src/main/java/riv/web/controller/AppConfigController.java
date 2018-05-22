package riv.web.controller;
 
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import riv.objects.config.AppConfig;
import riv.objects.config.AppConfig1;
import riv.objects.config.AppConfig2;
import riv.objects.config.Beneficiary;
import riv.objects.config.EnviroCategory;
import riv.objects.config.FieldOffice;
import riv.objects.config.ProjectCategory;
import riv.objects.config.Status;
import riv.objects.config.User;
import riv.util.validators.AppConfigValidator;
import riv.web.config.RivConfig;
import riv.web.service.DataService;

@Controller
@RequestMapping({"/config"})
public class AppConfigController {
	static final Logger LOG = LoggerFactory.getLogger(AppConfigController.class);
	@Autowired
    private DataService dataService;
	@Autowired
	private RivConfig rivConfig;
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(new AppConfigValidator());
	}
	
	@ModelAttribute("appConfig")
	public AppConfig getItem(@PathVariable String type, @PathVariable Integer id) throws Exception {
		AppConfig ac=null;
		if (id==-1) {	
			if (type.equals("office")) {
				ac=new FieldOffice();
			} else if (type.equals("category")) {
				ac=new ProjectCategory();
			} else if   (type.equals("beneficiary")) {
				ac=new Beneficiary();
			} else if  (type.equals("enviroCategory")) {
				ac=new EnviroCategory();
			} else if  (type.equals("status")) {
				ac=new Status();
			} else if  (type.equals("appConfig1")) {
				ac=new AppConfig1();
			} else if  (type.equals("appConfig2")) {
				ac=new AppConfig2();
			}
		} else {
			ac = dataService.getAppConfig(id);
		}
		return ac;
	}
	
	@RequestMapping(value="/{type}/{id}", method=RequestMethod.GET)
	public String getAppConfig(@PathVariable String type, @PathVariable Integer id, Model model, HttpServletRequest request) {	
		model.addAttribute("type",type);
		User u = (User)request.getAttribute("user");
		model.addAttribute("accessOK", u.isAdministrator() && rivConfig.isAdmin());
		return form(type);
	}
	
	@RequestMapping(value="/{type}/{id}", method=RequestMethod.POST) //, consumes="text/plain"
	public String updateAppConfig(@PathVariable String type, @Valid @ModelAttribute AppConfig appConfig, BindingResult result, HttpServletRequest request) {
		User u = (User)request.getAttribute("user");
		boolean access = u.isAdministrator() && rivConfig.isAdmin();
		if (!access || result.hasErrors()) {
			request.setAttribute("accessOK", u.isAdministrator() && rivConfig.isAdmin());
			return form(type);
		} else {
			rivConfig.storeAppConfig(appConfig);
			return "redirect:../"+type;
		}
	}
	
	@RequestMapping(value="/{type}/delete/{id}", method=RequestMethod.GET)
	public String delete(@PathVariable String type, @ModelAttribute AppConfig appConfig, Model model, HttpServletRequest request) {
		User u = (User)request.getAttribute("user");
		if (u.isAdministrator() && rivConfig.isAdmin()) {
			rivConfig.deleteAppConfig(appConfig);
		}
		return "redirect:../../"+type;
	}
	
	private String form(String type) {
		String form=null;
		if (type.equals("office")) {
			form="config/office";
		} else if  (type.equals("category")) {
			form="config/category";
		} else if  (type.equals("beneficiary")) {
			form="config/benef";
		} else if  (type.equals("enviroCategory")) {
			form="config/enviro";
		} else if  (type.equals("status")) {
			form="config/status";
		} else if  (type.equals("appConfig1")) {
			form="config/appConfig";
		} else if  (type.equals("appConfig2")){
			form="config/appConfig";
		}
		return form;
	}
}