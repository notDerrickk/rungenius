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
import com.garmin.fit.Intensity;

import com.rungenius.model.RunGeniusGenerator.Seance;
import com.rungenius.model.RunGeniusGenerator.Profil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;


@Service
public class FitExportService {

    public byte[] generateWorkoutFit(Seance seance, Profil profil) throws IOException {
        Path temp = Files.createTempFile("workout_", ".fit");

        try {
            FileEncoder encoder = new FileEncoder(temp.toFile(), com.garmin.fit.Fit.ProtocolVersion.V2_0);

            FileIdMesg fileId = new FileIdMesg();
            fileId.setType(File.WORKOUT);
            fileId.setManufacturer(Manufacturer.DEVELOPMENT);
            fileId.setTimeCreated(new DateTime(new Date()));
            encoder.write(fileId);

            WorkoutMesg workout = new WorkoutMesg();
            workout.setSport(Sport.RUNNING);
            workout.setWktName("Seance");
            encoder.write(workout);

            int idx = 0;

            if (seance.getDureeEchauffement() > 0) {
                WorkoutStepMesg warmup = createTimeStep("Echauffement", seance.getDureeEchauffement(), false);
                warmup.setMessageIndex(idx++);
                encoder.write(warmup);
            }

            WorkoutStepMesg main = createTimeStep("Bloc", 20, false);
            main.setMessageIndex(idx++);
            encoder.write(main);

            if (seance.getDureeCooldown() > 0) {
                WorkoutStepMesg cooldown = createTimeStep("Cooldown", seance.getDureeCooldown(), true);
                cooldown.setMessageIndex(idx++);
                encoder.write(cooldown);
            }


            encoder.close();
            return Files.readAllBytes(temp);

        } finally {
            Files.deleteIfExists(temp);
        }
    }
    private WorkoutStepMesg createTimeStep(String name, int minutes, boolean recovery) {
        WorkoutStepMesg step = new WorkoutStepMesg();
        step.setWktStepName(name);
        step.setDurationType(WktStepDuration.TIME);
        step.setDurationValue(minutes * 60L);
        step.setIntensity(recovery ? Intensity.REST : Intensity.ACTIVE);
        return step;
    }

}
