package io.bizflowframework.biz.flow.ext.runtime;

import org.apache.commons.lang3.Validate;

import java.util.Objects;

public final class AggregateVersion {
    private static final Long UNINITIALIZED_VERSION = -1L;
    private final Long version;

    public AggregateVersion() {
        this.version = UNINITIALIZED_VERSION;
    }

    public AggregateVersion(final Long version) {
        this.version = Objects.requireNonNull(version);
        Validate.validState(version > UNINITIALIZED_VERSION);
    }

    public Long version() {
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
