package com.wjy.jpa.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月11日 10:12:00
 */
@Entity
@Table(name = "t", schema = "test", catalog = "")
public class TEntity {
    private int id;
    private Integer c;
    private Integer d;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "c")
    public Integer getC() {
        return c;
    }

    public void setC(Integer c) {
        this.c = c;
    }

    @Basic
    @Column(name = "d")
    public Integer getD() {
        return d;
    }

    public void setD(Integer d) {
        this.d = d;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TEntity tEntity = (TEntity) o;
        return id == tEntity.id &&
                Objects.equals(c, tEntity.c) &&
                Objects.equals(d, tEntity.d);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, c, d);
    }
}
