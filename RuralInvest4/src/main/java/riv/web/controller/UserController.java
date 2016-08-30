package riv.web.controller;
 
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import riv.objects.config.User;
import riv.util.validators.UserValidator;
import riv.web.config.RivConfig;
import riv.web.service.DataService;

/**
 * @author barzecharya
 *
 */
@Controller
@RequestMapping({"/config"})
public class UserController {
	static final Logger LOG = LoggerFactory.getLogger(UserController.class);
	@Autowired
    private DataService dataService;
	@Autowired
	private RivConfig rivConfig;
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(new UserValidator());
	}
	
	@ModelAttribute("user")
	public User getItem(@PathVariable Integer id, HttpServletRequest request) throws Exception {
		// @ModelAttribute needs name equal to the class, 
		// so we need to move the request.user (current user)
		// to a new request attribute (currentUser) 
		// and replace user with the model attribute
		User currentUser = (User)request.getAttribute("user");
		request.setAttribute("currentUser", currentUser);
		
		User u=null;
		if (id==-1) {	 // add user
			u = new User();
		} else { // edit/view existing user
			u = dataService.getUser(id);
		}
		
		// if user doesn't have language set, use settings default
		if (u.getLang()==null) { u.setLang(rivConfig.getSetting().getLang()); }
		return u;
	}
	
	@RequestMapping(value="/user/{id}", method=RequestMethod.GET)
	public String getUser(@PathVariable Integer id, @RequestParam(required=false) String changePassword,  Model model, HttpServletRequest request) {	
		User user = (User)request.getAttribute("user");
		model.addAttribute("accessOK", id==-1 || (isCurrentUser(id, request) && (!rivConfig.isDemo() || user.getUserId()>18)));
		return changePassword==null ? "config/user" : "config/userPassword";
	}
	
	@RequestMapping(value="/indicators/{id}", method=RequestMethod.GET)
	public String indicators(@PathVariable Integer id, Model model, HttpServletRequest request) {
		model.addAttribute("accessOK", isCurrentUser(id, request));
		return "config/indicators";
	}
	
	// indicators.jsp also uses User object
	@RequestMapping(value="indicators/{id}", method=RequestMethod.POST)
	public String saveIndicators(@ModelAttribute User user, HttpServletRequest request) {
		if (isCurrentUser(user.getUserId(), request)) {
			dataService.storeUser(user);
			reloadCurrentUser(user);
		}
		return "redirect:../indicators/"+user.getUserId();
	}
	
	/**
	 Saved data from user.jsp and userPassword.jsp since all come from User object
	 */
	@RequestMapping(value="/user/{id}", method=RequestMethod.POST)
	public String updateUser(@Valid @ModelAttribute User user, BindingResult result, Model model,
			@PathVariable Integer id, @RequestParam(required=false) String changePassword, HttpServletRequest request) {
		// check permissions
		User currentUser = (User)request.getAttribute("currentUser");
		if (id==-1 && !currentUser.isAdministrator()) { return "redirect:../user"; } // must be admin to add user
		if (id!=-1 && !isCurrentUser(id, request)) { return "redirect:../user"; } // cannot change another user
		
		// check that password is repeated correctly
		if ((changePassword!=null || id==-1) &!user.getPassword().equals(request.getParameter("passwordRepeat"))) {
			result.rejectValue("password", "user.repeatedPassword");
		}
		// check that username is unique
		if (id==-1 && dataService.getUserByUsername(user.getUsername())!=null) {
			result.rejectValue("description", "error.user.usernameTaken");
		}
		
		if (result.hasErrors()) {
			model.addAttribute("accessOK", id==-1 || isCurrentUser(user.getUserId(), request));
			return changePassword==null ? "config/user" : "config/userPassword";
		} else {
			if (request.getParameter("passwordRepeat")!=null || changePassword!=null) { // encrypt password
				ShaPasswordEncoder encoder = new ShaPasswordEncoder();
				String encodedPass = encoder.encodePassword(user.getPassword(), null);
				user.setPassword(encodedPass);
			}
			
			dataService.storeUser(user);
			
			if (isCurrentUser(id, request)) { reloadCurrentUser(user); }
			return "redirect:../user";
		}
	}
	
	private boolean isCurrentUser(int userId, HttpServletRequest request) {
		return userId==((User)request.getAttribute("currentUser")).getUserId();
	}
	// tell spring security to update copy of currently logged-in user
	private void reloadCurrentUser(User user) {
		Authentication authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
	}
	
	@RequestMapping(value="/user/{id}/delete", method=RequestMethod.GET)
	public String delete(@PathVariable Integer id, @ModelAttribute User user, Model model, HttpServletRequest request) {
		User currentUser = (User)request.getAttribute("currentUser");
		if (!(isCurrentUser(id, request) || !currentUser.isAdministrator())) {
				dataService.deleteUser(user);
		} else {
			System.out.println("Access denied deleting user.");
		}
		return "redirect:../../user";
	}	
}