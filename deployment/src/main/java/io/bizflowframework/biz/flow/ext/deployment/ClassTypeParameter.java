package io.bizflowframework.biz.flow.ext.deployment;

import org.apache.commons.lang3.Validate;
import org.objectweb.asm.Type;

import java.util.Objects;

public record ClassTypeParameter(String type) {
    public ClassTypeParameter {
        Objects.requireNonNull(type);
        Validate.validState(type.startsWith("L"));
    }

    String descriptor() {
        return "()Ljava/lang/Class;";
    }

    String signature() {
        return String.format("()Ljava/lang/Class<%s;>;", type);
    }

    Type asmType() {
        return Type.getType(type + ";");
    }
}