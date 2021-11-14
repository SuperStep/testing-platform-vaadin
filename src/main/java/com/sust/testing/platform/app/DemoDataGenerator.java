package com.sust.testing.platform.app;

import javax.annotation.PostConstruct;

import com.sust.testing.platform.backend.entity.*;
import com.sust.testing.platform.backend.repository.TestRepository;
import com.sust.testing.platform.backend.repository.UserRepository;
import com.sust.testing.platform.backend.repository.VectorRepository;
import com.sust.testing.platform.backend.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vaadin.flow.spring.annotation.SpringComponent;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@SpringComponent
public class DemoDataGenerator implements HasLogger {

	private final UserRepository userRepository;
	private final TestRepository testRepository;
	private final VectorRepository vectorRepository;
	private final QuestionService questionService;
	private final Random rand = new Random();

	@Autowired
	private PasswordEncoder passwordEncoder;
	/**
	 * The password encoder to use when encrypting passwords.
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	@Autowired
	public DemoDataGenerator(UserRepository userRepository, TestRepository testRepository, VectorRepository vectorRepository, QuestionService questionService) {
		this.userRepository = userRepository;
		this.testRepository = testRepository;
		this.vectorRepository = vectorRepository;
		this.questionService = questionService;
	}

	@PostConstruct
	public void loadDemoData() {
		if (userRepository.count() != 0L) {
			getLogger().info("Using existing database");
			return;
		}

		getLogger().info("... generating users");
		createAdmin(userRepository);
		getLogger().info("... generating example tests");
		generateTests();
	}

	@DependsOn("VectorService")
	@PostConstruct
	public void generateTests() {
		if (testRepository.count() == 0) {
			testRepository.saveAll(
					Stream.of("First test", "Second test", "Last test")
					.map(Test::new)
					.collect(Collectors.toList()));

			if (vectorRepository.count() == 0) {
				vectorRepository.saveAll(
						Stream.of("Psychological type 1", "Psychological type 2", "Psychological type 3")
								.map(VectorPsychotype::new)
								.collect(Collectors.toList()));
			}

			List<VectorPsychotype> vectors = vectorRepository.findAll();
			List<Test> tests = testRepository.findAll();

			Map<Integer, String> questions = getQuestions();
			for (Test test: tests) {
				for (int i = 1; i < 8; i++) {
					generateQuestion(questions.get(i),
							i, test, vectors);
				}
			}
		}
	}

	private static Map<Integer, String> getQuestions() {
		Map<Integer, String> questionsMap = new HashMap<>();
		questionsMap.put(0, "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.");
		questionsMap.put(1, "Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt.");
		questionsMap.put(2, "adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.");
		questionsMap.put(3, "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
		questionsMap.put(4, "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium.");
		questionsMap.put(5, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
		questionsMap.put(6, "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium.");
		questionsMap.put(7, "Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur.");
		return questionsMap;
	}

	private void generateQuestion(String text, int position, Test test,
								  List<VectorPsychotype> vectors) {

		Question question = new Question();
		question.setTest(test);
		question.setText(text);
		question.setPositionInTest(position);
		questionService.save(question);
		List<Influence> influenceList = new ArrayList<>();
		for (int i = 0; i < rand.nextInt(4) + 1 ; i++) {
			influenceList.add(
					new Influence(getRandomAnswer(),
							getRandomIntValue(),
							question,
							getRandomVector(vectors)
					)
			);
		}
		questionService.saveInfluenceList(influenceList, question);

	}

	private int getRandomIntValue() {
		return rand.nextInt(6) - 1;
	}

	private Influence.Answer getRandomAnswer(){

		int intAnsw = rand.nextInt(2);
		if (intAnsw == 0) {
			return Influence.Answer.Yes;
		} else {
			return Influence.Answer.No;
		}
	}

	private VectorPsychotype getRandomVector(List<VectorPsychotype> vectors) {
		return vectors.get(rand.nextInt(2));
	}


	private User createAdmin(UserRepository userRepository) {
		return userRepository.save(
				createUser("admin@admin.com",
						"Admin",
						"Admin",
						passwordEncoder.encode("admin"),
						Role.ADMIN,
						true));
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
		return user;
	}
}
