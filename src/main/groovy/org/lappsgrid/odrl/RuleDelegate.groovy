package org.lappsgrid.odrl

import odrlmodel.Action
import odrlmodel.Constraint
import odrlmodel.Party
import odrlmodel.Rule

/**
 * @author Keith Suderman
 */
class RuleDelegate {
    Rule rule

    public RuleDelegate(Rule rule) {
        this.rule = rule
    }

    void target(String uri) {
        rule.target = uri
    }

    void actions(Action... actions) {
        rule.actions = actions
    }

    void actions(String... actions) {
        rule.actions = actions.collect { new Action(it) }
    }

    void constraint(Closure cl) {
        ConstraintDelegate delegate = new ConstraintDelegate()
        cl.delegate = delegate
        cl.resolveStrategy = Closure.DELEGATE_FIRST
        cl()
        rule.constraints << delegate.constraint
    }

    void constraint(Constraint constraint) {
        rule.constraints << constraint
    }

    void assignee(Party party) {
        rule.assignee = party
    }

    void assignee(String name) {
        rule.assignee = new Party(name)
    }

    void assigner(Party party) {
        rule.assigner = party
    }

    void assigner(String name) {
        rule.assigner = new Party(name)
    }


}
