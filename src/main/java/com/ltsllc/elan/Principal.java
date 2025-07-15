package com.ltsllc.elan;

import java.util.*;

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

    protected Map<String, Relation> leaves = new HashMap<>();

    public Principal (String name, Principal source) {
        this.name = name;
        this.source = source;
    }

    public Principal (String name, Principal source, Relation[] relations) {
        this.name = name;
        this.source = source;
        addRelations(relations);
    }

    public Map<String, Relation> getLeaves() {
        return leaves;
    }

    public void setLeaves(Map<String, Relation> leaves) {
        this.leaves = leaves;
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

        leaves = buildLeaves(null);
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

    /**
     * Print a report for a given {@link Principal}.  If the {@link Principal} could not be found, then print
     * "unknown."
     *
     * @param principal - the principal that the caller wants the report for.
     * @return The trust put into that principal.
     */
    public double reportFor (Principal principal) {
        if (principal == null || principal.source == null) {
            return 1;
        }

        reportFor(principal.source);
        Relation relation = principal.source.getRelations().get(principal.name);
        Elan.out.print(" ");
        Elan.out.print(name);
        Elan.out.print(" (");
        Elan.out.print(relation.getTrust() * 100);
        Elan.out.print(") ");

        return relation.getTrust();
    }

    public void reportFor() {
        reportFor(this);
    }

    /**
     * Build a map from principal name to a {@link Relation} with the highest trust.
     *
     * @param map The existing map or null if we are at the top (i.e. root) level.
     * @return A map as defined above.
     */
    public Map<String, Relation> buildLeaves(Map<String, Relation> map) {
        if (map == null) {
            map = new HashMap<>();
        }


        Collection<Relation> col1 = relations.values();
        for (Relation relation : col1) {
            if (relation.getType() == Relation.TrustType.direct) {
                if (null == map.get(relation.getDestination().getName())) {
                    map.put(relation.getDestination().getName(), relation);
                } else if (relation.getTrust() > map.get(relation.getDestination().getName()).getTrust()) {
                    map.put (relation.getDestination().getName(), relation);
                } else {
                    throw new RuntimeException("impossible case");
                }
            } else if (relation.getType() == Relation.TrustType.recommendation) {
                relation.getDestination().buildLeaves(map);
            }
        }
        return map;
    }

    public void reportFor(String name) {
        if (!leaves.containsKey(name)) {
            Elan.out.println("unknown");
        } else {
            Principal principal = leaves.get(name).getDestination();
            principal.reportFor();
        }
    }
}
