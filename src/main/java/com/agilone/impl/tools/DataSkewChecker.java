package com.agilone.impl.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class DataSkewChecker {

    public static void main(String[] args) {
        String query;
        Path path = Paths.get(args[0]);
        try {
            query = Files.lines(path).collect(Collectors.joining());
            System.out.println( query);
            query = query.replaceAll( "\t", " ");

            Files.lines(path)
                    .filter( line -> line.contains("BIGINT"))
                    .forEach(line -> System.out.println(line));

            System.out.println( query);
        }
        catch( IOException ioe)
        {
            ioe.printStackTrace();
            System.exit(1);
        }
    }
}
