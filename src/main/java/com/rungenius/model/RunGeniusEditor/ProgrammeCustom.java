package com.rungenius.model.RunGeniusEditor;

import java.util.ArrayList;
import java.util.List;

import com.rungenius.model.RunGeniusGenerator.*;

public class ProgrammeCustom implements Programme {
    private String title;
    private double distanceKm;
    private List<Seance[]> semaines;
    
    public ProgrammeCustom(String title, double distanceKm) {
        this.title = title;
        this.distanceKm = distanceKm;
        this.semaines = new ArrayList<>();
    }
    
    public void addSemaine(Seance[] seances) {
        semaines.add(seances);
    }
    
    public void setSemaine(int index, Seance[] seances) {
        if (index >= 0 && index < semaines.size()) {
            semaines.set(index, seances);
        }
    }
    
    public List<Seance[]> getSemaines() {
        return semaines;
    }
    
    public double getDistanceKm() {
        return distanceKm;
    }
    
    public String getTitle() {
        return title;
    }
}