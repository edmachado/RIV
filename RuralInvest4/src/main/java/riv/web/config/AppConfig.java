package riv.web.config;

import java.util.Arrays;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@EnableWebMvc
@ComponentScan(basePackages={"riv"})
@Import({RepositoryConfig.class, RivSecurityConfiguration.class})
//@ImportResource({"classpath:springapp-security.xml"})
@EnableCaching
@EnableWebSecurity
@Configuration
public class AppConfig extends WebMvcConfigurerAdapter {
	@Autowired
	SessionFactory sessionFactory;
	
	 @Override
	    public void addResourceHandlers(ResourceHandlerRegistry registry) {
	    registry.addResourceHandler("/styles/**").addResourceLocations("/styles/").setCachePeriod(31556926);
	    registry.addResourceHandler("/img/**").addResourceLocations("/img/").setCachePeriod(31556926);
	    registry.addResourceHandler("/scripts/**").addResourceLocations("/scripts/").setCachePeriod(31556926);
	    registry.addResourceHandler("/docs/**").addResourceLocations("/docs/").setCachePeriod(31556926);
		 
	 }
	 
	 @Bean
	 public LocaleResolver localeResolver() {
		 return new RivLocaleResolver();
	 }
	 
	 @Bean
	 public CommonsMultipartResolver filterMultipartResolver(){
		 CommonsMultipartResolver mr = new CommonsMultipartResolver();
		 mr.setMaxUploadSize(5242880);
		 return mr;
	 }

	 @Bean(name="cacheManager")
	 public SimpleCacheManager cacheManager() {
		 SimpleCacheManager simpleCache = new SimpleCacheManager();
		 simpleCache.setCaches(Arrays.asList(
				 new ConcurrentMapCache("jrTemplates")
		));
		 return simpleCache;
	 }
	 
	 @Bean(name = "validator")
	 public LocalValidatorFactoryBean validator() {
	     LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
	     bean.setValidationMessageSource(messageSource());
	     return bean;
	 }
	  
	 @Override
	 public Validator getValidator() {
	     return validator();
	 }
	 
	@Bean
	 public RivInjectionFilter rivInjectFilter() {
		return new RivInjectionFilter();
	 }
	   
	 @Override
	 public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
	    configurer.enable();
	 }
	 
	 @Bean
	 public InternalResourceViewResolver getInternalResourceViewResolver() {
	    InternalResourceViewResolver resolver = new InternalResourceViewResolver();
	    resolver.setPrefix("/WEB-INF/jsp/views/");
	    resolver.setSuffix(".jsp");
	    return resolver;
	 }
	 
	 @Bean
	 public static PropertyPlaceholderConfigurer getPropertyPlaceholderConfigurer() {
		 PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
		 ppc.setLocation(new ClassPathResource("application.properties"));
		 ppc.setIgnoreUnresolvablePlaceholders(true);
		 return ppc;
	 }
	 
	 @Bean
	 public MessageSource messageSource() {
	        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
	        messageSource.setBasenames("/WEB-INF/messages/messages");
	        // if true, the key of the message will be displayed if the key is not
	        // found, instead of throwing a NoSuchMessageException
	        //messageSource.setUseCodeAsDefaultMessage(true);
	        messageSource.setDefaultEncoding("UTF-8");
	        // # -1 : never reload, 0 always reload
	        messageSource.setCacheSeconds(-1);
	        return messageSource;
	  }
	 
	 
}
