package com.wjy.jpa.service;

import com.wjy.jpa.model.OptimisticLock;
import com.wjy.jpa.repository.OptimisticLockRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月24日 12:17:00
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@ComponentScan(basePackageClasses = OptimisticLockService.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OptimisticLockServiceTest {
    @Autowired
    private OptimisticLockService optimisticLockService;
    @Autowired
    private OptimisticLockRepository optimisticLockRepository;

    @Test
    public void testVersion() {
        OptimisticLock save = optimisticLockRepository
                .save(OptimisticLock.builder().id(1).money(0).build());
        Assertions.assertEquals(1, save.getId());
        Assertions.assertEquals(0, save.getVersion());
        Assertions.assertEquals(0, save.getMoney());

        optimisticLockService
                .updateByOptimisticLock(OptimisticLock.builder().id(1).money(10).build());
        OptimisticLock one = optimisticLockRepository.getOne(1);
        Assertions.assertEquals(10, one.getMoney());
        Assertions.assertEquals(1, one.getVersion());
    }

    @Test
    @Rollback(false)
    @Transactional(propagation = Propagation.NEVER)
    public void testForException() {
        OptimisticLock one = optimisticLockRepository.getOne(1);
        one.setMoney(10);
        new Thread(() -> {
            optimisticLockService.updateByOptimisticLock(one);
        }).start();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Exception exception = Assertions.assertThrows(OptimisticLockingFailureException.class, ()-> {
            optimisticLockService
                    .updateByOptimisticLock(one);
            OptimisticLock two = optimisticLockRepository.getOne(1);
            Assertions.assertEquals(40, two.getMoney());
            Assertions.assertEquals(4, two.getVersion());
        });
        System.out.println(exception);
    }
}
