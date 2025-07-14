package com.ltsllc.elan;

/**
 * A class that prints reports.
 * <P>
 *     This class collects methods that are useful to classes that print reports.
 * </P>
 */
public class Reportable {
    public void printIndent (int indent) {
        for (int i = 0; i < indent; i++) {
            Elan.out.print(" ");
        }
    }

    public double asPercentage(double value) {
        value *= 100;
        return value;
    }
}
