	package riv.web.controller;
 
import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
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
import riv.util.Upgrader;
import riv.web.config.RivConfig;
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
				p.convertCurrency(exchRate);
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
	private String uploadError(String error, Model model) {
		model.addAttribute("error", messageSource.getMessage(error, null, new Locale(rivConfig.getSetting().getLang())));
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
	
	@RequestMapping(value = "/{type}/import", method = RequestMethod.POST)
	public String upload(@PathVariable String type, Model model, MultipartHttpServletRequest request, HttpServletResponse response) { 
		Iterator<String> itr =  request.getFileNames();
		MultipartFile mpf = request.getFile(itr.next());
		
		// 1. Get main riv object and keep containing file
		try {
			filename = mpf.getOriginalFilename();
			containingFile = mpf.getBytes();
			byte[] rivFile =  attachTools.getFileFromZip(containingFile, 0);
			rivFile = upgrader.upgradeXml(rivFile);
			
			// get object from uploaded file
			ByteArrayInputStream bais= new ByteArrayInputStream(rivFile);				
			XMLDecoder decoder = new XMLDecoder(bais);
			try {
				decoded = decoder.readObject();
			} catch (Exception ex) {
				if (type.equals("config")) { return uploadError("error.import.notSettings", model); }
				else { return uploadError("error.import.wrongType", model); }
			} finally {
				rivFile=null;
				decoder.close();
				bais.close();
			}
		} catch (Exception e) {
			return uploadError("error.import.notARivFile", model);
		} 
		
		// 2. Check if any errors in riv object
		String error=checkRivObjectForErrors(type);
		if (error!=null) { return uploadError(error, model); }
		
		// 3. Update/require confirmations and save
		if (type.equals("config")) {
			return handleConfig(model);
		} else if (type.equals("profile")) {
			return handleProfile((User)request.getAttribute("user"), model);
		} else  {
			return handleProject((User)request.getAttribute("user"), model);
		}
	}
	
	private String handleProject(User user, Model model) {
		Project project = (Project)decoded;
		
		boolean exists = dataService.getProjectByUniqueId(project.getUniqueId()) !=null;
		if (!exists &! project.isGeneric()) {
			project.setWizardStep(1);
			project.setPrepDate(new java.util.Date());
			project.setTechnician(user);
			//try {
				dataService.storeProject(project, false);
				attachTools.saveAttachedFilesFromZip(project, containingFile);
				clearFormData();
				return "redirect:step1/"+project.getProjectId();
			//} catch (Exception e) {
				//return uploadError("error.import.profile", model);
			//}
		} else { 
			return needConfirm(project.isGeneric(), exists, model);
		}
	}
	
	private String handleProfile(User user, Model model) {
		Profile profile = (Profile)decoded;
		
		boolean exists = dataService.getProfileByUniqueId(profile.getUniqueId())!=null;
		if (!exists &! profile.isGeneric()) { // import directly
			profile.setWizardStep(1);
			profile.setPrepDate(new java.util.Date());
			profile.setTechnician(user);
			
			try {
				dataService.storeProfile(profile, false);
				attachTools.saveAttachedFilesFromZip(profile, containingFile);
				clearFormData();
				return "redirect:step1/"+profile.getProfileId();
			} catch (Exception e) {
				return uploadError("An error occurred when saving profile. "+e.getMessage(), model);
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
		//dataService.addOrgLogo(logoBytes, rcNew.getSetting());
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