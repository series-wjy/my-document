/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author wangjiayou 2019/9/6
 * @version ORAS v1.0
 */
public class IterateDirectory {
    public static void main(String[] args) throws IOException {
        System.out.println("========================file or directories in current path==============================");
        Files.list(Paths.get(".").toAbsolutePath()).forEach(x -> System.out.println(x.toAbsolutePath()));

        // working with a large directory,DirectoryStrem more faster
        Files.newDirectoryStream(Paths.get(".")).forEach(System.out::println);


        System.out.println("========================only file in current path==============================");
        Files.list(Paths.get(".")).filter(Files::isRegularFile).forEach(System.out::println);

        // working with a large directory,DirectoryStrem more faster
        Files.newDirectoryStream(Paths.get("./jdk8-new-feature"),
                path -> path.toFile().isFile()).forEach(System.out::println);
        Files.newDirectoryStream(Paths.get("./jdk8-new-feature"),
                path -> path.toString().endsWith(".xml")).forEach(System.out::println);

        System.out.println("========================hidden file==============================");
        final File[] files1 = new File(".").listFiles(file -> file.isHidden());
//or
        final File[] files2 = new File(".").listFiles(File::isHidden);
        for(File f : files2) {
            System.out.println(f.toString());
        }
    }
}
