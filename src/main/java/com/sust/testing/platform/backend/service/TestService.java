package com.sust.testing.platform.backend.service;

import com.sust.testing.platform.backend.data.QuestionSet;
import com.sust.testing.platform.backend.entity.Question;
import com.sust.testing.platform.backend.entity.Test;
import com.sust.testing.platform.backend.entity.VectorPsychotype;
import com.sust.testing.platform.backend.repository.VectorRepository;
import com.sust.testing.platform.backend.entity.Influence;
import com.sust.testing.platform.backend.repository.TestRepository;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TestService {

  private static final Logger LOGGER = Logger.getLogger(TestService.class.getName());

  private TestRepository testRepository;
  private QuestionService questionService;
  private VectorRepository vectorRepository;
  private final Random rand = new Random();

  public TestService(TestRepository testRepository,
                     QuestionService questionService,
                     VectorRepository vectorRepository) {
    this.testRepository = testRepository;
    this.questionService = questionService;
    this.vectorRepository = vectorRepository;
  }

  public List<Test> findAll() {
    return testRepository.findAll();
  }

  public void delete(Test test) {
    testRepository.delete(test);
  }

  public void save(Test test) {
    if (test == null) {
      LOGGER.log(Level.SEVERE,
              "Contact is null. Are you sure you have connected your form to the application?");
      return;
    }
    testRepository.save(test);
  }

  public void persistData(List<QuestionSet> data) {

        List<Test> tests = findAll();
        List<VectorPsychotype> vectors = vectorRepository.findAll();

        for (QuestionSet questionSet: data) {
            Test currentTest = tests.stream()
                    .filter(test -> test.getName().equals(questionSet.getTestName()))
                    .findFirst()
                    .orElse(new Test(questionSet.getTestName()));
            testRepository.save(currentTest);
            tests.add(currentTest);

            List<Question> questions = currentTest.getQuestions();

            Question currentQuestion = questions.stream()
                    .filter(qst -> qst.getText().equals(questionSet.getQuestionText()))
                    .findFirst().orElse(new Question());
            currentQuestion.setTest(currentTest);
            currentQuestion.setText(questionSet.getQuestionText());
            currentQuestion.setPositionInTest(questionSet.getPosition());
            questionService.save(currentQuestion);
            questions.add(currentQuestion);

            Map<String, Integer> influenceMap = questionSet.getInfluenceMap();
            List<Influence> influenceList = new ArrayList<>();

            for (Map.Entry<String, Integer> inf: influenceMap.entrySet()) {
                VectorPsychotype vector = vectors.stream()
                        .filter(vec -> vec.getName().equals(inf.getKey()))
                        .findFirst().orElse(new VectorPsychotype(inf.getKey()));
                vectorRepository.save(vector);
                vectors.add(vector);

                Influence.Answer answer;
                if (questionSet.getAnswer().equals("Yes")) {
                    answer = Influence.Answer.Yes;
                } else {
                    answer = Influence.Answer.No;
                }

                Influence influence = new Influence(answer, inf.getValue(),
                        currentQuestion, vector);
                influenceList.add(influence);

            }

            questionService.saveInfluenceList(influenceList, currentQuestion);

        }
    }
}