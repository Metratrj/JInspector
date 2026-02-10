# Feature Specification: Visitor-based Parser Architecture

**Feature Branch**: `002-visitor-parser-architecture`  
**Created**: 2026-02-10  
**Status**: Draft  
**Input**: User description: "Überprüfe die Architektur und passe bei abweichung an.. Architektur-Vorgaben: Pattern: Implementiere konsequent das Visitor-Pattern. Trenne zwischen ClassReader (Parsing-Logik) und einem ClassVisitor-Interface (API). Memory-Management: Der Prozess ist zweigeteilt. 1. Ein paralleler File-Tree-Walk liest alle Dateien in DataRecord(String path, byte[] data) Objekte ein. 2. Die Analyse erfolgt rein im Speicher ohne offene File-Handles. Parsing-Logik & Visitor-Aufrufe: Implementiere den ClassReader so, dass er für ein DataRecord folgende Sequenz durchläuft: Header-Parsing (Class, Fields, Methods). Fields: Parse Header & Attribute -> visitField() -> visitAttribute(). Methods: Lazy Parsing von komplexen Attributen. Einfache Attribute direkt speichern, komplexe Attribute erst beim visitMethod() Aufruf parsen und inkrementell besuchen. Code-Special-Case: Vor dem Instruktions-Parsing müssen Labels (Code, Exception-Handler, LineNumbers, LocalVariables) vorab identifiziert werden. Instruction Loop: Während des Parsens der Byte-Instruktionen müssen an den korrekten Offsets visitFrame (via StackMapTable), visitLabel und visitLineNumber getriggert werden. Generiere die Interface-Definitionen für ClassVisitor, MethodVisitor und FieldVisitor."

## Clarifications

### Session 2026-02-10
- Q: Should the parallel file loader support only file system directories, or also JAR/ZIP archives? → A: Support both file system directories and JAR/ZIP archives.
- Q: How should the Visitor API handle the variety of class attributes? → A: Visit every attribute individually for fine-grained control.
- Q: How should the parser handle malformed class files? → A: Throw a specific exception and abort parsing for that class.
- Q: Should a single ClassReader instance support parallel `accept` calls? → A: No, `accept` is strictly single-threaded per `ClassReader`.
- Q: What data types should the Visitor API use for attribute values and constants? → A: Standard Java class library types (String, Number, etc.).

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Inspect Class Structure (Priority: P1)

As a developer, I want to inspect the structure of a Java class file (fields, methods, attributes) using a standardized API so that I can easily build analysis tools without knowing the low-level parsing details.

**Why this priority**: This is the core functionality of the bytecode inspector.

**Independent Test**: Can be tested by implementing a simple `ClassVisitor` that prints field and method names and verifying it works for a sample class.

**Acceptance Scenarios**:

1. **Given** a valid Java `.class` file as a `DataRecord`, **When** passed to `ClassReader.accept(ClassVisitor)`, **Then** all `visitField` and `visitMethod` calls are triggered in the correct order.
2. **Given** a field with attributes, **When** parsed, **Then** `visitField` is called followed by `visitAttribute`.

---

### User Story 2 - High-Performance Memory-Only Analysis (Priority: P1)

As a system architect, I want the analysis to happen entirely in memory after a parallel loading phase so that the system is not bottlenecked by file I/O during complex analysis tasks and doesn't exhaust file descriptors.

**Why this priority**: Performance and scalability for large codebases are critical requirements.

**Independent Test**: Verify that multiple classes can be loaded in parallel into `DataRecord` objects and subsequently analyzed without any active file handles to the original files.

**Acceptance Scenarios**:

1. **Given** a directory or JAR/ZIP archive of class files, **When** loaded via the parallel file-tree-walk, **Then** all files are converted to `DataRecord` objects.
2. **Given** a set of `DataRecord` objects, **When** analyzed, **Then** no file I/O occurs during the parsing phase.

---

### User Story 3 - Method Body Inspection (Priority: P2)

As a developer, I want to inspect method instructions and flow control labels so that I can perform deeper analysis like call graph generation or control flow analysis.

**Why this priority**: Advanced analysis requires access to bytecode instructions and labels.

**Independent Test**: Implement a `MethodVisitor` that tracks labels and instructions and verify it correctly identifies jumps and line numbers.

**Acceptance Scenarios**:

1. **Given** a method with bytecode, **When** `visitMethod` is called, **Then** labels are pre-identified before instruction parsing.
2. **Given** an instruction loop, **When** parsing bytecode, **Then** `visitFrame`, `visitLabel`, and `visitLineNumber` are triggered at the exact correct offsets.

---

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST implement a `ClassReader` that encapsulates all logic for parsing the Java ClassFile format.
- **FR-002**: System MUST provide a `ClassVisitor` interface that serves as the primary API for observing class structures.
- **FR-003**: System MUST provide `MethodVisitor` and `FieldVisitor` interfaces for detailed inspection of methods and fields respectively.
- **FR-004**: System MUST implement a two-stage memory management process: (1) Parallel loading from file system directories or JAR/ZIP archives into `DataRecord` objects, (2) In-memory analysis from `DataRecord` using single-threaded `ClassReader` instances.
- **FR-005**: `ClassReader` MUST follow a strict parsing sequence: Class Header -> Fields (Header & Attributes) -> Methods.
- **FR-006**: `ClassReader` MUST implement lazy parsing for complex method attributes, deferring their parsing until the specific `visitMethod` call.
- **FR-007**: System MUST identify all Labels (Code, Exception-Handler, LineNumbers, LocalVariables) in a method's code attribute *before* starting the instruction parsing loop.
- **FR-008**: The instruction parsing loop MUST trigger `visitFrame`, `visitLabel`, and `visitLineNumber` at their respective bytecode offsets.
- **FR-009**: The Visitor API MUST support visiting every attribute individually to provide fine-grained control over metadata inspection.

### Key Entities *(include if feature involves data)*

- **DataRecord**: A simple data container holding the file path and the raw byte content (`byte[] data`).
- **ClassReader**: The parser component that reads `DataRecord` and drives a `ClassVisitor`.
- **ClassVisitor / MethodVisitor / FieldVisitor**: The observer interfaces that receive events during the parsing process.
- **Label**: Represents a position in the bytecode (used for jumps, exception handlers, etc.).
- **Attribute Value**: Represented using standard Java types (e.g., `String` for UTF-8 constants, `Integer`/`Long`/`Float`/`Double` for numeric constants).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of standard class file elements (Header, Fields, Methods, Attributes) are correctly visited via the Visitor API.
- **SC-002**: Zero file handles are open during the second phase (Analysis phase) of the process.

### Performance Standards

- **PS-001**: Memory overhead for `DataRecord` storage should not exceed 1.2x the raw size of the class files.
- **PS-002**: Instruction parsing with label pre-identification must maintain performance parity with single-pass parsing (within 5% margin).

## Assumptions

- **AS-001**: The Visitor pattern will follow the general structure of the ASM library, as it is the industry standard for Java bytecode manipulation.
- **AS-002**: "Complex attributes" in methods primarily refers to the `Code` attribute and its sub-attributes like `StackMapTable`.
- **AS-003**: Error handling for malformed class files will involve throwing specific `BytecodeException` types during the `accept` call and aborting the parsing for that specific class to ensure data integrity.