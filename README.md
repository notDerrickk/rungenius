# RunGenius — Générateur de plan d'entraînement (Semi‑Marathon)

Outil Java qui génère automatiquement un programme d'entraînement pour un semi‑marathon à partir d'un profil utilisateur (niveau, sorties/semaine, VMA et objectif). L'application propose une interface graphique simple, produit un aperçu textuel et exporte un programme complet au format HTML prêt à imprimer ou sauvegarder en PDF.

## Fonctionnalités
- Interface Swing minimale pour saisir : niveau, sorties/semaine, VMA et objectif (HH:MM:SS, MM:SS ou minutes).
- Génération d'un plan avec semaines de récupération régulières.
- Export en fichier HTML (mise en page claire, zones d'allure, planning semaine par semaine).
- Calcul d'allures et estimation du kilométrage total.
- Possibilité de sauvegarder/charger un profil.

## Prérequis
- Java 8+ (JDK)
- Aucun autre framework requis

## Compilation et exécution
1. Compiler :
   javac *.java

2. Lancer l'application :
   java Main

3. Utiliser l'interface pour définir votre profil puis cliquer sur "Exporter le programme (HTML)". Le fichier `programme_semi_.html` sera créé dans le répertoire courant.

