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
}