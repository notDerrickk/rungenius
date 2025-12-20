import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainFrame extends JFrame {
    private JComboBox<String> niveauCombo;
    private JSpinner sortiesSpinner;
    private JTextField vmaField;
    private JTextField objectifField;
    private JButton exportButton;

    public MainFrame() {
        setTitle("Run Genius - Préparation Semi-Marathon");
        setSize(760, 360);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));

        JPanel center = new JPanel(new GridLayout(4, 2, 10, 10));
        center.setBorder(BorderFactory.createTitledBorder("Configuration du profil"));

        center.add(new JLabel("Niveau :"));
        String[] niveaux = {"Débutant", "Novice", "Expert"};
        niveauCombo = new JComboBox<>(niveaux);
        center.add(niveauCombo);

        center.add(new JLabel("Sorties par semaine :"));
        sortiesSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 7, 1));
        center.add(sortiesSpinner);

        center.add(new JLabel("VMA (km/h) :"));
        vmaField = new JTextField("15.0");
        center.add(vmaField);

        center.add(new JLabel("Objectif (HH:MM:SS ou MM:SS ou minutes) :"));
        objectifField = new JTextField("2:00:00");
        center.add(objectifField);

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

            Profil profil = new Profil(niveau, sorties, vma, objectifSec);
            SemiMarathon semi = new SemiMarathon(profil);

            HtmlGenerator generator = new HtmlGenerator();
            String filename = generator.genererHTML(semi, profil);

            JOptionPane.showMessageDialog(this,
                "Programme exporté en HTML :\n" + filename,
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