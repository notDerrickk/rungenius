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
        this. dureeCooldown = dureeCooldown;
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
        
        if (dureeEchauffement > 0) {
            sb.append("Échauffement: ").append(dureeEchauffement).append(" min (");
            sb.append(profil.getAllureFormatee(0.70)).append(")\n");
        }
        
        sb.append("Corps: ").append(corps).append("\n");
        sb.append("Allure: ").append(profil.getAllureFormatee(pourcentageVMA)).append("\n");
        
        if (dureeCooldown > 0) {
            sb.append("Cooldown: ").append(dureeCooldown).append(" min (");
            sb.append(profil.getAllureFormatee(0.70)).append(")\n");
        }
        
        return sb. toString();
    }
    
    public int getDureeTotal() {
        return dureeEchauffement + dureeCooldown + extraireMinutes(corps);
    }
    
    private int extraireMinutes(String description) {
        // Extraction simple - à améliorer selon le format
        return 30; // Valeur par défaut
    }
    
    public double getPourcentageVMA() {
        return pourcentageVMA;
    }
}