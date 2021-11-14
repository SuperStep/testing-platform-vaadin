package com.sust.testing.platform.security;

import com.sust.testing.platform.backend.entity.Role;
import com.sust.testing.platform.backend.entity.User;
import com.sust.testing.platform.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
  private static final String LOGIN_PROCESSING_URL = "/login";
  private static final String LOGIN_FAILURE_URL = "/login?error";
  private static final String LOGIN_URL = "/login";
  private static final String LOGOUT_SUCCESS_URL = "/login";

  @Autowired
  private final UserDetailsService userDetailsService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  public SecurityConfiguration(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
    this.userDetailsService = userDetailsService;
    this.passwordEncoder = passwordEncoder;
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public CurrentUser currentUser(UserRepository userRepository) {
    final String username = SecurityUtils.getUsername();
    User user =
            username != null ? userRepository.findByEmailIgnoreCase(username) :
                    null;
    return () -> user;
  }

  /**
   * Registers our UserDetailsService and the password encoder to be used on login attempts.
   */
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    super.configure(auth);
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
  }

  /**
   * Require login to access internal pages and configure login form.
   */
  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.csrf().disable()
            .requestCache().requestCache(new CustomRequestCache())
            .and().authorizeRequests()
            .antMatchers("/register/**", "/register**", "/register",
                    "/regitrationConfirm*", "/regitrationConfirm/**").permitAll()
            .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()
            .anyRequest().authenticated()
            .and().formLogin()
            .loginPage(LOGIN_URL).permitAll()
            .loginProcessingUrl(LOGIN_PROCESSING_URL)
            .failureUrl(LOGIN_FAILURE_URL)
            .and().logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL);
  }

  /**
   * Allows access to static resources, bypassing Spring security.
   */
  @Override
  public void configure(WebSecurity web) {
    web.ignoring().antMatchers(
            "/VAADIN/**",
            "/favicon.ico",
            "/robots.txt",
            "/manifest.webmanifest",
            "/sw.js",
            "/offline.html",
            "/icons/**",
            "/images/**",
            "/styles/**",
            "/h2-console/**",
            "/register/**",
            "/frontend/**");
  }
}

