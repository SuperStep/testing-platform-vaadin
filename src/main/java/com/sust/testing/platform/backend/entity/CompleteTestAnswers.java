package com.sust.testing.platform.backend.entity;

import com.sust.testing.platform.ui.view.list.CompletedTestsList;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "complete_test_answers")
public class CompleteTestAnswers extends AbstractEntity implements Cloneable {

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;
    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;
    @Enumerated(EnumType.STRING)
    private Influence.Answer answer;
    @ManyToOne
    @JoinColumn(name = "vector_id")
    private VectorPsychotype vector;
    private Integer value;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public CompleteTestAnswers(){
    }

    public CompleteTestAnswers(Test test,
                               Question question,
                               Influence.Answer answer,
                               VectorPsychotype vector,
                               Integer value,
                               User user) {
        this.test = test;
        this.question = question;
        this.answer = answer;
        this.value = value;
        this.vector = vector;
        this.user = user;
    }

}
