package com.sust.testing.platform.app.mail;

import com.amazonaws.auth.PropertiesFileCredentialsProvider;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsSesConfiguration {

    private final String region;

    public AwsSesConfiguration(@Value("${email.region}") String region) {
        this.region = region;
    }

    /**
     * Build the AWS ses client
     *
     * @return AmazonSimpleEmailServiceClientBuilder
     */
    @Bean
    public AmazonSimpleEmailService amazonSimpleEmailService() {
        return AmazonSimpleEmailServiceClientBuilder.standard()
                .withCredentials(new PropertiesFileCredentialsProvider("src/main/resources/credentials.properties"))
                .withRegion(region).build();
    }
}