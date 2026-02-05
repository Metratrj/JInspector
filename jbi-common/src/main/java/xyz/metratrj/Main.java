package xyz.metratrj;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    List<byte[]> readBytecode(Path root) throws IOException {
        // 1. Alle relevanten Pfade einsammeln ()
        Set<Path> allPaths;
        try (Stream<Path> paths = Files.walk(root, Integer.MAX_VALUE, FileVisitOption.FOLLOW_LINKS)) {
            allPaths = paths
                    .filter(p -> p.endsWith(".class"))
                    .filter(p -> !p.endsWith("module-info.class"))
                    .filter(p -> !p.endsWith("package-info.class"))
                    .collect(Collectors.toUnmodifiableSet());
        }


        try (Stream<Path> paths = Files.walk(root, Integer.MAX_VALUE, FileVisitOption.FOLLOW_LINKS)) {
            return paths
                    .filter(p -> p.endsWith(".class"))
                    .filter(p -> !p.endsWith("module-info.class"))
                    .filter(p -> !p.endsWith("package-info.class"))
                    .parallel()
                    .map(p -> {
                        try {
                            return Files.readAllBytes(p);
                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
    }

    static void main() {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        IO.println(String.format("Hello and welcome!"));

        for (int i = 1; i <= 5; i++) {
            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
            IO.println("i = " + i);
        }
    }
}
