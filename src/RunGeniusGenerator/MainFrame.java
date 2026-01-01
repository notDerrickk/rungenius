package RunGeniusGenerator;
import javax.swing.*;

import RunGeniusEditor.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class MainFrame extends JFrame {
    private JComboBox<String> raceTypeCombo;
    private JComboBox<String> niveauCombo;
    private JSpinner sortiesSpinner;
    private JTextField vmaField;
    private JTextField objectifField;
    private JTextField raceDateField;
    private JButton exportButton;

    public MainFrame() {
        setTitle("Run Genius - Préparation");
        setSize(760, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));

        JPanel center = new JPanel(new GridLayout(6, 2, 10, 10));
        center.setBorder(BorderFactory.createTitledBorder("Configuration du profil"));

        center.add(new JLabel("Type de course :"));
        String[] raceTypes = {"5km","10km","Semi-Marathon"};
        raceTypeCombo = new JComboBox<>(raceTypes);
        raceTypeCombo.addActionListener(e -> updateDefaultObjectif());
        center.add(raceTypeCombo);

        center.add(new JLabel("Niveau :"));
        String[] niveaux = {"Débutant (En douceur)", "Novice (Moyen)", "Expert (Intense dès le début)"};
        niveauCombo = new JComboBox<>(niveaux);
        center.add(niveauCombo);

        center.add(new JLabel("Sorties par semaine :"));
        sortiesSpinner = new JSpinner(new SpinnerNumberModel(2, 2, 5, 1));
        center.add(sortiesSpinner);

        center.add(new JLabel("VMA (km/h) :"));
        vmaField = new JTextField("15.0");
        center.add(vmaField);

        center.add(new JLabel("Objectif (HH:MM:SS ou MM:SS ou minutes) :"));
        objectifField = new JTextField("25:00");
        center.add(objectifField);

        center.add(new JLabel("Date de la course (YYYY-MM-DD) :"));
        raceDateField = new JTextField("2026-04-25");
        raceDateField.setToolTipText("Format ISO : 2026-06-14");
        center.add(raceDateField);

        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        exportButton = new JButton("Exporter le programme (HTML)");
        exportButton.addActionListener(this::exporterHTML);
        bottom.add(exportButton);

        JButton editeurButton = new JButton("Éditeur de Programme");
        editeurButton.addActionListener(this::ouvrirEditeur);
        bottom.add(editeurButton);

        add(bottom, BorderLayout.SOUTH);
    }

    private void updateDefaultObjectif() {
        String raceType = (String) raceTypeCombo.getSelectedItem();
        if ("5km".equals(raceType)) {
            objectifField.setText("25:00");
        } else if ("10km".equals(raceType)) {
            objectifField.setText("50:00");
        } else { // Semi-Marathon
            objectifField.setText("2:00:00");
        }
    }

    private void exporterHTML(ActionEvent e) {
        try {
            String raceType = (String) raceTypeCombo.getSelectedItem();
            String niveau = (String) niveauCombo.getSelectedItem();
            int sorties = (Integer) sortiesSpinner.getValue();
            double vma = Double.parseDouble(vmaField.getText());

            Integer objectifSec = parseTimeToSeconds(objectifField.getText().trim());

            String dateStr = raceDateField.getText().trim();
            if (dateStr.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Veuillez entrer la date de la course (YYYY-MM-DD).",
                    "Date manquante",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate raceDate;
            try {
                raceDate = LocalDate.parse(dateStr);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                    "Format de date invalide. Utilisez YYYY-MM-DD.",
                    "Erreur de saisie",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate today = LocalDate.now();
            if (raceDate.isBefore(today)) {
                JOptionPane.showMessageDialog(this,
                    "La date de la course est dans le passé.",
                    "Erreur de date",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            long daysBetween = ChronoUnit.DAYS.between(today, raceDate);
            int weeksBetween = (int) (daysBetween / 7);

            if (weeksBetween < 4) {
                JOptionPane.showMessageDialog(this,
                    "Trop juste pour un plan (moins de 4 semaines) — aucun plan généré.",
                    "Intervalle insuffisant",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (weeksBetween > 52) {
                JOptionPane.showMessageDialog(this,
                    "Intervalle trop long pour ce générateur (plus de 52 semaines) — aucun plan généré.",
                    "Intervalle trop long",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

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
            Profil profil = new Profil(niveau, sorties, vma, objectifSec);
            Programme programme;
            if ("5km".equals(raceType)) {
                programme = new Prepa5k(profil, weeksBetween, distanceKm, title);
            } else if ("10km".equals(raceType)) {
                programme = new Prepa10k(profil, weeksBetween, distanceKm, title);
            } else {
                programme = new SemiMarathon(profil, weeksBetween, distanceKm, title);
            }

            HtmlGenerator generator = new HtmlGenerator();
            String filename = generator.genererHTML(programme, profil);

            int nbSemaines = programme.getSemaines().size();

            JOptionPane.showMessageDialog(this,
                "Programme de " + nbSemaines + " semaines exporté en HTML :\n" + filename,
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            System.exit(0);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Veuillez entrer une VMA valide (nombre).",
                "Erreur de saisie",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'export : " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ouvrirEditeur(ActionEvent e) {
        ConfigProgrammeDialog dialog = new ConfigProgrammeDialog(this);
        dialog.setVisible(true);
        
        if (dialog.isValide()) {
            ProgrammeEditeurFrame editeur = new ProgrammeEditeurFrame(dialog);
            editeur.setVisible(true);
        }
    }

    private Integer parseTimeToSeconds(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            String[] parts = s.split(":");
            if (parts.length == 1) {
                int minutes = Integer.parseInt(parts[0].trim());
                return minutes * 60;
            } else if (parts.length == 2) {
                int minutes = Integer.parseInt(parts[0].trim());
                int seconds = Integer.parseInt(parts[1].trim());
                return minutes * 60 + seconds;
            } else if (parts.length == 3) {
                int hours = Integer.parseInt(parts[0].trim());
                int minutes = Integer.parseInt(parts[1].trim());
                int seconds = Integer.parseInt(parts[2].trim());
                return hours * 3600 + minutes * 60 + seconds;
            }
        } catch (NumberFormatException ex) {
            return null;
        }
        return null;
    }
}