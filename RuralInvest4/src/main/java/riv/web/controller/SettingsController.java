package riv.web.controller;
 
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import riv.objects.config.Setting;
import riv.objects.config.User;
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
	public String settings() {
		return "config/settings";
	}
	
	@RequestMapping(value="/config/settings", method=RequestMethod.POST)
	public String saveSettings(@Valid Setting setting, BindingResult result,
			HttpServletRequest request, @RequestParam("tempLogo") MultipartFile tempLogo) {
		User u = (User)request.getAttribute("user");
		boolean access = u.isAdministrator() && rivConfig.isAdmin();
		if (!access) { return "config/settings"; }
		
		// in case decimal separator is changing... make sure decimals are parsed correctly
		if (!result.hasFieldErrors("discountRate") && !result.hasFieldErrors("exchRate")) {
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
		}
		
		if (result.hasErrors()) {
			return "config/settings";
		} else {
			try { // Save in db
				if (tempLogo.isEmpty() && setting.getOrgLogo()==null) {
					setting.setOrgLogo(FileUtils.readFileToByteArray(new File(servletContext.getRealPath("/img/spacer.gif"))));
				} else if (!tempLogo.isEmpty()) {
					setting.setOrgLogo(tempLogo.getBytes());
				}
			} catch (IOException e) {
				LOG.error("IOException saving logo in settings to db", e);
			}
			
			Setting oldSetting = rivConfig.getSetting();
			boolean qualitativeChanged = setting.isQualActivitiesEnabled()!=oldSetting.isQualActivitiesEnabled() || setting.isQualAdminMisc1Enabled()!=oldSetting.isQualAdminMisc1Enabled()
					|| setting.isQualAdminMisc2Enabled()!=oldSetting.isQualAdminMisc2Enabled() || setting.isQualAdminMisc3Enabled()!=oldSetting.isQualAdminMisc3Enabled()
					|| setting.isQualAssumptionsEnabled()!=oldSetting.isQualAssumptionsEnabled() || setting.isQualBenefDescEnabled()!=oldSetting.isQualBenefDescEnabled()
					|| setting.isQualEnviroImpactEnabled()!=oldSetting.isQualEnviroImpactEnabled() || setting.isQualJustificationEnabled()!=oldSetting.isQualJustificationEnabled()
					|| setting.isQualMarketEnabled()!=oldSetting.isQualMarketEnabled() || setting.isQualOrganizationEnabled()!=oldSetting.isQualOrganizationEnabled()
					|| setting.isQualProjDescEnabled()!=oldSetting.isQualProjDescEnabled() || setting.isQualRequirementsEnabled()!=oldSetting.isQualRequirementsEnabled()
					|| setting.isQualSustainabilityEnabled()!=oldSetting.isQualSustainabilityEnabled() || setting.isQualTechnologyEnabled()!=oldSetting.isQualTechnologyEnabled()
					
					|| setting.getQualActivitiesWeight()!=oldSetting.getQualActivitiesWeight() || setting.getQualAdminMisc1Weight()!=oldSetting.getQualAdminMisc1Weight()
					|| setting.getQualAdminMisc2Weight()!=oldSetting.getQualAdminMisc2Weight() || setting.getQualAdminMisc3Weight()!=oldSetting.getQualAdminMisc3Weight()
					|| setting.getQualAssumptionsWeight()!=oldSetting.getQualAssumptionsWeight() || setting.getQualBenefDescWeight()!=oldSetting.getQualBenefDescWeight()
					|| setting.getQualEnviroImpactWeight()!=oldSetting.getQualEnviroImpactWeight() || setting.getQualJustificationWeight()!=oldSetting.getQualJustificationWeight()
					|| setting.getQualMarketWeight()!=oldSetting.getQualMarketWeight() || setting.getQualOrganizationWeight()!=oldSetting.getQualOrganizationWeight()
					|| setting.getQualProjDescWeight()!=oldSetting.getQualProjDescWeight() || setting.getQualRequirementsWeight()!=oldSetting.getQualRequirementsWeight()
					|| setting.getQualSustainabilityWeight()!=oldSetting.getQualSustainabilityWeight() || setting.getQualTechnologyWeight()!=oldSetting.getQualTechnologyWeight()
					;
			boolean discountChanged = !setting.getDiscountRate().equals(oldSetting.getDiscountRate());
			
			dataService.storeAppSetting(setting);
			setting.refreshCurrencyFormatter();
			boolean refreshAppConfigs = !request.getParameter("exLang").equals(setting.getLang());
			rivConfig.setSetting(dataService.getAppSetting(), refreshAppConfigs);
			
			
			if (discountChanged) { // if discount has changed, recalculate indicators
				dataService.recalculateCompletedProjects();
			}
			if (qualitativeChanged) { // only update qualitative analysis
				dataService.recalculateCompletedQualitativeAnalysis(setting);
			}
		}
		return "redirect:settings";
	}
}