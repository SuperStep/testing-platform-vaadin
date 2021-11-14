package com.sust.testing.platform;

import de.mekaso.vaadin.addon.compani.ResourceLoaderServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class, SecurityAutoConfiguration.class})
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ServletRegistrationBean registerComponentAnimations() {
        return new ServletRegistrationBean(new ResourceLoaderServlet(),
                "/frontend/compani/compani.js");
    }

}


