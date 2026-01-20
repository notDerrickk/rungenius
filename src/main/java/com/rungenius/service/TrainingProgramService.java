package com.rungenius.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rungenius.model.RunGeniusGenerator.Profil;
import com.rungenius.model.RunGeniusGenerator.Programme;
import com.rungenius.model.RunGeniusGenerator.Seance;
import com.rungenius.model.dto.ProgrammeStorageDTO;
import com.rungenius.model.entity.TrainingProgram;
import com.rungenius.model.entity.User;
import com.rungenius.model.RunGeniusEditor.ProgrammeCustom;
import com.rungenius.repository.TrainingProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
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
            
            ProgrammeStorageDTO dto = ProgrammeStorageDTO.from(programme, profil);
            String json = objectMapper.writeValueAsString(dto);
            tp.setProgramData(json);
            
            return trainingProgramRepository.save(tp);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la sauvegarde du programme", e);
        }
    }
    
    @Transactional
    public TrainingProgram saveCustomProgram(User user, ProgrammeCustom programme, Profil profil,
                                             String objectif, LocalDate raceDate) {
        try {
            TrainingProgram tp = new TrainingProgram();
            tp.setUser(user);
            tp.setTitle(programme.getTitle());
            tp.setRaceType("custom");
            tp.setDistanceKm(programme.getDistanceKm());
            tp.setNiveau(profil.getNiveau());
            tp.setSorties(profil.getSortiesParSemaine());
            tp.setVma(profil.getVma());
            tp.setObjectif(objectif != null ? objectif : "");
            tp.setRaceDate(raceDate);
            
            ProgrammeStorageDTO dto = ProgrammeStorageDTO.from(programme, profil);
            String json = objectMapper.writeValueAsString(dto);
            tp.setProgramData(json);
            
            return trainingProgramRepository.save(tp);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la sauvegarde du programme personnalisé", e);
        }
    }
    
    public List<TrainingProgram> getUserPrograms(User user) {
        return trainingProgramRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    public Optional<TrainingProgram> getUserProgram(Long id, User user) {
        return trainingProgramRepository.findByIdAndUser(id, user);
    }
    
    @Transactional
    public TrainingProgram updateProgram(TrainingProgram trainingProgram) {
        return trainingProgramRepository.save(trainingProgram);
    }

    @Transactional
    public boolean deleteProgramForUser(User user, Long programId) {
        try {
            Optional<TrainingProgram> opt = trainingProgramRepository.findByIdAndUser(programId, user);
            if (opt.isPresent()) {
                trainingProgramRepository.delete(opt.get());
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la suppression du programme", e);
        }
    }
    
    public Map<String, Object> loadProgramData(TrainingProgram tp) {
        try {
            Map<String, Object> data = new HashMap<>();
            
            try {
                ProgrammeStorageDTO dto = objectMapper.readValue(
                    tp.getProgramData(), 
                    ProgrammeStorageDTO.class
                );
                
                Object[] result = dto.toProgrammeAndProfil();
                data.put("programme", result[0]);
                data.put("profil", result[1]);
                return data;
            } catch (Exception dtoException) {
                try {
                    Map<String, Object> rawData = objectMapper.readValue(
                        tp.getProgramData(),
                        Map.class
                    );
                    
                    // Si on a les données brutes, essayer de les reconstruire
                    Object programmeObj = rawData.get("programme");
                    Object profilObj = rawData.get("profil");
                    
                    if (programmeObj instanceof Map && profilObj instanceof Map) {
                        Programme programme = reconstructProgrammeFromMap((Map<String, Object>) programmeObj);
                        Profil profil = reconstructProfilFromMap((Map<String, Object>) profilObj);
                        
                        data.put("programme", programme);
                        data.put("profil", profil);
                        return data;
                    }
                    
                    // Si les structures ne correspondent pas, retourner les données brutes
                    return rawData;
                } catch (Exception fallbackException) {
                    throw dtoException;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du chargement du programme: " + e.getMessage(), e);
        }
    }
    
    @SuppressWarnings("unchecked")
    private Profil reconstructProfilFromMap(Map<String, Object> profilMap) {
        String niveau = (String) profilMap.get("niveau");
        int sorties = ((Number) profilMap.get("sortiesParSemaine")).intValue();
        double vma = ((Number) profilMap.get("vma")).doubleValue();
        Integer objectifSeconds = null;
        
        Object objObj = profilMap.get("objectifSeconds");
        if (objObj != null && objObj instanceof Number) {
            objectifSeconds = ((Number) objObj).intValue();
        }
        
        return new Profil(niveau, sorties, vma, objectifSeconds);
    }
    

    @SuppressWarnings("unchecked")
    private Programme reconstructProgrammeFromMap(Map<String, Object> programmeMap) {
        String title = (String) programmeMap.get("title");
        double distanceKm = ((Number) programmeMap.get("distanceKm")).doubleValue();
        
        ProgrammeCustom programme = new ProgrammeCustom(title, distanceKm);
        
        List<List<Map<String, Object>>> semaines = (List<List<Map<String, Object>>>) programmeMap.get("semaines");
        if (semaines != null) {
            for (List<Map<String, Object>> seancesMap : semaines) {
                List<Seance> seances = new ArrayList<>();
                for (Map<String, Object> seanceMap : seancesMap) {
                    Seance seance = reconstructSeanceFromMap(seanceMap);
                    seances.add(seance);
                }
                programme.addSemaine(seances.toArray(new Seance[0]));
            }
        }
        
        return programme;
    }
    

    @SuppressWarnings("unchecked")
    private Seance reconstructSeanceFromMap(Map<String, Object> seanceMap) {
        String nom = (String) seanceMap.get("nom");
        String type = (String) seanceMap.get("type");
        int dureeEchauffement = ((Number) seanceMap.get("dureeEchauffement")).intValue();
        String corps = (String) seanceMap.get("corps");
        int dureeCooldown = ((Number) seanceMap.get("dureeCooldown")).intValue();
        double pourcentageVMA = ((Number) seanceMap.get("pourcentageVMA")).doubleValue();
        
        Integer difficulte = null;
        Object diffObj = seanceMap.get("difficulte");
        if (diffObj != null && diffObj instanceof Number) {
            difficulte = ((Number) diffObj).intValue();
        }
        
        String typeBanque = (String) seanceMap.get("typeBanque");
        
        return new Seance(nom, type, dureeEchauffement, corps, dureeCooldown, pourcentageVMA, difficulte, typeBanque);
    }
}
