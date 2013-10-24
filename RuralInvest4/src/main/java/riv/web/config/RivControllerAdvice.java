package riv.web.config;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class RivControllerAdvice {
	static final Logger LOG = LoggerFactory.getLogger(RivControllerAdvice.class);
	
	@Autowired
	RivConfig rivConfig;
	
	@ExceptionHandler(Exception.class)
	public ModelAndView handleException(HttpServletRequest request, Exception ex) {
		LOG.error("Exception during running application.",ex);
		LOG.error("Request URL: "+request.getRequestURL().toString());
		return errorModelAndView(ex);
	}
	
	private ModelAndView errorModelAndView(Exception ex) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("error");
		return modelAndView;
	}
	
  /*@InitBinder
  public void registerCustomEditors(WebDataBinder binder, WebRequest request) {
	  binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	  binder.registerCustomEditor(Double.class, new CustomNumberEditor(Double.class, true));
	  // binder.registerCustomEditor(BigDecimal.class, new  CustomNumberEditor(BigDecimal.class, NumberFormat.getNumberInstance(new Locale("pt", "BR"), true));
  }*/
}