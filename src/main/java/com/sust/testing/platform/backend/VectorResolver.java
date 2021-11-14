package com.sust.testing.platform.backend;

import com.sust.testing.platform.backend.entity.*;
import com.sust.testing.platform.backend.service.CompleteTestAnswersService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class VectorResolver {

    private static final Logger LOGGER = Logger.getLogger(VectorResolver.class.getName());

    private CompleteTestAnswersService completeTestAnswersService;

    public VectorResolver (CompleteTestAnswersService completeTestAnswersService) {
        this.completeTestAnswersService = completeTestAnswersService;
    }

    public void saveAnswers(Test test, Question question, Influence.Answer answer, User user) {

        List<Influence> influenceList = question.getInfluenceList();

        for (Influence influence:
                influenceList) {
            if (answer == influence.getAnswer()) {

                this.completeTestAnswersService.save(new CompleteTestAnswers(
                        test,
                        question,
                        answer,
                        influence.getVector(),
                        influence.getValue(),
                        user));
            }
        }

    }
}
