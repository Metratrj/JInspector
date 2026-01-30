package xyz.metratrj;

import xyz.metratrj.system.*;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

public class Programm {
    private static final Logger logger = Logger.getLogger(ClassFile.class.getName());

    static void main(String[] args) {
        Path startPath = Paths.get("out/production/TestModule/");
        /*try (Stream<Path> stream = Files.walk(startPath)) {
            stream.filter(Files::isRegularFile).forEach(System.out::println);
            Stream<Path> streams = Files.walk(startPath);
            streams.forEachOrdered(path -> {
                System.out.println(path);
            });
        } catch (Exception e) {
            System.out.println(e.getMessage() + Arrays.toString(e.getStackTrace()));
        }*/

        try {
            ArrayList<ClassFile> arr = new ArrayList<>();
            Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    System.out.println("Datei: " + file);
                    try {
                        ClassFile classFile = ClassFile.parse(file);
                        arr.add(classFile);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                    return FileVisitResult.CONTINUE;

                    // return super.visitFile(file, attrs);
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    System.err.println("Fehler beim Lesen der Datei: " + file);
                    return FileVisitResult.CONTINUE;
                    // return super.visitFileFailed(file, exc);
                }
            });

            System.out.println(Arrays.toString(arr.toArray()));

            // Okay now make something that make also sense to read
            for (ClassFile classFile : arr) {
                // Klassenname
                int    classNameIndex = classFile.getConstantPoolItem(classFile.getThisClass(), CONSTANT_Class_info.class).name_index;
                String className      = classFile.getConstantPoolItem(classNameIndex, CONSTANT_Utf8_info.class).getValue();
                System.out.println(className);

                // Superklassen Name
                int    superClassNameIndex = classFile.getConstantPoolItem(classFile.getSuperClass(), CONSTANT_Class_info.class).name_index;
                String superClassName      = classFile.getConstantPoolItem(superClassNameIndex, CONSTANT_Utf8_info.class).getValue();
                System.out.println(superClassName);


                ArrayList<AccessFlags> classAccessFlagsArr = new ArrayList<>();
                int                    classAccessFlag     = classFile.getAccessFlags();
                ExtractAccessModifierFlags(classAccessFlagsArr, classAccessFlag);


                // Methods
                for (method_info methodInfo : classFile.getMethods()) {
                    int    nameIndex = methodInfo.getNameIndex();
                    String name      = classFile.getConstantPoolItem(nameIndex, CONSTANT_Utf8_info.class).getValue();
                    System.out.println(name);
                    int    descriptionIndex = methodInfo.getDescriptionIndex();
                    String description      = classFile.getConstantPoolItem(descriptionIndex, CONSTANT_Utf8_info.class).getValue();
                    System.out.println(description);
                    System.out.println(Arrays.toString(methodInfo.getAttributes()));

                    ArrayList<AccessFlags> methodAccessFlags = new ArrayList<>();
                    int                    accessFlags       = methodInfo.getAccessFlags();
                    ExtractAccessModifierFlags(methodAccessFlags, accessFlags);
                    if ((accessFlags & ClassFile.ACC_SYNCHRONIZED) != 0) {
                        methodAccessFlags.add(AccessFlags.SYNCHRONIZED);
                    }
                    if ((accessFlags & ClassFile.ACC_BRIDGE) != 0) {
                        methodAccessFlags.add(AccessFlags.BRIDGE);
                    }
                    if ((accessFlags & ClassFile.ACC_VARARGS) != 0) {
                        methodAccessFlags.add(AccessFlags.VARARGS);
                    }
                    if ((accessFlags & ClassFile.ACC_NATIVE) != 0) {
                        methodAccessFlags.add(AccessFlags.NATIVE);
                    }
                    if ((accessFlags & ClassFile.ACC_ABSTRACT) != 0) {
                        methodAccessFlags.add(AccessFlags.ABSTRACT);
                    }
                    if ((accessFlags & ClassFile.ACC_STRICT) != 0) {
                        methodAccessFlags.add(AccessFlags.STRICT);
                    }
                    if ((accessFlags & ClassFile.ACC_SYNTHETIC) != 0) {
                        methodAccessFlags.add(AccessFlags.SYNCHRONIZED);
                    }

                    System.out.printf("\nMethodName: %s\nFlags: %s\n\n", name, methodAccessFlags);

                }


                // Fields
                for (field_info fieldInfo : classFile.getFields()) {
                    ArrayList<AccessFlags> fieldAccessFlags = new ArrayList<>();
                    int                    accessFlags      = fieldInfo.getAccess_flags();

                    ExtractAccessModifierFlags(fieldAccessFlags, accessFlags);
                    if ((accessFlags & ClassFile.ACC_VOLATILE) != 0) {
                        fieldAccessFlags.add(AccessFlags.VOLATILE);
                    }
                    if ((accessFlags & ClassFile.ACC_TRANSIENT) != 0) {
                        fieldAccessFlags.add(AccessFlags.TRANSIENT);
                    }
                    if ((accessFlags & ClassFile.ACC_SYNTHETIC) != 0) {
                        fieldAccessFlags.add(AccessFlags.SYNTHETIC);
                    }
                    if ((accessFlags & ClassFile.ACC_ENUM) != 0) {
                        fieldAccessFlags.add(AccessFlags.ENUM);
                    }

                    String fieldName = classFile.getConstantPoolItem(fieldInfo.getName_index(), CONSTANT_Utf8_info.class).getValue();

                    System.out.printf("\nFieldName: %s\nFlags: %s\n\n", fieldName, fieldAccessFlags);
                    System.out.println(Arrays.toString(fieldInfo.getAttributes()));
                }
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


        String filePath = "out/production/TestModule/xyz/metratrj/Main.class";
        if (args.length > 0) {
            filePath = args[0];
        }
        try (DataInputStream in = new DataInputStream(new FileInputStream(filePath))) {
            // 1. Magic Number (4 Byte)
            int magic = in.readInt();
            if (magic == 0xCAFEBABE) {
                System.out.printf("MagicNumber: %X Correct\n", magic);
            }
            else {
                System.out.println("No valid Java Class");
            }
        } catch (IOException e) {
            System.out.printf("IOException %s", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void ExtractAccessModifierFlags(ArrayList<AccessFlags> fieldAccessFlags, int accessFlags) {
        if ((accessFlags & ClassFile.ACC_PUBLIC) != 0) {
            fieldAccessFlags.add(AccessFlags.PUBLIC);
        }
        if ((accessFlags & ClassFile.ACC_PRIVATE) != 0) {
            fieldAccessFlags.add(AccessFlags.PRIVATE);
        }
        if ((accessFlags & ClassFile.ACC_PROTECTED) != 0) {
            fieldAccessFlags.add(AccessFlags.PROTECTED);
        }
        if ((accessFlags & ClassFile.ACC_STATIC) != 0) {
            fieldAccessFlags.add(AccessFlags.STATIC);
        }
        if ((accessFlags & ClassFile.ACC_FINAL) != 0) {
            fieldAccessFlags.add(AccessFlags.FINAL);
        }
    }

}
