# Projektantrag: JByteInspector

## 1. Projektbezeichnung

**Entwicklung eines Java-Bytecode-Analyzers zur statischen Untersuchung von Klassenhierarchien und Instruktionsmetriken.**

## 2. Projektbeschreibung

**Ausgangslage:**

Nach der Kompilierung von Java-Quellcode liegt dieser als Bytecode in `.class`-Dateien vor. Für Entwickler ist es oft schwer nachzuvollziehen, wie der Java-Compiler (javac) Optimierungen vornimmt oder welche Abhängigkeiten auf Bytecode-Ebene tatsächlich bestehen. Bestehende Tools wie `javap` sind oft rein textbasiert und für schnelle, statistische Auswertungen unhandlich.
*Siehe detaillierte [[Ist-Analyse]].*

**Zielsetzung:**

Ziel des Projekts ist die Entwicklung der Java-Anwendung **JByteInspector**. Diese soll `.class`-Dateien automatisiert einlesen, deren interne Struktur analysieren und die gewonnenen Informationen (wie Klassenhierarchien, Methodensignaturen und Instruktionsstatistiken) übersichtlich aufbereiten. Das Tool soll Entwicklern helfen, die Komplexität und den Aufbau von kompilierten Klassen effizient zu verstehen.
*Siehe [[Soll-Konzept]] und [[diagrams/Use Cases & User Stories|Use Cases & User Stories]].*

## 3. Zeitplanung (100 Stunden)

| **Phase**              | **Aufgabe**                                                            | **Stunden** | **Summe** |
| ---------------------- | ---------------------------------------------------------------------- | ----------- | --------- |
| **1. Analyse**         | [[Ist-Analyse]], Problemstellung                                       | 3h          | **8h**    |
|                        | Technologie-Recherche & Auswahl ([[Opcodes]])                          | 5h          |           |
| **2. Entwurf**         | Architekturdesign ([[Architektur]]) & [[Soll-Konzept]]                 | 6h          | **14h**   |
|                        | Klassendiagramme & [[diagrams/Use Cases & User Stories\|User Stories]] | 8h          |           |
| **3. Implementierung** | Projekt-Setup & Basis-Infrastruktur                                    | 5h          | **58h**   |
|                        | Kern-Entwicklung (Bytecode-Parsing)                                    | 18h         |           |
|                        | Implementierung der Analyse-Algorithmen                                | 15h         |           |
|                        | UI-Entwicklung (CLI/GUI) & Reporting-System                            | 10h         |           |
|                        | Qualitätssicherung (Unit-Tests & Bugfixing)                            | 10h         |           |
| **4. Dokumentation**   | Erstellung der technischen Projektdokumentation                        | 12h         | **20h**   |
|                        | Erstellung des Anwenderhandbuchs                                       | 4h          |           |
|                        | Projektabschlussbericht & Reflexion                                    | 4h          |           |
| **Gesamt**             |                                                                        |             | **100h**  |

---
**Projektstatus & Aufgaben:** [[Kanban]]
**Technische Dokumentation:** [[Architektur]]
**Erste Schritte:** [[Erste Schritte - Dokumentation]]