package riv.web.config;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import riv.objects.config.User;

@Component
public class RivLocaleResolver implements LocaleResolver {
	@Autowired
	private RivConfig rivConfig;
	
	// priorities for determining locale: 0. pageLang request attribute 1. user 2. settings 3. english (default)
	public Locale resolveLocale(HttpServletRequest request) {
		if (request.getAttribute("pageLocale")==null) {
			String lang=null;
			if (request.getAttribute("pageLang")!=null) {
				lang = (String)request.getAttribute("pageLang");
			} else if (request.getAttribute("user")!=null && ((User)request.getAttribute("user")).getLang()!=null) {
				lang=((User)request.getAttribute("user")).getLang();
			} else if (rivConfig.getSetting()!=null && rivConfig.getSetting().getLang()!=null) {
				lang = rivConfig.getSetting().getLang();
			} else {
				lang="en";
			}
			request.setAttribute("pageLocale", getLocaleFromLangString(lang));
			request.setAttribute("lang", lang);
		}
		return (Locale)request.getAttribute("pageLocale");
	}
	
	private Locale getLocaleFromLangString(String lang) {
		if (lang.contains("_")) {
			String[] langRegion = lang.split("_");
			return new Locale(langRegion[0],langRegion[1]);
		} else {
			return new Locale(lang);
		}
	}

	public void setLocale(HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		// ignore
	}
}
