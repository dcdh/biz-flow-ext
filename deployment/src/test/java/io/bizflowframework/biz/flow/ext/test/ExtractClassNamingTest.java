package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.deployment.ExtractClassNaming;
import org.assertj.core.api.Assertions;
import org.jboss.jandex.ClassType;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Type;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExtractClassNamingTest {

    @Test
    void shouldExtractClassName() {
        final Type classType = ClassType.create(DotName.createSimple(String.class));
        Assertions.assertThat(new ExtractClassNaming().apply(classType)).isEqualTo("String");
    }

    @Test
    void shouldExtractClassNameFromInnerClass() {
        final Type classType = ClassType.create(DotName.createSimple(InnerClass.class));
        assertThat(new ExtractClassNaming().apply(classType)).isEqualTo("InnerClass");
    }

    private static final class InnerClass {

    }

}