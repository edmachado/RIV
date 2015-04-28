package riv.web.controller;
 
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import riv.objects.config.Setting;
import riv.util.validators.SettingsValidator;
import riv.web.config.RivConfig;
import riv.web.service.DataService;
 
@Controller
public class SettingsController {
	static final Logger LOG = LoggerFactory.getLogger(SettingsController.class);
	@Autowired
    private DataService dataService;
	@Autowired
	private RivConfig rivConfig;
	@Autowired
	ServletContext servletContext;
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(new SettingsValidator());
	    
	    DecimalFormat df = rivConfig.getSetting().getDecimalFormat();
		CustomNumberEditor customNumberEditor = new CustomNumberEditor(Double.class, df, true);
		binder.registerCustomEditor(Double.class, "discountRate", customNumberEditor);
		binder.registerCustomEditor(Double.class, "exchRate", customNumberEditor);
	}
	
	@ModelAttribute("setting")
	public Setting setting() {
		return rivConfig.getSetting().copy();
	}
	
	@RequestMapping(value="/config/settings", method=RequestMethod.GET)
	public String settings(Model model) {
		return "config/settings";
	}
	
	@RequestMapping(value="/config/settings", method=RequestMethod.POST)
	public String saveSettings(@Valid Setting setting, BindingResult result,
			HttpServletRequest request, @RequestParam("tempLogo") MultipartFile tempLogo) {
		
		if (setting.getOrgLogo()==null && tempLogo.getSize()==0) {
			result.reject("error.logo");
		}
		
		// in case decimal separator is changing... make sure decimals are parsed correctly
		boolean decProblem=false;
		try {
			decProblem= setting.getDiscountRate()!=Double.parseDouble(request.getParameter("discountRate"))
				||	setting.getExchRate()!=Double.parseDouble(request.getParameter("exchRate"));	
		} catch (NumberFormatException e) {
			decProblem=true;
		}
		if (decProblem) {
			DecimalFormat df = setting.getDecimalFormat();
			try {
				setting.setDiscountRate(df.parse(request.getParameter("discountRate")).doubleValue());
			} catch (ParseException e) {
				result.rejectValue("discountRate", "error.fieldRequired");
			}
			try {
				setting.setExchRate(df.parse(request.getParameter("exchRate")).doubleValue());
			} catch (ParseException e) {
				result.rejectValue("exchRate", "error.fieldRequired");
			}
		}
		
		if (result.hasErrors()) {
			return "config/settings";
		} else {
			if (!tempLogo.isEmpty()) {
				try { // Save in db
					setting.setOrgLogo(tempLogo.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			dataService.storeAppSetting(setting);
			setting.refreshCurrencyFormatter();
			boolean refreshAppConfigs = !request.getParameter("exLang").equals(setting.getLang());
			rivConfig.setSetting(dataService.getAppSetting(), refreshAppConfigs);
			
			// if discount has changed, recalculate indicators
			if (request.getParameter("exDiscountRate")!=null &! request.getParameter("exDiscountRate").isEmpty()) {
				Double exDiscountRate =  Double.parseDouble(request.getParameter("exDiscountRate"));
				if (!exDiscountRate.equals(setting.getDiscountRate())) {
					dataService.recalculateCompletedProjects();
				}
			}
		}
		return "redirect:settings";
	}
}