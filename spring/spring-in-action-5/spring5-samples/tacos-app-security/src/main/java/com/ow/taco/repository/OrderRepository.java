package com.ow.taco.repository;

import com.ow.taco.entity.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository 
         extends CrudRepository<Order, Long> {

}
