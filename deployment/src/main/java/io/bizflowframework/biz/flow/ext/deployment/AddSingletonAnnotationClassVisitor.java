package io.bizflowframework.biz.flow.ext.deployment;

import io.quarkus.gizmo.Gizmo;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;

public final class AddSingletonAnnotationClassVisitor extends ClassVisitor {

    public AddSingletonAnnotationClassVisitor(final ClassVisitor classVisitor) {
        super(Gizmo.ASM_API_VERSION, classVisitor);
System.out.print("ICI DAMIEN youpii !!!");
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
System.out.print("ICI DAMIEN bah bravo !!!");
        // Add the @Singleton annotation to the class
        final AnnotationVisitor av = super.visitAnnotation("Ljavax/inject/Singleton;", true);
        av.visitEnd();
    }
}
