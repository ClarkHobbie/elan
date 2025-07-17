package com.ltsllc.elan;

import com.ltsllc.commons.test.TestCase;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class ElanTest extends ElanTestCase {

    @Test
    void main1() {
        Elan elan = new Elan();
        String[] args = {};

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        Elan.in = System.in;
        Elan.err = printStream;

        Elan.main1(args);

        String string = new String(baos.toByteArray());

        assert(string.trim().startsWith("usage:"));
    }

    @Test
    void processAddPrincipal() throws Exception {
        File trustStoreFile = new File("whatever");

        TrustStore trustStore = new TrustStore(trustStoreFile);
        trustStore.setRoot(this.buildNetwork());
        trustStore.store();

        try {
            Elan elan = new Elan();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            elan.out = new PrintStream(baos);

            String[] args = {
                    "whatever",
                    "add",
                    "principal",
                    "twoDotThree",
                    "two",
                    "0.75",
                    "direct"
            };
            elan.main1(args);
            String output = new String(baos.toByteArray());

            assert(output.trim().equalsIgnoreCase(""));

            args = new String[]{"whatever", "add", "principal", "iDontExist"};
            baos = new ByteArrayOutputStream();
            Elan.err = new PrintStream(baos);
            elan.main1(args);
            output = new String(baos.toByteArray());

            assert(output.trim().equalsIgnoreCase("usage: elan <trustStore> add principal <name> <source> <trust> <type>"));
        } finally {
            if (trustStoreFile.exists()) {
                trustStoreFile.delete();
            }
        }
    }

    @Test
    void processCommand() {
        Elan elan = new Elan();


    }

    @Test
    void processAdd() {
    }

    @Test
    void processAddRelation() {
    }

    @Test
    void testProcessAddPrincipal() {
    }

    @Test
    void processReport() {
    }

    @Test
    void processShow() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        Elan.out = printStream;

        buildNetwork().show();

        String output = new String(baos.toByteArray());
        String expected = "one  --> (99.0) two  --> (99.0) twoDotTwo \r\n" +
                "    twoDotOne \r\n" +
                "    three ";

        assert (output.equalsIgnoreCase(expected));
    }

    /**
    @Test
    void processReport() throws Exception {
        Principal one = new Principal("one", null);
        Principal two = new Principal("two", one);
        Principal three = new Principal("three", one);

        Relation relation = new Relation(one, two, 0.99, Relation.TrustType.recommendation);
        one.addRelation("two", relation);
        relation = new Relation(one, three, 0.75, Relation.TrustType.direct);
        one.addRelation("three", relation);

        File file = new File("whatever");
        if (file.exists()) {
            file.delete();
        }

        try {
            TrustStore trustStore = new TrustStore(file);
            trustStore.setRoot(one);
            trustStore.store();

            Elan elan = new Elan();
            String[] args = { "three" };
            elan.processReport(trustStore,args);
        } finally {
            if (file.exists()) {
                file.delete();
            }
        }
    }
    */


}