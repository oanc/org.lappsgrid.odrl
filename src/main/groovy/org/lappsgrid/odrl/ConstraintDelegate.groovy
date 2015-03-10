package org.lappsgrid.odrl

import odrlmodel.Constraint

/**
 * @author Keith Suderman
 */
class ConstraintDelegate {
    private static final OPERATORS = [
            '<': "http://www.w3.org/ns/odrl/2/lt",
            '<=':"http://www.w3.org/ns/odrl/2/lteq",
            '=':"http://www.w3.org/ns/odrl/2/eq",
            '==':"http://www.w3.org/ns/odrl/2/eq",
            '>':"http://www.w3.org/ns/odrl/2/gt",
            '>=':"http://www.w3.org/ns/odrl/2/gteq",
            '!=':"http://www.w3.org/ns/odrl/2/neq",
            'lt': "http://www.w3.org/ns/odrl/2/lt",
            'lteq':"http://www.w3.org/ns/odrl/2/lteq",
            'eq':"http://www.w3.org/ns/odrl/2/eq",
            'gt':"http://www.w3.org/ns/odrl/2/gt",
            'gteq':"http://www.w3.org/ns/odrl/2/gteq",
            'neq':"http://www.w3.org/ns/odrl/2/neq",
            'hasPart':"http://www.w3.org/ns/odrl/2/hasPart",
            'isa':"http://www.w3.org/ns/odrl/2/isA",
            'isA':"http://www.w3.org/ns/odrl/2/isA",
            'isAnyOf':"http://www.w3.org/ns/odrl/2/isAllOf",
            'isAnyOf':"http://www.w3.org/ns/odrl/2/isAnyOf",
            'isAnyOf':"http://www.w3.org/ns/odrl/2/isNoneOf",
            'isAnyOf':"http://www.w3.org/ns/odrl/2/isPartOf",
    ]

    Constraint constraint = new Constraint();

    void operator(String op) {
        if (op.startsWith('http')) {
            constraint.operator = op
        }
        else {
            String uri = OPERATORS[op]
            if (uri) {
                constraint.operator = uri
            }
            else {
                constraint.operator = op
            }
        }

    }

    void rightOperand(String operand) {
        constraint.rightOperand = operand
    }

    void value(String value) {
        constraint.value = value
    }

    void dataType(String type) {
        constraint.dataType = type
    }

    void unit(String unit) {
        constraint.unit = unit
    }
}
