package com.sust.testing.platform.backend.service;

import com.sust.testing.platform.backend.entity.Influence;
import com.sust.testing.platform.backend.entity.Question;
import com.sust.testing.platform.backend.entity.Test;
import com.sust.testing.platform.backend.repository.InfluenceRepository;
import com.sust.testing.platform.backend.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class QuestionService {

  private static final Logger LOGGER = Logger.getLogger(QuestionService.class.getName());

  private QuestionRepository questionRepository;
  private InfluenceRepository influenceRepository;
  @PersistenceContext
  EntityManager em;

  public QuestionService(QuestionRepository questionRepository,
                         InfluenceRepository influenceRepository,
                         EntityManager em) {
    this.questionRepository = questionRepository;
    this.influenceRepository = influenceRepository;
    this.em = em;
  }

  public List<Question> findAll() {
    return questionRepository.findAll();
  }

  public void delete(Question question) {
    questionRepository.delete(question);
  }

  public void save(Question question) {
    if (question == null) {
      LOGGER.log(Level.SEVERE,
              "Question is null. Are you sure you have connected your form to the application?");
      return;
    }
    questionRepository.save(question);
    saveInfluenceList(question.getInfluenceList(), question);
  }

  public void saveInfluenceList(List<Influence> influenceList, Question question) {
    for (Influence influence:
            influenceList) {
      influence.setQuestion(question);
      influenceRepository.save(influence);
    }
  }

  public List<Question> getQuestionsByTestID(Test test) {
    return questionRepository.getQuestionsByTestID(test);
  }

  public List<Question> getByCriteria(
          Optional<String> text,
          Optional<String> position) {

    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Question> cq = cb.createQuery(Question.class);

    Root<Question> question = cq.from(Question.class);
    List<Predicate> predicateList = new ArrayList<>();
    if (text.isPresent()) {
      Predicate textPredicate = cb.like(question.get("text"), "%" + text.get() + "%");
      predicateList.add(textPredicate);
    }
    if (position.isPresent()) {
      Predicate positionPredicate = cb.equal(question.get("positionInTest"),
              Integer.valueOf(position.get()));
      predicateList.add(positionPredicate);
    }

    if (!predicateList.isEmpty()) {
      cq.where(predicateList.toArray(new Predicate[predicateList.size()]));
    }

    TypedQuery<Question> query = em.createQuery(cq);
    return query.getResultList();
  }

}