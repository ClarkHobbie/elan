package com.ltsllc.elan;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class Elan {
    public static InputStream in = null;
    public static PrintStream out = null;

    public static void main(String[] args) {
        if (args.length < 2) {
            printUsage();
            System.exit (1);
        }
    }

    public static void printUsage() {
        Elan.out.println("usage: elan <principal> <trustStore>");
    }
}
