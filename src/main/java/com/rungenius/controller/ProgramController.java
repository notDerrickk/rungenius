package com.rungenius.controller;

import com.rungenius.model.RunGeniusGenerator.*;
import com.rungenius.model.RunGeniusEditor.ProgrammeCustom;
import com.rungenius.model.dto.ProgramDataDTO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
public class ProgramController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("today", LocalDate.now());
        return "index";
    }

    @GetMapping("/editor")
    public String editor(Model model) {
        model.addAttribute("today", LocalDate.now());
        return "editor";
    }

    @PostMapping("/editor/generate")
    public String generateCustomProgram(
            @RequestBody ProgramDataDTO programData,
            Model model
    ) {
        try {
            String title = programData.getTitle();
            double distanceKm = programData.getDistance();
            double vma = programData.getVma();
            String raceDate = programData.getRaceDate();
            
            ProgrammeCustom programme = new ProgrammeCustom(title, distanceKm);
            
            List<List<ProgramDataDTO.SeanceDTO>> weeks = programData.getWeeks();
            for (List<ProgramDataDTO.SeanceDTO> week : weeks) {
                List<Seance> seancesList = new ArrayList<>();
                for (ProgramDataDTO.SeanceDTO seanceDTO : week) {
                    String nom = seanceDTO.getNom();
                    String type = seanceDTO.getType();
                    int echauffement = seanceDTO.getEchauffement();
                    String corps = seanceDTO.getCorps();
                    int cooldown = seanceDTO.getCooldown();
                    
                    // Utiliser allurePct si disponible, sinon calculer depuis allureText
                    Double pctVMA = seanceDTO.getAllurePct();
                    if (pctVMA == null || pctVMA <= 0) {
                        pctVMA = 0.65; // Fallback
                    }
                    
                    Seance seance = new Seance(nom, type, echauffement, corps, cooldown, pctVMA);
                    seancesList.add(seance);
                }
                programme.addSemaine(seancesList.toArray(new Seance[0]));
            }
            
            // Créer un profil basique
            Profil profil = new Profil("Novice", programData.getNbSessions(), vma, null);
            
            double totalKm = estimerKilometragePrecis(programme, profil);
            String[] allures = profil.getAlluresPrincipales(distanceKm);
            
            List<Seance[]> semaines = programme.getSemaines();
            double[] weekTotals = new double[semaines.size()];
            int[][] seanceNumbers = new int[semaines.size()][];
            int globalSeanceNumber = 1;
            
            for (int i = 0; i < semaines.size(); i++) {
                Seance[] semaine = semaines.get(i);
                seanceNumbers[i] = new int[semaine.length];
                double weekTotal = 0.0;
                
                for (int j = 0; j < semaine.length; j++) {
                    seanceNumbers[i][j] = globalSeanceNumber++;
                    weekTotal += semaine[j].getDistanceKm(profil);
                }
                
                weekTotals[i] = weekTotal;
            }
            
            String distanceLabel = (Math.abs(distanceKm - Math.round(distanceKm)) < 1e-6)
                    ? String.format(Locale.US, "%d", (int)Math.round(distanceKm))
                    : String.format(Locale.US, "%.1f", distanceKm);
            
            model.addAttribute("programme", programme);
            model.addAttribute("profil", profil);
            model.addAttribute("raceType", "custom");
            model.addAttribute("objectif", "");
            model.addAttribute("raceDate", raceDate);
            model.addAttribute("totalKm", totalKm);
            model.addAttribute("allures", allures);
            model.addAttribute("distanceLabel", distanceLabel);
            model.addAttribute("weekTotals", weekTotals);
            model.addAttribute("seanceNumbers", seanceNumbers);
            
            return "result";
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Erreur lors de la génération: " + e.getMessage());
            model.addAttribute("today", LocalDate.now());
            return "editor";
        }
    }

    @PostMapping("/editor/download")
    public void downloadCustomProgram(
            @RequestBody ProgramDataDTO programData,
            HttpServletResponse response
    ) throws IOException {
        try {
            String title = programData.getTitle();
            double distanceKm = programData.getDistance();
            double vma = programData.getVma();
            
            ProgrammeCustom programme = new ProgrammeCustom(title, distanceKm);
            
            // Convertir les séances
            List<List<ProgramDataDTO.SeanceDTO>> weeks = programData.getWeeks();
            for (List<ProgramDataDTO.SeanceDTO> week : weeks) {
                List<Seance> seancesList = new ArrayList<>();
                for (ProgramDataDTO.SeanceDTO seanceDTO : week) {
                    Double pctVMA = seanceDTO.getAllurePct();
                    if (pctVMA == null || pctVMA <= 0) {
                        pctVMA = 0.65;
                    }
                    
                    Seance seance = new Seance(
                        seanceDTO.getNom(),
                        seanceDTO.getType(),
                        seanceDTO.getEchauffement(),
                        seanceDTO.getCorps(),
                        seanceDTO.getCooldown(),
                        pctVMA
                    );
                    seancesList.add(seance);
                }
                programme.addSemaine(seancesList.toArray(new Seance[0]));
            }
            
            Profil profil = new Profil("Novice", programData.getNbSessions(), vma, null);
            
            HtmlGenerator generator = new HtmlGenerator();
            String filename = generator.genererHTML(programme, profil);
            
            Path file = Path.of(filename);
            if (!Files.exists(file)) {
                response.sendError(500, "Fichier non généré");
                return;
            }
            
            response.setContentType("text/html; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getFileName().toString() + "\"");
            Files.copy(file, response.getOutputStream());
            response.getOutputStream().flush();
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Erreur: " + e.getMessage());
        }
    }

    @PostMapping("/generate")
    public String generateProgram(
            @RequestParam("raceType") String raceType,
            @RequestParam("niveau") String niveau,
            @RequestParam("sorties") int sorties,
            @RequestParam("vma") double vma,
            @RequestParam("objectif") String objectifStr,
            @RequestParam("raceDate") String raceDateStr,
            Model model
    ) {
        try {
            LocalDate raceDate = LocalDate.parse(raceDateStr);
            LocalDate today = LocalDate.now();
            long weeksBetween = ChronoUnit.WEEKS.between(today, raceDate);
            if (weeksBetween < 4) weeksBetween = 4;
            if (weeksBetween > 52) weeksBetween = 52;

            double distanceKm;
            String title;
            if ("5km".equals(raceType)) {
                distanceKm = 5.0;
                title = "Préparation 5 km";
            } else if ("10km".equals(raceType)) {
                distanceKm = 10.0;
                title = "Préparation 10 km";
            } else {
                distanceKm = 21.1;
                title = "Préparation Semi-Marathon";
            }

            Integer objectifSec = parseTimeToSeconds(objectifStr);
            Profil profil = new Profil(niveau, sorties, vma, objectifSec);

            Programme programme;
            if ("5km".equals(raceType)) {
                programme = new Prepa5k(profil, (int) weeksBetween, distanceKm, title);
            } else if ("10km".equals(raceType)) {
                programme = new Prepa10k(profil, (int) weeksBetween, distanceKm, title);
            } else {
                programme = new SemiMarathon(profil, (int) weeksBetween, distanceKm, title);
            }

            // Calcul kilométrage total
            double totalKm = estimerKilometragePrecis(programme, profil);
            
            String[] allures = profil.getAlluresPrincipales(distanceKm);
            
            // label de distance
            String distanceLabel = (Math.abs(distanceKm - Math.round(distanceKm)) < 1e-6)
                    ? String.format(Locale.US, "%d", (int)Math.round(distanceKm))
                    : String.format(Locale.US, "%.1f", distanceKm);
            
            List<Seance[]> semaines = programme.getSemaines();
            double[] weekTotals = new double[semaines.size()];
            int[][] seanceNumbers = new int[semaines.size()][];
            int globalSeanceNumber = 1;
            
            for (int i = 0; i < semaines.size(); i++) {
                Seance[] semaine = semaines.get(i);
                seanceNumbers[i] = new int[semaine.length];
                double weekTotal = 0.0;
                
                for (int j = 0; j < semaine.length; j++) {
                    seanceNumbers[i][j] = globalSeanceNumber++;
                    weekTotal += semaine[j].getDistanceKm(profil);
                }
                
                weekTotals[i] = weekTotal;
            }

            model.addAttribute("programme", programme);
            model.addAttribute("profil", profil);
            model.addAttribute("raceType", raceType);
            model.addAttribute("objectif", objectifStr);
            model.addAttribute("raceDate", raceDateStr);
            model.addAttribute("totalKm", totalKm);
            model.addAttribute("allures", allures);
            model.addAttribute("distanceLabel", distanceLabel);
            model.addAttribute("weekTotals", weekTotals);
            model.addAttribute("seanceNumbers", seanceNumbers);

            return "result";

        } catch (DateTimeParseException ex) {
            model.addAttribute("error", "Format de date invalide (YYYY-MM-DD).");
            return "index";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur: " + e.getMessage());
            return "index";
        }
    }

    @PostMapping("/download")
    public void downloadProgram(
            @RequestParam("raceType") String raceType,
            @RequestParam("niveau") String niveau,
            @RequestParam("sorties") int sorties,
            @RequestParam("vma") double vma,
            @RequestParam("objectif") String objectifStr,
            @RequestParam("raceDate") String raceDateStr,
            HttpServletResponse response
    ) throws IOException {
        LocalDate raceDate = LocalDate.parse(raceDateStr);
        long weeksBetween = ChronoUnit.WEEKS.between(LocalDate.now(), raceDate);
        if (weeksBetween < 4) weeksBetween = 4;
        if (weeksBetween > 52) weeksBetween = 52;

        double distanceKm;
        String title;
        if ("5km".equals(raceType)) { distanceKm = 5.0; title = "Préparation 5 km"; }
        else if ("10km".equals(raceType)) { distanceKm = 10.0; title = "Préparation 10 km"; }
        else { distanceKm = 21.1; title = "Préparation Semi-Marathon"; }

        Integer objectifSec = parseTimeToSeconds(objectifStr);
        Profil profil = new Profil(niveau, sorties, vma, objectifSec);

        Programme programme;
        if ("5km".equals(raceType)) programme = new Prepa5k(profil, (int) weeksBetween, distanceKm, title);
        else if ("10km".equals(raceType)) programme = new Prepa10k(profil, (int) weeksBetween, distanceKm, title);
        else programme = new SemiMarathon(profil, (int) weeksBetween, distanceKm, title);

        HtmlGenerator generator = new HtmlGenerator();
        String filename = generator.genererHTML(programme, profil); // produit le fichier sur disque

        Path file = Path.of(filename);
        if (!Files.exists(file)) {
            response.sendError(500, "Fichier non généré");
            return;
        }

        response.setContentType("text/html; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getFileName().toString() + "\"");
        Files.copy(file, response.getOutputStream());
        response.getOutputStream().flush();
    }

    private Integer parseTimeToSeconds(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            String[] parts = s.split(":");
            if (parts.length == 1) {
                return Integer.parseInt(parts[0].trim()) * 60;
            } else if (parts.length == 2) {
                return Integer.parseInt(parts[0].trim()) * 60 + Integer.parseInt(parts[1].trim());
            } else if (parts.length == 3) {
                return Integer.parseInt(parts[0].trim()) * 3600 + Integer.parseInt(parts[1].trim()) * 60 + Integer.parseInt(parts[2].trim());
            }
        } catch (NumberFormatException ex) {
            return null;
        }
        return null;
    }
    
    private double estimerKilometragePrecis(Programme programme, Profil profil) {
        double totalKm = 0.0;
        List<Seance[]> semaines = programme.getSemaines();
        for (int i = 0; i < semaines.size(); i++) {
            Seance[] arr = semaines.get(i);
            for (int j = 0; j < arr.length; j++) {
                totalKm += arr[j].getDistanceKm(profil);
            }
        }
        return totalKm;
    }
}