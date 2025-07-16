package com.ltsllc.elan;

/**
 * A relationship between two {@link Principal}s.
 */
public class Relation extends Reportable {
    protected double trust;
    protected TrustType type;
    protected Principal source;
    protected Principal destination;

    public GsonRelation buildGsonRelation() {
        GsonRelation gsonRelation = new GsonRelation(source.getName(), destination.getName(), trust, type);
        return gsonRelation;
    }

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

    public boolean equals (Object object) {
        if (null == object || !(object instanceof Relation)) {
            return false;
        }

        Relation other = (Relation) object;
        if (!other.destination.equals(destination))
            return false;

        if (!other.source.equals(source))
            return false;

        if (other.trust != trust)
            return false;

        if (other.type != type)
            return false;

        return true;
    }
}
