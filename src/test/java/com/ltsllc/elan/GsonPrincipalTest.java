package com.ltsllc.elan;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GsonPrincipalTest extends ElanTestCase {

    @Test
    void addRelation() {
        Principal root = buildNetwork();

        HashMap<String, GsonPrincipal> map = new HashMap<>();
        GsonPrincipal gsonPrincipal = root.buildGsonPrincipal(map);
        GsonPrincipal three = map.get("three");
        GsonPrincipal twoDotOne = map.get("twoDotOne");

        GsonRelation gsonRelation = new GsonRelation(three.getName(), twoDotOne.getName(), 0.66,
                Relation.TrustType.direct);
        three.addRelation(gsonRelation);

        gsonRelation = three.getRelation("twoDotOne");

        assert (null != gsonRelation);
        assert (gsonRelation.getSource().equalsIgnoreCase("three"));
        assert (gsonRelation.getDestination().equalsIgnoreCase("twoDotOne"));
        assert (gsonRelation.getTrust() == 0.66);
        assert (gsonRelation.getType().equals(Relation.TrustType.direct));
    }

    @Test
    void buildPrincipalMap() {
        Principal root = buildNetwork();
        Map<String, Principal> principalMap = new HashMap<>();
        root.buildPrincipalMap(principalMap);

        Principal one = principalMap.get("one");
        assert (one != null);
        assert (one.getName().equalsIgnoreCase("one"));
    }

    @Test
    void buildRelations() {
        Principal root = buildNetwork();
        Map<String, Principal> principalMap = new HashMap<>();
        root.buildPrincipalMap(principalMap);
        GsonPrincipal gsonPrincipal = root.buildGsonPrincipal(new HashMap<>());
        Principal one = gsonPrincipal.buildRelations(principalMap);

        assert(one != null);
        assert (one.getRelations().get("two") != null);
        assert (one.getRelations().get("three") != null);
    }
}