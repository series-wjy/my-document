package com.wjy.jpa.dao;

import com.wjy.jpa.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}