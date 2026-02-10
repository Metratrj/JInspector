# Data Model: Consolidated Module Structure

This document outlines the refactored module relationships and the placement of previously orphaned or duplicated code.

## Root Monorepo Structure

| Module | Purpose | Key Entities |
|--------|---------|--------------|
| `jbi-model` | Shared domain data | `ClassReport`, `MethodReport`, `FieldReport` |
| `jbi-parser` | Low-level .class parsing | `BytecodeParser`, `ConstantPool`, `Attributes` |
| `jbi-core` | High-level analysis logic | `AnalysisService`, `ClassFileAnalyzer` (Moved) |
| `jbi-io` | File system & Resource access | `FileWalker`, `JarScanner` |
| `jbi-utils` | Low-level helpers | `StringHelpers`, `LogConfig` |
| `jbi-cli` | End-user interface | `Main` |
| `jbi-report` | Result formatting | `ConsoleReporter` |

## Code Migration Map

| Source Path | Destination Path | Reason |
|-------------|------------------|--------|
| `JInspector/Analyzer/src/xyz/metratrj/ClassFileAnalyzer.java` | `jbi-core/src/main/java/xyz/metratrj/jbyteinspector/core/ClassFileAnalyzer.java` | Consolidation of analysis logic. |
| `JInspector/Analyzer/src/xyz/metratrj/system/ClassFileAnalyzerWalker.java` | `jbi-core/src/main/java/xyz/metratrj/jbyteinspector/core/ClassFileAnalyzerWalker.java` | Consolidation of analysis logic. |
| `JInspector/build.gradle` | `./build.gradle` | Root build configuration. |
| `JInspector/settings.gradle` | `./settings.gradle` | Root settings (paths adjusted). |

## Module Dependencies (Internal)

1. `jbi-core` → `jbi-parser`, `jbi-model`, `jbi-io`
2. `jbi-parser` → `jbi-model`, `jbi-utils`
3. `jbi-cli` → `jbi-core`, `jbi-report`
4. `jbi-report` → `jbi-model`
5. All modules → `jbi-utils` (optional)
