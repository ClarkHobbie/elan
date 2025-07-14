package com.ltsllc.elan;

import com.google.gson.Gson;
import com.ltsllc.commons.io.TextFile;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TrustStoreTest {

    @Test
    void load() throws Exception {

        File file = new File("whatever");

        Principal principal = new Principal("root", null);

        try {
            Gson gson = new Gson();
            String json = gson.toJson(principal);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();

            TrustStore trustStore = new TrustStore(file);
            trustStore.load();

            assert (trustStore.getRoot().getName().equalsIgnoreCase("root"));
        } catch (IOException e) {
            throw new RuntimeException("error with file, " + file, e);
        } finally {
            if (file.exists()) {
                file.delete();
            }
        }
    }
}