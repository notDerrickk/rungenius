import java.util.ArrayList;
import java.util.List;

public class SemiMarathon {
    private Profil profil;
    private List<Seance[]> semaines;
    private static final int NB_SEMAINES = 12;
    private static final double DISTANCE_SEMI = 21.1;
    
    public SemiMarathon(Profil profil) {
        this.profil = profil;
        this.semaines = new ArrayList<Seance[]>();
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
            // Semaine de repos :  volume réduit, intensité faible
            for (int i = 0; i < nbSeances; i++) {
                semaine[i] = creerSeanceRepos(i);
            }
        } else {
            // Semaine normale
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
    
    private Seance creerSeanceNormale(int jour, int semaine) {
        String niveau = profil.getNiveau();
        double[] p = profil.getPourcentagesPrincipales(DISTANCE_SEMI);
        double pEF = p[0];
        double pSpec = p[1];
        double pSeuil = p[2];
        double pVMA = p[3];
        
        if (jour == 0) {
            // Première séance :  endurance
            String nom = "Séance 1 - Endurance";
            int duree = calculerDureeProgressive(40, semaine);
            String corps = duree + " min en endurance fondamentale";
            return new Seance(nom, "Endurance Fondamentale", 10, corps, 5, pEF);
        } else if (jour == 1) {
            // Deuxième séance : fractionné (proche de la VMA)
            String nom = "Séance 2 - Fractionné";
            String corps = "8 x 400m récup 1min";
            double pourcentageFractionne = pVMA * 0.95; // 95% de la VMA
            return new Seance(nom, "Fractionné Court", 15, corps, 10, pourcentageFractionne);
        } else {
            // Autres séances :  allure spécifique
            String nom = "Séance " + (jour + 1) + " - Allure spécifique";
            int duree = calculerDureeProgressive(30, semaine);
            String corps = duree + " min à allure semi-marathon";
            return new Seance(nom, "Allure Spécifique", 15, corps, 10, pSpec);
        }
    }
    
    private int calculerDureeProgressive(int dureeBase, int semaine) {
        // Progression de 10% maximum par semaine
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