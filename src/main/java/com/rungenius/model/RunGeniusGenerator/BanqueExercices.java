package com.rungenius.model.RunGeniusGenerator;
import java.util.*;

public class BanqueExercices {
    public Map<String, List<CorpsDeSeance>> exercicesParType;
    
    public BanqueExercices() {
        exercicesParType = new HashMap<>();
        initialiserExercices();
    }
    
    public void initialiserExercices() {
        // FRACTIONNÉ COURT (VMA, 95-100% VMA)
        List<CorpsDeSeance> fracCourt = new ArrayList<>();
        // Niveau 1 - Facile
        fracCourt.add(new CorpsDeSeance("6 x 400m récup 1min15",CorpsDeSeance.AllureType.PERCENT_VMA, 0.95, 1));
        fracCourt.add(new CorpsDeSeance("8 x 300m récup 1min", CorpsDeSeance.AllureType.PERCENT_VMA, 0.95, 1));
        fracCourt.add(new CorpsDeSeance("5 x 500m récup 2min", CorpsDeSeance.AllureType.PERCENT_VMA, 0.95, 1));
        fracCourt.add(new CorpsDeSeance("10 x 200m récup 45sec", CorpsDeSeance.AllureType.PERCENT_VMA, 0.98, 1));
        // Niveau 2 - Moyen
        fracCourt.add(new CorpsDeSeance("8 x 400m récup 1min", CorpsDeSeance.AllureType.PERCENT_VMA, 0.95, 2));
        fracCourt.add(new CorpsDeSeance("10 x 300m récup 1min", CorpsDeSeance.AllureType.PERCENT_VMA, 0.95, 2));
        fracCourt.add(new CorpsDeSeance("6 x 500m récup 1min30", CorpsDeSeance.AllureType.PERCENT_VMA, 0.95, 2));
        fracCourt.add(new CorpsDeSeance("12 x 200m récup 30sec", CorpsDeSeance.AllureType.PERCENT_VMA, 1.00, 2));
        // Niveau 3 - Difficile
        fracCourt.add(new CorpsDeSeance("10 x 400m récup 1min", CorpsDeSeance.AllureType.PERCENT_VMA, 0.95, 3));
        fracCourt.add(new CorpsDeSeance("12 x 300m récup 45sec", CorpsDeSeance.AllureType.PERCENT_VMA, 0.98, 3));
        fracCourt.add(new CorpsDeSeance("8 x 400m récup 30sec (court)", CorpsDeSeance.AllureType.PERCENT_VMA, 0.95, 3));
        fracCourt.add(new CorpsDeSeance("6 x 600m récup 1min30", CorpsDeSeance.AllureType.PERCENT_VMA, 0.95, 3));
        fracCourt.add(new CorpsDeSeance("20 x 200m récup 30sec", CorpsDeSeance.AllureType.PERCENT_VMA, 0.98, 3));
        fracCourt.add(new CorpsDeSeance("10 x 200m récup 30sec", CorpsDeSeance.AllureType.PERCENT_VMA, 1.00, 3));

        exercicesParType.put("Fractionné Court", fracCourt);
        
        // FRACTIONNÉ LONG
        List<CorpsDeSeance> fracLong = new ArrayList<>();

        // Niveau 1 - Facile
        fracLong.add(new CorpsDeSeance("4 x 1000m récup 2min30", CorpsDeSeance.AllureType.PERCENT_VMA, 0.90, 1));
        fracLong.add(new CorpsDeSeance("3 x 1500m récup 3min", CorpsDeSeance.AllureType.PERCENT_VMA, 0.88, 1));
        fracLong.add(new CorpsDeSeance("6 x 800m récup 2min", CorpsDeSeance.AllureType.PERCENT_VMA, 0.91, 1));
        fracLong.add(new CorpsDeSeance("2 x 2000m récup 4min", CorpsDeSeance.AllureType.PERCENT_VMA, 0.86, 1));

        // Niveau 2 - Moyen
        fracLong.add(new CorpsDeSeance("5 x 1000m récup 2min", CorpsDeSeance.AllureType.PERCENT_VMA, 0.91, 2));
        fracLong.add(new CorpsDeSeance("4 x 1500m récup 3min", CorpsDeSeance.AllureType.PERCENT_VMA, 0.89, 2));
        fracLong.add(new CorpsDeSeance("8 x 800m récup 90sec", CorpsDeSeance.AllureType.PERCENT_VMA, 0.92, 2));
        fracLong.add(new CorpsDeSeance("5 x 1200m récup 2min", CorpsDeSeance.AllureType.PERCENT_VMA, 0.89, 2));
        fracLong.add(new CorpsDeSeance("10 x 600m récup 90sec", CorpsDeSeance.AllureType.PERCENT_VMA, 0.92, 2));

        // Niveau 3 - Difficile
        fracLong.add(new CorpsDeSeance("6 x 1000m récup 90sec", CorpsDeSeance.AllureType.PERCENT_VMA, 0.92, 3));
        fracLong.add(new CorpsDeSeance("3 x 2000m récup 3min", CorpsDeSeance.AllureType.PERCENT_VMA, 0.89, 3));
        fracLong.add(new CorpsDeSeance("4 x 1600m récup 2min", CorpsDeSeance.AllureType.PERCENT_VMA, 0.90, 3));
        fracLong.add(new CorpsDeSeance("2 x 3000m récup 4min", CorpsDeSeance.AllureType.PERCENT_VMA, 0.88, 3));

        exercicesParType.put("Fractionné Long", fracLong);

        
        // SEUIL
        List<CorpsDeSeance> seuil = new ArrayList<>();

        // Niveau 1 - Facile
        seuil.add(new CorpsDeSeance("2 x 10min récup 3min", CorpsDeSeance.AllureType.SEUIL, 0, 1));
        seuil.add(new CorpsDeSeance("3 x 8min récup 2min30", CorpsDeSeance.AllureType.SEUIL, 0, 1));
        seuil.add(new CorpsDeSeance("20min en continu au seuil", CorpsDeSeance.AllureType.SEUIL, 0, 1));
        seuil.add(new CorpsDeSeance("4 x 6min récup 2min", CorpsDeSeance.AllureType.SEUIL, 0, 1));

        // Niveau 2 - Moyen
        seuil.add(new CorpsDeSeance("3 x 10min récup 3min", CorpsDeSeance.AllureType.SEUIL, 0, 2));
        seuil.add(new CorpsDeSeance("2 x 15min récup 4min", CorpsDeSeance.AllureType.SEUIL, 0, 2));
        seuil.add(new CorpsDeSeance("4 x 8min récup 2min", CorpsDeSeance.AllureType.SEUIL, 0, 2));
        seuil.add(new CorpsDeSeance("30min en continu au seuil", CorpsDeSeance.AllureType.SEUIL, 0, 2));
        seuil.add(new CorpsDeSeance("5 x 6min récup 2min", CorpsDeSeance.AllureType.SEUIL, 0, 2));

        // Niveau 3 - Difficile
        seuil.add(new CorpsDeSeance("3 x 12min récup 3min", CorpsDeSeance.AllureType.SEUIL, 0, 3));
        seuil.add(new CorpsDeSeance("40min en continu au seuil", CorpsDeSeance.AllureType.SEUIL, 0, 3));
        seuil.add(new CorpsDeSeance("2 x 20min récup 4min", CorpsDeSeance.AllureType.SEUIL, 0, 3));
        seuil.add(new CorpsDeSeance("6 x 5min récup 90sec", CorpsDeSeance.AllureType.SEUIL, 0, 3));
        seuil.add(new CorpsDeSeance("3 x 15min récup 3min", CorpsDeSeance.AllureType.SEUIL, 0, 3));

        exercicesParType.put("Seuil", seuil);

        
        // ALLURE SPÉCIFIQUE (pour semi-marathon)
        List<CorpsDeSeance> allureSpecAS21 = new ArrayList<>();

        // Niveau 1 - Facile (jusqu’à 10km AS)
        allureSpecAS21.add(new CorpsDeSeance("3 x 1km à allure semi récup 2min", CorpsDeSeance.AllureType.AS, 21.1, 1));
        allureSpecAS21.add(new CorpsDeSeance("4 x 1km à allure semi récup 2min", CorpsDeSeance.AllureType.AS, 21.1, 1));
        allureSpecAS21.add(new CorpsDeSeance("5 x 1km à allure semi récup 90sec", CorpsDeSeance.AllureType.AS, 21.1, 1));

        allureSpecAS21.add(new CorpsDeSeance("2 x 2km à allure semi récup 2min", CorpsDeSeance.AllureType.AS, 21.1, 1));
        allureSpecAS21.add(new CorpsDeSeance("3 x 2km à allure semi récup 2min", CorpsDeSeance.AllureType.AS, 21.1, 1));
        allureSpecAS21.add(new CorpsDeSeance("4 x 2km à allure semi récup 2min", CorpsDeSeance.AllureType.AS, 21.1, 1));

        allureSpecAS21.add(new CorpsDeSeance("2 x 3km à allure semi récup 3min", CorpsDeSeance.AllureType.AS, 21.1, 1));
        allureSpecAS21.add(new CorpsDeSeance("3 x 3km à allure semi récup 2min", CorpsDeSeance.AllureType.AS, 21.1, 1));

        allureSpecAS21.add(new CorpsDeSeance("2 x 4km à allure semi récup 3min", CorpsDeSeance.AllureType.AS, 21.1, 1));

        allureSpecAS21.add(new CorpsDeSeance("6km en continu à allure semi", CorpsDeSeance.AllureType.AS, 21.1, 1));
        allureSpecAS21.add(new CorpsDeSeance("8km en continu à allure semi", CorpsDeSeance.AllureType.AS, 21.1, 1));
        allureSpecAS21.add(new CorpsDeSeance("10km en continu à allure semi", CorpsDeSeance.AllureType.AS, 21.1, 1));

        // Niveau 2 - Moyen (jusqu’à 14km AS)
        allureSpecAS21.add(new CorpsDeSeance("4 x 2km à allure semi récup 2min", CorpsDeSeance.AllureType.AS, 21.1, 2));
        allureSpecAS21.add(new CorpsDeSeance("5 x 2km à allure semi récup 2min", CorpsDeSeance.AllureType.AS, 21.1, 2));
        allureSpecAS21.add(new CorpsDeSeance("6 x 2km à allure semi récup 90sec", CorpsDeSeance.AllureType.AS, 21.1, 2));

        allureSpecAS21.add(new CorpsDeSeance("3 x 3km à allure semi récup 2min", CorpsDeSeance.AllureType.AS, 21.1, 2));
        allureSpecAS21.add(new CorpsDeSeance("4 x 3km à allure semi récup 2min", CorpsDeSeance.AllureType.AS, 21.1, 2));

        allureSpecAS21.add(new CorpsDeSeance("2 x 4km à allure semi récup 3min", CorpsDeSeance.AllureType.AS, 21.1, 2));
        allureSpecAS21.add(new CorpsDeSeance("3 x 4km à allure semi récup 2min", CorpsDeSeance.AllureType.AS, 21.1, 2));

        allureSpecAS21.add(new CorpsDeSeance("2 x 5km à allure semi récup 3min", CorpsDeSeance.AllureType.AS, 21.1, 2));

        allureSpecAS21.add(new CorpsDeSeance("10km en continu à allure semi", CorpsDeSeance.AllureType.AS, 21.1, 2));
        allureSpecAS21.add(new CorpsDeSeance("12km en continu à allure semi", CorpsDeSeance.AllureType.AS, 21.1, 2));
        allureSpecAS21.add(new CorpsDeSeance("14km en continu à allure semi", CorpsDeSeance.AllureType.AS, 21.1, 2));

        // Niveau 3 - Difficile (jusqu’à 18–19km AS)
        allureSpecAS21.add(new CorpsDeSeance("4 x 3km à allure semi récup 2min", CorpsDeSeance.AllureType.AS, 21.1, 3));
        allureSpecAS21.add(new CorpsDeSeance("5 x 3km à allure semi récup 2min", CorpsDeSeance.AllureType.AS, 21.1, 3));

        allureSpecAS21.add(new CorpsDeSeance("3 x 4km à allure semi récup 2min", CorpsDeSeance.AllureType.AS, 21.1, 3));
        allureSpecAS21.add(new CorpsDeSeance("4 x 4km à allure semi récup 2min", CorpsDeSeance.AllureType.AS, 21.1, 3));

        allureSpecAS21.add(new CorpsDeSeance("2 x 5km à allure semi récup 3min", CorpsDeSeance.AllureType.AS, 21.1, 3));
        allureSpecAS21.add(new CorpsDeSeance("2 x 6km à allure semi récup 3min", CorpsDeSeance.AllureType.AS, 21.1, 3));

        allureSpecAS21.add(new CorpsDeSeance("16km en continu à allure semi", CorpsDeSeance.AllureType.AS, 21.1, 3));
        allureSpecAS21.add(new CorpsDeSeance("18km en continu à allure semi", CorpsDeSeance.AllureType.AS, 21.1, 3));

        exercicesParType.put("Allure Spécifique", allureSpecAS21);

        // ALLURE SPÉCIFIQUE 10 KM
        List<CorpsDeSeance> allureSpecAS10 = new ArrayList<>();

        // Niveau 1 - Facile (jusqu’à 5-6 km AS)
        allureSpecAS10.add(new CorpsDeSeance("4 x 1km à allure 10k récup 2min", CorpsDeSeance.AllureType.AS, 10.0, 1));
        allureSpecAS10.add(new CorpsDeSeance("5 x 1km à allure 10k récup 90sec", CorpsDeSeance.AllureType.AS, 10.0, 1));
        allureSpecAS10.add(new CorpsDeSeance("3 x 2km à allure 10k récup 2min", CorpsDeSeance.AllureType.AS, 10.0, 1));
        allureSpecAS10.add(new CorpsDeSeance("2 x 3km à allure 10k récup 3min", CorpsDeSeance.AllureType.AS, 10.0, 1));
        allureSpecAS10.add(new CorpsDeSeance("6 x 800m à allure 10k récup 1min30", CorpsDeSeance.AllureType.AS, 10.0, 1));
        allureSpecAS10.add(new CorpsDeSeance("8 x 600m à allure 10k récup 1min15", CorpsDeSeance.AllureType.AS, 10.0, 1));
        allureSpecAS10.add(new CorpsDeSeance("10 x 400m à allure 10k récup 1min", CorpsDeSeance.AllureType.AS, 10.0, 1));
        allureSpecAS10.add(new CorpsDeSeance("3 x 1,5km à allure 10k récup 2min", CorpsDeSeance.AllureType.AS, 10.0, 1));
        allureSpecAS10.add(new CorpsDeSeance("2 x 2,5km à allure 10k récup 3min", CorpsDeSeance.AllureType.AS, 10.0, 1));
        allureSpecAS10.add(new CorpsDeSeance("5km en continu à allure 10k", CorpsDeSeance.AllureType.AS, 10.0, 1));
        allureSpecAS10.add(new CorpsDeSeance("6km progressif jusqu’à allure 10k", CorpsDeSeance.AllureType.AS, 10.0, 1));
        allureSpecAS10.add(new CorpsDeSeance("4 x 1200m à allure 10k récup 90sec", CorpsDeSeance.AllureType.AS, 10.0, 1));

        // Niveau 2 - Moyen (jusqu’à ~8–9 km AS)
        allureSpecAS10.add(new CorpsDeSeance("5 x 1km à allure 10k récup 1min30", CorpsDeSeance.AllureType.AS, 10.0, 2));
        allureSpecAS10.add(new CorpsDeSeance("6 x 1km à allure 10k récup 1min15", CorpsDeSeance.AllureType.AS, 10.0, 2));
        allureSpecAS10.add(new CorpsDeSeance("4 x 2km à allure 10k récup 2min", CorpsDeSeance.AllureType.AS, 10.0, 2));
        allureSpecAS10.add(new CorpsDeSeance("3 x 3km à allure 10k récup 2min30", CorpsDeSeance.AllureType.AS, 10.0, 2));
        allureSpecAS10.add(new CorpsDeSeance("8 x 800m à allure 10k récup 1min15", CorpsDeSeance.AllureType.AS, 10.0, 2));
        allureSpecAS10.add(new CorpsDeSeance("10 x 600m à allure 10k récup 1min", CorpsDeSeance.AllureType.AS, 10.0, 2));
        allureSpecAS10.add(new CorpsDeSeance("12 x 400m à allure 10k récup 1min", CorpsDeSeance.AllureType.AS, 10.0, 2));
        allureSpecAS10.add(new CorpsDeSeance("2 x 4km à allure 10k récup 3min", CorpsDeSeance.AllureType.AS, 10.0, 2));
        allureSpecAS10.add(new CorpsDeSeance("3 x 2,5km à allure 10k récup 2min", CorpsDeSeance.AllureType.AS, 10.0, 2));
        allureSpecAS10.add(new CorpsDeSeance("5km en continu à allure 10k", CorpsDeSeance.AllureType.AS, 10.0, 2));
        allureSpecAS10.add(new CorpsDeSeance("6km en continu à allure 10k", CorpsDeSeance.AllureType.AS, 10.0, 2));
        allureSpecAS10.add(new CorpsDeSeance("4 x 1500m à allure 10k récup 1min30", CorpsDeSeance.AllureType.AS, 10.0, 2));

        // Niveau 3 - Difficile (jusqu’à 10 km AS)
        allureSpecAS10.add(new CorpsDeSeance("6 x 1km à allure 10k récup 1min", CorpsDeSeance.AllureType.AS, 10.0, 3));
        allureSpecAS10.add(new CorpsDeSeance("7 x 1km à allure 10k récup 1min", CorpsDeSeance.AllureType.AS, 10.0, 3));
        allureSpecAS10.add(new CorpsDeSeance("5 x 2km à allure 10k récup 2min", CorpsDeSeance.AllureType.AS, 10.0, 3));
        allureSpecAS10.add(new CorpsDeSeance("3 x 3km à allure 10k récup 2min", CorpsDeSeance.AllureType.AS, 10.0, 3));
        allureSpecAS10.add(new CorpsDeSeance("10 x 800m à allure 10k récup 1min", CorpsDeSeance.AllureType.AS, 10.0, 3));
        allureSpecAS10.add(new CorpsDeSeance("12 x 600m à allure 10k récup 1min", CorpsDeSeance.AllureType.AS, 10.0, 3));
        allureSpecAS10.add(new CorpsDeSeance("15 x 400m à allure 10k récup 1min", CorpsDeSeance.AllureType.AS, 10.0, 3));
        allureSpecAS10.add(new CorpsDeSeance("2 x 5km à allure 10k récup 3min", CorpsDeSeance.AllureType.AS, 10.0, 3));
        allureSpecAS10.add(new CorpsDeSeance("3 x 4km à allure 10k récup 2min", CorpsDeSeance.AllureType.AS, 10.0, 3));
        allureSpecAS10.add(new CorpsDeSeance("6km en continu à allure 10k", CorpsDeSeance.AllureType.AS, 10.0, 3));
        allureSpecAS10.add(new CorpsDeSeance("7km en continu à allure 10k", CorpsDeSeance.AllureType.AS, 10.0, 3));
        allureSpecAS10.add(new CorpsDeSeance("5km + 3km + 2km à allure 10k récup 2min", CorpsDeSeance.AllureType.AS, 10.0, 3));
        exercicesParType.put("Allure Spécifique 10km", allureSpecAS10);

        // ALLURE SPÉCIFIQUE 5 KM
        List<CorpsDeSeance> allureSpecAS5 = new ArrayList<>();

        // Niveau 1 - Facile 
        allureSpecAS5.add(new CorpsDeSeance("5 x 600m à allure 5k récup 1min30", CorpsDeSeance.AllureType.AS, 5.0, 1));
        allureSpecAS5.add(new CorpsDeSeance("6 x 500m à allure 5k récup 1min30", CorpsDeSeance.AllureType.AS, 5.0, 1));
        allureSpecAS5.add(new CorpsDeSeance("8 x 400m à allure 5k récup 1min15", CorpsDeSeance.AllureType.AS, 5.0, 1));
        allureSpecAS5.add(new CorpsDeSeance("10 x 300m à allure 5k récup 1min", CorpsDeSeance.AllureType.AS, 5.0, 1));
        allureSpecAS5.add(new CorpsDeSeance("3 x 1km à allure 5k récup 2min", CorpsDeSeance.AllureType.AS, 5.0, 1));
        allureSpecAS5.add(new CorpsDeSeance("2 x 1,5km à allure 5k récup 2min30", CorpsDeSeance.AllureType.AS, 5.0, 1));
        allureSpecAS5.add(new CorpsDeSeance("12 x 200m à allure 5k récup 45sec", CorpsDeSeance.AllureType.AS, 5.0, 1));
        allureSpecAS5.add(new CorpsDeSeance("4 x 800m à allure 5k récup 1min30", CorpsDeSeance.AllureType.AS, 5.0, 1));
        allureSpecAS5.add(new CorpsDeSeance("2km en continu à allure 5k", CorpsDeSeance.AllureType.AS, 5.0, 1));
        allureSpecAS5.add(new CorpsDeSeance("3km progressif jusqu’à allure 5k", CorpsDeSeance.AllureType.AS, 5.0, 1));

        // Niveau 2 - Moyen 
        allureSpecAS5.add(new CorpsDeSeance("5 x 600m à allure 5k récup 1min15", CorpsDeSeance.AllureType.AS, 5.0, 2));
        allureSpecAS5.add(new CorpsDeSeance("6 x 600m à allure 5k récup 1min15", CorpsDeSeance.AllureType.AS, 5.0, 2));
        allureSpecAS5.add(new CorpsDeSeance("8 x 500m à allure 5k récup 1min15", CorpsDeSeance.AllureType.AS, 5.0, 2));
        allureSpecAS5.add(new CorpsDeSeance("10 x 400m à allure 5k récup 1min", CorpsDeSeance.AllureType.AS, 5.0, 2));
        allureSpecAS5.add(new CorpsDeSeance("12 x 300m à allure 5k récup 1min", CorpsDeSeance.AllureType.AS, 5.0, 2));
        allureSpecAS5.add(new CorpsDeSeance("4 x 1km à allure 5k récup 2min", CorpsDeSeance.AllureType.AS, 5.0, 2));
        allureSpecAS5.add(new CorpsDeSeance("2 x 2km à allure 5k récup 3min", CorpsDeSeance.AllureType.AS, 5.0, 2));
        allureSpecAS5.add(new CorpsDeSeance("3km en continu à allure 5k", CorpsDeSeance.AllureType.AS, 5.0, 2));
        allureSpecAS5.add(new CorpsDeSeance("3 x 1200m à allure 5k récup 2min", CorpsDeSeance.AllureType.AS, 5.0, 2));
        allureSpecAS5.add(new CorpsDeSeance("6 x 800m à allure 5k récup 1min15", CorpsDeSeance.AllureType.AS, 5.0, 2));

        // Niveau 3 - Difficile 
        allureSpecAS5.add(new CorpsDeSeance("7 x 600m à allure 5k récup 1min", CorpsDeSeance.AllureType.AS, 5.0, 3));
        allureSpecAS5.add(new CorpsDeSeance("8 x 600m à allure 5k récup 1min", CorpsDeSeance.AllureType.AS, 5.0, 3));
        allureSpecAS5.add(new CorpsDeSeance("10 x 500m à allure 5k récup 1min", CorpsDeSeance.AllureType.AS, 5.0, 3));
        allureSpecAS5.add(new CorpsDeSeance("12 x 400m à allure 5k récup 1min", CorpsDeSeance.AllureType.AS, 5.0, 3));
        allureSpecAS5.add(new CorpsDeSeance("15 x 300m à allure 5k récup 1min", CorpsDeSeance.AllureType.AS, 5.0, 3));
        allureSpecAS5.add(new CorpsDeSeance("5 x 1km à allure 5k récup 1min30", CorpsDeSeance.AllureType.AS, 5.0, 3));
        allureSpecAS5.add(new CorpsDeSeance("3 x 2km à allure 5k récup 2min30", CorpsDeSeance.AllureType.AS, 5.0, 3));
        allureSpecAS5.add(new CorpsDeSeance("2 x 2,5km à allure 5k récup 3min", CorpsDeSeance.AllureType.AS, 5.0, 3));
        allureSpecAS5.add(new CorpsDeSeance("3km en continu à allure 5k", CorpsDeSeance.AllureType.AS, 5.0, 3));
        allureSpecAS5.add(new CorpsDeSeance("3km + 2km à allure 5k récup 2min", CorpsDeSeance.AllureType.AS, 5.0, 3));

        exercicesParType.put("Allure Spécifique 5km", allureSpecAS5);

        // TEMPO (80-85% VMA)
        List<CorpsDeSeance> tempo = new ArrayList<>();
        // Niveau 1 - Facile
        tempo.add(new CorpsDeSeance("30min en tempo", CorpsDeSeance.AllureType.PERCENT_VMA,0.80, 1));
        tempo.add(new CorpsDeSeance("2 x 3km récup 5min (EF) tempo", CorpsDeSeance.AllureType.PERCENT_VMA,0.82, 1));
        tempo.add(new CorpsDeSeance("3 x 2km récup 3min (EF) tempo", CorpsDeSeance.AllureType.PERCENT_VMA,0.82, 1));
        tempo.add(new CorpsDeSeance("35min en tempo continu", CorpsDeSeance.AllureType.PERCENT_VMA,0.80, 1));
        // Niveau 2 - Moyen
        tempo.add(new CorpsDeSeance("40min en tempo", CorpsDeSeance.AllureType.PERCENT_VMA,0.82, 2));
        tempo.add(new CorpsDeSeance("2 x 4km récup 5min (EF) tempo", CorpsDeSeance.AllureType.PERCENT_VMA,0.83, 2));
        tempo.add(new CorpsDeSeance("3 x 3km récup 3min (EF) tempo", CorpsDeSeance.AllureType.PERCENT_VMA,0.83, 2));
        tempo.add(new CorpsDeSeance("50min en tempo continu", CorpsDeSeance.AllureType.PERCENT_VMA,0.82, 2));
        tempo.add(new CorpsDeSeance("4 x 2km récup 2min (EF) tempo", CorpsDeSeance.AllureType.PERCENT_VMA,0.84, 2));
        // Niveau 3 - Difficile
        tempo.add(new CorpsDeSeance("60min en tempo continu", CorpsDeSeance.AllureType.PERCENT_VMA,0.82, 3));
        tempo.add(new CorpsDeSeance("3 x 4km récup 4min (EF) tempo", CorpsDeSeance.AllureType.PERCENT_VMA,0.83, 3));
        tempo.add(new CorpsDeSeance("2 x 5km récup 5min (EF) tempo", CorpsDeSeance.AllureType.PERCENT_VMA,0.84, 3));
        tempo.add(new CorpsDeSeance("5 x 2km récup 2min (EF) tempo", CorpsDeSeance.AllureType.PERCENT_VMA,0.84, 3));
        tempo.add(new CorpsDeSeance("70min en tempo progressif", CorpsDeSeance.AllureType.PERCENT_VMA,0.82, 3));
        exercicesParType.put("Tempo", tempo);
        

    }
    // Récupère un exercice aléatoire pour un type de séance donné
    public CorpsDeSeance getExerciceAleatoire(String typeSeance) {
        List<CorpsDeSeance> exercices = exercicesParType.get(typeSeance);
        if (exercices == null || exercices.isEmpty()) {
            return null;
        }
        Random rand = new Random();
        return exercices.get(rand.nextInt(exercices.size()));
    }
    
