package com.rungenius.model.RunGeniusEditor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import com.rungenius.model.RunGeniusGenerator.*;

public class ProgrammeEditeurFrame extends JFrame {
    private double vma;
    private double distanceKm;
    private int nbSemaines;
    private int nbSortiesParSemaine;
    private String[] allureLabels;
    private double[] allureValues;
    
    private ProgrammeCustom programme;
    private List<List<Seance>> toutesLesSeances;
    
    private int semaineActuelle = 0;
    private int seanceActuelle = 0;
    
    private JLabel infoLabel;
    private SeanceEditeurPanel editeurPanel;
    private JPanel navigationPanel;
    private JButton prevSeanceButton;
    private JButton nextSeanceButton;
    private JButton prevSemaineButton;
    private JButton nextSemaineButton;
    private JButton exporterButton;
    
    public ProgrammeEditeurFrame(ConfigProgrammeDialog config) {
        this.vma = config.getVma();
        this.distanceKm = config.getDistanceKm();
        this.nbSemaines = config.getNbSemaines();
        this.nbSortiesParSemaine = config.getNbSortiesParSemaine();
        
        double allureEF = config.getAllureEF();
        double allureSeuil = config.getAllureSeuil();
        double allureVMA = config.getAllureVMA();
        
        double pctEF = 60.0 / (vma * allureEF);
        double pctSeuil = 60.0 / (vma * allureSeuil);
        double pctVMA = 60.0 / (vma * allureVMA);
        double allureObjectif = config.getAllureObjectif();
        double pctObjectif = 60.0 / (vma * allureObjectif);
        
        allureLabels = new String[]{
            String.format("Endurance Fondamentale (%s min/km)", formatAllure(allureEF)),
            String.format("Allure Seuil (%s min/km)", formatAllure(allureSeuil)),
            String.format("Allure VMA (%s min/km)", formatAllure(allureVMA)),
            String.format("Allure Objectif (%s min/km)", formatAllure(allureObjectif))
        };
        
        allureValues = new double[]{pctEF, pctSeuil, pctVMA, pctObjectif};
        
        // Initialiser les séances
        toutesLesSeances = new ArrayList<>();
        for (int i = 0; i < nbSemaines; i++) {
            List<Seance> semaine = new ArrayList<>();
            for (int j = 0; j < nbSortiesParSemaine; j++) {
                semaine.add(createDefaultSeance(i + 1, j + 1));
            }
            toutesLesSeances.add(semaine);
        }
        
        initUI();
    }
    
    private String formatAllure(double minutesPerKm) {
        int minutes = (int) minutesPerKm;
        int seconds = (int) ((minutesPerKm - minutes) * 60);
        return String.format("%d:%02d", minutes, seconds);
    }
    
    private Seance createDefaultSeance(int semaine, int seance) {
        String nom = "Semaine " + semaine + " - Séance " + seance;
        return new Seance(nom, "Endurance", 15, "40 min", 10, allureValues[0]);
    }
    
    private void initUI() {
        setTitle("Éditeur de Programme");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        
        infoLabel = new JLabel();
        updateInfoLabel();
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(infoLabel, BorderLayout.NORTH);
        
        editeurPanel = new SeanceEditeurPanel(allureLabels, allureValues);
        add(editeurPanel, BorderLayout.CENTER);
        
        navigationPanel = new JPanel(new BorderLayout());
        
        JPanel seanceNavPanel = new JPanel(new FlowLayout());
        prevSeanceButton = new JButton("◀ Séance précédente");
        prevSeanceButton.addActionListener(e -> prevSeance());
        nextSeanceButton = new JButton("Séance suivante ▶");
        nextSeanceButton.addActionListener(e -> nextSeance());
        seanceNavPanel.add(prevSeanceButton);
        seanceNavPanel.add(nextSeanceButton);
        
        JPanel semaineNavPanel = new JPanel(new FlowLayout());
        prevSemaineButton = new JButton("◀◀ Semaine précédente");
        prevSemaineButton.addActionListener(e -> prevSemaine());
        nextSemaineButton = new JButton("Semaine suivante ▶▶");
        nextSemaineButton.addActionListener(e -> nextSemaine());
        exporterButton = new JButton("✓ Exporter le programme (HTML)");
        exporterButton.addActionListener(e -> exporterProgramme());
        exporterButton.setBackground(new Color(76, 175, 80));
        exporterButton.setForeground(Color.WHITE);
        exporterButton.setFont(new Font("Arial", Font.BOLD, 12));
        semaineNavPanel.add(prevSemaineButton);
        semaineNavPanel.add(nextSemaineButton);
        semaineNavPanel.add(exporterButton);
        
        navigationPanel.add(seanceNavPanel, BorderLayout.NORTH);
        navigationPanel.add(semaineNavPanel, BorderLayout.SOUTH);
        
        add(navigationPanel, BorderLayout.SOUTH);
        
        chargerSeance();
        updateButtons();
    }
    
