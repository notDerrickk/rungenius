package com.rungenius.model.RunGeniusGenerator;
import java.util.List;

public interface Programme {
    List<Seance[]> getSemaines();
    double getDistanceKm();
    String getTitle();
}
