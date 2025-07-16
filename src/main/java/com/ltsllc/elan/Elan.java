package com.ltsllc.elan;

import com.ltsllc.commons.util.ImprovedArrays;
import com.ltsllc.commons.util.ImprovedRandom;

import java.io.IOException;
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

        String command = args[0];
        args = ImprovedArrays.restOf(args,1);

        Elan elan = new Elan();
        if (command.trim().equalsIgnoreCase("add")) {
            elan.processAdd(args);
        }
    }

    public void processCommand (String command, String[] args) {
        if (args.length < 1) {
            printUsage();
            Elan.exitCode = 1;
            return;
        }

        if (command.equalsIgnoreCase("add")) {
            if (args.length < 2) {
                printUsage();
                Elan.exitCode = 1;
                return;
            }

            args = ImprovedArrays.restOf(args,1);

            if (command.equalsIgnoreCase("add")) {
                processAdd(args);
            } else if (command.equalsIgnoreCase("report")) {
                processReport(args);
            }

        }
    }

    public void processAdd(String[] args) {
        if (args.length < 3) {
            Elan.out.println("usage: elan add principal <trustStore> <principal>");
            Elan.exitCode = 1;
            return;
        }

        String command = args[0];
        String trustStoreFileName = args[1];
        TrustStore trustStore = new TrustStore(trustStoreFileName);
        try {
            trustStore.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        args = ImprovedArrays.restOf(args, 2);

        if (command.equalsIgnoreCase("principal")) {
            processAddPrincipal(trustStore, args);
        } else if (command.equalsIgnoreCase("relation")) {
            processAddRelation(trustStore, args);
        } else {
            Elan.out.println("unknown command, " + command);
            printUsage();
            Elan.exitCode = 1;
            return;
        }
    }

    public void processAddRelation(TrustStore trustStore, String[] temp) {
    }

    public void processAddPrincipal(TrustStore trustStore, String[] args) {
        if (args.length < 1) {
            printUsage();
            Elan.exitCode = 1;
            return;
        }

        String principalName = args[0];
        Principal principal = trustStore.getRoot().findPrincipal(principalName);
        if (principal == null) {
            Elan.out.println("principal, " + principalName + ", not found");
            Elan.exitCode = 1;
            return;
        }
    }

    public void processReport(String[] args) {
        if (args.length < 2) {
            Elan.out.println("usage: elan report <trustStore> <subject>");
            Elan.exitCode = 1;
            return;
        }

        TrustStore trustStore = new TrustStore(args[0]);
        String subject = args[1];

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
