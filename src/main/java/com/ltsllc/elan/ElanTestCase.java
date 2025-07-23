package com.ltsllc.elan;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ltsllc.commons.test.TestCase;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public TrustStore buildTrustStore(File file) throws Exception {
        Principal root = buildNetwork();
        Map<String, GsonPrincipal> gsonPrincipalMap = new HashMap<>();
        root.buildGsonPrincipalMap(gsonPrincipalMap);
        List<GsonPrincipal> list = new ArrayList<>(gsonPrincipalMap.values());

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.serializeNulls();
        Gson gson = gsonBuilder.create();
        String json = gson.toJson(list);

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(json.getBytes());
        fileOutputStream.close();

        TrustStore trustStore = new TrustStore(file);
        trustStore.load();

        return trustStore;
    }
}
