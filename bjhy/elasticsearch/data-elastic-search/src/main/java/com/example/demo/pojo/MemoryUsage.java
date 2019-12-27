package com.example.demo.pojo;

import lombok.Data;

@Data
public class MemoryUsage {
    private long init;
    private long used;
    private long committed;
    private long max;
}