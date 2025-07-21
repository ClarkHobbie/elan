package com.ltsllc.elan;

import com.ltsllc.commons.util.ImprovedArrays;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * A system for managing trust.
 * <P>
 *     elan performs commands relating to trust. Commands have the form:
 * </P>
 * <P>
 *     elan &lt;truststore&gt; &lt;command&gt; &lt;arguments&gt;
 * </P>
 * <P>
 *     For example
 * </P>
 * <P>
 *     elan whatever remove principal three
 * </P>
 * <P>
 *     Uses "whatever" as the filename for the truststore, remove principal as the command and three as the name of the
 *     principal to remove.
 * </P>
 * <P>
 *     The command are:
 *     <UL>
 *         <LI>add principal</LI>
 *         <LI>add relation</LI>
 *         <LI>remove principal</LI>
 *         <LI>remove relation</LI>
 *         <LI>report</LI>
 *     </UL>
 * </P>
 */
public class Elan {
    public static InputStream in = System.in;
    public static PrintStream out = System.out;
    public static PrintStream err = System.err;
    public static int exitCode = 0;
    public static Map<String, Principal> principals = new HashMap<>();

    public static void main(String[] args) {
        Elan elan = new Elan();
        elan.main1(args);
        System.exit(exitCode);
    }

    /**
     * The real main method.
     * <P>
     *     The differences between this method and {@link Elan#main(String[])} include
     *     <UL>
     *         <LI>{@link #main1(String[])} is an instance method.</LI>
     *     </UL>
     * </P>
     * <P>
     *     This method expects a command line to be of the form:
     * </P>
     * <P>
     *     elan &lt;truststore&gt; &lt;command&gt; &lt;arguments&gt;
     * </P>
     * <P>
     *     Where &lt;command&gt; is one of
     *     <UL>
     *         <LI>report</LI>
     *         <LI>add principal</LI>
     *         <LI>remove principal</LI>
     *         <LI>add relation</LI>
     *         <LI>remove relation</LI>
     *     </UL>
     * </P>
     * @param args args[0] should contain the truststore filename, args[1] should contain the first part of the
     *             command to be performed.
     */
    public void main1(String[] args) {
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
            Elan.err.println("error with truststore file, " + trustStoreFileName);
            e.printStackTrace(Elan.err);
            Elan.exitCode = 1;
            return;
        }

        Map<String, Principal> principalMap= new HashMap<>();
        trustStore.getRoot().buildPrincipalMap(principalMap);
        principals = principalMap;

        String command = args[1];
        args = ImprovedArrays.restOf(args,2);

