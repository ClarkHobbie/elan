package com.ltsllc.elan;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class WebTest {

    /*
    @Test
    void buildLeaves() {
        List<Relation> list = new ArrayList<>();

        Principal one = new Principal("one", null);
        Principal two = new Principal("two", one);
        Principal three = new Principal("three", one);

        Relation relation = new Relation(one,two, 0.99, Relation.TrustType.recommendation);
        one.getRelations().put (one.getName(), relation);

        relation = new Relation(one, three, 0.75, Relation.TrustType.direct);
        one.getRelations().put(three.getName(), relation);

        Web web = new Web(one);
        List<Relation> list2 = web.buildLeaves(one);

        assert (list2.contains(one.getRelations().get(three.getName())));
        assert (one.getRelations().get(two.getName()) == null);
    }

     */


    @Test
    void reportFor() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Elan.out = new PrintStream(baos);

        Principal one = new Principal("one", null);
        Principal two = new Principal("two", one);
        Principal three = new Principal("three", one);
        Relation relation = new Relation(one, two, 0.99, Relation.TrustType.recommendation);
        one.getRelations().put (relation.getDestination().getName(), relation);
        relation = new Relation(one, three, 0.75, Relation.TrustType.direct);
        one.getRelations().put (relation.getDestination().getName(), relation);

        Web web = new Web(one);
        web.printReportFor(three);
        String output = new String(baos.toByteArray());

        assert (output.endsWith("three (75.0)\r\n"));
    }


}