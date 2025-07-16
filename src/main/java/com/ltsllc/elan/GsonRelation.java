package com.ltsllc.elan;

import java.util.Map;

/**
 * A relation without cycles so it is gson safe.
 */
public class GsonRelation {
    protected String source;
    protected String destination;
    protected double trust;
    protected Relation.TrustType type;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public double getTrust() {
        return trust;
    }

    public void setTrust(double trust) {
        this.trust = trust;
    }

    public Relation.TrustType getType() {
        return type;
    }

    public void setType(Relation.TrustType type) {
        this.type = type;
    }

    public GsonRelation (String source, String destination, double trust, Relation.TrustType type) {
        this.source = source;
        this.destination = destination;
        this.trust = trust;
        this.type = type;
    }

    public Relation buildRelation(Map<String, Principal> map) {
        Relation relation = new Relation((Principal) map.get(source), (Principal) map.get(destination), trust, type);
        return relation;
    }
}
