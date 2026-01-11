package com.rungenius.model.RunGeniusGenerator;
public class Seance {
    private String nom;
    private String type;
    private int dureeEchauffement;
    private String corps;
    private int dureeCooldown;
    private double pourcentageVMA;


    private Integer difficulte;
    private String typeBanque;

    public Seance(String nom, String type, int dureeEchauffement, String corps, int dureeCooldown, double pourcentageVMA) {
        this.nom = nom;
        this.type = type;
        this.dureeEchauffement = dureeEchauffement;
        this.corps = corps;
        this. dureeCooldown = dureeCooldown;
        this.pourcentageVMA = pourcentageVMA;
        this.difficulte = null;
        this.typeBanque = null;
    }

    public Seance(
            String nom,
            String type,
            int dureeEchauffement,
            String corps,
            int dureeCooldown,
            double pourcentageVMA,
            Integer difficulte,
            String typeBanque
    ) {
        this.nom = nom;
        this.type = type;
        this.dureeEchauffement = dureeEchauffement;
        this.corps = corps;
        this.dureeCooldown = dureeCooldown;
        this.pourcentageVMA = pourcentageVMA;
        this.difficulte = difficulte;
        this.typeBanque = typeBanque;
    }

    public String getNom() {
        return nom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription(Profil profil) {
        StringBuilder sb = new StringBuilder();
        sb.append(nom).append("\n");
        sb.append("Type: ").append(type).append("\n");

        String efAllure = profil.getAlluresPrincipales(21.1)[0];

        if (dureeEchauffement > 0) {
            sb.append("Échauffement: ").append(dureeEchauffement).append(" min (");
            sb.append(efAllure).append(")\n");
        }

        sb.append("Corps: ").append(corps).append("\n");
        sb.append("Allure: ").append(profil.getAllureFormatee(pourcentageVMA)).append("\n");

        if (dureeCooldown > 0) {
            sb.append("Cooldown: ").append(dureeCooldown).append(" min (");
            sb.append(efAllure).append(")\n");
        }

        return sb.toString();
    }

    public double getPourcentageVMA() {
        return pourcentageVMA;
    }

    public void setPourcentageVMA(double pourcentageVMA) {
        this.pourcentageVMA = pourcentageVMA;
    }

    public double getDistanceKm(Profil profil) {
        double distance = 0.0;

        distance += distanceFromMinutesAtPercent(profil, dureeEchauffement, 0.65);
        distance += distanceFromMinutesAtPercent(profil, dureeCooldown, 0.65);

        String c = corps;
        if (c == null) {
            return distance;
        }

        String lower = c.toLowerCase().trim();
        lower = lower.replace('×', 'x');

        double repKm = parseRepsKm(lower);
        if (repKm > 0) {
            distance += repKm;
            return distance;
        }

        double kmContinu = parseKmContinu(lower);
        if (kmContinu > 0) {
            distance += kmContinu;
            return distance;
        }

        double fracKm = parseFractionMetresToKm(lower);
        if (fracKm > 0) {
            distance += fracKm;
            return distance;
        }

        int rep = parseReps(lower);
        int minutesPerRep = parseMinutesPerRep(lower);
        if (rep > 0 && minutesPerRep > 0) {
            int totalMinutes = rep * minutesPerRep;
            distance += distanceFromMinutesAtPercent(profil, totalMinutes, pourcentageVMA);
            return distance;
        }

        int minutes = parseSingleMinutes(lower);
        if (minutes > 0) {
            distance += distanceFromMinutesAtPercent(profil, minutes, pourcentageVMA);
            return distance;
        }

        distance += distanceFromMinutesAtPercent(profil, 30, pourcentageVMA);
        return distance;
    }

    private double distanceFromMinutesAtPercent(Profil profil, int minutes, double percentVma) {
        if (minutes <= 0) {
            return 0.0;
        }
        double hours = minutes / 60.0;
        double speed = profil. getVma() * percentVma;
        return speed * hours;
    }

    private double parseFractionMetresToKm(String lower) {
        if (lower.contains("km")) {
            return 0.0;
        }

        int xIndex = lower.indexOf("x");
        int mIndex = lower.indexOf("m");
        if (xIndex < 0 || mIndex < 0 || mIndex < xIndex) {
            return 0.0;
        }

        String left = lower.substring(0, xIndex).trim();
        String right = lower.substring(xIndex + 1).trim();

        int mPos = right.indexOf("m");
        if (mPos < 0) {
            return 0.0;
        }

        String metresStr = right.substring(0, mPos).trim();

        left = left.replaceAll("\\s+", "");
        metresStr = metresStr.replaceAll("\\s+", "");

        int reps;
        int metres;
        try {
            reps = Integer.parseInt(extractLeadingNumber(left));
            metres = Integer.parseInt(extractLeadingNumber(metresStr));
        } catch (Exception e) {
            return 0.0;
        }

        if (reps <= 0 || metres <= 0) {
            return 0.0;
        }

        double totalMetres = reps * (double) metres;
        return totalMetres / 1000.0;
    }

    private int parseReps(String lower) {
        int xIndex = lower.indexOf("x");
        if (xIndex < 0) {
            return 0;
        }
        String left = lower.substring(0, xIndex).trim().replaceAll("\\s+", "");
        try {
            return Integer.parseInt(extractLeadingNumber(left));
        } catch (Exception e) {
            return 0;
        }
    }

    private int parseMinutesPerRep(String lower) {
        int xIndex = lower.indexOf("x");
        if (xIndex < 0) {
            return 0;
        }
        String right = lower.substring(xIndex + 1).trim();

        int minIndex = right.indexOf("min");
        if (minIndex < 0) {
            return 0;
        }

        String beforeMin = right.substring(0, minIndex).trim().replaceAll("\\s+", "");

        try {
            return Integer.parseInt(extractLeadingNumber(beforeMin));
        } catch (Exception e) {
            return 0;
        }
    }

    private double parseRepsKm(String lower) {
        int xIndex = lower.indexOf("x");
        if (xIndex < 0) {
            return 0.0;
        }

        String left = lower.substring(0, xIndex).trim();
        String right = lower.substring(xIndex + 1).trim();

        int kmIndex = right.indexOf("km");
        if (kmIndex < 0) {
            return 0.0;
        }

        int reps;
        try {
            String repsStr = left.replaceAll("\\s+", "");
            reps = Integer. parseInt(extractLeadingNumber(repsStr));
        } catch (Exception e) {
            return 0.0;
        }

        if (reps <= 0) {
            return 0.0;
        }

        String kmPart = right.substring(0, kmIndex).trim();
        kmPart = kmPart.replaceAll("[^0-9.,]", "");
        
        if (kmPart.isEmpty()) {
            return 0.0;
        }

        double km;
        try {
            km = Double.parseDouble(kmPart. replace(',', '.'));
        } catch (Exception e) {
            return 0.0;
        }

        if (km <= 0) {
            return 0.0;
        }

        return reps * km;
    }

    private double parseKmContinu(String lower) {
        if (lower.contains("x")) {
            return 0.0;
        }

        int kmIndex = lower.indexOf("km");
        if (kmIndex < 0) {
            return 0.0;
        }

        String beforeKm = lower.substring(0, kmIndex).trim();
        beforeKm = beforeKm.replaceAll("\\s+", "");
        
        if (beforeKm.isEmpty()) {
            return 0.0;
        }

        double km;
        try {
            km = Double.parseDouble(extractLeadingDecimal(beforeKm));
        } catch (Exception e) {
            return 0.0;
        }

        if (km <= 0) {
            return 0.0;
        }
        return km;
    }

    private int parseSingleMinutes(String lower) {
        int minIndex = lower.indexOf("min");
        if (minIndex < 0) {
            return 0;
        }

        if (lower.contains("x") && lower.indexOf("x") < minIndex) {
            return 0;
        }

        String beforeMin = lower.substring(0, minIndex).trim().replaceAll("\\s+", "");
        try {
            return Integer.parseInt(extractLeadingNumber(beforeMin));
        } catch (Exception e) {
            return 0;
        }
    }

    private String extractLeadingNumber(String s) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch >= '0' && ch <= '9') {
                b.append(ch);
            } else {
                break;
            }
        }
        return b.toString();
    }

    private String extractLeadingDecimal(String s) {
        StringBuilder b = new StringBuilder();
        boolean dotUsed = false;
        for (int i = 0; i < s.length(); i++) {
            char ch = s. charAt(i);
            if (ch >= '0' && ch <= '9') {
                b. append(ch);
            } else if ((ch == '.' || ch == ',') && !dotUsed) {
                dotUsed = true;
                b.append('.');
            } else {
                break;
            }
        }
        return b.toString();
    }

    public String getCorps() {
        return corps;
    }

    public void setCorps(String corps) {
        this.corps = corps;
    }

    public int getDureeEchauffement() {
        return dureeEchauffement;
    }

    public void setDureeEchauffement(int dureeEchauffement) {
        this.dureeEchauffement = dureeEchauffement;
    }

    public int getDureeCooldown() {
        return dureeCooldown;
    }

    public void setDureeCooldown(int dureeCooldown) {
        this.dureeCooldown = dureeCooldown;
    }

    public Integer getDifficulte() {
        return difficulte;
    }

    public void setDifficulte(Integer difficulte) {
        this.difficulte = difficulte;
    }

    public String getTypeBanque() {
        return typeBanque;
    }

    public void setTypeBanque(String typeBanque) {
        this.typeBanque = typeBanque;
    }
}