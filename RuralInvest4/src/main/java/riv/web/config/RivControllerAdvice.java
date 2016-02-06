package riv.web.config;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class RivControllerAdvice {
	static final Logger LOG = LoggerFactory.getLogger(RivControllerAdvice.class);
	
	@Autowired
	RivConfig rivConfig;
	
	@Value("${buildVersion}")	private String buildVersion;
	
//	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(Exception.class)
	public ModelAndView handleException(HttpServletRequest request, Exception ex) {
		request.setAttribute("version",buildVersion);
		LOG.error("RuralInvest version "+buildVersion);
		LOG.error("Exception during running application.",ex);
		LOG.error("Request URL: "+request.getRequestURL().toString());
		return errorModelAndView(ex);
	}
	
	private ModelAndView errorModelAndView(Exception ex) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("error");
		return modelAndView;
	}
}