import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class MainFrame extends JFrame {
    private JComboBox<String> niveauCombo;
    private JSpinner sortiesSpinner;
    private JTextField vmaField;
    private JTextField objectifField;
    private JTextField raceDateField;
    private JButton exportButton;

    public MainFrame() {
        setTitle("Run Genius - Préparation Semi-Marathon");
        setSize(760, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));

        JPanel center = new JPanel(new GridLayout(5, 2, 10, 10));
        center.setBorder(BorderFactory.createTitledBorder("Configuration du profil"));

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
        objectifField = new JTextField("2:00:00");
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
        add(bottom, BorderLayout.SOUTH);
    }

    private void exporterHTML(ActionEvent e) {
        try {
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

            if (weeksBetween < 10) {
                JOptionPane.showMessageDialog(this,
                    "Trop juste pour un plan (moins de 10 semaines) — aucun plan généré.",
                    "Intervalle insuffisant",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (weeksBetween > 25) {
                JOptionPane.showMessageDialog(this,
                    "Intervalle trop long pour ce générateur (plus de 25 semaines) — aucun plan généré.",
                    "Intervalle trop long",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            Profil profil = new Profil(niveau, sorties, vma, objectifSec);
            SemiMarathon semi = new SemiMarathon(profil, weeksBetween);

            HtmlGenerator generator = new HtmlGenerator();
            String filename = generator.genererHTML(semi, profil);

            int nbSemaines = semi.getSemaines().size();

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