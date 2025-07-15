package com.ltsllc.elan;

import com.ltsllc.commons.util.ImprovedRandom;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;

public class Elan {
    public static InputStream in = null;
    public static PrintStream out = null;
    public static int exitCode = 0;

    public static void main(String[] args) {
        main1(args);
        System.exit(exitCode);
    }

    public static void main1(String[] args) {
        if (args.length < 3) {
            printUsage();
            Elan.exitCode = 1;
            return;
        }

        String command = args[1];
        String trustStoreFile = args[0];
        Object object = Arrays.copyOfRange(args, 2, args.length - 1);

        TrustStore trustStore = new TrustStore(trustStoreFile);

        Elan elan = new Elan();
        elan.processCommand(command, trustStore, args);
    }

    public void processCommand(String command, TrustStore trustStore, String[] args) {
        if (command.equalsIgnoreCase("report")) {
            processReport (trustStore, args);
        } else {
            Elan.out.println("unknown command, " + command);
            Elan.exitCode = 1;
            return;
        }
    }

    public void processReport(TrustStore trustStore, String[] args) {
        if (args.length < 1) {
            Elan.out.println("usage: elan report <subject>");
            Elan.exitCode = 1;
            return;
        }

        String subject = args[0];

        if (trustStore.getRoot().getLeaves().get(subject) == null) {
            Elan.out.print("subject, ");
            Elan.out.print(subject);
            Elan.out.print(", is unknown");
            Elan.exitCode = 0;
            return;
        }

        Relation relation = trustStore.getRoot().getLeaves().get(subject);
        relation.getDestination().report();
    }

    public static void printUsage() {
        Elan.out.println("usage: elan <trustStore> <command> <arguments>");
    }
}
