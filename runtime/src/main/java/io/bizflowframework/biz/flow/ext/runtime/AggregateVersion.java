package io.bizflowframework.biz.flow.ext.runtime;

import org.apache.commons.lang3.Validate;

import java.io.Serializable;
import java.util.Objects;

public final class AggregateVersion implements Serializable {
    private static final Integer UNINITIALIZED_VERSION = -1;
    private final Integer version;

    public AggregateVersion() {
        this.version = UNINITIALIZED_VERSION;
    }

    public AggregateVersion(final Integer version) {
        this.version = Objects.requireNonNull(version);
        Validate.validState(version > UNINITIALIZED_VERSION);
    }

    public Integer version() {
        return version;
    }

    boolean isUninitialized() {
        return UNINITIALIZED_VERSION.equals(version);
    }

    public AggregateVersion increment() {
        return new AggregateVersion(this.version + 1);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AggregateVersion) obj;
        return Objects.equals(this.version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version);
    }

    @Override
    public String toString() {
        return "AggregateVersion[" +
               "version=" + version + ']';
    }

}
