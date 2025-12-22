import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
    private double cumulativeASKm = 0.0;
    private int asSequenceIndex = 0;

    public SemiMarathon(Profil profil) {
        this(profil, 12);
    }

    public SemiMarathon(Profil profil, int nbSemaines) {
        this.profil = profil;
        this.semaines = new ArrayList<Seance[]>();
        this.banque = new BanqueExercices();
        this.nbSemaines = (nbSemaines > 0) ? nbSemaines : 12;
        genererSemaines();
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
        Seance[] semaine = new Seance[nbSeances];

        if (repos) {
            for (int i = 0; i < nbSeances; i++) {
                semaine[i] = creerSeanceRepos(i, numeroSemaine);
            }
        } else {
            for (int i = 0; i < nbSeances; i++) {
                semaine[i] = creerSeanceNormale(i, numeroSemaine);
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

    private int niveauToDifficulte(String niveau) {
        if (niveau == null) return 2;
        String s = niveau.toLowerCase();
        if (s.contains("début") || s.contains("debut") || s.contains("debutant")) return 1;
        if (s.contains("avanc") || s.contains("expert")) return 3;
        return 2;
    }

    private int choisirDifficulteFractionne(String niveau) {
        String s = (niveau == null) ? "" : niveau.toLowerCase();
        Random rnd = new Random();
        double r = rnd.nextDouble();
        if (s.contains("début") || s.contains("debut") || s.contains("debutant")) {
            return (r < 0.6) ? 1 : 2;
        } else if (s.contains("avanc") || s.contains("expert")) {
            return (r < 0.5) ? 2 : 3;
        } else {
            return (r < 0.8) ? 2 : 3;
        }
    }

    private Seance creerSeanceNormale(int jour, int semaine) {
        String niveau = profil.getNiveau();
        int difficulteGenerale = niveauToDifficulte(niveau);

        double[] p = profil.getPourcentagesPrincipales(DISTANCE_SEMI);
        double pEF = p[0];
        double pSpec = p[1];
        double pSeuil = p[2];
        double pVMA = p[3];

        if (jour == 0) {
            String nom = "Séance 1 - Endurance";
            double km = computeEnduranceKmForWeek(semaine);
            String corps = formatKmValue(km) + "km en endurance fondamentale";
            return new Seance(nom, "Endurance Fondamentale", 5, corps, 5, pEF);
        } else if (jour == 1) {
            String nom = "Séance 2 - Fractionné";
            int difficulteFrac = choisirDifficulteFractionne(niveau);
            CorpsDeSeance ex = banque.getExerciceAleatoire("Fractionné Court", difficulteFrac);
            String corps;
            double pourcentageFractionne;
            if (ex != null) {
                corps = ex.getDescription();
                pourcentageFractionne = ex.resolvePourcentageVMA(profil);
            } else {
                corps = "8 x 400m récup 1min";
                pourcentageFractionne = 0.95;
            }
            return new Seance(nom, "Fractionné Court", 15, corps, 10, pourcentageFractionne);

        } else {
            String nom = "Séance " + (jour + 1) + " - Allure spécifique (AS21)";

            // détermination de difficulté selon cumul AS parcouru
            int chosenDiff = determineASDifficulty();
            List<CorpsDeSeance> candidats = banque.getExercicesParDifficulte("Allure Spécifique", chosenDiff);
            CorpsDeSeance exAS = null;
            if (candidats != null && !candidats.isEmpty()) {
                exAS = candidats.get(asSequenceIndex % candidats.size());
                asSequenceIndex++;
            }

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

            // si on n'a pas trouvé de km dans la description, estimer depuis %VMA et durée fallback
            if (kmAS <= 0.0) {
                kmAS = calculerDistanceProgressiveKm(30, semaine, pourcentageAS);
            }

            cumulativeASKm += kmAS;
            return new Seance(nom, "Allure Spécifique (AS21)", 15, corpsAS, 10, pourcentageAS);
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

    private int determineASDifficulty() {
        if (cumulativeASKm < 10.0) return 1;
        if (cumulativeASKm < 14.0) return 2;
        return 3;
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