package riv.web.controller;
 
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import riv.objects.AttachedFile;
import riv.objects.Probase;
import riv.objects.config.AppConfig1;
import riv.objects.config.AppConfig2;
import riv.objects.config.Beneficiary;
import riv.objects.config.EnviroCategory;
import riv.objects.config.FieldOffice;
import riv.objects.config.ProjectCategory;
import riv.objects.config.Status;
import riv.objects.config.User;
import riv.objects.config.Version;
import riv.web.config.RivConfig;
import riv.web.config.RivLocaleResolver;
import riv.web.service.AttachTools;
import riv.web.service.DataService;
import riv.web.service.Exporter;
 
@Controller
public class MainController {
	static final Logger LOG = LoggerFactory.getLogger(MainController.class);
	
	@Autowired
    private DataService dataService;
	@Autowired
	private RivConfig rivConfig;
	@Autowired 
	private RivLocaleResolver rivLocaleResolver;
	@Autowired
	MessageSource messageSource;
	@Autowired
	AttachTools attachTools;
	@Autowired
	ServletContext sc;
	@Autowired
	Exporter exporter;
	
	@PostConstruct
	public void onAppStart() {
		Version v = dataService.getLatestVersion();
		if (v.isRecalculate()) {
			dataService.upgradeFixes(v);
			recalculate();
			v.setRecalculate(false);
			dataService.storeVersion(v);
		}
	}
	
    @RequestMapping("/login")
    public String login(@RequestParam(required=false) String lang, Model model, HttpServletRequest request) {
    	if (rivConfig.isDemo() && lang!=null) {
    		request.setAttribute("pageLang", lang);
		}
		model.addAttribute("rivConfig", rivConfig);
    	return "login";
    }
    
	@RequestMapping("/home")
    public String home(Model model) {
		model.addAttribute("homeData", dataService.homeData());
		return "home";
    }
	
	@RequestMapping("config/reset")
	public String reset(HttpServletRequest request) {
		User u = (User)request.getAttribute("user");
		if (u.isAdministrator()) {
			dataService.deleteAll(true, true);
			dataService.deleteAll(true, false);
			dataService.deleteAll(false, true);
			dataService.deleteAll(false, false);
			dataService.deleteAllAppConfigs();
			rivConfig.reload();
		}
		return "redirect:settings";
	}
	
	/*
	 * Used by testing framework
	 */
	@RequestMapping("help/deleteAll")
	public String deleteAll(@RequestParam String type, @RequestParam String ig, HttpServletRequest request) {
		User u = (User)request.getAttribute("user");
		if (u.isAdministrator()) {
			dataService.deleteAll(type.equals("project"), ig.equals("true"));
		}
		return "redirect:../home";
	}
	
	/*
	 * Used by testing framework
	 */
	@RequestMapping("help/deleteAllAppConfigs")
	public String deleteAllAppConfigs(HttpServletRequest request) {
		User u = (User)request.getAttribute("user");
		if (u.isAdministrator()) {
			dataService.deleteAllAppConfigs();
			rivConfig.reload();
		}
		return "redirect:../home";
	}
	
	@RequestMapping("help/recalculate")
	public String recalculate() {
		attachTools.migrateFromVersion3(sc.getRealPath("/WEB-INF/data"));
		migrateVersion3logo();
		dataService.checkProjectsOnUpgrade();
		dataService.checkProfilesOnUpgrade();
		dataService.recalculateCompletedProjects();
		dataService.recalculateCompletedProfiles();
		return "redirect:../home";
	}
	
	public void migrateVersion3logo() {
		File f = new File(sc.getRealPath("/WEB-INF/data/userLogo"));
		if (f.exists()) {
			if (rivConfig.getSetting().getOrgLogo()==null) {
				byte[] bytes=new byte[0];
				try {
					bytes = FileUtils.readFileToByteArray(f);
				} catch (IOException e) {
					LOG.error("Error reading logo file for migration.");
				}
				rivConfig.getSetting().setOrgLogo(bytes);
				dataService.storeAppSetting(rivConfig.getSetting());
			}
			f.delete();
		}
	}
	