    // Récupère un exercice aléatoire avec une difficulté spécifique
    public CorpsDeSeance getExerciceAleatoire(String typeSeance, int difficulte) {
        List<CorpsDeSeance> exercices = getExercicesParDifficulte(typeSeance, difficulte);
        if (exercices.isEmpty()) {
            return null;
        }
        Random rand = new Random();
        return exercices.get(rand.nextInt(exercices.size()));
    }
    
    // Récupère un exercice spécifique par son index
    public CorpsDeSeance getExercice(String typeSeance, int index) {
        List<CorpsDeSeance> exercices = exercicesParType.get(typeSeance);
        if (exercices == null || index < 0 || index >= exercices.size()) {
            return null;
        }
        return exercices.get(index);
    }
    
    // Récupère tous les exercices d'un type
    public List<CorpsDeSeance> getExercices(String typeSeance) {
        return exercicesParType.get(typeSeance);
    }
    
    // Récupère les exercices d'un type filtré par difficulté
    public List<CorpsDeSeance> getExercicesParDifficulte(String typeSeance, int difficulte) {
        List<CorpsDeSeance> exercices = exercicesParType.get(typeSeance);
        List<CorpsDeSeance> resultat = new ArrayList<>();
        if (exercices != null) {
            for (CorpsDeSeance ex : exercices) {
                if (ex.getDifficulte() == difficulte) {
                    resultat.add(ex);
                }
            }
        }
        return resultat;
    }
    
