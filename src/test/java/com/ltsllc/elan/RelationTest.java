package com.ltsllc.elan;

import org.junit.jupiter.api.Test;

class RelationTest {

    @Test
    void buildGsonRelation() {
        Principal one = new Principal("one", null);
        Principal two = new Principal("two", one);
        Principal three = new Principal("three", one);

        Relation relation = new Relation(one, two, 0.99, Relation.TrustType.recommendation);
        one.addRelation("two", relation);
        relation = new Relation(one, three, 0.75, Relation.TrustType.direct);
        one.addRelation("three", relation);

        GsonRelation gsonRelation = relation.buildGsonRelation();

        assert (gsonRelation.getSource().equalsIgnoreCase("one"));
        assert (gsonRelation.getDestination().equalsIgnoreCase("three"));
        assert (gsonRelation.getTrust() == 0.75);
        assert (gsonRelation.getType() == Relation.TrustType.direct);
    }
}