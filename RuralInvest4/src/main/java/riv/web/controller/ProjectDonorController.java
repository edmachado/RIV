package riv.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import riv.objects.config.User;
import riv.objects.project.Donor;
import riv.objects.project.Project;
import riv.util.validators.DonorValidator;
import riv.web.service.DataService;
 
@Controller
@RequestMapping({"/project/donor"})
public class ProjectDonorController {
	@Autowired
    private DataService dataService;
//	@Autowired
//	private RivConfig rivConfig;
	@Autowired
	MessageSource messageSource;
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(new DonorValidator());
	}
	
	@ModelAttribute("donor")
	public Donor getItem(@PathVariable Integer id, @RequestParam(required=false) String type, @RequestParam(required=false) Integer projectId)  {
		Donor d;
		if (id != -1) { d= dataService.getDonor(id); }
		else {
			Project p = dataService.getProject(projectId, 2);
			d = new Donor();
			d.setOrderBy(p.getDonors().size());
			d.setProject(p);
		}
		return d; 
	}
    
    @RequestMapping(value="/{id}/delete", method=RequestMethod.GET)
    public @ResponseBody String delete(@ModelAttribute Donor donor, HttpServletRequest request) {
    	User u = (User)request.getAttribute("user");
    	Boolean accessOK = donor.getProject().isShared() || donor.getProject().getTechnician().getUserId().equals(u.getUserId());
    	List<Integer> donorsUsed = dataService.donorsUsed(donor.getProject().getProjectId());
    	if (accessOK && !donorsUsed.contains(donor.getOrderBy())) {
	    	dataService.deleteDonor(donor);
	    	return "{\"success\": \"success\"}";
    	} else {
    		return null;
    	}
    }
    
    @RequestMapping(value="/{id}", method=RequestMethod.POST)
	public @ResponseBody String saveDonor(@Valid @ModelAttribute Donor donor, BindingResult result, @RequestParam(required=false) Integer projectId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
    	User u = (User)request.getAttribute("user");
    	Boolean accessOK = donor.getProject().isShared() || donor.getProject().getTechnician().getUserId().equals(u.getUserId());
    	
    	if (!accessOK) {
    		return null;
    	} else if (result.hasErrors()) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			StringBuilder sb = new StringBuilder("[");
			for (FieldError error : result.getFieldErrors()) {
				String message = messageSource.getMessage(error.getCode(), null, LocaleContextHolder.getLocale());
				sb.append("{\"field\":\"donor-"+error.getField()+"\",\"message\":\""+ message +"\"},");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append("]");
			response.getWriter().write(sb.toString());
			response.flushBuffer();
    		return null;
		} else {
			if (donor.getDonorId()==null)  {
				Project p = dataService.getProject(projectId, 2);
				donor.setProject(p);
				p.addDonor(donor);
			}
			dataService.storeDonor(donor);
			return "{\"success\": \"success\"}";
		}
    }
}