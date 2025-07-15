package com.ltsllc.elan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A principal without cycles so it is {@link com.google.gson.Gson} safe.
 *
 */
public class GsonPrincipal {
    protected String name;
    protected String source;
    protected Map<String, GsonRelation> relations = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Map<String, GsonRelation> getRelations() {
        return relations;
    }

    public void setRelations(Map<String, GsonRelation> relations) {
        this.relations = relations;
    }

    public GsonPrincipal(String name, String source) {
        this.name = name;
        this.source = source;
    }

    public void addRelation(GsonRelation gsonRelation) {
        relations.put(gsonRelation.getDestination(), gsonRelation);
    }
}
