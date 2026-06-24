package com.ljm.studentspringboot.entity;

import java.util.List;

public class PageResult {

    private Long total;
    private List<Student> rows;

    public PageResult() {
    }

    public PageResult(Long total, List<Student> rows) {
        this.total = total;
        this.rows = rows;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<Student> getRows() {
        return rows;
    }

    public void setRows(List<Student> rows) {
        this.rows = rows;
    }
}