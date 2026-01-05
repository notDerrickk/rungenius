![Java](https://img.shields.io/badge/Java-21%2B-blue)
![Version](https://img.shields.io/badge/Version-v1.0-green)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

# RunGenius — Générateur de plans d'entraînement (v1)

**RunGenius** est une application de bureau Java qui génère des plans d'entraînement pour différentes distances (5 km, 10 km, Semi‑Marathon). La version v1 fournit un générateur automatique basé sur un profil utilisateur et un éditeur minimal pour personnaliser les séances avant export en HTML.

## Table des matières

- 🪧 [À propos](#à-propos)
- 📦 [Prérequis](#prérequis)
- 🚀 [Installation](#installation)
- 🛠️ [Utilisation](#utilisation)
- 🤝 [Contribution](#contribution)
- 🏗️ [Construit avec](#construit-avec)
- 📚 [Documentation](#documentation)
- 🏷️ [Gestion des versions](#gestion-des-versions)
- 📝 [Licence](#licence)

## À propos

Cette version v1 permet de :
- Générer un programme hebdomadaire adapté au niveau et à la VMA de l'utilisateur.
- Calculer des allures (EF, Seuil, VMA) et estimer les kilométrages.
- Exporter le programme final au format HTML imprimable.

## Prérequis

- Java Development Kit (JDK) 21 ou supérieur

## Installation

1. Récupérez le projet (ex. via clonage ou téléchargement) et placez-vous dans le répertoire racine du projet.

2. Compiler les sources et placer les .class dans un dossier `bin` :

```bash
mkdir bin
javac -d bin -sourcepath src src/*.java
```

3. Lancer l'application (exemple) :

```bash
java -cp bin Main
```

Remarque : selon votre organisation des packages, adaptez le chemin `src/...` et la classe principale.

## Utilisation

L'application propose deux modes principaux accessibles depuis l'interface :

### Générateur automatique

1. Lancez l'application.
2. Renseignez le profil : type de course (5k/10k/Semi), niveau, sorties/semaine, VMA, objectif.
3. Cliquez sur "Exporter le programme (HTML)" pour générer le fichier de sortie.

### Éditeur (basique)

1. Ouvrez l'éditeur de programme depuis l'écran d'accueil.
2. Ajustez les séances (échauffement, corps de séance, retour au calme) et les allures.
3. Exportez votre programme personnalisé en HTML.

## Contribution

Les contributions sont les bienvenues :

1. Forkez le dépôt.
2. Créez une branche de fonctionnalité : `git checkout -b feature/ma-fonctionnalite`.
3. Commitez vos changements et ouvrez une Pull Request.

## Construit avec

- Java 21+ (JDK)
- Swing pour l'interface graphique

## Documentation 

- Zones d'allure utilisées : EF (~65% VMA), Seuil (~80–85% VMA), VMA (~95–100%).
- Le `Profil` contient les données du coureur (VMA, sorties/semaine, objectif).
- Les classes clefs se trouvent dans `src/` : préparations (`Prepa5k`, `Prepa10k`, `SemiMarathon`), génération (`HtmlGenerator`), interface (`MainFrame`).

## Gestion des versions

Les versions disponibles ainsi que les journaux décrivant les changements apportés sont disponibles depuis [la page des Releases](https://github.com/notDerrickk/rungenius/releases).

## Licence

Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE) pour les détails.

---

Copyright © Rodéric Neveu (https://github.com/notDerrickk)

