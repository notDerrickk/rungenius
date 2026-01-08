package com.rungenius.service;

import org.springframework.stereotype.Service;

import com.garmin.fit.FileEncoder;
import com.garmin.fit.FileIdMesg;
import com.garmin.fit.File;
import com.garmin.fit.Manufacturer;
import com.garmin.fit.DateTime;
import com.garmin.fit.WorkoutMesg;
import com.garmin.fit.Sport;
import com.garmin.fit.WorkoutStepMesg;
import com.garmin.fit.WktStepDuration;
import com.garmin.fit.WktStepTarget;
import com.garmin.fit.Intensity;

import com.rungenius.model.RunGeniusGenerator.Seance;
import com.rungenius.model.RunGeniusGenerator.Profil;

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
            FileEncoder encoder = new FileEncoder(temp.toFile(), com.garmin.fit.Fit.ProtocolVersion.V2_0);

            FileIdMesg fileId = new FileIdMesg();
            fileId.setType(com.garmin.fit.File.WORKOUT);
            fileId.setManufacturer(Manufacturer.DEVELOPMENT);
            fileId.setTimeCreated(new DateTime(new Date()));
            encoder.write(fileId);

            WorkoutMesg workout = new WorkoutMesg();
            workout.setSport(Sport.RUNNING);
            workout.setWktName(safeName(seance.getNom()));
            encoder.write(workout);
        
            int stepIndex = 0;

            if (seance.getDureeEchauffement() > 0) {
                WorkoutStepMesg w = createTimeStep("Échauffement", seance.getDureeEchauffement(), false);
                w.setMessageIndex(stepIndex++);
                encoder.write(w);
            }

            List<WorkoutStepMesg> body = buildBodySteps(seance.getCorps());
            for (WorkoutStepMesg step : body) {
                step.setMessageIndex(stepIndex++);
                encoder.write(step);
            }

            if (seance.getDureeCooldown() > 0) {
                WorkoutStepMesg c = createTimeStep("Cooldown", seance.getDureeCooldown(), true);
                c.setMessageIndex(stepIndex++);
                encoder.write(c);
            }

            encoder.close();
            byte[] out = Files.readAllBytes(temp);
            Files.deleteIfExists(temp);
            return out;
        } catch (Exception e) {
            Files.deleteIfExists(temp);
            throw new IOException("Erreur génération FIT: " + e.getMessage(), e);
        }
    }
    private WorkoutStepMesg createTimeStep(String name, int minutes, boolean isRecovery) {
        WorkoutStepMesg step = new WorkoutStepMesg();
        step.setWktStepName(safeName(name));
        step.setDurationType(WktStepDuration.TIME);
        step.setDurationValue((long) minutes * 60); // seconds
        step.setTargetType(WktStepTarget.OPEN);
        step.setIntensity(isRecovery ? Intensity.REST : Intensity.ACTIVE);
        return step;
    }

    private WorkoutStepMesg createDistanceStep(String name, int meters, boolean isRecovery) {
        WorkoutStepMesg step = new WorkoutStepMesg();
        step.setWktStepName(safeName(name));
        step.setDurationType(WktStepDuration.DISTANCE);
        step.setDurationValue((long) meters); // meters
        step.setTargetType(WktStepTarget.OPEN);
        step.setIntensity(isRecovery ? Intensity.REST : Intensity.ACTIVE);
        return step;
    }
    private WorkoutStepMesg createRecoveryStep(int seconds) {
        WorkoutStepMesg step = new WorkoutStepMesg();
        step.setWktStepName(safeName("Récup"));
        step.setDurationType(WktStepDuration.TIME);
        step.setDurationValue((long) seconds); // seconds
        step.setTargetType(WktStepTarget.OPEN);
        step.setIntensity(Intensity.REST);
        return step;
    }
    private List<WorkoutStepMesg> buildBodySteps(String corps) {
        List<WorkoutStepMesg> steps = new ArrayList<>();
        if (corps == null) {
            steps.add(createTimeStep("Bloc", 20, false));
            return steps;
        }
        
        String s = corps.toLowerCase().replace('×', 'x').trim();
        Integer recup = extractRecupSeconds(s);

        // N x M m
        Matcher m1 = Pattern.compile("(\\d+)\\s*x\\s*(\\d+(?:[.,]\\d+)?)\\s*m").matcher(s);
        if (m1.find()) {
            int reps = Integer.parseInt(m1.group(1));
            int metres = (int) Math.round(Double.parseDouble(m1.group(2).replace(',', '.')));
            for (int i = 0; i < reps; i++) {
                steps.add(createDistanceStep("Rep " + (i+1), metres, false));
                if (recup != null && i < reps - 1) {
                    steps.add(createRecoveryStep(recup));
                }
            }
            return steps;
        }

        // N x K km
        Matcher m2 = Pattern.compile("(\\d+)\\s*x\\s*(\\d+(?:[.,]\\d+)?)\\s*km").matcher(s);
        if (m2.find()) {
            int reps = Integer.parseInt(m2.group(1));
            int metres = (int) Math.round(Double.parseDouble(m2.group(2).replace(',', '.')) * 1000);
            for (int i = 0; i < reps; i++) {
                steps.add(createDistanceStep("Rep " + (i+1), metres, false));
                if (recup != null && i < reps - 1) {
                    steps.add(createRecoveryStep(recup));
                }
            }
            return steps;
        }

        // N x T min
        Matcher m3 = Pattern.compile("(\\d+)\\s*x\\s*(\\d+)\\s*min").matcher(s);
        if (m3.find()) {
            int reps = Integer.parseInt(m3.group(1));
            int min = Integer.parseInt(m3.group(2));
            for (int i = 0; i < reps; i++) {
                steps.add(createTimeStep("Rep " + (i+1), min, false));
                if (recup != null && i < reps - 1) {
                    steps.add(createRecoveryStep(recup));
                }
            }
            return steps;
        }

        // K km single
        Matcher m4 = Pattern.compile("(\\d+(?:[.,]\\d+)?)\\s*km").matcher(s);
        if (m4.find()) {
            steps.add(createDistanceStep("Continu", (int)Math.round(Double.parseDouble(m4.group(1).replace(',', '.')) * 1000), false));
            return steps;
        }

        // T min single
        Matcher m5 = Pattern.compile("(\\d+)\\s*min").matcher(s);
        if (m5.find()) {
            steps.add(createTimeStep("Continu", Integer.parseInt(m5.group(1)), false));
            return steps;
        }

        steps.add(createTimeStep("Bloc", 20, false));
        return steps;
    }

    private Integer extractRecupSeconds(String text) {
        Matcher m1 = Pattern.compile("récup\\s*(\\d+)\\s*min(?:\\s*(\\d+)\\s*sec)?").matcher(text);
        if (m1.find()) {
            int sec = Integer.parseInt(m1.group(1)) * 60;
            if (m1.group(2) != null) {
                sec += Integer.parseInt(m1.group(2));
            }
            return sec;
        }

        Matcher m2 = Pattern.compile("récup\\s*(\\d+)\\s*s(?:ec)?").matcher(text);
        if (m2.find()) {
            return Integer.parseInt(m2.group(1));
        }

        return null;
    }
    private String safeName(String name) {
        if (name == null) return "Seance";
        String safe = name.replaceAll("[^A-Za-z0-9àâäéèêëïîôöùûüÿæœç\\s-]", "");
        return safe.length() > 15 ? safe.substring(0, 15) : safe;
    }
}
