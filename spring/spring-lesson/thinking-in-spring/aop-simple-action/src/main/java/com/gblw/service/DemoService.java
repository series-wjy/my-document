package com.gblw.service;

import java.util.List;

public interface DemoService {
    List<String> findAll();

    int add(String userId, int points);

    int subtract(String userId, int points);
}