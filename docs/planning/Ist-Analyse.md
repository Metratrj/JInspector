# 2.1 Ist-Analyse & Aktueller Prozess
### 2.1.1 Tools
Disassembler (Standart): 
- `javap` (Teil des JDKs)
Decompiler (Source-Fokus | Diese Tools zielen darauf ab, Java-Code wiederherzustellen, analysieren aber nicht den Bytecode an sich.) : 
- CFR https://www.benf.org/other/cfr/ Last release dec 21
- Procyon https://github.com/mstrobel/procyon Last release feb 22
- JD https://java-decompiler.github.io/ Last release dec 19
- Fernflower now integrated into IntelliJ https://github.com/JetBrains/fernflower
Frameworks (Dies sind keine vollwertige Analyse-Tools sondern eine Library, die eine Entwicklung vereinfacht bzw. beschleunigt):
- ASM https://asm.ow2.io/ Library for bytecode manipulation and analysis framework
- Projekt aber dazu da den Ablauf zu lernen
## 2.1.2 Aktuelle Umgebung
- IntelliJ IDEA 2025.3
- CachyOS
- OpenJDK 25

## 2.1.3 Aktuelle Lage
In der aktuellen Lage werden Java-Anwendungen nach der Kompilierung oft als 'Black Box' betrachtet. Zur Untersuchung des generierten Bytecodes wird steht das JDK interne Tool `javap` zur Verfügung. Dies liefert zwar detaillierte Einblicke, ist jedoch für eine schnelle, automatisierte Analyse von größeren Projekten oder zu Gewinnung von statistischen Metriken ungeeignet, da die Ausgabe rein Textbasiert ist.

## 2.1.4 Aktueller Workflow
1. Manuelles Öffnen des Terminals im Zielverzeichnis.
2. Ausführen von `javap -c [Klasse]` für jede einzelne Datei.
3. Manuelle Sichtung langer Textausgaben (siehe Beispiel), um die Logik zu verstehen.
4. Händische Extraktion von Metriken (z.B. Anzahl der Methodenaufrufe)

