package riv.web.controller;
 
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import riv.objects.FilterCriteria;
import riv.objects.config.AppConfig1;
import riv.objects.config.AppConfig2;
import riv.objects.config.Beneficiary;
import riv.objects.config.EnviroCategory;
import riv.objects.config.FieldOffice;
import riv.objects.config.ProjectCategory;
import riv.objects.config.Status;
import riv.objects.profile.ProfileResult;
import riv.objects.project.ProjectResult;
import riv.util.ExcelWrapper;
import riv.util.ReportWrapper;
import riv.web.config.RivConfig;
import riv.web.editors.AppConfigCollectionEditor;
import riv.web.editors.UserCollectionEditor;
import riv.web.service.DataService;
import riv.web.service.ExcelWorksheetBuilder;
import riv.web.service.Exporter;
import riv.web.service.PdfReportCreator;
 
@Controller
@RequestMapping("/search")
public class SearchController {
	@Autowired
    private DataService dataService;
	@Autowired
	private FilterCriteria filter;
	@Autowired
	private RivConfig rivConfig;
	@Autowired
	private PdfReportCreator reportCreator;
//	@Autowired
//	private MessageSource messageSource;
	@Autowired
	private Exporter exporter;
	@Autowired
	private ExcelWorksheetBuilder ewb;
	
    @InitBinder
    protected void initBinder(HttpServletRequest request, WebDataBinder binder) {
    	binder.registerCustomEditor(List.class, "offices", new AppConfigCollectionEditor(rivConfig, List.class, FieldOffice.class));
        binder.registerCustomEditor(List.class, "statuses", new AppConfigCollectionEditor(rivConfig, List.class, Status.class));
        binder.registerCustomEditor(List.class, "categories", new AppConfigCollectionEditor(rivConfig, List.class, ProjectCategory.class));
        binder.registerCustomEditor(List.class, "enviroCategories", new AppConfigCollectionEditor(rivConfig, List.class, EnviroCategory.class));
        binder.registerCustomEditor(List.class, "beneficiaries", new AppConfigCollectionEditor(rivConfig, List.class, Beneficiary.class));
        binder.registerCustomEditor(List.class, "appConfig1s", new AppConfigCollectionEditor(rivConfig, List.class, AppConfig1.class));
        binder.registerCustomEditor(List.class, "appConfig2s", new AppConfigCollectionEditor(rivConfig, List.class, AppConfig2.class));
        binder.registerCustomEditor(List.class, "users", new UserCollectionEditor(dataService, List.class));
    }
    
    @RequestMapping(value="/report/projectResults.xlsx", method=RequestMethod.GET)
    public void projectListXls(HttpServletResponse response) throws IOException {
    	List<ProjectResult> results = dataService.getProjectResults(filter);
    	ExcelWrapper report = ewb.create();
		//ExcelWorksheetBuilder ewb = new ExcelWorksheetBuilder(messageSource, rivConfig);
		ewb.projectResults(report, results, filter);
		response.setHeader("Content-disposition", "attachment; filename=projectResults.xls");
		report.getWorkbook().write(response.getOutputStream());
		report.getWorkbook().dispose();
    }
    
    @RequestMapping(value="/report/projectResults.pdf", method=RequestMethod.GET)
	public void projectList(HttpServletRequest request, HttpServletResponse response) {
		List<ProjectResult> results = dataService.getProjectResults(filter);
		ReportWrapper report = reportCreator.projectList(results, filter);
		reportCreator.export(response, report);
	}
    
    @RequestMapping(value="/report/profileResults.xlsx", method=RequestMethod.GET)
    public void profileListXls(HttpServletResponse response) throws IOException {
    	List<ProfileResult> results = dataService.getProfileResults(filter);
    	ExcelWrapper report = ewb.create();
		ewb.profileResults(report, results, filter);
		response.setHeader("Content-disposition", "attachment; filename=profileResults.xls");
		report.getWorkbook().write(response.getOutputStream());
		report.getWorkbook().dispose();
    }
	
	@RequestMapping(value="/report/profileResults.pdf", method=RequestMethod.GET)
	public void profileList(HttpServletRequest request, HttpServletResponse response) {
		List<ProfileResult> results = dataService.getProfileResults(filter);
		ReportWrapper report = reportCreator.profileList(results, filter);
		reportCreator.export(response, report);
	}
    
	@RequestMapping(value="/results/{type}Results.zip", method=RequestMethod.GET)
	public void batchDownload(@PathVariable String type, OutputStream out) {
		if (filter.isProfile()) {
			List<ProfileResult> results = dataService.getProfileResults(filter);
			exporter.batchExportProfiles(results, false, out);
		} else {
			List<ProjectResult> results = dataService.getProjectResults(filter);
			exporter.batchExportProjects(results, false, out);
		}
	}
	
	@SuppressWarnings("rawtypes")
	private List getResults() {
		List results=null;
    	
    	if (filter.isProfile()) {
    		if (filter.isUnfinished()) { results = dataService.getProfileUnfinished(filter.isIncomeGen()); }
    		else { results = dataService.getProfileResults(filter); }
    	} else {
    		if (filter.isUnfinished()) { results = dataService.getProjectUnfinished(filter.isIncomeGen()); }
    		else { results = dataService.getProjectResults(filter); }
    	}
    	return results;
	}
	
    @RequestMapping(value="/results", method=RequestMethod.GET)
    public String results(Model model) { 
    	if (filter==null) { 
    		filter=new FilterCriteria(); 
    	}
    	
    	model.addAttribute("results", getResults());
    	model.addAttribute("filter",filter);
    	String unfinished = filter.isUnfinished() ? "Unfinished" : "";
    	return filter.isProfile() ? "profile/list"+unfinished : "project/list"+unfinished;
    }
	
    @RequestMapping(value="/quick", method=RequestMethod.POST)
    public String quickSearch(@RequestParam String freeText, @RequestParam String objType, HttpServletRequest request) {
    	filter = new FilterCriteria();
    	filter.setFreeText(freeText);
    	filter.setObjType(objType);
    	filter.setUnfinished(request.getParameter("unfinished").equals("true"));
    	return "redirect:/search/results";
    }
    
    @RequestMapping(method=RequestMethod.GET)
    public String search(Model model, HttpServletRequest request) {
    	model.addAttribute("filterCriteria",filter);
    	model.addAttribute("users",dataService.getUsers());
    	return "search";
    }

    @RequestMapping(value="/new", method=RequestMethod.GET)
    public String searchNew(Model model, HttpServletRequest request) {
    	filter=new FilterCriteria();
    	return "redirect:/search";
    }
    
    @RequestMapping(method=RequestMethod.POST)
    public String processSearch(FilterCriteria filterCriteria) {
    	filter=filterCriteria;
    	return "redirect:search/results";
    }
}
