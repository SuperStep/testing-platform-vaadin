package com.sust.testing.platform.backend.service;

import com.sust.testing.platform.backend.entity.VectorPsychotype;
import com.sust.testing.platform.backend.repository.VectorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class VectorService {

    private static final Logger LOGGER = Logger.getLogger(VectorService.class.getName());

    private VectorRepository vectorRepository;

    public VectorService(VectorRepository vectorRepository) {
        this.vectorRepository = vectorRepository;
    }

    public List<VectorPsychotype> findAll() {
        return vectorRepository.findAll();
    }

    public VectorPsychotype save(VectorPsychotype vectorPsychotype) {
        vectorRepository.save(vectorPsychotype);
        return vectorPsychotype;
    }

    public void delete(VectorPsychotype vectorPsychotype) {
        vectorRepository.delete(vectorPsychotype);
    }

}
