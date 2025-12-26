import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SemiMarathon {
    private Profil profil;
    private List<Seance[]> semaines;
    private int nbSemaines;
    private static final double DISTANCE_SEMI = 21.1;

    private BanqueExercices banque;

    // suivi pour AS (ordre + cumul km)
    private int asSequenceIndex = 0;
    private int fractionneCycleIndex = 0;

    // Séquences prédéfinies pour AS selon niveau
    private List<CorpsDeSeance> asSequenceDebutant;
    private List<CorpsDeSeance> asSequenceMoyen;
    private List<CorpsDeSeance> asSequenceExpert;

    public SemiMarathon(Profil profil) {
        this(profil, 12);
    }

    public SemiMarathon(Profil profil, int nbSemaines) {
        this.profil = profil;
        this.semaines = new ArrayList<Seance[]>();
        this.banque = new BanqueExercices();
        this.nbSemaines = (nbSemaines > 0) ? nbSemaines : 12;
        initialiserSequencesAS();
        genererSemaines();
    }

    private void initialiserSequencesAS() {
        // exercices AS par difficulté depuis la banque
        List<CorpsDeSeance> niveau1 = banque.getExercicesParDifficulte("Allure Spécifique", 1);
        List<CorpsDeSeance> niveau2 = banque.getExercicesParDifficulte("Allure Spécifique", 2);
        List<CorpsDeSeance> niveau3 = banque.getExercicesParDifficulte("Allure Spécifique", 3);

        // Pour débutant: niveau 1 (12 exercices) puis niveau 2 (11 exercices)
        asSequenceDebutant = new ArrayList<>();
        if (niveau1 != null) asSequenceDebutant.addAll(niveau1);
        if (niveau2 != null) asSequenceDebutant.addAll(niveau2);

        // Pour moyen: niveau 2 (11 exercices) puis niveau 3 (8 exercices)
        asSequenceMoyen = new ArrayList<>();
        if (niveau2 != null) asSequenceMoyen.addAll(niveau2);
        if (niveau3 != null) asSequenceMoyen.addAll(niveau3);

        // Pour expert: niveau 3 en boucle (8 exercices)
        asSequenceExpert = new ArrayList<>();
        if (niveau3 != null) asSequenceExpert.addAll(niveau3);
    }

    private void genererSemaines() {
        for (int i = 0; i < nbSemaines; i++) {
            boolean semaineRepos = ((i + 1) % 5 == 0);
            Seance[] semaine = genererSemaine(i + 1, semaineRepos);
            semaines.add(semaine);
        }
    }

    private Seance[] genererSemaine(int numeroSemaine, boolean repos) {
        int nbSeances = profil.getSortiesParSemaine();
        if (repos) {
            return genererSemaineRepos(numeroSemaine, nbSeances);
        } else {
            Seance[] semaine = new Seance[nbSeances];
            for (int i = 0; i < nbSeances; i++) {
                semaine[i] = creerSeanceNormale(i, numeroSemaine);
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
        double[] p = profil.getPourcentagesPrincipales(DISTANCE_SEMI);
        double pEF = p[0];
        String nom = "Séance " + (jour + 1) + " (Repos)";
        double km = computeEnduranceKmForWeek(semaine);
        String corps = formatKmValue(km) + "km en endurance fondamentale";
        return new Seance(nom, "Endurance Fondamentale", 5, corps, 5, pEF);
    }

    private Seance creerSeanceTempo(int jour, int semaine) {
        String niveau = profil.getNiveau();
        int difficulte = niveauToDifficulte(niveau);

        CorpsDeSeance ex = banque.getExerciceAleatoire("Tempo", difficulte);

        double pSeuil;
        String corps;

        if (ex != null) {
            corps = ex.getDescription();
            pSeuil = ex.resolvePourcentageVMA(profil);
        } else {
            double[] p = profil.getPourcentagesPrincipales(DISTANCE_SEMI);
            pSeuil = p[2];
            int dureeMin = calculerDureeProgressive(25, semaine);
            corps = dureeMin + "min en tempo confortable (pas trop dur)";
        }

        String nom = "Séance " + (jour + 1) + " - Tempo confortable";
        return new Seance(nom, "Tempo", 10, corps, 5, pSeuil);
    }

    private int niveauToDifficulte(String niveau) {
        if (niveau == null) return 2;
        String s = niveau.toLowerCase();
        if (s.contains("début") || s.contains("debut") || s.contains("debutant")) return 1;
        if (s.contains("avanc") || s.contains("expert")) return 3;
        return 2;
    }

    private int choisirDifficulteFractionne(String niveau) {
        String s = niveau.toLowerCase();
        if (s.contains("début") || s.contains("debut") || s.contains("debutant") || s.contains("débutant")) return 1;
        if (s.contains("novice")) return 2;
        if (s.contains("avanc") || s.contains("expert")) return 3;
        return 2;
    }

    private boolean useFractionneLongNext() {
        boolean useLong = (fractionneCycleIndex % 3) < 2;
        fractionneCycleIndex++;
        return useLong;
    }

    private Seance creerSeanceNormale(int jour, int semaine) {
        String niveau = profil.getNiveau();

        double[] p = profil.getPourcentagesPrincipales(DISTANCE_SEMI);
        double pSpec = p[1];


        if (jour == 0) {
            // Séance 1 : Fractionné
            boolean useLong = useFractionneLongNext();
            String typeFrac = useLong ? "Fractionné Long" : "Fractionné Court";
            String nom = "Séance 1 - " + typeFrac;
            int difficulteFrac = choisirDifficulteFractionne(niveau);
            CorpsDeSeance ex = banque.getExerciceAleatoire(typeFrac, difficulteFrac);
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
            return new Seance(nom, typeFrac, 15, corps, 10, pourcentageFractionne);

        } else {
            // Séance 2 : Allure Spécifique (AS21)
            String nom = "Séance 2 - Allure spécifique (AS21)";

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
                corpsAS = formatKmValue(kmAS) + "km à allure semi-marathon";
            }

            if (kmAS <= 0.0) {
                kmAS = calculerDistanceProgressiveKm(30, semaine, pourcentageAS);
            }

            return new Seance(nom, "Allure Spécifique (AS21)", 15, corpsAS, 10, pourcentageAS);
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
        sb.append("=== PROGRAMME SEMI-MARATHON ===\n\n");
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
}