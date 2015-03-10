# org.lappsgrid.odrl
LAPPS Open Digital Rights Language

[ODRL](http://www.w3.org/ns/odrl/2/) is an open international specification for
Policy Language expressions.  ODRL is typically expressed in some form of RDF (XML,
Turtle, N-Quads, JSON-LD, etc.)

The LAPPS ODRL Language (LOL) is a Groovy DSL that can use used to generate ODRL RDF
in any format from a simple and expressive DSL.  Under the hood LOL uses the
[ODRLAPI](https://github.com/oeg-upm/odrlapi) for the data model (i.e. the Java objects)
and [Apache Jena](https://jena.apache.org) for serialization.

## Policy Types

The following policy types are currently supported.

1. **Agreement** formal contracts stipulating all the terms of usage and all the
parties involved.
1. **Offer** proposed terms of usage from a Asset owner.
1. **Request** proposed terms of usage made to an Asset owner.
1. **Set** a set of entities from the complete model. Set policies are typically refined
by other systems or profiles that process the information at a later time. No
priviledges are granted to any party.
1. **Ticket** policy expression that stipulates the terms of usage and is redeemable by
any Party who currently

## Structure ##

The general format of a LOL policy file is:

```groovy
    <policy-type>("<id>") {
        permission {
            target <URL>
            assigner <URL>
            assignee <URL>
            actions <URL>, ...
            constraint { ... }
            duty { ... }
        }
        prohibition {
            // Same as permission, but no duties.
        }
    }
```

### Example: Offer Policy

Sony is offering permission to download a song for 0.50USD. Distribution is not allowed.

```groovy
    offer("http://example.com/policy/1") {
        permission {
            assigner "http://www.sony.com"
            target "http://example.com/media/songs/blue_suede_shoes"
            actions "http://www.w3.org/ns/odrl/2/download", "http://www.w3.org/ns/odrl/2/play"
            duty {
                actions "http://www.w3.org/ns/odrl/2/pay"
                target "http://example.com/currency/usd/0.50"
            }
        }
        prohibition {
            target "http://example.com/media/songs/blue_suede_shoes"
            actions "http://www.w3.org/ns/odrl/2/distribute"
        }
    }
```

### Example: Request and Agreement

Rasheed request permission to play the game Titanfall from Microsoft

```groovy
request("http://example.com/policy/2") {
    permission {
        target "http://games/micrsoft.com/titanfall"
        actions "http://www.w3.org/ns/odrl/2/play"
    }
}
```
Microsoft agrees, but grants permission until the end of 2015 only,

```groovy
agreement("http://example.com/policy/3") {
    permission [
        target "http://games/micrsoft.com/titanfall"
        actions "http://www.w3.org/ns/odrl/2/play"
        assigner "http://microsoft.com"
        assignee "http://facebook.com/rasheed"
        constraint {
            operator "lt"
            rightOperand "http://www.w3.org/ns/odrl/2/dateTime"
            value "2015-12-31"
        }
        duty {
            action "http://www.w3.org/ns/odrl/2/pay"
            target "http://example.com/currency/usd/19.99"
        }
    }
}
```

## Programming Language Features

LOL is not simply an alternative RDF syntax, LOL files are full blown Groovy scripts
capable of using the suite of Groovy language constructs.

### Variables

Groovy is a dynamically typed language so variables may be introduced simply by using
them.

```groovy

// A local scoped variable.  Only visible in the odrl file that defined it.
def i = o

// If this is the first use of j then this declares a global variable.  Global
// variables are visible in files that include the file that declares j.
j = 0

// Types aren't required, but are useful for documenting the code.
String odrl = "http://www.w3.org/ns/odrl/2"
String play = "${odrl}/play"
String copy = "${odrl}/copy"

set("http://example.com/policy/4") {
   actions play, copy
}
```

### Collections

The full suite of Java collections can be used as well as the Groovy extensions
to the Java collections.  The most commonly used collection types (List and Map)
have simplified syntax in Groovy.

```groovy
// Creates an empty ArrayList
def list = []
// Create an empty HashMap
def map = [:]
```

Groovy also provides a simplified for Map access using the *dot notation* where
the identifier after the dot is used as the map key.
```groovy
def Actions = [:]
Actions.play = "http://www.w3.org/ns/odrl/2/play"
Actions.copy = "http://www.w3.org/ns/odrl/2/copy"

// Is the same as
Map<String,String> Actions = new HashMap<String,String>()
Actions.put("play", "http://www.w3.org/ns/odrl/2/play")
Actions.put("copy", "http://www.w3.org/ns/odrl/2/copy")
```

## Command Line Usage

The project uses Maven to build a runnable JAR file that includes all external
dependencies; Groovy, Jena, etc.
```bash
> java -jar odrl-x.y.z.jar -l [rdf|jsonld|ttl|n3] <input_file>
```

The output types are RDF/XML, JSON-LD, Turtle and N3.