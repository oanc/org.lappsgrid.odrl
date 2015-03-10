package org.lappsgrid.odrl

import odrlmodel.Permission
import odrlmodel.Policy
import odrlmodel.Prohibition

/**
 * @author Keith Suderman
 */
class PolicyDelegate {
    private static final Map<String,Integer> TYPES = [
            set : Policy.POLICY_SET,
            agreement : Policy.POLICY_AGREEMENT,
            cc : Policy.POLICY_CC,
            offer : Policy.POLICY_OFFER,
            request : Policy.POLICY_REQUEST,
            ticket : Policy.POLICY_TICKET
    ]

    Policy policy

    def propertyMissing(String name, value) {
        throw new MissingPropertyException("Unknown property ${name}")
    }

    def propertyMissing(String name) {
        throw new MissingPropertyException("Unknown property ${name}")
    }

    public PolicyDelegate(String name) {
        this.policy = new Policy(name)
    }

    void type(int i)
    {
       policy.setType(i);
    }

    void type(String value) {
        policy.setType(TYPES[value])
    }

    void permission(Permission permission) {
        policy.addRule(permission)
    }

    void permission(Closure cl) {
        PermissionsDelegate delegate = new PermissionsDelegate()
        cl.delegate = delegate
        cl.resolveStrategy = Closure.DELEGATE_FIRST
        cl()
        policy.addRule(delegate.permission)
    }

    void prohibition(Prohibition prohibition) {
        policy.addRule(prohibition)
    }

    void prohibition(Closure cl) {
        ProhibitionDelegate delegate = new ProhibitionDelegate()
        cl.delegate = delegate
        cl.resolveStrategy = Closure.DELEGATE_FIRST
        cl()
        policy.addRule(delegate.prohibition)
    }
}
