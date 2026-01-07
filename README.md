# RunGenius
Read this in: [Français](README.fr.md)

![Java](https://img.shields.io/badge/Java-21%2B-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.6-green)
![Version](https://img.shields.io/github/v/release/notDerrickk/rungenius)
![License](https://img.shields.io/github/license/notDerrickk/rungenius)

## About

**RunGenius** is a Java/Spring Boot web application designed to support runners in their physical preparation. Whether you are targeting a 5K, 10K, or Half Marathon, RunGenius automatically generates a structured and personalized training plan based on your fitness level, your VMA (Maximal Aerobic Speed), and your scheduling constraints.

The application offers two main features: an **automatic training plan generator** and an **interactive web editor** that allows you to fully create and customize your sessions. The final training calendar can be exported in HTML format for easy tracking and printing.

## Table of Contents

- 🪧 [About](#about)
- 📦 [Requirements](#requirements)
- 🚀 [Installation](#installation)
- 🛠️ [Usage](#usage)
- 🤝 [Contributing](#contributing)
- 🏗️ [Built With](#built-with)
- 📚 [Documentation](#documentation)
- 🏷️ [Versioning](#versioning)
- 📝 [License](#license)

## Requirements

To build and run this project, you will need:

- **Java Development Kit (JDK)**: Version 21 or higher.
  - [Download Java](https://www.oracle.com/java/technologies/downloads/)
- **Apache Maven**: Version 3.6+ for dependency management.
  - [Download Maven](https://maven.apache.org/download.cgi)
- **Git**: To clone the repository.
  - [Download Git](https://git-scm.com/downloads)

## Installation

Follow these steps to install and run the project locally:

1. **Clone the repository**
   ```bash
   git clone https://github.com/notDerrickk/rungenius.git
   cd rungenius
   ```

2. **Build the project with Maven**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**
   
   Open your browser and go to:
   ```
   http://localhost:8080
   ```

## Usage

The web application provides two usage modes accessible from the home page (http://localhost:8080).

### Training Plan Generator (Automatic)

Recommended mode to quickly generate a training plan adapted to your goal:

1. From the home page, fill in the **Profile Configuration** form:
   - **Race type**: Select 5K, 10K or Half Marathon.
   - **Level**: Choose Beginner, Novice or Expert (determines the complexity of interval sessions).
   - **Sessions per week**: From 2 to 5 weekly sessions.
   - **VMA**: Your Maximal Aerobic Speed in km/h (e.g: 15.0).
   - **Goal**: Your target race time (e.g: 50:00 for a 10K).
   - **Race date**: Format YYYY-MM-DD.
2. Click **"Generate program"**.
3. The application automatically calculates:
   - Pace zones (Easy, Threshold, VMA, Race Pace)
   - Progressive weekly volume
   - Interval sessions adapted to your level
4. View your complete calendar with all detailed sessions.
5. Download the program in HTML format to print it or view it offline.

### Training Plan Editor (Custom)

For full control over your training:

1. Click **"Create a custom program"** from the home page.
2. Access the **Editor** web interface (http://localhost:8080/editor).
3. Configure global settings:
   - Program title
   - Race distance (km)
   - VMA and race date
   - Number of weeks
   - Number of sessions per week
4. Use the interactive interface to customize each session:
   - Navigate between weeks using navigation buttons
   - Edit session name, type and description
   - Adjust warm-up (in minutes)
   - Define the main workout (e.g: "5x1000m R:2min")
   - Configure cool-down (in minutes)
   - Select target pace (% VMA or predefined paces)
5. Preview weekly and total mileage in real time.
6. Export your custom program to HTML using the **"Generate program"** button.

## Contributing

Contributions are welcome! Here's how to proceed:

1. Fork the project.
2. Create your feature branch (`git checkout -b feature/MyAwesomeFeature`).
3. Commit your changes (`git commit -m 'Add MyAwesomeFeature'`).
4. Push to the branch (`git push origin feature/MyAwesomeFeature`).
5. Open a Pull Request on the main repository.

## Built With

### Languages & Frameworks

- **[Java 21](https://www.java.com/)** - Main project language.
- **[Spring Boot 3.2.6](https://spring.io/projects/spring-boot)** - Framework for the web application.
- **[Thymeleaf](https://www.thymeleaf.org/)** - Template engine for HTML views.
- **[Maven](https://maven.apache.org/)** - Dependency management and build.

### Architecture

The project follows an MVC (Model-View-Controller) architecture organized into several packages:

- **`controller`**: Contains `ProgramController` which handles web routes (`/`, `/editor`, etc.).
- **`service`**: Business services such as `HtmlGeneratorService` for HTML generation.
- **`model`**:
  - `RunGeniusGenerator`: Logic for algorithmic plan generation (Prepa5k, Prepa10k, SemiMarathon) and the exercise bank.
  - `RunGeniusEditor`: Models for manual editing of custom programs.
  - `dto`: Data Transfer Objects for JSON exchanges.
- **`templates`**: Thymeleaf views (index.html, editor.html, result.html).

### Technical Stack

- **Backend**: Spring Boot with Spring Web MVC
- **Frontend**: HTML5, CSS3, Vanilla JavaScript
- **Templating**: Thymeleaf for server-side rendering
- **Build**: Maven
- **Server**: Embedded Tomcat (via Spring Boot)

## Documentation

### Pace Calculations

The software uses VMA percentages to calculate training zones:
- **Easy Run (Easy)**: ~65% VMA
- **Threshold**: ~80-85% VMA
- **VMA**: 95-100% VMA
- **Race Pace**: Calculated based on goal time and distance.

### Data Structure

- **Profile**: Stores the runner's physiological data.
- **Session**: Represents a training unit (warm-up + main workout + cool-down).
- **Program**: Interface implemented by the different types of preparations (`Prepa5k`, `Prepa10k`, `SemiMarathon`).

## Versioning

Available versions as well as changelogs describing the changes made are available from [the Releases page](https://github.com/notDerrickk/rungenius/releases).

## License

This project is licensed under the MIT License. See the [LICENSE](./LICENSE) file in the repository for more details.


Copyright © Rodéric Neveu (https://github.com/notDerrickk)

