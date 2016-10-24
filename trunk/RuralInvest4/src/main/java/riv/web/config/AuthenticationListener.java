package riv.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import riv.web.service.DataService;

@Service
public class AuthenticationListener implements ApplicationListener<AuthenticationSuccessEvent> {
	@Autowired
	DataService dataService;
	
	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent event) {
		UserDetails user = ((UserDetails)event.getAuthentication().getPrincipal());
		String username = user.getUsername();
		dataService.updateLogin(username);
	}

}
