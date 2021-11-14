package com.sust.testing.platform.ui.view.login;

import com.sust.testing.platform.backend.service.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("register")
@PageTitle("Register | Testing platform")
public class RegistrationView extends Composite {

    private final UserService userDetailsService;

    public RegistrationView(UserService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected Component initContent() {
        EmailField emailField = new EmailField(getTranslation("login.email"));
        emailField.setAutocomplete(Autocomplete.EMAIL);
        PasswordField passwordField1 = new PasswordField(getTranslation("login.password"));
        passwordField1.setAutocomplete(Autocomplete.CURRENT_PASSWORD);
        PasswordField passwordField2 = new PasswordField(getTranslation("login.secondpassword"));

        VerticalLayout registerLayout =  new VerticalLayout(
                new H1(getTranslation("app.name")),
                new H2(getTranslation("app.register")),
                emailField,
                passwordField1,
                passwordField2,
                new Button(getTranslation("app.register"), event -> register(
                        emailField.getValue(),
                        passwordField1.getValue(),
                        passwordField2.getValue()
                ))
        );

        registerLayout.setSizeFull();
        registerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        registerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        return registerLayout;
    }

    private void register(String username, String password1, String password2) {
        if (username.trim().isEmpty()) {
            Notification.show("Enter email");
        } else if (password1.isEmpty()) {
            Notification.show("Enter password");
        } else if (!password1.equals(password2)) {
            Notification.show("Passwords don't match");
        } else {
            if (!userDetailsService.userExists(username)){
                userDetailsService.register(username, password1);
                Notification.show(
                        getTranslation("app.register.success.message"),
                        10000,
                        Notification.Position.MIDDLE);
                UI.getCurrent().navigate("login");
            } else {
                Notification.show("User already exists");
            }

        }

    }

}
