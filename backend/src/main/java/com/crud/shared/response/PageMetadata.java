package com.crud.shared.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Metadata para respuestas paginadas.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageMetadata extends Metadata {
    
    private Integer page;
    private Integer size;
    private Integer totalPages;

    public PageMetadata() {
        super();
    }

    public PageMetadata(Integer status, String message, Long totalItems, Integer page, Integer size, Integer totalPages) {
        super(status, message, totalItems);
        this.page = page;
        this.size = size;
        this.totalPages = totalPages;
    }

    // Getters y Setters
    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
}
