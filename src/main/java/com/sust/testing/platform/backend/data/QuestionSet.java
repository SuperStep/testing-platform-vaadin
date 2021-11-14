package com.sust.testing.platform.backend.data;

import java.util.Map;

public class QuestionSet {

    private String testName;
    private String questionText;
    private int position;
    private String answer;
    private Map<String, Integer> influenceMap;

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Map<String, Integer> getInfluenceMap() {
        return influenceMap;
    }

    public void setInfluenceMap(Map<String, Integer> influenceMap) {
        this.influenceMap = influenceMap;
    }

    public QuestionSet(){
    }

    public QuestionSet(String testName,
                       String questionText,
                       int position,
                       String answer,
                       Map<String, Integer> influenceMap ) {
        this.testName = testName;
        this.questionText = questionText;
        this.position = position;
        this.answer = answer;
        this.influenceMap = influenceMap;
    }
}
