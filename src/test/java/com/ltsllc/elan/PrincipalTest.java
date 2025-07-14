package com.ltsllc.elan;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

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
}