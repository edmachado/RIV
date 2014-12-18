package riv.web.config;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import riv.objects.config.AppConfig;
import riv.objects.config.AppConfig1;
import riv.objects.config.AppConfig2;
import riv.objects.config.Beneficiary;
import riv.objects.config.EnviroCategory;
import riv.objects.config.FieldOffice;
import riv.objects.config.ProjectCategory;
import riv.objects.config.Setting;
import riv.objects.config.Status;
import riv.objects.config.User;
import riv.web.service.DataService;

@Component("rivConfig")
public class RivConfig {
	private Setting setting;
	private Map<Integer, User> users;
	private Map<Integer, Beneficiary> beneficiaries;
	private Map<Integer, ProjectCategory> categories;
	private Map<Integer, FieldOffice> fieldOffices;
	private Map<Integer, EnviroCategory> enviroCategories;
	private Map<Integer, Status> statuses;
	private Map<Integer, AppConfig1> appConfig1s;
	private Map<Integer, AppConfig2> appConfig2s;
	private MessageSource messageSource;
	private DataService dataService;
	private double version;
	private Map<String, String> labourTypes=new HashMap<String, String>();
	private Map<Integer, String> lengthUnits = new HashMap<Integer, String>();
	private Map<Integer, String> contribTypes = new HashMap<Integer, String>();
	private Map<Integer, String> recommendationTypes = new HashMap<Integer, String>();
	
	@Value("${av}")	private String admin;
	@Value("${buildLang}") private String buildLang;
	
	public RivConfig() {}
	
	@Autowired(required=true)
	public RivConfig(DataService dService, MessageSource messageSource) {
		this.messageSource=messageSource;
		this.dataService=dService;
		this.version=dataService.getLatestVersion().getVersion();
		reload();
		loadUsers();
		
		labourTypes.put("0", translate("units.pyears"));
		labourTypes.put("1", translate("units.pmonths"));
		labourTypes.put("2", translate("units.pweeks"));
		labourTypes.put("3", translate("units.pdays"));
		
		lengthUnits.put(0, translate("units.months"));
		lengthUnits.put(1, translate("units.weeks"));
		lengthUnits.put(2, translate("units.days.calendar"));
		lengthUnits.put(3, translate("units.days.week"));
		
		contribTypes.put(0, translate("projectContribution.contribType.govtCentral"));
		contribTypes.put(1, translate("projectContribution.contribType.govtLocal"));
		contribTypes.put(2, translate("projectContribution.contribType.ngoLocal"));
		contribTypes.put(3, translate("projectContribution.contribType.ngoIntl"));
		contribTypes.put(5, translate("projectContribution.contribType.beneficiary"));
		contribTypes.put(4, translate("projectContribution.contribType.other"));
		
		recommendationTypes.put(null, "");
		recommendationTypes.put(1, translate("project.recommendation.implement"));
		recommendationTypes.put(2, translate("project.recommendation.reject"));
		recommendationTypes.put(3, translate("project.recommendation.review"));
	}
	
	public boolean isAdmin() { return admin.equals("true"); }
	public boolean isQa() { return buildLang.equals("build-qa"); }
	public boolean isDemo() { return buildLang.equals("build-demo"); }
	public double getVersion() { return version; }
	
	public void reload() {
		setting = dataService.getAppSetting();
		if (setting==null) {
			setting=new Setting();
		}
		loadBeneficiaries();
		loadFieldOffices();
		loadCategories();
		loadEnviroCategories();
		loadStatuses();
		loadAppConfig1s();
		loadAppConfig2s();
	}
	
	public void loadUsers() {
		users = new LinkedHashMap<Integer, User>();
		for (User user : dataService.getUsers()) {
			users.put(user.getUserId(), user);
		}
	}

public Map<Integer, User> getUsers() {
	return users;
}
	
