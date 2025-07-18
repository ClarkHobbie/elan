package com.ltsllc.elan;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ltsllc.commons.test.TestCase;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.HashMap;

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

    @Test
    void processReport() throws Exception {

        File file = new File("whatever");
        if (file.exists()) {
            file.delete();
        }

        File testFile = new File("test");
        if (testFile.exists()) {
            testFile.delete();
        }

        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setPrettyPrinting();
            gsonBuilder.serializeNulls();
            Gson gson = gsonBuilder.create();

            Principal root = buildNetwork();
            GsonPrincipal gsonPrincipal = root.buildGsonPrincipal(new HashMap<>());
            String jsonText = gson.toJson(gsonPrincipal);

            FileOutputStream fileOutputStream = new FileOutputStream("test");
            fileOutputStream.write(jsonText.getBytes());
            fileOutputStream.close();

            TrustStore trustStore = new TrustStore(file);
            trustStore.setRoot(root);
            trustStore.store();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(baos);
            Elan.out = printStream;

            Elan elan = new Elan();
            String[] args = { "three" };
            elan.processReport(trustStore, args);

            String output = new String(baos.toByteArray());
            String expected = "one --> (75.0%) three";

            assert(output.equalsIgnoreCase(expected));
        } finally {
            if (file.exists()) {
                file.delete();
            }

            if (testFile.exists()) {
                testFile.delete();
            }
        }
    }



}