package org.exigencecorp.structgen.processor;

import javax.lang.model.type.TypeMirror;

public class Property {
    public String name;
    public TypeMirror type;

    public Property(String name, TypeMirror type) {
        this.name = name;
        this.type = type;
    }
}
