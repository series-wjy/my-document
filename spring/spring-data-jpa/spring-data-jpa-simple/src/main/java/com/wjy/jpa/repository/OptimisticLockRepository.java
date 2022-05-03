package com.wjy.jpa.repository;

import com.wjy.jpa.model.OptimisticLock;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 乐观锁实现操作
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月24日 09:41:00
 */
public interface OptimisticLockRepository extends JpaRepository<OptimisticLock, Integer> {
}
