package io.bizflowframework.biz.flow.ext.deployment;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;

import java.util.function.Function;

public final class ApplyTransactionAnnotation implements Function<ClassVisitor, Void> {

    private static final String TRANSACTIONAL_DESCRIPTOR = "Ljavax/transaction/Transactional;";

    @Override
    public Void apply(final ClassVisitor classVisitor) {
        final AnnotationVisitor annotationVisitor = classVisitor.visitAnnotation(TRANSACTIONAL_DESCRIPTOR, true);
        assert annotationVisitor != null;
        annotationVisitor.visitEnd();
        return null;
    }
}