	@RequestMapping("help/log")
	public void appLog(HttpServletResponse response) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmm");
		String filename = "riv-application-"+format.format(new Date())+".log";
		response.addHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
		response.setContentType("application/force-download; name=\""+filename+"\"");
		OutputStream out=null;
		try {
			out= response.getOutputStream();
		} catch (IOException e) {
			LOG.error("Error getting output stream.",e);
		}
		exporter.errorDiagnostic(out);
	}
	
	@RequestMapping(value="/config/orgLogo")
	public void orgLogo(OutputStream out) throws SQLException, IOException {
		IOUtils.copy(new ByteArrayInputStream(rivConfig.getSetting().getOrgLogo()), out);
	}
	
	@RequestMapping("/{type}/{id}/attach/{fileId}/delete")
	public String deleteAttachedFile(@PathVariable String type, @PathVariable Integer id, @PathVariable int fileId) {
		dataService.deleteAttachedFile(fileId,type.equals("project"));
		return "redirect:../../../step1/"+id;
	}
	
	@RequestMapping(value="/{type}/{id}/attach/{fileId}/{filename}")
	public void downloadAttachedFile(@PathVariable String type, @PathVariable Integer id, @PathVariable int fileId, @PathVariable String filename, OutputStream out) {	
		dataService.copyAttachedFileContent(fileId, out, type.equals("project"));
		try {
			out.flush();
	        out.close();
		} catch (IOException e) {
			LOG.error("Error closing output stream.",e);
		}
	}
	
	@RequestMapping(value="/{type}/{id}/attach", method=RequestMethod.POST)
	public String storeAttachedFileProject(@PathVariable String type, @PathVariable Integer id, Model model, MultipartHttpServletRequest request) {
		Probase probase = type.equals("profile") ? dataService.getProfile(id, 1) : dataService.getProject(id, 1);
		
		Iterator<String> itr =  request.getFileNames();
		MultipartFile mpf = request.getFile(itr.next());
		String error="";
		
		// get disk space available and check if filename is unique
		long dirSize=0L;
		for (AttachedFile pf : attachTools.getAttached(probase.getProId(), probase.isProject(), false)) {
			if (pf.getFilename().equals(mpf.getOriginalFilename())) {
				error="attach.fileExists";
				break;
			}
			dirSize+=pf.getLength();
		}
		if (mpf.getSize()>AttachTools.dirSizeLimit - dirSize) {
			error="attach.noSpace";
		}
		
		if (!error.isEmpty()) {
			model.addAttribute("error", messageSource.getMessage(error, new Object[]{}, new Locale(rivConfig.getSetting().getLang())));
			attachPageAttributes(request, probase, model);
			return "attachFile";
		} else {
			try {
				attachTools.addAttachedFile(probase, mpf.getOriginalFilename(), mpf.getBytes());
			} catch (IOException e) {
				LOG.error("Error getting contents of attached file.",e);
			}
			return "redirect:../step1/"+probase.getProId();
		}
	}
	
	
	
	private void attachPageAttributes(HttpServletRequest request, Probase p, Model model) {
		User u = (User)request.getAttribute("user");
		model.addAttribute("commandName","probase");
		model.addAttribute("probase",p);
		model.addAttribute("accessOK", p.isShared() || p.getTechnician().getUserId().equals(u.getUserId()));
		model.addAttribute("currentStep", "1");
		model.addAttribute("currentId",p.getProId());
		model.addAttribute("wizardStep",p.getWizardStep());
		String type = p.isProject() ? "project" : "profile";
		if (p.getIncomeGen()) { 
			model.addAttribute("menuType",type);
		} else { 
			model.addAttribute("menuType",type+"Noninc");
		}
		
		long dirSize=0L;
		List<AttachedFile> files = attachTools.getAttached(p.getProId(), p.isProject(), false);
		for (AttachedFile pf : files) {
			dirSize+=pf.getLength();
		}
		model.addAttribute("files",files);
		model.addAttribute("dirSize", attachTools.humanReadableInt(dirSize));
		model.addAttribute("freeSpace", attachTools.humanReadableInt(AttachTools.dirSizeLimit-dirSize));
	
	}
	
	@RequestMapping(value="/{type}/{id}/attach", method=RequestMethod.GET)
	public String attachFile(@PathVariable String type, @PathVariable Integer id, Model model, HttpServletRequest request) {
		Probase p = type.equals("profile") ? dataService.getProfile(id, 1) : dataService.getProject(id, 1);
		attachPageAttributes(request, p, model);
		return "attachFile";
	}
	
	@RequestMapping("/help/error")
	public String throwException() throws IOException {
		boolean throwException = true;
		if (throwException) throw new IOException("This is my IOException");
		return "home";
	}
	
	@RequestMapping(value="/help/{page}", method=RequestMethod.GET)
	public String getHelp(@PathVariable String page, Model model) {
		Version v = dataService.getLatestVersion();
		model.addAttribute("version",v.getVersion());
		return "help/"+page;
	}
	
	// PROJECT INDICATORS
	@RequestMapping(value="/config/indicators", method=RequestMethod.GET)
	public String getIndicators(HttpServletRequest request) {
		return "redirect:indicators/"+((User)request.getAttribute("user")).getUserId();
	}
	
	// USERS
	@RequestMapping("/config/user")
    public String users(Model model) {
    	model.addAttribute("users", rivConfig.getUsers().values());
    	return "config/users";
    }
	
	// FIELD OFFICES
	 @RequestMapping("/config/office")
	 public String officeList(Model model, HttpServletRequest request) {
		Locale locale=rivLocaleResolver.resolveLocale(request);
		model.addAttribute("type","office");
		model.addAttribute("appConfigList",rivConfig.getFieldOffices().values());
		model.addAttribute("usage",dataService.appConfigUsage(FieldOffice.class.getSimpleName(), "fieldOffice", true));
		model.addAttribute("step","3");
		model.addAttribute("pageTitle",translate("fieldOffice.fieldOffices", locale));
		model.addAttribute("deleteWarn",translate("fieldOffice.delete.warn", locale));
		model.addAttribute("addNew",translate("fieldOffice.addOffice", locale));
		model.addAttribute("addImage","office.gif");
		model.addAttribute("description",translate("fieldOffice.description", locale));
		return "config/appConfigList";
	 }
	 // PROJECT CATEGORIES 
	 @RequestMapping("/config/category")
	 public String categoryList(Model model, HttpServletRequest request) {
		 Locale locale=rivLocaleResolver.resolveLocale(request);
		 model.addAttribute("type","category");
		 model.addAttribute("appConfigList",rivConfig.getCategories().values());
		 model.addAttribute("usage",dataService.appConfigUsage(ProjectCategory.class.getSimpleName(), "projCategory", false));
		 model.addAttribute("step","4");
		 model.addAttribute("pageTitle",translate("projectCategory.categories", locale));
		 model.addAttribute("deleteWarn",translate("projectCategory.delete.warn", locale));
		 model.addAttribute("addNew",translate("projectCategory.addCat", locale));
		 model.addAttribute("addImage","category.gif");
		 model.addAttribute("description",translate("projectCategory.description", locale));
		return "config/appConfigList";
	 }
	 // BENEFICIARIES
	 @RequestMapping("/config/beneficiary")
	 public String benefList(Model model, HttpServletRequest request) {
		 Locale locale=rivLocaleResolver.resolveLocale(request);
		 model.addAttribute("type","beneficiary");
		 model.addAttribute("appConfigList",rivConfig.getBeneficiaries().values());
		 model.addAttribute("usage",dataService.appConfigUsage(Beneficiary.class.getSimpleName(), "beneficiary", false));
		 model.addAttribute("step","5");
		 model.addAttribute("pageTitle",translate("beneficiary.beneficiaries", locale));
		 model.addAttribute("deleteWarn",translate("beneficiary.delete.warn", locale));
		 model.addAttribute("addNew",translate("beneficiary.addBenef", locale));
		 model.addAttribute("addImage","beneficiaries.gif");
		 model.addAttribute("description",translate("beneficiary.description", locale));
		return "config/appConfigList";
	 }
	 // ENVIRO CATEGORY
		@RequestMapping("/config/enviroCategory")
		 public String enviroCatList(Model model, HttpServletRequest request) {
			Locale locale=rivLocaleResolver.resolveLocale(request);
			 model.addAttribute("type","enviroCategory");
			 model.addAttribute("appConfigList",rivConfig.getEnviroCategories().values());
			 model.addAttribute("usage",dataService.appConfigUsage(EnviroCategory.class.getSimpleName(), "enviroCategory", false));
			 model.addAttribute("step","6");
			 model.addAttribute("pageTitle",translate("enviroCategory.categories", locale));
			 model.addAttribute("deleteWarn",translate("enviroCategory.delete.warn", locale));
			 model.addAttribute("addNew",translate("enviroCategory.addCat", locale));
			 model.addAttribute("addImage","category.gif");
			 model.addAttribute("description",translate("enviroCategory.description", locale));
			return "config/appConfigList";
		 }

		// PROJECT STATUSES 
		@RequestMapping("/config/status")
		 public String statusList(Model model, HttpServletRequest request) {
			Locale locale=rivLocaleResolver.resolveLocale(request);
			 model.addAttribute("type","status");
			 model.addAttribute("appConfigList",rivConfig.getStatuses().values());
			 model.addAttribute("usage",dataService.appConfigUsage(Status.class.getSimpleName(), "status", true));
			 model.addAttribute("step","7");
			 model.addAttribute("pageTitle",translate("projectStatus.statuses", locale));
			 model.addAttribute("deleteWarn",translate("projectStatus.delete.warn", locale));
			 model.addAttribute("addNew",translate("projectStatus.addStatus", locale));
			 model.addAttribute("addImage","category.gif");
			 model.addAttribute("description",translate("status.description", locale));
			return "config/appConfigList";
		 }
		
		// CUSTOM APPCONFIGS 
		@RequestMapping("/config/appConfig1")
		 public String appConfig1List(Model model, HttpServletRequest request) {
			Locale locale=rivLocaleResolver.resolveLocale(request);
			 model.addAttribute("type","appConfig1");
			 model.addAttribute("appConfigList",rivConfig.getAppConfig1s().values());
			 model.addAttribute("usage",dataService.appConfigUsage(AppConfig1.class.getSimpleName(), "appConfig1", false));
			 model.addAttribute("step","9");
			 model.addAttribute("pageTitle",rivConfig.getSetting().getAdmin1Title());
			 model.addAttribute("deleteWarn",translate("customFields.appConfig.delete.warn", locale));
			 model.addAttribute("addNew",translate("customFields.add", locale));
			 model.addAttribute("addImage","category.gif");
			 model.addAttribute("description",translate("customFields.description", locale));
			return "config/appConfigList";
		 }
		
		@RequestMapping("/config/appConfig2")
		 public String appConfig2List(Model model, HttpServletRequest request) {
			Locale locale=rivLocaleResolver.resolveLocale(request);
			 model.addAttribute("type","appConfig2");
			 model.addAttribute("appConfigList",rivConfig.getAppConfig2s().values());
			 model.addAttribute("usage",dataService.appConfigUsage(AppConfig2.class.getSimpleName(), "appConfig2", false));
			 int step = rivConfig.getSetting().getAdmin1Enabled() ? 10 : 9;
			 model.addAttribute("step",step);
			 model.addAttribute("pageTitle",rivConfig.getSetting().getAdmin2Title());
			 model.addAttribute("deleteWarn",translate("customFields.appConfig.delete.warn", locale));
			 model.addAttribute("addNew",translate("customFields.add", locale));
			 model.addAttribute("addImage","category.gif");
			 model.addAttribute("description",translate("customFields.description", locale));
			return "config/appConfigList";
		 }
    
	private String translate(String messageCode, Locale locale) {
		return messageSource.getMessage(messageCode, new Object[0], "", locale);
	}
}