package xyz.metratrj;

import xyz.metratrj.system.ClassFile;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class Programm {

    public static void main(String[] args)  {
        Path startPath = Paths.get("out/production/TestModule/");
        try (Stream<Path> stream = Files.walk(startPath)){
            stream.filter(Files::isRegularFile).forEach(path -> {
                System.out.println(path);
            });
            Stream<Path>streams  = Files.walk(startPath);
            streams.forEachOrdered(path -> {
                System.out.println(path);
            });
        } catch (Exception e) {
            System.out.println(e.getMessage() + Arrays.toString(e.getStackTrace()));
        }

        try {
            ArrayList<ClassFile> arr = new ArrayList<>();
            Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file,
                                                 BasicFileAttributes attrs) throws IOException {
                    System.out.println("Datei: "+ file);
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
                public FileVisitResult visitFileFailed(Path file,
                                                       IOException exc) throws IOException {
                    System.err.println("Fehler beim Lesen der Datei: "+ file);
                    return FileVisitResult.CONTINUE;
                    // return super.visitFileFailed(file, exc);
                }
            });

            System.out.println(Arrays.toString(arr.toArray()));

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }



        String filePath = "out/production/TestModule/xyz/metratrj/Main.class";
        if (args.length > 0)
            filePath = args[0];
        try (DataInputStream in = new DataInputStream(new FileInputStream(filePath))){
            // 1. Magic Number (4 Byte)
            int magic = in.readInt();
            if (magic == 0xCAFEBABE) {
                System.out.printf("MagicNumber: %X Correct\n", magic);
            } else {
                System.out.println("No valid Java Class");
                return;
            }
        } catch (IOException e) {
            System.out.printf("IOException %s", e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
