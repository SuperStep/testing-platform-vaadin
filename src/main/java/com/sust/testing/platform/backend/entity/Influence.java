package com.sust.testing.platform.backend.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Influence extends AbstractEntity implements Cloneable {

    public enum Answer {
        Yes, No
    }

    @Enumerated(EnumType.STRING)
    @NotNull
    private Influence.Answer answer;

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public VectorPsychotype getVector() {
        return vector;
    }

    public void setVector(VectorPsychotype vector) {
        this.vector = vector;
    }

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;
    private Integer value;
    @ManyToOne
    @JoinColumn(name = "vector_id")
    private VectorPsychotype vector;

    public String getVectorName() {
        return this.vector != null ? this.vector.getName() : "None";
    }

    public Influence(){
    }
    public Influence(Answer answer, Integer value){
        this.answer = answer;
        this.value = value;
    }
    public Influence(Answer answer, Integer value, Question question, VectorPsychotype vector) {
        this.answer = answer;
        this.question = question;
        this.value = value;
        this.question = question;
        this.vector = vector;
    }
}
