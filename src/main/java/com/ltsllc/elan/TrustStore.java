package com.ltsllc.elan;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * A repository for trust information.
 */
public class TrustStore{
    protected File file = null;
    protected String[] text;
    protected Principal root;

    public Principal getRoot() {
        return root;
    }

    public void setRoot(Principal root) {
        this.root = root;
    }

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
        if (file.exists()) {
            FileReader fileReader = null;
            try {
                fileReader = new FileReader(file);
                Gson gson = new Gson();
                root = gson.fromJson(fileReader, Principal.class);
                fileReader.close();
            } catch (IOException e) {
                throw new RuntimeException("error with file, " + file, e);
            }
        }
    }
}
