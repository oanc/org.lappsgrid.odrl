package org.lappsgrid.odrl

import odrlmodel.Action
import odrlmodel.Constraint
import odrlmodel.Duty
import odrlmodel.Party
import odrlmodel.Permission
import odrlmodel.Rule

/**
 * @author Keith Suderman
 */
class PermissionsDelegate extends RuleDelegate {
//    Permission permission = new Permission()

    public PermissionsDelegate() {
        super(new Permission())
    }

    public Permission getPermission() {
        return (Permission) rule
    }

    void duty(Duty duty) {
        ((Permission)rule).duty = duty
    }

    void duty(String uri) {
        ((Permission)rule).duty = new Duty(uri)
    }

    void duties(Duty... duties) {
        ((Permission)rule).duties = duties
    }

    void duties(String... uri) {
        ((Permission)rule).duties = uri.collect { new Duty(it) }
    }

    void constraint(Constraint constraint) {
        this.permission.constraints << constraint
    }

    void constraint(Closure cl) {
        ConstraintDelegate delegate = new ConstraintDelegate()
        cl.delegate = delegate
        cl.resolveStrategy = Closure.DELEGATE_FIRST
        cl()
        this.permission.constraints.add delegate.constraint
    }

//    void target(String uri) {
//        permission.target = uri
//    }
//
//    void actions(Action... actions) {
//        permission.actions = actions
//    }
//
//    void actions(String... actions) {
//        permission.actions = actions.collect { new Action(it) }
//    }
//
//    void assignee(Party party) {
//        permission.assignee = party
//    }
//
//    void assignee(String name) {
//        permission.assignee = new Party(name)
//    }
}
