package com.wjy.jpa.service;

import com.wjy.jpa.model.Person;
import com.wjy.jpa.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年11月05日 14:45:00
 */
@Service
public class UserService {

    @Autowired
    private PersonRepository personRepository;

    public void queryPage() {
        //查询user里面的lastname=jk的第一页，每页大小是20条；并会返回一共有多少页的信息
        Page<Person> users = personRepository.findByLastname("jk", PageRequest.of(1, 20));
//        //查询user里面的lastname=jk的第一页的20条数据，不知道一共多少条
//        Slice<User> users = personRepository.findByLastname("jk",PageRequest.of(1, 20));
//        //查询出来所有的user里面的lastname=jk的User数据，并按照name正序返回List
//        List<User> users = personRepository.findByLastname("jk", Sort.by(Sort.Direction.ASC, "name"))
//        //按照createdAt倒序，查询前一百条User数据
//        List<User> users = personRepository.findByLastname("jk",PageRequest.of(0, 100, Sort.Direction.DESC, "createdAt"));
    }
}
