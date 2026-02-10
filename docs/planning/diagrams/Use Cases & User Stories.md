```plantuml
@startuml  
left to right direction  
actor Developer as d  
  
skinparam usecase {  
    BackgroundColor YellowGreen  
}  
  
package JByteInspector {  
    usecase "UC1: Bytecode-Quelle laden\n(.class / .jar)" as UC1  
    usecase "UC2: Instruktions-Statistik\nanzeigen" as UC2  
    usecase "UC3: Klassenhierarchie &\nMethoden-Signaturen prüfen" as UC3  
    usecase "UC4: Call-Tree\nanzeigen" as UC4  
}  
  
d --> UC1  
d --> UC2  

d --> UC3  
d --> UC4  
  
' Relations  
UC2 ..> UC1 : <<include>>  
UC3 ..> UC1 : <<include>>  
UC4 ..> UC1 : <<include>>  
  
@enduml
```

### US1: Flexibles Laden von Bytecode-Quellen (basierend auf UC1)
**Story:**
	Als **Entwickler** möchte ich **einzelne .class-Dateien, ganze Verzeichnisse oder .jar-Archive** in das Tool laden können, damit ich nicht jede Datei händisch einzeln spezifizieren muss und flexibel in der Analyse bin.
**Akzeptanzkriterien:**
	- Das Programm akzeptiert einen Dateipfad als Kommandozeilenargument oder Input.
	- Bei Angabe eines Verzeichnisses werden alle darin enthaltenen `.class`-Dateien rekusiv gefunden.
	- Inhalte von `.jar`-Archiven werden temporär entpackt oder via `ZipInputStream`direkt gelesen.
	- Bei ungültigen Dateiformaten (z.B. keine Java-Klasse) wird eine verständliche Fehlermeldung ausgegeben, statt abzustürzen.

### US2: Übersichtliche Instruktions-Statistik (basierend auf UC2)#
**Story:**
	 Als **Entwickler** möchte ich eine **zusammengefasste Statistik der Bytecode-Instruktionen** sehen, damit ich auf einem Blick erkenne, ob eine Klasse eher rechenintensiv (Math), logiklastig (Control Flow) oder anderweitig geprägt ist.
**Akzeptanzkriterien:**
	- Alle Opcodes einer Klasse werden gezählt.
	- Die Opcodes werden in sinnvolle Kategorien gruppiert
	- Die Ausgabe erfolgt in einer tabellarischen Übersicht (z.B. absolute Anzahl und prozentualer Anteil)
	- (Optional): Die Top-3 der am häufigsten verwendeten Befehle werden hervorgehoben.

### US3: Transparenz der Klassenhierarchie (basiert auf UC3)
**Story:**
	Als **Entwickler** möchte ich die **Vererbungslinie und die importierten Interfaces** einer kompilierten Klasse einsehen, um die strukturelle Einordnung der Klasse in einem fremden Projekt schnell zu verstehen.
**Akzeptanzkriterien:**
	- Der Name der Superklasse wird korrekt extrahiert.
	- Sämtliche direkt implementierten Interfaces werden aufgelistet.
	- Modifier der Klasse (z.B. `final`, `abstract`, `public`) werden im Klartext angezeigt.
	- Die Namen und Rückgabetypen aller deklarierten Methoden werden übersichtlich aufgelistet.

### US4: Visualisierung der Methoden-Abhängigkeiten (basiert auf UC4)
**Story:**
	Als **Entwickler** möchte ich sehen, welche externen Methoden von meiner Klasse aufgerufen werden (Call-Tree), damit ich verstehe, wie stark die Klasse mit anderen Komponenten des Systems gekoppelt ist.
**Akzeptanzkriterien:**
	- Alle `INVOKEVIRTUAL`-, `INVOKESTATIC`-, `INVOKEINTERFACE`- und `INVOKESPECIAL`- Befehle werden identifiziert.
	- Das System extrahiert Ziel-Klasse und Ziel-Methode jedes Aufrufs.
	- Die Liste der Abhängigkeiten wird ohne Duplikate ausgegeben (eindeutige Call-Graph)
	- Die Ausgabe unterscheidet zwischen Aufrufen innerhalb der eigenen Klasse und Aufrufen externen Bibliotheken (z.B. `java.lang.*`)



--- 

```mermaid
graph LR
    %% Akteur
    User((Entwickler))

    subgraph JByteInspector [System: JByteInspector]
        UC1([UC1: Einzelne Klasse analysieren])
        UC2([UC2: JAR/Verzeichnis scannen])
        UC3([UC3: Statistiken anzeigen])
        UC4([UC4: Klassenhierarchie zeigen])
        UC5([UC5: Report exportieren])
        
        %% Beziehungen innerhalb des Systems
        UC2 -.->|include| UC1
        UC5 -.->|extend| UC1
    end

    %% Verbindungen vom Akteur zu den Use Cases
    User --- UC1
    User --- UC2
    User --- UC3
    User --- UC4
    User --- UC5

    %% Styling für die Optik
    style User fill:#f9f,stroke:#333,stroke-width:2px
    style JByteInspector fill:#f5f5f5,stroke:#666,stroke-dasharray: 5 5
```

