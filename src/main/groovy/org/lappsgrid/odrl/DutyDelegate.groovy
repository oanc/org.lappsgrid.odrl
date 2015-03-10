package org.lappsgrid.odrl

import odrlmodel.Duty

/**
 * @author Keith Suderman
 */
class DutyDelegate extends RuleDelegate {
    public DutyDelegate() {
        super(new Duty())
    }

    Duty getDuty() {
        return (Duty) rule
    }
}
