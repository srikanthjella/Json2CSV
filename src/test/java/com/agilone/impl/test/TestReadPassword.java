package com.agilone.impl.test;

import java.io.Console;

public class TestReadPassword {
    public static void main(String[] args) {
        Console console = System.console();
        char[] password = console.readPassword( "Enter Password");
        System.out.println( password);
    }
}
