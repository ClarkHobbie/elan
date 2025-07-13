package com.ltsllc.elan;

import java.util.ArrayList;
import java.util.List;

public class Principal {
    protected String name;
    protected List<Relation> relations = new ArrayList<>();

    public Principal (String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Relation> getRelations() {
        return relations;
    }

    public void setRelations(List<Relation> relations) {
        this.relations = relations;
    }

    public boolean hasSameName(String string) {
        return name.equalsIgnoreCase(string);
    }

}
