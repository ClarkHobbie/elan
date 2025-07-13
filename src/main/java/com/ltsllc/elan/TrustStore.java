package com.ltsllc.elan;

import com.ltsllc.commons.io.TextFile;

import java.io.File;

/**
 * A repository for trust information.
 */
public class TrustStore {
    protected File file = null;
    protected String[] text;

    public String[] getText() {
        return text;
    }

    public void setText(String[] text) {
        this.text = text;
    }

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
        TextFile textFile = new TextFile(file);

        textFile.load();
        setText(textFile.getTextAsArray());
    }
}
