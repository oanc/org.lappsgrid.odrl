package org.lappsgrid.odrl

import odrlmodel.Prohibition

/**
 * @author Keith Suderman
 */
class ProhibitionDelegate extends RuleDelegate {

    public ProhibitionDelegate() {
        super(new Prohibition())
    }

    public Prohibition getProhibition() {
        return (Prohibition) rule
    }
}
