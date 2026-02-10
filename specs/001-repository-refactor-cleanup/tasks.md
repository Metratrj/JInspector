# Tasks: Repository Refactor and Cleanup

**Input**: Design documents from `/specs/001-repository-refactor-cleanup/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md

**Tests**: This project uses JUnit 5 for verification. All existing tests must pass after refactoring.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Move build scripts and prepare the root monorepo environment.

- [ ] T001 Move `JInspector/build.gradle` to `./build.gradle`
- [ ] T002 Move `JInspector/settings.gradle` to `./settings.gradle`
- [ ] T003 [P] Update `./settings.gradle` to reflect absolute root module paths
- [ ] T004 Move `JInspector/gradle/` and `JInspector/gradlew*` to repository root

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Remove redundant "ghost" directories and consolidate empty modules.

- [ ] T005 [P] Delete empty `jbi-common` module at repository root
- [ ] T006 [P] Delete redundant `jbyteinspector/` directory at repository root
- [ ] T007 Delete redundant `JInspector/jbyteinspector/` directory
- [ ] T008 [P] Consolidate any configuration from root `pom.xml` into root `build.gradle` if missing
- [ ] T008b Update or deprecate root pom.xml to reflect the new monorepo structure (per Gradle-primary strategy)

**Checkpoint**: Monorepo structure initialized - user story implementation can begin.

---

## Phase 3: User Story 1 - Unified Project Structure (Priority: P1) 🎯 MVP

**Goal**: Consolidate all functional code and artifacts at the repository root.

**Independent Test**: Root `./gradlew build` passes and all modules are correctly recognized.

### Implementation for User Story 1

- [ ] T009 [P] [US1] Move `JInspector/Analyzer/src/xyz/metratrj/ClassFileAnalyzer.java` to `jbi-core/src/main/java/xyz/metratrj/jbyteinspector/core/ClassFileAnalyzer.java`
- [ ] T010 [P] [US1] Move `JInspector/Analyzer/src/xyz/metratrj/system/ClassFileAnalyzerWalker.java` to `jbi-core/src/main/java/xyz/metratrj/jbyteinspector/core/ClassFileAnalyzerWalker.java`
- [ ] T011 [US1] Update all `module-info.java` files in all `jbi-*` modules to ensure correct JPMS exports/requires
- [ ] T012 [US1] Delete the `JInspector/` directory entirely after verifying no unique files remain
- [ ] T013 [US1] Verify build with `./gradlew build`

**Checkpoint**: Repository is unified at the root.

---

## Phase 4: User Story 2 - Code Cleanup & Documentation (Priority: P2)

**Goal**: Eliminate code duplicates and ensure high Javadoc coverage.

**Independent Test**: Static analysis (Qodana) shows zero duplicate code and 100% public Javadoc.

### Implementation for User Story 2

- [ ] T014 [P] [US2] Identify and extract duplicate parsing logic from `jbi-parser` and `jbi-core` into `jbi-utils`
- [ ] T015 [US2] Add Javadoc to all public classes/methods in `jbi-model`
- [ ] T016 [US2] Add Javadoc to all public classes/methods in `jbi-parser`
- [ ] T017 [US2] Add Javadoc to all public classes/methods in `jbi-core`
- [ ] T018 [US2] Add Javadoc to all public classes/methods in `jbi-cli` and `jbi-report`
- [ ] T019 [US2] Add Javadoc to all public classes/methods in `jbi-io` and `jbi-utils`

**Checkpoint**: Codebase is documented and refactored.

---

## Phase 5: User Story 3 - Linting & Standards Compliance (Priority: P3)

**Goal**: Achieve zero static analysis warnings and full standards compliance.

**Independent Test**: `./gradlew test` and static analysis both pass with no issues.

### Implementation for User Story 3

- [ ] T020 [US3] Run static analysis (Qodana/Checkstyle) and resolve all remaining warnings
- [ ] T021 [US3] Verify that all `jbi-*` modules are correctly using Java 25 features and JPMS
- [ ] T022 [US3] Final verification of JMH benchmarks in `jbi-benchmark` to ensure no performance regression

---

## Phase N: Polish & Cross-Cutting Concerns

**Purpose**: Final cleanup and documentation updates.

- [ ] T023 [P] Update root `README.md` to reflect the new project structure
- [ ] T024 [P] Remove `MAVEN_USAGE.md` if no longer relevant per Gradle primary decision
- [ ] T025 [P] Run `quickstart.md` validation from repository root
- [ ] T026 Verify SC-001: Measure repository size reduction (Target >30%)
- [ ] T027 Verify PS-001: Measure build time impact of Javadoc generation (Target <5% increase)
- [ ] T028 Verify Constitution Principle II: Ensure >90% test coverage is maintained

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: Blocks all other phases.
- **Phase 2 (Foundational)**: Depends on Phase 1; blocks all User Stories.
- **Phase 3 (US1)**: Priority P1 - Blocks US2 and US3 for safety during major file moves.
- **Phase 4 (US2)**: Priority P2.
- **Phase 5 (US3)**: Priority P3.

### Parallel Opportunities

- T003, T005, T006, T008 can be done in parallel once scripts are moved.
- Javadoc tasks (T015-T019) can be done in parallel once refactoring (T014) is stable.
- Polish tasks (T023-T025) can be done in parallel at the end.

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Setup and Foundational phases.
2. Complete US1 to have a functional, unified root-level build.
3. Validate with `./gradlew build`.

### Incremental Delivery

1. Unified Build (US1) -> Clean and documented code (US2) -> Full Standards Compliance (US3).
2. Each step increases the "cleanliness" of the repository as verified by static analysis.
