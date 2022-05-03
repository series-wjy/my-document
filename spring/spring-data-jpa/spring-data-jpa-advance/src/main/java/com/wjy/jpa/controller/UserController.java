package com.wjy.jpa.controller;

import com.wjy.jpa.domain.UserQbe;
import com.wjy.jpa.repository.UserQbeRepository;
import com.wjy.jpa.specification.SpecificationsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserQbeRepository repo;
    @RequestMapping(method = RequestMethod.GET, value = "/users")
    @ResponseBody
    public List<UserQbe> search(@RequestParam(value = "search") String search) {
        Specification<UserQbe> spec = new SpecificationsBuilder<UserQbe>().buildSpecification(search);
        return repo.findAll(spec);
    }
}
