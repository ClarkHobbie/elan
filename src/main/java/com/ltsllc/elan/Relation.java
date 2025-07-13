package com.ltsllc.elan;

/**
 * A relationship between two {@link Principal}s.
 */
public class Relation {
    protected double trust;
    protected TrustType type;

    public enum TrustType {
        direct,
        recommendation
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
}
