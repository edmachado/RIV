package riv.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.AccessDeniedHandler;

import riv.web.service.DataService;

@Configuration
@EnableWebSecurity
public class RivSecurityConfiguration extends WebSecurityConfigurerAdapter {
	@Autowired
    private DataService dataService;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		.antMatchers("/login*").permitAll()
		.antMatchers("/img/**").permitAll()
		.antMatchers("/styles/**").permitAll()
		.antMatchers("/scripts/**").permitAll()
		.antMatchers("/**").authenticated()
		.and().formLogin()
		
		.loginPage("/login")
		.defaultSuccessUrl("/home")
		.failureUrl("/login?error=true")
		.and()
		.logout().logoutUrl("/logout")
        .logoutSuccessUrl("/login")

        .and()
        .exceptionHandling().accessDeniedHandler(accessDeniedHandler());
	}

	@Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.userDetailsService(dataService).passwordEncoder(new ShaPasswordEncoder());
    }
	
	@Bean
	public AccessDeniedHandler accessDeniedHandler(){
	    return new Riv403Handler();
	}
}
