package com.ltsllc.elan;

import com.ltsllc.commons.io.TextFile;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class TrustStoreTest {

    @Test
    void load() {

        File file = new File("whatever");

        String[] text =  {
                "hello world"
        };

        TextFile textFile = new TextFile(file);
        textFile.setText(text);
        textFile.store();

        try {
            TrustStore trustStore = new TrustStore(file);
            trustStore.load();

            assert (trustStore.getText()[0].equalsIgnoreCase("hello world"));
        } finally {
            if (file.exists()) {
                file.delete();
            }
        }
    }
}