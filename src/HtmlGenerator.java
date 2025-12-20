import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HtmlGenerator {
    public String genererHTML(SemiMarathon semi, Profil profil) throws IOException {
        String filename = "programme_semi_"+".html";

        StringBuilder html = new StringBuilder();

        // Début du document
        append(html, "<!doctype html>");
        append(html, "<html lang='fr'>");
        append(html, "<head>");
        append(html, "  <meta charset='utf-8'>");
        append(html, "  <meta name='viewport' content='width=device-width, initial-scale=1'>");
        append(html, "  <title>RunGenius - Programme Semi-Marathon</title>");

        // Styles CSS
        append(html, "  <style>");
        append(html, "    :root {");
        append(html, "      --bg: linear-gradient(135deg,#e6f0ff 0%, #f7f9fb 100%);");
        append(html, "      --card: #ffffff;");
        append(html, "      --primary: #2b8aef;");
        append(html, "      --accent: #ff6b6b;");
        append(html, "      --muted: #6b7280;");
        append(html, "      --green: #2ecc71;");
        append(html, "    }");
        append(html, "    *{box-sizing:border-box;margin:0;padding:0}");
        append(html, "    body{font-family: Inter, Arial, Helvetica, sans-serif; background:var(--bg); color:#111; padding:20px}");
        append(html, "    .container{max-width:1100px;margin:0 auto}");
        append(html, "    header{background:linear-gradient(135deg,var(--primary),#764ba2); color:white;padding:26px;border-radius:12px;box-shadow:0 10px 30px rgba(15,23,42,0.18)}");
        append(html, "    header h1{font-size:22px;margin-bottom:6px}");
        append(html, "    header p{opacity:0.9;margin-top:4px}");
        append(html, "    .meta{margin-top:12px;display:flex;flex-wrap:wrap;gap:8px}");
        append(html, "    .chip{background:rgba(255,255,255,0.12);padding:8px 12px;border-radius:999px;font-weight:600}");
        append(html, "    .card{background:var(--card);border-radius:12px;padding:18px;margin-top:18px;box-shadow:0 6px 18px rgba(20,30,60,0.06)}");
        append(html, "    .grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(200px,1fr));gap:12px}");
        append(html, "    .week{border-left:6px solid var(--primary);padding:14px;margin-bottom:14px;border-radius:10px;background:#fff}");
        append(html, "    .week.recup{border-left-color:var(--green);background:linear-gradient(180deg,#f1fff4,#fffef9)}");
        append(html, "    .seance{display:flex;justify-content:space-between;background:#fbfdff;padding:12px;border-radius:8px;margin-bottom:8px;align-items:flex-start}");
        append(html, "    .seance .left{max-width:72%}");
        append(html, "    .type{font-weight:700;color:#174ea6}");
        append(html, "    .allure{background:linear-gradient(90deg,var(--primary),#764ba2);color:white;padding:6px 10px;border-radius:999px;font-weight:700}");
        append(html, "    .footer{margin-top:26px;text-align:center;color:var(--muted);font-size:13px}");
        append(html, "    .btn-print{display:block;margin:12px auto;border-radius:24px;padding:10px 16px;border:none;background:linear-gradient(90deg,var(--primary),#764ba2);color:white;cursor:pointer}");
        append(html, "    @media(max-width:760px){.seance{flex-direction:column}.seance .left{max-width:100%}}");
        append(html, "  </style>");

        append(html, "</head>");
        append(html, "<body>");
        append(html, "  <div class='container'>");

        // Header
        append(html, "    <header>");
        append(html, "      <h1>🏃 RunGenius — Programme Semi-Marathon</h1>");
        append(html, "      <p>Progression douce • Semaines de repos toutes les 5 semaines</p>");
        append(html, "      <div class='meta'>");
        append(html, "        <div class='chip'>Niveau: " + profil.getNiveau() + "</div>");
        append(html, "        <div class='chip'>Sorties / semaine: " + profil.getSortiesParSemaine() + "</div>");
        append(html, "        <div class='chip'>VMA: " + String.format("%.1f km/h", profil.getVma()) + "</div>");
        append(html, "        <div class='chip'>Généré: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "</div>");
        append(html, "      </div>");
        append(html, "    </header>");

        // Résumé court
        double kmEstime = estimerKilometrage(semi, profil);
        append(html, "    <div class='card'>");
        append(html, "      <div style='display:flex;justify-content:space-between;align-items:center'>");
        append(html, "        <div>");
        append(html, "          <strong>Profil</strong><br>");
        append(html, "          <span style='color:var(--muted)'>" + profil.getNiveau() + " — " + profil.getSortiesParSemaine() + " sorties / semaine</span>");
        append(html, "        </div>");
        append(html, "        <div style='text-align:right'>");
        append(html, "          <div style='font-weight:800;font-size:18px'>" + String.format("%.1f km", kmEstime) + "</div>");
        append(html, "          <div style='color:var(--muted);font-size:13px'>Kilométrage estimé total</div>");
        append(html, "        </div>");
        append(html, "      </div>");
        append(html, "    </div>");

        // Zones d'allure
        append(html, "    <div class='card'>");
        append(html, "      <h3>Zones d'allure</h3>");
        append(html, "      <div class='grid' style='margin-top:10px'>");
        append(html, createAllureCard("Endurance (EF)", profil.getAlluresPrincipales(21.1)[0], "#4caf50"));
        append(html, createAllureCard("Allure spécifique (semi)", profil.getAlluresPrincipales(21.1)[1], "#ffc107"));
        append(html, createAllureCard("Seuil", profil.getAlluresPrincipales(21.1)[2], "#ff9800"));
        append(html, createAllureCard("VMA (100%)", profil.getAlluresPrincipales(21.1)[3], "#f44336"));
        append(html, "      </div>");
        append(html, "    </div>");

        // Bouton imprimer
        append(html, "    <button class='btn-print no-print' onclick='window.print()'>🖨️ Imprimer / Sauvegarder en PDF</button>");

        // Planning semaine par semaine
        append(html, "    <div style='margin-top:20px'>");
        append(html, "      <h2 style='text-align:center'>Planning détaillé</h2>");

        List<Seance[]> semaines = semi.getSemaines();
        int totalSemaines = semaines.size();
        int seanceIndex = 1;
        for (int i = 0; i < totalSemaines; i++) {
            Seance[] semaine = semaines.get(i);
            int numero = i + 1;
            boolean recup = isSemaineRecup(numero, totalSemaines);

            append(html, "      <div class='week card" + (recup ? " recup" : "") + "'>");
            append(html, "        <div style='display:flex;justify-content:space-between;align-items:center;margin-bottom:10px'>");
            append(html, "          <div style='font-size:16px;font-weight:700'>Semaine " + numero + (recup ? " — RÉCUP" : "") + "</div>");
            append(html, "          <div style='color:var(--muted)'>Séances: " + semaine.length + "</div>");
            append(html, "        </div>");

            // Séances
            for (int j = 0; j < semaine.length; j++) {
                Seance se = semaine[j];
                String nomSeance = se.getNom();
                String typeSeance = se.getType();
                String description = se.getDescription(profil);
                String allure = profil.getAllureFormatee(se.getPourcentageVMA());

                append(html, "        <div class='seance" + (recup ? " recup-seance" : "") + "'>");
                append(html, "          <div class='left'>");
                append(html, "            <div class='type'>" + typeSeance + " — " + nomSeance + "</div>");
                append(html, "            <div style='color:#374151;margin-top:6px;font-size:14px;'>" + (description == null ? "" : description.replace("\n", "<br>")) + "</div>");
                append(html, "          </div>");
                append(html, "          <div style='text-align:right'>");
                append(html, "            <div class='allure'>" + allure + "</div>");
                append(html, "            <div style='font-size:12px;color:var(--muted);margin-top:6px;'>Séance " + seanceIndex + "</div>");
                append(html, "          </div>");
                append(html, "        </div>");

                seanceIndex++;
            }

            append(html, "      </div>");
        }

        append(html, "    </div>"); // fin planning

        // Footer 
        append(html, "    <div class='footer'>Généré par RunGenius — " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "</div>");
        append(html, "  </div>");

        append(html, "  <script>");
        append(html, "    document.querySelectorAll('.seance input[type=\"checkbox\"]').forEach(cb => {");
        append(html, "      cb.addEventListener('change', function() {");
        append(html, "        var el = this.closest('.seance');");
        append(html, "        if (this.checked) { el.style.opacity = '0.6'; el.style.textDecoration = 'line-through'; }");
        append(html, "        else { el.style.opacity = '1'; el.style.textDecoration = 'none'; }");
        append(html, "      });");
        append(html, "    });");
        append(html, "  </script>");

        append(html, "</body>");
        append(html, "</html>");

        // Écriture sur disque
        try (FileWriter fw = new FileWriter(filename)) {
            fw.write(html.toString());
        }

        return filename;
    }


    private static void append(StringBuilder sb, String line) {
        sb.append(line).append("\n");
    }


    // Carte d'allure
    private static String createAllureCard(String label, String pace, String color) {
        String bg = (color == null || color.isEmpty()) ? "#ffffff" : color.trim();
        StringBuilder b = new StringBuilder();
        b.append("        <div class='card' style='text-align:center;padding:12px;border-radius:12px;background:" + bg + ";'>");
        b.append("<div style='font-size:12px;color:rgba(255,255,255,0.9)'>" + label + "</div>");
        b.append("<div style='font-weight:800;margin-top:8px;color:#ffffff'>" + pace + "</div>");
        b.append("</div>");
        return b.toString();
    }

    // Estimation du kilométrage total 
    private static double estimerKilometrage(SemiMarathon semi, Profil profil) {
        double totalKm = 0.0;
        List<Seance[]> semaines = semi.getSemaines();
        for (Seance[] sArr : semaines) {
            for (Seance s : sArr) {
                int mins = 30;
                try {
                    mins = s.getDureeTotal();
                } catch (Exception e) {
                    // fallback à 30 min si indisponible
                    mins = 30;
                }
                double hours = mins / 60.0;
                double vitesse = profil.getVma() * s.getPourcentageVMA(); // km/h
                totalKm += vitesse * hours;
            }
        }
        return totalKm;
    }

    // Semaine de récupération
    private static boolean isSemaineRecup(int numeroSemaine, int totalSemaines) {
        if (numeroSemaine > totalSemaines - 2) return false;
        return (numeroSemaine % 5 == 0);
    }
}