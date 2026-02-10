<!--
Sync Impact Report
- Version change: 1.0.0 -> 1.1.0
- List of modified principles:
  - III. Unified CLI Experience (UX): Added mandate for "Aggregated Insight" (FA3/FA6) over raw bytecode streams.
  - IV. Performance Benchmarking (JMH): Integrated concrete target from Soll-Konzept (<10s per 100 classes).
- Added sections:
  - Updated Quality Gates to include multi-source import validation (FA1).
- Templates requiring updates:
  - .specify/templates/plan-template.md (✅ updated)
  - .specify/templates/spec-template.md (✅ updated)
  - .specify/templates/tasks-template.md (✅ updated)
- Follow-up TODOs:
  - Update Soll-Konzept.md (3.5.1 Risikoanalyse) to remove ASM references and 10% coverage target to match constitution.
-->

# JByteInspector Constitution

## Core Principles

### I. Java 25 Modernity & Modularity
JByteInspector MUST leverage the latest Java 25 features (Records, Sealed Classes, Pattern Matching) and adhere to the Java Platform Module System (JPMS). The architecture MUST maintain strict separation between parsing, domain model, core logic, and presentation (CLI).

**Rationale**: To ensure the project remains maintainable, type-safe, and takes full advantage of modern JVM performance and security enhancements.

### II. Comprehensive Testing Discipline
Every module MUST have a corresponding JUnit 5 test suite. Low-level parsing and model transformations require high unit test coverage (target >90%). Cross-module workflows and CLI interactions MUST be validated via integration tests.

**Rationale**: Accuracy is non-negotiable for a bytecode analyzer; tests are the primary defense against regressions in complex binary parsing. This standard supersedes lower legacy targets.

### III. Unified CLI Experience (UX)
The CLI MUST provide a consistent user experience. Arguments, flags, and output formats (human-readable vs. machine-parsable) must be predictable. Output MUST prioritize **aggregated insights** (e.g., opcode statistics, complexity metrics) over raw bytecode streams to provide immediate value over tools like `javap`.

**Rationale**: A powerful tool is only useful if it is intuitive for developers and provides higher-level analysis than standard JDK tools.

### IV. Performance Benchmarking (JMH)
Performance is a core feature. Critical code paths MUST be benchmarked using JMH.
- **Target**: Analysis of a standard JAR (approx. 100 classes) MUST complete in under 10 seconds.
- Any significant change to parsing paths MUST be accompanied by benchmark results proving no performance regression.

**Rationale**: Static analysis tools are often used in CI/CD pipelines where speed directly impacts developer productivity.

### V. Zero-Dependency Core
The core bytecode parser MUST NOT depend on third-party libraries like ASM, BCEL, or Javassist. All parsing logic must be implemented natively within the `jbi-parser` module.

**Rationale**: This ensures maximum control over the parsing process, avoids library version conflicts, and keeps the core footprint minimal. This principle intentionally supersedes earlier planning documents mentioning ASM.

## Performance Standards
- **Parsing Throughput**: Target >10MB of class data per second on a modern quad-core machine.
- **Memory Footprint**: Maintain low heap allocation during analysis; prefer streaming or efficient data structures over large in-memory graphs.

## Quality Gates
- **Multi-Source Validation**: All features MUST support individual `.class` files, directories, and `.jar` archives consistently (FA1).
- **Static Analysis**: All code must pass `qodana` or `checkstyle` with zero warnings.
- **Documentation**: All public APIs in `jbi-model` and `jbi-core` MUST have Javadoc.

## Governance
This constitution supersedes all other development practices and planning documents (e.g., Soll-Konzept). Amendments require a proposal, documentation of the rationale, and an update to this file with a version bump.

**Version**: 1.1.0 | **Ratified**: 2026-01-26 | **Last Amended**: 2026-02-10
