package com.rungenius.model.RunGeniusEditor;

import javax.swing.*;
import java.awt.*;

import com.rungenius.model.RunGeniusGenerator.*;

public class SeanceEditeurPanel extends JPanel {
    private JTextField nomField;
    private JComboBox<String> typeCombo;
    private JSpinner echauffementSpinner;
    private JTextArea corpsArea;
    private JComboBox<String> allureCombo;
    private JSpinner cooldownSpinner;
    
    protected String[] allureLabels;
    protected double[] allureValues;
    
    public SeanceEditeurPanel(String[] allureLabels, double[] allureValues) {
        this.allureLabels = allureLabels;
        this.allureValues = allureValues;
        
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Édition de séance"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        int row = 0;
        
        // Nom
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Nom de la séance :"), gbc);
        gbc.gridx = 1;
        nomField = new JTextField("Séance");
        gbc.weightx = 1.0;
        add(nomField, gbc);
        gbc.weightx = 0;
        row++;
        
        // Type
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Type :"), gbc);
        gbc.gridx = 1;
        String[] types = {"Endurance", "Fractionné", "Sortie longue", "Seuil", "VMA", "Récupération", "Autre"};
        typeCombo = new JComboBox<>(types);
        add(typeCombo, gbc);
        row++;
        
        // Échauffement
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Échauffement (min) :"), gbc);
        gbc.gridx = 1;
        echauffementSpinner = new JSpinner(new SpinnerNumberModel(15, 0, 60, 5));
        add(echauffementSpinner, gbc);
        row++;
        
        // Corps de séance
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(new JLabel("Corps de séance :"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        corpsArea = new JTextArea(4, 20);
        corpsArea.setLineWrap(true);
        corpsArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(corpsArea);
        add(scrollPane, gbc);
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        row++;
        
        // Allure
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Allure :"), gbc);
        gbc.gridx = 1;
        allureCombo = new JComboBox<>(allureLabels);
        add(allureCombo, gbc);
        row++;
        
        // Cooldown
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Retour au calme (min) :"), gbc);
        gbc.gridx = 1;
        cooldownSpinner = new JSpinner(new SpinnerNumberModel(10, 0, 60, 5));
        add(cooldownSpinner, gbc);
        row++;
    }
    
    public Seance getSeance() {
        String nom = nomField.getText().trim();
        if (nom.isEmpty()) nom = "Séance";
        
        String type = (String) typeCombo.getSelectedItem();
        int echauffement = (Integer) echauffementSpinner.getValue();
        String corps = corpsArea.getText().trim();
        int cooldown = (Integer) cooldownSpinner.getValue();
        
        int allureIndex = allureCombo.getSelectedIndex();
        double pourcentageVMA = allureValues[allureIndex];
        
        return new Seance(nom, type, echauffement, corps, cooldown, pourcentageVMA);
    }
    
    public void setSeance(Seance seance) {
        if (seance == null) {
            clear();
            return;
        }
        
        nomField.setText(seance.getNom());
        typeCombo.setSelectedItem(seance.getType());
        echauffementSpinner.setValue(seance.getDureeEchauffement());
        
        // Extraire le corps de séance
        String desc = seance.getCorps();
        corpsArea.setText(desc != null ? desc : "");
        
        // Trouver l'allure correspondante
        double seancePct = seance.getPourcentageVMA();
        int closestIndex = 0;
        double minDiff = Math.abs(allureValues[0] - seancePct);
        for (int i = 1; i < allureValues.length; i++) {
            double diff = Math.abs(allureValues[i] - seancePct);
            if (diff < minDiff) {
                minDiff = diff;
                closestIndex = i;
            }
        }
        allureCombo.setSelectedIndex(closestIndex);
        
        cooldownSpinner.setValue(seance.getDureeCooldown());
    }
    
    public void clear() {
        nomField.setText("Séance");
        typeCombo.setSelectedIndex(0);
        echauffementSpinner.setValue(15);
        corpsArea.setText("");
        allureCombo.setSelectedIndex(0);
        cooldownSpinner.setValue(10);
    }
}