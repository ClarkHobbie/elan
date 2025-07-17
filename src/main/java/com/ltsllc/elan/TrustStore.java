package com.ltsllc.elan;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A repository for trust information.
 */
public class TrustStore {
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
        }

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            String json = new String(fileInputStream.readAllBytes());
            Gson gson = new Gson();
            Type type = TypeToken.getParameterized(List.class, GsonPrincipal.class).getType();
            List<GsonPrincipal> list = gson.fromJson(json, type);

            //
             // get a map of names to principals
            //
            Map<String, Principal> map = new HashMap<>();
            for (GsonPrincipal gsonPrincipal : list) {
                Principal prinicpal = gsonPrincipal.toPrincipal();
                if (!map.containsKey(prinicpal.getName())) {
                    map.put (prinicpal.getName(), prinicpal);
                }
            }

            //
             // fixup the source
            //
            Principal root = null;
            for (GsonPrincipal gsonPrincipal : list) {
                Principal principal = map.get(gsonPrincipal.getName());
                Principal source = null;

                if (gsonPrincipal.getSource() != null) {
                    source = map.get(gsonPrincipal.getSource());
                }

                principal.setSource(source);

                if (principal.getSource() == null) {
                    root = principal;
                }
            }

            //
             // fixup the relationships
            //
            for (GsonPrincipal gsonPrincipal : list) {
                Principal principal = map.get(gsonPrincipal.getName());
                for (GsonRelation gsonRelation : gsonPrincipal.getRelations().values()) {
                    Relation relation = gsonRelation.buildRelation(map);
                    principal.addRelation(gsonRelation.getDestination(), relation);
                }
            }

            this.root = root;
        } catch (IOException e) {
            throw new RuntimeException("error with file, " + file, e);
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

        Map<String, GsonPrincipal> map = new HashMap<>();
        root.buildGsonPrincipalMap(map);
        List<GsonPrincipal> gsonPrincipals = new ArrayList<GsonPrincipal>(map.values());
        String json = gson.toJson(gsonPrincipals);
        fileOutputStream.write(json.getBytes());

        fileOutputStream.close();
    }
}
