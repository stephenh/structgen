package org.exigencecorp.structgen.processor;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import org.exigencecorp.structgen.Struct;

@SupportedAnnotationTypes( { "org.exigencecorp.structgen.Struct" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class Processor extends AbstractProcessor {

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Struct.class)) {
            if (element instanceof TypeElement && element.getSimpleName().toString().endsWith("Spec")) {
                new Generator(this.processingEnv, (TypeElement) element).generate();
            } else {
                this.processingEnv.getMessager().printMessage(Kind.WARNING, "Unhandled element " + element);
            }
        }
        return true;
    }

}
