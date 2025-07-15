package com.ltsllc.elan;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.List;

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

    /**
     * Store tbe instance to the file associated with it.
     * <P>
     *     Note that this routine has to be careful of cycles because Gson cannot handle them.
     * </P>
     * @throws IOException
     * @see Gson
     */
    public void store() throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.serializeNulls();
        Gson gson = gsonBuilder.create();

        FileOutputStream fileOutputStream = new FileOutputStream(file);

        List<GsonPrincipal> list = root.buildGsonPrincipal();
        String jason = gson.toJson(list);
        fileOutputStream.write(jason.getBytes());

        fileOutputStream.close();
    }
}