	public void storeAppConfig(AppConfig ac) {
		dataService.storeAppConfig(ac);
		if (ac.getClass().isAssignableFrom(Beneficiary.class)) {
			loadBeneficiaries();
		} else if  (ac.getClass().isAssignableFrom(FieldOffice.class)) {
			loadFieldOffices();
		} else if (ac.getClass().isAssignableFrom(ProjectCategory.class)) {
			loadCategories();
		} else if (ac.getClass().isAssignableFrom(EnviroCategory.class)) {
			loadEnviroCategories();
		} else if (ac.getClass().isAssignableFrom(Status.class)) {
			loadStatuses();
		} else if (ac.getClass().isAssignableFrom(AppConfig1.class)) {
			loadAppConfig1s();
		} else if (ac.getClass().isAssignableFrom(AppConfig2.class)) {
			loadAppConfig2s();
		}
	}
	
	public void deleteAppConfig(AppConfig ac) {
		if (ac.getClass().isAssignableFrom(Beneficiary.class)) {
			beneficiaries.remove(ac.getConfigId());
			dataService.deleteAppConfig(ac);
			loadBeneficiaries();
		} else if  (ac.getClass().isAssignableFrom(FieldOffice.class)) {
			fieldOffices.remove(ac.getConfigId());
			dataService.deleteAppConfig(ac);
			loadFieldOffices();
		} else if (ac.getClass().isAssignableFrom(ProjectCategory.class)) {
			categories.remove(ac.getConfigId());
			dataService.deleteAppConfig(ac);
			loadCategories();
		} else if (ac.getClass().isAssignableFrom(EnviroCategory.class)) {
			enviroCategories.remove(ac.getConfigId());
			dataService.deleteAppConfig(ac);
			loadEnviroCategories();
		} else if (ac.getClass().isAssignableFrom(Status.class)) {
			statuses.remove(ac.getConfigId());
			dataService.deleteAppConfig(ac);
			loadStatuses();
		} else if (ac.getClass().isAssignableFrom(AppConfig1.class)) {
			appConfig1s.remove(ac.getConfigId());
			dataService.deleteAppConfig(ac);
			loadAppConfig1s();
		} else if (ac.getClass().isAssignableFrom(AppConfig2.class)) {
			appConfig2s.remove(ac.getConfigId());
			dataService.deleteAppConfig(ac);
			loadAppConfig2s();
		}
	}

	public void setSetting(Setting setting) {
		this.setting = setting;
	}

	public Setting getSetting() {
		return setting;
	}

	public void setFieldOffices(Map<Integer, FieldOffice> fieldOffices) {
		this.fieldOffices = fieldOffices;
	}

	public Map<Integer, FieldOffice> getFieldOffices() {
		return fieldOffices;
	}
	
	public void loadFieldOffices() {
		List<FieldOffice> offices = dataService.getFieldOffices();
		LinkedHashMap<Integer, FieldOffice> fieldOffices = new LinkedHashMap<Integer, FieldOffice>();
		for (FieldOffice office : offices) {
			if (office.getConfigId()==-4) office.setDescription(translate("fieldOffice.generic"));
			fieldOffices.put(office.getConfigId(), office);
		}
		this.fieldOffices=fieldOffices;
	}
	
	public void setBeneficiaries(Map<Integer, Beneficiary> beneficiaries) {
		this.beneficiaries = beneficiaries;
	}

	public Map<Integer, Beneficiary> getBeneficiaries() {
		return beneficiaries;
	}
	public void loadBeneficiaries() {
		List<Beneficiary> benefs = dataService.getBeneficiaries();
		LinkedHashMap<Integer, Beneficiary> newBenefs = new LinkedHashMap<Integer, Beneficiary>();
		for (Beneficiary benef : benefs) {
			if (benef.getConfigId()==-3) benef.setDescription(translate("beneficiary.generic"));
			newBenefs.put(benef.getConfigId(), benef);
		}
		this.beneficiaries=newBenefs;
	}

	public void setAppConfig1s(Map<Integer, AppConfig1> a) {
		this.appConfig1s=a;
	}
	public Map<Integer, AppConfig1> getAppConfig1s() {
		return appConfig1s;
	}
	public void loadAppConfig1s() {
		List<AppConfig1> as = dataService.getAppConfig1s();
		LinkedHashMap<Integer, AppConfig1> newAs = new LinkedHashMap<Integer, AppConfig1>();
		for (AppConfig1 a : as) {
			if (a.getConfigId()==-8) a.setDescription(translate("customFields.appConfig1.generic"));
			newAs.put(a.getConfigId(), a);
		}
		this.appConfig1s=newAs;
	}
	
