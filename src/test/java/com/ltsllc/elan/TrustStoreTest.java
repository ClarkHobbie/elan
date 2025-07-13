package com.ltsllc.elan;

import com.ltsllc.commons.io.TextFile;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class TrustStoreTest {

    @Test
    void load() {

        File file = new File("whatever");

        try {
            TextFile textFile = new TextFile();
        } finally {
            if (file.exists()) {
                file.delete();
            }
        }
    }
}