    // Récupère tous les types de séances disponibles
    public Set<String> getTypesSeances() {
        return exercicesParType.keySet();
    }
    
    // Moyenne du pourcentage VMA pour un type, en résolvant AS via Profil
    public double getAllurePrincipale(String typeSeance, Profil profil) {
        List<CorpsDeSeance> exercices = exercicesParType.get(typeSeance);
        if (exercices == null || exercices.isEmpty()) return 0.85;
        double somme = 0;
        for (CorpsDeSeance ex : exercices) {
            somme += ex.resolvePourcentageVMA(profil);
        }
        return somme / exercices.size();
    }
    
    // Affichage avec allure formatée via Profil 
    public String afficherTousLesExercices(Profil profil) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== BANQUE D'EXERCICES ===\n\n");
        for (String type : exercicesParType.keySet()) {
            sb.append("--- ").append(type).append(" ---\n");
            double p = getAllurePrincipale(type, profil);
            sb.append("Allure principale: ").append(profil.getAllureFormatee(p)).append("\n\n");
            for (int diff = 1; diff <= 3; diff++) {
                List<CorpsDeSeance> exs = getExercicesParDifficulte(type, diff);
                if (!exs.isEmpty()) {
                    sb.append("  Niveau ").append(diff).append(":\n");
                    for (CorpsDeSeance ex : exs) {
                        sb.append("    • ").append(ex.getDescription())
                          .append(" (").append(ex.getAllureFormatee(profil)).append(")\n");
                    }
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }
}