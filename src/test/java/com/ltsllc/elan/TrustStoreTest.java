package com.ltsllc.elan;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ltsllc.commons.io.TextFile;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TrustStoreTest extends ElanTestCase {

    @Test
    void load() throws Exception {

        File file = new File("whatever");

        Principal principal = new Principal("root", null);

        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setPrettyPrinting();
            gsonBuilder.serializeNulls();
            Gson gson = gsonBuilder.create();

            Principal root = buildNetwork();
            Map<String, GsonPrincipal> map = new HashMap<>();
            root.buildGsonPrincipalMap(map);

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
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setPrettyPrinting();
            gsonBuilder.serializeNulls();
            Gson gson = gsonBuilder.create();

            Principal root = buildNetwork();

            Map<String, GsonPrincipal> map = new HashMap<>();
            root.buildGsonPrincipalMap(map);
            List<GsonPrincipal> list = new ArrayList<GsonPrincipal>(map.values());

            String json = gson.toJson(list);
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(json);
            fileWriter.close();

            TrustStore trustStore = new TrustStore(file2);
            trustStore.setRoot(root);
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