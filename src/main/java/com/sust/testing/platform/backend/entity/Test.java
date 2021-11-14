package com.sust.testing.platform.backend.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Data
public class Test extends AbstractEntity implements Cloneable{

    private String name;
    private String description;

    @OneToMany(mappedBy = "test", fetch = FetchType.EAGER)
    private List<Question> questions = new LinkedList<>();

    public Test(){
    }
    public Test(String name) {
        this.name = name;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public String getName() {
        return name;
    }

    public List<Question> getQuestions(Boolean randomize) {
        if (randomize){
            Collections.shuffle(questions);
        } else {
            return questions.stream()
                    .sorted(Comparator.comparing(Question::getPositionInTest))
                    .collect(Collectors.toList());
        }
        return questions;

    }

}
