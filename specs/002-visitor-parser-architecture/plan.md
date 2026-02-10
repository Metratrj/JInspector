# Implementation Plan: Visitor-based Parser Architecture

**Branch**: `002-visitor-parser-architecture` | **Date**: 2026-02-10 | **Spec**: [specs/002-visitor-parser-architecture/spec.md](spec.md)
**Input**: Feature specification from `/specs/002-visitor-parser-architecture/spec.md`

## Summary
Implement a high-performance, zero-dependency bytecode parser following the Visitor pattern. The system utilizes a two-stage memory management process: parallel loading of class files/archives into `DataRecord` objects, followed by in-memory analysis using a single-threaded `ClassReader` and a suite of visitor interfaces (`ClassVisitor`, `FieldVisitor`, `MethodVisitor`).

## Technical Context

**Language/Version**: Java 25  
**Primary Dependencies**: None (Standard Library only)  
**Storage**: In-memory (`DataRecord`)  
**Testing**: JUnit 5, JMH (Performance Benchmarking)  
**Target Platform**: JVM (Cross-platform)
**Project Type**: Multi-module Java project  
**Performance Goals**: < 10s per 100 classes (Constitution target), > 10MB/s parsing throughput.  
**Constraints**: Zero third-party bytecode libraries, < 1.2x memory overhead for raw data.  
**Scale/Scope**: Support full Java 25 ClassFile format, JAR/ZIP archives.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

1. **Java 25 & JPMS**: Yes, using Records for `DataRecord` and `Label`. Modules `jbi-parser`, `jbi-model`, `jbi-io` will be involved.
2. **Testing**: Yes, unit tests for all visitor events and integration tests for multi-source loading.
3. **CLI UX**: Output focuses on high-level insights via visitors.
4. **Performance**: JMH benchmarks required for instruction parsing and parallel loading.
5. **Dependencies**: ZERO third-party bytecode libraries used.

## Project Structure

### Documentation (this feature)

```text
specs/002-visitor-parser-architecture/
├── plan.md              # This file
├── research.md          # Research findings
├── data-model.md        # Entity definitions
├── quickstart.md        # API usage example
├── contracts/           # Interface definitions
└── tasks.md             # Tasks (to be generated)
```

### Source Code (repository root)

```text
jbi-model/src/main/java/xyz/jinspector/model/
├── DataRecord.java
├── Label.java
├── ClassVisitor.java
├── FieldVisitor.java
└── MethodVisitor.java

jbi-parser/src/main/java/xyz/jinspector/parser/
├── ClassReader.java
├── ConstantPool.java
└── AttributeParser.java

jbi-io/src/main/java/xyz/jinspector/io/
└── ParallelLoader.java

jbi-tests/src/test/java/xyz/jinspector/parser/
├── ClassReaderTest.java
└── ParallelLoaderTest.java

jbi-benchmark/src/jmh/java/xyz/jinspector/benchmark/
└── ParserBenchmark.java
```

**Structure Decision**: Multi-module approach to separate model definitions, parsing logic, and I/O utilities, adhering to JPMS.

## Complexity Tracking

*No violations detected.*