Example Output from `javap`
```
~/sources/JInspector/Code/JInspector/out/production/JInspector
❯ javap -c Person.class 
Compiled from "Person.java"
public class Person {
  public Person(java.lang.String, java.lang.String);
    Code:
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: aload_0
         5: aload_1
         6: putfield      #7                  // Field firstname:Ljava/lang/String;
         9: aload_0
        10: aload_2
        11: putfield      #13                 // Field lastname:Ljava/lang/String;
        14: return

  public java.lang.String toString();
    Code:
         0: aload_0
         1: invokevirtual #16                 // Method hashCode:()I
         4: invokestatic  #20                 // Method java/lang/Integer.toHexString:(I)Ljava/lang/String;
         7: aload_0
         8: getfield      #7                  // Field firstname:Ljava/lang/String;
        11: aload_0
        12: getfield      #13                 // Field lastname:Ljava/lang/String;
        15: invokedynamic #26,  0             // InvokeDynamic #0:makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        20: areturn

  public boolean equals(java.lang.Object);
    Code:
         0: aload_1
         1: instanceof    #8                  // class Person
         4: ifeq          15
         7: aload_1
         8: checkcast     #8                  // class Person
        11: astore_2
        12: goto          17
        15: iconst_0
        16: ireturn
        17: aload_0
        18: invokevirtual #30                 // Method getFirstname:()Ljava/lang/String;
        21: aload_2
        22: invokevirtual #30                 // Method getFirstname:()Ljava/lang/String;
        25: invokestatic  #34                 // Method java/util/Objects.equals:(Ljava/lang/Object;Ljava/lang/Object;)Z
        28: ifeq          49
        31: aload_0
        32: invokevirtual #40                 // Method getLastname:()Ljava/lang/String;
        35: aload_2
        36: invokevirtual #40                 // Method getLastname:()Ljava/lang/String;
        39: invokestatic  #34                 // Method java/util/Objects.equals:(Ljava/lang/Object;Ljava/lang/Object;)Z
        42: ifeq          49
        45: iconst_1
        46: goto          50
        49: iconst_0
        50: ireturn

  public int hashCode();
    Code:
         0: iconst_2
         1: anewarray     #2                  // class java/lang/Object
         4: dup
         5: iconst_0
         6: aload_0
         7: invokevirtual #30                 // Method getFirstname:()Ljava/lang/String;
        10: aastore
        11: dup
        12: iconst_1
        13: aload_0
        14: invokevirtual #40                 // Method getLastname:()Ljava/lang/String;
        17: aastore
        18: invokestatic  #43                 // Method java/util/Objects.hash:([Ljava/lang/Object;)I
        21: ireturn

  public java.lang.String getFirstname();
    Code:
         0: aload_0
         1: getfield      #7                  // Field firstname:Ljava/lang/String;
         4: areturn

  public void setFirstname(java.lang.String);
    Code:
         0: aload_0
         1: aload_1
         2: putfield      #7                  // Field firstname:Ljava/lang/String;
         5: return

  public java.lang.String getLastname();
    Code:
         0: aload_0
         1: getfield      #13                 // Field lastname:Ljava/lang/String;
         4: areturn

  public void setLastname(java.lang.String);
    Code:
         0: aload_0
         1: aload_1
         2: putfield      #13                 // Field lastname:Ljava/lang/String;
         5: return
}

~/sources/JInspector/Code/JInspector/out/production/JInspector
❯ javap -c Programm.class 
Compiled from "Programm.java"
public class Programm {
  public Programm();
    Code:
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return

  public static void main(java.lang.String[]);
    Code:
         0: new           #7                  // class Person
         3: dup
         4: ldc           #9                  // String Johannes
         6: ldc           #11                 // String Grimm
         8: invokespecial #13                 // Method Person."<init>":(Ljava/lang/String;Ljava/lang/String;)V
        11: astore_1
        12: new           #7                  // class Person
        15: dup
        16: ldc           #16                 // String Anna
        18: ldc           #18                 // String Heske
        20: invokespecial #13                 // Method Person."<init>":(Ljava/lang/String;Ljava/lang/String;)V
        23: astore_2
        24: new           #7                  // class Person
        27: dup
        28: ldc           #9                  // String Johannes
        30: ldc           #11                 // String Grimm
        32: invokespecial #13                 // Method Person."<init>":(Ljava/lang/String;Ljava/lang/String;)V
        35: astore_3
        36: getstatic     #20                 // Field java/lang/System.out:Ljava/io/PrintStream;
        39: aload_1
        40: invokevirtual #26                 // Method java/io/PrintStream.println:(Ljava/lang/Object;)V
        43: getstatic     #20                 // Field java/lang/System.out:Ljava/io/PrintStream;
        46: aload_2
        47: invokevirtual #26                 // Method java/io/PrintStream.println:(Ljava/lang/Object;)V
        50: getstatic     #20                 // Field java/lang/System.out:Ljava/io/PrintStream;
        53: aload_3
        54: invokevirtual #26                 // Method java/io/PrintStream.println:(Ljava/lang/Object;)V
        57: getstatic     #20                 // Field java/lang/System.out:Ljava/io/PrintStream;
        60: aload_1
        61: aload_2
        62: invokevirtual #32                 // Method Person.equals:(Ljava/lang/Object;)Z
        65: invokevirtual #36                 // Method java/io/PrintStream.println:(Z)V
        68: getstatic     #20                 // Field java/lang/System.out:Ljava/io/PrintStream;
        71: aload_1
        72: aload_3
        73: invokevirtual #32                 // Method Person.equals:(Ljava/lang/Object;)Z
        76: invokevirtual #36                 // Method java/io/PrintStream.println:(Z)V
        79: return
}
```

## 2.2.1 Der Decompiler-Trugschluss
Moderne IDEs wie IntelliJ IDEA (via Fernflower) oder Eclipse besitzen Features zum "decompilieren", diese transformieren den Bytecode jedoch zurück in Java-Quellcode. Dabei glätten diese den Code, um ihn lesbar zu machen. Hierbei gehen Informationen über die tatsächliche Bytecode-Struktur (z.B. spezifische Optimierungen des Compilers oder exakte [[Opcode]]-Abfolgen) verloren. Durch diese abstrahierung des zugrundeliegenden [[Opcodes]] wird eine tiefe Analyse der JVM-Instruktionen verhindert.

## 2.2.2 Schwachstellen des aktuellen Prozesses
- Unübersichtlichkeit: Die textbasierte Ausgabe von `javap` ist bei großen Klassen schwer zu erfassen
- Fehlende Automatisierung: Das Scannen ganzer Verzeichnisse oder JAR-Archive ist standartmäßig nicht vorgesehen.
- Mangelde Visualisierung: Es existieren keine grafischen Aufbereitung von Klassenhierachien oder Call-Trees
- Warungsstau existierender Tools: Viele Open-Source-Projekte (z.B. JD, Procyon) werden nicht mehr aktiv gepflegt oder unterstützen aktuelle Java-Versionen (wie JDK 25) nur unvollständig.