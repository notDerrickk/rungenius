package com.rungenius.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rungenius.model.RunGeniusGenerator.Programme;
import com.rungenius.model.RunGeniusGenerator.Profil;
import com.rungenius.model.entity.TrainingProgram;
import com.rungenius.model.entity.User;
import com.rungenius.repository.TrainingProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TrainingProgramService {
    
    @Autowired
    private TrainingProgramRepository trainingProgramRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Transactional
    public TrainingProgram saveProgram(User user, Programme programme, Profil profil, 
                                       String raceType, String objectif, LocalDate raceDate) {
        try {
            TrainingProgram tp = new TrainingProgram();
            tp.setUser(user);
            tp.setTitle(programme.getTitle());
            tp.setRaceType(raceType);
            tp.setDistanceKm(programme.getDistanceKm());
            tp.setNiveau(profil.getNiveau());
            tp.setSorties(profil.getSortiesParSemaine());
            tp.setVma(profil.getVma());
            tp.setObjectif(objectif);
            tp.setRaceDate(raceDate);
            
            Map<String, Object> data = new HashMap<>();
            data.put("programme", programme);
            data.put("profil", profil);
            
            String json = objectMapper.writeValueAsString(data);
            tp.setProgramData(json);
            
            return trainingProgramRepository.save(tp);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la sauvegarde du programme", e);
        }
    }
    
    public List<TrainingProgram> getUserPrograms(User user) {
        return trainingProgramRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    public Optional<TrainingProgram> getUserProgram(Long id, User user) {
        return trainingProgramRepository.findByIdAndUser(id, user);
    }
    
    public Map<String, Object> loadProgramData(TrainingProgram tp) {
        try {
            return objectMapper.readValue(tp.getProgramData(), Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du chargement du programme", e);
        }
    }
}
