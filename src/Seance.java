public class Seance {
    private String nom;
    private String type;
    private int dureeEchauffement;
    private String corps;
    private int dureeCooldown;
    private double pourcentageVMA;

    public Seance(String nom, String type, int dureeEchauffement, String corps, int dureeCooldown, double pourcentageVMA) {
        this.nom = nom;
        this.type = type;
        this.dureeEchauffement = dureeEchauffement;
        this.corps = corps;
        this.dureeCooldown = dureeCooldown;
        this.pourcentageVMA = pourcentageVMA;
    }

    public String getNom() {
        return nom;
    }

    public String getType() {
        return type;
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

    public double getDistanceKm(Profil profil) {
        double distance = 0.0;

        distance += distanceFromMinutesAtPercent(profil, dureeEchauffement, 0.65);
        distance += distanceFromMinutesAtPercent(profil, dureeCooldown, 0.65);

        String c = corps;
        if (c == null) {
            return distance;
        }

        String lower = c.toLowerCase().trim();

        // Fractionné:
        double fracKm = parseFractionMetresToKm(lower);
        if (fracKm > 0) {
            distance += fracKm;
            return distance;
        }

        // Répétitions au temps
        int rep = parseReps(lower);
        int minutesPerRep = parseMinutesPerRep(lower);
        if (rep > 0 && minutesPerRep > 0) {
            int totalMinutes = rep * minutesPerRep;
            distance += distanceFromMinutesAtPercent(profil, totalMinutes, pourcentageVMA);
            return distance;
        }

        // Répétitions en km: "2 x 3km" / "5 x 1.5km"
        double repKm = parseRepsKm(lower);
        if (repKm > 0) {
            distance += repKm;
            return distance;
        }

        // "10km en continu" / "8km en continu"
        double kmContinu = parseKmContinu(lower);
        if (kmContinu > 0) {
            distance += kmContinu;
            return distance;
        }

        // Simple durée: "40 min ..." / "30min ..."
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
        double speed = profil.getVma() * percentVma;
        return speed * hours;
    }

    private double parseFractionMetresToKm(String lower) {
        // Cherche un pattern simple: "<reps> x <metres>m"
        int xIndex = lower.indexOf("x");
        int mIndex = lower.indexOf("m");
        if (xIndex < 0 || mIndex < 0) {
            return 0.0;
        }

        // On prend le premier "m" après le x
        if (mIndex < xIndex) {
            return 0.0;
        }

        String left = lower.substring(0, xIndex).trim();
        String right = lower.substring(xIndex + 1).trim();

        // right commence par "400m ..."
        int mPos = right.indexOf("m");
        if (mPos < 0) {
            return 0.0;
        }

        String metresStr = right.substring(0, mPos).trim();

        left = left.replace(" ", "");
        metresStr = metresStr.replace(" ", "");

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
        // "3 x 10min" => 3
        int xIndex = lower.indexOf("x");
        if (xIndex < 0) {
            return 0;
        }
        String left = lower.substring(0, xIndex).trim().replace(" ", "");
        try {
            return Integer.parseInt(extractLeadingNumber(left));
        } catch (Exception e) {
            return 0;
        }
    }

    private int parseMinutesPerRep(String lower) {
        // "3 x 10min" => 10
        int xIndex = lower.indexOf("x");
        if (xIndex < 0) {
            return 0;
        }
        String right = lower.substring(xIndex + 1).trim();

        // trouver "min"
        int minIndex = right.indexOf("min");
        if (minIndex < 0) {
            return 0;
        }

        String beforeMin = right.substring(0, minIndex).trim();
        beforeMin = beforeMin.replace(" ", "");

        // beforeMin peut contenir "10" ou "10mn" etc  on extrait le nombre au début
        try {
            return Integer.parseInt(extractLeadingNumber(beforeMin));
        } catch (Exception e) {
            return 0;
        }
    }

    private double parseRepsKm(String lower) {
        // "2 x 3km" => 6 km
        int xIndex = lower.indexOf("x");
        if (xIndex < 0) {
            return 0.0;
        }
        int kmIndex = lower.indexOf("km");
        if (kmIndex < 0) {
            return 0.0;
        }
        if (kmIndex < xIndex) {
            return 0.0;
        }

        int reps = parseReps(lower);
        if (reps <= 0) {
            return 0.0;
        }

        String right = lower.substring(xIndex + 1).trim();
        int kmPos = right.indexOf("km");
        if (kmPos < 0) {
            return 0.0;
        }

        String kmStr = right.substring(0, kmPos).trim().replace(" ", "");
        double km;
        try {
            km = Double.parseDouble(extractLeadingDecimal(kmStr));
        } catch (Exception e) {
            return 0.0;
        }

        if (km <= 0) {
            return 0.0;
        }
        return reps * km;
    }

    private double parseKmContinu(String lower) {
        // "10km en continu ..." => 10
        int kmIndex = lower.indexOf("km");
        if (kmIndex < 0) {
            return 0.0;
        }

        // on ne veut pas confondre avec "2 x 3km"
        if (lower.contains("x") && lower.indexOf("x") < kmIndex) {
            return 0.0;
        }

        String beforeKm = lower.substring(0, kmIndex).trim().replace(" ", "");
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

        // si c'est un format "3 x 10min" on laisse parseMinutesPerRep gérer
        if (lower.contains("x") && lower.indexOf("x") < minIndex) {
            return 0;
        }

        String beforeMin = lower.substring(0, minIndex).trim().replace(" ", "");
        try {
            return Integer.parseInt(extractLeadingNumber(beforeMin));
        } catch (Exception e) {
            return 0;
        }
    }

    private String extractLeadingNumber(String s) {
        // garde uniquement les chiffres au début
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
        // garde chiffres + "." au début (ex: "21.1")
        StringBuilder b = new StringBuilder();
        boolean dotUsed = false;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch >= '0' && ch <= '9') {
                b.append(ch);
            } else if (ch == '.' && !dotUsed) {
                dotUsed = true;
                b.append(ch);
            } else {
                break;
            }
        }
        return b.toString();
    }
}