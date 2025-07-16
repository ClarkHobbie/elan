package com.ltsllc.elan;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void load() throws IOException {
        if (!file.exists()) {
            throw new IOException("the trustStore file, " + file + ", does not exist");
        } else {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
                String json = new String(fileInputStream.readAllBytes());
                Gson gson = new Gson();
                Class<List> type = List.class;

                // Type listType = new TypeToken<ArrayList<GsonPrincipal>>(){}.getType();
                // ArrayList<GsonPrincipal> myCustomList = gson.fromJson(json, listType);
                GsonPrincipal gsonPrincipal = gson.fromJson(json, GsonPrincipal.class);
                Map<String, Principal> map = gsonPrincipal.buildPrincipalMap(new HashMap<>());
                Principal principal1 = gsonPrincipal.buildRelations(map);
                Object object = ((GsonPrincipal)gsonPrincipal).buildPrincipal(new HashMap<>());
                this.root = (Principal) object;
                fileInputStream.close();
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

        GsonPrincipal gsonPrincipal = root.buildGsonPrincipal();
        String json = gson.toJson(gsonPrincipal);
        fileOutputStream.write(json.getBytes());

        fileOutputStream.close();
    }
}
