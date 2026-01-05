package com.rungenius.service;

import com.rungenius.model.RunGeniusGenerator.Programme;
import com.rungenius.model.RunGeniusGenerator.Profil;
import com.rungenius.model.RunGeniusGenerator.Seance;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class HtmlGeneratorService {

    public String generateHtmlString(Programme programme, Profil profil) {
        StringBuilder html = new StringBuilder();

        append(html, "<!doctype html>");
        append(html, "<html lang='fr'>");
        append(html, "<head>");
        append(html, "  <meta charset='utf-8'>");
        append(html, "  <meta name='viewport' content='width=device-width, initial-scale=1'>");
        append(html, "  <title>RunGenius - Programme " + escapeHtml(programme.getTitle()) + "</title>");
        append(html, "  <style>");
        append(html, "    :root {");
        append(html, "      --bg: linear-gradient(135deg,#e6f0ff 0%, #f7f9fb 100%);");
        append(html, "      --card: #ffffff;");
        append(html, "      --primary: #2b8aef;");
        append(html, "      --muted: #6b7280;");
        append(html, "      --green: #2ecc71;");
        append(html, "    }");
        append(html, "    *{box-sizing:border-box;margin:0;padding:0}");
        append(html, "    body{font-family: Inter, Arial, Helvetica, sans-serif; background:var(--bg); color:#111; padding:20px}");
        append(html, "    .container{max-width:1100px;margin:0 auto}");
        append(html, "    header{background:linear-gradient(135deg,var(--primary),#764ba2); color:white;padding:26px;border-radius:12px;box-shadow:0 10px 30px rgba(15,23,42,0.18)}");
        append(html, "    header h1{font-size:22px;margin-bottom:6px}");
        append(html, "    .card{background:var(--card);border-radius:12px;padding:18px;margin-top:18px;box-shadow:0 6px 18px rgba(20,30,60,0.06)}");
        append(html, "    .week{border-left:6px solid var(--primary);padding:14px;margin-bottom:14px;border-radius:10px;background:#fff}");
        append(html, "    .week.recup{border-left-color:var(--green);background:linear-gradient(180deg,#f1fff4,#fffef9)}");
        append(html, "    .seance{display:flex;justify-content:space-between;background:#fbfdff;padding:12px;border-radius:8px;margin-bottom:8px;align-items:flex-start;gap:12px}");
        append(html, "    .type{font-weight:700;color:#174ea6}");
        append(html, "    .allure{background:linear-gradient(90deg,var(--primary),#764ba2);color:white;padding:6px 10px;border-radius:999px;font-weight:700;font-size:12px;display:inline-block}");
        append(html, "  </style>");
        append(html, "</head>");
        append(html, "<body>");
        append(html, "  <div class='container'>");

        append(html, "    <header>");
        append(html, "      <h1>🏃 RunGenius — Programme " + escapeHtml(programme.getTitle()) + "</h1>");
        append(html, "      <p>Généré: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "</p>");
        append(html, "    </header>");

        append(html, "    <div style='margin-top:20px'>");
        append(html, "      <h2 style='text-align:center'>Planning détaillé</h2>");

        List<Seance[]> semaines = programme.getSemaines();
        int totalSemaines = semaines.size();
        int seanceIndex = 1;
        for (int i = 0; i < totalSemaines; i++) {
            Seance[] semaine = semaines.get(i);
            int numero = i + 1;
            boolean recup = (numero % 5 == 0);

            append(html, "      <div class='week card" + (recup ? " recup" : "") + "'>");
            append(html, "        <div style='display:flex;justify-content:space-between;align-items:center;margin-bottom:10px'>");
            append(html, "          <div style='font-size:16px;font-weight:700'>Semaine " + numero + (recup ? " — RÉCUP" : "") + "</div>");
            append(html, "          <div style='color:var(--muted)'>Séances: " + semaine.length + "</div>");
            append(html, "        </div>");

            for (int j = 0; j < semaine.length; j++) {
                Seance se = semaine[j];
                String nomSeance = escapeHtml(se.getNom());
                String description = escapeHtml(se.getDescription(profil)).replace("\n", "<br>");
                String allure = escapeHtml(profil.getAllureFormatee(se.getPourcentageVMA()));

                double kmSeance = se.getDistanceKm(profil);

                append(html, "        <div class='seance'>");
                append(html, "          <div class='left'>");
                append(html, "            <div class='type'>Séance " + seanceIndex + " - " + nomSeance + "</div>");
                append(html, "            <div style='color:#374151;margin-top:6px;font-size:14px;'>" + description + "</div>");
                append(html, "          </div>");
                append(html, "          <div class='right' style='text-align:right'>");
                append(html, "            <div class='allure'>" + allure + "</div>");
                append(html, "            <div class='distance' style='font-weight:700;margin-top:6px;color:var(--primary)'>" + String.format(Locale.US, "%.2f km", kmSeance) + "</div>");
                append(html, "          </div>");
                append(html, "        </div>");

                seanceIndex++;
            }

            append(html, "      </div>");
        }

        append(html, "    </div>");

        append(html, "    <div style='margin-top:26px;text-align:center;color:#6b7280;font-size:13px'>Généré par RunGenius — " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "</div>");
        append(html, "  </div>");
        append(html, "</body>");
        append(html, "</html>");

        return html.toString();
    }

    private void append(StringBuilder sb, String line) {
        sb.append(line).append("\n");
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}