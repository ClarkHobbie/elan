package com.ltsllc.elan;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class ReportableTest {

    @Test
    void printIndent() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        Elan.out = printStream;

        Reportable reportable = new Reportable();
        reportable.printIndent(4);
        String string = new String(baos.toByteArray());

        assert (string.equalsIgnoreCase("    "));
    }
}