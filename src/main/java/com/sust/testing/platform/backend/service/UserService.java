package com.sust.testing.platform.backend.service;

import com.sust.testing.platform.backend.entity.Role;
import com.sust.testing.platform.backend.entity.User;
import com.sust.testing.platform.backend.entity.VerificationToken;
import com.sust.testing.platform.backend.events.OnRegistrationCompleteEvent;
import com.sust.testing.platform.backend.repository.TokensRepository;
import com.sust.testing.platform.backend.repository.UserRepository;
import com.sust.testing.platform.security.SecurityUtils;
import com.vaadin.flow.component.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Implements the {@link UserDetailsService}.
 * 
 * This implementation searches for {@link User} entities by the e-mail address
 * supplied in the login screen.
 */
@Service
@Primary
public class UserService implements UserDetailsService {

	private static final String HOST_URL = "http://130.61.153.141";
	private final UserRepository userRepository;
	private final TokensRepository tokensRepository;
	private final PasswordEncoder passwordEncoder;
	private final ApplicationEventPublisher eventPublisher;

	@Autowired
	public UserService(UserRepository userRepository,
					   TokensRepository tokensRepository,
					   PasswordEncoder passwordEncoder,
					   ApplicationEventPublisher eventPublisher) {
		this.userRepository = userRepository;
		this.tokensRepository = tokensRepository;
		this.passwordEncoder = passwordEncoder;
		this.eventPublisher = eventPublisher;
	}

	/**
	 *
	 * Recovers the {@link User} from the database using the e-mail address supplied
	 * in the login screen. If the user is found, returns a
	 * {@link org.springframework.security.core.userdetails.User}.
	 *
	 * @param username User's e-mail address
	 * 
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmailIgnoreCase(username);
		if (null == user) {
			throw new UsernameNotFoundException("No user present with username: " + username);
		} else {
			return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPasswordHash(),
					Collections.singletonList(new SimpleGrantedAuthority(user.getRole())));
		}
	}

	public Boolean userExists(String email) {
		return userRepository.findByEmailIgnoreCase(email) != null;
	}

	public void register(String userEmail, String password){

		String passwordHash = passwordEncoder.encode(password);
		createUser(userEmail, "", "", passwordHash, Role.ADMIN, false);
	}

	public User createUser(String email, String firstName, String lastName, String passwordHash, String role,
							boolean locked) {
		User user = new User();
		user.setEmail(email);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setPasswordHash(passwordHash);
		user.setRole(role);
		user.setLocked(locked);
		userRepository.save(user);

		// Event for sending confirmation
		eventPublisher.publishEvent(
				new OnRegistrationCompleteEvent(user,
						UI.getCurrent().getLocale(),
						HOST_URL));

		return user;
	}

	public List<User> findAll(){
		return userRepository.findAll();
	}

	public User save(User user) {
		userRepository.save(user);
		return user;
	}

	public void delete(User user) {
		userRepository.delete(user);
	}

	public void createVerificationToken(User user, String token) {
		tokensRepository.save(new VerificationToken(user, token));
	}

	public User getCurrent() {
		return userRepository.findByEmailIgnoreCase(SecurityUtils.getUsername());
	}

	public VerificationToken getVerificationToken(String token) {
		return tokensRepository.findByToken(token);
	}
}