package com.rungenius.model.RunGeniusEditor;
import javax.swing.*;


import java.awt.*;

public class ConfigProgrammeDialog extends JDialog {
    private JTextField objectifField;
    private JTextField vmaField;
    private JTextField allureEFField;
    private JTextField allureSeuilField;
    private JTextField allureVMAField;
    private JSpinner semainesSpinner;
    private JSpinner sortiesSpinner;
    private JComboBox<String> distanceCombo;
    private JButton calculerAlluresButton;
    private JButton okButton;
    private JButton annulerButton;
    
    private boolean valide = false;
    private double vma;
    private double allureEF;
    private double allureSeuil;
    private double allureVMA;
    private int nbSemaines;
    private int nbSortiesParSemaine;
    private double distanceKm;
    
    public ConfigProgrammeDialog(JFrame parent) {
        super(parent, "Configuration du Programme", true);
        setSize(500, 450);
        setLocationRelativeTo(parent);
        
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        int row = 0;
        
        // Distance objectif
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Distance objectif :"), gbc);
        gbc.gridx = 1;
        distanceCombo = new JComboBox<>(new String[]{"5km", "10km", "Semi-Marathon (21.1km)", "Marathon (42.195km)", "Autre"});
        mainPanel.add(distanceCombo, gbc);
        row++;
        
        // Objectif temps
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Objectif temps (MM:SS ou HH:MM:SS) :"), gbc);
        gbc.gridx = 1;
        objectifField = new JTextField("25:00");
        mainPanel.add(objectifField, gbc);
        row++;
        
        // VMA
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("VMA (km/h) :"), gbc);
        gbc.gridx = 1;
        vmaField = new JTextField("15.0");
        mainPanel.add(vmaField, gbc);
        row++;
        
        // Bouton calculer allures
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        calculerAlluresButton = new JButton("Calculer les allures automatiquement");
        calculerAlluresButton.addActionListener(e -> calculerAllures());
        mainPanel.add(calculerAlluresButton, gbc);
        gbc.gridwidth = 1;
        row++;
        
        // Séparateur
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        mainPanel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;
        row++;
        
        // Allure EF
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Allure Endurance Fondamentale (min/km) :"), gbc);
        gbc.gridx = 1;
        allureEFField = new JTextField("6:30");
        mainPanel.add(allureEFField, gbc);
        row++;
        
        // Allure Seuil
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Allure Seuil (min/km) :"), gbc);
        gbc.gridx = 1;
        allureSeuilField = new JTextField("5:20");
        mainPanel.add(allureSeuilField, gbc);
        row++;
        
        // Allure VMA
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Allure VMA (min/km) :"), gbc);
        gbc.gridx = 1;
        allureVMAField = new JTextField("4:00");
        mainPanel.add(allureVMAField, gbc);
        row++;
        
        // Séparateur
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        mainPanel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;
        row++;
        
        // Nombre de semaines
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Nombre de semaines :"), gbc);
        gbc.gridx = 1;
        semainesSpinner = new JSpinner(new SpinnerNumberModel(8, 1, 52, 1));
        mainPanel.add(semainesSpinner, gbc);
        row++;
        
        // Sorties par semaine
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Sorties par semaine :"), gbc);
        gbc.gridx = 1;
        sortiesSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 7, 1));
        mainPanel.add(sortiesSpinner, gbc);
        row++;
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        okButton = new JButton("OK");
        okButton.addActionListener(e -> valider());
        annulerButton = new JButton("Annuler");
        annulerButton.addActionListener(e -> dispose());
        buttonPanel.add(okButton);
        buttonPanel.add(annulerButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void calculerAllures() {
        try {
            double vmaValue = Double.parseDouble(vmaField.getText().trim());
            
            // Calculer les allures basées sur la VMA
            double allureEFValue = 60.0 / (vmaValue * 0.65); // 65% VMA
            double allureSeuilValue = 60.0 / (vmaValue * 0.85); // 85% VMA
            double allureVMAValue = 60.0 / vmaValue; // 100% VMA
            
            allureEFField.setText(formatAllure(allureEFValue));
            allureSeuilField.setText(formatAllure(allureSeuilValue));
            allureVMAField.setText(formatAllure(allureVMAValue));
            
            JOptionPane.showMessageDialog(this,
                "Allures calculées automatiquement selon votre VMA.",
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Veuillez entrer une VMA valide.",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String formatAllure(double minutesPerKm) {
        int minutes = (int) minutesPerKm;
        int seconds = (int) ((minutesPerKm - minutes) * 60);
        return String.format("%d:%02d", minutes, seconds);
    }
    
    private double parseAllure(String allure) throws NumberFormatException {
        String[] parts = allure.trim().split(":");
        if (parts.length == 2) {
            int minutes = Integer.parseInt(parts[0]);
            int seconds = Integer.parseInt(parts[1]);
            return minutes + seconds / 60.0;
        }
        throw new NumberFormatException("Format invalide");
    }
    
    private void valider() {
        try {
            vma = Double.parseDouble(vmaField.getText().trim());
            allureEF = parseAllure(allureEFField.getText());
            allureSeuil = parseAllure(allureSeuilField.getText());
            allureVMA = parseAllure(allureVMAField.getText());
            nbSemaines = (Integer) semainesSpinner.getValue();
            nbSortiesParSemaine = (Integer) sortiesSpinner.getValue();
            
            String distanceStr = (String) distanceCombo.getSelectedItem();
            if (distanceStr.startsWith("5km")) {
                distanceKm = 5.0;
            } else if (distanceStr.startsWith("10km")) {
                distanceKm = 10.0;
            } else if (distanceStr.startsWith("Semi")) {
                distanceKm = 21.1;
            } else if (distanceStr.startsWith("Marathon")) {
                distanceKm = 42.195;
            } else {
                distanceKm = 10.0; 
            }
            
            valide = true;
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Veuillez vérifier les valeurs saisies.",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isValide() { return valide; }
    public double getVma() { return vma; }
    public double getAllureEF() { return allureEF; }
    public double getAllureSeuil() { return allureSeuil; }
    public double getAllureVMA() { return allureVMA; }
    public int getNbSemaines() { return nbSemaines; }
    public int getNbSortiesParSemaine() { return nbSortiesParSemaine; }
    public double getDistanceKm() { return distanceKm; }
    public double getAllureObjectif() {
        try {
            String temps = objectifField.getText().trim();
            String[] parts = temps.split(":");
            double totalMinutes;
            
            if (parts.length == 2) {
                // Format MM:SS
                int minutes = Integer.parseInt(parts[0]);
                int seconds = Integer.parseInt(parts[1]);
                totalMinutes = minutes + seconds / 60.0;
            } else if (parts.length == 3) {
                // Format HH:MM:SS
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                int seconds = Integer.parseInt(parts[2]);
                totalMinutes = hours * 60 + minutes + seconds / 60.0;
            } else {
                return allureSeuil;
            }
            
            return totalMinutes / distanceKm;
        } catch (Exception e) {
            return allureSeuil;
        }
    }
}
