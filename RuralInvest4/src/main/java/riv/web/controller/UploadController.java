	package riv.web.controller;
 
import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.safehaus.uuid.UUIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import riv.objects.config.AppConfig1;
import riv.objects.config.AppConfig2;
import riv.objects.config.Beneficiary;
import riv.objects.config.EnviroCategory;
import riv.objects.config.FieldOffice;
import riv.objects.config.ProjectCategory;
import riv.objects.config.Status;
import riv.objects.config.User;
import riv.objects.profile.Profile;
import riv.objects.project.Project;
import riv.util.ExcelImportException;
import riv.util.Upgrader;
import riv.web.config.RivConfig;
import riv.web.config.RivLocaleResolver;
import riv.web.service.AttachTools;
import riv.web.service.DataService;
 
@Controller
@Scope("session")
public class UploadController implements Serializable {
	static final Logger LOG = LoggerFactory.getLogger(UploadController.class);
	private static final long serialVersionUID = -1124294766963818520L;
	
	@Autowired
    private DataService dataService;
	@Autowired
	private RivConfig rivConfig;
	@Autowired
	private AttachTools attachTools;
	@Autowired
	private Upgrader upgrader;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private ServletContext sc;
	@Autowired 
	private RivLocaleResolver rivLocaleResolver;
	
	private byte[] containingFile;
	private Object decoded;
	private String filename;
	
	@RequestMapping(value="/{type}/import",method=RequestMethod.GET)
	public String form(@PathVariable String type) {
		decoded=null;
		containingFile=null;
		filename=null;
		return "upload";
	}
	
	@RequestMapping(value="/{type}/import/confirm", method=RequestMethod.POST)
	public String confirm(@PathVariable String type, HttpServletRequest request) {
		if (type.equals("profile")) {
			Profile p = (Profile)decoded;
			// convert currency values to local currency
			if (p.isGeneric()) {
				double exchRate = Double.valueOf(request.getParameter("genericExchange"));
				p.convertCurrency(exchRate);
				p.setExchRate(exchRate);
			}
			p.setTechnician((User)request.getAttribute("user"));
			p.setWizardStep(1);
			p.setPrepDate(new java.util.Date());
			
			if (Boolean.parseBoolean(request.getParameter("overwriteOk"))) {
				Profile existing = dataService.getProfileByUniqueId(p.getUniqueId());
				try {
					dataService.deleteProfile(existing);
				} catch (Exception e) {
					LOG.error("Error deleting existing profile.",e);
					throw new RuntimeException("Error deleting existing profile.",e);
					//return "Error deleting existing profile:"+e.getMessage();
				}
			} else {
				p.setUniqueId(UUIDGenerator.getInstance().generateTimeBasedUUID().toByteArray());
			}
			
			try {
				dataService.storeProfile(p, false);
				attachTools.saveAttachedFilesFromZip(p, containingFile);
			} catch (javax.validation.ConstraintViolationException ex) {
				LOG.error("Constraint exception when importing profile");
				throw new RuntimeException("Constraint error:"+ex.getLocalizedMessage(),ex);
			} catch (Exception e) {
				LOG.error("Error saving imported profile.", e);
				throw new RuntimeException("Error saving imported profile.", e);
			}
			
			clearFormData();
			String rename=Boolean.parseBoolean(request.getParameter("overwriteOk")) ? "?rename=true" : "";
			return "redirect:../step1/"+p.getProfileId()+rename;
		} else { // project
			Project p = (Project)decoded;
			p.setWizardStep(1);
			p.setTechnician((User)request.getAttribute("user"));
			p.setPrepDate(new java.util.Date());
			if (p.isGeneric()) {
				double exchRate = Double.valueOf(request.getParameter("genericExchange"));
				p.convertCurrency(exchRate, rivConfig.getSetting().getDecimalLength());
				p.setExchRate(exchRate);
			}
			
			if (Boolean.parseBoolean(request.getParameter("overwriteOk"))) {
				Project existing = dataService.getProjectByUniqueId(p.getUniqueId());
				try {
					dataService.deleteProject(existing);
				} catch (Exception e) {
					LOG.error("Error deleting existing project.", e);
					throw new RuntimeException("Error deleting existing project.", e);
				}
			} else {
				p.setUniqueId(UUIDGenerator.getInstance().generateTimeBasedUUID().toByteArray());
			}
			
			try {
				dataService.storeProject(p, false);
				attachTools.saveAttachedFilesFromZip(p,  containingFile);
			} catch (javax.validation.ConstraintViolationException ex) {
				LOG.error("Constraint exception when importing project");
				throw new RuntimeException("Constraint error: "+ex.getLocalizedMessage(),ex);
			} catch (Exception e) {
				LOG.error("Error saving imported project.", e);
				throw new RuntimeException("Error saving imported project.", e);
			}
			
			clearFormData();
			String rename=Boolean.parseBoolean(request.getParameter("overwriteOk")) ? "?rename=true" : "";
			return "redirect:../step1/"+p.getProjectId()+rename;
		}
	}
	
	
	private void clearFormData() {
		decoded=null;containingFile=null;filename=null;
	}
	private String uploadError(String error, Model model, Locale locale) {
		model.addAttribute("error", messageSource.getMessage(error, null, locale));
		clearFormData();
		return "upload";
	}
	private String needConfirm(boolean generic, boolean exists, Model model) {
		model.addAttribute("filename", filename);
		if (generic) { 
			model.addAttribute("generic",true);
		}
		if (exists) {
			model.addAttribute("exists", true);
		}
		return "uploadConfirm";
	}
	
