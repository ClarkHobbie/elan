package com.ltsllc.elan;

/**
 * A relationship between two {@link Principal}s.
 */
public class Relation extends Reportable {
    protected double trust;
    protected TrustType type;
    protected Principal source;
    protected Principal destination;

    public enum TrustType {
        direct,
        recommendation
    }

    public Relation (Principal source, Principal destination, double trust, TrustType trustType) {
        this.source = source;
        this.destination = destination;
        this.trust = trust;
        this.type = trustType;
    }


    public Principal getDestination() {
        return destination;
    }

    public void setDestination(Principal destination) {
        this.destination = destination;
    }

    public Principal getSource() {
        return source;
    }

    public void setSource(Principal source) {
        this.source = source;
    }

    public double getTrust() {
        return trust;
    }

    public void setTrust(double trust) {
        this.trust = trust;
    }

    public TrustType getType() {
        return type;
    }

    public void setType(TrustType type) {
        this.type = type;
    }

    public void report (int indent) {
        printIndent(indent);
        Elan.out.print (" trusts(" + asPercentage(trust) + "%) ");
    }
}
