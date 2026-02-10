# Research: Repository Structural Audit

## Findings

### 1. Ghost Directories and Duplicates
The repository contains several redundant directories that appear to be incomplete copies or build output shells:
- `jbyteinspector/`: Contains empty `bin` structures for modules. No source code.
- `JInspector/jbyteinspector/`: Contains only a `build/` directory.
- `JInspector/Analyzer/`: Contains a separate `src` tree. Needs investigation.

### 2. Source of Truth
The modules located at the absolute repository root (`jbi-model`, `jbi-parser`, etc.) contain the actual `src` directories and build files (`build.gradle`, `pom.xml`). These are the definitive versions of the code.

### 3. Duplicate Modules
- `jbi-common` vs `jbi-utils`: Both exist at the root. `jbi-utils` has source, `jbi-common` has only `src/main/java` and `src/test/java` but no classes.
- `jbi-io`, `jbi-parser`, `jbi-core`: These are well-defined root-level modules.

## Decisions

- **Decision**: Consolidate all functional code into the root-level `jbi-*` modules.
- **Rationale**: Root-level modules are the only ones with source code and active build configurations.
- **Decision**: Remove `jbyteinspector/`, `JInspector/`, and empty modules like `jbi-common`.
- **Rationale**: These are redundant and cause confusion. `jbi-utils` will be the primary target for shared logic.
- **Decision**: Unified Gradle Root.
- **Rationale**: Move `JInspector/build.gradle` and `JInspector/settings.gradle` to the root and adjust paths.

## Structural Audit Table

| Module | Location | Status | Action |
|--------|----------|--------|--------|
| jbi-model | root | OK | Keep |
| jbi-parser | root | OK | Keep |
| jbi-core | root | OK | Keep |
| jbi-utils | root | OK | Keep |
| jbi-common | root | EMPTY | Remove (merge to utils) |
| jbi-* | jbyteinspector/ | EMPTY SHELL | Remove |
| jbi-* | JInspector/jbyteinspector/ | EMPTY SHELL | Remove |
