package riv.web.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import riv.objects.config.User;


/**
 * @author Bar Zecharya
 * Injects authenticated user and rivConfig into requests for use by controllers and views.
 * In case there is no rivConfig.setting (first-use scenario), 
 * redirects requests to settings import page (or for admin version, settings page).
 */
@Component
public class RivInjectionFilter extends OncePerRequestFilter {
	@Autowired
	private RivConfig rivConfig;

	public RivInjectionFilter() {}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String pageRequested = request.getRequestURI();
		if (rivConfig.isComplete() || pageRequested.endsWith("/config/import") || (rivConfig.isAdmin() && pageRequested.endsWith("/config/settings"))) {
			User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			request.setAttribute("user", user);
			request.setAttribute("rivConfig", rivConfig);
		} else {
			if (rivConfig.isAdmin()) {
				response.sendRedirect(basePath(pageRequested)+"/config/settings");
			} else {
				response.sendRedirect(basePath(pageRequested)+"/config/import");
			}			
		}
		chain.doFilter(request, response);
	}
	
	private String basePath(String path) {
		int index = path.indexOf("/");
		index = path.indexOf("/",index+1);
		return path.substring(0, index);
	}
}