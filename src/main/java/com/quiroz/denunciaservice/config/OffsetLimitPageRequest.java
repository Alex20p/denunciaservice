package com.quiroz.denunciaservice.config;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

public class OffsetLimitPageRequest implements Pageable, Serializable {

    private final long offset;
    private final int limit;
    private final Sort sort;

    public OffsetLimitPageRequest(long offset, int limit, Sort sort) {
        if (offset < 0) {
            throw new IllegalArgumentException("El parámetro offset no puede ser negativo");
        }
        if (limit < 1) {
            throw new IllegalArgumentException("El parámetro limit debe ser mayor a cero");
        }
        this.offset = offset;
        this.limit = limit;
        this.sort = (sort == null) ? Sort.by("id").ascending() : sort;
    }

    public OffsetLimitPageRequest(long offset, int limit) {
        this(offset, limit, Sort.by("id").ascending());
    }

    @Override
    public int getPageNumber() {
        return (int) (offset / limit);
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new OffsetLimitPageRequest(getOffset() + getPageSize(), getPageSize(), getSort());
    }

    public Pageable previous() {
        return hasPrevious() ? new OffsetLimitPageRequest(getOffset() - getPageSize(), getPageSize(), getSort()) : this;
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    public Pageable first() {
        return new OffsetLimitPageRequest(0, getPageSize(), getSort());
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new OffsetLimitPageRequest((long) pageNumber * getPageSize(), getPageSize(), getSort());
    }

    @Override
    public boolean hasPrevious() {
        return offset > 0;
    }
}
