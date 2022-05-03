package com.wjy.jpa.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wjy.jpa.domain.UserQbe;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    //利用该方式获得entityManager
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 测试entityManager用法
     * @throws JsonProcessingException
     */
    @Test
    @Rollback(false)
    public void testEntityManager() throws JsonProcessingException {
        //测试找到一个User对象
        UserQbe user = entityManager.find(UserQbe.class,2L);
        Assertions.assertEquals(user.getAddress().get(0).getAddress(),"shanghai");

        //我们改变一下user的删除状态
        user.setDeleted(true);
        //merger方法
        entityManager.merge(user);
        //更新到数据库里面
        entityManager.flush();
        //再通过createQuery创建一个JPQL，进行查询
        List<UserQbe> users =  entityManager.createQuery("select u From UserQbe u where u.name=?1")
                .setParameter(1,"jack")
                .getResultList();
        Assertions.assertTrue(users.get(0).isDeleted());
    }
}