        Elan elan = new Elan();
        elan.processCommand(command, trustStore, args);
    }

    public void processCommand (String command, TrustStore trustStore, String[] args) {
        if (command.equalsIgnoreCase("add")) {
            processAdd(trustStore, args);
        } else if (command.equalsIgnoreCase("report")) {
            processReport(trustStore, args);
        } else if (command.equalsIgnoreCase("remove")) {
            processRemove(trustStore, args);
        } else {
            Elan.err.println("unknown command, " + command);
            Elan.exitCode = 1;
            return;
        }
    }

    private void processRemove(TrustStore trustStore, String[] args) {
        if (args.length < 2) {
            Elan.err.println("usage: elan <trustStore> remove <what> <arguments>");
            Elan.exitCode = 1;
            return;
        }
        
        String command = args[0];
        args = ImprovedArrays.restOf(args, 1);
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

    /**
     * Remove a relation.
     * <P>
     *     The form of the command is:
     * </P>
     * <P>
     *     elan remove relation &lt;principal name containing the relation&gt; &lt;the destination name of the relation&gt;
     * </P>
     * @param trustStore The truststore for the operation.
     * @param args The arguments for the operation.  The principal name should be args[0] and the destination name
     *             should be in args[1].
     */
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

    /**
     * Remove an existing principal from the system.
     * <P>
     *     The form of the command is
     * </P>
     * <P>
     *     elan &lt;truststore&gt; remove principal &lt;name of the principal&gt;
     * </P>
     * @param trustStore The truststore for the operation.
     * @param args The arguments for the command.  The name of the principal to be removed should be in args[0].
     */
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

    public void processReport(TrustStore trustStore) {
        trustStore.getRoot().report();
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


    /**
     * Add a principal to the system.
     * <P>
     *     Since a principal cannot be added without a relation, this method adds that too.
     * </P>
     *
     * <P>
     *     The form of the command is
     * </P>
     *
     * <P>
     *     elan &lt;truststore&gt; add principal &lt;new principal name&gt; &lt;source name&gt; &lt;trust&gt; &lt;type&gt;
     * </P>
     * @param trustStore The truststore for the operation.
     * @param args The arguments for the command.  The name of the principal to be added should be in args[0], the
     *             source name in args[1], the level of trust in args[2] and the type in args[3].
     */
    public void processAddPrincipal(TrustStore trustStore, String[] args) {
        // elan <trustStore> add principal <name> <source name> <trust> <direct or recommendation>
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

    /**
     * Report on a principal.
     * <P>
     *     A report takes the form of how much trust you put in a principal (defined as the trust that you put in each
     *     relation along the way) as well as the path the principal takes to the root. Each link is printed as an
     *     arrow (-->) with the aggregate trust in parens.  A principal's trust is the product of the trust in a
     *     relation times the trust in the source of the relation.
     * </P>
     * @param trustStore The truststore for the operation.
     * @param args The arguments for the command.  args[0] should contain the name of the principal to be reported on.
     *
     */
    public void processReport(TrustStore trustStore, String[] args) {
        if (args.length < 1) {
            Elan.err.println("usage: elan <trustStore> report <subject>");
            Elan.exitCode = 1;
            return;
        }

        String subject = args[0];
        Map<String, Principal> principalMap = new HashMap<>();
        trustStore.getRoot().buildPrincipalMap(principalMap);

        if (principalMap.get(subject) == null) {
            Elan.err.print("subject, ");
            Elan.err.print(subject);
            Elan.err.print(", is unknown");
            Elan.exitCode = 0;
            return;
        }

        Principal subjectPrincipal = principalMap.get(subject);
        subjectPrincipal.printReport();
    }

    public static void printUsage() {
        Elan.err.println("usage: elan <trustStore> <command> <arguments>");
    }

    /**
     * Add a relation to an existing principal in the system.
     * <P>
     *     The source and destination principal must exist before the method is called.
     * </P>
     *
     * @param trustStore The truststore for the operation.
     * @param args The arguments for the command.  The name of the principal to be added should be in args[0].  The name
     *             of the source should be in args[1], the level of trust should be in args[2], and the type should be
     *             in args[3].
     */
    public void processAddRelation(TrustStore trustStore, String[] args) {
        // <source name> <destination name> <trust> <type>
        if (args.length < 4) {
            Elan.err.println("usage: elan <trustStore> add relation <source> <destination> <trust> <type>");
            Elan.exitCode = 1;
            return;
        }

        String sourceString = args[0];
        String destSring = args[1];
        String trustString = args[2];
        String typeString = args[3];

        Principal source = principals.get(sourceString);
        if (source == null) {
            Elan.err.println("the source, " + sourceString + ", was not found");
            Elan.exitCode = 1;
            return;
        }

        Principal destination = principals.get(destSring);
        if (destination == null) {
            Elan.err.println("the destination, " + destSring + ", was not found");
            Elan.exitCode = 1;
            return;
        }

        if (destination.getRelations().containsKey(destSring)) {
            Elan.err.println("the destination, " + destSring + ", already exists");
            Elan.exitCode = 1;
            return;
        }

        double trust = Double.valueOf(trustString);
        Relation.TrustType type = Relation.TrustType.valueOf(typeString);

        Relation relation = new Relation(source, destination, trust, type);
        source.addRelation(destSring, relation);
    }
}
