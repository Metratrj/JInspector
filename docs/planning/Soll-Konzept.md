## 3.1.1 Definition des Zielzustands
Das Ziel ist die Entwicklung von JByteInspector, einem Werkzeug, das die "Black Box" der kompilierten `.class`-Dateien transparent mach. Im Gegensatz zu `javap` liefert es keine unstrukturierten Textwüsten, sondern aggregierte Daten und visuelle Einblicke.
## 3.1.2 Der neue Workflow
1. Der Nutzer gibt einen Pfad (Datei, Verzeichnis oder `.jar`) an
2. Das Tool scannt alle enthaltenen Klassen automatisiert.
3. Die Ergebnisse werden strukturiert (z.B. als Tabelle oder Baumstruktur) ausgegeben.
4. Statistische Ausreißer (z.B. große Methoden) werden markiert.

## 3.2.1 Funktionale Anforderungen
**Prio 1: Kernfunktionen (Must-have)**
- **FA1 - Multi-Source-Import:** Einlesen von einzelnen `.class`-Dateien, ganze Ordnerstrukturen und `.jar`-Archiven.
- **FA2 - Struktur-Extraktion:** Anzeige von Klassenname, Sichtbarkeit (public/private), Interfaces und Superklassen
- **FA3 - Instruktions-Statistik:** Zählen und Kategorisieren von [[Opcodes]] (z.B. wie viel Prozent sind `Field Access`, wie viele `Method Invocations`, wie viele `Arithmetic`).
- **FA4 - Support für Modern Java:** Volle Kompatibilität mit JDK 25 
**Prio 2: Erweiterte Analyse (Should-have)**
- **FA5 - Call-Graph-Basis:** Auflistung aller von einer Klasse aufgerufenen Methoden (Abhängigkeitsanalyse)
- **FA6 - Kompexitätsmetrik:** Berechnung einer einfachen Metrik (z.B. Anzahl an Sprüngen pro Methode), um Code-Komplexität zu bewerten
- **FA7 - Suchfunktion:** Suche nach spezifischen Instruktionen (z.B. Wo im Bytecode wird `invokedynamic` genutzt?)
**Prio 3: Komfortfunktionen (Nice-to-have)**
- **FA8 - Export-Funktion:** Speichern der Analyseergebnissen
- **FA9 - GUI-Prototyp:** Eine einfache JavaFX-Oberfläcge zur Visualisierung des Call-Trees.

## 3.2.2 Nicht funktionale Anforderungen
- Performance: Die Analyse eines Standart-JAR-Archivs (z.B. 100 Klassen) soll weniger als 10 Sekunden dauern.
- Erweiterbarkeit: Das Hinzufügen neuer Analyse-Regeln ohne Umbau des Kernsystems sollte möglich sein.
- Tests: Code-Coverage von mindestens 10%
- Benutzerfreundlichkeit: Klare Fehlermeldungen bei korrupten oder inkompatiblen `.class`-Dateien

## 3.2.3 Abgrenzung
- **Kein Deobfusekator:**  Der Bytecode wird nur gelesen, nicht verändert

## 3.3.1 Vergleich Ist vs. Soll

| Merkmal            | Ist-Zustand (`javap`)      | Soll-Zustand (JByteInspector)     |
| ------------------ | -------------------------- | --------------------------------- |
| Darstellung        | Unformatierter Text-Stream | Strukturierte Tabellen / Metriken |
| Batch-Verarbeitung | Skripte nötig              | Nativ für Verzeichnisse/JARs      |
| Analyse-Tiefe      | Manuelles Suchen nötig     | Automatisierte Opcode-Statistik   |
| Abhängigkeiten     | Schwer erkennbar           | Call-Tree / Referenzliste         |

## 3.4.1 Wirtschaftlichkeit
*Argument*: Zeitersparnis. Die manuelle Analyse von 10 Klassen mit javap dauert ca. 60 Minuten. Mit JByteInspector reduziert sich dies auf 2 Minuten. Bei einem fiktiven Stundensatz von 70€ amortisiert sich das Tool bereits nach wenigen Anwendungen in größeren Projekten.

## 3.5.1 Risikoanalyse

| Risiko                                                          | Maßnahme                                                                              |
| --------------------------------------------------------------- | ------------------------------------------------------------------------------------- |
| Komplexität der ASM-Bibliothek (Einarbeitungszeit)              | Fokus auf MVP und Nutzung der umfangreichen ASM-Dokumentation                         |
| Änderung im Bytecode-Format durch neue Java-Versionen (JDK 25+) | Einsatz der aktuellsten stabilen ASM-Version und modularer Aufbau für spätere Updates |
