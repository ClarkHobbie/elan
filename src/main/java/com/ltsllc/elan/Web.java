package com.ltsllc.elan;

import java.util.ArrayList;
import java.util.List;

/**
 * A web of trust.
 */
public class Web extends Reportable {
    protected List<Relation> leaves = new ArrayList<>();
    protected Principal root = null;

    public Web(Principal root) {
        this.root = root;
        leaves = buildLeaves(this.root);
    }

    public Principal getRoot() {
        return root;
    }

    public void setRoot(Principal root) {
        this.root = root;
    }

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

    public void printReportFor (Principal principal) {
        Relation relation = reportFor(principal);

        if (relation == null) {
            Elan.out.println("0");
        } else {
            int level = relation.getSource().getLevel(relation.getDestination());
            int indent = level * 4;

            printPath(indent, relation.getDestination());
        }
    }

    public void printPath (int indent, Principal principal) {
        if (principal != null) {
            printIndent(indent);
            Elan.out.print(principal.getName());
            Elan.out.print(" (");
            Elan.out.print(principal.trustPath() * 100);
            Elan.out.println(")");
        }
    }


}
