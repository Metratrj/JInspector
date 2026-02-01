# JByteInspector

**JByteInspector** ist ein modularer, produktionsreifer **Java Bytecode Analyzer**, entwickelt für **Java 25**.
Das Projekt dient der statischen Analyse von Java-Klassendateien (`.class`) und bietet detaillierte Einblicke in die Struktur von Klassen, Methoden, Feldern und Bytecode-Instruktionen.

## 🚀 Features

*   **Java 25 Ready:** Nutzt die neuesten Sprachfeatures und das Java Platform Module System (JPMS).
*   **Modulare Architektur:** Klare Trennung von Parsing, Analyse-Logik, API und UI/CLI.
*   **Clean Parsing:** Eigener Bytecode-Parser (keine Abhängigkeiten zu ASM oder BCEL für den Core).
*   **Strukturierte Reports:** Analyseergebnisse als typsichere Java Records.
*   **Build & Test:** Vollständig automatisiert mit Gradle 9.2.0 und JUnit 5.

## 📋 Voraussetzungen

*   **JDK 25** (muss installiert und via `JAVA_HOME` oder Toolchain verfügbar sein).
*   Linux, macOS oder Windows.

## 🛠️ Build & Installation

Das Projekt nutzt den **Gradle Wrapper**, sodass keine lokale Gradle-Installation notwendig ist.

```bash
# Projekt bauen
./gradlew build

# Nur kompilieren
./gradlew assemble
```

## 💻 Benutzung (CLI)

Die CLI-Applikation ist der primäre Einstiegspunkt. Sie analysiert ein Verzeichnis (rekursiv), eine einzelne `.class`-Datei oder ein `.jar`-Archiv.

### Syntax
```bash
./gradlew :cli-app:run --args="<pfad-zu-den-klassen-oder-jar>"
```

### Beispiele
*   **Verzeichnis:** `./gradlew :cli-app:run --args="path/to/classes"`
*   **JAR-Datei:** `./gradlew :cli-app:run --args="path/to/library.jar"`
*   **Einzeldatei:** `./gradlew :cli-app:run --args="path/to/MyClass.class"`

**Output:**
```text
Inspecting: /.../JByteInspector/examples/build/classes/java/main
--------------------------------------------------
Class: xyz/metratrj/jbyteinspector/examples/animals/Katze
Super: xyz/metratrj/jbyteinspector/examples/animals/Tier
Flags: [SYNCHRONIZED, PUBLIC]

Fields:
  [PUBLIC] Leben

Methods:
  [PUBLIC] <init> (Ljava/lang/String;)V
  [PUBLIC] MachLaut ()V
...
```

## 📂 Projektstruktur

Das Projekt ist als **Gradle Monorepo** organisiert:

| Modul | Beschreibung |
|-------|--------------|
| `core-utils` | Allgemeine Hilfsfunktionen (z. B. File-IO). Keine Fachlogik. |
| `bytecode-parser` | Low-Level Parsing von `.class` Dateien. Liest Constant Pool, Attribute, etc. |
| `analysis-api` | Öffentliche Schnittstellen und Datenmodelle (`ClassReport`, `MethodReport`). |
| `analysis-engine` | Die "Business Logik". Orchestriert Parser und generiert Reports. |
| `cli-app` | Command Line Interface. Entry-Point für den Benutzer. |
| `examples` | Test-Code und Beispiele für Benchmarks und Validierung. |
| `benchmarks` | JMH Benchmarks zur Performance-Messung. |
| `tests` | Integrations-Tests über mehrere Module hinweg. |

Weitere Details zur Architektur finden sich in [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## 🧪 Tests

```bash
# Alle Tests ausführen
./gradlew test

# Spezifisches Modul testen
./gradlew :bytecode-parser:test
```

## 🤝 Contributing

1.  Forken & Clonen.
2.  Feature Branch erstellen (`git checkout -b feature/AmazingFeature`).
3.  Änderungen committen (Bitte [Conventional Commits](https://www.conventionalcommits.org/) nutzen).
4.  Push & Pull Request.

---
*Erstellt für das JByteInspector Projekt (2026).*
