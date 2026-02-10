# Quickstart: Visitor-based Parser Architecture

## Basic Usage

Implementing a simple visitor to print method names:

```java
import xyz.jinspector.model.ClassVisitor;
import xyz.jinspector.model.MethodVisitor;
import xyz.jinspector.parser.ClassReader;
import xyz.jinspector.model.DataRecord;

public class MethodPrinter extends ClassVisitor {
    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        System.out.println("Found method: " + name);
        return null; // Return null if you don't need to visit the method body
    }
}

// Usage
DataRecord record = ...; // Loaded via parallel loader
ClassReader reader = new ClassReader(record);
reader.accept(new MethodPrinter());
```

## Parallel Loading

```java
import xyz.jinspector.io.ParallelLoader;
import xyz.jinspector.model.DataRecord;
import java.nio.file.Paths;
import java.util.List;

List<DataRecord> records = ParallelLoader.load(Paths.get("path/to/classes_or_jar"));
records.parallelStream().forEach(record -> {
    new ClassReader(record).accept(new MyAnalysisVisitor());
});
```

## Parsing Bytecode Instructions

```java
public class InstructionVisitor extends MethodVisitor {
    @Override
    public void visitInsn(int opcode) {
        // Handle simple instructions
    }

    @Override
    public void visitLabel(Label label) {
        System.out.println("Label at offset: " + label.getOffset());
    }
}
```
