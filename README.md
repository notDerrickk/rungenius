# RunGenius
![Java](https://img.shields.io/badge/Java-8%2B-blue)
![Version](https://img.shields.io/github/v/release/notDerrickk/rungenius)
![License](https://img.shields.io/github/license/notDerrickk/rungenius)

## À propos

**RunGenius** est une application de bureau Java conçue pour accompagner les coureurs dans leur préparation physique. Que vous visiez un 5km, un 10km ou un Semi-Marathon, RunGenius génère automatiquement un plan d'entraînement structuré et personnalisé en fonction de votre niveau, de votre VMA (Vitesse Maximale Aérobie) et de vos contraintes d'emploi du temps.

Le projet inclut également un éditeur complet permettant de modifier manuellement les séances, d'ajuster les allures et d'exporter le programme final sous forme de calendrier HTML imprimable.

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

## Prérequis

Pour compiler et exécuter ce projet, vous aurez besoin des éléments suivants :

- **Java Development Kit (JDK)** : Version 8 ou supérieure.
  - [Télécharger Java](https://www.oracle.com/java/technologies/downloads/)
- **Git** : Pour cloner le dépôt.
  - [Télécharger Git](https://git-scm.com/downloads)

## Installation

Suivez ces étapes pour installer et lancer le projet en local :

1. **Cloner le dépôt**
   ```bash
   git clone https://github.com/notDerrickk/rungenius.git
   cd rungenius
   ```

2. **Compiler les sources**
   Créez un dossier pour les fichiers compilés (par exemple `bin`) et compilez le projet :
   ```bash
   mkdir bin
   javac -d bin -sourcepath src src/RunGeniusGenerator/Main.java
   ```
   *Note : Assurez-vous d'inclure tous les fichiers sources si votre commande javac n'est pas récursive par défaut.*

3. **Lancer l'application**
   ```bash
   java -cp bin RunGeniusGenerator.Main
   ```

## Utilisation

L'application se divise en deux fonctionnalités principales accessibles depuis l'interface d'accueil.

### Générateur de Programme (Automatique)

1. Lancez l'application.
2. Remplissez le formulaire de **Configuration du profil** :
   - **Type de course** : 5km, 10km ou Semi-Marathon.
   - **Niveau** : Débutant, Novice ou Expert (influe sur la complexité des séances de fractionné).
   - **Sorties par semaine** : De 2 à 5 séances.
   - **VMA** : Votre VMA actuelle en km/h (ex: 15.0).
   - **Objectif** : Votre temps cible (ex: 50:00).
   - **Date de la course** : Format YYYY-MM-DD.
3. Cliquez sur **"Exporter le programme (HTML)"**.
4. Un fichier HTML sera généré à la racine du projet contenant votre calendrier complet avec les allures calculées.

### Éditeur de Programme (Manuel)

Pour un contrôle total sur votre entraînement :

1. Cliquez sur **"Éditeur de Programme"** depuis l'écran d'accueil.
2. Configurez les paramètres globaux (Distance, VMA, Allures EF/Seuil/VMA).
3. Utilisez l'interface visuelle pour :
   - Naviguer entre les semaines et les séances.
   - Modifier le type de séance (Endurance, Fractionné, Sortie longue, etc.).
   - Ajuster l'échauffement, le corps de séance et le retour au calme.
4. Exportez votre création personnalisée en HTML.

## Contribution

Les contributions sont les bienvenues ! Voici la marche à suivre :

1. Forkez le projet.
2. Créez votre branche de fonctionnalité (`git checkout -b feature/MaSuperFeature`).
3. Committez vos changements (`git commit -m 'Ajout de MaSuperFeature'`).
4. Pushez vers la branche (`git push origin feature/MaSuperFeature`).
5. Ouvrez une Pull Request sur le dépôt principal.

## Construit avec

### Langages & Frameworks

- **[Java](https://www.java.com/)** - Langage principal du projet.
- **[Swing](https://docs.oracle.com/javase/tutorial/uiswing/)** - Bibliothèque graphique utilisée pour l'interface utilisateur (GUI).

### Architecture

Le projet est structuré en deux packages principaux :
- `RunGeniusGenerator` : Contient la logique de génération algorithmique des plans (Prepa5k, Prepa10k, SemiMarathon) et la banque d'exercices.
- `RunGeniusEditor` : Contient les composants de l'interface graphique pour l'édition manuelle des programmes.

## Documentation

### Calcul des allures

Le logiciel utilise des pourcentages de VMA pour calculer les zones d'entraînement :
- **Endurance Fondamentale (EF)** : ~65% VMA
- **Seuil** : ~80-85% VMA
- **VMA** : 95-100% VMA
- **Allure Spécifique (AS)** : Calculée en fonction de l'objectif de temps et de la distance.

### Structure des données

- **Profil** : Stocke les données physiologiques du coureur.
- **Seance** : Représente une unité d'entraînement (échauffement + corps + retour au calme).
- **Programme** : Interface implémentée par les différents types de préparations (`Prepa5k`, `Prepa10k`, `SemiMarathon`).

## Gestion des versions

Les versions disponibles ainsi que les journaux décrivant les changements apportés sont disponibles depuis [la page des Releases](https://github.com/notDerrickk/rungenius/releases).

## Licence

Ce projet est sous licence MIT. Voir le fichier [LICENSE](./LICENSE) du dépôt pour plus de détails.


Copyright © Rodéric Neveu (https://github.com/notDerrickk)
