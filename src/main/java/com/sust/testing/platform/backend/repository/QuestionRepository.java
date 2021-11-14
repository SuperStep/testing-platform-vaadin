package com.sust.testing.platform.backend.repository;

import com.sust.testing.platform.backend.entity.Question;
import com.sust.testing.platform.backend.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.EntityManager;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("select q from Question q " +
            "where q.test = :test")
    List<Question> getQuestionsByTestID(@Param("test") Test test);

}
