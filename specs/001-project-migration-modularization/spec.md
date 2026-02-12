# Feature Specification: Project Migration and Modularization to JBI-Reborn

**Feature Branch**: `001-project-migration-modularization`  
**Created**: 2026-02-11  
**Status**: Draft  
**Input**: User description: "Migrate the whole project to the JBI-Reborn directory. See that there are only 3 Modules cli,gui,core manage source file with packages. While migrating, write JSDoc comments."

## Clarifications

### Session 2026-02-11
- Q: Should all non-UI logic be consolidated into the single core module? → A: Consolidate all non-UI logic into core.
- Q: Should standard Javadoc syntax be used instead of JSDoc? → A: Use standard Javadoc syntax.
- Q: Should CLI output prioritize exact legacy strings or new structure accuracy? → A: Accuracy: output reflects new package structure; update test baselines.
- Q: Should JBI-Reborn use a flat root structure or nested directories? → A: Flat root structure (/cli, /core, /gui).

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Core Library Migration (Priority: P1)

As a developer, I want to have a dedicated `core` module that contains all the bytecode parsing and analysis logic, so that it can be reused by different interfaces.

**Why this priority**: The core logic is the foundation of the entire application. Without it, neither the CLI nor the GUI can function.

**Independent Test**: The `core` module can be built and unit-tested independently of the `cli` and `gui` modules.

**Acceptance Scenarios**:

1. **Given** the source project files, **When** migrated to the `core` module, **Then** all bytecode parsing tests pass.
2. **Given** the new `core` module, **When** checked for documentation, **Then** all public classes and methods have Javadoc comments.

---

### User Story 2 - CLI Module Implementation (Priority: P2)

As a power user, I want to use a command-line interface to analyze Java bytecode quickly without needing a graphical environment.

**Why this priority**: Provides a lightweight and automatable way to use the analysis engine.

**Independent Test**: Running the `cli` module with a sample `.class` file produces a structured report on the console.

**Acceptance Scenarios**:

1. **Given** a compiled `.class` file, **When** processed by the `cli` module, **Then** the output matches the original project's output format.

---

### User Story 3 - GUI Module Implementation (Priority: P3)

As a visual user, I want a graphical interface to explore class hierarchies and bytecode structures interactively.

**Why this priority**: Enhances usability for complex analysis tasks.

**Independent Test**: The `gui` module launches successfully and allows a user to select and analyze a file via a file picker.

**Acceptance Scenarios**:

1. **Given** the `gui` module, **When** launched, **Then** the main window appears and analysis results are displayed in the UI components.

---

### Edge Cases

- What happens when a source file from the original project doesn't fit into the `cli`, `gui`, or `core` modules?
- How does the system handle missing or incomplete Javadoc comments during the migration process?
- What happens if there are circular dependencies between the new modules?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The project MUST be successfully moved to the `JBI-Reborn` directory.
- **FR-002**: The destination project MUST be structured into exactly three modules (`cli`, `gui`, and `core`) using a flat root directory layout.
- **FR-003**: All source files MUST be organized into a consistent Java package structure starting with `xyz.metratrj.jbi.*`.
- **FR-004**: Every public class and method MUST have standard Javadoc comments (`/** ... */`) explaining its purpose and parameters.
- **FR-005**: The `core` module MUST encapsulate all non-UI logic, including bytecode parsing, constant pool handling, analysis engine logic, and shared utilities.
- **FR-006**: The `cli` module MUST provide a standalone executable or jar that accepts file paths as arguments.
- **FR-007**: The `gui` module MUST implement a JavaFX-based (or similar) interface as per existing mockups.
- **FR-008**: The migration MUST ensure that no original functionality is lost or degraded. This will be a "clean slate" migration without preserving legacy git history.

### Key Entities *(include if feature involves data)*

- **Bytecode Parser**: The component responsible for reading and validating `.class` file structures.
- **Analysis Engine**: The logic that transforms raw bytecode into high-level reports.
- **ClassReport/MethodReport**: Immutable data structures (Records) used to transfer results between modules.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: The project builds successfully using `./gradlew build` in the `JBI-Reborn` directory.
- **SC-002**: 100% of public API members in the new modules have valid Javadoc comments.
- **SC-003**: The `cli` module analysis output reflects the new package structure while maintaining logical identity with the original project.
- **SC-004**: The `gui` module launches in under 2 seconds on a standard developer machine.
- **SC-005**: All code is contained within the three specified modules with zero "orphan" source files.
