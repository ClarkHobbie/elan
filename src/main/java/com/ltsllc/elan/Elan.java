package com.ltsllc.elan;

import com.ltsllc.commons.util.ImprovedArrays;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class Elan {
    public static InputStream in = System.in;
    public static PrintStream out = System.out;
    public static PrintStream err = System.err;
    public static int exitCode = 0;
    public static Map<String, Principal> principals = new HashMap<>();

    public static void main(String[] args) {
        main1(args);
        System.exit(exitCode);
    }

    public static void main1(String[] args) {
        if (args.length < 2) {
            printUsage();
            Elan.exitCode = 1;
            return;
        }

        String trustStoreFileName = args[0];
        TrustStore trustStore = new TrustStore(trustStoreFileName);
        try {
            trustStore.load();
        } catch (IOException e) {
            throw new RuntimeException("error with trustStore file, " + trustStoreFileName, e);
        }

        Map<String, Principal> map = new HashMap<>();
        trustStore.getRoot().buildPrincipalMap(map);
        principals = map;

        String command = args[1];
        args = ImprovedArrays.restOf(args,2);

        Elan elan = new Elan();
        elan.processCommand(command, trustStore, args);
    }

    public void processCommand (String command, TrustStore trustStore, String[] args) {
        if (command.equalsIgnoreCase("add")) {
            processAdd(trustStore, args);
        } else if (command.equalsIgnoreCase("show")) {
            processShow(trustStore);
        } else if (command.equalsIgnoreCase("remove")) {
            processRemove(trustStore, args);
        } else {
            Elan.err.println("unknown command, " + command);
            Elan.exitCode = 1;
            return;
        }
    }

    private void processRemove(TrustStore trustStore, String[] args) {
        if (args.length < 1) {
            Elan.err.println("usage: elan <trustStore> remove <arguments>");
            Elan.exitCode = 1;
            return;
        }
        
        String command = args[0];
        args = ImprovedArrays.restOf(args, 2);
        if (command.equalsIgnoreCase("principal")) {
            processRemovePrincipal(trustStore, args);
        } else if (command.equalsIgnoreCase("relation")) {
            processRemoveRelation(trustStore, args);
        } else {
            Elan.err.println("unknown command, " + command);
            Elan.exitCode = 1;
            return;
        }
    }

    public void processRemoveRelation(TrustStore trustStore, String[] args) {
        //
         // elan <truststore> remove relation <principal name> <destination name>
        //
        if (args.length < 2) {
            Elan.err.println("usage: elam <truststore> remove relation <principal name> <destination name>");
            Elan.exitCode = 1;
            return;
        }

        String principalName = args[0];
        String destinationName = args[1];

        Map<String, Principal> principalMap = new HashMap<>();
        trustStore.getRoot().buildPrincipalMap(principalMap);
        Principal principal = principalMap.get(principalName);
        if (null == principal) {
            Elan.err.println("the principal, " + principalName + ", was not found");
            Elan.exitCode = 1;
            return;
        }

        Relation relation = principal.getRelations().get(destinationName);
        if (null == relation) {
            Elan.err.println("the relation, " + destinationName + ", was not found");
            Elan.exitCode = 1;
            return;
        }

        principal.removeRelation(relation);
    }

    public void processRemovePrincipal(TrustStore trustStore, String[] args) {
        if (args.length < 1) {
            Elan.err.println("usage: elan <trustStore> remove principal <principal name>");
            Elan.exitCode = 1;
            return;
        }
        
        String subjectName = args[0];
        Map<String, Principal> principalMap = new HashMap<>();
        trustStore.getRoot().buildPrincipalMap(principalMap);
        Principal subject = principalMap.get(subjectName);
        if (subject == null) {
            Elan.err.println("unknown principal, " + subjectName);
            Elan.exitCode = 1;
            return;
        }

        trustStore.getRoot().removePrincipal(subject);
    }

    public void processShow(TrustStore trustStore) {
        trustStore.getRoot().show();
    }

    public void processAdd(TrustStore trustStore, String[] args) {
        if (args.length < 2) {
            Elan.err.println("usage: elan <trustStore> add <what> <arguments>");
            Elan.exitCode = 1;
            return;
        }

        String subCommand = args[0];
        args = ImprovedArrays.restOf(args, 1);

        if (subCommand.equalsIgnoreCase("principal")) {
            processAddPrincipal(trustStore, args);
        } else if (subCommand.equalsIgnoreCase("relation")) {
            processAddRelation(trustStore, args);
        } else {
            Elan.err.println("unknown command, " + subCommand);
            Elan.exitCode = 1;
            return;
        }
    }

    public void processAddPrincipal(TrustStore trustStore, String[] args) {
        // elan <trustStore> add principal <name> <source name> <trust> <direct or recommendation
        if (args.length < 4) {
            Elan.err.println("usage: elan <trustStore> add principal <name> <source> <trust> <type>");
            Elan.exitCode = 1;
            return;
        }

        String principalName = args[0];
        String sourceName = args[1];
        String trustString = args[2];
        String experienceString = args[3];

        if (principals.containsKey(principalName)) {
            Elan.err.println("the name of the new principal, " + principalName + ", is already in use");
            Elan.exitCode = 1;
            return;
        }

        Principal source = principals.get(sourceName);
        if (source == null) {
            Elan.err.println("the source principal, " + sourceName + ", was not found.");
            Elan.exitCode = 1;
            return;
        }

        Principal destination = new Principal(principalName, source);
        if (principals.containsKey(destination.getName())) {
            Elan.err.println("the principal name, " + destination.getName() + ", is already in use");
            Elan.exitCode = 1;
            return;
        } else {
            principals.put(destination.getName(), destination);
        }

        double trustValue = Double.valueOf(trustString);

        Relation.TrustType trustType = Relation.TrustType.valueOf(experienceString);

        Relation relation = new Relation(source, destination, trustValue, trustType);

        source.addRelation(relation.getDestination().getName(), relation);
    }

    public void processReport(TrustStore trustStore, String[] args) {
        if (args.length < 1) {
            Elan.err.println("usage: elan report <trustStore> <subject>");
            Elan.exitCode = 1;
            return;
        }

        String subject = args[0];
        Map<String, Principal> map = new HashMap<>();
        trustStore.getRoot().buildPrincipalMap(map);
        Map<String, Principal> principalMap = map;

        if (principalMap.get(subject) == null) {
            Elan.err.print("subject, ");
            Elan.err.print(subject);
            Elan.err.print(", is unknown");
            Elan.exitCode = 0;
            return;
        }

        Principal subjectPrincipal = map.get(subject);
        subjectPrincipal.printReport();
    }

    public static void printUsage() {
        Elan.err.println("usage: elan <trustStore> <command> <arguments>");
    }

    public void processAddRelation(TrustStore trustStore, String[] args) {
        // <source name> <destination name> <trust> <type>
        if (args.length < 4) {
            Elan.err.println("usage: elan <trustStore> add relation <source> <destination> <trust> <type>");
            Elan.exitCode = 1;
            return;
        }

        Map<String, Principal> principalMap = new HashMap<>();
        trustStore.getRoot().buildPrincipalMap(principalMap);

        String sourceName = args[0];
        Principal source = principalMap.get(sourceName);
        String destName = args[1];
        Principal destination = principalMap.get(destName);
        double trust = Double.valueOf(args[2]);
        String typeString = args[3];

        Relation relation = new Relation(source, destination, trust, Relation.TrustType.valueOf(typeString));
        source.addRelation(destName, relation);
    }
}
