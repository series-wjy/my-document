package com.wjy.jpa.repository;


import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月05日 14:08:00
 */
@NoRepositoryBean
public interface MyBaseRepository<T, ID>  extends Repository<T, ID> {

    T findOne(ID id);

    T save(T entity);
}
