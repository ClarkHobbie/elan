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

        double trust1 = getTrust(relation);
        Principal destination = relation.getDestination();
        double trust2 = destination.getTrust();
        if (trust1 > trust2) {
            relation.getDestination().setSource(relation.getSource());
        }
        // leaves = buildLeaves(new HashMap<>());
    }

    /**
     * Get the trust product for the destination, using the provided relation.
     *
     * @param relation The relation to use, when calculating the trust,
     * @return The level of trust for the destination.
     */
    public double getTrust(Relation relation) {
        return relation.getTrust() * relation.getTrust();
    }


    /**
     * Return the level of trust a principal enjoys.
     * @return The level of trust a principal has, at least from the root's perspective.
     */
    public double getTrust() {
        if (source == null) {
            return 1;
        } else {
            Relation relation = source.relations.get(name);

            if (relation == null) {
                Elan.err.println("name = " + name);
                Elan.err.println("source is " + ((source == null) ? "not null" : "null"));
                Exception e = new Exception();
                e.printStackTrace();
            }
            return relation.getTrust() * source.getTrust();
        }
    }

    public boolean hasSameName(String string) {
        return name.equalsIgnoreCase(string);
    }

    /**
     * Build a {@link Principal} without cycles to be safe for {@link com.google.gson.Gson}.
     * @return The principal as described above.
     */
    public GsonPrincipal buildGsonPrincipal () {
        List<GsonPrincipal> list = new ArrayList<>();

        String sourceName = (source == null) ? null : source.getName();

        IdentityHashMap<Principal, Principal> identityHashMap = new IdentityHashMap();
        identityHashMap.put(this, this);

        for (Relation relation : relations.values()) {
            if (identityHashMap.containsKey(relation.getDestination())) {
                // ignore it
            } else {
                identityHashMap.put(relation.getDestination(), relation.getDestination());
            }
        }

        GsonPrincipal gsonPrincipal = new GsonPrincipal(name, sourceName);
        for (Relation relation : relations.values()) {
            GsonRelation gsonRelation = relation.buildGsonRelation();
            gsonPrincipal.addRelation(gsonRelation);
        }
        return gsonPrincipal;
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
     * @return The trust put into that principal.
     */
    public double report() {
        if (this == null || source == null) {
            Elan.out.print(name);
            return 1;
        }

        source.report();

        Relation relation = source.getRelations().get(name);
        Elan.out.print(" --> (");
        Elan.out.print(relation.getTrust() * relation.getSource().getTrust() * 100);
        Elan.out.print("%) ");
        Elan.out.print(" ");
        Elan.out.print(name);

        return relation.getTrust();
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

        for (Relation relation : relations.values()) {
            if (relation.getType() == Relation.TrustType.direct) {
                if (null == map.get(relation.getDestination().getName())) {
                    map.put(relation.getDestination().getName(), relation);
                } else if (relation.getTrust() > map.get(relation.getDestination().getName()).getTrust()) {
                    map.put (relation.getDestination().getName(), relation);
                } else {
                    throw new RuntimeException("impossible case");
                }
            } else if (relation.getType() == Relation.TrustType.recommendation) {
                // relation.getDestination().buildLeaves(map);
            }
        }
        return map;
    }

    public Principal findPrincipal(String principalName) {
        for (Relation relation : relations.values()) {
            if (relation.getDestination().getName().equalsIgnoreCase(principalName)) {
                return relation.getDestination();
            }
        }
        return null;
    }

    public boolean equals (Object object) {
        if (object == this)
            return true;

        if (null == object || !(object instanceof Principal))
            return false;

        Principal other = (Principal) object;
        if ((source == null) && (other.source != null))
            return false;

        if (relations.size() != other.relations.size())
            return false;

        for (Relation relation : relations.values()) {
            if (!relation.equals(other.relations.get(relation.getDestination().getName()))) {
                return false;
            }
        }

        return true;
    }

    public void buildGsonPrincipalMap(Map<String, GsonPrincipal> map) {
        String source = null;

        if (null != this.source) {
            source = this.source.getName();
        }

        GsonPrincipal gsonPrincipal = new GsonPrincipal(name, source);
        if (!map.containsKey(gsonPrincipal.getName())) {
            map.put(gsonPrincipal.getName(), gsonPrincipal);
        }
        for (Relation relation : relations.values()) {
            GsonRelation gsonRelation = new GsonRelation(name, relation.getDestination().getName(), relation.getTrust(),
                    relation.getType());
            relation.getDestination().buildGsonPrincipalMap(map);
            gsonPrincipal.addRelation(gsonRelation);
            GsonPrincipal temp = new GsonPrincipal(relation.getDestination().getName(), relation.getSource().getName());
            if (!map.containsKey(relation.getDestination().getName())) {
                map.put(relation.getDestination().getName(), temp);
            }
        }
    }

    public GsonPrincipal toGsonPrincipal() {
        String source = null;
        if (this.source != null) {
            source = this.source.getName();
        }
        return new GsonPrincipal(name, source);
    }

    /**
     * A method that builds a map from principal names to principals.
     *
     * @param map The current map.
     */
    public void buildPrincipalMap(Map<String, Principal> map) {
        if (!map.containsKey(name)) {
            map.put (name, this);
        }

        List<Relation> relationList = new ArrayList<>(relations.values());
        for (Relation relation : relationList) {
            relation.getDestination().buildPrincipalMap(map);
        }
    }

    public void show() {
        Elan.out.print(name);
        Elan.out.print(" ");

        boolean first = true;
        for (Relation relation : relations.values()) {
            if (!first) {
                Elan.out.println();
                printIndent(4);
            } else {
                Elan.out.print(" --> (");
                Elan.out.print(relation.getTrust() * 100);
                Elan.out.print(") ");
                first = false;
            }
            relation.getDestination().show();
        }
    }

    public void show(int indent) {
        printIndent(indent);
        Elan.out.print(name);
        boolean first = true;

        for (Relation relation : relations.values()) {
            if (!first) {
                printIndent(indent);
            }
            first = false;

            relation.getDestination().show(4 + indent);
        }
    }

    public GsonPrincipal buildGsonPrincipal(HashMap<String, GsonPrincipal> map) {
        String sourceName = (source == null) ? null : source.getName();
        GsonPrincipal gsonSource = null;
        if (sourceName != null) {
            if (map.containsKey(sourceName)) {
                gsonSource = map.get(sourceName);
            }
        }

        GsonPrincipal gsonPrincipal = new GsonPrincipal(name, sourceName);

        for (Relation relation : relations.values()) {
            GsonRelation gsonRelation = relation.buildGsonRelation();
            String destinationName = relation.getDestination().getName();
            if (!map.containsKey(destinationName)) {
                map.put (destinationName, relation.getDestination().buildGsonPrincipal(map));
            }
            gsonPrincipal.addRelation(gsonRelation);
        }

        return gsonPrincipal;
    }

    /**
     * Print the path to the root and return the trust that the node enjoys.
     * <P>
     *      Note that the root is considered to be a {@link Principal} with a null source.
     * </P>
     * @param trust The level that the node is trusted, relative to the root.
     * @return The level of trust that the node has, relative to the root.
     */
    public double printReport (double trust) {
        double relationTrust = 1.0;

        if (source == null) {
            Elan.out.print(name);
        } else {
            for (Relation relation : source.relations.values()) {
                if (relation.getDestination().equals(this)) {
                    relation.getSource().printReport(1.0);
                    Elan.out.print(" --> (");
                    Elan.out.print(trust * relation.getTrust() * 100);
                    relationTrust = relation.getTrust();
                    Elan.out.print("%) ");
                    Elan.out.print(name);
                    break;
                }
            }
        }

        return trust * relationTrust;
    }

    public void removePrincipal(Principal subject) {
        List<Relation> list = new ArrayList<>(relations.values());
        for (Relation relation : list) {
            if (relation.getDestination().name.equalsIgnoreCase(subject.name)) {
                relations.remove(subject.name);
            } else {
                relation.getDestination().removePrincipal(subject);
            }
        }
    }

    public void removeRelation (Relation relation) {
        relations.remove(relation.getDestination().getName());
    }

    public void buildGsonPrincipalList(List<GsonPrincipal> gsonPrincipals, Map<String, GsonPrincipal> gsonPrincipalMap) {
        if (!gsonPrincipalMap.containsKey(name)) {
            GsonPrincipal gsonPrincipal = this.buildGsonPrincipal();
            gsonPrincipals.add(gsonPrincipal);
            gsonPrincipalMap.put(name, gsonPrincipal);

            for (Relation relation : relations.values()) {
                relation.getDestination().buildGsonPrincipalList(gsonPrincipals, gsonPrincipalMap);
            }
        }
    }
}
