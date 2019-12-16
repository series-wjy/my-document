package com.ow.taco.repository;

import com.ow.taco.entity.Taco;
import org.springframework.data.repository.CrudRepository;

public interface TacoRepository
        extends CrudRepository<Taco, Long> {
}