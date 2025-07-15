package com.ltsllc.elan;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class ElanTest {

    @Test
    void main1() {
        Elan elan = new Elan();
        String[] args = {};
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        Elan.in = System.in;
        Elan.out = printStream;
        Elan.main1(args);

        assert(new String(baos.toByteArray()).startsWith("usage"));
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