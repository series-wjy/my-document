package com.wjy.jpa.service;

import com.wjy.jpa.model.OptimisticLock;
import com.wjy.jpa.repository.OptimisticLockRepository;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OptimisticLockException;
import java.util.Optional;
import java.util.Random;


/**
 * 乐观锁实现
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月24日 09:46:00
 */
@Service
@Slf4j
public class OptimisticLockService extends BaseServiceImpl<OptimisticLock, Integer, OptimisticLockRepository> {

    public OptimisticLockService(OptimisticLockRepository repository) {
        super(repository);
    }
    private Random random = new Random(100);

    @Transactional(rollbackFor = Exception.class)
    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 100,
            backoff = @Backoff(multiplier = 1.5,random = true))
    public OptimisticLock updateByOptimisticLock(OptimisticLock entity) {
        Optional<OptimisticLock> optional = getRepository().findById(entity.getId());
        if(optional.isPresent()) {
            // 模拟业务逻辑计算
            try {
                Thread.sleep(random.nextInt(500));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            OptimisticLock optimisticLock = optional.get();
            optimisticLock.setMoney(entity.getMoney() + optimisticLock.getMoney());
            return getRepository().saveAndFlush(optimisticLock);
        }
        return null;
    }
}