	@RequestMapping(value="/config/admin/import", method=RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	public @ResponseBody String importConfig(Model model, MultipartHttpServletRequest request, HttpServletResponse response) {
		Locale locale=rivLocaleResolver.resolveLocale(request);
		
		String error = "";
		User user = (User)request.getAttribute("user");
		
		if (!user.isAdministrator()) {
			System.out.println("Access denied importing configuration");
		} else {
			Iterator<String> itr =  request.getFileNames();
			MultipartFile mpf = request.getFile(itr.next());
			try {
				byte[] settings=mpf.getBytes();
				processUpload(settings, "config", model, user, true, locale);
				if (model.containsAttribute("error")) {
					error = (String) model.asMap().get("error");
				}
			} catch (Exception e) {
				e.printStackTrace(System.out);
				error = e.getMessage();
			}
		}
		
		if (error.isEmpty()) {
			return "{\"success\": \"success\"}";
		} else {
			return "{\"error\": \""+error.replace("\"", "\\\"")+"\"}";
		}
	}
	
	@RequestMapping(value="/config/admin/restore", method=RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	public @ResponseBody String importBackup(Model model, MultipartHttpServletRequest request, HttpServletResponse response) {
		Locale locale=rivLocaleResolver.resolveLocale(request);
		
		String error = "";
		User user = (User)request.getAttribute("user");
		
		if (!user.isAdministrator()) {
			System.out.println("Access denied importing backup");
		} else {
			
			FileOutputStream os;
		
			// add new data
			List<File> files = new ArrayList<File>(4);
			File settings=null;
			try {
				Iterator<String> itr =  request.getFileNames();
				MultipartFile file=request.getFile(itr.next());
				ByteArrayInputStream bais= new ByteArrayInputStream(file.getBytes());
				ZipEntry entry;
				//TODO confirm is a zip file
				ZipInputStream zis = new ZipInputStream(bais);
				
				// get files
				while ((entry = zis.getNextEntry()) != null) {
					File f = File.createTempFile(entry.getName(), "riv");
					os=new FileOutputStream(f);
					IOUtils.copy(zis, os);
					os.close(); os.flush();
					if (f.getName().startsWith("settings")) {
						settings=f;
					} else {
						files.add(f);
					}
					zis.closeEntry();
				}
				zis.close();
				zis=null;
				
				// confirm that settings are correct before deleting current data
				if (settings==null || getDecoded(FileUtils.readFileToByteArray(settings), "config")!=null) {
					String e = messageSource.getMessage("admin.restore.error.notBackup", null, locale);
					throw new ExcelImportException(e);
				}
				
				// possible function (if desired)
				// if no profiles or projects, should be uploaded as configuration to avoid confusion
//				if (files.size()==0) {
//					String e = messageSource.getMessage("",  null, locale);
//					throw new ExcelImportException(e);
//				}

				// delete current data
				dataService.deleteAll(true, true);
				dataService.deleteAll(true, false);
				dataService.deleteAll(false, true);
				dataService.deleteAll(false, false);
				dataService.deleteAllAppConfigs();
				
				
				// add settings
				processUpload(FileUtils.readFileToByteArray(settings), "config", model, user, true, locale);
				
				// add projects and profiles
				ByteArrayOutputStream baos;
				for (File f : files) {
					bais= new ByteArrayInputStream(FileUtils.readFileToByteArray(f));
					zis = new ZipInputStream(bais);
					String type = f.getName().contains("-project") ? "project" : "profile";
					
					while ((entry = zis.getNextEntry()) != null) {
						baos = new ByteArrayOutputStream();
						IOUtils.copy(zis, baos);
						processUpload(baos.toByteArray(), type, model, user, true, locale);
						zis.closeEntry();
					}
					zis.close();zis=null;
				}
			} catch (ExcelImportException e) {
				e.printStackTrace(System.out);
				error = e.getMessage();
			} catch (Exception e) {
				e.printStackTrace(System.out);
				error = e.getMessage();
			}

			dataService.checkProjectsOnUpgrade();
			dataService.checkProfilesOnUpgrade();
			dataService.recalculateCompletedProjects();
			dataService.recalculateCompletedProfiles();
		}
		
		if (error.isEmpty()) {
			return "{\"success\": \"success\"}";
		} else {
			return "{\"error\": \""+error.replace("\"", "\\\"")+"\"}";
		}
	}
	
	@RequestMapping(value = "/{type}/import", method = RequestMethod.POST)
	public String upload(@PathVariable String type, Model model, MultipartHttpServletRequest request, HttpServletResponse response,
			@RequestParam(required=true) Boolean allowComplete) throws Exception { 
		Locale locale=rivLocaleResolver.resolveLocale(request);
		
		// config import moved to own function
		if (type.equals("config")) {
			return "redirect:../home";
		}
		
		User user = (User)request.getAttribute("user");
		
		Iterator<String> itr =  request.getFileNames();
		MultipartFile mpf = request.getFile(itr.next());
		boolean complete = allowComplete!=null && allowComplete==true;
		return processUpload(mpf.getBytes(), type, model, user, complete, locale);
	}
	
	private String getDecoded(byte[] bytes, String type) {
		String result=null;
		try {
			containingFile = bytes;
			byte[] rivFile =  attachTools.getFileFromZip(containingFile, 0);
			rivFile = upgrader.upgradeXml(rivFile);
			
			// get object from uploaded file
			ByteArrayInputStream bais= new ByteArrayInputStream(rivFile);				
			XMLDecoder decoder = new XMLDecoder(bais);
			try {
				decoded = decoder.readObject();
			} catch (Exception ex) {
				if (type.equals("config")) { result="error.import.notSettings"; }
				else { result="error.import.wrongType"; }
			} finally {
				rivFile=null;
				decoder.close();
				bais.close();
			}
		} catch (Exception e) {
			result="error.import.notARivFile";
		} 
		return result;
	}
	
	private String processUpload(byte[] bytes, String type, Model model, User user, boolean markComplete, Locale locale) {
		// 1. Get main riv object and keep containing file
		String result = getDecoded(bytes, type);
		if (result != null) { return uploadError(result, model, locale); }
		
		// 2. Check if any errors in riv object
		String error=checkRivObjectForErrors(type);
		if (error!=null) { return uploadError(error, model, locale); }
		
		// 3. Update/require confirmations and save
		if (type.equals("config")) {
			return handleConfig(model);
		} else if (type.equals("profile")) {
			return handleProfile(user, model, markComplete, locale);
		} else  {
			return handleProject(user, model, markComplete);
		}
	}
	
	private String handleProject(User user, Model model, boolean markComplete) {
		Project project = (Project)decoded;
		
		boolean exists = dataService.getProjectByUniqueId(project.getUniqueId()) !=null;
		if (!exists &! project.isGeneric()) {
			if (!markComplete) {
				project.setWizardStep(1);
			}
			project.setPrepDate(new java.util.Date());
			project.setTechnician(user);
			dataService.storeProject(project, false);
			attachTools.saveAttachedFilesFromZip(project, containingFile);
			clearFormData();
			return "redirect:step1/"+project.getProjectId();
		} else { 
			return needConfirm(project.isGeneric(), exists, model);
		}
	}
	
	private String handleProfile(User user, Model model, boolean markComplete, Locale locale) {
		Profile profile = (Profile)decoded;
		
		boolean exists = dataService.getProfileByUniqueId(profile.getUniqueId())!=null;
		if (!exists &! profile.isGeneric()) { // import directly
			if (!markComplete) {
				profile.setWizardStep(1);
			}
			profile.setPrepDate(new java.util.Date());
			profile.setTechnician(user);
			
			try {
				dataService.storeProfile(profile, false);
				attachTools.saveAttachedFilesFromZip(profile, containingFile);
				clearFormData();
				return "redirect:step1/"+profile.getProfileId();
			} catch (Exception e) {
				return uploadError("An error occurred when saving profile. "+e.getMessage(), model, locale);
			}
		} else { 
			return needConfirm(profile.isGeneric(), exists, model);
		}
	}
	
	private String handleConfig(Model model) {
		RivConfig rcNew = (RivConfig)decoded;
		
		for (Beneficiary ac : rcNew.getBeneficiaries().values()) {
			dataService.storeAppConfig(ac);
		}
		for (FieldOffice ac : rcNew.getFieldOffices().values()) {
			dataService.storeAppConfig(ac);
		}
		for (ProjectCategory ac : rcNew.getCategories().values()) {
			dataService.storeAppConfig(ac);
		}
		for (Status ac : rcNew.getStatuses().values()) {
			dataService.storeAppConfig(ac);
		}
		for (EnviroCategory ac : rcNew.getEnviroCategories().values()) {
			dataService.storeAppConfig(ac);
		}
		for (AppConfig1 ac : rcNew.getAppConfig1s().values()) {
			dataService.storeAppConfig(ac);
		}
		for (AppConfig2 ac : rcNew.getAppConfig2s().values()) {
			dataService.storeAppConfig(ac);
		}
		
		// <4.0 userlogo stored in XML serialization
		byte[] logoBytes;
		if (rcNew.getSetting().getUserLogo()!=null) { // version <4.0 saved orgLogo in XML
			logoBytes=rcNew.getSetting().getUserLogo();
		} else { // get logo from zip file (>=4.0)
			logoBytes = attachTools.getFileFromZip(containingFile, 1);
		}
		if (logoBytes.length==0) { // just in case, use RuralInvest logo
			File rivLogo = new File(sc.getRealPath("img/logo40.gif"));
			try {
				logoBytes = FileUtils.readFileToByteArray(rivLogo);
			} catch (IOException e) {
				LOG.error("Error reading default logo image.",e);
			}
		}
		
		rcNew.getSetting().setOrgLogo(logoBytes);
		rcNew.getSetting().setSettingId(rivConfig.getSetting().getSettingId());
		dataService.storeAppSetting(rcNew.getSetting());
		
		rivConfig.reload();
		clearFormData();
		return "redirect:settings";
	}
	
	private String checkRivObjectForErrors(String type) {
		if (type.equals("config")) {
			if (decoded.getClass()!=RivConfig.class)  {
				return "error.import.notSettings";
			} else {
				RivConfig rc = (RivConfig)decoded;
				upgrader.upgradeRivConfig(rc);
				// do AppConfigs jave configId that is already used by an appconfig object from another class?
				// compatibility problem!
				if (dataService.isIdsUsedByOtherClasses(FieldOffice.class, rc.getFieldOffices().keySet()) ||
						dataService.isIdsUsedByOtherClasses(AppConfig1.class, rc.getAppConfig1s().keySet()) ||
						dataService.isIdsUsedByOtherClasses(AppConfig2.class, rc.getAppConfig2s().keySet()) ||
						dataService.isIdsUsedByOtherClasses(Beneficiary.class, rc.getBeneficiaries().keySet()) ||
						dataService.isIdsUsedByOtherClasses(EnviroCategory.class, rc.getEnviroCategories().keySet()) ||
						dataService.isIdsUsedByOtherClasses(ProjectCategory.class, rc.getCategories().keySet()) ||
						dataService.isIdsUsedByOtherClasses(Status.class, rc.getStatuses().keySet())) {
					return "error.import.incompatibleSettings";
				}
			}
		} else if (type.equals("profile")) {
			if (decoded.getClass()!=Profile.class) {
				return "error.import.wrongType";
			} else {
				Profile profile = (Profile)decoded;
				// check version
				if (profile.getRivVersion()>rivConfig.getVersion()) {
					return "error.import.futureVersion";
				}
				
				
				upgrader.upgradeProfile(profile);
				
				// check if config is compatible
				if (! profile.isGeneric()) { 
					if (!rivConfig.getFieldOffices().containsKey(profile.getFieldOffice().getConfigId())) 
						return "error.import.noFieldOffice";
					if (!rivConfig.getStatuses().containsKey(profile.getStatus().getConfigId())) 
						return "error.import.noStatus";
				}
			}
		} else { // it's a project
			if (decoded.getClass()!=Project.class) {
				return "error.import.wrongType";
			} else {
				Project project = (Project)decoded;
				upgrader.upgradeProject(project);
			
				if (!project.isGeneric()) { 
					if (!rivConfig.getBeneficiaries().containsKey(project.getBeneficiary().getConfigId())) {
						return "error.import.noBeneficiary";
					}
					if (!rivConfig.getCategories().containsKey(project.getProjCategory().getConfigId())) {
						return "error.import.noCategory";
					}
					if (!rivConfig.getFieldOffices().containsKey(project.getFieldOffice().getConfigId())) {
						return "error.import.noFieldOffice";
					}
					if (!rivConfig.getEnviroCategories().containsKey(project.getEnviroCategory().getConfigId())) {
						return "error.import.noEnviroCategory";
					}
					if (!rivConfig.getStatuses().containsKey(project.getStatus().getConfigId())) {
						return "error.import.noStatus";
					}
					if (!rivConfig.getAppConfig1s().containsKey(project.getAppConfig1().getConfigId()) ||
						!rivConfig.getAppConfig2s().containsKey(project.getAppConfig2().getConfigId())) {
						return "error.import.noAppConfig";
					}
				}
			}
		}
		return null;	
	}
}