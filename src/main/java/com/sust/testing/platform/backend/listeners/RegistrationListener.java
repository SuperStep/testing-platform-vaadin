package com.sust.testing.platform.backend.listeners;

import com.sust.testing.platform.app.mail.AwsSesService;
import com.sust.testing.platform.backend.entity.User;
import com.sust.testing.platform.backend.events.OnRegistrationCompleteEvent;
import com.sust.testing.platform.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationListener implements
        ApplicationListener<OnRegistrationCompleteEvent> {
 
    @Autowired
    private UserService userService;
 
    @Autowired
    private MessageSource messages;
 
    @Autowired
    private final AwsSesService awsSesService;

    public RegistrationListener(UserService userService, MessageSource messages, AwsSesService awsSesService) {
        this.userService = userService;
        this.messages = messages;
        this.awsSesService = awsSesService;
    }



    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {

        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user, token);

        String sb = "Registration confirmation" +
                System.getProperty("line.separator") +
                "https://" + event.getAppUrl() + "/regitrationConfirm?token=" + token;
        awsSesService.sendEmail(user.getEmail(), sb);
    }
}