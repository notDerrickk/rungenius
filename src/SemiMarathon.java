import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SemiMarathon {
    private Profil profil;
    private List<Seance[]> semaines;
    private static final int NB_SEMAINES = 12;
    private static final double DISTANCE_SEMI = 21.1;

    private BanqueExercices banque; 

    public SemiMarathon(Profil profil) {
        this.profil = profil;
        this.semaines = new ArrayList<Seance[]>();
        this.banque = new BanqueExercices(); 
        genererSemaines();
    }

    private void genererSemaines() {
        for (int i = 0; i < NB_SEMAINES; i++) {
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
                semaine[i] = creerSeanceRepos(i);
            }
        } else {
            for (int i = 0; i < nbSeances; i++) {
                semaine[i] = creerSeanceNormale(i, numeroSemaine);
            }
        }

        return semaine;
    }

    private Seance creerSeanceRepos(int jour) {
        double[] p = profil.getPourcentagesPrincipales(DISTANCE_SEMI);
        double pEF = p[0];
        String nom = "Séance " + (jour + 1) + " (Repos)";
        String corps = "30 min en endurance fondamentale";
        return new Seance(nom, "Endurance Fondamentale", 10, corps, 5, pEF);
    }

    private int niveauToDifficulte(String niveau) {
        if (niveau == null) return 2;
        String s = niveau.toLowerCase();
        if (s.contains("début") || s.contains("debut") || s.contains("debutant")) return 1;
        if (s.contains("avanc") || s.contains("expert")) return 3;
        return 2;
    }

    
    // Choisit une difficulté pour les séances de fractionné selon le niveau :
    // - Débutant  : 60% diff 1, 40% diff 2
    // - Medium    : 80% diff 2, 20% diff 3
    // - Expert    : 50% diff 2, 50% diff 3
    private int choisirDifficulteFractionne(String niveau) {
        String s = (niveau == null) ? "" : niveau.toLowerCase();
        Random rnd = new Random();
        double r = rnd.nextDouble();
        if (s.contains("début") || s.contains("debut") || s.contains("debutant")) {
            return (r < 0.6) ? 1 : 2;
        } else if (s.contains("avanc") || s.contains("expert")) {
            return (r < 0.5) ? 2 : 3;
        } else {
            // medium / par défaut
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
            int duree = calculerDureeProgressive(40, semaine);
            String corps = duree + " min en endurance fondamentale";
            return new Seance(nom, "Endurance Fondamentale", 10, corps, 5, pEF);
        } else if (jour == 1) {
            String nom = "Séance 2 - Fractionné";
            int difficulteFrac = choisirDifficulteFractionne(niveau);
            CorpsDeSeance ex = banque.getExerciceAleatoire("Fractionné Court", difficulteFrac);
            String corps;
            double pourcentageFractionne;
            if (ex != null) {
                corps = ex.getDescription();
                pourcentageFractionne = ex.resolvePourcentageVMA(profil); // derive % from allure (or AS)
            } else {
                corps = "8 x 400m récup 1min";
                pourcentageFractionne = 0.95; // fallback
            }
            return new Seance(nom, "Fractionné Court", 15, corps, 10, pourcentageFractionne);

        } else {
            String nom = "Séance " + (jour + 1) + " - Allure spécifique (AS21)";
            CorpsDeSeance exAS = banque.getExerciceAleatoire("Allure Spécifique (AS21)", difficulteGenerale);
            String corpsAS;
            double pourcentageAS = pSpec; // fallback
            if (exAS != null) {
                corpsAS = exAS.getDescription();
                pourcentageAS = exAS.resolvePourcentageVMA(profil);
            } else {
                int duree = calculerDureeProgressive(30, semaine);
                corpsAS = duree + " min à allure semi-marathon";
            }
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