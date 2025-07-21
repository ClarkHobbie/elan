package com.ltsllc.elan;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PrincipalTest extends ElanTestCase{
    @Test
    public void constructor() {
        Principal principal  = new Principal("fred", null);
        assert (principal.hasSameName("fred"));
    }

    @Test
    public void report() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Elan.out = new PrintStream(baos);

        Principal one = new Principal("one", null);
        Principal two = new Principal("two", one);

        Relation relation = new Relation(one, two, 0.75, Relation.TrustType.direct);
        one.addRelation("two", relation);

        two.report();

        String output = new String(baos.toByteArray());

        assert (output.startsWith("one -->"));
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

        three.report();

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

    @Test
    public void show() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        Elan.out = printStream;

        Principal root = buildNetwork();
        root.show();

        String output = new String(baos.toByteArray());
        String expected = new String("one  --> (99.0) two  --> (99.0) twoDotTwo \r\n    twoDotOne \r\n    three ");
        assert (output.equalsIgnoreCase(expected));
    }

    @Test
    public void printReport() {
        Principal root = buildNetwork();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        Elan.out = printStream;

        root.printReport();

        String output = new String(baos.toByteArray());
        String expected = "one";

        assert (output.equalsIgnoreCase(expected));
    }

    @Test
    public void removePrincipal() {
        Principal root = buildNetwork();
        Map<String, Principal> principalMap = new HashMap<>();
        root.buildPrincipalMap(principalMap);
        Principal subject = principalMap.get("twoDotTwo");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        Elan.out = printStream;

        root.removePrincipal(subject);

        String output = new String(baos.toByteArray());
        String expected = "";

        assert (output.equalsIgnoreCase(expected));

        principalMap = new HashMap<>();
        root.buildPrincipalMap(principalMap);

        assert (!principalMap.containsKey("twoDotTwo"));
    }

    @Test
    public void getTrust () {
        Principal root = buildNetwork();
        double trust = root.getTrust();

        assert (trust == 1.0);

        Map<String, Principal> principalMap = new HashMap<>();
        root.buildPrincipalMap(principalMap);
        Principal three = principalMap.get("three");

        trust = three.getTrust();

        Relation relation =new Relation(root, three, 1.0, Relation.TrustType.direct);
        root.addRelation("three", relation);
        double trust2 = three.getTrust();

        assert (trust != trust2);
    }

    @Test
    public void getTrustForRelation () {
        Principal root = buildNetwork();
        Map<String, Principal> principalMap = new HashMap<>();
        root.buildPrincipalMap(principalMap);
        Principal three = principalMap.get("three");

        Relation relation = new Relation(root, three, 1.0, Relation.TrustType.direct);
        double trust = root.getTrust(relation);

        assert (trust == 1.0);
    }

    @Test
    public void removeRelation() {
        Principal root = buildNetwork();

        Relation relation = root.getRelations().get("three");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        Elan.out = printStream;

        root.removeRelation(relation);

        String output = new String(baos.toByteArray());
        String expected = "";

        assert (output.equalsIgnoreCase(expected));
    }
}