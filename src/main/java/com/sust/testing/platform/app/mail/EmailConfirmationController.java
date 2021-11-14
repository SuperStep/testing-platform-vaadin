package com.sust.testing.platform.app.mail;

import com.sust.testing.platform.backend.entity.User;
import com.sust.testing.platform.backend.entity.VerificationToken;
import com.sust.testing.platform.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import java.util.Calendar;
import java.util.Locale;

@Controller
public class EmailConfirmationController {

    @Autowired
    private UserService userService;

    @GetMapping("/regitrationConfirm")
    public String confirmRegistration
            (WebRequest request, Model model, @RequestParam("token") String token) {

        Locale locale = request.getLocale();

        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
        }

        user.setActivated(true);
        userService.save(user);
        return "redirect:/login";
    }
}
