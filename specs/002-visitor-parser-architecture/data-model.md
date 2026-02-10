# Data Model: Visitor-based Parser Architecture

## Entities

### DataRecord (Record)
Represents a raw class file or archive entry loaded into memory.
- `path`: `String` (Original file path or JAR entry path)
- `data`: `byte[]` (Raw binary content)

### Label
Represents a position/offset within a method's bytecode.
- `offset`: `int` (The absolute offset in the bytecode array, initialized after parsing)
- `name`: `String` (Optional identifier for debugging)

### ClassFileStructure (Internal)
Internal representation used by `ClassReader` during parsing.
- `constantPool`: `ConstantPool` (Resolved constants)
- `accessFlags`: `int`
- `thisClass`: `String`
- `superClass`: `String`
- `interfaces`: `List<String>`
- `fields`: `List<FieldInfo>`
- `methods`: `List<MethodInfo>`

## State Transitions
1. **Loading**: `Path` / `JarFile` → `List<DataRecord>` (Parallel process)
2. **Accepting**: `DataRecord` → `ClassReader`
3. **Visiting**: `ClassReader` → triggers events on `ClassVisitor` → triggers `FieldVisitor` / `MethodVisitor`
4. **Instruction Parsing**: `MethodInfo.code` → Label identification → Sequential instruction visit with `visitLabel`, `visitFrame`, `visitLineNumber`.

## Validation Rules
- **Binary Integrity**: `DataRecord.data` must start with `0xCAFEBABE`.
- **Version Check**: `ClassReader` must support class versions defined by Java 25.
- **Label Resolution**: All jump targets in bytecode must point to valid instruction offsets.
