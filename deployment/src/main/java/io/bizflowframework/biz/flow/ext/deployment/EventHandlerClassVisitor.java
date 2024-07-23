package io.bizflowframework.biz.flow.ext.deployment;

import io.quarkus.gizmo.Gizmo;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;

public final class EventHandlerClassVisitor extends ClassVisitor {
    private static final String AGGREGATE_ROOT_CLASS_METHOD_NAMING = "aggregateRootClass";
    private static final String AGGREGATE_ROOT_EVENT_PAYLOAD_CLASS_METHOD_NAMING = "aggregateRootEventPayloadClass";
    private static final List<String> METHODS_TO_OVERRIDE = List.of(AGGREGATE_ROOT_CLASS_METHOD_NAMING, AGGREGATE_ROOT_EVENT_PAYLOAD_CLASS_METHOD_NAMING);

    private String className;
    private ClassTypeParameter aggregateRoot;
    private ClassTypeParameter aggregateRootEventPayload;

    public EventHandlerClassVisitor(final ClassVisitor classVisitor) {
        super(Gizmo.ASM_API_VERSION, classVisitor);
    }

    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        this.className = name;
        final String[] types = signature.substring(signature.lastIndexOf("<") + 1, signature.length() - 2).split(";");
        this.aggregateRoot = new ClassTypeParameter(types[1]);
        this.aggregateRootEventPayload = new ClassTypeParameter(types[2]);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String descriptor, final String signature, final String[] exceptions) {
        if (METHODS_TO_OVERRIDE.contains(name)) {
            return null;
        } else {
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }

    @Override
    public void visitEnd() {
        {
            // Generate aggregateRootClass method
            final MethodVisitor aggregateRootClassMethod = super.visitMethod(
                    Opcodes.ACC_PUBLIC,
                    AGGREGATE_ROOT_CLASS_METHOD_NAMING,
                    aggregateRoot.descriptor(),
                    aggregateRoot.signature(),
                    null
            );
            aggregateRootClassMethod.visitCode();
            final Label label0 = new Label();
            aggregateRootClassMethod.visitLabel(label0);
            aggregateRootClassMethod.visitLdcInsn(aggregateRoot.asmType());
            aggregateRootClassMethod.visitInsn(Opcodes.ARETURN);
            final Label label1 = new Label();
            aggregateRootClassMethod.visitLabel(label1);
            aggregateRootClassMethod.visitLocalVariable("this", String.format("L%s;", className), null, label0, label1, 0);
            aggregateRootClassMethod.visitMaxs(1, 1);
            aggregateRootClassMethod.visitEnd();
        }
        {
            // Generate aggregateRootEventPayloadClass method
            final MethodVisitor aggregateRootEventPayloadClassMethod = super.visitMethod(
                    Opcodes.ACC_PUBLIC,
                    AGGREGATE_ROOT_EVENT_PAYLOAD_CLASS_METHOD_NAMING,
                    aggregateRootEventPayload.descriptor(),
                    aggregateRootEventPayload.signature(),
                    null
            );
            aggregateRootEventPayloadClassMethod.visitCode();
            final Label label0 = new Label();
            aggregateRootEventPayloadClassMethod.visitLabel(label0);
            aggregateRootEventPayloadClassMethod.visitLdcInsn(aggregateRootEventPayload.asmType());
            aggregateRootEventPayloadClassMethod.visitInsn(Opcodes.ARETURN);
            final Label label1 = new Label();
            aggregateRootEventPayloadClassMethod.visitLabel(label1);
            aggregateRootEventPayloadClassMethod.visitLocalVariable("this", String.format("L%s;", className), null, label0, label1, 0);
            aggregateRootEventPayloadClassMethod.visitMaxs(1, 1);
            aggregateRootEventPayloadClassMethod.visitEnd();
        }
        new ApplyTransactionAnnotation().apply(this);
        super.visitEnd();
    }
}
