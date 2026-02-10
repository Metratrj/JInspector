# Implementation Plan: Repository Refactor and Cleanup

**Branch**: `001-repository-refactor-cleanup` | **Date**: 2026-02-10 | **Spec**: [/specs/001-repository-refactor-cleanup/spec.md]
**Input**: Feature specification from `/specs/001-repository-refactor-cleanup/spec.md`

## Summary

Consolidate the fragmented repository structure into a clean, root-level monorepo. This involves moving all `jbi-*` modules from the `JInspector/jbyteinspector/` subdirectory to the repository root, removing the redundant `JInspector/` and `jbyteinspector/` wrappers, and unifying the Gradle build system. Code quality will be improved by refactoring duplicates into `jbi-utils` and ensuring 100% Javadoc coverage for public APIs.

## Technical Context

**Language/Version**: Java 25  
**Primary Dependencies**: None (Standard Library only for core parser)  
**Storage**: N/A (File system based analysis)  
**Testing**: JUnit 5, JaCoCo  
**Target Platform**: JVM (Java 25+)  
**Project Type**: Multi-module Gradle Monorepo  
**Performance Goals**: Build time < 30s, Parsing throughput > 10MB/s  
**Constraints**: Zero-dependency core, JPMS (Java Platform Module System) compliance  
**Scale/Scope**: ~10 modules, ~100 classes

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

1. **Java 25 & JPMS**: YES. Project strictly targets Java 25 and uses modules.
2. **Testing**: YES. JUnit 5 is used project-wide. Refactor will preserve all tests.
3. **CLI UX**: YES. CLI module `jbi-cli` will be moved but its interface remains identical.
4. **Performance**: YES. JMH benchmarks will be used to verify no regressions after refactoring.
5. **Dependencies**: YES. Zero-dependency core principle is maintained.

## Project Structure

### Documentation (this feature)

```text
specs/001-repository-refactor-cleanup/
├── spec.md              # Feature specification
├── plan.md              # This file
├── research.md          # Structural audit and duplicate analysis
├── data-model.md        # Refactored module relationships
└── tasks.md             # Implementation tasks
```

### Source Code (repository root)

```text
/ (Repository Root)
├── build.gradle         # Unified root build script
├── settings.gradle      # Root module definitions
├── jbi-model/           # Domain models
├── jbi-parser/          # Bytecode parser (Core)
├── jbi-core/            # Analysis logic
├── jbi-io/              # File I/O
├── jbi-utils/           # Shared utilities (Refactor target)
├── jbi-cli/             # Command-line interface
├── jbi-report/          # Reporting logic
├── jbi-benchmark/       # JMH Benchmarks
├── jbi-examples/        # Example code
└── jbi-tests/           # Integration tests
```

**Structure Decision**: Consolidate all modules to the absolute repository root. Remove `JInspector/` and its nested `jbyteinspector/` folders.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | N/A | N/A |