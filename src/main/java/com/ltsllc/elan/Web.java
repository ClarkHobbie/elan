package com.ltsllc.elan;

import java.util.ArrayList;
import java.util.List;

/**
 * A web of trust.
 */
public class Web {
    protected List<Relation> leaves = new ArrayList<>();

    public List<Relation> getLeaves() {
        return leaves;
    }

    public void setLeaves(List<Relation> leaves) {
        this.leaves = leaves;
    }

    public List<Relation> buildLeaves (Principal root) {
        List<Relation> allDirectRelations = new ArrayList<>();
        
        for (Relation relation : root.getRelations().values()) {
            if (relation.getType() == Relation.TrustType.direct) {
                allDirectRelations.add(relation);
            }
            allDirectRelations.addAll(buildLeaves(relation.getDestination()));
        }
        
        return allDirectRelations;
    }
    
    public Relation reportFor (Principal principal) {
        List<Relation> list = new ArrayList<>();
        for (Relation relation : getLeaves()) {
            if (relation.getDestination().getName().equalsIgnoreCase(principal.getName())) {
                list.add(relation);
            }
        }
        
        Relation temp = null;
        Double highestTrust = new Double(0);
        for (Relation relation : list) {
            if (relation.getTrust() > highestTrust.doubleValue()) {
                highestTrust = new Double(relation.getTrust());
                temp = relation;
            }
        }

        return temp;
    }

}
