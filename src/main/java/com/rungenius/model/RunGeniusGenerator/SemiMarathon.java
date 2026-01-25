package com.rungenius.model.RunGeniusGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SemiMarathon implements Programme {
    private Profil profil;
    private List<Seance[]> semaines;
    private int nbSemaines;

    private BanqueExercices banque;

    // suivi pour AS (ordre + cumul km)
    private int asSequenceIndex = 0;
    private int fractionneCycleIndex = 0;
    private Random random = new Random();

    // Séquences prédéfinies pour AS selon niveau
    private List<CorpsDeSeance> asSequenceDebutant;
    private List<CorpsDeSeance> asSequenceMoyen;
    private List<CorpsDeSeance> asSequenceExpert;

    private double targetDistanceKm;
    private String title;

    public SemiMarathon(Profil profil) {
        this(profil, 12, 21.1, "Semi-Marathon");
    }

    public SemiMarathon(Profil profil, int nbSemaines) {
        this(profil, nbSemaines, 21.1, "Semi-Marathon");
    }

    public SemiMarathon(Profil profil, int nbSemaines, double targetDistanceKm, String title) {
        this.profil = profil;
        this.semaines = new ArrayList<Seance[]>();
        this.banque = new BanqueExercices();
        this.nbSemaines = (nbSemaines > 0) ? nbSemaines : 12;
        this.targetDistanceKm = targetDistanceKm;
        this.title = title != null ? title : (targetDistanceKm == 10.0 ? "10 km" : "Semi-Marathon");
        initialiserSequencesAS();
        genererSemaines();
    }

    private void initialiserSequencesAS() {
        String typeAS = getTypeAllureSpecifique();
        
        // exercices AS par difficulté depuis la banque
        List<CorpsDeSeance> niveau1 = banque.getExercicesParDifficulte(typeAS, 2);
        List<CorpsDeSeance> niveau2 = banque.getExercicesParDifficulte(typeAS, 4);
        List<CorpsDeSeance> niveau3 = banque.getExercicesParDifficulte(typeAS, 5);

        // Pour débutant: niveau 1 puis niveau 2
        asSequenceDebutant = new ArrayList<>();
        if (niveau1 != null) asSequenceDebutant.addAll(niveau1);
        if (niveau2 != null) asSequenceDebutant.addAll(niveau2);

        asSequenceMoyen = new ArrayList<>();
        if (niveau2 != null) asSequenceMoyen.addAll(niveau2);
        if (niveau3 != null) asSequenceMoyen.addAll(niveau3);

        asSequenceExpert = new ArrayList<>();
        if (niveau3 != null) asSequenceExpert.addAll(niveau3);
    }

    private String getTypeAllureSpecifique() {
        // Retourne le type d'exercice AS approprié selon la distance
        if (Math.abs(targetDistanceKm - 10.0) < 1e-6) {
            return "Allure Spécifique 10km";
        } else if (Math.abs(targetDistanceKm - 21.1) < 1e-6) {
            return "Allure Spécifique";
        } else {
            if (targetDistanceKm <= 15.0) {
                return "Allure Spécifique 10km";
            } else {
                return "Allure Spécifique";
            }
        }
    }

    private void genererSemaines() {
        for (int i = 0; i < nbSemaines; i++) {
            boolean semaineRepos = ((i + 1) % 5 == 0);
            int zone = determinerZone(i + 1);
            Seance[] semaine = genererSemaine(i + 1, semaineRepos, zone);
            semaines.add(semaine);
        }
        // Appliquer l'affûtage sur les 2 dernières semaines
        appliquerAffutage();
    }


    private int determinerZone(int numeroSemaine) {
        int deuxCinquiemes = (nbSemaines * 2) / 5;
        if (numeroSemaine <= deuxCinquiemes) {
            return 1;
        }
        return 2;
    }

    private void appliquerAffutage() {
        if (nbSemaines < 2) return;
        
        // S-2 : réduire de 25%
        int indexS2 = nbSemaines - 2;
        if (indexS2 >= 0 && indexS2 < semaines.size()) {
            reduireVolumeSemaine(indexS2, 0.75);
        }
        
        // S-1 : réduire de 50%
        int indexS1 = nbSemaines - 1;
        if (indexS1 >= 0 && indexS1 < semaines.size()) {
            reduireVolumeSemaine(indexS1, 0.50);
        }
    }

    private void reduireVolumeSemaine(int indexSemaine, double facteur) {
        Seance[] semaine = semaines.get(indexSemaine);
        for (int i = 0; i < semaine.length; i++) {
            semaine[i] = reduireVolumeSeance(semaine[i], facteur);
        }
    }

    private Seance reduireVolumeSeance(Seance seance, double facteur) {
        String corps = seance.getCorps();
        if (corps == null) return seance;
        
        String corpsReduit = reduireVolumeCorps(corps, facteur);
        
        return new Seance(
            seance.getNom(),
            seance.getType(),
            seance.getDureeEchauffement(),
            corpsReduit,
            seance.getDureeCooldown(),
            seance.getPourcentageVMA(),
            seance.getDifficulte(),
            seance.getTypeBanque()
        );
    }

    private String reduireVolumeCorps(String corps, double facteur) {
        Pattern pReps = Pattern.compile("(\\d+)\\s*[x×]");
        Matcher m = pReps.matcher(corps);
        if (m.find()) {
            try {
                int reps = Integer.parseInt(m.group(1));
                int newReps = Math.max(1, (int)(reps * facteur));
                return corps.replaceFirst("\\d+\\s*[x×]", newReps + " x");
            } catch (Exception ignored) {}
        }
        
        Pattern pKm = Pattern.compile("(\\d+(?:[.,]\\d+)?)\\s*km");
        Matcher m2 = pKm.matcher(corps);
        if (m2.find()) {
            try {
                double km = Double.parseDouble(m2.group(1).replace(',', '.'));
                double newKm = km * facteur;
                String formatted = formatKmValue(newKm);
                return corps.replaceFirst("\\d+(?:[.,]\\d+)?\\s*km", formatted + "km");
            } catch (Exception ignored) {}
        }
        
        Pattern pMin = Pattern.compile("(\\d+)\\s*min");
        Matcher m3 = pMin.matcher(corps);
        if (m3.find()) {
            try {
                int min = Integer.parseInt(m3.group(1));
                int newMin = Math.max(10, (int)(min * facteur));
                return corps.replaceFirst("\\d+\\s*min", newMin + "min");
            } catch (Exception ignored) {}
        }
        
        return corps;
    }

    private Seance[] genererSemaine(int numeroSemaine, boolean repos, int zone) {
        int nbSeances = profil.getSortiesParSemaine();
        if (repos) {
            return genererSemaineRepos(numeroSemaine, nbSeances);
        } else {
            Seance[] semaine = new Seance[nbSeances];
            for (int i = 0; i < nbSeances; i++) {
                semaine[i] = creerSeanceNormale(i, numeroSemaine, zone);
            }
            return semaine;
        }
    }

    private Seance[] genererSemaineRepos(int numeroSemaine, int nbSeances) {
        Seance[] semaine = new Seance[nbSeances];
        for (int i = 0; i < nbSeances; i++) {
            if (i == 0) {
                // séance EF tranquille 
                semaine[i] = creerSeanceRepos(i, numeroSemaine);
            } else if (i == 1) {
                // une séance tempo modérée 
                semaine[i] = creerSeanceTempo(i, numeroSemaine);
            } else {
                semaine[i] = creerSeanceRepos(i, numeroSemaine);
            }
        }
        return semaine;
    }

    private Seance creerSeanceRepos(int jour, int semaine) {
        double[] p = profil.getPourcentagesPrincipales(targetDistanceKm);
        double pEF = p[0];
        String nom = "Séance " + (jour + 1) + " (Repos)";
        double km = computeEnduranceKmForWeek(semaine);
        String corps = formatKmValue(km) + "km en endurance fondamentale";
        return new Seance(nom, "Endurance Fondamentale", 5, corps, 5, pEF);
    }

    private Seance creerSeanceTempo(int jour, int semaine) {
        String niveau = profil.getNiveau();
        int difficulte = niveauToDifficulte(niveau);

        // Garder l'ordre des exercices (pas de random)
        CorpsDeSeance ex = getNextExerciceEnOrdre("Tempo", difficulte);

        if (ex == null) {
            List<CorpsDeSeance> tempoList = banque.getExercicesParDifficulte("Tempo", difficulte);
            if (tempoList != null && !tempoList.isEmpty()) {
                ex = tempoList.get(0);
            }
        }

        double pSeuil;
        String corps;

        if (ex != null) {
            corps = ex.getDescription();
            pSeuil = ex.resolvePourcentageVMA(profil);
        } else {
            double[] p = profil.getPourcentagesPrincipales(targetDistanceKm);
            pSeuil = p[2];
            int dureeMin = calculerDureeProgressive(25, semaine);
            corps = dureeMin + "min en tempo confortable (pas trop dur)";
        }

        String nom = "Séance " + (jour + 1) + " - Tempo confortable";
        Integer diff = (ex != null) ? ex.getDifficulte() : difficulte;
        return new Seance(nom, "Tempo", 10, corps, 5, pSeuil, diff, "Tempo");
    }

    private int niveauToDifficulte(String niveau) {
        if (niveau == null) return 4;
        String s = niveau.toLowerCase();
        if (s.contains("début") || s.contains("debut") || s.contains("debutant")) return 2;
        if (s.contains("avanc") || s.contains("expert")) return 5;
        return 4;
    }

    private int choisirDifficulteFractionne(String niveau) {
        String s = niveau.toLowerCase();
        if (s.contains("début") || s.contains("debut") || s.contains("debutant") || s.contains("débutant")) return 2;
        if (s.contains("novice")) return 4;
        if (s.contains("avanc") || s.contains("expert")) return 5;
        return 4;
    }

    private boolean useFractionneLongNext() {
        boolean useLong = (fractionneCycleIndex % 3) < 2;
        fractionneCycleIndex++;
        return useLong;
    }

    private Seance creerSeanceNormale(int jour, int semaine, int zone) {
        String niveau = profil.getNiveau();

        double[] p = profil.getPourcentagesPrincipales(targetDistanceKm);
        double pSpec = p[1];
        double pEF = p[0];

        if (jour == 0) {
            // Séance 1 : Fractionné
            boolean useLong = useFractionneLongNext();
            String typeFrac = useLong ? "Fractionné Long" : "Fractionné Court";
            String nom = "Séance 1 - " + typeFrac;
            int difficulteFrac = choisirDifficulteFractionne(niveau);
            
            // Garder l'ordre des exercices (pas de random)
            CorpsDeSeance ex = getNextExerciceEnOrdre(typeFrac, difficulteFrac);
            
            String corps;
            double pourcentageFractionne;
            if (ex != null) {
                corps = ex.getDescription();
                pourcentageFractionne = ex.resolvePourcentageVMA(profil);
            } else if (useLong) {
                corps = "5 x 1000m récup 2min";
                pourcentageFractionne = 0.90;
            } else {
                corps = "8 x 400m récup 1min";
                pourcentageFractionne = 0.95;
            }
            Integer diff = (ex != null) ? ex.getDifficulte() : difficulteFrac;
            return new Seance(nom, typeFrac, 15, corps, 10, pourcentageFractionne, diff, typeFrac);

        } else {
            // À partir de 3 sorties/semaine : alterner Endurance (séances paires) et AS (séances impaires)
            if (profil.getSortiesParSemaine() >= 3) {

                if (jour % 2 == 1) {
                    // Séance paire : Endurance tranquille
                    double km = computeEnduranceKmForWeek(semaine);
                    String nom = "Séance " + (jour + 1) + " - Endurance tranquille";
                    String corps = formatKmValue(km) + "km en endurance fondamentale";
                    return new Seance(nom, "Endurance Fondamentale", 5, corps, 5, pEF);
                }
            }
            
            // Gestion de l'allure spécifique selon la zone
            boolean inclureAS = doitInclureAllureSpecifique(semaine, zone);
            
            if (!inclureAS) {
                // Semaine sans AS: EF avec progression
                double km = computeEnduranceKmForWeekAndZone(semaine, zone);
                String nom = "Séance " + (jour + 1) + " - Endurance fondamentale";
                String corps = formatKmValue(km) + "km en endurance fondamentale";
                return new Seance(nom, "Endurance Fondamentale", 5, corps, 5, pEF);
            }
            
            // Séance Allure Spécifique
            String distanceLabel = (Math.abs(targetDistanceKm - Math.round(targetDistanceKm)) < 1e-6)
                    ? String.format(Locale.US, "%d", (int)Math.round(targetDistanceKm))
                    : String.format(Locale.US, "%.1f", targetDistanceKm);
            String nom = "Séance " + (jour + 1) + " - Allure spécifique (" + distanceLabel + "km)";

            CorpsDeSeance exAS = getNextASExercice();

            String corpsAS;
            double pourcentageAS = pSpec;
            double kmAS = 0.0;

            if (exAS != null) {
                corpsAS = exAS.getDescription();
                pourcentageAS = exAS.resolvePourcentageVMA(profil);
                kmAS = parseKmFromDescription(corpsAS);
            } else {
                kmAS = calculerDistanceProgressiveKm(30, semaine, pourcentageAS);
                corpsAS = formatKmValue(kmAS) + "km à allure " + title;
            }

            if (kmAS <= 0.0) {
                kmAS = calculerDistanceProgressiveKm(30, semaine, pourcentageAS);
            }

            Integer diff = (exAS != null) ? exAS.getDifficulte() : null;
            return new Seance(nom, "Allure Spécifique", 15, corpsAS, 10, pourcentageAS, diff, getTypeAllureSpecifique());
        }
    }


    private boolean doitInclureAllureSpecifique(int semaine, int zone) {
        if (zone == 1) {
            // Zone 1: jamais d'AS, uniquement EF avec progression
            return false;
        } else {
            // Zone 2: AS toutes les semaines
            return true;
        }
    }


    private CorpsDeSeance getNextASExercice() {
        String niveau = profil.getNiveau();
        if (niveau == null) niveau = "";
        String niv = niveau.toLowerCase();

        List<CorpsDeSeance> sequence;

        if (niv.contains("début") || niv.contains("debut") || niv.contains("debutant")) {
            sequence = asSequenceDebutant;
        } else if (niv.contains("avanc") || niv.contains("expert")) {
            sequence = asSequenceExpert;
        } else {
            // Moyen
            sequence = asSequenceMoyen;
        }

        if (sequence == null || sequence.isEmpty()) {
            return null;
        }

        // Pour expert (niveau 3), on boucle
        if (niv.contains("avanc") || niv.contains("expert")) {
            int index = asSequenceIndex % sequence.size();
            asSequenceIndex++;
            return sequence.get(index);
        } else {
            // Pour débutant et moyen, progression linéaire
            if (asSequenceIndex < sequence.size()) {
                return sequence.get(asSequenceIndex++);
            } else {
                // Si on dépasse, on prend le dernier
                return sequence.get(sequence.size() - 1);
            }
        }
    }

    private int calculerDureeProgressive(int dureeBase, int semaine) {
        double facteur = 1.0 + (semaine - 1) * 0.08;
        if (facteur > 1.8) {
            facteur = 1.8;
        }
        return (int) (dureeBase * facteur);
    }

    private double calculerDistanceProgressiveKm(int dureeBaseMinutes, int semaine, double percent) {
        int duree = calculerDureeProgressive(dureeBaseMinutes, semaine);
        double hours = duree / 60.0;
        double speed = profil.getVma() * percent;
        return speed * hours;
    }

    private double computeEnduranceKmForWeek(int semaine) {
        // base 5 km, +1 après 6 semaines, +1 après 12 semaines (max +2)
        int add = 0;
        if (semaine > 6) add++;
        if (semaine > 12) add++;
        return 5.0 + Math.min(add, 2);
    }
    

    private double computeEnduranceKmForWeekAndZone(int semaine, int zone) {
        String niveau = profil.getNiveau();
        if (niveau == null) niveau = "";
        String niv = niveau.toLowerCase();
        
        // Distance de base selon niveau
        double baseKm;
        boolean isDebutant = false;
        if (niv.contains("début") || niv.contains("debut") || niv.contains("debutant")) {
            baseKm = 5.0;
            isDebutant = true;
        } else if (niv.contains("avanc") || niv.contains("expert")) {
            baseKm = 15.0;
        } else {
            // Moyen/intermédiaire
            baseKm = 10.0;
        }
        
        if (zone == 1) {
            int nbSemainesAvant = 0;
            for (int s = 1; s < semaine; s++) {
                if (determinerZone(s) == 1) {
                    nbSemainesAvant++;
                }
            }
            
            double km;
            if (isDebutant) {
                // Débutant: +1km par semaine
                km = baseKm + nbSemainesAvant;
            } else {
                // Moyen/Expert: +10% par semaine
                km = baseKm * Math.pow(1.10, nbSemainesAvant);
            }
            return Math.min(km, 20.0); // Max 20km
        } else {
            // Zone 2: distance stable ou légère progression
            return Math.min(baseKm + 2.0, 20.0);
        }
    }

    private String formatKmValue(double km) {
        if (Math.abs(km - Math.round(km)) < 1e-6) {
            return String.format(Locale.US, "%d", (int) Math.round(km));
        }
        return String.format(Locale.US, "%.1f", km);
    }

    private double parseKmFromDescription(String desc) {
        if (desc == null) return 0.0;
        String s = desc.toLowerCase();

        // pattern reps x km  => e.g. "2 x 3km"
        Pattern pRepsKm = Pattern.compile("(\\d+)\\s*[x×]\\s*(\\d+(?:[.,]\\d+)?)\\s*km");
        Matcher m1 = pRepsKm.matcher(s);
        if (m1.find()) {
            try {
                int reps = Integer.parseInt(m1.group(1));
                double km = Double.parseDouble(m1.group(2).replace(',', '.'));
                return reps * km;
            } catch (Exception ignored) {}
        }

        // pattern single km e.g. "6km", "10 km en continu"
        Pattern pSingleKm = Pattern.compile("(\\d+(?:[.,]\\d+)?)\\s*km");
        Matcher m2 = pSingleKm.matcher(s);
        if (m2.find()) {
            try {
                return Double.parseDouble(m2.group(1).replace(',', '.'));
            } catch (Exception ignored) {}
        }

        return 0.0;
    }

    public String genererProgramme() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== PROGRAMME " + title.toUpperCase() + " ===\n\n");
        sb.append("Profil: ").append(profil.getNiveau()).append("\n");
        sb.append("Sorties: ").append(profil.getSortiesParSemaine()).append(" par semaine\n");
        sb.append("VMA: ").append(profil.getVma()).append(" km/h\n\n");

        for (int i = 0; i < semaines.size(); i++) {
            sb.append("--- SEMAINE ").append(i + 1);
            if ((i + 1) % 5 == 0) {
                sb.append(" (REPOS)");
            }
            sb.append(" ---\n\n");

            Seance[] semaine = semaines.get(i);
            for (Seance seance : semaine) {
                sb.append(seance.getDescription(profil));
                sb.append("\n");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    public List<Seance[]> getSemaines() {
        return semaines;
    }

    public double getDistanceKm() {
        return targetDistanceKm;
    }

    public String getTitle() {
        return title;
    }
    
    private CorpsDeSeance getNextExerciceEnOrdre(String type, int difficulte) {
        List<CorpsDeSeance> exercices = banque.getExercicesParDifficulte(type, difficulte);
        if (exercices == null || exercices.isEmpty()) {
            return null;
        }
        
        // Sélection aléatoire pour les fractionnés
        if (type.equals("Fractionné Court") || type.equals("Fractionné Long")) {
            int index = random.nextInt(exercices.size());
            return exercices.get(index);
        } else {
            // Pour les autres types (Tempo), prendre le premier
            return exercices.get(0);
        }
    }
}