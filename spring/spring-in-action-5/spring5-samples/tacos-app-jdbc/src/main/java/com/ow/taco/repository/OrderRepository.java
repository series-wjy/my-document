package com.ow.taco.repository;


import com.ow.taco.entity.Order;

public interface OrderRepository {

  Order save(Order order);
  
}
