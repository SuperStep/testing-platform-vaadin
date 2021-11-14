package com.sust.testing.platform.ui.view.login;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import de.mekaso.vaadin.addon.compani.AnimatedComponent;
import de.mekaso.vaadin.addon.compani.Animator;
import de.mekaso.vaadin.addon.compani.animation.AnimationBuilder;
import de.mekaso.vaadin.addon.compani.animation.AnimationTypes;
import de.mekaso.vaadin.addon.compani.effect.EntranceEffect;

@Route("login")
@PageTitle("Login | Testing platform")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

  private LoginForm login = new LoginForm();
  final Animator animator = Animator.init(UI.getCurrent());

  public LoginView(){

    LoginI18n i18n = LoginI18n.createDefault();
    i18n.setHeader(new LoginI18n.Header());
    i18n.getHeader().setTitle("###Testing platform###");
    i18n.getHeader().setDescription(
            "admin@vaadin.com + admin\n" + "barista@vaadin.com + barista");
    i18n.setAdditionalInformation(null);
    i18n.setForm(new LoginI18n.Form());
    i18n.getForm().setSubmit(getTranslation("login.signin"));
    i18n.getForm().setTitle(getTranslation("login.signin"));
    i18n.getForm().setUsername(getTranslation("login.name"));
    i18n.getForm().setPassword(getTranslation("login.password"));
    login.setI18n(i18n);
    login.setForgotPasswordButtonVisible(false);
    login.setAction("login");
    addClassName("login-view");
    setSizeFull();
    setAlignItems(Alignment.CENTER);
    setJustifyContentMode(JustifyContentMode.CENTER);

    add(new H1(getTranslation("app.name")), login, new RouterLink(getTranslation("app.register"), RegistrationView.class));
  }

  @Override
  public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
    // inform the user about an authentication error
    if(beforeEnterEvent.getLocation()
            .getQueryParameters()
            .getParameters()
            .containsKey("error")) {
      login.setError(true);
    }
  }


}