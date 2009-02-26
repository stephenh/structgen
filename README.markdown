
Todo
====

* Ordering--Eclipse doesn't return methods in source order, but we need determinism. Need to at least sort by name. Perhaps add an annotation to give the order? Seems ugly.

* Flag for public field vs. getter method, both in the annotation (e.g. `@Struct(getter=true)` or project-level annotation key/value

* Is mutation allowed, e.g. setters? Probably not--if you add setters, equals/hashCode results will change. Which means you shouldn't be using structs anyway.

* Inheritance--either have structs extend each other or duplicate the properties in each

