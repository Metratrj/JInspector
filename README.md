# JByteInspector

JByteInspector ist ein modularer Java Bytecode Analyzer, entwickelt für Java 25. Das Projekt dient der statischen Analyse von Java-Klassendateien (.class) und bietet detaillierte Einblicke in die Struktur von Klassen, Methoden, Feldern und Bytecode-Instruktionen.

## Inhaltsverzeichnis

1.  Funktionsumfang
2.  Voraussetzungen
3.  Installation und Build
4.  Benutzung
5.  Projektstruktur
6.  Testen
7.  Architektur

## Funktionsumfang

*   **Java 25 Kompatibilität**: Nutzt aktuelle Sprachfeatures und das Java Platform Module System (JPMS).
*   **Modulare Architektur**: Strikte Trennung von Parsing, Analyse-Logik, Datenmodell und Benutzeroberfläche.
*   **Unabhängiges Parsing**: Implementiert einen eigenständigen Bytecode-Parser ohne Abhängigkeiten zu Drittbibliotheken wie ASM oder BCEL für die Kernfunktionalität.
*   **Typsichere Berichte**: Bereitstellung der Analyseergebnisse durch Java Records.
*   **Automatisierung**: Build- und Testprozesse basieren auf Gradle 9.2.0 und JUnit 5.

## Voraussetzungen

*   **JDK 25**: Muss installiert und über die Umgebungsvariable JAVA_HOME oder die Gradle Toolchain verfügbar sein.
*   **Betriebssystem**: Kompatibel mit Linux, macOS und Windows.

## Installation und Build

Das Projekt verwendet den Gradle Wrapper, wodurch eine lokale Gradle-Installation nicht erforderlich ist.

### Kompilierung und Build

Führen Sie folgenden Befehl aus, um das gesamte Projekt zu bauen:

```bash
./gradlew build
```

Um das Projekt nur zu kompilieren, ohne Tests auszuführen:

```bash
./gradlew assemble
```

## Benutzung

Die Kommandozeilenanwendung (CLI) dient als primäre Schnittstelle. Sie ermöglicht die Analyse von Verzeichnissen (rekursiv), einzelnen .class-Dateien oder .jar-Archiven.

### Syntax

```bash
./gradlew :jbi-cli:run --args="<pfad>"
```

### Anwendungsbeispiele

**Analyse eines Verzeichnisses**
```bash
./gradlew :jbi-cli:run --args="path/to/classes"
```

**Analyse eines JAR-Archivs**
```bash
./gradlew :jbi-cli:run --args="path/to/library.jar"
```

**Analyse einer Einzeldatei**
```bash
./gradlew :jbi-cli:run --args="path/to/MyClass.class"
```

### Beispielausgabe

```text
Inspecting: /path/to/classes
--------------------------------------------------
Class: xyz/metratrj/jbyteinspector/examples/animals/Katze
Super: xyz/metratrj/jbyteinspector/examples/animals/Tier
Flags: [SYNCHRONIZED, PUBLIC]

Fields:
  [PUBLIC] Leben

Methods:
  [PUBLIC] <init> (Ljava/lang/String;)V
  [PUBLIC] MachLaut ()V
```

## Projektstruktur

Das Projekt ist als Gradle Monorepo unter dem Wurzelverzeichnis `jbyteinspector/` organisiert.

| Modul | Beschreibung |
| :--- | :--- |
| **jbi-model** | Definiert das Domänenmodell und öffentliche Datenstrukturen (z. B. ClassReport). |
| **jbi-parser** | Implementiert das Low-Level-Parsing von .class-Dateien (Constant Pool, Attribute). |
| **jbi-core** | Beinhaltet die Kernlogik der Analyse und orchestriert Parser sowie Berichterstellung. |
| **jbi-io** | Stellt Funktionen für Dateisystemoperationen und das Einlesen von Ressourcen bereit. |
| **jbi-report** | Verantwortlich für die Aufbereitung und den Export der Analyseergebnisse. |
| **jbi-cli** | Implementiert die Kommandozeilenschnittstelle für den Endanwender. |
| **jbi-utils** | Enthält allgemeine Hilfsfunktionen ohne Domänenabhängigkeit. |
| **jbi-examples** | Beinhaltet Beispielcode zur Validierung und Demonstration. |
| **jbi-benchmark** | Performance-Tests auf Basis von JMH. |
| **jbi-tests** | Führt integrationsübergreifende Tests durch. |

## Testen

Die Qualitätssicherung erfolgt durch automatisierte Tests.

**Ausführung aller Tests:**
```bash
./gradlew test
```

**Testen eines spezifischen Moduls:**
```bash
./gradlew :jbi-parser:test
```

## Architektur

Detaillierte Informationen zur Systemarchitektur und den Designentscheidungen befinden sich in der Datei `docs/ARCHITECTURE.md`.