# Tasks: Visitor-based Parser Architecture

**Input**: Design documents from `/specs/002-visitor-parser-architecture/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Tests are included as high unit test coverage (>90%) is mandated by the project Constitution for parsing logic.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [x] T001 Create project structure for modules jbi-model, jbi-parser, jbi-io, and jbi-tests per implementation plan
- [x] T002 Update module-info.java for each module to ensure proper JPMS encapsulation and exports
- [x] T003 [P] Configure JMH benchmarking in jbi-benchmark/build.gradle for performance tracking

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure and data containers that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T004 Implement `DataRecord` as a Java Record in jbi-model/src/main/java/xyz/metratrj/jbyteinspector/model/DataRecord.java
- [x] T005 Implement `Label` class with offset tracking in jbi-model/src/main/java/xyz/metratrj/jbyteinspector/model/Label.java
- [x] T006 [P] Create `ClassVisitor` interface in jbi-model/src/main/java/xyz/metratrj/jbyteinspector/model/ClassVisitor.java
- [x] T007 [P] Create `FieldVisitor` interface in jbi-model/src/main/java/xyz/metratrj/jbyteinspector/model/FieldVisitor.java
- [x] T008 [P] Create `MethodVisitor` interface in jbi-model/src/main/java/xyz/metratrj/jbyteinspector/model/MethodVisitor.java
- [x] T009 Implement base `BytecodeException` in jbi-model/src/main/java/xyz/metratrj/jbyteinspector/model/BytecodeException.java

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 2 - High-Performance Memory-Only Analysis (Priority: P1) 🎯 MVP

**Goal**: Parallel loading of classes/archives into memory-resident DataRecords without persistent file handles.

**Independent Test**: Load a directory and a JAR file simultaneously and verify that all entries are present in a list of DataRecords and no file handles remain open.

### Implementation for User Story 2

- [x] T010 [P] [US2] Implement `ParallelLoader` for file system walking in jbi-io/src/main/java/xyz/metratrj/jbyteinspector/io/ParallelLoader.java
- [x] T011 [US2] Add JAR/ZIP archive support to `ParallelLoader` in jbi-io/src/main/java/xyz/metratrj/jbyteinspector/io/ParallelLoader.java
- [x] T012 [US2] Implement unit tests for multi-source loading in jbi-tests/src/test/java/xyz/metratrj/jbyteinspector/io/ParallelLoaderTest.java
- [ ] T013 [US2] Add JMH benchmark for loading throughput in jbi-benchmark/src/jmh/java/xyz/metratrj/jbyteinspector/benchmark/LoaderBenchmark.java

**Checkpoint**: User Story 2 is functional - files can be loaded into memory in parallel.

---

## Phase 4: User Story 1 - Inspect Class Structure (Priority: P1)

**Goal**: Implement `ClassReader` to parse class headers, fields, and method metadata and drive the Visitor API.

**Independent Test**: Use a mock `ClassVisitor` to verify that `visit`, `visitField`, and `visitMethod` are called with correct arguments for a known class file.

### Implementation for User Story 1

- [x] T014 [US1] Implement `ConstantPool` parser in jbi-parser/src/main/java/xyz/metratrj/jbyteinspector/parser/ConstantPool.java
- [x] T015 [US1] Implement class header parsing logic in `ClassReader.java` in jbi-parser/src/main/java/xyz/metratrj/jbyteinspector/parser/ClassReader.java
- [x] T016 [US1] Implement field parsing with attribute visiting in `ClassReader.java`
- [x] T017 [US1] Implement method metadata parsing (name, descriptor, etc.) in `ClassReader.java`
- [x] T018 [US1] Implement individual attribute visiting for class-level attributes in jbi-parser/src/main/java/xyz/metratrj/jbyteinspector/parser/ClassReader.java
- [x] T019 [US1] Implement unit tests for class structure inspection in jbi-tests/src/test/java/xyz/metratrj/jbyteinspector/parser/ClassReaderTest.java

**Checkpoint**: User Story 1 is functional - class structure can be inspected via visitors.

---

## Phase 5: User Story 3 - Method Body Inspection (Priority: P2)

**Goal**: Implement lazy parsing of the Code attribute, label pre-identification, and the instruction parsing loop.

**Independent Test**: Verify that `visitInsn` is called in sequence and `visitLabel` is triggered at the correct bytecode offsets.

### Implementation for User Story 3

- [x] T020 [US3] Implement lazy parsing of the `Code` attribute in `ClassReader.java` (deferring until `visitMethod`)
- [x] T021 [US3] Implement label pre-identification pass in `ClassReader.java` to find jump targets and handlers
- [x] T022 [US3] Implement the instruction parsing loop triggering `visitInsn`, `visitVarInsn`, etc., in `ClassReader.java`
- [x] T023 [US3] Implement triggering of `visitFrame`, `visitLabel`, and `visitLineNumber` at correct offsets in `ClassReader.java`
- [x] T024 [US3] Implement unit tests for method body and label inspection in jbi-tests/src/test/java/xyz/metratrj/jbyteinspector/parser/InstructionParsingTest.java
- [ ] T025 [US3] Add JMH benchmark for instruction parsing performance in jbi-benchmark/src/jmh/java/xyz/metratrj/jbyteinspector/benchmark/ParserBenchmark.java

**Checkpoint**: All user stories are functional.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T026 [P] Add Javadoc to all public APIs in `jbi-model` and `jbi-parser` (Constitution requirement)
- [ ] T027 Run `qodana` scan and resolve all warnings (Constitution requirement)
- [ ] T028 [P] Update README.md with usage examples from quickstart.md
- [ ] T029 Perform final memory overhead validation (SC-002, PS-001)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies.
- **Foundational (Phase 2)**: Depends on Setup (Phase 1).
- **User Story 2 (P1 - MVP)**: Depends on Foundational (Phase 2). *Chosen as MVP because it provides the data source for all subsequent analysis.*
- **User Story 1 (P1)**: Depends on Foundational (Phase 2).
- **User Story 3 (P2)**: Depends on User Story 1 (requires method metadata to trigger body visit).
- **Polish (Final Phase)**: Depends on all user stories.

### Parallel Opportunities

- Foundation interfaces (T006-T008) can be created in parallel.
- User Story 2 (Loading) and User Story 1 (Structure Parsing) can be developed in parallel once foundations are in place, although Story 1 needs DataRecords to be useful.
- Javadoc (T026) and README updates (T028) can run in parallel.

---

## Implementation Strategy

### MVP First (User Story 2)

1. Complete Setup and Foundations.
2. Complete Parallel Loader (US2) to prove the memory management strategy.
3. Validate loading of 100+ classes from a JAR under 10s (Constitution goal).

### Incremental Delivery

1. Add `ClassReader` structure parsing (US1) -> Now we can see "what" is in the JAR.
2. Add Method Body parsing (US3) -> Now we can see "how" it works.
3. Apply final Polish and Javadoc.

---

## Notes

- Every task follows the required checklist format.
- Paths are aligned with the multi-module Maven/Gradle structure specified in `plan.md`.
- Performance targets from the Constitution (benchmarks) are integrated into the tasks.
