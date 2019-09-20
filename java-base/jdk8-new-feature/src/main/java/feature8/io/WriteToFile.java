/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package feature8.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author wangjiayou 2019/9/6
 * @version ORAS v1.0
 */
public class WriteToFile {
    public static void main(String[] args) throws IOException {
        //Get the file feature8.reference
        Path path = Paths.get("d:/output.txt");

        //Use try-with-resource to get auto-closeable writer instance
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
            writer.append("Hello World !!");
        }

        String content = "Hello World2 !!";
//        Files.write(Paths.get("d:/output.txt"), content.getBytes());
    }
}
