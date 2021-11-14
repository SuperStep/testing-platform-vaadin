package com.sust.testing.platform.backend.service;

import com.sust.testing.platform.backend.entity.User;
import com.sust.testing.platform.backend.repository.CompleteTestRepository;
import com.sust.testing.platform.backend.IVectorResult;
import com.sust.testing.platform.backend.entity.CompleteTestAnswers;
import com.sust.testing.platform.backend.entity.Question;
import com.sust.testing.platform.backend.entity.VectorPsychotype;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class CompleteTestAnswersService {

    private static final Logger LOGGER = Logger.getLogger(CompleteTestAnswersService.class.getName());

    private CompleteTestRepository completeTestRepository;
    private final VectorService vectorService;

    public CompleteTestAnswersService(CompleteTestRepository completeTestRepository,
                                      VectorService vectorService) {
        this.completeTestRepository = completeTestRepository;
        this.vectorService = vectorService;
    }

    public List<CompleteTestAnswers> findAll() {
        return completeTestRepository.findAll();
    }

    public Map<VectorPsychotype, Integer> getStats(User user, Long test_id) {

        Map<VectorPsychotype, Integer> stats = new HashMap<>();
        List<IVectorResult> results = completeTestRepository.getStats(user.getId(), test_id);

        List<VectorPsychotype> vectors = vectorService.findAll();

        for (IVectorResult res:
                results) {
            VectorPsychotype vector = vectors.stream()
                    .filter(x -> x.getId() == res.getVector())
                    .findFirst().orElse(null);
            if (vector != null){
                stats.put(vector, res.getValue());
                LOGGER.info(String.format("vector: %s , value: %s",
                        vector.toString(),
                        res.getValue().toString()));
            }
        }

        return stats;
    }

    public void deleteAnswers(Question question, User user) {
        try{
            completeTestRepository.deleteTestQuestion(
                    question.getId(), user.getId());
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
        }
    }

    public void save(CompleteTestAnswers completeTest) {
        try {
            completeTestRepository.save(completeTest);
        } catch (Exception e) {
            LOGGER.info(e.getLocalizedMessage());
        }
    }

    public void delete(CompleteTestAnswers completeTest) {
        completeTestRepository.delete(completeTest);
    }
}
