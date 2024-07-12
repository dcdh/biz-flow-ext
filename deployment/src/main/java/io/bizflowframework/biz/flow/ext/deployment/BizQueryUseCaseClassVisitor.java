package io.bizflowframework.biz.flow.ext.deployment;

import io.quarkus.gizmo.Gizmo;
import org.objectweb.asm.ClassVisitor;

public final class BizQueryUseCaseClassVisitor extends ClassVisitor {

    public BizQueryUseCaseClassVisitor(final ClassVisitor classVisitor) {
        super(Gizmo.ASM_API_VERSION, classVisitor);
    }

    @Override
    public void visitEnd() {
        new ApplyTransactionAnnotation().apply(this);
        super.visitEnd();
    }
}
