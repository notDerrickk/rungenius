package com.rungenius.model.dto;

import com.rungenius.model.RunGeniusGenerator.Profil;
import com.rungenius.model.RunGeniusGenerator.Seance;
import com.rungenius.model.RunGeniusEditor.ProgrammeCustom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO pour sérialiser/désérialiser un Programme et son Profil
 * Permet de stocker dans la base de données et de reconstruire les objets
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgrammeStorageDTO {
    
    private String title;
    private double distanceKm;
    private List<List<SeanceStorageDTO>> semaines;
    
    // Profil data
    private String niveau;
    private int sortiesParSemaine;
    private double vma;
    private Integer objectifSeconds;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeanceStorageDTO {
        private String nom;
        private String type;
        private int dureeEchauffement;
        private String corps;
        private int dureeCooldown;
        private double pourcentageVMA;
        private Integer difficulte;
        private String typeBanque;
    }
    

    public static ProgrammeStorageDTO from(com.rungenius.model.RunGeniusGenerator.Programme programme, Profil profil) {
        ProgrammeStorageDTO dto = new ProgrammeStorageDTO();
        dto.setTitle(programme.getTitle());
        dto.setDistanceKm(programme.getDistanceKm());
        
        // Convertir les semaines et séances
        List<List<SeanceStorageDTO>> semaines = new ArrayList<>();
        for (Seance[] semaine : programme.getSemaines()) {
            List<SeanceStorageDTO> seancesDto = new ArrayList<>();
            for (Seance seance : semaine) {
                SeanceStorageDTO seanceDto = new SeanceStorageDTO();
                seanceDto.setNom(seance.getNom());
                seanceDto.setType(seance.getType());
                seanceDto.setDureeEchauffement(seance.getDureeEchauffement());
                seanceDto.setCorps(seance.getCorps());
                seanceDto.setDureeCooldown(seance.getDureeCooldown());
                seanceDto.setPourcentageVMA(seance.getPourcentageVMA());
                seanceDto.setDifficulte(seance.getDifficulte());
                seanceDto.setTypeBanque(seance.getTypeBanque());
                seancesDto.add(seanceDto);
            }
            semaines.add(seancesDto);
        }
        dto.setSemaines(semaines);
        
        // Copier les données du profil
        dto.setNiveau(profil.getNiveau());
        dto.setSortiesParSemaine(profil.getSortiesParSemaine());
        dto.setVma(profil.getVma());
        dto.setObjectifSeconds(profil.getObjectifSeconds());
        
        return dto;
    }
    

    public Object[] toProgrammeAndProfil() {
        Profil profil = new Profil(
            this.niveau,
            this.sortiesParSemaine,
            this.vma,
            this.objectifSeconds
        );
        
        // Reconstruire le Programme
        ProgrammeCustom programme = new ProgrammeCustom(this.title, this.distanceKm);
        
        for (List<SeanceStorageDTO> seancesDto : this.semaines) {
            List<Seance> seances = new ArrayList<>();
            for (SeanceStorageDTO seanceDto : seancesDto) {
                Seance seance = new Seance(
                    seanceDto.getNom(),
                    seanceDto.getType(),
                    seanceDto.getDureeEchauffement(),
                    seanceDto.getCorps(),
                    seanceDto.getDureeCooldown(),
                    seanceDto.getPourcentageVMA(),
                    seanceDto.getDifficulte(),
                    seanceDto.getTypeBanque()
                );
                seances.add(seance);
            }
            programme.addSemaine(seances.toArray(new Seance[0]));
        }
        
        return new Object[]{programme, profil};
    }
}
