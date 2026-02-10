# Maven Befehlsübersicht

Dieses Projekt wurde von Gradle auf Maven migriert. Hier sind die wichtigsten Befehle für die Entwicklung und den Build-Prozess.

## Standard Lifecycle

| Befehl | Beschreibung |
| :--- | :--- |
| `mvn clean install` | Bereinigt das Projekt, kompiliert den Code, führt Tests aus und installiert die Artefakte im lokalen Repository. |
| `mvn test` | Führt alle Unit-Tests im gesamten Projekt aus. |
| `mvn compile` | Kompiliert den Quellcode aller Module. |
| `mvn package` | Erstellt die JAR-Dateien in den jeweiligen `target/` Verzeichnissen. |

## Spezielle Optionen

| Befehl | Beschreibung |
| :--- | :--- |
| `mvn clean install -DskipTests` | Führt den Build ohne Tests aus (schneller). |
| `mvn clean install -pl jbi-cli -am` | Baut nur das Modul `jbi-cli` und alle seine benötigten Abhängigkeiten (`also-make`). |
| `mvn test -pl jbi-parser` | Führt nur die Tests für das Modul `jbi-parser` aus. |

## Ausführung der CLI

Nach einem erfolgreichen Build mit `mvn package` kann die CLI wie folgt gestartet werden. Das Modul `jbi-cli` erstellt automatisch ein "shaded" (fat) JAR, das alle Abhängigkeiten enthält:

```bash
java -jar jbi-cli/target/jbi-cli-0.1.0-SNAPSHOT.jar <pfad-zu-klasse-oder-jar>
```

Beispiel:
```bash
java -jar jbi-cli/target/jbi-cli-0.1.0-SNAPSHOT.jar jbi-cli/target/jbi-cli-0.1.0-SNAPSHOT.jar
```

## Projektstruktur

Das Projekt ist als Multi-Module-Projekt organisiert:

- **jbi-utils**: Gemeinsame Hilfsklassen.
- **jbi-model**: Datenmodelle für die Bytecode-Analyse.
- **jbi-parser**: Logik zum Parsen von `.class` Dateien.
- **jbi-io**: Datei- und Ein-/Ausgabe-Operationen.
- **jbi-core**: Kernlogik der Analyse.
- **jbi-report**: Generierung von Analyseberichten.
- **jbi-cli**: Befehlszeilenschnittstelle.
- **jbi-benchmark**: JMH Benchmarks.
- **jbi-examples**: Beispiel-Klassen für Tests.
- **jbi-tests**: Modulübergreifende Integrationstests.

---
*Hinweis: JaCoCo ist derzeit in der `pom.xml` deaktiviert, da die aktuelle Version Java 25 noch nicht vollständig unterstützt.*
