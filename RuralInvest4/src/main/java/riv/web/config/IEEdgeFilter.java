package riv.web.config;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet Filter implementation class IEEdgeFilter.
 * Overrides IE compatibility mode
 */
@WebFilter("/IEEdgeFilter")
public class IEEdgeFilter implements Filter {

    /**
     * Default constructor. 
     */
    public IEEdgeFilter() {
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		//Set the IE=Edge header which makes Internet Explorer recognise the latest version of IE:
		HttpServletResponse resp = (HttpServletResponse)response;
		resp.setHeader("X-UA-Compatible","IE=Edge");
		
		// pass the request along the filter chain
		chain.doFilter(request, resp);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
	}

}
