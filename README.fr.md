# RunGenius
Lire ce document en : [English](README.md)

![Java](https://img.shields.io/badge/Java-21%2B-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.6-green)
![Version](https://img.shields.io/github/v/release/notDerrickk/rungenius)
![License](https://img.shields.io/github/license/notDerrickk/rungenius)

## À propos

**RunGenius** est une application web Java/Spring Boot conçue pour accompagner les coureurs dans leur préparation physique. Que vous visiez un 5km, un 10km ou un Semi-Marathon, RunGenius génère automatiquement un plan d'entraînement structuré et personnalisé en fonction de votre niveau, de votre VMA (Vitesse Maximale Aérobie) et de vos contraintes d'emploi du temps.

L'application propose deux fonctionnalités principales : un **générateur automatique** de programmes d'entraînement et un **éditeur web interactif** permettant de créer et personnaliser entièrement vos séances. Le calendrier final est exportable en HTML pour un suivi et une impression facilités.

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

- **Java Development Kit (JDK)** : Version 21 ou supérieure.
  - [Télécharger Java](https://www.oracle.com/java/technologies/downloads/)
- **Apache Maven** : Version 3.6+ pour la gestion des dépendances.
  - [Télécharger Maven](https://maven.apache.org/download.cgi)
- **Git** : Pour cloner le dépôt.
  - [Télécharger Git](https://git-scm.com/downloads)

## Installation

Suivez ces étapes pour installer et lancer le projet en local :

1. **Cloner le dépôt**
   ```bash
   git clone https://github.com/notDerrickk/rungenius.git
   cd rungenius
   ```

2. **Compiler le projet avec Maven**
   ```bash
   mvn clean install
   ```

3. **Lancer l'application**
   ```bash
   mvn spring-boot:run
   ```

4. **Accéder à l'application**
   
   Ouvrez votre navigateur et rendez-vous sur :
   ```
   http://localhost:8080
   ```

## Utilisation

L'application web propose deux modes d'utilisation accessibles depuis la page d'accueil (http://localhost:8080).

### Générateur de Programme (Automatique)

Mode recommandé pour générer rapidement un plan d'entraînement adapté à votre objectif :

1. Depuis la page d'accueil, remplissez le formulaire de **Configuration du profil** :
   - **Type de course** : Sélectionnez 5km, 10km ou Semi-Marathon.
   - **Niveau** : Choisissez Débutant, Novice ou Expert (détermine la complexité des séances de fractionné).
   - **Sorties par semaine** : De 2 à 5 séances hebdomadaires.
   - **VMA** : Votre Vitesse Maximale Aérobie en km/h (ex: 15.0).
   - **Objectif** : Votre temps cible pour la course (ex: 50:00 pour un 10km).
   - **Date de la course** : Format YYYY-MM-DD.
2. Cliquez sur **"Générer le programme"**.
3. L'application calcule automatiquement :
   - Les zones d'allure (EF, Seuil, VMA, AS)
   - Le volume hebdomadaire progressif
   - Les séances de fractionné adaptées à votre niveau
### Exporter en FIT
Vous pouvez également exporter les séances au format `.fit` (bouton "Exporter en FIT" dans l'interface). Pour les charger sur une montre Garmin, connectez la montre à votre ordinateur, copiez les fichiers `.fit` dans le dossier `workout` de la montre, puis éjectez-la en toute sécurité avant de la débrancher.
4. Visualisez votre calendrier complet avec toutes les séances détaillées.
5. Téléchargez le programme au format HTML pour l'imprimer ou le consulter hors ligne.

### Éditeur de Programme (Personnalisé)

Pour un contrôle total sur votre entraînement :

1. Cliquez sur **"Créer un programme personnalisé"** depuis la page d'accueil.
2. Accédez à l'interface web de l'**Éditeur** (http://localhost:8080/editor).
3. Configurez les paramètres globaux :
   - Titre du programme
   - Distance de la course (km)
   - VMA et date de course
   - Nombre de semaines
   - Nombre de séances par semaine
4. Utilisez l'interface interactive pour personnaliser chaque séance :
   - Naviguez entre les semaines via les boutons de navigation
   - Modifiez le nom, le type et la description de chaque séance
   - Ajustez l'échauffement (en minutes)
   - Définissez le corps de séance (ex: "5x1000m R:2min")
   - Configurez le retour au calme (en minutes)
   - Sélectionnez l'allure cible (% VMA ou allures prédéfinies)
5. Prévisualisez le kilométrage hebdomadaire et total en temps réel.
6. Exportez votre programme personnalisé en HTML via le bouton **"Générer le programme"**.

## Contribution

Les contributions sont les bienvenues ! Voici la marche à suivre :

1. Forkez le projet.
2. Créez votre branche de fonctionnalité (`git checkout -b feature/MaSuperFeature`).
3. Committez vos changements (`git commit -m 'Ajout de MaSuperFeature'`).
4. Pushez vers la branche (`git push origin feature/MaSuperFeature`).
5. Ouvrez une Pull Request sur le dépôt principal.

## Construit avec

### Langages & Frameworks

- **[Java 21](https://www.java.com/)** - Langage principal du projet.
- **[Spring Boot 3.2.6](https://spring.io/projects/spring-boot)** - Framework pour l'application web.
- **[Thymeleaf](https://www.thymeleaf.org/)** - Moteur de template pour les vues HTML.
- **[Maven](https://maven.apache.org/)** - Gestion des dépendances et build.

### Architecture

Le projet suit une architecture MVC (Model-View-Controller) organisée en plusieurs packages :

- **`controller`** : Contient `ProgramController` qui gère les routes web (`/`, `/editor`, etc.).
- **`service`** : Services métier comme `HtmlGeneratorService` pour la génération de HTML.
- **`model`** :
  - `RunGeniusGenerator` : Logique de génération algorithmique des plans (Prepa5k, Prepa10k, SemiMarathon) et la banque d'exercices.
  - `RunGeniusEditor` : Modèles pour l'édition manuelle de programmes personnalisés.
  - `dto` : Objets de transfert de données (Data Transfer Objects) pour les échanges JSON.
- **`templates`** : Vues Thymeleaf (index.html, editor.html, result.html).

### Stack Technique

- **Backend** : Spring Boot avec Spring Web MVC
- **Frontend** : HTML5, CSS3, JavaScript vanilla
- **Templating** : Thymeleaf pour le rendu côté serveur
- **Build** : Maven
- **Serveur** : Tomcat embarqué (via Spring Boot)

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
