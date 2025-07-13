package com.ltsllc.elan;

import java.io.InputStream;
import java.io.PrintStream;

public class Elan {
    public static InputStream in = null;
    public static PrintStream out = null;

    public static void main(String[] args) {
        if (args.length < 2) {
            printUsage();
            System.exit (1);
        }

        //
         // build a web of trust
        //
        TrustStore trustStore = new TrustStore(args[1]);

    }

    public static void printUsage() {
        Elan.out.println("usage: elan <principal> <trustStore>");
    }
}
