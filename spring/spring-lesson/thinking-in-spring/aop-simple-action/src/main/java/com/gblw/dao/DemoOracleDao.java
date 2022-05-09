package com.gblw.dao;

import java.util.Arrays;
import java.util.List;

public class DemoOracleDao implements DemoDao {
    @Override
    public List<String> findAll() {
        return Arrays.asList("oracle", "oracle");
    }
}
