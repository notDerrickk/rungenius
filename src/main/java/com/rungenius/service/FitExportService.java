package com.rungenius.service;

import com.garmin.fit.*;
import com.rungenius.model.RunGeniusGenerator.Profil;
import com.rungenius.model.RunGeniusGenerator.Seance;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FitExportService {

    public byte[] generateWorkoutFit(Seance seance, Profil profil) throws IOException {
        Path temp = Files.createTempFile("workout_", ".fit");
        try {
            FileEncoder encoder = new FileEncoder(temp.toFile());

            // File ID Message
            FileIdMesg fileId = new FileIdMesg();
            fileId.setType(com.garmin.fit.File.WORKOUT);
            fileId.setManufacturer(Manufacturer.DEVELOPMENT);
            fileId.setTimeCreated(new DateTime(new Date()));
            encoder.write(fileId);

            // Workout Message
            WorkoutMesg workout = new WorkoutMesg();
            workout.setSport(Sport.RUNNING);
            workout.setWktName(safeName(seance.getNom()));
            encoder.write(workout);

            int stepIndex = 0;

            // Échauffement
            if (seance.getDureeEchauffement() > 0) {
                WorkoutStepMesg warmup = createTimeStep("Échauffement", seance.getDureeEchauffement(), false);
                warmup.setMessageIndex(stepIndex++);
                encoder.write(warmup);
            }

            // Corps de séance
            List<WorkoutStepMesg> bodySteps = buildBodySteps(seance.getCorps());
            for (WorkoutStepMesg step : bodySteps) {
                step.setMessageIndex(stepIndex++);
                encoder.write(step);
            }

            // Retour au calme
            if (seance.getDureeCooldown() > 0) {
                WorkoutStepMesg cooldown = createTimeStep("Retour au calme", seance.getDureeCooldown(), true);
                cooldown.setMessageIndex(stepIndex++);
                encoder.write(cooldown);
            }

            encoder.close();
            byte[] result = Files.readAllBytes(temp);
            Files.deleteIfExists(temp);
            return result;

        } catch (Exception e) {
            Files.deleteIfExists(temp);
            throw new IOException("Erreur génération FIT: " + e.getMessage(), e);
        }
    }

    private WorkoutStepMesg createTimeStep(String name, int minutes, boolean isRecovery) {
        WorkoutStepMesg step = new WorkoutStepMesg();
        step.setWktStepName(safeName(name));
        step.setDurationType(WktStepDuration.TIME);
        long seconds = (long) minutes * 60L;
        step.setDurationValue(seconds * 10L); // FIT expects value scaled : seconds * 10
        step.setTargetType(WktStepTarget.OPEN);
        step.setIntensity(isRecovery ? Intensity.REST : Intensity.ACTIVE);
        return step;
    }

    private WorkoutStepMesg createDistanceStep(String name, int meters, boolean isRecovery) {
        WorkoutStepMesg step = new WorkoutStepMesg();
        step.setWktStepName(safeName(name));
        step.setDurationType(WktStepDuration.DISTANCE);
        step.setDurationValue((long) meters * 100L); // FIT expects value scaled : meters * 100
        step.setTargetType(WktStepTarget.OPEN);
        step.setIntensity(isRecovery ? Intensity.REST : Intensity.ACTIVE);
        return step;
    }

    private WorkoutStepMesg createRecoveryStep(int seconds) {
        WorkoutStepMesg step = new WorkoutStepMesg();
        step.setWktStepName("Récup");
        step.setDurationType(WktStepDuration.TIME);
        step.setDurationValue((long) seconds * 10L); // seconds * 10
        step.setTargetType(WktStepTarget.OPEN);
        step.setIntensity(Intensity.REST);
        return step;
    }

    private List<WorkoutStepMesg> buildBodySteps(String corps) {
        List<WorkoutStepMesg> steps = new ArrayList<>();
        
        if (corps == null || corps.isBlank()) {
            steps.add(createTimeStep("Bloc", 20, false));
            return steps;
        }

        String normalized = corps.toLowerCase().replace('×', 'x').trim();
        Integer recupSeconds = extractRecupSeconds(normalized);

        // Pattern: N x M m (ex: "10 x 400m récup 1min")
        Pattern repsMeters = Pattern.compile("(\\d+)\\s*x\\s*(\\d+(?:[.,]\\d+)?)\\s*m");
        Matcher mMeters = repsMeters.matcher(normalized);
        if (mMeters.find()) {
            int reps = Integer.parseInt(mMeters.group(1));
            int meters = (int) Math.round(Double.parseDouble(mMeters.group(2).replace(',', '.')));
            
            for (int i = 0; i < reps; i++) {
                steps.add(createDistanceStep("Rep " + (i + 1), meters, false));
                if (recupSeconds != null && i < reps - 1) {
                    steps.add(createRecoveryStep(recupSeconds));
                }
            }
            return steps;
        }

        // Pattern: N x K km (ex: "3 x 2km récup 2min")
        Pattern repsKm = Pattern.compile("(\\d+)\\s*x\\s*(\\d+(?:[.,]\\d+)?)\\s*km");
        Matcher mKm = repsKm.matcher(normalized);
        if (mKm.find()) {
            int reps = Integer.parseInt(mKm.group(1));
            double km = Double.parseDouble(mKm.group(2).replace(',', '.'));
            int meters = (int) Math.round(km * 1000);
            
            for (int i = 0; i < reps; i++) {
                steps.add(createDistanceStep("Rep " + (i + 1), meters, false));
                if (recupSeconds != null && i < reps - 1) {
                    steps.add(createRecoveryStep(recupSeconds));
                }
            }
            return steps;
        }

        // Pattern: N x T min (ex: "3 x 10min récup 3min")
        Pattern repsMinutes = Pattern.compile("(\\d+)\\s*x\\s*(\\d+)\\s*min");
        Matcher mMinutes = repsMinutes.matcher(normalized);
        if (mMinutes.find()) {
            int reps = Integer.parseInt(mMinutes.group(1));
            int minutes = Integer.parseInt(mMinutes.group(2));
            
            for (int i = 0; i < reps; i++) {
                steps.add(createTimeStep("Rep " + (i + 1), minutes, false));
                if (recupSeconds != null && i < reps - 1) {
                    steps.add(createRecoveryStep(recupSeconds));
                }
            }
            return steps;
        }

        // Pattern: K km (ex: "10km en continu")
        Pattern singleKm = Pattern.compile("(\\d+(?:[.,]\\d+)?)\\s*km");
        Matcher mSingleKm = singleKm.matcher(normalized);
        if (mSingleKm.find() && !normalized.contains("x")) {
            double km = Double.parseDouble(mSingleKm.group(1).replace(',', '.'));
            int meters = (int) Math.round(km * 1000);
            steps.add(createDistanceStep("Continu", meters, false));
            return steps;
        }

        // Pattern: T min (ex: "30min en tempo")
        Pattern singleMinutes = Pattern.compile("(\\d+)\\s*min");
        Matcher mSingleMin = singleMinutes.matcher(normalized);
        if (mSingleMin.find() && !normalized.contains("x")) {
            int minutes = Integer.parseInt(mSingleMin.group(1));
            steps.add(createTimeStep("Continu", minutes, false));
            return steps;
        }

        // Fallback: 20 minutes par défaut
        steps.add(createTimeStep("Bloc", 20, false));
        return steps;
    }

    private Integer extractRecupSeconds(String text) {
        // "récup 2min 30sec" ou "récup 2min30"
        Pattern minSec = Pattern.compile("récup\\s*(\\d+)\\s*min(?:\\s*(\\d+)\\s*sec)?");
        Matcher m1 = minSec.matcher(text);
        if (m1.find()) {
            int total = Integer.parseInt(m1.group(1)) * 60;
            if (m1.group(2) != null) {
                total += Integer.parseInt(m1.group(2));
            }
            return total;
        }

        // "récup 90sec" ou "récup 90s"
        Pattern seconds = Pattern.compile("récup\\s*(\\d+)\\s*s(?:ec)?");
        Matcher m2 = seconds.matcher(text);
        if (m2.find()) {
            return Integer.parseInt(m2.group(1));
        }

        return null;
    }

    private String safeName(String name) {
        if (name == null || name.isBlank()) {
            return "Seance";
        }
        String safe = name.replaceAll("[^A-Za-z0-9àâäéèêëïîôöùûüÿæœç\\s-]", "");
        return safe.length() > 15 ? safe.substring(0, 15) : safe;
    }
}
