package com.ltsllc.elan;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class that aids the testing of elan classes
 */
public class ElanTestCase {
    /**
     * Build a network of {@link Principal}s and {@link Relation}s, useful for testing.
     *
     * @return The root node of the network.
     */
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

    /**
     * Build a {@link TrustStore}, complete with a network of {@link Principal}s and {@link Relation}s.
     *
     * @param file The place for the trustStore to live.
     * @return The trustStore.
     * @throws IOException If there is a problem with building the trustStore,
     */
    public TrustStore buildTrustStore(File file) throws IOException {
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