    private void updateInfoLabel() {
        infoLabel.setText(String.format("Programme %d semaines - %d sorties/semaine | Semaine %d/%d - Séance %d/%d",
            nbSemaines, nbSortiesParSemaine, semaineActuelle + 1, nbSemaines, seanceActuelle + 1, nbSortiesParSemaine));
    }
    
    private void sauvegarderSeance() {
        Seance seance = editeurPanel.getSeance();
        toutesLesSeances.get(semaineActuelle).set(seanceActuelle, seance);
    }
    
    private void chargerSeance() {
        Seance seance = toutesLesSeances.get(semaineActuelle).get(seanceActuelle);
        editeurPanel.setSeance(seance);
    }
    
    private void prevSeance() {
        sauvegarderSeance();
        if (seanceActuelle > 0) {
            seanceActuelle--;
        } else if (semaineActuelle > 0) {
            semaineActuelle--;
            seanceActuelle = nbSortiesParSemaine - 1;
        }
        chargerSeance();
        updateInfoLabel();
        updateButtons();
    }
    
    private void nextSeance() {
        sauvegarderSeance();
        if (seanceActuelle < nbSortiesParSemaine - 1) {
            seanceActuelle++;
        } else if (semaineActuelle < nbSemaines - 1) {
            semaineActuelle++;
            seanceActuelle = 0;
        }
        chargerSeance();
        updateInfoLabel();
        updateButtons();
    }
    
    private void prevSemaine() {
        sauvegarderSeance();
        if (semaineActuelle > 0) {
            semaineActuelle--;
            seanceActuelle = 0;
            chargerSeance();
            updateInfoLabel();
            updateButtons();
        }
    }
    
    private void nextSemaine() {
        sauvegarderSeance();
        if (semaineActuelle < nbSemaines - 1) {
            semaineActuelle++;
            seanceActuelle = 0;
            chargerSeance();
            updateInfoLabel();
            updateButtons();
        }
    }
    
    private void updateButtons() {
        prevSeanceButton.setEnabled(!(semaineActuelle == 0 && seanceActuelle == 0));
        nextSeanceButton.setEnabled(!(semaineActuelle == nbSemaines - 1 && seanceActuelle == nbSortiesParSemaine - 1));
        prevSemaineButton.setEnabled(semaineActuelle > 0);
        nextSemaineButton.setEnabled(semaineActuelle < nbSemaines - 1);
    }
    
    private void exporterProgramme() {
        sauvegarderSeance();
        
        // Créer le programme
        programme = new ProgrammeCustom("Programme Personnalisé", distanceKm);
        for (List<Seance> semaine : toutesLesSeances) {
            Seance[] seanceArray = semaine.toArray(new Seance[0]);
            programme.addSemaine(seanceArray);
        }
        
        // Créer un profil pour l'export
        Profil profil = new Profil("Expert", nbSortiesParSemaine, vma);
        
        try {
            HtmlGenerator generator = new HtmlGenerator();
            String filename = generator.genererHTMLString(programme, profil);
            
            JOptionPane.showMessageDialog(this,
                "Programme personnalisé exporté en HTML :\n" + filename,
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
            
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'export : " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}