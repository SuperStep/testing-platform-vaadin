package com.sust.testing.platform.security;

import com.sust.testing.platform.ui.view.login.AccessDeniedException;
import com.sust.testing.platform.ui.view.login.LoginView;
import com.sust.testing.platform.ui.view.login.RegistrationView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class ConfigureUIServiceInitListener implements VaadinServiceInitListener {

  @Override
  public void serviceInit(ServiceInitEvent event) {
    event.getSource().addUIInitListener(uiEvent -> {
      final VaadinSession session = uiEvent.getUI().getSession();
      session.setLocale(new Locale("en"));
      final UI ui = uiEvent.getUI();
      ui.addBeforeEnterListener(this::authenticateNavigation);
      ui.addBeforeEnterListener(this::beforeEnter);
    });

  }
  private void authenticateNavigation(BeforeEnterEvent event) {
    if (!LoginView.class.equals(event.getNavigationTarget())
            && !RegistrationView.class.equals(event.getNavigationTarget())
            && !"regitrationConfirm".equals(event.getLocation().getPath())
            && !SecurityUtils.isUserLoggedIn()) {
        event.rerouteTo(LoginView.class);

    }
  }
  private void beforeEnter(BeforeEnterEvent event) {
    if(!SecurityUtils.isAccessGranted(event.getNavigationTarget())) { //
      if(SecurityUtils.isUserLoggedIn()) { //
        event.rerouteToError(AccessDeniedException.class);
      } else {
          event.rerouteTo(LoginView.class);
      }
    }
  }
}