package org.exigencecorp.structgen.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import javax.tools.Diagnostic.Kind;

import org.apache.commons.lang.StringUtils;
import org.exigencecorp.gen.GClass;
import org.exigencecorp.gen.GMethod;

public class Generator {

    private final ProcessingEnvironment processingEnv;
    private final TypeElement element;
    private final GClass structClass;
    private final List<Property> properties;

    public Generator(ProcessingEnvironment processingEnv, TypeElement element) {
        this.processingEnv = processingEnv;
        this.element = element;
        this.structClass = new GClass(StringUtils.removeEnd(this.element.toString(), "Spec"));
        this.properties = this.getProperties();
    }

    public void generate() {
        this.addFields();
        this.addConstructor();
        this.addEquals();
        this.addHashCode();
        this.saveCode();
    }

    private List<Property> getProperties() {
        List<Property> properties = new ArrayList<Property>();
        for (Element enclosed : this.element.getEnclosedElements()) {
            if (enclosed.getModifiers().contains(Modifier.STATIC) || enclosed.getKind() != ElementKind.METHOD) {
                continue;
            }
            properties.add(new Property(enclosed.getSimpleName().toString(), ((ExecutableElement) enclosed).getReturnType()));
        }
        return properties;
    }

    private void addConstructor() {
        String[] typeAndNames = new String[this.properties.size()];
        for (int i = 0; i < typeAndNames.length; i++) {
            Property p = this.properties.get(i);
            typeAndNames[i] = p.type.toString() + " " + p.name;
        }
        GMethod cstr = this.structClass.getConstructor(typeAndNames);
        for (Property p : this.properties) {
            cstr.body.line("this.{} = {};", p.name, p.name);
        }
    }

    private void addEquals() {
        GMethod equals = this.structClass.getMethod("equals").returnType("boolean").arguments("Object o");
        equals.body.line("if (o == null || this.getClass() != o.getClass()) {");
        equals.body.line("    return false;");
        equals.body.line("}");
        equals.body.line("final {} other = ({}) o;", this.structClass.getSimpleClassName(), this.structClass.getSimpleClassName());
        for (Property p : this.properties) {
            equals.body.line("if (this.{} != other.{} && (this.{} == null || !this.{}.equals(other.{}))) {", p.name, p.name, p.name, p.name, p.name);
            equals.body.line("    return false;");
            equals.body.line("}");
        }
        equals.body.line("return true;");
    }

    private void addHashCode() {
        GMethod hashCode = this.structClass.getMethod("hashCode").returnType("int");
        hashCode.body.line("int hash = 9;");
        for (Property p : this.properties) {
            hashCode.body.line("hash += 97 * hash + (this.{} != null ? this.{}.hashCode() : 0);", p.name, p.name);
        }
        hashCode.body.line("return hash;");
    }

    private void addFields() {
        for (Property p : this.properties) {
            this.structClass.getField(p.name).type(p.type.toString()).setPublic().setFinal();
        }
    }

    private void saveCode() {
        try {
            JavaFileObject jfo = this.processingEnv.getFiler().createSourceFile(this.structClass.getFullClassNameWithoutGeneric(), this.element);
            Writer w = jfo.openWriter();
            w.write(this.structClass.toCode());
            w.close();
        } catch (IOException io) {
            this.processingEnv.getMessager().printMessage(Kind.ERROR, io.getMessage());
        }
    }

}
