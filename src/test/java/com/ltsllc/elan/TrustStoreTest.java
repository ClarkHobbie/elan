package com.ltsllc.elan;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ltsllc.commons.io.TextFile;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TrustStoreTest {

    @Test
    void load() throws Exception {

        File file = new File("whatever");

        Principal principal = new Principal("root", null);

        try {
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

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setPrettyPrinting();
            gsonBuilder.serializeNulls();
            Gson gson = gsonBuilder.create();

            Map<String, GsonPrincipal> map = new HashMap<>();
            one.buildGsonPrincipalMap(map);
            List<GsonPrincipal> list = new ArrayList<GsonPrincipal>(map.values());
            String json = gson.toJson(list);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();

            TrustStore trustStore = new TrustStore(file);
            trustStore.load();

            assert (trustStore.getRoot().getName().equalsIgnoreCase("one"));
        } catch (IOException e) {
            throw new RuntimeException("error with file, " + file, e);
        } finally {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    @Test
    void store() throws Exception {
        File file = new File("whatever");
        File file2 = new File("test");

        try {
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

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setPrettyPrinting();
            gsonBuilder.serializeNulls();
            Gson gson = gsonBuilder.create();

            FileWriter fileWriter = new FileWriter(file);
            Map<String, GsonPrincipal> map = new HashMap<>();
            one.buildGsonPrincipalMap(map);
            List<GsonPrincipal> list = new ArrayList<GsonPrincipal>(map.values());
            String json = gson.toJson(list);
            fileWriter.write(json);
            fileWriter.close();

            TrustStore trustStore = new TrustStore(file2);
            trustStore.setRoot(one);
            trustStore.store();

            TextFile textFile1 = new TextFile(file);
            textFile1.load();
            TextFile textFile2 = new TextFile(file2);
            textFile2.load();

            assert (textFile1.equals(textFile2));
        } finally {
            if (file.exists()) {
                file.delete();
            }

            if (file2.exists()) {
                file2.delete();
            }
        }
    }
}