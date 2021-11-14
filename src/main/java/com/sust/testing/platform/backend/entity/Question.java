package com.sust.testing.platform.backend.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Question extends AbstractEntity implements Cloneable {

    @NotNull
    @NotEmpty
    private String text;
    private Integer positionInTest;
    @OneToMany(mappedBy = "question", fetch = FetchType.EAGER)
    private List<Influence> influences = new LinkedList<>();
    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getPositionInTest() {
        return positionInTest;
    }

    public void setPositionInTest(Integer positionInTest) {
        this.positionInTest = positionInTest;
    }

    public List<Influence> getInfluences() {
        return influences;
    }

    public void setInfluences(List<Influence> influences) {
        this.influences = influences;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public List<Influence> getInfluenceList() {
        return influences;
    }

    @Override
    public String toString() {
        return text;
    }
}
