package de.edu.pamp.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author Lukas Konfiguration von Spring Security
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private DataSource dataSource;

	/**
	 * Konfiguration der Login Funktion
	 *
	 * @param auth Manager der die Authentifizierung vornimmt
	 * @throws Exception Fehler
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().dataSource(dataSource)
				.usersByUsernameQuery("select email, password, enabled from benutzer where email=?")
				.authoritiesByUsernameQuery(
						"select user_email as principal, role_name as role from user_roles where user_email=?")
				.passwordEncoder(passwordEncoder()).rolePrefix("ROLE_");

	}

	/**
	 * Konfiguration welche Seiten vor Authentifizierung aufgerufen werden können,
	 * setzen der Login und Logout Seiten
	 *
	 * @param http Kontext der HttpSecurity
	 * @throws Exception Fehler
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests()
				.antMatchers("/register", "/", "/index", "/about", "/login", "/css/**", "/img/**", "/webjars/**",
						"/registrationConfirm.html", "/impressum", "/resend", "/reset", "/resetPassword.html",
						"/resetpassword", "/agbs", "/contacts")
				.permitAll().anyRequest().authenticated().and().formLogin().loginPage("/login").permitAll()
				.defaultSuccessUrl("/dashboard", true).and().logout().logoutSuccessUrl("/").and().csrf().disable();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
