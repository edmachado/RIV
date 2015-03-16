package riv.web.controller;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import riv.objects.FilterCriteria;
import riv.objects.project.Project;
import riv.web.config.RivConfig;
import riv.web.config.RivLocaleResolver;
import riv.web.service.AttachTools;
import riv.web.service.DataService;

 
@Controller
@RequestMapping({"/"})
public class JsonController {
	static final Logger LOG = LoggerFactory.getLogger(JsonController.class);
	
	@Autowired
    private DataService dataService;
	@Autowired
    private RivConfig rivConfig;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private RivLocaleResolver localeResolver;
	@Autowired
	FilterCriteria filter;
	@Autowired
	AttachTools attachTools;
	
	@RequestMapping(value="/home/{id}/donors.json", method=RequestMethod.GET)
	public @ResponseBody Object getDonors(@PathVariable Integer id) {
		Project project = dataService.getProject(id, 2);
		return project.getDonors();
	}
	
	@RequestMapping(value="/home/test", method=RequestMethod.GET)
	public String test() {
		return "test";
	}

}
	
	