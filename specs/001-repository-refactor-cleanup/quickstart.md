# Quickstart: Clean JByteInspector Repository

## Setup and Build

This repository has been consolidated into a single root-level Gradle monorepo.

### Prerequisites
- JDK 25

### Build the Project
Run from the repository root:
```bash
./gradlew build
```

### Run the CLI
```bash
./gradlew :jbi-cli:run --args="path/to/class/or/jar"
```

### Run Tests
```bash
./gradlew test
```

## Structural Overview
- All modules are now located at the root (e.g., `/jbi-core`, `/jbi-parser`).
- Redundant directories (`/JInspector`, `/jbyteinspector`) have been removed.
- Use `jbi-utils` for any new shared helper classes.

## Development Standards
- **Javadoc**: All public APIs must have Javadoc.
- **Testing**: Unit tests are required for all new logic.
- **Static Analysis**: Run `./gradlew qodanaScan` (if configured) or check CI logs for linting results.
