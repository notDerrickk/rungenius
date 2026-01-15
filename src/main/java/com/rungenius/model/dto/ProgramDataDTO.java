package com.rungenius.model.dto;

import java.util.List;
import java.util.Map;

public class ProgramDataDTO {
    private String title;
    private double distance;
    private double vma;
    private int nbWeeks;
    private int nbSessions;
    private AlluresDTO allures;
    private String raceDate;
    private String objectifTemps;
    private List<List<SeanceDTO>> weeks;
    // Envoyé par l'éditeur comme objet (ex: {ef:0.65,seuil:0.85,...}).
    // Le backend n'en a pas besoin pour générer, mais on le mappe pour éviter une erreur Jackson.
    private Map<String, Double> _allurePct;

    // Getters et Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    public double getVma() { return vma; }
    public void setVma(double vma) { this.vma = vma; }

    public int getNbWeeks() { return nbWeeks; }
    public void setNbWeeks(int nbWeeks) { this.nbWeeks = nbWeeks; }

    public int getNbSessions() { return nbSessions; }
    public void setNbSessions(int nbSessions) { this.nbSessions = nbSessions; }

    public AlluresDTO getAllures() { return allures; }
    public void setAllures(AlluresDTO allures) { this.allures = allures; }

    public String getRaceDate() { return raceDate; }
    public void setRaceDate(String raceDate) { this.raceDate = raceDate; }

    public String getObjectifTemps() { return objectifTemps; }
    public void setObjectifTemps(String objectifTemps) { this.objectifTemps = objectifTemps; }

    public List<List<SeanceDTO>> getWeeks() { return weeks; }
    public void setWeeks(List<List<SeanceDTO>> weeks) { this.weeks = weeks; }

    public Map<String, Double> get_allurePct() { return _allurePct; }
    public void set_allurePct(Map<String, Double> _allurePct) { this._allurePct = _allurePct; }

    public static class AlluresDTO {
        private String ef;
        private String seuil;
        private String vma;
        private String objectif;
        private String objectifTemps;

        public String getEf() { return ef; }
        public void setEf(String ef) { this.ef = ef; }

        public String getSeuil() { return seuil; }
        public void setSeuil(String seuil) { this.seuil = seuil; }

        public String getVma() { return vma; }
        public void setVma(String vma) { this.vma = vma; }

        public String getObjectif() { return objectif; }
        public void setObjectif(String objectif) { this.objectif = objectif; }

        public String getObjectifTemps() { return objectifTemps; }
        public void setObjectifTemps(String objectifTemps) { this.objectifTemps = objectifTemps; }
    }

    public static class SeanceDTO {
        private String nom;
        private String type;
        private int echauffement;
        private String corps;
        private String allure;
        private String allureText;
        private Double allurePct; // Pourcentage VMA calculé (peut être null)
        private int cooldown;
        private boolean customAllure;

        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public int getEchauffement() { return echauffement; }
        public void setEchauffement(int echauffement) { this.echauffement = echauffement; }

        public String getCorps() { return corps; }
        public void setCorps(String corps) { this.corps = corps; }

        public String getAllure() { return allure; }
        public void setAllure(String allure) { this.allure = allure; }

        public String getAllureText() { return allureText; }
        public void setAllureText(String allureText) { this.allureText = allureText; }

        public Double getAllurePct() { return allurePct; }
        public void setAllurePct(Double allurePct) { this.allurePct = allurePct; }

        public int getCooldown() { return cooldown; }
        public void setCooldown(int cooldown) { this.cooldown = cooldown; }

        public boolean isCustomAllure() { return customAllure; }
        public void setCustomAllure(boolean customAllure) { this.customAllure = customAllure; }
    }
}