	public void setAppConfig2s(Map<Integer, AppConfig2> a) {
		this.appConfig2s=a;
	}
	public Map<Integer, AppConfig2> getAppConfig2s() {
		return appConfig2s;
	}
	public void loadAppConfig2s() {
		List<AppConfig2> as = dataService.getAppConfig2s();
		LinkedHashMap<Integer, AppConfig2> newAs = new LinkedHashMap<Integer, AppConfig2>();
		for (AppConfig2 a : as) {
			if (a.getConfigId()==-9) a.setDescription(translate("customFields.appConfig2.generic"));
			newAs.put(a.getConfigId(), a);
		}
		this.appConfig2s=newAs;
	}

	public void setCategories(Map<Integer, ProjectCategory> categories) {
		this.categories = categories;
	}

	
	public Map<Integer, ProjectCategory> getCategoriesIG() {
		HashMap<Integer, ProjectCategory> cats = new HashMap<Integer, ProjectCategory>();
		for (ProjectCategory cat : categories.values()) {
			if (cat.getIncomeGen()) {
				cats.put(cat.getConfigId(), cat);
			}
		}
		return cats;
	}
	public Map<Integer, ProjectCategory> getCategoriesNig() {
		HashMap<Integer, ProjectCategory> cats = new HashMap<Integer, ProjectCategory>();
		for (ProjectCategory cat : categories.values()) {
			if (!cat.getIncomeGen()) {
				cats.put(cat.getConfigId(), cat);
			}
		}
		return cats;
	}
	public Map<Integer, ProjectCategory> getCategories() {
		return categories;
	}
	public void loadCategories() {
		List<ProjectCategory> categories = dataService.getProjectCategories();
		LinkedHashMap<Integer, ProjectCategory> cats = new LinkedHashMap<Integer, ProjectCategory>();
		for (ProjectCategory cat : categories) {
			if (cat.getConfigId()==-5) cat.setDescription(translate("projectCategory.generic.ig"));
			if (cat.getConfigId()==-2) cat.setDescription(translate("projectCategory.generic.nig"));
			cats.put(cat.getConfigId(), cat);
		}
		this.categories=cats;
	}
	
	public void setEnviroCategories(Map<Integer, EnviroCategory> enviroCategories) {
		this.enviroCategories = enviroCategories;
	}

	public Map<Integer, EnviroCategory> getEnviroCategories() {
		return enviroCategories;
	}
	public void loadEnviroCategories() {
		List<EnviroCategory> envs=dataService.getEnviroCategories();
		LinkedHashMap<Integer, EnviroCategory> newEnvs = new LinkedHashMap<Integer, EnviroCategory>();
		for (EnviroCategory env : envs) {
			if (env.getConfigId()==-6) env.setDescription(translate("enviroCategory.generic"));
			newEnvs.put(env.getConfigId(), env);
		}
		this.enviroCategories=newEnvs;
	}
	
	public void setStatuses(Map<Integer, Status> statuses) {
		this.statuses = statuses;
	}

	public Map<Integer, Status> getStatuses() {
		return statuses;
	}
	public void loadStatuses() {
		List<Status> statuses = dataService.getStatuses();
		LinkedHashMap<Integer, Status> newStats = new LinkedHashMap<Integer, Status>();
		for (Status stat : statuses) {
			if (stat.getConfigId()==-7) stat.setDescription(translate("projectStatus.generic"));
			if (stat.getConfigId()==-20) stat.setDescription(translate("projectStatus.proposal"));
			if (stat.getConfigId()==-21) stat.setDescription(translate("projectStatus.approved"));
			if (stat.getConfigId()==-22) stat.setDescription(translate("projectStatus.investment"));
			if (stat.getConfigId()==-23) stat.setDescription(translate("projectStatus.operational"));
			newStats.put(stat.getConfigId(), stat);
		}
		this.statuses=newStats;
	}
	
