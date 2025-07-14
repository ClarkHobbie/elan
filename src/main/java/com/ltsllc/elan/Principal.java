package com.ltsllc.elan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A person you might trust.
 */
public class Principal extends Reportable{
    /**
     * The name of the person.
     */
    protected String name;

    /**
     * The {@link Relation}s to other {@link Principal}s.
     */
    protected Map<String, Relation> relations = new HashMap<>();

    public Principal (String name) {
        this.name = name;
    }

    public Principal (String name, Relation[] relations) {
        this.name = name;
        addRelations(relations);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Relation> getRelations() {
        return relations;
    }

    public void setRelations(Map<String, Relation> relations) {
        this.relations = relations;
    }

    public void addRelation (String name, Relation relation) {
        relations.put(name, relation);
    }


    public boolean hasSameName(String string) {
        return name.equalsIgnoreCase(string);
    }

    public void report (int indent) {
        printIndent(indent);
        Elan.out.print(name);
        Elan.out.println(" has");
        for (String string : relations.keySet()) {
            Relation relation = relations.get(string);
            relation.report(indent + 4);
        }
    }

    public void addRelations (Relation[] relations) {
        for (Relation relation : relations) {
            addRelation(relation.getSource().getName(), relation);
        }
    }

}
