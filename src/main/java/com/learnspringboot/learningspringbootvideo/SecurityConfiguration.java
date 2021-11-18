package com.learnspringboot.learningspringbootvideo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * @author Victor
 * Date 18/11/2021 09:35
 * @project learning-spring-boot-video
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers(HttpMethod.GET, "/", "/images/**", "/main.css", "/webjars/**").permitAll().
                antMatchers(HttpMethod.POST, "/images").hasRole("USER").antMatchers("/imageMessages/**").permitAll().
                and().formLogin().permitAll().and().logout().logoutSuccessUrl("/");
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, SpringDataUserDetailsService springDataUserDetailsService) throws Exception {
        /**auth.inMemoryAuthentication()
                .withUser("bleau83").password("{noop}bleau83").roles("ADMIN","USER")
                .and()
                .withUser("user").password("{noop}user").roles("USER");*/
        auth.userDetailsService(springDataUserDetailsService);
    }
}
