package com.sust.testing.platform.security;

import com.vaadin.flow.server.ServletHelper;
import com.vaadin.flow.shared.ApplicationConstants;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public final class SecurityUtils {
  private SecurityUtils() {
    // Util methods only
  }

  /**
   * Gets the user name of the currently signed in user.
   *
   * @return the user name of the current user or <code>null</code> if the user
   *         has not signed in
   */
  public static String getUsername() {
    SecurityContext context = SecurityContextHolder.getContext();
    Object principal = context.getAuthentication().getPrincipal();
    if(principal instanceof UserDetails) {
      UserDetails userDetails = (UserDetails) context.getAuthentication().getPrincipal();
      return userDetails.getUsername();
    }
    // Anonymous or no authentication.
    return null;
  }

  static boolean isFrameworkInternalRequest(HttpServletRequest request) {

    final String parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);

    return parameterValue != null
            & Stream.of(ServletHelper.RequestType.values())
            .anyMatch(r -> r.getIdentifier().equals(parameterValue));
  }
  static boolean isUserLoggedIn() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null
            && !(authentication instanceof AnonymousAuthenticationToken)
            && authentication.isAuthenticated();
  }

  public static boolean isAccessGranted(Class<?> securedClass) {
    // Allow if no roles are required.
    Secured secured = AnnotationUtils.findAnnotation(securedClass, Secured.class);
    if (secured == null) {
      return true; // (1)
    }

    // lookup needed role in user roles
    List<String> allowedRoles = Arrays.asList(secured.value());
    Authentication userAuthentication = SecurityContextHolder.getContext().getAuthentication();
    return userAuthentication.getAuthorities().stream() // (2)
            .map(GrantedAuthority::getAuthority)
            .anyMatch(allowedRoles::contains);
  }
}