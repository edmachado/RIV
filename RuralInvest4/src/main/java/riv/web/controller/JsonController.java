package riv.web.controller;
 
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import riv.objects.project.Donor;
import riv.objects.project.Project;
import riv.web.service.DataService;

 
@Controller
@RequestMapping({"/"})
public class JsonController {
	static final Logger LOG = LoggerFactory.getLogger(JsonController.class);
	
	@Autowired
    private DataService dataService;
	
	@RequestMapping(value="/home/{id}/donors.json", method=RequestMethod.GET)
	public @ResponseBody Object getDonors(@PathVariable Integer id) {
		Project project = dataService.getProject(id, 2);
		List<Integer> donorsUsed = dataService.donorsUsed(id);
		Set<Donor> donors = project.getDonors();
		for (Donor d : donors) {
			if (donorsUsed.contains(d.getOrderBy())) {
				d.setInUse(true);
			}
		}
		return donors;
	}
}
	
	