	public boolean isComplete() {
		if (this.setting==null || this.setting.getLang()==null || this.setting.getLang().isEmpty()) {
			return false;
		} else {
			return true;
		}
	}
	
	private String translate(String messageCode) {
		String lang = setting==null || setting.getLang()==null ? "en" : setting.getLang();
		return messageSource.getMessage(messageCode, new Object[0], "", new Locale(lang));
	}
	
	public RivConfig copyForExport() throws SQLException {
		RivConfig newRc = new RivConfig();
		newRc.setSetting(this.getSetting().copy());
		
		newRc.setBeneficiaries(new HashMap<Integer,Beneficiary>());
		for (Beneficiary ac : beneficiaries.values()) {
			if (ac.getConfigId()!=-4) {
				Beneficiary nac = new Beneficiary();
				nac.setConfigId(ac.getConfigId());
				nac.setDescription(ac.getDescription());
				newRc.beneficiaries.put(ac.getConfigId(),nac);
			}
		}
		newRc.setCategories(new HashMap<Integer,ProjectCategory>());
		for (ProjectCategory ac : categories.values()) {
			if (ac.getConfigId()!=-5 && ac.getConfigId()!=-2) {
				ProjectCategory nac = new ProjectCategory();
				nac.setConfigId(ac.getConfigId());
				nac.setDescription(ac.getDescription());
				nac.setIncomeGen(ac.getIncomeGen());
				newRc.categories.put(ac.getConfigId(),nac);
			}
		}
		newRc.setFieldOffices(new HashMap<Integer,FieldOffice>());
		for (FieldOffice ac : fieldOffices.values()) {
			if (ac.getConfigId()!=-4) {
				FieldOffice nac = new FieldOffice();
				nac.setConfigId(ac.getConfigId());
				nac.setDescription(ac.getDescription());
				newRc.fieldOffices.put(ac.getConfigId(),nac);
			}
		}
		newRc.setEnviroCategories(new HashMap<Integer,EnviroCategory>());
		for (EnviroCategory ac : enviroCategories.values()) {
			if (ac.getConfigId()!=-6) {
				EnviroCategory nac = new EnviroCategory();
				nac.setConfigId(ac.getConfigId());
				nac.setDescription(ac.getDescription());
				newRc.enviroCategories.put(ac.getConfigId(),nac);
			}
		}
		newRc.setStatuses(new HashMap<Integer,Status>());
		for (Status ac : statuses.values()) {
			if (ac.getConfigId()!=-7) {
				Status nac = new Status();
				nac.setConfigId(ac.getConfigId());
				nac.setDescription(ac.getDescription());
				newRc.statuses.put(ac.getConfigId(),nac);
			}
		}
		newRc.setAppConfig1s(new HashMap<Integer,AppConfig1>());
		for (AppConfig1 ac : appConfig1s.values()) {
			if (ac.getConfigId()!=-8) {
				AppConfig1 nac = new AppConfig1();
				nac.setConfigId(ac.getConfigId());
				nac.setDescription(ac.getDescription());
				newRc.appConfig1s.put(ac.getConfigId(),nac);
			}
		}
		newRc.setAppConfig2s(new HashMap<Integer,AppConfig2>());
		for (AppConfig2 ac : appConfig2s.values()) {
			if (ac.getConfigId()!=-9) {
				AppConfig2 nac = new AppConfig2();
				nac.setConfigId(ac.getConfigId());
				nac.setDescription(ac.getDescription());
				newRc.appConfig2s.put(ac.getConfigId(),nac);
			}
		}
		
		return newRc;
	}
	
	public Map<String, String> getLabourTypes() {
		return labourTypes;
	}
	public Map<Integer, String> getLengthUnits() {
		return lengthUnits;
	}
	public Map<Integer, String> getContribTypes() {
		return contribTypes;
	}
	public Map<Integer, String> getRecommendationTypes() {
		return recommendationTypes;
	}
}