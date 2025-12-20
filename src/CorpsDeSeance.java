public class CorpsDeSeance {
    public enum AllureType { PERCENT_VMA, EF, SEUIL, AS }

    private String description;
    private AllureType allureType;
    private double value; 
    private int difficulte;

    public CorpsDeSeance(String description, AllureType allureType, double value, int difficulte) {
        this.description = description;
        this.allureType = allureType;
        this.value = value;
        this.difficulte = difficulte;
    }

    public String getDescription() { return description; }
    public AllureType getAllureType() { return allureType; }
    public double getValue() { return value; }
    public int getDifficulte() { return difficulte; }

    
    // Résout le pourcentage VMA correspondant à cet exercice en fonction du profil
    public double resolvePourcentageVMA(Profil profil) {
        if (allureType == AllureType.PERCENT_VMA) {
            return value;
        } else if (allureType == AllureType.EF) {
            return 0.65;
        } else if (allureType == AllureType.SEUIL) {
            return 0.85;
        } else if (allureType == AllureType.AS) {
            return profil.getPourcentageFromObjectif(value);
        }
        return 0.85;
    }

    // Retourne l'allure formatée (MM:SS min/km) correspondant à cet exercice pour le profil.
    public String getAllureFormatee(Profil profil) {
        if (allureType == AllureType.PERCENT_VMA) {
            return profil.getAllureFormatee(value);
        } else if (allureType == AllureType.EF) {
            return profil.getAllureEFFormatee();
        } else if (allureType == AllureType.SEUIL) {
            return profil.getAllureSeuilFormatee();
        } else if (allureType == AllureType.AS) {
            return profil.getAllureASFormatee(value);
        }
        return profil.getAllureFormatee(0.85);
    }
}