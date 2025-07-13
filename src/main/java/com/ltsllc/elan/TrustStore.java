package com.ltsllc.elan;

import java.io.File;

/**
 * A repository for trust information.
 */
public class TrustStore {
    protected File file = null;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public TrustStore (String fileName) {
        file = new File(fileName);
    }

    public TrustStore (File file) {
        this.file = file;
    }

    public void load() {

    }
}
