package com.ltsllc.elan;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WebTest {

    @Test
    void buildLeaves() {
        List<Relation> list = new ArrayList<>();

        Principal one = new Principal("one");
        Principal two = new Principal("two");
        Principal three = new Principal("three");

        Relation relation = new Relation(one,two, 0.99, Relation.TrustType.recommendation);
        one.getRelations().put (one.getName(), relation);

        relation = new Relation(one, three, 0.75, Relation.TrustType.direct);
        one.getRelations().put(three.getName(), relation);

        Web web = new Web();
        List<Relation> list2 = web.buildLeaves(one);

        assert (list2.contains(one.getRelations().get(three.getName())));
        assert (one.getRelations().get(two.getName()) == null);
    }

    @Test
    void reportFor() {
    }
}