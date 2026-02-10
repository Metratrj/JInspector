# Feature Specification: Repository Refactor and Cleanup

**Feature Branch**: `001-repository-refactor-cleanup`  
**Created**: 2026-02-10  
**Status**: Draft  
**Input**: User description: "Refactor the whole repository, clean up duplicate code, add comments, write JSDoc Documentation. We Need a clean repository."

## Clarifications

### Session 2026-02-10
- Q: Final Project Structure Root → A: Use the absolute repository root (current `.git` location) and delete the `JInspector/` subfolder.
- Q: Primary Build System → A: Designate Gradle as the primary system; keep Maven files only if they don't block progress.
- Q: Refactoring Target for Shared Logic → A: Move to existing root-level jbi-utils module (jbi-common is empty and will be removed).

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Unified Project Structure (Priority: P1)

As a Maintainer, I want a single, consistent project structure without redundant directories or nested repository copies, so that the codebase is easy to navigate and build.

**Why this priority**: High. The current structure has duplicate `jbi-*` modules at the root and inside `JInspector/jbyteinspector/`, leading to confusion and build inconsistencies.

**Independent Test**: Can be fully tested by verifying that only one set of modules exists and that `./gradlew build` passes from the root.

**Acceptance Scenarios**:

1. **Given** a repository with duplicate module structures, **When** I consolidate them into a single root-level structure (deleting the redundant `JInspector/` subfolder), **Then** only root-level modules remain.
2. **Given** a consolidated structure at the repository root, **When** I run the build, **Then** all tests pass and artifacts are generated correctly.

---

### User Story 2 - Code Cleanup & Documentation (Priority: P2)

As a Developer, I want the code to be free of duplicates, well-commented, and fully documented with Javadoc, so that I can understand and maintain the logic effectively.

**Why this priority**: Medium. Improves long-term maintainability and onboarding.

**Independent Test**: Verified by Qodana/Checkstyle reports showing zero warnings and high Javadoc coverage.

**Acceptance Scenarios**:

1. **Given** existing Java classes, **When** I apply Javadoc to all public methods and classes, **Then** documentation can be generated without errors.
2. **Given** duplicated logic across modules, **When** I refactor into shared utilities (specifically `jbi-utils`), **Then** functionality remains identical as verified by tests.

---

### User Story 3 - Linting & Standards Compliance (Priority: P3)

As a Quality Engineer, I want the repository to pass all static analysis checks, so that we maintain a high standard of code quality.

**Why this priority**: Medium. Ensures that the cleanup isn't just cosmetic but adheres to project standards.

**Independent Test**: Successful run of `qodana` with zero detected issues.

**Acceptance Scenarios**:

1. **Given** the refactored codebase, **When** I run static analysis, **Then** no "duplicate code" or "missing documentation" warnings are present.

## Edge Cases

- **Circular Dependencies**: Refactoring duplicates into shared modules might introduce circular dependencies between modules.
- **Build Script Synchronization**: Gradle and Maven build files must be updated simultaneously to avoid breaking one of the build systems (noting Gradle is primary).
- **Hidden Git Submodules**: Check if any nested directories are actually untracked git submodules or independent repos.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST have a single, flat module structure at the absolute repository root.
- **FR-002**: System MUST remove the redundant `JInspector/` directory and its contents entirely.
- **FR-003**: All public classes and methods MUST have Javadoc documentation.
- **FR-004**: Duplicate code identified by static analysis MUST be refactored into the shared jbi-utils module.
- **FR-005**: All `module-info.java` files MUST be updated to reflect the new structure and maintain JPMS compliance.
- **FR-006**: Gradle MUST be maintained as the primary build system; Maven POM files are secondary and should only be updated if it does not significantly block progress.

### Key Entities

- **Gradle Build Files**: `build.gradle`, `settings.gradle` - define the project structure (Primary).
- **Maven POM Files**: `pom.xml` - provide alternative build support (Secondary).
- **Javadoc**: The primary source for code documentation.
- **Shared Modules**: `jbi-utils` - target for consolidated logic.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Repository size (excluding `.git` and `build/`) is reduced by at least 30% through removal of duplicates.
- **SC-002**: Static analysis (Qodana) reports 0 "Duplicate Code" fragments.
- **SC-003**: Javadoc coverage for public APIs reaches 100%.

### Performance Standards

- **PS-001**: Build time (`./gradlew assemble`) should not increase by more than 5% due to new documentation generation.
- **PS-002**: The project must remain compatible with Java 25.
