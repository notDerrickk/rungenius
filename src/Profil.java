import java.io.*;

public class Profil {
    private String niveau;
    private int sortiesParSemaine;
    private double vma;
    private Integer objectifSeconds; 

    public Profil(String niveau, int sortiesParSemaine, double vma) {
        this(niveau, sortiesParSemaine, vma, null);
    }

    public Profil(String niveau, int sortiesParSemaine, double vma, Integer objectifSeconds) {
        this.niveau = niveau;
        this.sortiesParSemaine = sortiesParSemaine;
        this.vma = vma;
        this.objectifSeconds = objectifSeconds;
    }

    public String getNiveau() { return niveau; }
    public int getSortiesParSemaine() { return sortiesParSemaine; }
    public double getVma() { return vma; }

    public Integer getObjectifSeconds() { return objectifSeconds; }
    public void setObjectifSeconds(Integer s) { this.objectifSeconds = s; }

    public double getAllure(double pourcentageVMA) {
        double vitesseKmH = vma * pourcentageVMA;
        double allureMinKm = 60.0 / vitesseKmH;
        return allureMinKm;
    }

    public String getAllureFormatee(double pourcentageVMA) {
        double allure = getAllure(pourcentageVMA);
        int minutes = (int) allure;
        int secondes = (int) ((allure - minutes) * 60);
        return String.format("%d:%02d min/km", minutes, secondes);
    }

    public double getAllureFromObjectif(double distanceKm) {
        if (objectifSeconds == null || distanceKm <= 0) return getAllure(0.85); // fallback
        double secondsPerKm = objectifSeconds / distanceKm;
        double minutesPerKm = secondsPerKm / 60.0;
        return minutesPerKm;
    }

    public String getAllureObjectifFormatee(double distanceKm) {
        double allure = getAllureFromObjectif(distanceKm);
        int minutes = (int) allure;
        int secondes = (int) ((allure - minutes) * 60);
        return String.format("%d:%02d min/km", minutes, secondes);
    }

    public String[] getAlluresPrincipales(double distanceKm) {
        String allureEF = getAllureFormatee(0.65); 
        String allureSpecifique = getAllureObjectifFormatee(distanceKm); 
        String allureSeuil = getAllureFormatee(0.85); 
        String allureVMA = getAllureFormatee(1.0); 
        return new String[] { allureEF, allureSpecifique, allureSeuil, allureVMA };
    }

    public double getPourcentageFromAllure(double minutesPerKm) {
        if (minutesPerKm <= 0 || vma <= 0) return 0.85;
        return 60.0 / (vma * minutesPerKm);
    }

    public double getPourcentageFromObjectif(double distanceKm) {
        if (objectifSeconds == null || distanceKm <= 0) return 0.85;
        double secondsPerKm = objectifSeconds / distanceKm;
        double minutesPerKm = secondsPerKm / 60.0;
        return getPourcentageFromAllure(minutesPerKm);
    }

    public double[] getPourcentagesPrincipales(double distanceKm) {
        double pEF = 0.65;
        double pSpec = getPourcentageFromObjectif(distanceKm);
        double pSeuil = 0.85;
        double pVMA = 1.0;
        return new double[] { pEF, pSpec, pSeuil, pVMA };
    }

    public void sauvegarder(String filename) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(filename));
        writer.println(niveau);
        writer.println(sortiesParSemaine);
        writer.println(vma);
        writer.println(objectifSeconds == null ? "" : objectifSeconds);
        writer.close();
    }

    public static Profil charger(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String niveau = reader.readLine();
        int sorties = Integer.parseInt(reader.readLine());
        double vma = Double.parseDouble(reader.readLine());
        String objLine = reader.readLine();
        Integer objectif = null;
        if (objLine != null && !objLine.trim().isEmpty()) {
            objectif = Integer.parseInt(objLine.trim());
        }
        reader.close();
        return new Profil(niveau, sorties, vma, objectif);
    }
}