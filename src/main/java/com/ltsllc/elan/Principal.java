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

    protected Principal source = null;

    public Principal (String name, Principal source) {
        this.name = name;
        this.source = source;
    }

    public Principal (String name, Principal source, Relation[] relations) {
        this.name = name;
        this.source = source;
        addRelations(relations);
    }

    public Principal getSource() {
        return source;
    }

    public void setSource(Principal source) {
        this.source = source;
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

    public int getLevel(Principal principal) {
        int level = 0;

        for (Principal temp = source; temp != null; temp = temp.getSource()) {
            level++;
        }
        
        return level;
    }

    /**
     * Return the recursive trust of a {@link Principal}.
     *
     * @return If this is a "root node" (it has a null source) then return 1, otherwise return the trust of the
     * {@link Relation} to this node.
     */
    public double trustPath () {
        if (source == null) {
            return 1;
        } else {
            Relation relation = source.relations.get(name);
            double trust = relation.getTrust();
            return trust * source.trustPath();
        }
    }
}
