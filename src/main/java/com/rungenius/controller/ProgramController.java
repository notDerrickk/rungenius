package com.rungenius.controller;

import com.rungenius.model.RunGeniusGenerator.*;
import com.rungenius.model.RunGeniusEditor.ProgrammeCustom;
import com.rungenius.model.dto.ProgramDataDTO;
import com.rungenius.service.FitExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
public class ProgramController {

    private static final String SESSION_PROGRAMME = "rg.programme";
    private static final String SESSION_PROFIL = "rg.profil";
    private static final String SESSION_RACETYPE = "rg.raceType";
    private static final String SESSION_OBJECTIF = "rg.objectif";
    private static final String SESSION_RACEDATE = "rg.raceDate";
    private static final String SESSION_DISTANCEKM = "rg.distanceKm";

    @Autowired
    private FitExportService fitExportService;

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
            Model model,
            HttpSession session
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
                        pctVMA = 0.65; 
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

            // Stocker en session pour rendre le feedback utilisable
            session.setAttribute(SESSION_PROGRAMME, programme);
            session.setAttribute(SESSION_PROFIL, profil);
            session.setAttribute(SESSION_RACETYPE, "custom");
            session.setAttribute(SESSION_OBJECTIF, "");
            session.setAttribute(SESSION_RACEDATE, raceDate);
            session.setAttribute(SESSION_DISTANCEKM, distanceKm);
            
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
            String html = generator.genererHTMLString(programme, profil);
            
            String safeTitle = sanitizeFilename(programme.getTitle());
            String attachmentName = String.format("%s.html", safeTitle.isEmpty() ? "programme" : "programme_" + safeTitle);

            byte[] bytes = html.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            response.setContentType("text/html; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + attachmentName + "\"");
            response.setContentLength(bytes.length);
            response.getOutputStream().write(bytes);
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
            Model model,
            HttpSession session
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

            // Stocker en session pour permettre des ajustements ciblés 
            session.setAttribute(SESSION_PROGRAMME, programme);
            session.setAttribute(SESSION_PROFIL, profil);
            session.setAttribute(SESSION_RACETYPE, raceType);
            session.setAttribute(SESSION_OBJECTIF, objectifStr);
            session.setAttribute(SESSION_RACEDATE, raceDateStr);
            session.setAttribute(SESSION_DISTANCEKM, distanceKm);

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

