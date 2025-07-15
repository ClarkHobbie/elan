package com.ltsllc.elan;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PrincipalTest {
    @Test
    public void constructor() {
        Principal principal  = new Principal("fred", null);
        assert (principal.hasSameName("fred"));
    }

    @org.junit.jupiter.api.Test
    void report() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Elan.out = new PrintStream(baos);

        Principal source = new Principal("one", null);
        Principal destination = new Principal("two", source);
        Relation relation = new Relation(source, destination, 0.99, Relation.TrustType.direct);
        Relation[] relations = { relation };
        Principal principal = new Principal("one", null, relations);
        principal.report(0);
        String output = new String(baos.toByteArray());

        assert (output.startsWith("one has"));

    }

    @Test
    public void reportFor() {
        Principal one = new Principal("one", null);
        Principal two = new Principal("two", one);
        Principal three = new Principal("three", one);

        Relation relation = new Relation(one, two, 0.99, Relation.TrustType.recommendation);
        one.addRelation("two", relation);
        relation = new Relation(one, three, 0.75, Relation.TrustType.direct);
        one.addRelation("three", relation);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Elan.out = new PrintStream(baos);

        three.reportFor();

        String output = new String(baos.toByteArray());

        assert (output.endsWith("three (75.0) "));
    }

    @Test
    public void buildLeaves() {
        Principal one = new Principal("one", null);
        Principal two = new Principal("two", one);
        Principal three = new Principal("three", one);

        Relation relation = new Relation(one, two, 0.99, Relation.TrustType.recommendation);
        one.addRelation("two", relation);
        relation = new Relation(one, three, 0.75, Relation.TrustType.direct);
        one.addRelation("three", relation);

        Map<String, Relation> map = one.buildLeaves(null);

        Principal temp1 = map.get(three.getName()).getSource();
        Principal temp2 = map.get(three.getName()).getDestination();

        assert (temp1 == one && temp2 == three);
    }
}