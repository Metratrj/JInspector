# Research: Visitor-based Parser Architecture

## Decision: Core Parser Design
- **Choice**: Hand-written binary parser following the Java ClassFile format (JVM Spec Ch. 4).
- **Rationale**: Strict adherence to the Constitution's "Zero-Dependency Core" principle. Manual parsing ensures full control over lazy loading and label identification.
- **Alternatives Considered**: 
  - ASM: Rejected per Constitution.
  - Java Class-File API (JEP 457): Considered, but project requires native implementation for specialized "DataRecord" memory management.

## Decision: Visitor Pattern Implementation
- **Choice**: ASM-like API with `ClassVisitor`, `FieldVisitor`, and `MethodVisitor`.
- **Rationale**: It is the industry standard for Java bytecode manipulation. Developers familiar with ASM will immediately understand the API.
- **Structure**:
  - `ClassReader`: Stateless parser (accepts `DataRecord`).
  - `ClassVisitor`: Interface with methods like `visit(int version, int access, String name, ...)`, `visitField(...)`, `visitMethod(...)`.

## Decision: Parallel Loading Strategy
- **Choice**: Use `java.nio.file.Files.walkFileTree` combined with `java.util.concurrent.ForkJoinPool` or `java.util.concurrent.ExecutorService` to load files into `DataRecord` objects.
- **Rationale**: Efficiently utilizes multiple cores for I/O and decompression (for JARs).
- **JAR Support**: Use `java.util.zip.ZipInputStream` or `java.nio.file.FileSystems.newFileSystem(path)` for parallel entry processing.

## Decision: Lazy Parsing of Method Attributes
- **Choice**: `ClassReader` stores the offset and size of the `Code` attribute during the initial pass. The actual instruction parsing happens only when `MethodVisitor.visitCode()` or a similar trigger is called.
- **Rationale**: Minimizes initial memory footprint and speeds up structure-only analysis.

## Decision: Label Pre-identification
- **Choice**: Perform a quick scan of the `Code` attribute bytecode to identify jump targets (offsets) and exception handler start/end/handler positions before triggering `MethodVisitor` events.
- **Rationale**: Required for the Visitor API to trigger `visitLabel(Label l)` at the correct sequential position during instruction parsing.

## Best Practices for Java 25
- **Records**: Use `DataRecord` as a Java Record for immutability.
- **Sealed Classes**: Consider making `Attribute` a sealed hierarchy if internal modeling is needed before visiting.
- **Pattern Matching**: Use pattern matching for switch/instanceof when dispatching attribute types.
