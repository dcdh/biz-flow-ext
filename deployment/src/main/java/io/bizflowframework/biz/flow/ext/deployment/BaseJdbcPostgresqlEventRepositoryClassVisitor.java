package io.bizflowframework.biz.flow.ext.deployment;

import io.quarkus.gizmo.Gizmo;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public final class BaseJdbcPostgresqlEventRepositoryClassVisitor extends ClassVisitor {
    public static final String AGGREGATE_ROOT_ID_CLASS_METHOD_NAMING = "aggregateIdClazz";
    private String className;
    private ClassTypeParameter aggregateRootId;

    public BaseJdbcPostgresqlEventRepositoryClassVisitor(final ClassVisitor classVisitor) {
        super(Gizmo.ASM_API_VERSION, classVisitor);
    }

    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        this.className = name;
        final String[] types = signature.substring(signature.lastIndexOf("<") + 1, signature.length() - 2).split(";");
        this.aggregateRootId = new ClassTypeParameter(types[0]);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String descriptor, final String signature, final String[] exceptions) {
        if (AGGREGATE_ROOT_ID_CLASS_METHOD_NAMING.equals(name)) {
            return null;
        } else {
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }

    @Override
    public void visitEnd() {
        {
            // Generate aggregateRootId method
            final MethodVisitor aggregateRootClassMethod = super.visitMethod(
                    Opcodes.ACC_PUBLIC,
                    AGGREGATE_ROOT_ID_CLASS_METHOD_NAMING,
                    aggregateRootId.descriptor(),
                    aggregateRootId.signature(),
                    null
            );
            aggregateRootClassMethod.visitCode();
            final Label label0 = new Label();
            aggregateRootClassMethod.visitLabel(label0);
            aggregateRootClassMethod.visitLdcInsn(aggregateRootId.asmType());
            aggregateRootClassMethod.visitInsn(Opcodes.ARETURN);
            final Label label1 = new Label();
            aggregateRootClassMethod.visitLabel(label1);
            aggregateRootClassMethod.visitLocalVariable("this", String.format("L%s;", className), null, label0, label1, 0);
            aggregateRootClassMethod.visitMaxs(1, 1);
            aggregateRootClassMethod.visitEnd();
        }
        super.visitEnd();
    }
}
