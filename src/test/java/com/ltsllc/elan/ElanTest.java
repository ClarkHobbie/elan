package com.ltsllc.elan;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ltsllc.commons.test.TestCase;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    void processAddRelation() throws Exception {
        // elan <trustStore file name> add relation <source name> <destination name> <trust> <type>

        File trustStoreFile = new File("whatever");

        String[] args = {
                "two",
                "twoDotThree",
                "0.66",
                "direct"
        };

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(baos);
            Elan.out = printStream;

            TrustStore trustStore = new TrustStore(trustStoreFile);
            trustStore.setRoot(buildNetwork());
            trustStore.store();

            Elan elan = new Elan();
            elan.processAddRelation(trustStore, args);

            args = new String[]{
                    "whatever",
                    "show"
            };

            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
            printStream = new PrintStream(baos2);
            Elan.err = printStream;

            Elan.main1(args);

            String errorOutput = new String(baos2.toByteArray());
            String output = new String(baos.toByteArray());
            String expected = "one  --> (99.0) two  --> (99.0) twoDotTwo \r\n" +
                    "    twoDotOne \r\n" +
                    "    three ";

            assert (output.equalsIgnoreCase(expected));
        } finally {
            if (trustStoreFile.exists()) {
                trustStoreFile.delete();
            }
        }
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

    @Test
    void processRemovePrincipal () throws Exception {
        Principal root = buildNetwork();

        File trustStoreFile = new File("whatever");

        TrustStore trustStore = buildTrustStore(trustStoreFile);

        String[] args = { "three" };

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        Elan.out = printStream;

        Elan elan = new Elan();
        elan.processRemovePrincipal(trustStore, args);

        String output = new String(baos.toByteArray());
        String expected = "";

        assert(output.equalsIgnoreCase(expected));

        Map<String, Principal> principalMap = new HashMap<>();
        trustStore.getRoot().buildPrincipalMap(principalMap);
        Principal principal = principalMap.get("three");

        assert (principal == null);
    }

    @Test
    void processRemoveRelation () throws Exception {
        String[] args = { "one", "three" };

        ByteArrayOutputStream outBaos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outBaos);
        Elan.out = printStream;

        ByteArrayOutputStream errBaos = new ByteArrayOutputStream();
        printStream = new PrintStream(errBaos);
        Elan.err = printStream;

        Principal root = buildNetwork();
        Map<String, GsonPrincipal> gsonPrincipalMap = new HashMap<>();
        root.buildGsonPrincipalMap(gsonPrincipalMap);
        List<GsonPrincipal> list = new ArrayList<>(gsonPrincipalMap.values());

        File file = new File("whatever");

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.serializeNulls();
        Gson gson = gsonBuilder.create();

        String json = gson.toJson(list);

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(json.getBytes());
        fileOutputStream.close();

        TrustStore trustStore = new TrustStore(file);
        trustStore.load();

        Elan elan = new Elan();
        elan.processRemoveRelation(trustStore, args);

        String output = new String(outBaos.toByteArray());
        String expectedOutput = "";

        String errorOutput = new String(errBaos.toByteArray());
        String expectedErrorOutput = "";

        assert (output.equalsIgnoreCase(expectedOutput));
        assert (errorOutput.equalsIgnoreCase(expectedErrorOutput));

        Principal principal = trustStore.getRoot();

        assert (!principal.getRelations().containsKey("three"));
    }
}