            return "redirect:/programme";

        } catch (DateTimeParseException ex) {
            model.addAttribute("error", "Format de date invalide (YYYY-MM-DD).");
            return "index";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur: " + e.getMessage());
            return "index";
        }
    }

    @GetMapping("/programme")
    public String showProgramme(Model model, HttpSession session) {
        Object pObj = session.getAttribute(SESSION_PROGRAMME);
        Object profilObj = session.getAttribute(SESSION_PROFIL);
        Object raceTypeObj = session.getAttribute(SESSION_RACETYPE);
        Object objectifObj = session.getAttribute(SESSION_OBJECTIF);
        Object raceDateObj = session.getAttribute(SESSION_RACEDATE);
        Object distanceKmObj = session.getAttribute(SESSION_DISTANCEKM);

        if (!(pObj instanceof Programme) || !(profilObj instanceof Profil) || !(raceTypeObj instanceof String)) {
            return "redirect:/";
        }

        Programme programme = (Programme) pObj;
        Profil profil = (Profil) profilObj;
        String raceType = (String) raceTypeObj;
        String objectif = objectifObj instanceof String ? (String) objectifObj : "";
        String raceDate = raceDateObj instanceof String ? (String) raceDateObj : "";
        double distanceKm = distanceKmObj instanceof Double ? (Double) distanceKmObj : programme.getDistanceKm();

        double totalKm = estimerKilometragePrecis(programme, profil);
        String[] allures = profil.getAlluresPrincipales(distanceKm);

        String distanceLabel = (Math.abs(distanceKm - Math.round(distanceKm)) < 1e-6)
                ? String.format(Locale.US, "%d", (int) Math.round(distanceKm))
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
        model.addAttribute("objectif", objectif);
        model.addAttribute("raceDate", raceDate);
        model.addAttribute("totalKm", totalKm);
        model.addAttribute("allures", allures);
        model.addAttribute("distanceLabel", distanceLabel);
        model.addAttribute("weekTotals", weekTotals);
        model.addAttribute("seanceNumbers", seanceNumbers);

        return "result";
    }

    public static class FeedbackRequest {
        public String feeling; 
        public boolean hasPain;
        public int week; 
        public int sessionIndex;
    }

    public static class FeedbackResponse {
        public boolean ok;
        public String message;
        public Integer updatedWeek;
        public Integer updatedSessionIndex;
        public String newNom;
        public String newType;
        public String newCorps;
        public Integer newEchauffement;
        public Integer newCooldown;
        public Double newAllurePct;
        public String newAllureLabel;
        public Double newDistanceKm;
    }

    @PostMapping("/feedback")
    @ResponseBody
    public FeedbackResponse feedback(@RequestBody FeedbackRequest req, HttpSession session) {
        FeedbackResponse resp = new FeedbackResponse();

        Object pObj = session.getAttribute(SESSION_PROGRAMME);
        Object profilObj = session.getAttribute(SESSION_PROFIL);
        Object raceTypeObj = session.getAttribute(SESSION_RACETYPE);
        Object distanceKmObj = session.getAttribute(SESSION_DISTANCEKM);

        if (!(pObj instanceof Programme) || !(profilObj instanceof Profil)) {
            resp.ok = false;
            resp.message = "Programme non trouvé (session expirée)";
            return resp;
        }

        Programme programme = (Programme) pObj;
        Profil profil = (Profil) profilObj;
        String raceType = raceTypeObj instanceof String ? (String) raceTypeObj : "";
        double distanceKm = distanceKmObj instanceof Double ? (Double) distanceKmObj : programme.getDistanceKm();

        List<Seance[]> semaines = programme.getSemaines();
        int w = req.week - 1;
        int i = req.sessionIndex;
        if (w < 0 || w >= semaines.size() || i < 0 || i >= semaines.get(w).length) {
            resp.ok = false;
            resp.message = "Index séance invalide";
            return resp;
        }

        Seance current = semaines.get(w)[i];

        //Déterminer la séance à modifier
        Location target;
        if (req.hasPain) {
            target = findNextGlobal(semaines, w, i);
        } else {
            // Ajuster la prochaine séance du même type
            target = findNextOfType(semaines, w, i, current.getType());
            if (target == null) {
                target = findNextGlobal(semaines, w, i);
            }
        }

        if (target == null) {
            resp.ok = true;
            resp.message = "Aucune séance suivante à ajuster";
            return resp;
        }

        Seance next = semaines.get(target.weekIndex)[target.sessionIndex];

        // Appliquer la règle
        if (req.hasPain) {
            Seance ef = createSimpleEFReplacement(next.getNom());
            semaines.get(target.weekIndex)[target.sessionIndex] = ef;
            resp.ok = true;
            resp.message = "Douleur signalée : prochaine séance remplacée par EF";
            fillResponseWithUpdatedSeance(resp, ef, profil, target);
            return resp;
        }

        if (req.feeling == null || req.feeling.isBlank() || "doable".equals(req.feeling)) {
            resp.ok = true;
            resp.message = "OK";
            return resp;
        }

        BanqueExercices banque = new BanqueExercices();
        String banqueKey = inferBanqueKey(current, raceType, distanceKm);
        int currentDiff = resolveDifficulte(current, banque, banqueKey);

        int newDiff = currentDiff;
        if ("tooEasy".equals(req.feeling)) {
            if (currentDiff < 5) newDiff = currentDiff + 1;
        } else if ("tooHard".equals(req.feeling)) {
            if (currentDiff > 1) newDiff = currentDiff - 1;
        }

        if (newDiff == currentDiff) {
            resp.ok = true;
            resp.message = "Niveau inchangé";
            return resp;
        }

        CorpsDeSeance ex = banque.getExerciceAleatoire(banqueKey, newDiff);
        if (ex == null) {
            resp.ok = false;
            resp.message = "Impossible de trouver un exercice à ce niveau";
            return resp;
        }

        // Ne modifier qu'une seule séance
        next.setCorps(ex.getDescription());
        next.setPourcentageVMA(ex.resolvePourcentageVMA(profil));
        next.setDifficulte(newDiff);
        next.setTypeBanque(banqueKey);

        resp.ok = true;
        resp.message = "Programme ajusté";
        fillResponseWithUpdatedSeance(resp, next, profil, target);
        return resp;
    }

    private static class Location {
        final int weekIndex;
        final int sessionIndex;

        Location(int weekIndex, int sessionIndex) {
            this.weekIndex = weekIndex;
            this.sessionIndex = sessionIndex;
        }
    }

    private Location findNextGlobal(List<Seance[]> semaines, int fromWeek, int fromSession) {
        for (int w = fromWeek; w < semaines.size(); w++) {
            Seance[] week = semaines.get(w);
            int start = (w == fromWeek) ? fromSession + 1 : 0;
            for (int i = start; i < week.length; i++) {
                return new Location(w, i);
            }
        }
        return null;
    }

    private Location findNextOfType(List<Seance[]> semaines, int fromWeek, int fromSession, String type) {
        if (type == null) return null;
        for (int w = fromWeek; w < semaines.size(); w++) {
            Seance[] week = semaines.get(w);
            int start = (w == fromWeek) ? fromSession + 1 : 0;
            for (int i = start; i < week.length; i++) {
                String t = week[i].getType();
                if (type.equals(t)) {
                    return new Location(w, i);
                }
            }
        }
        return null;
    }

    private Seance createSimpleEFReplacement(String nom) {
        String safeNom = "SEANCE REPOS";
        return new Seance(safeNom, "Endurance Fondamentale", 0, "20min en endurance fondamentale", 0, 0.65, null, null);
    }

    private void fillResponseWithUpdatedSeance(FeedbackResponse resp, Seance seance, Profil profil, Location target) {
        resp.updatedWeek = target.weekIndex + 1;
        resp.updatedSessionIndex = target.sessionIndex;
        resp.newNom = seance.getNom();
        resp.newType = seance.getType();
        resp.newCorps = seance.getCorps();
        resp.newEchauffement = seance.getDureeEchauffement();
        resp.newCooldown = seance.getDureeCooldown();
        resp.newAllurePct = seance.getPourcentageVMA();
        resp.newAllureLabel = profil.getAllureFormatee(seance.getPourcentageVMA());
        resp.newDistanceKm = seance.getDistanceKm(profil);
    }

    private String inferBanqueKey(Seance seance, String raceType, double distanceKm) {
        if (seance.getTypeBanque() != null && !seance.getTypeBanque().isBlank()) {
            return seance.getTypeBanque();
        }
        String type = seance.getType();
        if ("Allure Spécifique".equals(type)) {
            if ("5km".equals(raceType) || Math.abs(distanceKm - 5.0) < 1e-6) return "Allure Spécifique 5km";
            if ("10km".equals(raceType) || Math.abs(distanceKm - 10.0) < 1e-6) return "Allure Spécifique 10km";
            return "Allure Spécifique";
        }
        return type;
    }

    private int resolveDifficulte(Seance seance, BanqueExercices banque, String banqueKey) {
        Integer d = seance.getDifficulte();
        if (d != null && d >= 1 && d <= 5) return d;

        String corps = seance.getCorps();
        if (corps == null || corps.isBlank()) return 3;

        List<CorpsDeSeance> exs = banque.getExercices(banqueKey);
        if (exs == null || exs.isEmpty()) return 3;

        String needle = normalize(corps);
        for (CorpsDeSeance ex : exs) {
            if (needle.equals(normalize(ex.getDescription()))) {
                return ex.getDifficulte();
            }
        }
        return 3;
    }

    private String normalize(String s) {
        if (s == null) return "";
        String out = removeAccents(s).toLowerCase(Locale.ROOT).trim();
        out = out.replace('×', 'x');
        out = out.replaceAll("\\s+", " ");
        return out;
    }

    @PostMapping("/download")
    public void downloadProgram(
            @RequestParam("raceType") String raceType,
            @RequestParam("niveau") String niveau,
            @RequestParam("sorties") int sorties,
            @RequestParam("vma") double vma,
            @RequestParam("objectif") String objectifStr,
            @RequestParam("raceDate") String raceDateStr,
            HttpServletResponse response,
            HttpSession session
    ) throws IOException {
        Programme programme = null;
        Profil profil = null;

        Object pObj = session.getAttribute(SESSION_PROGRAMME);
        Object profilObj = session.getAttribute(SESSION_PROFIL);
        Object raceTypeObj = session.getAttribute(SESSION_RACETYPE);

        if (pObj instanceof Programme && profilObj instanceof Profil && raceTypeObj instanceof String && raceType.equals(raceTypeObj)) {
            programme = (Programme) pObj;
            profil = (Profil) profilObj;
        }

        if (programme == null || profil == null) {
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
            profil = new Profil(niveau, sorties, vma, objectifSec);

            if ("5km".equals(raceType)) programme = new Prepa5k(profil, (int) weeksBetween, distanceKm, title);
            else if ("10km".equals(raceType)) programme = new Prepa10k(profil, (int) weeksBetween, distanceKm, title);
            else programme = new SemiMarathon(profil, (int) weeksBetween, distanceKm, title);
        }

        HtmlGenerator generator = new HtmlGenerator();
        String html = generator.genererHTMLString(programme, profil);
        
        String safeTitle = sanitizeFilename(programme.getTitle());
        String attachmentName = String.format("%s.html", safeTitle.isEmpty() ? "programme" : "programme_" + safeTitle);

        byte[] bytes = html.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        response.setContentType("text/html; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + attachmentName + "\"");
        response.setContentLength(bytes.length);
        response.getOutputStream().write(bytes);
        response.getOutputStream().flush();
    }

    // Export d'une seule séance en .fit
    @PostMapping("/fit/export-single")
    public void exportSingleSeance(
            @RequestParam("nom") String nom,
            @RequestParam(value="type", required=false) String type,
            @RequestParam(value="echauffement", required=false, defaultValue="0") int echauffement,
            @RequestParam(value="corps", required=false) String corps,
            @RequestParam(value="cooldown", required=false, defaultValue="0") int cooldown,
            @RequestParam(value="allurePct", required=false) Double allurePct,
            @RequestParam(value="vma", required=false, defaultValue="0") double vma,
            HttpServletResponse response
    ) throws IOException {
        try {
            Double pct = (allurePct == null || allurePct <= 0) ? 0.85 : allurePct;
            Seance seance = new Seance(nom, type, echauffement, corps, cooldown, pct);
            Profil profil = new Profil("Custom", 3, vma, null);

            byte[] fit = fitExportService.generateWorkoutFit(seance, profil);
            
            String filename = sanitizeFilename(nom) + ".fit"; // supprime les accents

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            response.getOutputStream().write(fit);
            response.getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Erreur export FIT: " + e.getMessage());
        }
    }

    // Export de toutes les séances en .zip
    @PostMapping("/fit/export-all")
    public void exportAllFit(
            @RequestParam("raceType") String raceType,
            @RequestParam("niveau") String niveau,
            @RequestParam("sorties") int sorties,
            @RequestParam("vma") double vma,
            @RequestParam("objectif") String objectifStr,
            @RequestParam("raceDate") String raceDateStr,
            HttpServletResponse response,
            HttpSession session
    ) throws IOException {
        try {
            String raceFormat;
            if ("5km".equals(raceType)) raceFormat = "5km";
            else if ("10km".equals(raceType)) raceFormat = "10km";
            else raceFormat = "semi_marathon";

            Programme programme = null;
            Profil profil = null;
            Object pObj = session.getAttribute(SESSION_PROGRAMME);
            Object profilObj = session.getAttribute(SESSION_PROFIL);
            Object raceTypeObj = session.getAttribute(SESSION_RACETYPE);
            if (pObj instanceof Programme && profilObj instanceof Profil && raceTypeObj instanceof String && raceType.equals(raceTypeObj)) {
                programme = (Programme) pObj;
                profil = (Profil) profilObj;
            }

            if (programme == null || profil == null) {
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
                profil = new Profil(niveau, sorties, vma, objectifSec);

                if ("5km".equals(raceType)) programme = new Prepa5k(profil, (int) weeksBetween, distanceKm, title);
                else if ("10km".equals(raceType)) programme = new Prepa10k(profil, (int) weeksBetween, distanceKm, title);
                else programme = new SemiMarathon(profil, (int) weeksBetween, distanceKm, title);
            }

            String zipFilename = String.format("preparation_%s_seances.zip", raceFormat);
            
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + zipFilename + "\"");

            try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
                List<Seance[]> semaines = programme.getSemaines();
                int seanceNumber = 1;

                for (int i = 0; i < semaines.size(); i++) {
                    Seance[] semaine = semaines.get(i);
                    for (int j = 0; j < semaine.length; j++) {
                        Seance seance = semaine[j];
                        String filename = String.format("Seance_%d_%s.fit", 
                            seanceNumber,
                            sanitizeFilename(seance.getNom())  
                        );

                        byte[] fitData = fitExportService.generateWorkoutFit(seance, profil);
                        
                        ZipEntry entry = new ZipEntry(filename);
                        zos.putNextEntry(entry);
                        zos.write(fitData);
                        zos.closeEntry();

                        seanceNumber++;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Erreur lors de l'export FIT: " + e.getMessage());
        }
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

    private String removeAccents(String text) {
        if (text == null) return "";
        return java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    private String sanitizeFilename(String name) {
        if (name == null || name.isBlank()) return "seance";
        String clean = removeAccents(name);
        clean = clean.replaceAll("\\s+", "_");
        clean = clean.replaceAll("[^A-Za-z0-9_-]", "");
        return clean.substring(0, Math.min(clean.length(), 50));
    }
}