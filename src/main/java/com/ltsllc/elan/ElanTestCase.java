package com.ltsllc.elan;

import com.ltsllc.commons.test.TestCase;

public class ElanTestCase extends TestCase {
    public Principal buildNetwork () {
        Principal one = new Principal("one", null);
        Principal two = new Principal("two", one);
        Principal twoDotOne = new Principal("twoDotOne", two);
        Principal twoDotTwo = new Principal("twoDotTwo", two);
        Principal three = new Principal("three", one);

        Relation relation = new Relation(one, two, 0.99, Relation.TrustType.recommendation);
        one.addRelation("two", relation);
        relation = new Relation(one, three, 0.75, Relation.TrustType.direct);
        one.addRelation("three", relation);
        relation = new Relation(two, twoDotOne, 0.66, Relation.TrustType.direct);
        two.addRelation("twoDotOne", relation);
        relation = new Relation(two, twoDotTwo, 0.99, Relation.TrustType.recommendation);
        two.addRelation("twoDotTwo", relation);

        return one;
    }
}
